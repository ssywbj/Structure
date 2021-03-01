package com.suheng.structure.gson;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

public class IntegerTest {

    @Test
    public void equalTest() {
        Integer i = new Integer(10);
        Integer j = new Integer(10);
        Integer k = 10;
        Integer l = 10;

        //New生成的是两个不同的对象，两者内存地址不同
        Assert.assertFalse(i == j);
        //i是New生成的对象，k指向的是Java常量池中的对象，两者的内存地址不同
        Assert.assertFalse(i == k);
        //l和k指向的是Java常量池中的同一个对象，两者的内存地址不同
        Assert.assertTrue(l == k);

        int hour = 23, minute = 58;
        Assert.assertEquals("23:58", hour / 10 + "" + hour % 10 + ":" + minute / 10 + minute % 10);
    }

    @Test
    public void equalTest2() {
        // Integer变量和int变量进行比较时，只要两个变量的值相等，则结果就为True，(因为包装类Integer和基本数据类型比较的时候，
        // java会自动拆箱为int，然后进行比较，实际上就是两个int变量进行比较)
        int i = new Integer(10);
        Integer j = new Integer(10);
        Assert.assertTrue(i == j);
    }

    @Test
    public void equalTest3() {
        // Integer变量和int变量进行比较时，只要两个变量的值相等，则结果就为True，(因为包装类Integer和基本数据类型比较的时候，
        // java会自动拆箱为int，然后进行比较，实际上就是两个int变量进行比较)
        int i = new Integer(10);
        Integer j = new Integer(10);
        Assert.assertTrue(i == j);
    }

    @Test
    public void equalTest4() {
        // 两个非new出来的Integer对象，进行比较的时候，如果两个变量的值区间在-127~128之间的时候，则返回的结果为true，
        // 如果两个变量的变量值不在这个区间，则比较的结果为false。下面返回的是true
        Integer i = 127;
        Integer j = 127;
        Assert.assertTrue(i == j);
        Integer a = -127;
        Integer b = -127;
        Assert.assertTrue(a == b);
        Integer k = 128;
        Integer l = 128;
        Assert.assertFalse(k == l);

        List list;
        Set set;
        Map map;

        Vector vector;
        ArrayList arrayList;
        LinkedList linkedList;

        TreeSet treeSet;
        HashSet hashSet;

        TreeMap treeMap;
        HashMap hashMap;
    }
}