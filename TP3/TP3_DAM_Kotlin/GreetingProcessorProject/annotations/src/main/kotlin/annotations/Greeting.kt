package annotations
@Target ( AnnotationTarget . FUNCTION )// esta annotation so pode ser aplicada a funcoes
@Retention ( AnnotationRetention . SOURCE )// esta annotation nao sera apresentadas em runtime, vai ser
//apenas usada para compilar o tempo para o processamento
annotation class Greeting (val message : String ) // a parametro permite que passemos uma mensagem greeting
//ex @Greeting ("Hello , Kotlin !")
//class MyClass