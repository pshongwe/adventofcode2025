package day4

import utils.InputReader

fun main(args: Array<String>) {
    // Prep Data:
    val lines = InputReader.readLinesForDay(4, args)
    val grid = lines.map { it.toCharArray() }.toTypedArray()
    println("Initial Grid: ")
    println("----------")
    grid.forEach { println(it.concatToString()) }
    println("----------")

    // Solve Problem

}