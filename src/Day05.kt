
data class Rule(val prior: Int, val latter: Int) {
    companion object {
        fun parse(s: String): Rule {
            return s.trim().split("|").let {
                assert(it.size == 2)
                Rule(it[0].toInt(), it[1].toInt())
            }
        }
    }
}

fun <T> List<T>.middleItem(): T {
    return this[size.div(2)]
}

fun main() {
    fun part1(input: List<String>): Int {
        val (ruleInput, updateInput) = input.slice(0..<input.indexOf("")) to input.slice(input.indexOf("").inc()..<input.size)
        val rules = ruleInput.map(Rule::parse)
        val updates = updateInput.map { it.split(",").map<String, Int>(String::toInt) }
        var validUpdates = mutableListOf<List<Int>>()

        for (update in updates) {
            val currentRules = rules.filter { update.containsAll(listOf(it.prior, it.latter)) }
            val updateWithIndexes = update.withIndex().associate { it.value to it.index }
            val validUpdate = currentRules.all { updateWithIndexes[it.prior]!! < updateWithIndexes[it.latter]!! }
            if (validUpdate) {
                validUpdates.add(update)
            }
        }

        return validUpdates.sumOf { it.middleItem() }
    }

    readInput("Day05_test").let {
        part1(it).println()
    }

    readInput("Day05").let {
        part1(it).println()
    }
}