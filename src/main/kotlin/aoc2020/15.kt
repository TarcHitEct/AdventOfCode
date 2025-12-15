package aoc2020

private fun main() {
    val nthNumber = 30000000
    val list = mutableListOf(2, 0, 1, 7, 4, 14, 18)

    val map = list.dropLast(1).mapIndexed { index, i ->
        i to index
    }.toMap().toMutableMap()
    var cur = list.last()
    (list.size - 1 until nthNumber - 1).forEach {
        val lastIdx = map[cur]
        map[cur] = it
        if (lastIdx != null) {
            cur = it - lastIdx
        } else {
            cur = 0
        }
    }
    println(cur)
}