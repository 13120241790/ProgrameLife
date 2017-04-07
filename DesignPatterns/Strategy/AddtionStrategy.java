package com.javaee.strategy;


public class AddtionStrategy implements Strategy {
    @Override
    public int calculate(int a, int b) {
        return a + b;
    }
}
