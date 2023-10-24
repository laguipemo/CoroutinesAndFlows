package com.laguipemo.coroutines

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Locale
import kotlin.random.Random
import kotlin.system.measureTimeMillis

/**
 * Project: Coroutines
 * from: com.laguipemo.coroutines
 * Created by Lázaro Guillermo Pérez Montoto (chachy) on 22/10/23 at 19:11
 * All rights reserved 2023
 *
 * https://github.com/laguipemo/
 **/

@OptIn(ExperimentalCoroutinesApi::class)
fun main() {
//    coldFlow()
//    cancelFlow()
//    flowOperators()
//    terminalFlowOperators()
//    bufferFlow()
//    conflationFlow()
//    multiFlow()
//    flatFlow()
//    flowExeptions()
    completions()
}

fun completions() {
    runBlocking {
        newTopic("Fin de un Flujo (onCompletion)")
        getCitiesFlow()
            .onCompletion {
                println("Quitar el progressBar...")
            }
            .collect {
                println(it)
            }
        println()

        getMatchResultFlow()
            .onCompletion {
                println("Mostrar las estadísticas")
            }
            .catch {
                emit("Error: $this $it")
            }
//            .collect{
//                println(it)
//            }

        newTopic("Cancelando un Flow")
        getDataByFlowStatic()
            .onCompletion { println("Ya no le intersa al usuario...") }
            .cancellable()
            .collect{
                if (it > 22.5f) { cancel() }
                println(it)
            }
    }
}

fun flowExeptions() {
    runBlocking {
        newTopic("Control de errores en flujos")

        newTopic("Try/Catch")
//        try {
//            getMatchResultFlow()
//                .collect {
//                    println("Result: $it")
//                    if (it.contains("2"))  throw Exception("Habían acordado 1:1 :V")
//                }
//        } catch (e: Exception) {
//            print("Error: ${e.message}\n")
//            e.printStackTrace()
//        }

        newTopic("Transparencia de la excepción")
        getMatchResultFlow()
            .catch {
                emit("Error: ${it.message}")
            }
//            .collect {
//                println("Resultado: $it")
//                if (!it.contains("-")) { println("Notificar al programador...") }
//            }


    }
}

@ExperimentalCoroutinesApi
fun flatFlow() {
    runBlocking {
        newTopic("Flujos de aplanamiento")

        newTopic("FlatMapConcat") // aplana de forma que cada elemento de un flujo se le pasa
                                        // al otro y se concatenan las emisiones de modo que se
                                        // colectan de forma organizada (priorizo organización)
        getCitiesFlow()
            .flatMapConcat { city ->
                getDataToFlatFlow(city)
            }
            .map { setFormat(it) }
//            .collect{ println(it) }

        newTopic("FlatMapMerge") // aplana de forma que cada elemento de un flujo se le pasa
                                       // al otro y se mezclan sus emisiones de modo que puedan
                                       // colectar lo más rápido posisible (priorizo velocidad)
        getCitiesFlow()
            .flatMapMerge { city ->
                getDataToFlatFlow(city)
            }
            .map { setFormat(it) }
            .collect{ println(it) }
    }
}

fun getDataToFlatFlow(city: String): Flow<Float> = flow {
    (1..5).forEach {
        println("Temperatura de ayer en $city")
        emit(Random.nextInt(10, 30).toFloat())

        println("Temperatura actual en $city")
        delay(100)
        emit(20 + it + Random.nextFloat())
    }

}

fun getCitiesFlow(): Flow<String> = flow {
    listOf("Bogotá", "Medellín", "Cali", "Buenos Aires", "Lima").forEach{city ->
        println("\n Consultando ciudad...")
        delay(1000)
        emit(city)
    }
}

fun multiFlow() {
    runBlocking {
        newTopic("Zip & Combine")
        getDataByFlowStatic()
            .map { setFormat(it) }
            .combine(getMatchResultFlow()) { degrees, result -> // une los flujos en uno, tamaño igual al mayor (el último valor del menor ser reutiliza)
//            .zip(getMatchResultFlow()) {degrees, result -> // une los flujos en uno, tamaño igual al menor
                "$result with $degrees"
            }
//            .collect{ println(it) }
    }
}

fun conflationFlow() {
    runBlocking {
        newTopic("Fusión de Flows")
        val time = measureTimeMillis {
            getMatchResultFlow()
                .conflate() // 2532 ms
//                .buffer() // 4630 ms
                .collectLatest { // con conflate y realizando la colección del último dato 2473 ms
//                .collect{ // sin conflate ni buffer, solo colectando los datos 6826 ms
                    delay(100)
                    println(it)
                }
        }
        println("Time: $time ms")
    }
}

fun getMatchResultFlow(): Flow<String> = flow {
    var homeTeam = 0
    var awayTeam = 0
    (1..45).forEach {
        println("minuto: $it")
        delay(50)
        homeTeam += Random.nextInt(0, 21)/20
        awayTeam += Random.nextInt(0, 21)/20
        emit("$homeTeam-$awayTeam")

        if(homeTeam == 2 || awayTeam == 2) { throw Exception("Habían acordado 1:1 :V") }
    }
}

fun bufferFlow() {
    runBlocking {
        newTopic("Buffer para Flow")
        val time = measureTimeMillis {
            getDataByFlowStatic()
                .map { setFormat(it) }
                .buffer()
                .collect {      //000111222333444 => 5x300ms=1500ms
                    delay(500) //***0000011111222223333344444 => 1x300ms + 5x500ms = 300ms + 2500ms = 2800ms para imprimir
                    println(it)
                }
        }
        println("Time: $time ms")
    }
}

fun getDataByFlowStatic(): Flow<Float> {
    return flow {
        (1..5).forEach {
            println("procesando datos...")
            delay(300)
            emit(20 + it * Random.nextFloat())
        }
    }
}

fun terminalFlowOperators() {
    runBlocking {
        newTopic("Operadores de Flow Terminales")

        newTopic("List") // se colectan todos los elementos del flow y se devuelve una lista
        val list = getDataByFlow()
            .map { setFormat(it) }
//            .toList()
        println("List: $list")

        newTopic("Single") // tomar un valor único. Si hay más de un valor lanza exception
        val single = getDataByFlow()
            .take(1) // se colectarán 1 elemento y así se evita la exception
            .map { setFormat(it) }
//            .single()
//        println("Single: $single")

        newTopic("First") // tomar el primer elemento del flow o el primero que cumpla con la condición que se le pase
        val first = getDataByFlow()
//            .first()
//            .first{ it > 23 }
//        println("First: ${first}")
//        println("First > 23: ${setFormat(first)}")

        newTopic("Last") // tomar el último elemento del flow
        val last = getDataByFlow()
//            .last()
//        println("Last: ${last}")

        newTopic("Reduce") // suma acumulativa de los elementos del flow, accumulador inicial = 0
        val saving = getDataByFlow()
            .reduce { accumulator, value ->
                println("Accumulator: $accumulator")
                println("Value: $value")
                println("Current saving: ${accumulator + value}")
                accumulator + value
            }
        println("Saving: $saving")

        newTopic("Fold") // igual que reduce pero se le pasa un valor incial para el acumulador
        val lastSaving = saving
        val totalSaving = getDataByFlow()
            .fold(lastSaving) { accumulator, value ->
                println("Accumulator: $accumulator")
                println("Value: $value")
                println("Current saving: ${accumulator + value}")
                accumulator + value
            }
        println("Total saving: $totalSaving")
    }
}

fun flowOperators() {
    runBlocking {
        newTopic("Operadores de Flow Intermediarios")

        newTopic("Map") // para sometera un proceso a cada uno de los elementos de flow
        getDataByFlow()
            .map {
                // cuando se realizan más un proceso a los datos, solo se tiene en cuenta el último
//                setFormat(it) // comentada esta línea o no, el resultado es el mismo
                setFormat(convertCelsToFahr(it), degree = "F")
            }
//            .collect { println(it) }

        newTopic("Filter") // se utiliza un condición para filtrar los datos que se colectarán
        getDataByFlow()
            .filter {
                it > 23
            }
            .map {
                setFormat(it)
            }
//            .collect {println(it)}

        newTopic("Transformar") // a diferencia de map, al hacer varias transformaciones, se
                                      // colectan todos los resultados
        getDataByFlow()
            .transform {
                emit(setFormat(it))
                emit(setFormat(convertCelsToFahr(it), degree = "F"))
            }
//            .collect { println(it) }

        newTopic("Take") // limita el número de elementos del flow que se colectarán
        getDataByFlow()
            .take(3) // se colectarán 3 elementos
            .map {
                setFormat(it)
            }
            .collect{ println(it) }

    }
}

fun convertCelsToFahr(cels: Float): Float = (cels * 9 / 5) + 32

fun setFormat(temp: Float, degree: String = "C"): String =
    String.format(Locale.getDefault(), "%.2fº${degree}", temp)

fun cancelFlow() {
    runBlocking {
        newTopic("Cancelar Flows")
        val job = launch {
            getDataByFlow().collect{ println("${it}º") }
        }
        delay(someTime() * 2)
        println("Cancelando...")
        job.cancel() // cancelando la corrutina que contiene al flow, este último se cancela
    }
}

fun coldFlow() {
    newTopic("Flows are Cold")
    runBlocking {
        // asigno un flow a una variable pero eso no implica que se comience a ejecutar el flow
        val dataFlow = getDataByFlow()
        println("esperando...")
        delay(someTime()) // durante este tiempo no se ejecuta el flow
        // solo cuando se manda el método receptor como collect es que se comienza a ejecutar el
        // flow, generándose los datos.
        dataFlow.collect{
            println("${it}º")
        }
    }
}
