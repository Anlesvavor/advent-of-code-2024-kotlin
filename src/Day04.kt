
typealias CharMatrix = List<List<Char>>

enum class Orientation {
    TOP, TOP_RIGHT, RIGHT, BOTTOM_RIGHT, BOTTOM, BOTTOM_LEFT, LEFT, TOP_LEFT
}

fun main() {
    fun searchStringInCoord(row: Int, col: Int, matrix: CharMatrix, string: String): Int {
        val len = string.length
        val rowsIndices = matrix.indices
        val colIndices = matrix.first().indices
        fun foundInOrientation(orientation: Orientation): String? {
            val rIndices = (0..<len).mapNotNull { dr ->
                when(orientation) {
                    Orientation.BOTTOM, Orientation.BOTTOM_RIGHT, Orientation.BOTTOM_LEFT -> row + dr
                    Orientation.TOP, Orientation.TOP_RIGHT, Orientation.TOP_LEFT -> row - dr
                    else -> row
                }.takeIf { it in rowsIndices }
            }
            val cIndices = (0..<len).mapNotNull { dc ->
                when(orientation) {
                    Orientation.RIGHT, Orientation.TOP_RIGHT, Orientation.BOTTOM_RIGHT -> col + dc
                    Orientation.LEFT, Orientation.TOP_LEFT, Orientation.BOTTOM_LEFT -> col - dc
                    else -> col
                }.takeIf { it in colIndices }
            }
            return rIndices.zip(cIndices).fold("") { acc, (r, c) -> acc.plus(matrix[r][c]) }
        }
        val result = Orientation.entries.toTypedArray().mapNotNull { foundInOrientation(it) }
        return result.count { it == string }
    }

    fun part1(input: List<String>): Int {
        val matrix = input.map(String::toList)
        return (matrix.indices).fold(0) { acc, r ->
            acc + (matrix.first().indices).fold(0) { accc, c ->
                accc + searchStringInCoord(r, c, matrix, "XMAS")
            }
        }
    }


//    fun part2(input: List<String>): Int {
        // Might resume this at a later date
//        fun searchMatrixInCoord(row: Int, col: Int, matrix: CharMatrix, otherCharMatrix: CharMatrix): Int {
//            val lenRow = otherCharMatrix.first().size
//            val lenCol = otherCharMatrix.size
//            val rowsIndices = matrix.indices
//            val colIndices = matrix.first().indices
//            matrix.subList(row, row.plus(lenRow))
//                .map { it.subList(col, col.plus(lenCol)) }
//            return 0
//        }
//        return 0
//    }

    readInput("Day04_test").let {
        part1(it).println()
    }

    readInput("Day04").let {
        part1(it).println()
    }

}
