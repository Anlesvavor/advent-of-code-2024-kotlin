import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readText().trim().lines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun <T> Sequence<T>.headTail(): Pair<T?, Sequence<T>> {
    val iterator = iterator()
    val head = if (iterator.hasNext()) iterator.next() else null
    return head to iterator.asSequence()
}

fun <T> Iterable<T>.headTail(): Pair<T?, Iterable<T>> {
    return firstOrNull() to drop(1)
}