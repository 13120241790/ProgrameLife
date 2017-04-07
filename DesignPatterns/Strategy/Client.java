package com.javaee.strategy;


public class Client {
    public static void main(String[] args) {
        //加法策略
        EnvironmentContext environmentContext = new EnvironmentContext(new AddtionStrategy());
        System.out.println(environmentContext.calculate(5,5));
        //减法策略
        environmentContext.setStrategy(new SubtractionStrategy());
        System.out.println(environmentContext.calculate(5,5));
        //乘法策略
        environmentContext.setStrategy(new MultiplicationStrategy());
        System.out.println(environmentContext.calculate(5,5));
        //除法策略
        environmentContext.setStrategy(new DivisionStrategy());
        System.out.println(environmentContext.calculate(5,5));
    }
}
