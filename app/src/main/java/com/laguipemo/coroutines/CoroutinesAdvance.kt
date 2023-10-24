package com.laguipemo.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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
    produceChannel()
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
