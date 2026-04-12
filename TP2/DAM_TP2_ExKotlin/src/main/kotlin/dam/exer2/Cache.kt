package dam.exer2

class Cache<K : Any, V : Any> {
    var map = mutableMapOf<K, V>()

    fun put(key: K, value:V){
        map[key]=value
    }

    fun get(Key: K):V?{
        return map[Key]
    }
    fun evict(Key: K){
        map.remove(Key)
    }
    fun size():Int{
        return map.size
    }

    fun getOrPut(key: K, default: () -> V): V{
        return map.getOrPut(key,default)

    }


    fun transform(key: K, action: (V) -> V): Boolean{
        if(map.containsKey(key)){
            // os dois pontos obriga a n ser nulo
            map[key]=action(map[key]!!)
            return true
        }
        return false
    }

    fun snapshot(): Map<K, V>{
        //isto faz uma copia do mapa read-only ou seja imotavel
        return map.toMap()
    }
    fun filterValues(predicate:(V) -> Boolean):Map<K,V>{
        return map.filterValues(predicate)
    }

}
fun main (){

        println("--- Word frequency cache ---")

        val wordCache = Cache<String, Int>()

        // popula o cache com palavras
        wordCache.put("kotlin", 1)
        wordCache.put("scala", 1)
        wordCache.put("haskell", 1)

        println("Size: ${wordCache.size()}")
        println("Frequency of \"kotlin\": ${wordCache.get("kotlin")}")

        // getOrPut — se existe retorna o valor, se não  mete 0
        println("getOrPut \"kotlin\": ${wordCache.getOrPut("kotlin", { 0 })}")
        println("getOrPut \"java\": ${wordCache.getOrPut("java", {0})}")
        println("Size after getOrPut: ${wordCache.size()}")

        // transform
        println("Transform \"kotlin\" (+1): ${wordCache.transform("kotlin") { it + 1 }}")
        println("Transform \"cobol\" (+1): ${wordCache.transform("cobol") { it + 1 }}")

        println("Snapshot: ${wordCache.snapshot()}")

        println("--- Id registry cache ---")

        val idCache = Cache<Int, String>()

        // popula o cache com ids
        idCache.put(1, "Alice")
        idCache.put(2, "Bob")

        println("Id 1 -> ${idCache.get(1)}")
        println("Id 2 -> ${idCache.get(2)}")

        idCache.evict(1)
        println("After evict id 1, size: ${idCache.size()}")
        println("Id 1 after evict -> ${idCache.get(1)}")

// print para verificar o challenge do tp
       println("Words with count > 0 : ${wordCache.filterValues { it > 0 }}")

}

