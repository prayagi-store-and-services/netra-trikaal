package com.prayagi.netratrikaal

import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Test

class NumerologyTest {
    private val d = LocalDate.of(1990, 7, 15)
    @Test fun mulank() { assertEquals(6, Numerology.mulank(d)); assertEquals(2, Numerology.mulank(LocalDate.of(2000, 1, 29))); assertEquals(9, Numerology.mulank(LocalDate.of(2000, 1, 9))) }
    @Test fun bhagyank() = assertEquals(5, Numerology.bhagyank(d))
    @Test fun lifePath() = assertEquals(5, Numerology.lifePath(d))
    @Test fun lifePathKeepsMasterNumber() = assertEquals(11, Numerology.lifePath(LocalDate.of(1990, 1, 9)))
    @Test fun nameNumber() { assertEquals(6, Numerology.nameNumber("abc")); assertEquals(6, Numerology.nameNumber("A B-C")); assertNull(Numerology.nameNumber("123 ")) }
    @Test fun letterValues() { assertEquals(1, Numerology.letterValue('a')); assertEquals(9, Numerology.letterValue('I')); assertEquals(1, Numerology.letterValue('J')); assertEquals(8, Numerology.letterValue('Z')); assertEquals(0, Numerology.letterValue('-')) }
    @Test fun reduceKeepsOrDropsMasters() { assertEquals(11, Numerology.reduce(29, true)); assertEquals(2, Numerology.reduce(29, false)); assertEquals(22, Numerology.reduce(22, true)) }
    @Test fun everyNumberHasAMeaning() { (1..9).forEach { assertTrue(Numerology.meaning(it).contains(Numerology.PLANET[it]!!)) }; assertTrue(Numerology.meaning(22).contains("Master number 22")) }
}
