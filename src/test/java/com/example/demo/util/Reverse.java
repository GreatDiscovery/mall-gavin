package com.example.demo.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 翻转数组算法
 */
public class Reverse {

    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19};
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            list.add(arr[i]);
        }

        Collections.reverse(list);
        list.stream().forEach(System.out::print);
    }

    public static void reverse(List<?> list) {
        int size = list.size();
        for (int i = 0, mid = size >> 1, j = size - 1; i < mid; i++, j--) {
            swap(list, i, j);
        }
    }

    public static void swap(List<?> list, int i, int j) {
        final List l = list;
        l.set(i, l.set(j, l.get(i)));
    }

}
