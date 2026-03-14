package dam.exer.virtual_library

class DigitalBook(title :String,
                  author :String,
                  publicationYear: Int,
                  availableCopies:Int,
                  var fileSize:Double,
                  var format :String):Book(title,author,publicationYear,availableCopies){

    override fun toString(): String {
        return "${super.toString()},File size $fileSize MB, Format $format"
    }

    override fun getStorageInfo() {
        println("Stored digitally: $fileSize, Format: $format")
    }
}