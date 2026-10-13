package com.prayagi.netratrikaal

import java.time.LocalDate

/**
 * Traditional numerology arithmetic, on the phone only. Mulank and Bhagyank follow the Indian method (single digit 1 to 9, each tied
 * to a ruling planet). Life Path and Name number use the Pythagorean letter values and keep the master numbers 11, 22 and 33.
 * The text for each number is traditional, general information.
 */
object Numerology {
    fun reduce(n: Int, keepMaster: Boolean): Int {
        var x = n
        while (x > 9 && !(keepMaster && (x == 11 || x == 22 || x == 33))) x = digitSum(x)
        return x
    }
    fun digitSum(n: Int): Int { var x = Math.abs(n); var s = 0; while (x > 0) { s += x % 10; x /= 10 }; return s }

    /** Birth day reduced to 1..9. */
    fun mulank(d: LocalDate): Int = reduce(d.dayOfMonth, false)
    /** All digits of day, month and year added, reduced to 1..9. */
    fun bhagyank(d: LocalDate): Int = reduce(digitSum(d.dayOfMonth) + digitSum(d.monthValue) + digitSum(d.year), false)
    /** Month, day and year each reduced (masters kept), added, reduced again. */
    fun lifePath(d: LocalDate): Int = reduce(reduce(d.monthValue, true) + reduce(d.dayOfMonth, true) + reduce(d.year, true), true)

    /** Pythagorean value: A=1 ... I=9, J=1 ... R=9, S=1 ... Z=8. Only A to Z count. */
    fun letterValue(c: Char): Int { val u = c.uppercaseChar(); return if (u in 'A'..'Z') (u - 'A') % 9 + 1 else 0 }
    fun nameNumber(name: String): Int? {
        val s = name.sumOf { letterValue(it) }
        return if (s == 0) null else reduce(s, true)
    }

    val PLANET = mapOf(1 to "Sun", 2 to "Moon", 3 to "Jupiter", 4 to "Rahu", 5 to "Mercury", 6 to "Venus", 7 to "Ketu", 8 to "Saturn", 9 to "Mars")
    private val TRAITS = mapOf(
        1 to "leadership, independence and a wish to start things",
        2 to "sensitivity, cooperation and patience",
        3 to "expression, optimism and love of learning",
        4 to "discipline, hard work and a practical outlook",
        5 to "curiosity, adaptability and good communication",
        6 to "care for others, harmony and a sense of responsibility",
        7 to "reflection, analysis and an inner, spiritual bent",
        8 to "persistence, ambition and organisation",
        9 to "energy, courage and generosity")
    fun base(n: Int): Int = if (n > 9) digitSum(n).let { if (it > 9) digitSum(it) else it } else n
    fun meaning(n: Int): String {
        val b = base(n)
        val m = if (n > 9) " Master number $n: traditionally a stronger form of the number $b." else ""
        return "Traditionally linked with ${PLANET[b]}: ${TRAITS[b]}.$m"
    }
}
