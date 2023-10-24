package com.laguipemo.coroutines

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.random.Random

/**
 * Project: Coroutines
 * from: com.laguipemo.coroutines
 * Created by Lázaro Guillermo Pérez Montoto (chachy) on 22/10/23 at 1:40
 * All rights reserved 2023
 *
 * https://github.com/laguipemo/
 **/
 
fun main() {
    // Con los dispatchers definimos donde queremos que se ejecuten los hilos de las corrutinas
    // existen vairos dispatchers, cada uno especializado en un tipo de trabajo:
    //
    // Main: (utilizado basicamente en android como hilo principal para mostrar la interfaz de usuario.
    //       su uso en Kotlin puro genera un error.)
    // IO: (Utilizado para correr las corrutinas en segundo plano y especializado en tareas de
    //      entrada/salida: con bases de datos, ficheros, datos en servidores, etc.)
    // Default: (Utilizado para correr las corrutinas en segundo plano y especializado en tareas de
    //           gran consumo de cpu: procesar imagen, cálculos complejos, etc.)
    // Unconfined: (Utilizado para correr las corrutinas en segundo plano y especializado en tareas
    //              en las que no se requiere compartir datos con otras corrutinas. Además permite
    //              cambiar de hilo siempre que esté en una función supendida.)
    // Personalizado: (Utilizado para correr las corrutinas en segundo plano recomendado para tareas
    //                 de depuración)
//    dispatchers()
//    nested()
//    changeWithContext()
    basicFlows()

}

fun basicFlows() {
    runBlocking {
        newTopic("Flows Básicos")
        launch {
            getDataByFlow().collect {
                println("${it}º")
            }
        }
        launch {
            (1..50).forEach {
                delay(someTime()/10)
                println("Tarea 2...")
            }
        }
    }

}

fun getDataByFlow(): Flow<Float> {
    return flow {
        (1..5).forEach {
            println("procesando datos...")
            delay(someTime())
            emit(20 + it * Random.nextFloat())
        }
    }
}

fun changeWithContext() {
    runBlocking {
        newTopic("WithContext")
        startMsg()
        withContext(newSingleThreadContext("Cursos Adoroid ANT")) {
            startMsg()
            delay(someTime())
            println("CursosAndroidANT...")
            endMsg()
        }

        withContext(Dispatchers.IO) {
            startMsg()
            delay(someTime())
            println("Petición al servidor...")
            endMsg()
        }

        endMsg()
    }
}

fun nested() {
    runBlocking {
        newTopic("Nested")

        val job = launch {
            startMsg()
            launch {
                startMsg()
                delay(someTime())
                println("Otra tarea...")
                endMsg()
            }

            val jobInServer = launch(Dispatchers.IO) {
                startMsg()
                launch(newSingleThreadContext("Cursos Adoroid ANT")) {
                    startMsg()
                    println("Tarea cursos android ANT...")
                    endMsg()
                }

                delay(someTime())
                println("Tarea en el servidor...")
                endMsg()
            }

//            delay(someTime()/10) // dejo tiempo para que se complete Tarea cursos android ANT
            jobInServer.cancel()
            println("Tarea en el servidor cancelada")

            var sum = 0
            (1..100).forEach {
                sum += it
                delay(someTime()/100)
            }
            println("Suma: $sum")
            endMsg()
        }
//        delay(someTime()/2)
//        job.cancel()
//        println("Job cancelado...")

    }
}

@OptIn(DelicateCoroutinesApi::class)
fun dispatchers() {
    runBlocking {
        newTopic("Dispatchers")
        launch {
            startMsg()
            println("None")
            endMsg()
        }

        launch(Dispatchers.IO) {
            startMsg()
            println("IO")
            endMsg()
        }

        // Main solo para Andriod (error si lo utilizamos aquí en Kotlin puro)
//        launch(Dispatchers.Main) {
//            startMsg()
//            println("Main")
//            endMsg()
//        }

        launch(Dispatchers.Default) {
            startMsg()
            println("Default")
            endMsg()
        }

        launch(newSingleThreadContext("Cursos Adoroid ANT")) {
            startMsg()
            println("Mi corrutina personalizada con un dispatcher")
            endMsg()
        }

        newSingleThreadContext("CurosAndroidANT").use { myContext ->
            launch(myContext) {
                startMsg()
                println("Mi corrutina personalizada con un dispatcher2")
                endMsg()
            }
        }
    }
}
