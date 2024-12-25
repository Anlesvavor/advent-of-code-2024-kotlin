import org.w3c.dom.xpath.XPathResult
import kotlin.math.absoluteValue


//enum class Orientation { NORTH, EAST, SOUTH, WEST }

fun Int.distanceTo(other: Int) = minus(other).absoluteValue

private fun Orientation.rotatedClockwise(): Orientation = when (this) {
    Orientation.TOP -> Orientation.RIGHT
    Orientation.RIGHT -> Orientation.BOTTOM
    Orientation.BOTTOM -> Orientation.LEFT
    Orientation.LEFT -> Orientation.TOP
    Orientation.TOP_RIGHT -> TODO()
    Orientation.BOTTOM_RIGHT -> TODO()
    Orientation.BOTTOM_LEFT -> TODO()
    Orientation.TOP_LEFT -> TODO()
}

internal sealed class Tile(open val row: Int, open val col: Int) {
    data class Obstruction(override val row: Int, override val col: Int): Tile(row, col)
    data class Free(override val row: Int, override val col: Int): Tile(row, col) {
        fun toStepped() = Stepped(row, col)
    }
    data class Stepped(override val row: Int, override val col: Int): Tile(row, col)
}

private fun List<Tile>.partitionTiles(): Triple<List<Tile.Obstruction>, List<Tile.Free>, List<Tile.Stepped>> {
    return fold(Triple<List<Tile.Obstruction>, List<Tile.Free>, List<Tile.Stepped>>(emptyList(), emptyList(), emptyList())) { acc, tile ->
        when (tile) {
            is Tile.Obstruction -> Triple(acc.first.plus(tile), acc.second, acc.third)
            is Tile.Free -> Triple(acc.first, acc.second.plus(tile), acc.third)
            is Tile.Stepped -> Triple(acc.first, acc.second, acc.third.plus(tile))
        }
    }
}

private fun List<Tile>.toString(dimensions: Pair<Int, Int>): String {
    val (height, width) = dimensions
    var matrix = IntRange(0, height * width).map { '_' }.toCharArray()
    forEach { tile ->
        val i = tile.row.times(width).plus(tile.col)
        matrix[i] = when(tile) {
            is Tile.Free -> '.'
            is Tile.Obstruction -> '#'
            is Tile.Stepped -> 'X'
        }
    }
    return matrix.toList().chunked(width).map {
        it.joinToString(separator = "" )
    }.joinToString(separator = "\n")
}

data class Guard(val row: Int, val col: Int, val orientation: Orientation) {
    companion object {
        fun newInstance(row: Int, col: Int, char: Char) = when (char) {
            '^' -> Orientation.TOP
            '>' -> Orientation.RIGHT
            'v' -> Orientation.BOTTOM
            '<' -> Orientation.LEFT
            else -> null
        }?.let { Guard(row, col, it) }
    }
    internal fun walkedAhead(tiles: List<Tile>): Pair<Guard?, List<Tile>>? {
        return when(orientation) {
            Orientation.TOP -> {
                val (obstructionsInPath, freeTilesInPath, steppedTilesInPath) = tiles
                    .filter { it.row in IntRange(0, row) && it.col == col }
                    .partitionTiles()
                val firstObstruction = obstructionsInPath.sortedBy { it.row.distanceTo(row) }.firstOrNull()
                val newGuard = Guard(firstObstruction?.row?.inc() ?: -1, col, orientation.rotatedClockwise())
                val newTiles = run {
                    val tileMap = tiles.associateBy { it.row to it.col }
                        .toMutableMap()
                    freeTilesInPath
                        .map { if(it.row >= newGuard.row) it.toStepped() else it }
                        .forEach { tileMap[it.row to it.col] = it }
                    tileMap.values.toList()
                }
                (newGuard.takeIf { firstObstruction != null } to newTiles)
            }
            Orientation.RIGHT -> {
                val (obstructionsInPath, freeTilesInPath, steppedTilesInPath) = tiles
                    .filter { it.col in IntRange(col, Int.MAX_VALUE) && it.row == row }
                    .partitionTiles()
                val firstObstruction = obstructionsInPath.sortedBy { it.col.distanceTo(col) }.firstOrNull()
                val newGuard = Guard(row, firstObstruction?.col?.dec() ?: Int.MAX_VALUE, orientation.rotatedClockwise())
                val newTiles = run {
                    val tileMap = tiles.associateBy { it.row to it.col }
                        .toMutableMap()
                    freeTilesInPath
                        .map { if(it.col <= newGuard.col) it.toStepped() else it }
                        .forEach { tileMap[it.row to it.col] = it }
                    tileMap.values.toList()
                }
                (newGuard.takeIf { firstObstruction != null } to newTiles)
            }
            Orientation.BOTTOM -> {
                val (obstructionsInPath, freeTilesInPath, steppedTilesInPath) = tiles
                    .filter { it.row in IntRange(row, Int.MAX_VALUE) && it.col == col }
                    .partitionTiles()
                val firstObstruction = obstructionsInPath.sortedBy { it.row.distanceTo(row) }.firstOrNull()
                val newGuard = Guard(firstObstruction?.row?.dec() ?: Int.MAX_VALUE, col, orientation.rotatedClockwise())
                val newTiles = run {
                    val tileMap = tiles.associateBy { it.row to it.col }
                        .toMutableMap()
                    freeTilesInPath
                        .map { if(it.row <= newGuard.row) it.toStepped() else it }
                        .forEach { tileMap[it.row to it.col] = it }
                    tileMap.values.toList()
                }
                (newGuard.takeIf { firstObstruction != null } to newTiles)
            }
            Orientation.LEFT -> {
                val (obstructionsInPath, freeTilesInPath, steppedTilesInPath) = tiles
                    .filter { it.col in IntRange(0, col) && it.row == row }
                    .partitionTiles()
                val firstObstruction = obstructionsInPath.sortedBy { it.col.distanceTo(col) }.firstOrNull()
                val newGuard = Guard(row, firstObstruction?.col?.inc() ?: -1, orientation.rotatedClockwise())
                val newTiles = run {
                    val tileMap = tiles.associateBy { it.row to it.col }
                        .toMutableMap()
                    freeTilesInPath
                        .map { if(it.col >= newGuard.col) it.toStepped() else it }
                        .forEach { tileMap[it.row to it.col] = it }
                    tileMap.values.toList()
                }
                (newGuard.takeIf { firstObstruction != null } to newTiles)
            }
            else -> {
                null
            }
        }
    }
}


fun main() {

    fun steppedTiles(input: List<String>): List<Tile> {
        val matrix = input.map(String::toList)
        val rowBounds = IntRange(0, matrix.size)
        val colBounds = IntRange(0, matrix.first().size)
        var (guards, tiles) = matrix
            .flatMapIndexed { row, line ->
                line.mapIndexedNotNull { col, char ->
                    when (char) {
                        '#' -> Tile.Obstruction(row, col)
                        '.' -> Tile.Free(row, col)
                        else ->Guard.newInstance(row, col, char)
                    }
                }
            }
            .partition { it is Guard } as Pair<List<Guard>, List<Tile>>
        assert(guards.size == 1)
        var guard: Guard = guards.first()
        tiles = tiles.plus(Tile.Stepped(guard.row, guard.col))
        val maxNumberOfSteps = rowBounds.last * colBounds.last
        for (i in 0..maxNumberOfSteps) {
            val (newGuard, newTiles) = guard.walkedAhead(tiles)!!
            tiles = newTiles
            if (newGuard == null) {
                break
            }
            guard = newGuard
        }
        return tiles
    }

    fun part1(input: List<String>): Int {
        return steppedTiles(input).count { it is Tile.Stepped }
    }

    fun part2(input: List<String>): Int {
        val matrix = input.map(String::toList)
        val height = matrix.size
        val width = matrix.first().size
        val rowBounds = IntRange(0, height)
        val colBounds = IntRange(0, width)
        var loopCount = 0
        val s = steppedTiles(input).filter { it is Tile.Stepped }
        for (newObstacle in steppedTiles(input)) {
            var (guards, tiles) = matrix
                .flatMapIndexed { row, line ->
                    line.mapIndexedNotNull { col, char ->
                        when (char) {
                            '#' -> Tile.Obstruction(row, col)
                            '.' -> Tile.Free(row, col)
                            else ->Guard.newInstance(row, col, char)
                        }
                    }
                }
                .partition { it is Guard } as Pair<List<Guard>, List<Tile>>
            assert(guards.size == 1)
            var guard: Guard = guards.first()
            tiles = tiles.plus(Tile.Stepped(guard.row, guard.col))
            if (!(newObstacle.row == guard.row && newObstacle.col == guard.col)) {
                tiles = tiles.map { if (it.row == newObstacle.row && it.col == newObstacle.col) Tile.Obstruction(it.row, it.col) else it }
            }
            val maxNumberOfSteps = rowBounds.last * colBounds.last
            var isLooping = true
            var turnsInPlace = 0
            for (i in 0..maxNumberOfSteps) {
                val (newGuard, newTiles) = guard.walkedAhead(tiles)!!
                if (newGuard == null) {
                    isLooping = false
                    break
                }
                if (guard.row == newGuard.row && guard.col == newGuard.col) {
                    turnsInPlace = turnsInPlace.inc()
                } else {
                    if (newTiles == tiles) {
                        isLooping = true
                        break
                    }
                }
                tiles = newTiles
                guard = newGuard
    //            tiles.toString(colBounds.last to rowBounds.last).println()
            }
            if (isLooping) {
//                tiles.toString(colBounds.last to rowBounds.last).println()
                loopCount = loopCount.inc()
            }
        }
        return loopCount
    }


    readInput("Day06_test").let {
        part1(it).println()
        part2(it).println()
    }

    readInput("Day06").let {
        part1(it).println()
        part2(it).println()
    }
}

