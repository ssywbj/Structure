package com.suheng.structure.gson;

import com.suheng.structure.gson.bean.User2;
import com.suheng.structure.gson.bean.User3;
import com.suheng.structure.gson.bean.User4;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public class ObjectTest {

    //###:start----------------------------HashCode、IdentityHashCode------------------------------
    /**
     * Object的hashCode()默认返回的是对象的内存地址，但是hashCode()能被重写以返回其它结果值，所以hashCode()的返回值不能代表内存地址；
     * System.identityHashCode(Object)返回的始终是对象的内存地址且不能被重写，也不用管hashCode()是否被重写。
     */
    @Test
    public void testHashCode() {
        User2 user = new User2("11", "Wbj", 30);
        /*点击hashCode()查看源码，可发现它默认返回的是对象的内存地址，因为它实际上调用的是Object的identityHashCode(Object)，
        再点击System.identityHashCode(Object)查看源码，可发现它实际上也调用的是Object的identityHashCode(Object)，
        所以在不重写hashCode()并改变它返回逻辑的情况下，两个方法的返回值是一样的。
         */
        int hashCode = user.hashCode();
        int identityHashCode = System.identityHashCode(user);
        assertEquals(hashCode, identityHashCode);

        System.out.println("user one hash code: " + hashCode + ", memory address: " + identityHashCode);
    }

    @Test
    public void testIdentityHashCode() {
        User3 user1 = new User3("11", "Wbj", 30);
        User3 user2 = new User3("11", "Wbj", 30);
        //点击hashCode()查看，可发现它的返回逻辑被重写了。
        int hashCode1 = user1.hashCode();
        int hashCode2 = user2.hashCode();
        assertEquals(hashCode1, hashCode2);
        assertNotEquals(user1, user2);//虽然两个对象的HashCode一样，但它们不一定相等

        //因为每new一次对象就会新开辟一个堆内存空间，所以即使两个对象的hash code一样，它们的堆内存地址还是不一样的。
        int identityHashCode1 = System.identityHashCode(user1);
        int identityHashCode2 = System.identityHashCode(user2);
        assertNotEquals(identityHashCode1, identityHashCode2);

        System.out.println("user3 one hash code: " + hashCode1 + ", memory address: " + identityHashCode1);
        System.out.println("user3 two hash code: " + hashCode2 + ", memory address: " + identityHashCode2);
    }
    //###:end----------------------------HashCode、IdentityHashCode--------------------------------

    //###:start----------------------------==、equals()------------------------------

    /**
     * "=="的作用是判断对象的地址是否相等，即判断两个对象是否为同一个对象(基本数据类型比较的是值，引用数据类型比较的是内存地址)；
     * "equals()"的默认作用也是判断对象的地址是否相等，但一般都把它重写为用来判断两个对象的内容是否相等。
     */
    @Test
    public void testEqualsSign() {
        User3 user1 = new User3("11", "Wbj", 30);
        User3 user2 = new User3("11", "Wbj", 30);
        assertNotSame(user1, user2);//断言两个对象是否为不同的对象，等价"assertNotEquals(true, user1 == user2)"
        //assertNotEquals(true, user1 == user2);
        /*点击equals(Object)查看源码，可发现equals(Object)也是用"=="来判断两个对象是否为同一个对象，
        所以在不重写equals(Object)并改变其返回逻辑的情况下，它的功能和"=="是一样的。
        */
        assertNotEquals(true, user1.equals(user2));//等价"assertNotEquals(user1, user2)"
        //assertNotEquals(user1, user2);
    }

    @Test
    public void testEqualsMethod() {
        User4 user1 = new User4("11", "Wbj", 30);
        User4 user2 = new User4("11", "Wbj", 30);
        //点击equals(Object)查看，可发现它被重写成比较两个对象的内容是否一样了。
        assertEquals(user1, user2);//等价"assertEquals(true, user1.equals(user2))"
        //assertEquals(true, user1.equals(user2));
        //因为每new一次对象就会新开辟一个堆内存空间，所以即使两个对象的内容一样，它们的堆内存地址还是不一样的，因此它们本质上不是同一个对象。
        assertNotSame(user1, user2);

        User4 user3 = user2;
        assertSame(user3, user2);//只有两个对象的堆内存地址一样，它们才是同一个对象。
        user3.setName("ABCD");
        assertSame(user3, user2);//因此虽然是user3修改了属性的值，但等同于用user2来执行这些操作，因为堆内存一样，他们始终是同一个对象。

        System.out.println("user1 hash code: " + user1.hashCode() + ", memory address: " + System.identityHashCode(user1));
        System.out.println("user2 hash code: " + user2.hashCode() + ", memory address: " + System.identityHashCode(user2));
        System.out.println("user3 hash code: " + user3.hashCode() + ", memory address: " + System.identityHashCode(user3));
        System.out.println("user2 : " + user2);
    }
    //###:end----------------------------==、equals()------------------------------

    //###:start----------------------------toString()------------------------------
    @Test
    public void testToString() {
        User2 user1 = new User2("11", "Wbj", 30);
        //点击toString()查看源码，可发现它默认返回的是对象的基本信息，格式：完整类名+@+hashCode(十六进制)
        assertEquals(user1.getClass().getName() + "@" + Integer.toHexString(user1.hashCode()), user1.toString());
        //Integer.toHexString(int)：把传入的整数以十六进制展示，返回的是字符串
        System.out.println(user1.toString());
        System.out.println(user1);//就算直接打印对象，对象也会默认调用toString()

        User3 user2 = new User3("11", "Wbj", 30);
        //点击toString()查看源码，可发现它默认返回的是对象的基本信息，格式：完整类名+@+hashCode(十六进制)
        assertNotEquals(user2.getClass().getName() + "@" + Integer.toHexString(user2.hashCode()), user1.toString());
        System.out.println(user2);//就算直接打印对象，对象也会默认调用toString()
    }
    //###:end----------------------------toString()------------------------------

    //###:start-----------------------HashCode与List、HashSet、HashMap等类----------------------------
    //https://blog.csdn.net/qq_33619378/article/details/92661494
    //https://blog.csdn.net/lijiecao0226/article/details/24609559
    //https://www.cnblogs.com/whgk/p/6071617.html
    @Test
    public void testHashCodeUser3() {
        User3 user1 = new User3("11", "Wbj", 30);
        User3 user2 = new User3("11", "Wbj", 30);
        User3 user3 = new User3("22", "Wbj", 30);
        User3 user4 = new User3("22", "Wbj", 30);
        User3 user5 = new User3("33", "Wbj", 30);

        List<User3> user3List = new ArrayList<>();
        user3List.add(user1);
        user3List.add(user2);
        user3List.add(user3);
        user3List.add(user4);
        user3List.add(user5);
        assertEquals(5, user3List.size());

        Set<User3> user3Set = new HashSet<>();
        user3Set.add(user1);
        user3Set.add(user2);
        user3Set.add(user3);
        user3Set.add(user4);
        user3Set.add(user5);
        assertEquals(5, user3Set.size());
        for (User3 user : user3Set) {
            System.out.println("set User3: " + user);
        }

        System.out.println("-------------------------------------------------------------");

        Map<User3, String> user3Map = new HashMap<>();
        user3Map.put(user1, "1111111");
        user3Map.put(user2, "2222222");
        user3Map.put(user4, "4444444");
        user3Map.put(user3, "3333333");
        user3Map.put(user5, "5555555");
        assertEquals(5, user3Map.size());
        for (Map.Entry<User3, String> user3StringEntry : user3Map.entrySet()) {
            System.out.println("Map User3: " + user3StringEntry.getKey() + ", value: " + user3StringEntry.getValue());
        }
    }

    @Test
    public void testHashCodeUser4() {
        User4 user1 = new User4("11", "Wbj", 30);
        User4 user2 = new User4("11", "Wbj", 30);
        User4 user3 = new User4("22", "Wbj", 30);
        User4 user4 = new User4("22", "Wbj", 30);
        User4 user5 = new User4("33", "Wbj", 30);

        List<User4> user4List = new ArrayList<>();
        user4List.add(user1);
        user4List.add(user2);
        user4List.add(user3);
        user4List.add(user4);
        user4List.add(user5);
        assertEquals(5, user4List.size());

        Set<User4> user4Set = new HashSet<>();
        user4Set.add(user1);
        user4Set.add(user2);
        user4Set.add(user3);
        user4Set.add(user4);
        user4Set.add(user5);
        assertEquals(3, user4Set.size());
        for (User4 user : user4Set) {
            System.out.println("set User3: " + user);
        }

        System.out.println("-------------------------------------------------------------");

        Map<User4, String> user4Map = new HashMap<>();
        user4Map.put(user1, "1111111");
        user4Map.put(user2, "2222222");
        user4Map.put(user4, "4444444");
        user4Map.put(user3, "3333333");
        user4Map.put(user5, "5555555");
        assertEquals(3, user4Map.size());
        for (Map.Entry<User4, String> user3StringEntry : user4Map.entrySet()) {
            System.out.println("Map User3: " + user3StringEntry.getKey() + ", value: " + user3StringEntry.getValue());
        }
    }
    //###:end-------------------------HashCode、List、HashSet、HashMap等类----------------------------
}