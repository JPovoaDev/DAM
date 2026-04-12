package dam.exer3

class Pipeline{
var pipelinelist = mutableMapOf<String, (List<String>)->List<String>>()

    fun addStage(name :String,trasform:(List<String>)->List<String>){
        //so adicionamos o nome e o transform
        pipelinelist.put(name,trasform)
    }
    fun execute (input: List<String>): List<String>{
        var result = input
        // corremos a lista
        for (pipelines in pipelinelist){
            // e atualizamos o valor pelo resultado do input
            result = pipelines.value(result)
        }
        return result
    }

    fun describe() {
        for (pipelines in pipelinelist){
            // vemos o valor da chave
            println(pipelines.key)
        }
    }

    //funcao fork do desafio.
    // apanha o input e os dois pipelines independetes.
    fun fork(input: List<String>,pipeline1: Pipeline,pipeline2: Pipeline):Pair<List<String>,List<String>>{
        //damos execute dos dois pipelines
        var result1 = pipeline1.execute(input)
        var result2 = pipeline2.execute(input)
        //e damos pair dos mesmos
        return Pair(result1,result2)
    }


}
fun buildPipeline(pipeline: (Pipeline)->Unit):Pipeline{
    // criamos um pipeline
    val pipeline1 = Pipeline()
    // e aplicamos a fncao lambda ao pipeline criado
    pipeline(pipeline1)
    return pipeline1
}

fun main() {
    // lista de logs
    val logs = listOf(
        " INFO: server started ",
        " ERROR: disk full ",
        " DEBUG: checking config ",
        " ERROR: out of memory ",
        " INFO: request received ",
        " ERROR: connection timeout "
    )
// constroimos o pipeline com os stages
    val pipeline = buildPipeline {pipe->
        // o trim  remove os espacos em branco
        pipe.addStage("Trim") { list -> list.map { it.trim() } }
        //apenas o que contem  a palavra error
        pipe.addStage("Filter errors") { list -> list.filter { it.contains("ERROR") } }
        //mete tudo em maiusculas
        pipe.addStage("Uppercase") { list -> list.map { it.uppercase() } }
        //adiciona o index
        pipe.addStage("Add index") { list -> list.mapIndexed { index, line -> "${index + 1}. $line" } }
    }

    println("Pipeline stages:")
    pipeline.describe()

    println()

    println("Result:")
    val result = pipeline.execute(logs)
    for (line in result) {
        println(line)
    }
}

