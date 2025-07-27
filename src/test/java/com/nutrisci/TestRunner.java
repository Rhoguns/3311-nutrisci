package com.nutrisci;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class TestRunner {
    public static void main(String[] args) {
        JUnitCore junit = new JUnitCore();
        Result core = junit.run(TestSuite.class);
        Result integration = junit.run(IntegrationTestSuite.class);
        
        System.out.println("Core: " + core.getRunCount() + " tests, " + core.getFailureCount() + " failed");
        System.out.println("Integration: " + integration.getRunCount() + " tests, " + integration.getFailureCount() + " failed");
    }
}
