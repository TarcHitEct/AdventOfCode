package aoc2021

import readInput

private fun main() {
    val binary = readInput(::main).trim()
        .windowed(1)
        .map { it.toLong(16).toString(2).padStart(4, '0') }
        .joinToString("")
    val packet = decodePacket(binary.iterator())
    println(sumVersion(packet))
    println(packet.value())
}

fun sumVersion(packet: Packet): Long {
    return packet.version + packet.subPackets.sumOf { sumVersion(it) }
}

fun decodePacket(bits: CharIterator): Packet {
    val version = get(3, bits).toLong(2)
    val type = get(3, bits).toLong(2)
    if (type == LITERAL) {
        return Value(version, type, decodeValue(bits))
    } else { //OPERATOR
        return Operator(version, type, decodeOperands(bits))
    }
}

fun decodeOperands(bits: CharIterator): List<Packet> {
    val lengthType = get(1, bits)
    if (lengthType == "0") {
        val nrBits = get(15, bits).toLong(2)
        return decodeMultiple(get(nrBits, bits).iterator())
    } else {
        val nrPackets = get(11, bits).toLong(2)
        return (0 until nrPackets).map { decodePacket(bits) }
    }
}

fun decodeMultiple(bits: CharIterator): List<Packet> {
    val packets = mutableListOf<Packet>()
    while (bits.hasNext()) {
        packets.add(decodePacket(bits))
    }
    return packets
}

fun decodeValue(bits: CharIterator): Long {
    var next = true
    var value = ""
    while (next) {
        next = get(1, bits) == "1"
        value += get(4, bits)
    }
    return value.toLong(2)
}

fun get(n: Long, bits: CharIterator): String {
    return (0 until n).map { bits.next() }.joinToString("")
}

class Value(version: Long, type: Long, val value: Long) : Packet(version, type, emptyList()) {
    override fun value(): Long {
        return value
    }
}

class Operator(version: Long, type: Long, subPackets: List<Packet>) : Packet(version, type, subPackets) {
    override fun value(): Long {
        return when (type) {
            SUM -> subPackets.sumOf { it.value() }
            PRODUCT -> subPackets.fold(1L) { acc, packet -> acc * packet.value() }
            MINIMUM -> subPackets.minOf { it.value() }
            MAXIMUM -> subPackets.maxOf { it.value() }
            GT -> if (subPackets.first().value() > subPackets.last().value()) 1L else 0L
            LT -> if (subPackets.first().value() < subPackets.last().value()) 1L else 0L
            EQ -> if (subPackets.first().value() == subPackets.last().value()) 1L else 0L
            else -> throw Exception("Invalid type")
        }
    }
}

abstract class Packet(val version: Long, val type: Long, val subPackets: List<Packet>) {
    abstract fun value(): Long
}

const val LITERAL = 4L
const val SUM = 0L
const val PRODUCT = 1L
const val MINIMUM = 2L
const val MAXIMUM = 3L
const val GT = 5L
const val LT = 6L
const val EQ = 7L
