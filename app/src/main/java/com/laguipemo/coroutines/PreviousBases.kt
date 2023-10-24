package com.laguipemo.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import kotlin.concurrent.thread
import kotlin.random.Random

/**
 * Project: Coroutines
 * from: com.laguipemo.coroutines
 * Created by Lázaro Guillermo Pérez Montoto (chachy) on 21/10/23 at 15:27
 * All rights reserved 2023
 *
 * https://github.com/laguipemo/
 **/
 
fun main(){
//    lambda()
//    threads()
//    coroutinesVsThreads()
    sequences()
}

fun sequences() {
    newTopic("Sequences")
    getDataBySeq().forEach {
        println("${it}º")
    }
}

fun getDataBySeq(): Sequence<Float> {
    return sequence {
        (1..5).forEach {
            println("procesando datos...")
            Thread.sleep(someTime())
            yield(20 + it * Random.nextFloat())
        }
    }
}

fun coroutinesVsThreads() {
    newTopic("Coroutines vs Threads")
    runBlocking {
        (1..1_000_000).forEach {
            launch {
                delay(someTime())
                print("*")
            }
        }

    }

//    (1..1_000_000).forEach {
//        thread {
//            Thread.sleep(someTime())
//            print("*")
//        }
//
//    }
}

fun threads() {
    newTopic("Threads")
    println("Thread: ${multiThread(2, 3)}")
    multiTreadLamda(2, 3) {
            result -> println("Thread + Lambda: $result")
    }
}

fun multiTreadLamda(x: Int, y: Int, callback: (result: Int) -> Unit) {
    var result = 0
    thread {
        Thread.sleep(someTime())
        result = multi(x, y)
        callback(result)
    }
}

fun multiThread(x: Int, y: Int): Int {
    var result = 0
    thread {
        Thread.sleep(someTime())
        result = multi(x, y)
    }
//    Thread.sleep(2100)
    return result
}

fun lambda() {
    newTopic("Lambda")
    println("Sin lambda: ${multi(2, 3)}")
//    multiLambda(2, 3, { result -> println(result) })
    multiLambda(2, 3) { result ->
        println("Con lambda: ${result}")
    }
}

fun multiLambda(x: Int, y: Int, callback: (result: Int) -> Unit) {
    callback(multi(x, y))
}

fun multi(x: Int, y: Int): Int {
    return x * y
}

private const val SEPARATOR = "====================="
fun newTopic(topic: String) {
    println("\n$SEPARATOR $topic $SEPARATOR\n")
}

fun someTime(): Long = Random.nextLong(500, 2000)


