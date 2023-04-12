package com.suheng.structure;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import androidx.annotation.NonNull;

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
 */
@RunWith(Parameterized.class)
public class Parameterized2Test {

    private final int one, two;

    @NonNull
    @Parameterized.Parameters
    public static Iterable<Integer[]> parameters() {
        //return Arrays.asList(new Integer[]{7, 5}, new Integer[]{7, 4}, new Integer[]{7, 3}, new Integer[]{7, 2});
        return Arrays.asList(new Integer[][]{{7, 5}, {7, 4}, {7, 3}, {7, 2}});
    }

    public Parameterized2Test(int one, int two) {
        this.one = one;
        this.two = two;
    }

    @Before
    public void before() {
        System.out.println("-------------------Parameterized开始测试-------------------");
    }

    @After
    public void after() {
        System.out.println("-------------------Parameterized结束测试-------------------");
    }

    private int add(int one, int two) {
        return one + two;
    }

    @Test
    public void testAdd() {
        //Assert.assertEquals(10, add(one, two));
        Assert.assertThat(add(one, two), is(equalTo(10)));
        //Assert.assertThat(add(one, two), equalTo(10));
    }

}