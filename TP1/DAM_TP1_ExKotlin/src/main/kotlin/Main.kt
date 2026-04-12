import dam.exer.virtual_library.DigitalBook
import dam.exer.virtual_library.Library
import dam.exer.virtual_library.PhysicalBook

fun main(args: Array<String>) {
    /*var x:Int= 5
    val name = "Kotlin"

    x +=1 //pode se fazer mas nome = outra coisa, n se pode

    // em kotlin val é uma variavel imutavel, ou sej an pode mudar
    // enquanto a var pode se mudar

    var a = 1
    val s1 = "a is $a"

    a = 2
    val s2 ="${s1.replace("is","was")},but now is $a"
    println(s2)


    var centro = "Terra"
    val f1 = "Pensa-se que o centro é $centro"

    centro ="Sol"
    val f2 = "Antes ${f1.replace("Pensa-se","Pensava-se" ) .replace("é", "era")} mas agr sabe-se que é o $centro"
    println(f2)


    for (i in 1..5){
        println("Numero $i")
    }

    var twoDArray = Array(2) { Array<Int>(2) { 0 } }
    twoDArray[0][1]=2
    twoDArray[1][0]=3
    twoDArray[1][1]=1
    twoDArray[0][0]=0
    // [[1,2][2,3]] {a,b-> a+b}
    println(twoDArray.contentDeepToString())

    var numero = 5386
    var resto = numero % 16
    val lista = mutableListOf<Any>()
    while (numero != 0 ){
        var restoHex=when (resto){
            10 ->"A"
            11 ->"B"
            12 ->"C"
            13 ->"D"
            14 ->"E"
            15 ->"F"
            else -> {resto}
        }
        lista.add(restoHex)
        numero = numero / 16
        println("O quoeciente é $numero")
        resto = numero % 16
        println("O resto é $resto")


    }
    lista.reverse()
    println(lista.joinToString (""))

    /*var count = 3
    // isto gera uma sequencia comecando em 3 e a cada iteracao diminui 1 e não para enquanto o valor ser maior que 0
    val sequence = generateSequence {
        (count--).takeIf{it>0}
    }
    println(sequence.toList())
    println(sequence.forEach {  })*/

    //generateSequence(100.0) { it * 0.6 }
      //  .filter { it >= 1.0 }
        //.take(15)
        //.forEach { println("%.2f".format(it)) }

    val altura =100.0
    val bounces = generateSequence (altura){
        (it* 0.6).takeIf {it>=1.0}
    }
    println(bounces.take(15).toList().map{"%.2f".format(it)})*/


        val library = Library("Central Library")
        val digitalBook = DigitalBook(
            "Kotlin in Action",
            "Dmitry Jemerov",
            2017,
            5,
            4.5,
            "PDF"
        )
        val physicalBook = PhysicalBook(
            "Clean Code",
            "Robert C. Martin",
            2008,
            3,
            650.0,
            true
        )
        val classicBook = PhysicalBook(
            "1984",
            "George Orwell",
            1949,
            2,
            400.0,
            false
        )
        library.addBook(digitalBook)
        library.addBook(physicalBook)
        library.addBook(classicBook)
        library.showBooks()
        println("\n--- Borrowing Books ---")
        library.borrowBook("Clean Code")
        library.borrowBook("1984")
        library.borrowBook("1984")
        library.borrowBook("1984") // Should fail - no copies left
        println("\n--- Returning Books ---")
        library.returnBook("1984")
        println("\n--- Search by Author ---")
        library.searchByAuthor("George Orwell")


}