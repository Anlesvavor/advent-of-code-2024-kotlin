
enum class TileType { BOX, WALL, NONE }

sealed class WarehouseEntity(open var row: Row, open var col: Col) {
    data class WarehouseTile(override var row: Row, override var col: Col, val type: TileType): WarehouseEntity(row, col)
    data class Robot(override var row: Row, override var col: Col): WarehouseEntity(row, col)

    val coord: Coord
        get() = row to col

    companion object {
        fun fromChar(row: Row, col: Col, char: Char): WarehouseEntity = when(char) {
            '.' -> WarehouseTile(row, col, TileType.NONE)
            '#' -> WarehouseTile(row, col, TileType.WALL)
            'O' -> WarehouseTile(row, col, TileType.BOX)
            '@' -> Robot(row, col)
            else -> throw IllegalStateException("Unsupported character")
        }

        fun parseWarehouse(input: List<String>): List<WarehouseEntity> {
            return input.flatMapIndexed { r, row ->
                row.mapIndexed { c, char -> fromChar(r, c, char)  }
            }
        }

    }


    fun advance(orientation: Orientation): WarehouseEntity = apply {
        when (orientation)  {
            Orientation.TOP -> row = row.dec()
            Orientation.RIGHT -> col = col.inc()
            Orientation.BOTTOM -> row = row.inc()
            Orientation.LEFT -> col = col.dec()
            else -> throw IllegalStateException("Not supported")
        }
    }
}

fun List<WarehouseEntity>.toString(height: Int, width: Int): String {
    var matrix = IntRange(0, height * width).map { ' ' }.toCharArray()
    forEach { tile ->
        val i = tile.coord.first.times(width).plus(tile.coord.second)
        matrix[i] = when(tile) {
            is WarehouseEntity.Robot -> '@'
            is WarehouseEntity.WarehouseTile -> when(tile.type) {
                TileType.BOX -> 'O'
                TileType.WALL -> '#'
                TileType.NONE -> '.'
            }
        }
    }
    return matrix.toList().chunked(width).map { it.joinToString(separator = "") }.joinToString(separator = "\n")
}

fun List<WarehouseEntity>.getRobot(): WarehouseEntity.Robot {
    return first { it is WarehouseEntity.Robot } as WarehouseEntity.Robot
}

fun Coord.manhattanDistanceTo(other: Coord): Int {
    return first.distanceTo(other.first) + second.distanceTo(other.second)
}

fun WarehouseEntity.manhattanDistanceTo(other: WarehouseEntity): Int {
    return coord.manhattanDistanceTo(other.coord)
}

fun List<WarehouseEntity>.pushInDirections(orientation: Orientation): List<WarehouseEntity> {
    val robot = getRobot()
    val (r, c) = robot.coord
    fun List<WarehouseEntity>.pushPushables(robot: WarehouseEntity.Robot, orientation: Orientation): List<WarehouseEntity> {
        check(isNotEmpty()) { "???" }
        val ordered = sortedBy { robot.manhattanDistanceTo(it) }
        val (index, firstNotBox) = (ordered
            .withIndex()
            .drop(1) // Drop the Robot
            .firstOrNull { (it.value as? WarehouseEntity.WarehouseTile)?.type != TileType.BOX }
            ?: return emptyList())
        val ret = when (firstNotBox) {
            is WarehouseEntity.Robot -> listOf()
            is WarehouseEntity.WarehouseTile -> when (firstNotBox.type) {
                TileType.WALL -> emptyList()
                TileType.BOX -> throw Error("Not possible: firstNotBox is NOT A BOX")
                TileType.NONE ->
                    ordered.slice(0..<index)
                        .map { it.advance(orientation) }
                        .let {
                            // Prepend a empty space, the one that the robot leaves behind as it advances
                            val newEmptySpace = WarehouseEntity.WarehouseTile(r, c, TileType.NONE)
                            listOf(newEmptySpace).plus(it)
                        }
            }
        }
        return ret
    }
    val tilesInDirection = when (orientation) {
        Orientation.TOP -> filter { it.row in robot.row.downTo(0) && it.col == robot.col }
        Orientation.RIGHT -> filter { it.row == robot.row && it.col in robot.col.rangeTo(Int.MAX_VALUE) }
        Orientation.BOTTOM -> filter { it.row in robot.row.rangeTo(Int.MAX_VALUE) && it.col == robot.col }
        Orientation.LEFT -> filter { it.row == robot.row && it.col in robot.col.downTo(0) }
        else -> throw IllegalStateException("Not supported")
    }
    val tilesToUpdate = tilesInDirection.pushPushables(robot, orientation).associateBy(WarehouseEntity::coord)
    return associateBy(WarehouseEntity::coord).plus(tilesToUpdate).values.toList()
}

fun parseMovement(char: Char): Orientation = when(char) {
    '^' -> Orientation.TOP
    '>' -> Orientation.RIGHT
    'v' -> Orientation.BOTTOM
    '<' -> Orientation.LEFT
    else -> throw IllegalStateException("Not supported")
}

fun main() {

    fun part1(input: List<String>): Int {
        val (warehouseInput, movementsInput) = input.slice(0..<input.indexOf("")) to input.slice(input.indexOf("").inc()..<input.size)
        var warehouse = WarehouseEntity.parseWarehouse(warehouseInput)
        val movements = movementsInput.joinToString(separator = "").map(::parseMovement)
        return movements
//            .fold(warehouse, List<WarehouseEntity>::pushInDirections)
            .fold(warehouse) { entities, orientation ->
                entities.pushInDirections(orientation)
                    .also { it.toString(warehouseInput.size, warehouseInput.first().length).println() }
            }
            .also { it.toString(warehouseInput.size, warehouseInput.first().length).println() }
            .filter { (it as? WarehouseEntity.WarehouseTile)?.type == TileType.BOX }
            .sumOf { it.row.times(100).plus(it.col) }
    }

    readInput("Day15_test").let {
        part1(it).println()
    }

    readInput("Day15").let {
        part1(it).println()
    }
}