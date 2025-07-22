package com.nutrisci;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

/**
 * Runs the complete TDD test suite covering TC01-TC12
 */
public class TDDTestRunner {
    
    public static void main(String[] args) {
        System.out.println("🧪 Running NutriSci TDD Test Suite (TC01-TC12)");
        System.out.println("=" .repeat(60));
        
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
            .selectors(selectClass(IntegrationTestSuite.class))
            .build();

        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        TestExecutionSummary summary = listener.getSummary();
        
        System.out.println("\n📊 TDD Test Results Summary:");
        System.out.println("Tests found: " + summary.getTestsFoundCount());
        System.out.println("Tests successful: " + summary.getTestsSucceededCount());
        System.out.println("Tests failed: " + summary.getTestsFailedCount());
        System.out.println("Tests skipped: " + summary.getTestsSkippedCount());
        
        if (summary.getTestsFailedCount() > 0) {
            System.out.println("\n❌ Failed tests:");
            summary.getFailures().forEach(failure -> {
                System.out.println("- " + failure.getTestIdentifier().getDisplayName());
                System.out.println("  Reason: " + failure.getException().getMessage());
            });
        } else {
            System.out.println("\n✅ All TDD test cases passed successfully!");
            System.out.println("TC01-TC12 requirements validated ✓");
        }
    }
}