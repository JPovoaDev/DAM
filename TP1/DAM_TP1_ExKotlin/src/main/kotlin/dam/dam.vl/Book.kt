package dam.exer.virtual_library

// Companion Object como queremos ver quantos livros sao adicionados à library, criamos nesta classe um companig
abstract class Book ( // antes da implementacao dos metodos abstratos como o digital e physical book eram herdados de Book tinhamos que meter
    // open antes de class, porem como temos metodos abstratos podemos meter apenas abstract pois ja implica open
    val title :String,
    val author :String,
    val publicationYear: Int,
    // aqui tiramos o var/val pois o se fizermos isso no construtor o kotlin cria uma propriedade com este nome e se
    //formos fazer um setter deste valor da conflito pois tem o mesmo nome, portanto tira-se o var do construtor para
    //criar apenas um parametro simples e depois no setter criamos ent a propriedade com os valores
    availableCopies : Int
){
    // um companion object é um valor compartilhado pela classe inteira, algo que não é individual às instancias, como o titulo ou o autor
    companion object {
        var totalBooks = 0
    }
    val era : String
        get() = if (publicationYear < 1980) "Classic"
                else if (publicationYear >= 1980 && publicationYear <=2010)"Modern"
                else "Contemporary"
    var availableCopies : Int = availableCopies
        set(value) {
            if (value >= 0) {
                if(value > field)//ou seja se o valor que tinha antes da chamada deste set for maior que o valor depois
                    // do set(ou seja se foi retornado a biblioteca) da um print que diz que foi retornado
                    println("The book $title was retorned successfully.Books on stock:$value")

                else if (value == 0) {
                    println("The book $title was requested successfully.Warning:the book is now out of stock!")
                }else{
                    // se tiver mais que 0 porem o valor for mais pequeno que o valor depois do setter quer dizer que o livro foi requisitado
                    // ent manda esta print
                    println("The book $title was requested successfully. Books on stock: $value")
                }
                field = value// o field é o valor que tinha anteriormente e o value é o valor que metemos ao chamar a funcao
                // ao fazermos isto estamos a atualizar o valor da propriendade anteriormente metidos

            } else {
                println("Error: $title cannot have negative copies!")
            }
        }
   init{
       println("Book created: Title = $title with the author $author")
       totalBooks++ // cada vez que o init é chamado quer dizer que um livro é criado na library ent aumentamos o companion object
   }
    /*fun details():String{
        return ("Title: $title, Author: $author, Publication Year: $publicationYear, Era of the book: $era, Available Copies $availableCopies")
    }
    Tinha feito esta funcao antes de ser explicito fazer o toString
    */
    override fun toString(): String {
        return "Title: $title, Author: $author, Publication Year: $publicationYear, Era of the book: $era, Available $availableCopies Copies "
    }
    abstract fun getStorageInfo()

    fun getTotalBooksCreated():Int{
        return totalBooks
    }

}