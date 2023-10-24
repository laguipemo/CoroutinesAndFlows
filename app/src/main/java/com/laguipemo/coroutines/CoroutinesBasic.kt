package com.laguipemo.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Project: Coroutines
 * from: com.laguipemo.coroutines
 * Created by Lázaro Guillermo Pérez Montoto (chachy) on 21/10/23 at 13:19
 * All rights reserved 2023
 *
 * https://github.com/laguipemo/
 **/
 
fun main() {
    //globalScope()
    //suspendFun()
    newTopic("Constructores de corrutinas")
//    cRunBlocking()
//    cLaunch()
//    cAsync()
//    job()
//    deferred()
    cProduce()

    readln()
}

fun cProduce() = runBlocking {
    newTopic("Produce")
    val names = produceNames()
    names.consumeEach {
        println(it)
    }
}

fun CoroutineScope.produceNames(): ReceiveChannel<String> = produce {
    (1..5).forEach {
        send("Name: $it")
    }
}

fun deferred() {
    runBlocking {
        newTopic("Deferred")
        val deferred = async {
            startMsg()
            delay(someTime())
            println("Deferred...")
            endMsg()
            multi(5,2)
        }

        println("Deferred: ${deferred}") // solo referencia al objeto deferred
        // el estado será activo (isActive: true, isCancelled: false, isCompleted: false)
        println("Deferred isActive: ${deferred.isActive}") // job is active?
        println("Deferred isCancelled: ${deferred.isCancelled}") // job is cancelled?
        println("Deferred isCompleted: ${deferred.isCompleted}") // job is completed?

        println("Result: ${deferred.await()}") // espera que finalice cálculo del valor diferido y
                                               // lo recupera
        // el estado será completado (isActive: false, isCancelled: false, isCompleted: true)
        println("Deferred isActive: ${deferred.isActive}") // job is active?
        println("Deferred isCancelled: ${deferred.isCancelled}") // job is cancelled?
        println("Deferred isCompleted: ${deferred.isCompleted}") // job is completed?

        println("Formato async resumido")
        val result = async {
            multi(3, 3)
        }.await()
        println("Result: $result")
    }
}

fun job() {
    runBlocking {
        newTopic("Job")
        val job = launch {
            startMsg()
            delay(someTime())
            println("job...")
            endMsg()
        }
        println("Job: ${job}") // solo referencia al objeto job

        // conocer el estado del job
        // En este punto el job no se ha completado teniendo en cuenta que tiene un delay y su estado
        // será activo (isActive: true, isCancelled: false, isCompleted: false)
        println("isActive: ${job.isActive}") // job está activo?
        println("isCancelled: ${job.isCancelled}") // job está cancelado?
        println("isCompleted: ${job.isCompleted}") // job está completado?

        // cancelar el job antes de que termine y por tanto su estado será cancelado
        // (isActive: false, isCancelled: true, isCompleted: false)
        delay(1000) // tiempo pequeño para garantizar que el no ha terminado y se puede cancelar
        println("Tarea cancelada o interrumpida")
        job.cancel()
        println("isActive: ${job.isActive}") // job está activo?
        println("isCancelled: ${job.isCancelled}") // job está cancelado?
        println("isCompleted: ${job.isCompleted}") // job está completado?

        // Si esparamos  un tiempo prudecial para que termine el job, estonces su estado será
        // completado (isActive: false, isCancelled: false, isCompleted: true)
//        delay(4_000)
//        println("isActive: ${job.isActive}") // job está activo?
//        println("isCancelled: ${job.isCancelled}") // job está cancelado?
//        println("isCompleted: ${job.isCompleted}") // job está completado?

    }
}

fun cAsync() {
    runBlocking {
        newTopic("Async")
        // async es un constructor de corrutina que no bloquea el hilo desde el que se lanza. A
        // diferencia del constructor launch, async es utilizado para devolver un resultado al
        // finalizar la corrutina. El resultado se devuelve como un resultado diferido, haciendo
        // uso de la implementacion de un Deferred. Es decir, de un objeto de tipo Job, cancelable,
        // con información del estado del Job por medio de sus estados y con metodos adicionales que
        // permiten recuperar el resultado final (exitoso o fallido). El Deferred estará activo
        // mientras se calcula el resultado, cuyo valor puede ser recuperado, una vez completada
        // la corrutina mediante el método #.await

        val deferred = async {
            startMsg()
            delay(someTime())
            println("Async...")
            endMsg()
            5
        }
//        println("Result: ${deferred}") // solo referencia al objeto deferred
        println("Result: ${deferred.await()}") // espera que finalice cálculo del valor diferido y
                                               // lo recupera
    }
}

fun cLaunch() {
    runBlocking {
        newTopic("Launch")
        // launch es un constructor de corrutina que requiere ser utilizado desde una corrutina o
        // desde una función suspendida. Está diseñado para lazar su bloque (corrutina)
        // en un hilo diferente SIN bloquear el hilo desde el que se lanza. Además devuelve una
        // referencia a dicha corrutina en forma de "Job" que puede ser cancelado y que guarda la
        // informacion del ciclo de vida de la corrutina a través de sus estados #.isActive,
        // #.isCompleted y #.isCancelled.
        // No está diseñado para devolver un resultado concreto desde la corrutina que crea, por lo
        // que es utilizado cuando NO se necesita o espera ningún valor o resultado del bloque de
        // corrutina que lanza. Se utiliza básicamente para ejecutar tareas secundarias de las que
        // no se espera ningún resultado. El contexto de la corrutina se hereda del CoroutineScope y
        // si ese contexto no cuenta con un dispatcher ni otro ContinuationIntercept, entonces
        // utiliza el Dispatcher.Default para ejecutar su corrutina.
        // En este caso al no especificarse un dispatcher, en primera instancia, trabaja con el del
        // padre runBlocking que es el Dispatcher.Main
        // hereda el de la corrutina padre.
        launch {
            startMsg()
            delay(someTime())
            println("launch...")
            endMsg()
        }
    }
}

fun cRunBlocking() {
    newTopic("RunBlocking")
    // RunBlocking es un constructor de corrutina que no requiere ser lanzado desde una corrutina o
    // una función suspendida. Está diseñado para bloquear el hilo desde el que se lanza hasta
    // que termine su bloque (corrutina). Esta característica es la causa por la que solo se
    // recomienda su uso para fines de prueba o didacticos. Su dispatcher por defecto es el hilo en que
    // se lanza. Si se especifica un dispatcher diferente, se utiliza ese dispatcher para ejecutar
    // su corrutina, pero el hilo desde el que se lanzón se bloqueará igualmente.
    runBlocking {
        startMsg()
        delay(someTime())
        println("RunBlocking...")
        endMsg()
    }
}

fun suspendFun() {
    newTopic("Suspend")
    Thread.sleep(someTime())
    // delay es una función suspendida y solo puede ser llamada desde una corrutina u otra función
    // suspendida.
    runBlocking {
        delay(someTime())
    }
}

fun globalScope() {
    newTopic("Global Scope")
    // GlobalScope la corrutina se ejecuta en el contexto global y estará viva por el tiempo
    // de ejecución de la aplicación.
    GlobalScope.launch {
        startMsg()
        delay(someTime())
        println("Mi corrutina")
        endMsg()
    }


}



fun startMsg() {
    println("Comenzando corrutina -${Thread.currentThread().name}-")
}

fun endMsg() {
    println("Corrutina -${Thread.currentThread().name}-Finalizada")
}