package com.google.ar.core.examples.java.augmentedimage;

import java.util.Random;

public interface random_number{
    Random r = new Random();
    public int random_number = r.nextInt(5);//おみくじの乱数
    void method_random();
}