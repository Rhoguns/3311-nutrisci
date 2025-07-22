package com.nutrisci;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;

/**
 * Simple test runner for the complete TDD test suite
 */
public class TestRunner {
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("🧪 NUTRISCI TEST SUITE EXECUTION");
        System.out.println("=".repeat(60));
        
        // Create launcher
        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        
        // Build discovery request for our test suite
        LauncherDiscoveryRequest request = request()
            .selectors(selectClass(TestSuite.class))
            .build();
        
        // Execute tests
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);
        
        // Print summary
        TestExecutionSummary summary = listener.getSummary();
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📊 TEST EXECUTION SUMMARY");
        System.out.println("=".repeat(60));
        System.out.printf("Tests found: %d%n", summary.getTestsFoundCount());
        System.out.printf("Tests successful: %d%n", summary.getTestsSucceededCount());
        System.out.printf("Tests failed: %d%n", summary.getTestsFailedCount());
        System.out.printf("Tests skipped: %d%n", summary.getTestsSkippedCount());
        
        if (summary.getTestsFailedCount() > 0) {
            System.out.println("\n❌ FAILED TESTS:");
            summary.getFailures().forEach(failure -> 
                System.out.println("  - " + failure.getTestIdentifier().getDisplayName() + 
                                 ": " + failure.getException().getMessage()));
        }
        
        System.out.println("=".repeat(60));
        System.exit(summary.getTestsFailedCount() > 0 ? 1 : 0);
    }
}