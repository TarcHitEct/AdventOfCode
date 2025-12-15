package aoc2020

import readInput

private fun main() {
    val input = readInput(::main)
    val rules = input.trim().split("\n\n").first().trim().split("\n").map {
        it.trim().let {
            val colon = it.indexOf(":")
            val nr = it.substring(0, colon).toInt()
            Rule2(nr, cleanRule(nr, it.substring(colon + 1).trim()))
        }
    }
    val mainRule = StringBuilder(rules.single { it.id == 0 }.expr)
    while (mainRule.contains(Regex("\\d"))) {
        rules.forEach {
            val toReplace = " ${it.id.toString()} "
            val index = mainRule.indexOf(toReplace)
            if (index != -1) {
                mainRule.replace(index, index + toReplace.length, " ${it.expr} ")
            }
        }
    }
    val mainRegex = Regex(mainRule.replace(Regex(" "), ""))

    val msgs = input.trim().split("\n\n")[1].trim().split("\n").map {
        it.trim()
    }
    msgs.count {
        mainRegex.matchEntire(it) != null
    }.also {
        println(it)
    }
}

data class Rule2(val id: Int, val expr: String)

fun cleanRule(nr: Int, expr: String): String {
    /* // enable for part two and evaluate on regex101. the java regex implementation does not support recursion.
    if (nr == 8) {
        return "( 42 +)"
    }
    if (nr == 11) {
        return "(?<recname> 42 \\g<recname>? 31 )"
    }*/
    return expr.replace("\"", "").let {
        if (it.contains("|")) {
            "(( ${it.replace("|", " )|( ")} ))"
        } else {
            "( ${it} )"
        }
    }
}