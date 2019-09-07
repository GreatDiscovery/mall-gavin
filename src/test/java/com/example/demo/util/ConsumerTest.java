package com.example.demo.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试map的consumer,PECS（Producer Extends Consumer Super）
 * @author gavin
 * @date 2019-09-04 23:05
 */
public class ConsumerTest {
    public static void main(String[] args) {
        Plate<? extends Fruit> plate = new Plate<Apple>(new Apple());
//        plate.set(new Fruit());
        plate.get();

        Plate<? super Fruit> plate1 = new Plate<>();
        plate1.set(new Apple());
        plate1.get();
    }
}

class Fruit {
    public void say() {
        System.out.println("i am fruit");
    }
}

class Apple extends Fruit {
    @Override
    public void say() {
        System.out.println("i am apple");
    }
}

class Plate<T> {
    private T item;
    Plate() {

    }
    Plate(T t) {
        item = t;
    }
    T get() {
        return item;
    }
    void set(T t) {
        item = t;
    }
}