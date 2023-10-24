package com.laguipemo.coroutines

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.ArithmeticException
import java.util.concurrent.TimeoutException

/**
 * Project: Coroutines
 * from: com.laguipemo.coroutines
 * Created by Lázaro Guillermo Pérez Montoto (chachy) on 24/10/23 at 18:28
 * All rights reserved 2023
 *
 * https://github.com/laguipemo/
 **/

val countries = listOf("Santander", "Bogota", "Medellin", "Barranquilla", "Cali", "Bucaramanga")

fun main() {
//    basicChannel()
//    closeChannel()
//    produceChannel()
//    pipelines()
//    bufferChannel()
    exceptions()
    readLine()
}

fun exceptions() {
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("Notifica al programador... $throwable in $coroutineContext")
        println()
        if (throwable is ArithmeticException) {
            println("Mostrar mensaje de reintentar...")
            println()
        }
    }

    runBlocking {
        newTopic("Manejo de excepciones")
        launch {
            try {
                delay(100)
//                throw Exception()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val globalScope = CoroutineScope(Job() + exceptionHandler)
        globalScope.launch {
            delay(200)
            throw TimeoutException("Agotado el tiempo del servidor")
        }

        CoroutineScope(Job() + exceptionHandler).launch {
            val result = async {
                delay(500)
                multiLambda(2, 3)  {
                    if (it > 5) throw ArithmeticException("Division por cero")
                }
            }
            println("Resultado: ${result.await()}")
        }

        val channel = Channel<String>()
        CoroutineScope(Job()).launch(exceptionHandler) {
            delay(800)
            countries.forEach {
                channel.send(it)
                if (it.equals("Medellin")) channel.close()
            }
        }
        channel.consumeEach {
            println(it)
        }
    }
}

fun bufferChannel() {
    runBlocking {
        newTopic("Buffer para canales")
        val time = System.currentTimeMillis()
        val channel = Channel<String>()
        launch {
            countries.forEach {
                delay(100)
                channel.send(it)
            }
            channel.close()
        }

        launch {
            delay(1000)
            channel.consumeEach { println(it) }
            println("Time: ${System.currentTimeMillis() - time}ms")
        }

        val bufferTime = System.currentTimeMillis()
        val bufferChannel = Channel<String>(3)
        launch {
            countries.forEach {
                delay(100)
                bufferChannel.send(it)
            }
            bufferChannel.close()
        }

        launch {
            delay(1000)
            bufferChannel.consumeEach { println(it) }
            println("BufferTime: ${System.currentTimeMillis() - bufferTime}ms")
        }
    }
}

fun pipelines() {
    runBlocking {
        newTopic("Pipelines")
        val citiesChannels = produceCities()
        val foodsChannel = produceFoods(citiesChannels)
        foodsChannel.consumeEach { println(it) }

        citiesChannels.cancel()
        foodsChannel.cancel()
        println("Todo está 10/10")
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun CoroutineScope.produceFoods(cities: ReceiveChannel<String>): ReceiveChannel<String> = produce{
    for (city in cities){
        val food = getFoodByCity(city)
        send("$food desde $city")
    }
}

suspend fun getFoodByCity(city: String): String {
    delay(300)
    return when (city) {
        "Santander" -> "Arepas"
        "Bogota" -> "Pizza"
        "Medellin" -> "Sancocho"
        "Barranquilla" -> "Chicharrón"
        "Cali" -> "Frijoles"
        "Bucaramanga" -> "Tacos"
        else -> "Sin datos"

    }
}

fun produceChannel() {
    runBlocking {
        newTopic("Canales y el patrón productor-consumidor")
        val names = produceCities()
        names.consumeEach {
            println(it)
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun CoroutineScope.produceCities(): ReceiveChannel<String> = produce {
    countries.forEach {
        send(it)
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun closeChannel() {
    runBlocking {
        newTopic("Cerrar un canal")
        val channel = Channel<String>()
        launch {
            countries.forEach {
                channel.send(it)
//                if (it.equals("Medellin")) channel.close()
                if (it.equals("Medellin")) {
                    channel.close()
                    return@launch
                }
            }
//            channel.close()
        }

//        for (value in channel) {
//            println(value)
//        }

        while(!channel.isClosedForReceive){
            println(channel.receive())
        }

//        channel.consumeEach { println(it) }
    }
}

fun basicChannel() {
    runBlocking {
        newTopic("Canal Basico")
        val channel = Channel<String>()
        launch {
            countries.forEach {
                channel.send(it)
            }
        }

//        repeat(countries.size) {
//            println(channel.receive())
//        }

        for (value in channel) {
            println(value)
        }
    }
}
