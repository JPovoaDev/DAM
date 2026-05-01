package dam.exer4

import kotlin.math.sqrt

data class Vec2(val x:Double,val y:Double):Comparable<Vec2>{

    operator fun plus(vector1:Vec2):Vec2{
        return Vec2(this.x+ vector1.x,this.y+vector1.y)
    }
    operator fun minus(vector1:Vec2):Vec2{
        return Vec2(this.x- vector1.x,this.y-vector1.y)
    }
    operator fun times(vector1:Double):Vec2{
        return Vec2(this.x* vector1,this.y*vector1)
    }


    operator fun unaryMinus():Vec2{
        return Vec2(-x,-y)
    }

    fun magnitude():Double{
        return sqrt(x * x + y * y)
    }
    override operator fun compareTo(vector1: Vec2):Int{
        val thisMagnitude = this.magnitude()
        val vector1Magnitude = vector1.magnitude()
        return thisMagnitude.compareTo(vector1Magnitude)
    }
    fun dot(vector1: Vec2): Double{
        return this.x*vector1.x + this.y*vector1.y
    }
    fun normalized():Vec2{
        val magnitude = magnitude()
        if(magnitude==0.0){
            throw IllegalStateException("The magnitude is 0 cannot normalize a number with magnitude iqual to 0 ")
        }
        return Vec2(x/magnitude,y/magnitude)
    }
    operator fun get(idx:Int):Double{
        if(idx< 0 || idx >1){
            throw IndexOutOfBoundsException("The index must be 0 or 1")
        }
        if(idx == 0){
            return x
        }
     return y
    }
    /*Tentei fazer o exercicio do Challenge do destructuring quando implementei isto:
    override operator fun component1(): Double {
        return x
    }

    override operator fun component2(): Double {
        return y
    }
    porem isto deu erro fui ver o porque na documentacao das data classes e pelos vistos como as data classes ja, por
    default, implementam este tipo de funcoes, o IDE n nós deixa fazer.
    */

}


operator fun Double.times(vector2:Vec2):Vec2{
    return Vec2(vector2.x*this,vector2.y*this)
}
fun main () {
    val a = Vec2 (3.0 , 4.0)
    val b = Vec2 (1.0 , 2.0)
    println ("a = $a") // a = Vec2 (x=3.0 , y =4.0)
    println ("b = $b") // b = Vec2 (x=1.0 , y =2.0)
    println ("a + b = ${a + b}") // a + b = Vec2 (x=4.0 , y =6.0)
    println ("a - b = ${a - b}") // a - b = Vec2 (x=2.0 , y =2.0)
    println ("a * 2.0 = ${a * 2.0} ") // a * 2.0 = Vec2 (x=6.0 , y =8.0)
    println ("-a = ${ -a}") // -a = Vec2 (x= -3.0 , y= -4.0)
    println ("|a| = ${a. magnitude ()}") // |a| = 5.0
    println ("a dot b = ${a.dot(b)}") // a dot b = 11.0
    println (" norm (a) = ${a. normalized ()}")
// norm (a) = Vec2 (x=0.6 , y =0.8)
    println ("a[0] = ${a [0]} ") // a[0] = 3.0
    println ("a[1] = ${a [1]} ") // a[1] = 4.0
    println ("a > b = ${a > b}") // a > b = true
    println ("a < b = ${a < b}") // a < b = false
   val vectors = listOf ( Vec2 (1.0 , 0.0) , Vec2 (3.0 , 4.0) , Vec2 (0.0 , 2.0) )
    println (" Longest = ${ vectors .max ()}") // Longest = Vec2 (x=3.0 , y =4.0)
    println (" Shortest = ${ vectors .min ()}") // Shortest = Vec2 (x=1.0 , y =0.0)
}
