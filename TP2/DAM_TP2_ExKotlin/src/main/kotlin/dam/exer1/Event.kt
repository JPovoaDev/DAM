package dam.exer1

sealed class Event {

    class Login(val username: String,val timestamp: Long):Event(){
        // adicionei gets para conseguir ter acesso as variaveis nas futuras funcoes
        val user get() = username
        val time get() = timestamp
    }

    class Purchase(val username: String, val amount : Double, val timestamp: Long):Event(){
        val user get() = username
        val money get() = amount
        val time get() = timestamp
    }

    class Logout(val username: String,val timestamp: Long):Event(){
        val user get() = username
        val time get() = timestamp

    }
}

//este tipo de funcoes so funciona em List<events>
fun List<Event>.filterByUser(username: String):List<Event>{
    // criamos uma lista para guardar os eventos com o username igaul ao passado como parametro
    val list = mutableListOf<Event>()

    // fazemos um for para a lista onde estamos a chamar a funcao
    for(i in 0..this.size-1){
        //apanhamos os enventos 1 a 1
        val event = this.get(i)
        //vemos se o evento é login e se tem o username = ao username como parametro e se sim adicionamos a lista
        if (event is Event.Login && event.user == username){
            list.add(event)
        }
        if (event is Event.Purchase && event.user == username){
            list.add(event)
        }
        if (event is Event.Logout && event.user == username){
            list.add(event)
        }
    }
    return list
}

fun List<Event>.totalSpent(username: String):Double{
    //variavel q serve como contador para contar o total de gastos que um certo username gasta
    var money=0.0
    for(i in 0..this.size-1){
        val event = this.get(i)
        //se encontramos um evento purchase com esse username somamos a variavel
        if (event is Event.Purchase && event.user == username){
            money += event.money
        }
    }
    return money
}

fun List<Event>.processEvent(handler: (Event) -> Unit):Unit{
    for (i in 0 .. this.size-1){
        val events = this.get(i)

        // aplica o lambda ao evento, para fazermos o que precisarmos
        handler(events)
    }
}
fun main(){
    val events = listOf (
        Event.Login("alice", 1000),
        Event.Purchase("alice", 49.99, 1100),
        Event.Purchase("bob", 19.99, 1200),
        Event.Login("bob", 1050),
        Event.Purchase("alice", 15.00, 1300),
        Event.Logout("alice", 1400),
        Event.Logout("bob", 1500)
    )
    events.processEvent { event ->
        when(event){
            is Event.Login -> println("[LOGIN] ${event.user} logged in at t=${event.time}")
            is Event.Purchase -> println("[PURCHASE]${event.user} spent $${event.money}, t=${event.time}")
            is Event.Logout -> println("[LOGOUT]${event.user} looged out at t=${event.time}")
        }
    }
    println("--------------------------------------------------------------------------")

    println("Total spent by alice: $${"%.2f".format(events.totalSpent("alice"))}")
    println("Total spent by bob: $${"%.2f".format(events.totalSpent("bob"))}")

    println("--------------------------------------------------------------------------")

    println("Events for alice :")
    var listAlice = events.filterByUser("alice")
    listAlice.processEvent {event->
        when(event){
            is Event.Login -> println("Login(username= ${event.user},timestamp=${event.time})")
            is Event.Purchase -> println("Purchase(username=${event.user}, amount=${event.money}, timestamp=${event.time})")
            is Event.Logout -> println("Logout(username=${event.user}, timestamp=${event.time})")
        }
    }


}


