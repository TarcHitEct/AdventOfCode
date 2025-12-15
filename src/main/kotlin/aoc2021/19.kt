package aoc2021

import readInput
import solveA
import solveB
import java.util.*
import kotlin.math.*
import kotlin.Pair as KPair


private fun main() {
    val scanners = readInput(::main).let {
        it.split(Regex("---.*---")).filter { it.isNotBlank() }.map { scannerInput ->
            val beacons = scannerInput.trim().split("\n").map { beconPos ->
                val coords = beconPos.trim().split(",")
                Beacon(Position(coords[0].toLong(), coords[1].toLong(), coords[2].toLong()))
            }
            ScannerResult(beacons)
        }
    }.toMutableList()
    scanners.forEach { it.calcRelativeBeacons() }

    val group = Group(mutableListOf(scanners.removeFirst()))
    do {
        mergeOne(group, scanners)
        println("${scanners.size} left to merge")
    } while (scanners.isNotEmpty())

    solveA(::main, group.getAllBeacons().size)
    val maxDist = group.scanners.maxOf { a ->
        group.scanners.maxOf { b ->
            abs(a.center.x - b.center.x) +
                    abs(a.center.y - b.center.y) +
                    abs(a.center.z - b.center.z)
        }
    }
    solveB(::main, maxDist)
}

fun mergeOne(group: Group, scanners: MutableList<ScannerResult>) {
    scanners.forEach { scanner ->
        if (group.tryMerge(scanner)) {
            scanners.remove(scanner)
            return
        }
    }
    throw Exception("Could not merge any scanner")
}

data class Position(val x: Long, val y: Long, val z: Long) {
    operator fun minus(other: Position): Position {
        return Position(x - other.x, y - other.y, z - other.z)
    }

    operator fun plus(other: Position): Position {
        return Position(x + other.x, y + other.y, z + other.z)
    }

    fun transform(orientation: Orientation): Position {
        val faced = when (orientation.face) {
            "x" -> this
            "y" -> this.rotate(0, 0, 90)
            "z" -> this.rotate(90, 0, 0)
            else -> throw Exception("Invalid axis")
        }
        val flipped = if (orientation.flip) {
            faced.rotate(0, 0, 180)
        } else {
            faced
        }
        return flipped.rotate(0, 90 * orientation.rotate, 0)
    }

    fun rotate(pitch: Int, roll: Int, yaw: Int): Position {
        val cosa = cos(Math.toRadians(yaw.toDouble()))
        val sina = sin(Math.toRadians(yaw.toDouble()))

        val cosb = cos(Math.toRadians(pitch.toDouble()))
        val sinb = sin(Math.toRadians(pitch.toDouble()))

        val cosc = cos(Math.toRadians(roll.toDouble()))
        val sinc = sin(Math.toRadians(roll.toDouble()))

        val Axx = cosa * cosb
        val Axy = cosa * sinb * sinc - sina * cosc
        val Axz = cosa * sinb * cosc + sina * sinc

        val Ayx = sina * cosb
        val Ayy = sina * sinb * sinc + cosa * cosc
        val Ayz = sina * sinb * cosc - cosa * sinc

        val Azx = -sinb
        val Azy = cosb * sinc
        val Azz = cosb * cosc

        return Position(
            (Axx * x + Axy * y + Axz * z).roundToLong(),
            (Ayx * x + Ayy * y + Ayz * z).roundToLong(),
            (Azx * x + Azy * y + Azz * z).roundToLong()
        )
    }

    fun transform(transform: Transform): Position {
        return transform(transform.orientation) + transform.translate
    }
}

data class Beacon(val position: Position) {
    lateinit var relativeBeacons: Set<Position>

    fun calcRelativeBeacons(beacons: Collection<Beacon>) {
        relativeBeacons = beacons.map { inner ->
            inner.position - position
        }.toSet()
    }
}

data class ScannerResult(val beacons: Collection<Beacon>, val center: Position = Position(0, 0, 0)) {
    fun calcRelativeBeacons() {
        beacons.forEach { b ->
            b.calcRelativeBeacons(beacons.filter { it != b })
        }
    }

    companion object {
        val mergeCache = mutableMapOf<KPair<ScannerResult, ScannerResult>, Optional<Transform>>()
    }

    fun canMergeCached(other: ScannerResult): Transform? {
        mergeCache.computeIfAbsent(KPair(this, other)) {
            Optional.ofNullable(canMerge(other))
        }.let {
            return if (it.isPresent) {
                it.get()
            } else {
                null
            }
        }
    }

    fun canMerge(other: ScannerResult): Transform? {
        this.beacons.forEach { a ->
            other.beacons.forEach { b ->
                allOrientations.forEach { orientation ->
                    val common = a.relativeBeacons.intersect(b.relativeBeacons.map { it.transform(orientation) })
                    if (common.size >= 11) {
                        val offset = a.position - b.position.transform(orientation)
                        return Transform(offset, orientation)
                    }
                }
            }
        }
        return null
    }

    fun transform(transform: Transform): ScannerResult {
        return ScannerResult(
            beacons.map { Beacon(it.position.transform(transform)) },
            center.transform(transform)
        ).apply {
            calcRelativeBeacons()
        }
    }
}

data class Group(val scanners: MutableList<ScannerResult>) {
    fun tryMerge(b: ScannerResult): Boolean {
        scanners.forEach { a ->
            val mergeOrientationTransform = a.canMergeCached(b)
            if (mergeOrientationTransform != null) {
                scanners.add(b.transform(mergeOrientationTransform))
                return true
            }
        }
        return false
    }

    fun getAllBeacons(): Set<Beacon> {
        return scanners.flatMap { it.beacons }.toSet()
    }
}

data class Transform(val translate: Position, val orientation: Orientation)

data class Orientation(val face: String, val flip: Boolean, val rotate: Int)

val allOrientations = listOf("x", "y", "z").flatMap { axis ->
    listOf(false, true).flatMap { invert ->
        listOf(0, 1, 2, 3).map { rotate ->
            Orientation(axis, invert, rotate)
        }
    }
}

val testInput19 = """
    --- scanner 0 ---
    404,-588,-901
    528,-643,409
    -838,591,734
    390,-675,-793
    -537,-823,-458
    -485,-357,347
    -345,-311,381
    -661,-816,-575
    -876,649,763
    -618,-824,-621
    553,345,-567
    474,580,667
    -447,-329,318
    -584,868,-557
    544,-627,-890
    564,392,-477
    455,729,728
    -892,524,684
    -689,845,-530
    423,-701,434
    7,-33,-71
    630,319,-379
    443,580,662
    -789,900,-551
    459,-707,401

    --- scanner 1 ---
    686,422,578
    605,423,415
    515,917,-361
    -336,658,858
    95,138,22
    -476,619,847
    -340,-569,-846
    567,-361,727
    -460,603,-452
    669,-402,600
    729,430,532
    -500,-761,534
    -322,571,750
    -466,-666,-811
    -429,-592,574
    -355,545,-477
    703,-491,-529
    -328,-685,520
    413,935,-424
    -391,539,-444
    586,-435,557
    -364,-763,-893
    807,-499,-711
    755,-354,-619
    553,889,-390

    --- scanner 2 ---
    649,640,665
    682,-795,504
    -784,533,-524
    -644,584,-595
    -588,-843,648
    -30,6,44
    -674,560,763
    500,723,-460
    609,671,-379
    -555,-800,653
    -675,-892,-343
    697,-426,-610
    578,704,681
    493,664,-388
    -671,-858,530
    -667,343,800
    571,-461,-707
    -138,-166,112
    -889,563,-600
    646,-828,498
    640,759,510
    -630,509,768
    -681,-892,-333
    673,-379,-804
    -742,-814,-386
    577,-820,562

    --- scanner 3 ---
    -589,542,597
    605,-692,669
    -500,565,-823
    -660,373,557
    -458,-679,-417
    -488,449,543
    -626,468,-788
    338,-750,-386
    528,-832,-391
    562,-778,733
    -938,-730,414
    543,643,-506
    -524,371,-870
    407,773,750
    -104,29,83
    378,-903,-323
    -778,-728,485
    426,699,580
    -438,-605,-362
    -469,-447,-387
    509,732,623
    647,635,-688
    -868,-804,481
    614,-800,639
    595,780,-596

    --- scanner 4 ---
    727,592,562
    -293,-554,779
    441,611,-461
    -714,465,-776
    -743,427,-804
    -660,-479,-426
    832,-632,460
    927,-485,-438
    408,393,-506
    466,436,-512
    110,16,151
    -258,-428,682
    -393,719,612
    -211,-452,876
    808,-476,-593
    -575,615,604
    -485,667,467
    -680,325,-822
    -627,-443,-432
    872,-547,-609
    833,512,582
    807,604,487
    839,-516,451
    891,-625,532
    -652,-548,-490
    30,-46,-14
""".trimIndent()