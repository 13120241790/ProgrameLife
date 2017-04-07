package com.javaee.strategy;


public class MultiplicationStrategy implements Strategy {
    @Override
    public int calculate(int a, int b) {
        return a * b;
    }
}
