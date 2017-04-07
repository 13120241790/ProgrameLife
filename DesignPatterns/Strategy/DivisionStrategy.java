package com.javaee.strategy;


public class DivisionStrategy implements Strategy {
    @Override
    public int calculate(int a, int b) {
        return a / b;
    }
}
