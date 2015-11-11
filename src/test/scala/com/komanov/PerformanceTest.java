package com.komanov;

public class PerformanceTest {

    public static void main(String[] args) throws Exception
    {

        System.out.println("Fastest Dmitry");
        UUIDParserPerformanceTest test = new UUIDParserPerformanceTest();
        test.testImplementations();



        System.out.println("Fastest Noam");
        UUIDParserPerformance2Test test2 = new UUIDParserPerformance2Test();
        test2.testImplementations();
    }


}
