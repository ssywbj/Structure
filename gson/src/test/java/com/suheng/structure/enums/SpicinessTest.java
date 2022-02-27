package com.suheng.structure.enums;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class SpicinessTest {

    @Before
    public void before() {
        System.out.println("-------------------开始测试-------------------");
    }

    @After
    public void after() {
        System.out.println("-------------------结束测试-------------------");
    }

    @Test
    public void testUseEnum() {
        Spiciness spiciness = Spiciness.HOT;
        System.out.println("spiciness: " + spiciness);
        for (Spiciness value : Spiciness.values()) {
            System.out.println("value: " + value + ", " + value.name() + ", " + value.ordinal() +
                    ", compareTo: " + value.compareTo(Spiciness.MEDIUM) + ", " + value.equals(Spiciness.MEDIUM)
                    + ", " + (value == Spiciness.MEDIUM) + ", getDeclaringClass: " + value.getDeclaringClass());
        }

        //for (String s : "NOT MEDIUM HIGH HOT".split(" ")) {
        for (String s : "NOT MEDIUM HOT".split(" ")) {
            Spiciness spn = Enum.valueOf(Spiciness.class, s);
            System.out.println("spn: " + spn);
        }


        for (OzWitch value : OzWitch.values()) {
            System.out.println("value: " + value + ", " + value.name() + ", " + value.ordinal() +
                    ", description: " + value.getDescription());
        }

        this.change(OzWitch.EAST);
        this.change(OzWitch.WEST);
    }

    private void change(OzWitch ozWitch) {
        switch (ozWitch) {
            case WEST:
                System.out.println("WEST, WEST, WEST");
                return;
            //break;
            case NORTH:
                System.out.println("NORTH, NORTH, NORTH");
                return;
            //break;
            case EAST:
                System.out.println("EAST, EAST, EAST");
                //break;
            /*case SOUTH:
                System.out.println("SOUTH, SOUTH, SOUTH");
                break;*/
        }
    }
}