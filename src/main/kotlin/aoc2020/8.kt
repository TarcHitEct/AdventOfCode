package aoc2020

import readInput

private fun main() {
    var ops = readInput(::main).let {
        Regex("(...) (\\+?-?\\d*)").findAll(it).map {
            Op(it.groupValues[1].trim(), it.groupValues[2].trim().toInt())
        }
    }.toList()

    ops.indices.find {
        execute(ops.mapIndexed { index, op ->
            if (index == it) {
                op.fix()
            } else op
        })
    }
}

private fun execute(ops: List<Op>): Boolean {
    val executed = mutableSetOf<Int>()
    var acc = 0
    var ip = 0
    while (ip >= 0 && ip < ops.size) {
        if (executed.contains(ip)) {
            println("Loop detected. acc = $acc")
            return false
        }
        executed.add(ip)
        val op = ops[ip]
        when (op.name) {
            "acc" -> {
                acc += op.value
                ip++
            }
            "jmp" -> ip += op.value
            "nop" -> ip += 1
            else -> throw Exception("Invalid op ${op.name}")
        }
    }
    println("Program finished ${if (ip == ops.size) "correctly" else "incorrectly"}. acc = $acc")
    return ip == ops.size
}

data class Op(val name: String, val value: Int) {
    fun fix(): Op {
        return when (name) {
            "acc" -> this
            "jmp" -> Op("nop", value)
            "nop" -> Op("jmp", value)
            else -> throw Exception("Invalid op $name")
        }
    }
}