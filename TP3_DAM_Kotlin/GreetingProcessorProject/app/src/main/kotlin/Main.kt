package com.example.app

import com.dam.DataProcessorExtractor
import org.example.MyClass
import org.example.MyClassWrapper

fun main () {
    val myClass = MyClass ()
    val wrappedMyClass = MyClassWrapper ( myClass )
    wrappedMyClass . sayHello ()
    wrappedMyClass . compute ()

    val input = " Name : John Address : 123 Street "// Using the generated DataProcessorExtractor
    val extractor = DataProcessorExtractor ( input )
    println (" Name : ${ extractor . getName ()}")
    println (" Address : ${ extractor . getAddress ()}")

}