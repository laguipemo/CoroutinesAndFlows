package com.laguipemo.coroutines

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

/**
 * Project: Coroutines
 * from: com.laguipemo.coroutines
 * Created by Lázaro Guillermo Pérez Montoto (chachy) on 25/10/23 at 0:59
 * All rights reserved 2023
 *
 * https://github.com/laguipemo/
 **/
val estudiantes = listOf("Juan", "María", "Pedro", "Ana", "Carlos", "Lucía")
val asignaturas = listOf("Matematicas", "Fisica", "Quimica", "Historia", "Lengua")


@OptIn(ExperimentalCoroutinesApi::class)
fun main() {
    runBlocking {
        launch {
            var totalSuspensos = 0
            getEstudiantes()
                .buffer()
                .flatMapConcat { estudiante ->
                    getEvaluation(estudiante)
                        .catch {
                            println("Alerta: Suspenso")
                            totalSuspensos++
                            if (totalSuspensos >= 2) {
                                cancel()
                                throw Exception("Demasiados suspensos")
                            }
                        }
                }
                .catch {
                    println(it.message)
                    cancel()
                }
                .map {
                    setFormat(it)
                }
                .collect { println(it) }
        }

    }

}

fun setFormat(asignaturaNota: Pair<String, Int>): String =
    "${" ".repeat(5)}${asignaturaNota.first} : ${asignaturaNota.second}"

fun getEstudiantes(): Flow<String> = flow {
    delay(200)
    estudiantes.forEach{
        emit(it)
    }
}

fun getEvaluation(estudiante: String): Flow<Pair<String, Int>> = flow {
    var total = 0
    println("\nEstudiante: $estudiante")
    println("-".repeat(20))
    asignaturas.forEach{asignatura ->
        delay(100)
        val nota = Random.nextInt(0, 10)
        total += nota
        emit(Pair(asignatura, nota ))
    }
    val promedio = total/asignaturas.size
    println("Promedio: $promedio")
    if (promedio < 5) throw Exception("Reprobado")
}