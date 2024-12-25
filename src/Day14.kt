

data class Drone(var coord: Coord, val vx: Int, val vy: Int) {
}

fun List<Drone>.toString(height: Int, width: Int): String {
    var matrix = IntRange(0, height * width).map { '.' }.toCharArray()
    val drones = groupBy { it.coord }
        .mapValues { it.value.count() }
    forEach { drone ->
        val i = drone.coord.first.times(width).plus(drone.coord.second)
        matrix[i] = drones.get(drone.coord)?.toString()?.first() ?: '.'
    }
    return matrix.toList().chunked(width).map { it.joinToString(separator = "") }.joinToString(separator = "\n")
}

fun main() {

    fun parseInput(input: List<String>): List<Drone> {
        fun parseLine(line: String): Drone {
            val numbers = Regex("-?[0-9]+")
                .findAll(line)
                .map { it.value.toInt() }
                .toList()
            assert(numbers.size == 4)
            return Drone(numbers[1] to numbers[0], numbers[2], numbers[3])
        }
        return input.map(::parseLine)
    }

    fun part1(input: List<String>, dimen: Pair<Int, Int> = 103 to 101, seconds: Int = 100): Int {
        var drones = parseInput(input)
        val height = dimen.first
        val width = dimen.second
        fun step(drones: List<Drone>): List<Drone> {
            return drones.map {
                it.apply {
                    coord = coord.first.plus(vy).mod(height) to coord.second.plus(vx).mod(width)
                }
            }
        }
        repeat(seconds) {
            drones = step(drones)
        }
        val dronesByQuadrant = drones.groupBy {
            when {
                it.coord.first in 0..<height.div(2) && it.coord.second in 0..<width.div(2) -> 2
                it.coord.first in 0..<height.div(2) && it.coord.second in width.div(2).inc()..<width -> 1
                it.coord.first in height.div(2).inc()..<height && it.coord.second in 0..<width.div(2) -> 3
                it.coord.first in height.div(2).inc()..<height && it.coord.second in width.div(2).inc()..<width -> 4
                else -> -1
            }
        }
        return  dronesByQuadrant
            .mapValues { it.value.count() }
            .filterKeys { it != -1 }
            .values
            .product()
    }

    fun part2(input: List<String>, dimen: Pair<Int, Int> = 103 to 101, seconds: Int = 100): Int {
        var drones = parseInput(input)
        val height = dimen.first
        val width = dimen.second
        fun step(drones: List<Drone>): List<Drone> {
            return drones.map {
                it.apply {
                    coord = coord.first.plus(vy).mod(height) to coord.second.plus(vx).mod(width)
                }
            }
        }
        fun canBuildTree(drones: List<Drone>): Boolean {
            return false
        }
        repeat(seconds) {
            drones = step(drones)
        }
        val dronesByQuadrant = drones.groupBy {
            when {
                it.coord.first in 0..<height.div(2) && it.coord.second in 0..<width.div(2) -> 2
                it.coord.first in 0..<height.div(2) && it.coord.second in width.div(2).inc()..<width -> 1
                it.coord.first in height.div(2).inc()..<height && it.coord.second in 0..<width.div(2) -> 3
                it.coord.first in height.div(2).inc()..<height && it.coord.second in width.div(2).inc()..<width -> 4
                else -> -1
            }
        }
        return  dronesByQuadrant
            .mapValues { it.value.count() }
            .filterKeys { it != -1 }
            .values
            .product()
    }

    readInput("Day14_test").let {
        part1(it, dimen = 7 to 11).println()
    }

    readInput("Day14").let {
        part1(it).println()
    }
}
