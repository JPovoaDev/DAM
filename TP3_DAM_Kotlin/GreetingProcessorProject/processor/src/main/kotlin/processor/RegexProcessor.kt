package processor
import annotations.Extract
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedAnnotationTypes("annotations.Extract")
/* A diferenca entre este Processor e o do ex 1, é que o outro era composicao e este quer herança,
Antes estavamos a criar um MyclassWrapper que recebia uma instacia da classe original no construtor
Este exercicio pede que a classe gerada herda da classe original. Entao em vez de .addProperty, devemos
de usar .superclass
 */
class RegexProcessor : AbstractProcessor() {
    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        val classMethodMap = mutableMapOf<TypeElement, MutableList<ExecutableElement>>()
        // Procuramos por todos os métodos anotados com @Extract
        for (element in roundEnv.getElementsAnnotatedWith(Extract::class.java)) {
            if (element is ExecutableElement) {
                val enclosingClass = element.enclosingElement as TypeElement
                classMethodMap.computeIfAbsent(enclosingClass) { mutableListOf() }.add(element)
            }
        }
        // Gerar as classes Extractor para cada classe que contenha métodos anotados
        for ((classElement, methods) in classMethodMap) {
            generateKotlinWrapperClass(classElement, methods)
        }
        return true
    }
    private fun generateKotlinWrapperClass(
        classElement: TypeElement,
        methods: List<ExecutableElement>
    ) {
        val packageName = processingEnv.elementUtils.getPackageOf(classElement).toString()
        val originalClassName = classElement.simpleName.toString()
        val wrapperClassName = "${originalClassName}Extractor"
        // Aqui vamos criar a classe para que ela herde
        val classBuilder = TypeSpec.classBuilder(wrapperClassName)
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter("input", String::class) // assim o contrutor recebe a string do input
                    .build()
            )
            .superclass(ClassName(packageName, originalClassName)) // Aqui definimos a Herança
            .addSuperclassConstructorParameter("input") // Passamos o input para o super()
        // Gerar os métodos que fazem a extração
        for (method in methods) {
            val methodName = method.simpleName.toString()

            // Mapeamos os parâmetros
            val parameters = method.parameters.map { param ->
                ParameterSpec.builder(param.simpleName.toString(), param.asType().asTypeName()).build()
            }
            // Vamos buscar a Regex definida na anotação
            val regexMessage = method.getAnnotation(Extract::class.java)?.regex ?: "Hello!"
            val methodBuilder = FunSpec.builder(methodName)
                // Além do que estava no outro codigo, as funcoes apresentadas têm um return
                // que retorna match?.groupValues?.get(1)
                // estes returns sao ou strings ou nulls, logo pode ser null
                .addModifiers(KModifier.OVERRIDE) // IMPORTANTE: como herdamos, temos de fazer override dos métodos abstratos
                .addParameters(parameters) // adicionamos os parametros caso existam
                .addStatement("val match = Regex(%S).find(input)", regexMessage) // %S usa a variável regexPattern e coloca aspas
                .addStatement("return match?.groupValues?.get(1)") // adicionamos o return da extração
                .returns(String::class.asTypeName().copy(nullable = true)) // Definimos o retorno como String? (nullable)
            classBuilder.addFunction(methodBuilder.build())
        }
        // Construir o ficheiro Kotlin
        val file = FileSpec.builder(packageName, wrapperClassName)
            .addType(classBuilder.build())
            .build()
        // Escrever o ficheiro gerado
        try {
            val kaptKotlinGeneratedDir = processingEnv.options["kapt.kotlin.generated"]
            if (kaptKotlinGeneratedDir != null) {
                file.writeTo(File(kaptKotlinGeneratedDir))
            } else {
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "kapt.kotlin.generated not found"
                )
            }
        } catch (e: Exception) {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Error generating Kotlin file : ${e.message}"
            )
        }
    }
}