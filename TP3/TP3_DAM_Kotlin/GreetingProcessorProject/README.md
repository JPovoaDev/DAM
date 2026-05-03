# TP3 — Annotation Processors em Kotlin

**Unidade Curricular:** Desenvolvimento de Aplicações Móveis (DAM)  
**Instituto:** Instituto Superior de Engenharia de Lisboa (ISEL)  
**Semestre:** 6.º Semestre  
**Restrições de desenvolvimento:** `AC: NO` · `AI: NO`

> Todo o código presente neste repositório foi escrito manualmente pelo aluno, sem recurso a ferramentas de autocompletar assistido por IA nem geração automática de código. As únicas ferramentas de apoio utilizadas foram a documentação oficial do Kotlin, do KAPT e do KotlinPoet.

---

## Índice

1. [Visão Geral do Projeto](#1-visão-geral-do-projeto)
2. [Estrutura do Projeto](#2-estrutura-do-projeto)
3. [Exercício 1 — Greeting Annotation Processor](#3-exercício-1--greeting-annotation-processor)
4. [Exercício 2 — Regex Annotation Processor](#4-exercício-2--regex-annotation-processor)
5. [Conceitos-Chave Aprendidos](#5-conceitos-chave-aprendidos)
6. [Conclusão](#6-conclusão)

---

## 1. Visão Geral do Projeto

Este trabalho tem como objetivo a implementação de **annotation processors personalizados em Kotlin**, uma técnica avançada que permite gerar código-fonte de forma automática durante a fase de compilação. O trabalho está dividido em dois exercícios progressivos, cada um explorando um padrão distinto de geração de código.

### Annotation Processing em Kotlin

Em Kotlin (e na JVM em geral), as anotações são metadados associados a elementos do código — classes, funções, propriedades, etc. — que podem ser inspecionados tanto em tempo de execução como em tempo de compilação. O *annotation processing* é o mecanismo que permite interceptar estas anotações durante a compilação e reagir a elas de forma programática, geralmente produzindo novo código-fonte como resultado.

O fluxo conceptual é o seguinte:

```
Código-fonte (.kt)
     ↓
 Compilador Kotlin + KAPT
     ↓
Annotation Processor (lê anotações)
     ↓
 Código gerado (.kt / .java)
     ↓
  Compilação final
```

Esta abordagem é utilizada por bibliotecas amplamente conhecidas como o **Room**, o **Hilt** e o **Retrofit**, que geram implementações concretas com base em interfaces e anotações definidas pelo programador.

### Arquitetura Multi-Módulo

O projeto foi estruturado como um **projeto multi-módulo Gradle**, separando claramente as responsabilidades entre os diferentes componentes do sistema. Esta separação é fundamental para evitar dependências circulares e garantir que o módulo de processamento não é incluído no artefacto final da aplicação.

---

## 2. Estrutura do Projeto

```
GreetingProcessorProject/
├── annotations/        # Definição das anotações personalizadas
├── processor/          # Lógica dos annotation processors
├── app/                # Aplicação cliente que usa as anotações
├── build.gradle.kts    # Build script raiz
└── settings.gradle.kts # Declaração dos módulos
```

### Módulo `annotations`

Este módulo contém exclusivamente as **definições das anotações** (`@Greeting` e `@Extract`). A razão para isolar as anotações num módulo próprio prende-se com o ciclo de dependências: tanto o módulo `processor` como o módulo `app` precisam de conhecer as anotações, mas não devem depender um do outro.

### Módulo `processor`

Este módulo implementa os **annotation processors** propriamente ditos — `GreetingProcessor` e `RegexProcessor`. Ambos estendem `AbstractProcessor`, a classe base da API de processamento de anotações da JVM (`javax.annotation.processing`). É aqui que reside toda a lógica de análise do código-fonte e de geração de novas classes.

O módulo utiliza:
- **KAPT** (*Kotlin Annotation Processing Tool*) para integração com o compilador Kotlin
- **KotlinPoet** para a construção programática de ficheiros e classes Kotlin
- **Google AutoService** para o registo automático dos processors no compilador

### Módulo `app`

O módulo `app` é a **aplicação cliente** do sistema. Contém as classes anotadas (`MyClass` e `DataProcessor`) e o ponto de entrada (`main`), que utiliza as classes geradas automaticamente pelo processor durante a compilação (`MyClassWrapper` e `DataProcessorExtractor`).

---

## 3. Exercício 1 — Greeting Annotation Processor

### 3.1 Objetivo

O objetivo deste exercício é implementar um processor que, ao encontrar métodos anotados com `@Greeting`, gere automaticamente uma **classe wrapper** por cada classe que contenha esses métodos. A classe gerada encapsula a classe original e adiciona, antes de cada chamada ao método original, a impressão de uma mensagem de saudação personalizada.

---

### 3.2 Design da Anotação

A anotação `@Greeting` foi definida no módulo `annotations` com as seguintes características:

```kotlin
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Greeting(val message: String)
```

- **`@Target(AnnotationTarget.FUNCTION)`** — restringe o uso da anotação exclusivamente a funções/métodos. Tentar aplicá-la a uma classe ou propriedade resultará num erro de compilação.
- **`@Retention(AnnotationRetention.SOURCE)`** — indica que a anotação existe apenas no código-fonte. Não é incluída no bytecode compilado nem acessível em tempo de execução, o que é adequado para anotações cujo único propósito é guiar o processor em tempo de compilação.
- **`val message: String`** — parâmetro obrigatório que permite ao programador definir a mensagem de saudação associada a cada método anotado. Por exemplo: `@Greeting("Hello from MyClass!")`.

---

### 3.3 Lógica do Processor

O `GreetingProcessor` implementa o método `process()` herdado de `AbstractProcessor`. A lógica de processamento pode ser descrita em três etapas:

**1. Recolha de elementos anotados**

O processor itera sobre todos os elementos devolvidos por `roundEnv.getElementsAnnotatedWith(Greeting::class.java)`. Para cada elemento, verifica se se trata de um `ExecutableElement` (i.e., uma função), e obtém a classe que o contém através de `element.enclosingElement`.

**2. Agrupamento por classe**

Os métodos anotados são agrupados num `Map<TypeElement, MutableList<ExecutableElement>>`, onde a chave é a classe encapsuladora. Este agrupamento é essencial para que o processor gere **uma única classe wrapper por classe**, independentemente do número de métodos anotados que ela contenha.

**3. Geração das classes wrapper**

Para cada entrada no mapa, o processor invoca `generateKotlinWrapperClass()`, que constrói e escreve o ficheiro Kotlin gerado.

---

### 3.4 Estratégia de Geração de Código

A geração de código é feita com recurso à biblioteca **KotlinPoet**, que oferece uma DSL fluente para construir ficheiros, classes e funções Kotlin de forma programática.

O padrão utilizado neste exercício é o de **composição**: a classe gerada não herda da classe original, mas sim recebe uma instância dela como parâmetro no construtor e delega as chamadas para esse objeto interno.

```
MyClassWrapper(val original: MyClass)
    └── fun sayHello() → println(message) + original.sayHello()
    └── fun compute()  → println(message) + original.compute()
```

Para cada método anotado, o processor:
1. Lê o nome do método e os seus parâmetros através da API de elementos
2. Extrai a mensagem da instância de `@Greeting` com `method.getAnnotation(Greeting::class.java)?.message`
3. Constrói a função com `FunSpec.builder()`, adicionando um `println` com a mensagem antes da delegação ao objeto original

O ficheiro gerado é escrito no diretório `kapt.kotlin.generated`, obtido através de `processingEnv.options`, que é o local onde o KAPT espera encontrar os ficheiros Kotlin gerados pelos processors.

---

### 3.5 Exemplo

**Classe original — `MyClass.kt`:**

```kotlin
open class MyClass {
    @Greeting("Hello from MyClass!")
    open fun sayHello() {
        println("Executing sayHello method")
    }

    @Greeting("Welcome to the compute function!")
    open fun compute() {
        println("Computing something important...")
    }
}
```

**Classe gerada automaticamente — `MyClassWrapper` (simplificado):**

```kotlin
class MyClassWrapper(val original: MyClass) {
    fun sayHello() {
        println("Hello from MyClass!")
        original.sayHello()
    }

    fun compute() {
        println("Welcome to the compute function!")
        original.compute()
    }
}
```

A classe `MyClassWrapper` é gerada **em tempo de compilação**, pelo que é totalmente invisível no código-fonte do projeto mas está disponível para uso no módulo `app` através do KAPT.

---

### 3.6 Output Final

Ao executar a `main` do módulo `app`, o comportamento observado é o seguinte:

```
Hello from MyClass!
Executing sayHello method
Welcome to the compute function!
Computing something important...
```

O que demonstra que a classe wrapper intercepta corretamente cada chamada, imprimindo a mensagem de saudação antes de delegar a execução ao método original.

---

## 4. Exercício 2 — Regex Annotation Processor

### 4.1 Objetivo

O objetivo deste exercício é estender o conceito de annotation processing para um caso de uso mais concreto: a **extração de dados de strings através de expressões regulares**. O processor gera automaticamente uma implementação concreta de uma classe abstrata, onde cada método abstrato anotado com `@Extract` recebe uma implementação que aplica a regex definida na anotação sobre uma string de input.

A diferença fundamental em relação ao exercício anterior reside no padrão de geração: em vez de composição, utiliza-se **herança**.

---

### 4.2 Design da Anotação `@Extract`

```kotlin
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Extract(val regex: String)
```

O parâmetro `regex` recebe uma expressão regular que define o padrão de extração associado ao método. A convenção é que a regex contenha **um grupo de captura** (delimitado por parênteses), cujo valor será devolvido como resultado da chamada ao método.

Por exemplo, `@Extract(regex = "Name: (\\w+)")` indica que o método deve extrair o primeiro grupo capturado pelo padrão `Name:` seguido de uma palavra.

---

### 4.3 Lógica do Processor

O `RegexProcessor` segue uma lógica semelhante à do exercício anterior no que toca à recolha e agrupamento de elementos anotados. A distinção principal está na geração das classes.

Neste caso, a classe base (`DataProcessor`) é **abstrata**, pelo que a classe gerada precisa de:
1. **Herdar** da classe original em vez de a encapsular
2. **Implementar** (fazer `override`) de cada método abstrato anotado com `@Extract`

A assinatura dos métodos abstratos define os contratos que a classe gerada tem de respeitar — nomeadamente, o tipo de retorno `String?` (nullable).

---

### 4.4 Estratégia de Geração de Código

Em termos de KotlinPoet, a diferença face ao exercício 1 é a substituição de `addProperty` (composição) por `superclass` (herança):

```kotlin
// Exercício 1 — Composição
TypeSpec.classBuilder("MyClassWrapper")
    .addProperty("original", ClassName(...))

// Exercício 2 — Herança
TypeSpec.classBuilder("DataProcessorExtractor")
    .superclass(ClassName(packageName, originalClassName))
    .addSuperclassConstructorParameter("input")
```

Para cada método `@Extract`, o processor gera uma função com `KModifier.OVERRIDE` que:

1. Compila a regex definida na anotação com `Regex(pattern).find(input)`
2. Extrai o primeiro grupo de captura através de `match?.groupValues?.get(1)`
3. Retorna o valor extraído como `String?` (pode ser `null` caso não haja correspondência)

O parâmetro `input` é recebido no construtor da classe gerada e passado para o construtor da superclasse (`super(input)`), tornando-o acessível em todos os métodos gerados.

---

### 4.5 Exemplo

**Classe abstrata original — `DataProcessor.kt`:**

```kotlin
abstract class DataProcessor(val input: String) {
    @Extract(regex = "Name: (\\w+)")
    abstract fun getName(): String?

    @Extract(regex = "Address: (.+)")
    abstract fun getAddress(): String?
}
```

**Classe gerada automaticamente — `DataProcessorExtractor` (simplificado):**

```kotlin
class DataProcessorExtractor(input: String) : DataProcessor(input) {

    override fun getName(): String? {
        val match = Regex("Name: (\\w+)").find(input)
        return match?.groupValues?.get(1)
    }

    override fun getAddress(): String? {
        val match = Regex("Address: (.+)").find(input)
        return match?.groupValues?.get(1)
    }
}
```

O programador define apenas os contratos (métodos abstratos e as respetivas regexes). O processor trata da implementação concreta em tempo de compilação.

---

### 4.6 Output Final

Com o input `"Name: John Address: 123 Street"`, a execução na `main` produz:

```
Name: John
Address: 123 Street
```

O valor `null` seria devolvido caso a string de input não contivesse o padrão correspondente, o que é refletido no tipo de retorno `String?` definido em cada método gerado.

---

## 5. Conceitos-Chave Aprendidos

### Annotation Processing

O annotation processing é um mecanismo da JVM que permite estender o compilador com lógica personalizada. Um processor recebe os elementos do programa (classes, métodos, etc.) que possuem determinadas anotações e pode, com base nessa informação, emitir novos ficheiros de código-fonte, erros de compilação ou avisos. É uma forma poderosa de aplicar metaprogramação sem sobrecarga em tempo de execução.

### KAPT (Kotlin Annotation Processing Tool)

O KAPT é a camada de compatibilidade que permite utilizar annotation processors baseados na API Java (`javax.annotation.processing`) em projetos Kotlin. O compilador Kotlin gera stubs Java correspondentes às classes Kotlin, que são depois processados pelos processors. Os ficheiros gerados são então compilados juntamente com o restante código-fonte. A configuração é feita no `build.gradle.kts` através do plugin `kotlin("kapt")`.

### KotlinPoet

O KotlinPoet é uma biblioteca da Square que fornece uma API fluente e tipada para a geração de código Kotlin. Em vez de construir strings de código manualmente (o que seria frágil e difícil de manter), o KotlinPoet permite compor `FileSpec`, `TypeSpec`, `FunSpec` e `PropertySpec` de forma estruturada. O resultado é código gerado sintaticamente correto e bem formatado.

### Geração de Código em Tempo de Compilação

Uma das grandes vantagens do annotation processing é que a geração de código ocorre **antes** da compilação final. Isto significa que o código gerado é verificado pelo compilador da mesma forma que o código escrito manualmente, garantindo segurança de tipos. Não há custo em tempo de execução associado ao processo de geração.

### Tempo de Compilação vs. Tempo de Execução

Ao definir `@Retention(AnnotationRetention.SOURCE)`, as anotações são descartadas após o processamento e **não existem em tempo de execução**. Isto contrasta com `AnnotationRetention.RUNTIME`, que mantém as anotações no bytecode e permite a sua leitura via reflexão. A escolha de `SOURCE` é deliberada neste projeto: o único consumidor das anotações é o processor, e manter metadados desnecessários em runtime seria um desperdício sem qualquer benefício.

---

## 6. Conclusão

Este trabalho permitiu explorar de forma prática um dos mecanismos mais sofisticados disponíveis no ecossistema Kotlin e da JVM: a geração de código em tempo de compilação através de annotation processors.

O exercício 1 introduziu os conceitos base — como registar um processor, navegar na árvore de elementos do programa e usar o KotlinPoet para construir novas classes. O exercício 2 aprofundou esses fundamentos ao introduzir um caso de uso mais realista (extração de dados com regex) e ao explorar um padrão de geração distinto (herança em vez de composição).

Do ponto de vista académico, este trabalho é particularmente valioso porque obriga a compreender a fronteira entre o que acontece em tempo de compilação e o que acontece em tempo de execução — uma distinção que é frequentemente ignorada mas que tem implicações diretas na eficiência, segurança de tipos e manutenibilidade das aplicações. Além disso, a necessidade de trabalhar com a API de elementos (`javax.lang.model`) exige uma compreensão sólida do modelo de tipos da JVM, o que constitui um complemento útil ao desenvolvimento Android quotidiano.
