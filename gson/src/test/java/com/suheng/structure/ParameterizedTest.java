package com.suheng.structure;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 * <p>
 * https://www.jianshu.com/p/2195b51d4a54
 * https://juejin.cn/post/7106485706985324574
 */
@RunWith(Parameterized.class)
public class ParameterizedTest {

    private final int actual2;
    private int actual;

    @Parameterized.Parameters
    public static Iterable<Integer> parameters() {
        return Arrays.asList(12, 11, 10, 9);
    }

    public ParameterizedTest(int actual) {
        this.actual2 = actual;
    }

    @Before
    public void before() {
        System.out.println("-------------------Parameterized开始测试-------------------");
        actual = 13;
    }

    @After
    public void after() {
        System.out.println("-------------------Parameterized结束测试-------------------");
    }

    @Test
    public void testParameterized() {
        Assert.assertEquals(10, actual2); //多个值连续测试
        Assert.assertEquals(10, actual); //单个值测试
    }
}