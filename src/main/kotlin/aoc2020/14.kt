package aoc2020

import readInput
import kotlin.math.pow

private fun main() {
    val list = readInput(::main).let {
        it.trim().split("\n").map {
            it.trim().let {
                if (it.startsWith("mask = ")) {
                    MaskOp(it.substring("mask = ".length))
                } else {
                    Regex("mem\\[(\\d*)\\] = (\\d*)").matchEntire(it)!!.let {
                        MemOp(it.groupValues[1].toLong(), it.groupValues[2].toLong())
                    }
                }
            }
        }
    }.toList()

    val mem = mutableMapOf<Long, Long>()
    var mask = list.first() as MaskOp
    list.forEach {
        if (it is MaskOp) {
            mask = it
        }
        if (it is MemOp) {
            mem[it.addr] = (it.value and mask.removeMask) + mask.addValue
        }
    }
    println(mem.values.sum())

    mem.clear()
    mask = list.first() as MaskOp
    list.forEach {
        if (it is MaskOp) {
            mask = it
        }
        if (it is MemOp) {
            it.getEffectiveAddrs(mask.value).forEach { addr ->
                mem[addr] = it.value
            }
        }
    }
    println(mem.values.sum())

}

data class MaskOp(val value: String) {
    val removeMask = value.replace("1", "0").replace("X", "1").toLong(2)
    val addValue = value.replace("X", "0").toLong(2)
}

data class MemOp(val addr: Long, val value: Long) {
    val binaryStr = addr.toString(2).padStart(36, '0')

    fun getMaskedAddr(mask: String): String {
        require(mask.length == binaryStr.length)
        return mask.mapIndexed { index, maskChar ->
            val addrChar = binaryStr[index]
            if (maskChar == 'X' || maskChar == '1') {
                maskChar
            } else {
                addrChar
            }
        }.joinToString("")
    }

    fun getEffectiveAddrs(mask: String): List<Long> {
        val masked = getMaskedAddr(mask)
        val xIndices = masked.mapIndexedNotNull { index, c ->
            if (c == 'X') index else null
        }
        return (0 until 2.0.pow(xIndices.size.toDouble()).toLong()).map {
            val fillStr = it.toString(2).padStart(xIndices.size, '0')
            var result = masked
            fillStr.forEach {
                result = result.replaceFirst('X', it)
            }
            result.toLong(2)
        }
    }
}
