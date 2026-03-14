package dam.exer.virtual_library
class PhysicalBook(title :String,
                   author :String,
                   publicationYear: Int,
                   availableCopies:Int,
                   var weight:Double,
                   var hasHardcover :Boolean =true)// como é setado a por defauld metemos = true
    :Book(title,author,publicationYear,availableCopies) {
    override fun toString(): String {
        return "${super.toString()}, Wight $weight, ${if (hasHardcover)" and the book have Hard cover" else "and the book dont have Hard Cover"}"
    }

    override fun getStorageInfo() {
        println("Physical book $weight, HardCover: ${if (hasHardcover)"Yes" else "No"}")
    }
}