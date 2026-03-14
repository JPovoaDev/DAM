package dam.exer.virtual_library

class Library (name:String){
     val books : MutableList<Book> = mutableListOf()

    fun addBook(book:Book){
        books.add(book)
    }
    fun findBook(title:String):Book?{//o ponto de interrogacao serve para conseguirmos returnar null caso preciso,

        for (book in books){
            if (title == book.title){
                return book
            }
        }
        return null;
    }
    fun borrowBook(title:String){
        var book=findBook(title)
        if(book!=null){
            // a minha funcao que da set a availableCopies tem os prints correspondentes para o sucesso ou insucesso
            book.availableCopies-=1
        }
    }
    fun returnBook(title:String){
        var book = findBook(title)
        if (book != null){
            book.availableCopies+=1
        }
    }
    fun showBooks(){
        for (book in books){
            println(book)
        }
    }
    fun searchByAuthor(author:String){
        val authors = mutableListOf<Book>()
        for (book in books){
            if (author == book.author){
                println("Books by $author:" +
                        " Title-${book.title} (Publish Year: ${book.publicationYear}, Era: ${book.era}, Copies available: ${book.availableCopies})")
            }

        }
    }


}
