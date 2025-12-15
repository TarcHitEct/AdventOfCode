package aoc2021

import deepCopy
import readInput
import solveA
import solveB
import java.io.Serializable
import kotlin.math.ceil

private fun main() {
    val list = readInput(::main).let {
        it.trim().split("\n").map {
            readSNumber(it.trim().map { it.toString() }.toMutableList())
        }
    }.toList()
    val sum = list.reduce { a, b -> a + b }
    println(sum.toString())
    solveA(::main, sum.getMagnitude())
    val max = list.maxOf { a ->
        list.maxOf { b ->
            if (a != b) {
                (a + b).getMagnitude()
            } else {
                0
            }
        }
    }
    solveB(::main, max)
}

fun readSNumber(chars: MutableList<String>): SNumber {
    if (chars[0] == "[") {
        chars.removeFirst()
        val left = readSNumber(chars)
        chars.removeFirst()
        val right = readSNumber(chars)
        chars.removeFirst()
        return Pair(left, right)
    } else {
        return RegularNumber(chars.removeFirst().toInt())
    }
}

var _prev: RegularNumber? = null

abstract class SNumber : Serializable {

    abstract fun link()

    operator fun plus(other: SNumber): SNumber {
        return Pair(this, other).reduce()
    }

    fun reduce(): SNumber {
        var res: SNumber = deepCopy(this)
        do {
            _prev = null
            res.link()
            val afterExplode = res.explode()
            val exploded = res != afterExplode
            val afterSplit = if (!exploded) {
                res.split()
            } else {
                afterExplode
            }
            val split = res != afterSplit
            res = afterSplit
        } while (exploded || split)
        return res
    }

    abstract fun split(): SNumber

    abstract fun explode(depth: Int = 0): SNumber

    abstract fun getMagnitude(): Int
}

data class Pair(val left: SNumber, val right: SNumber) : SNumber() {
    override fun link() {
        left.link()
        right.link()
    }

    override fun split(): SNumber {
        val leftSplit = left.split()
        if (leftSplit != left) {
            return Pair(leftSplit, right)
        } else {
            return Pair(leftSplit, right.split())
        }
    }

    override fun explode(depth: Int): SNumber {
        if (depth < 4) {
            val leftEx = left.explode(depth + 1)
            if (leftEx != left) {
                return Pair(leftEx, right)
            } else {
                return Pair(leftEx, right.explode(depth + 1))
            }
        }
        val leftVal = left as RegularNumber
        val rightVal = right as RegularNumber
        leftVal.prev?.let {
            it.value += left.value
        }
        rightVal.next?.let {
            it.value += rightVal.value
        }
        return RegularNumber(0)
    }

    override fun getMagnitude(): Int {
        return 3 * left.getMagnitude() + 2 * right.getMagnitude()
    }

    override fun toString(): String {
        return "[$left,$right]"
    }

}

data class RegularNumber(var value: Int, var prev: RegularNumber? = null, var next: RegularNumber? = null) : SNumber() {
    override fun link() {
        this.prev = _prev
        if (_prev != null) {
            _prev!!.next = this
        }
        _prev = this
    }

    override fun split(): SNumber {
        if (value >= 10) {
            return Pair(RegularNumber(value / 2), RegularNumber(ceil(value.toDouble() / 2).toInt()))
        } else {
            return this
        }
    }

    override fun explode(depth: Int): SNumber {
        return this
    }

    override fun getMagnitude(): Int {
        return value
    }

    override fun toString(): String {
        return value.toString()
    }
}