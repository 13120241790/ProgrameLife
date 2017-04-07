package com.javaee.strategy;


public class SubtractionStrategy implements Strategy {
    @Override
    public int calculate(int a, int b) {
        return a - b;
    }
}
