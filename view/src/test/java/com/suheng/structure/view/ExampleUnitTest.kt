package com.suheng.structure.view

import com.suheng.structure.view.mock.Kid
import com.suheng.structure.view.mock.Mother
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testGiveMoney() {
        val mother = mockk<Mother>()
        every { mother.giveMoney() } returns 30
    }

    @Test
    fun shouldReturn30WhenMotherGiveMoneyIs30() {
        //given
        val mother = mockk<Mother>(relaxed = true)
        every { mother.giveMoney() } returns 100
        val kid = Kid(mother)
        //when
        kid.wantMoney()
        //then
        assertEquals(100, kid.money)
    }

}