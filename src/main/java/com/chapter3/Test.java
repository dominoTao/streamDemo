package com.chapter3;

import com.google.common.collect.Lists;
import org.junit.Assert;

import java.util.ArrayList;

public class Test {

    public static void main(String[] args) {

        ArrayList<String> value1 = Lists.newArrayList("a", "b", "c");
        ArrayList<String> value2 = Lists.newArrayList("a", "b", "c");


        Assert.assertEquals(value1, value2);

        Assert.assertArrayEquals(value1.toArray(), value2.toArray());
    }
}
