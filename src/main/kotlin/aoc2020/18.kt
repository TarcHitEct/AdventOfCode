package aoc2020

import readInput

var precedence: Char? = null

private fun main() {
    val list = readInput(::main).trim().split("\n").map { it.trim().replace(" ", "") }
    list.sumOf {
        val (result, pos) = eval(it, 0)
        require(pos == it.length)
        result
    }.also {
        println(it)
    }

    precedence = '+'
    list.sumOf {
        val (result, pos) = eval(it, 0)
        require(pos == it.length)
        result
    }.also {
        println(it)
    }
}

private fun eval(str: String, start: Int): Pair<Long, Int> {
    var value = 0L
    var pos = start
    while (pos < str.length) {
        when {
            str[pos].isDigit() || str[pos] == '(' -> {
                val (result, newPos) = evalToken(str, pos)
                value = result
                pos = newPos
            }
            str[pos] == '+' -> {
                val (result, newPos) = evalToken(str, pos + 1)
                value += result
                pos = newPos
            }
            str[pos] == '*' -> {
                val (result, newPos) = evalToken(str, pos + 1)
                value *= result
                pos = newPos
            }
            str[pos] == ')' -> {
                return Pair(value, pos + 1)
            }
        }
    }
    return Pair(value, pos)
}

private fun evalToken(str: String, start: Int): Pair<Long, Int> {
    var (result, newPos) = when {
        str[start] == '(' -> {
            eval(str, start + 1)
        }
        str[start].isDigit() -> {
            val numberStr = Regex("\\d+").find(str, start)!!.value
            Pair(numberStr.toLong(), start + numberStr.length)
        }
        else -> {
            throw IllegalStateException("found ${str[start]} instead of token")
        }
    }
    if (newPos in str.indices && str[newPos] == precedence) {
        val (plusTokenResult, plusNewPos) = evalToken(str, newPos + 1)
        result += plusTokenResult
        newPos = plusNewPos
    }
    return Pair(result, newPos)
}