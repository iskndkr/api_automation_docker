package com.bookstore.listeners;

import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG listener for test execution events
 * Provides additional logging and reporting capabilities
 */
@Slf4j
public class TestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        log.info("===== Starting Test Suite: {} =====", context.getName());
        Allure.getLifecycle().updateTestCase(testResult -> {
            testResult.setName(context.getName());
        });
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("===== Finished Test Suite: {} =====", context.getName());
        log.info("Tests Passed: {}", context.getPassedTests().size());
        log.info("Tests Failed: {}", context.getFailedTests().size());
        log.info("Tests Skipped: {}", context.getSkippedTests().size());
    }

    @Override
    public void onTestStart(ITestResult result) {
        log.info(">>> Starting Test: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("<<< Test PASSED: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("<<< Test FAILED: {}", result.getMethod().getMethodName());
        log.error("Failure Reason: {}", result.getThrowable().getMessage());

        // Attach failure information to Allure report
        Allure.addAttachment("Failure Message", result.getThrowable().getMessage());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("<<< Test SKIPPED: {}", result.getMethod().getMethodName());
    }
}
