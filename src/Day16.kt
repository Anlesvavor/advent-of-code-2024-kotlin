import kotlin.Float
import kotlin.math.pow

fun <X, R> ((X) -> R).memoized(): (X) -> R {
    val cache = mutableMapOf<X, R>()
    return { cache.getOrPut(it) { this(it) } }
}

fun <X, R> memoize(fn: ((X) -> R)): (X) -> R {
    val cache = mutableMapOf<X, R>()
    return { cache.getOrPut(it) { fn(it) } }
}

fun <X, Y, R> memoize(fn: ((X, Y) -> R)): (X, Y) -> R {
    val cache = mutableMapOf<Pair<X, Y>, R>()
    return { x, y -> cache.getOrPut(x to y) { fn(x, y) } }
}

fun <A, B> Pair<A?, B?>.takeIfBothNotNullOrNull(): Pair<A, B>? {
    return if (first != null && second != null) {
        this as Pair<A, B>?
    } else null
}

fun Int.squared(): Int = this * this

fun Int.squareRoot(): Float = this.toFloat().pow(2)

fun Coord.distanceTo(other: Coord): Float {
    return first.distanceTo(other.first).squared().plus(second.distanceTo(other.second).squared()).squareRoot()
}
sealed class MazeTile() {
    abstract var row: Row
    abstract var col: Col
    val coord: Coord
        get() = row to col

    data class Free(override var row: Row, override var col: Col): MazeTile()
    data class Wall(override var row: Row, override var col: Col): MazeTile()
    data class Start(override var row: Row, override var col: Col): MazeTile()
    data class End(override var row: Row, override var col: Col): MazeTile()


    companion object {
        fun fromChar(row: Row, col: Col, c: Char): MazeTile = when(c) {
            '#' -> Wall(row, col)
            '.' -> Free(row, col)
            'S' -> Start(row, col)
            'E' -> End(row, col)
            else -> throw IllegalArgumentException("Not a maze tile")
        }

        fun fromInput(input: List<String>): List<MazeTile> {
            return input.flatMapIndexed { r, row -> row.mapIndexed { c, char -> fromChar(r, c, char) } }
        }
    }

    fun toChar(): Char = when(this) {
        is End -> 'E'
        is Free -> '.'
        is Start -> 'S'
        is Wall -> '#'
    }
}
val Orientation.opposite: Orientation
    get() = when(this) {
        Orientation.TOP -> Orientation.BOTTOM
        Orientation.TOP_RIGHT -> Orientation.BOTTOM_LEFT
        Orientation.RIGHT -> Orientation.LEFT
        Orientation.BOTTOM_RIGHT -> Orientation.TOP_LEFT
        Orientation.BOTTOM -> Orientation.TOP
        Orientation.BOTTOM_LEFT -> Orientation.TOP_RIGHT
        Orientation.LEFT -> Orientation.RIGHT
        Orientation.TOP_LEFT -> Orientation.BOTTOM_RIGHT
    }

val Orientation.delta: Pair<Int, Int>
    get() = when(this) {
        Orientation.TOP -> -1 to 0
        Orientation.BOTTOM_RIGHT -> 1 to 1
        Orientation.RIGHT -> 0 to 1
        Orientation.TOP_RIGHT -> -1 to 1
        Orientation.BOTTOM -> 1 to 0
        Orientation.TOP_LEFT -> -1 to -1
        Orientation.LEFT -> 0 to -1
        Orientation.BOTTOM_LEFT -> 1 to -1
    }


fun List<MazeTile>.nextOf(tile: MazeTile, orientation: Orientation): Map<Orientation, MazeTile> {
    return listOf(Orientation.TOP, Orientation.RIGHT, Orientation.BOTTOM, Orientation.LEFT)
        .filterNot { it == orientation.opposite }
        .associateWith { tile.coord + it.delta }
        .mapNotNull { neighbouringCord ->
            find { it.coord == neighbouringCord.value && it !is MazeTile.Wall }?.let {
                neighbouringCord.key to it
            }
        }
        .toMap()
}

fun List<MazeTile>.buildNeighborsMap(): Map<Coord, Map<Orientation, MazeTile>> {
    val orientations = listOf(Orientation.TOP, Orientation.RIGHT, Orientation.BOTTOM, Orientation.LEFT)
    val nonWalls = filterNot { it is MazeTile.Wall }.associateBy(MazeTile::coord)
    return nonWalls.mapValues { (_, tile) ->
        orientations.mapNotNull { it.to(nonWalls[tile.coord + it.delta]).takeIfBothNotNullOrNull() }.toMap()
    }
}

//val memoizedNeighborsOf = fun(maze: List<MazeTile>): (MazeTile) -> Map<Orientation, MazeTile> {
//    val nonWalls = maze.filter { it !is MazeTile.Wall }
//    return memoize(fn = nonWalls::neighborsOf)
//}


fun <K, V> List<Map.Entry<K, V>>.toMap(): Map<K, V> = map { it.toPair() }.toMap()

fun <K, V> Map<K, V>.partition(predicate: (Map.Entry<K, V>) -> Boolean): Pair<Map<K, V>, Map<K, V>> {
    return entries.partition(predicate).let { it.first.toMap() to it.second.toMap() }
}

//    fun computeNextTiles(neighbors: Map<Orientation, MazeTile>, tile: Triple<MazeTile, Orientation, Int>): List<List<Triple<MazeTile, Orientation, Int>>> {
//        tailrec fun Map<Orientation, MazeTile>.aux(tile: Triple<MazeTile, Orientation, Int>, acc: List<Triple<MazeTile, Orientation, Int>>): List<List<Triple<MazeTile, Orientation, Int>>> {
//            val nextTiles = neighborsMap.getOrDefault(tile.first.coord, emptyMap()).filterNot { it.key == tile.second.opposite }
//            return when {
//                nextTiles.isEmpty() -> {
//                    listOf(acc)
//                }
//                nextTiles.size == 1 -> {
//                    val nextTile = nextTiles.entries.first().toPair()
//                    if (nextTile.first == tile.second) {
//                        aux(Triple(nextTile.second, nextTile.first, 1), acc.plusElement(tile))
//                    } else {
//                        aux(Triple(nextTile.second, nextTile.first, 1001), acc.plusElement(tile))
//                    }
//                }
//                else -> { // Branching "Crossroad tile"
//                    nextTiles.map {
//                        if (it.key == tile.second) {
//                            acc.plusElement(tile).plus(Triple(it.value, it.key, 1))
//                        } else {
//                            acc.plusElement(tile).plus(Triple(it.value, it.key, 1001))
//                        }
//                    }
//                }
//            }
//        }
//        return neighbors.aux(tile, listOf<Triple<MazeTile, Orientation, Int>>())
//    }

fun List<MazeTile>.buildPaths(log: (List<Triple<MazeTile, Orientation, Int>>) -> Unit = { }): List<List<Triple<MazeTile, Orientation, Int>>> {
    val start: MazeTile = single { it is MazeTile.Start }
    val end: MazeTile = single { it is MazeTile.End }
    val neighborsMap = buildNeighborsMap()
    tailrec fun aux(
        acc: List<List<Triple<MazeTile, Orientation, Int>>>,
        stack: List<List<Triple<MazeTile, Orientation, Int>>>,
        countDown: Int
    ): List<List<Triple<MazeTile, Orientation, Int>>> {
        if (countDown <= 0) { return acc }
        val (stackHead, stackTail) = stack.headTail()
        if (stackHead.isNullOrEmpty()) { return acc }
        check(stackHead.isNotEmpty())
        val (head: Triple<MazeTile, Orientation, Int>?, tail: Iterable<Triple<MazeTile, Orientation, Int>>) = stackHead.headTail()
        check(head != null)
        if (head.first == end) {
            log(stackHead)
            val newAcc = acc.plusElement(stackHead)
            stackHead
            return aux(newAcc, stackTail.toList(), countDown)
        }
        val nextTiles: Map<Orientation, MazeTile> = neighborsMap.getOrDefault(head.first.coord, emptyMap()).filterNot { it.key == head.second.opposite }
//        val nextTiles = computeNextTiles(neighborsMap.getValue(head.first.coord), head)
        if (nextTiles.isEmpty()) {
            log(stackHead)
            val newAcc = acc.plusElement(stackHead)
            return aux(newAcc, stackTail.toList(), countDown)
        }
        val (_cyclingPaths, directPaths) = nextTiles.partition { (_, nextTile) ->
            tail.any { it.first == nextTile }
        }
        val newPaths = directPaths.mapNotNull { (nextOrientation, nextTile) ->
            val newPath = if (tail.any { it.first == nextTile }) {
                tail.plusElement(head).asReversed()
            } else {
                val cost = if (nextOrientation == head.second) 1 else 1001
                tail.plusElement(head).plus(Triple(nextTile, nextOrientation, cost)).asReversed()
            }
            newPath
        }
        val newStackTail = stackTail.plus(newPaths)
            .partition { it.any { it.first == end } }
            .let { (hasEnd, incomplete) ->
                hasEnd + (incomplete.sortedBy {
                    it.first().first.coord.distanceTo(end.coord)
                })
            }
        return aux(acc, newStackTail, countDown.dec())
//        return aux(acc, stackTail.plus(newPaths))
//        val newPaths = nextTiles
//            .map { nextTilePath ->
//                val directPath = nextTilePath.filterNot {
//                    tail.any { x -> x.first == it.first }
//                }
//                tail.plus(directPath)
//            }
//        return aux(acc, stackTail.plus(newPaths))
    }
//    val startingStack = nextOf(start).map { listOf(Triple(it.value, it.key, 1)) }
    val startingStack = listOf(listOf(Triple(start, Orientation.RIGHT, 0)))
    return aux(emptyList(), startingStack, 100000)
}

fun pathToString(height: Int, width: Int, maze: List<MazeTile>, path: List<Triple<MazeTile, Orientation, Int>>): String {
    var matrix = IntRange(0, height * width).map { ' ' }.toCharArray()
    maze.forEach { tile ->
        val i = tile.coord.first.times(width).plus(tile.coord.second)
        matrix[i] = tile.toChar()
    }
    path.forEach { (tile, orientation, _) ->
        val i = tile.coord.first.times(width).plus(tile.coord.second)
        matrix[i] = when(orientation) {
            Orientation.TOP -> '^'
            Orientation.RIGHT -> '>'
            Orientation.BOTTOM -> 'v'
            Orientation.LEFT -> '<'
            else -> throw Error("not supported")
        }
    }
    return matrix.toList().chunked(width).map { it.joinToString(separator = "") }.joinToString(separator = "\n")
}

val List<String>.width
    get() = first().length

val List<String>.height
    get() = size

fun main() {
    fun part1(input: List<String>): Int {
        val maze = MazeTile.fromInput(input)
        val paths = maze
            .buildPaths(
                log = {
//                    pathToString(input.height, input.width, maze, it).println()
                }
            )
            .filter {
                it.any { (tile, _) -> tile is MazeTile.End  }
            }

        val pathsOrdered = paths.sortedBy {
            it.sumOf { it.third }
        }
        val best =  pathsOrdered.first()
        pathToString(input.height, input.width, maze, best).println()
        return best.sumOf { it.third }
    }

    readInput("Day16_test").let {
        part1(it).println()
    }

    readInput("Day16_test_2").let {
        part1(it).println()
    }

    readInput("Day16").let {
        part1(it).println()
    }
}