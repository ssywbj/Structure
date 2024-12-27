package com.suheng.structure.view

import com.suheng.structure.view.mock.Kid
import com.suheng.structure.view.mock.Mother
import com.suheng.structure.view.mock.Son
import com.suheng.structure.view.mock.SonUsage
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.verify
import io.mockk.verifyOrder
import io.mockk.verifySequence
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class KidTest {
    //@MockK
    //@RelaxedMockK
    @MockK(relaxUnitFun = true)
    lateinit var mother: Mother

    @InjectMockKs
    lateinit var kid: Kid

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun shouldReturn30WhenMotherGiveMoneyIs30() {
        //given
        every { mother.giveMoney() } returns 100
        //when
        kid.wantMoney()
        //then
        assertEquals(100, kid.money)
        verify { mother.doSomeThing() }
        verify { mother.giveMoney() }
        /*verify(exactly = 1) { mother.doSomeThing() }
        verify(exactly = 2) { mother.giveMoney() }*/
    }

    @Test
    fun testVerifySequence() {
        //given
        every { mother.giveMoney() } returns 100
        every { mother.inFrom(any()) } returns "test"
        //when
        mother.inFrom("123")
        kid.wantMoney()
        //then
        assertEquals(100, kid.money)
        verifySequence {
            mother.inFrom(any())
            mother.giveMoney()
            mother.doSomeThing()
        }
    }

    @Test
    fun testVerifyOrder() {
        //given
        every { mother.giveMoney() } returns 100
        every { mother.inFrom(any()) } returns "test"
        every { mother.getStr(any()) } returns "test"
        //when
        mother.inFrom("123")
        mother.getStr("456")
        kid.wantMoney()
        //then
        assertEquals(100, kid.money)
        verifyOrder {
            mother.inFrom(any())
            mother.giveMoney()
            //mother.getStr("456")
        }
    }

    @Test
    fun testObject() {
        mockkObject(Son)
        every { Son.test5() } returns 10
        assertEquals(10, Son.test5())
    }

    @Test
    fun testKtStaticMethod() {
        //given
        val sonUsage = SonUsage()
        mockkStatic(Son::class)
        every { Son.ok() } returns "mocked returns"
        //when
        sonUsage.useKotlinUtil()
        //then
        verify { Son.ok() }
    }

    //https://dev.to/davidibrahim/unit-testing-with-mockk-tweaks-and-tricks-part1-2f6i
    //https://mockk.io/
    //https://developer.android.com/training/testing/local-tests/robolectric?hl=zh-cn
    //https://developer.android.com/training/testing/instrumented-tests?hl=zh-cn
    //https://robolectric.org/getting-started/
    @Test
    fun testPrivate() {
        val spySon = spyk<Son>(recordPrivateCalls = true)
        every { spySon["privateResult"]() } returns 10
        assertEquals(10, spySon.publicResult())

        every { spySon["privateResult2"](any<Int>()) } returns 10
        assertEquals(10, spySon.publicResult2())

        verify {
            spySon["privateResult2"](any<Int>())
            spySon["privateResult"]()
        }

        verifyOrder {
            spySon["privateResult"]()
            spySon["privateResult2"](any<Int>())
        }

        verifySequence {
            spySon.publicResult()
            spySon["privateResult"]()
            spySon.publicResult2()
            spySon["privateResult2"](any<Int>())
        }
    }

    @Test
    fun testProperty() {
        val mockSon = spyk(Son, recordPrivateCalls = true)

        println("before answers:${mockSon.jj}")
        assertEquals(10, mockSon.jj)
        every { mockSon.jj } answers { fieldValue + 1 }
        println("after answers:${mockSon.jj}")
        assertEquals(11, mockSon.jj)
    }

    @Test
    fun testObject2() {
        mockkObject(Son) // applies mocking to an Object
        //assertEquals(3, Son.add(1, 2))
        every { Son.add(1, 2) } returns 55
        assertEquals(55, Son.add(1, 2))
    }
}