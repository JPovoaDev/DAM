package dam.exer_3

fun main(args: Array<String>) {
    /*
        A bola é deixada cair a 100 metros de altura e cada vez que salta chega a 60 porcento da altura anterior
        entao basta fazer uma sequencia onde a altura atual é 60 % da altura anterior.
        Podemos fazer isso simplesmente ao apanhar a altura e multiplica-la por 0.6(60 porcento)
        E fazemos isso sempre q a altura seja pelo menos 1 metro
     */
    //altura inicial = 100
    val altura =100.0
    //entramos na sequencia que vai ser aplicada a altura
    val bounces = generateSequence (altura){(it* 0.6).takeIf {it>=1.0}
        //a sequencia continua sempre que esta condicao (it>=1.0, a altura do bounce for maior que 1) for verdadeira, quando for falsa retorna
    // null terminando a sequencia

    }
    //damos print dos primeiros 15 bounces (take(15)) listamo-los (toList()) e transformamos todos os bounces
    //individualmente para ficarem com 2 casas decimais (".2f".format(it)) (o it representa cada salto individual)
    println(bounces.take(15).toList().map{"%.2f".format(it)})
}