package dam.exer_2

import java.lang.NumberFormatException
import java.util.HexFormat

fun main(args: Array<String>) {

    println("Bem vindo a calculadora em Kotlin")
    // este do while é para dar a escolha de continuar a fazer calculos ao utilizador sem o programa fechar ent fazemos tudo no fim pergunta se se
    // quer continuar e se sim entra no while outra vez.
    do{
        var primeiroNumero:Float
        // faço um do while para que faca sempre primerio a pergunta de qual o numero que queremos usar, depois verificamos se o numero é valido
        //se nao for valido volta para dentro do loop, dizendo ao utilizador que o numero não é valido e perguntado denovo qual numero ele quer usar
        do {
            println("Adiciona o primeiro numero")
            try{
                primeiroNumero = readln().toFloat()
                break
            }catch ( e : NumberFormatException){
                println("Tem que ser um número! É favor colocar um numero")

            }

        }while(true)

        val operadoresValidos = listOf("+","-","/","*","&&","||","!","shl","shr")
        var operador :String

        // faço um do while para que faca sempre primeiro a pergunta de qual operador queremos usar, depois verificamos se o operador é valido
        //se nao for valido volta para dentro do loop, dizendo ao utilizador que o operador não é valido e perguntado denovo qual operador ele quer usar
        do {
            println("Qual o teu operador?(+,-,/,*,&&(and),||(or),!(not),shl(left shift),shr(right shift)")
            operador = readln()

            if (operador !in operadoresValidos){
                print("Operador invalido tem que ser um dos seguintes operadores (+,-,/,*,&&,||,!,shl,shr")
            }
        } while (operador !in operadoresValidos)


        // mesma logica do primeiro numero
        // temos que associar um valor no segundoNumero, pois quando uma variavel é inicializada como var e depois essa variavel é utilizada mais afrente,
        // é obrigatorio ter um valor, neste caso, como o segundoNumero é utilizado  no when o compilador necessita de ter um valor associado .
        // em casos mais atras como o operador, n foi preciso inicializar pois o compilador entendeu que posteriormente estava um do while, ou seja a variavel iria ser
        //preenchida de certeza

        var segundoNumero:Float=0f
        if(operador != "!") {
            do {
                println("Adiciona o segundo numero")
                try{
                    segundoNumero = readln().toFloat()
                    break
                }catch ( e : NumberFormatException){
                    println("Tem que ser um número! É favor colocar um numero")

                }

            }while(true)
        }



    // fazer um when para decidir qual funcao utilizar dependendo do operador a utilizar
        val resultado = when (operador){
            "+" -> sum(primeiroNumero,segundoNumero)
            "-" -> sub(primeiroNumero,segundoNumero)
            "/" -> div(primeiroNumero,segundoNumero)
            "*" ->mult(primeiroNumero,segundoNumero)
            "&&" -> and(primeiroNumero.toInt(),segundoNumero.toInt())
            "||" -> or(primeiroNumero.toInt(),segundoNumero.toInt())
            "!"-> not (primeiroNumero.toInt())
            "shl" -> shl(primeiroNumero.toInt(),segundoNumero.toInt())
            "shr" -> shr(primeiroNumero.toInt(),segundoNumero.toInt())
            else -> {"operador não valido"}
        }
        // para transformar em boolean eu fiz que qualquer valor diferente de 0 seria true e apenas os que sao 0 a true
        val booleanResult = when(resultado) {
            is Float -> resultado != 0f
            is Int -> resultado != 0
            else -> false
        }

        // e mostro o resultado tanto em decimal como em hexadecimal, em hexadecimal eu fiz uma funcao que transforma
        // o numero decimal em numero hexadecimal numToHex()
        println("O resultado em deciamal da operacçao $primeiroNumero $operador $segundoNumero é = $resultado, e" +
                " o resultado em hexadecimal é ${numToHex(resultado.toString().toFloat().toInt())} e " +
                " o resultado em boolean é $booleanResult")
        // tenho que fazer toString.toFloat.toInt pois a funcao do numToHex retorna Any o resutlado tem que ser mandado como tostring
        // e os numeros vem em float ou seja com um . e o toInt não aceita esse tipo de dados por isso antes de fazer o doInt fazermos o toFloat

        println("Quer continuar? s/n")
        var continuar = readln()
        }while (continuar =="s")
        println("Obrigado!")

}
fun sum ( a: Float,b:Float):Float =a + b
fun sub ( a: Float,b:Float):Float =a - b
fun div ( a: Float,b:Float):Float{
    return if (b == 0f){
        throw IllegalArgumentException("Não se pode divir por 0")
    }else
       a / b
}
fun mult (a: Float,b:Float):Float = a * b

/*para as funcoes booleanas eu pensei fazer o seguinte transformar os numeros em bits e fazer os operadores diretamente sobre eles
//ex a=2 e o b=4 seria 2=010 e 4 = 100 o resultado do AND ficaria 0 pois
//0 AND 1 = 0
//1 AND 0 = 0
//0 AND 0 = 0
// o or e o not é igual porem o not so invertemos o bit do numero que queremos
*/

// as funcoes and or e .inv() no kotlin ja fazem a transformacao em bits dos numeros e a operacao bit a bit
fun and(a:Int,b:Int):Int = a and b
fun or (a:Int,b:Int):Int = a or b

fun not (a:Int):Int=a.inv()


// estas funcoes basicamente shiftam b bits do numero a(transformado pela funcao em bits) para a direita ou para a esquerda
// Ex: a = 5 -> 0101 e b = 1 o shl (shift para a esquerda) seria 1010 = 10
// se por para a direita ficaria 0010 = 2
fun shl(a:Int,b:Int):Int = a shl b

fun shr(a:Int,b:Int):Int = a shr b


//Para transformar um nuemro em hexadecimal temos que fazer divisoes por 16 sucessivas e guardar o resto da divisao. Como dividimos por 16 o resto
// é sempre entre 0 e 15, sendo os numero de 0 a 9 reprensentados com os proprios numeros, e os numeros de 10 a 15 representados por ordem de A a F
//Fazemos divisoes sucessivas por 16, e o resultado da divisao inteira vai ser usado para dividir na proxima iteracao, guardando sempre o resto,
// Pois é o resto que vai ser usado para a traduçao. O loop so termina quando o resultado da divisao inteira for 0. O resultado é representado
//por ordem inversa ou seja o primeiro numero a ser mostrado é o ultimo resto que deu do loop. Por isso no fim o reverse da lista
fun numToHex(a:Int):Any{
    var numero = a
    var resto = a % 16
    val lista = mutableListOf<Any>()
    while (numero != 0){
        val restoHex=when (resto){
            10 ->"A"
            11 ->"B"
            12 ->"C"
            13 ->"D"
            14 ->"E"
            15 ->"F"
            else -> {resto}
        }
        lista.add(restoHex)
        numero /= 16
       // println("O quoeciente é $numero")
        resto = numero % 16
        //println("O resto é $resto")


    }
    lista.reverse()

    return lista.joinToString ("")
}
