
fun Int.isOdd(): Boolean = mod(2).equals(0)

fun main() {
    fun Int.idFromFoldIndex() = div(2)
    fun Int.isFileBlock() = isOdd()
    fun List<Int?>.lastBlockMoved(): List<Int?> {
        return dropLastWhile { it == null }.run {
            val (rest, lastNotNull) = dropLast(1).toTypedArray() to last()
            val indexOfFirstNull = rest.indexOfFirst { it == null }
            if (indexOfFirstNull == -1) { return this }
            rest[indexOfFirstNull] = lastNotNull
            rest.toList().dropLastWhile { it == null }
        }
    }

    fun part1(input: List<String>): Long {
        assert(input.size == 1)
        val diskMap = input[0]
        var disk: List<Int?> = diskMap.foldIndexed(emptyList<Int?>()) { i, acc, c ->
            if (i.isFileBlock()) {
                acc + (List(c.digitToInt()) { i.idFromFoldIndex() })
            } else {
                acc + (List(c.digitToInt()) { null })
            }
        }
        while (true) {
            disk = disk.lastBlockMoved()
            if (disk.all { it != null }) {
                return (disk as List<Long>).withIndex().sumOf { it.index * it.value }
            }
        }
        return 0
    }

    readInput("Day09_test").let {
        part1(it).println()
    }

    readInput("Day09").let {
        part1(it).println()
    }

//    listOf<Int?>(1,null,null,2,3,4,null,null,5,null,null).lastBlockMoved().println()
}