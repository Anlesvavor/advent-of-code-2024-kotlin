
typealias Row = Int
typealias Col = Int
typealias Coord = Pair<Row, Col>

internal data class HillTile(val value: Int, val coord: Coord) {

}

inline fun <reified T> Map<Pair<Int, Int>, T>.neighborsOf(coord: Pair<Int,Int>): Map<Pair<Int, Int>, T> {
    return listOf(Orientation.TOP, Orientation.RIGHT, Orientation.BOTTOM, Orientation.LEFT)
        .mapNotNull { neighborOf<T>(coord, it) }
        .associate { it }
}

inline fun <reified T> Map<Pair<Int, Int>, T>.listOfNeighborsOf(coord: Pair<Int,Int>): List<Pair<Pair<Int, Int>, T>> {
    return listOf(Orientation.TOP, Orientation.RIGHT, Orientation.BOTTOM, Orientation.LEFT)
        .mapNotNull { neighborOf<T>(coord, it) }
}

fun <T> Map<Pair<Int, Int>, T>.neighborOf(coord: Pair<Int,Int>, orientation: Orientation): Pair<Pair<Int, Int>, T>? {
    val newCoord = when (orientation)  {
        Orientation.TOP -> coord.first.dec() to coord.second
        Orientation.RIGHT -> coord.first to coord.second.inc()
        Orientation.BOTTOM -> coord.first.inc() to coord.second
        Orientation.LEFT -> coord.first to coord.second.dec()
        Orientation.TOP_RIGHT -> TODO()
        Orientation.BOTTOM_RIGHT -> TODO()
        Orientation.BOTTOM_LEFT -> TODO()
        Orientation.TOP_LEFT -> TODO()
    }
    val t = get(newCoord)
    return if (t != null) {
        (newCoord to t)
    } else null
}

fun main() {
    fun part1(input: List<String>): Int {
        val height = input.size
        val width = input.first().length
        val tiles = input
            .flatMapIndexed { r, row ->
                row.mapIndexed { c, a ->
                    HillTile(a.digitToInt(), r to c)
                }
            }
            .associateBy { it.coord }
        val roots = tiles.filter { it.value.value == 0 }
        fun walkUpTheHill(m: Map<Coord, HillTile>, root: HillTile): List<List<HillTile?>> {
            tailrec fun aux(acc: List<List<HillTile?>>): List<List<HillTile?>> {
                val (head, tail) = acc.firstOrNull() to acc.drop(1)
                val lastOfHead = head?.last()
                return if (head != null && lastOfHead != null) {
                    val candidates = m
                        .listOfNeighborsOf(lastOfHead.coord)
                        .map { it.second }
                        .filter { it.value == lastOfHead.value.inc() }
                        .takeIf { it.isNotEmpty() }
                    val nextAcc = if (candidates != null ) {
                        tail.plus(candidates.map { (head.plus(it)) as List<HillTile?> })
                    } else {
                        tail.plus(head.plus(null)) as List<List<HillTile?>>
                    }
                    aux(nextAcc) as List<List<HillTile?>>
                } else if (acc.all { it.last() == null }) {
                    acc
                } else {
                    aux(tail.plus(head) as List<List<HillTile?>>)
                }
            }
            return aux(listOf(listOf(root)))
        }
        return roots.values.map { walkUpTheHill(tiles, it).size }.sum()

        return 0
    }

    readInput("Day10_test").let {
        part1(it).println()
    }
}