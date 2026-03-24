

fun main(args: Array<String>) {

    // alinea a)

    // como fazemos um array entre 0 e 50 se fizermos diretamente i*i a primeira iteracao ficaria 0*0 que daria 0
    // porem o 0 não é um quadrado perfeito, o primeiro quadrado perfeito é o 1 que é 1*1, que diz respeito a seguna iteracao
    // do array logo fazemos (i+1)*(i+1).
    val arrayA = IntArray(50){i -> (i+1)*(i+1)}
    //for (i in 1..arrayA.size){

    //    arrayA[i-1] = i*i
   // }

    println("Alinea A: Os primeiros 50 quadrados perfeitos sao ${arrayA.contentToString()}")


    // alinea b)
    // o map basicamente transforma os valores em algo novo, como queremos fazer os primeiros 50 quadrados perfeitos
    // fazemos uma lista que vai de 1 a 50 e depois transformamos esses valores, com o map, para o quadrado (i*i) dos mesmos

    // como n mudamos as variaveis em nunhum lado podemos usar o val em vez de var
    val listValoresB= (1..50)
    val quadrados = listValoresB.map{ i -> i * i }

    println("Aliena B: Os primeiros 50 quadrados perfeitos sao $quadrados")

    //alinea  c)
    val arrayC = Array(50){i -> (i+1)*(i+1)}
    println("Aliena C: Os primeiros 50 quadrados perfeitos sao ${arrayC.contentToString()}")
}
