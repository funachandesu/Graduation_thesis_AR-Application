package com.google.ar.core.examples.java.augmentedimage;

import java.util.Random;

//以下関数で、おみくじの結果やクーポンコードの元となる乱数を生成しています。
//他画面にも値を受け渡す必要があり、継承が使用できない画面があったためinterfaceで実装しました。
public interface random_number{
    Random r = new Random();
    public int random_number = r.nextInt(5);//おみくじの乱数
    void method_random();
}