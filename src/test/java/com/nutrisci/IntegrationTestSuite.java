package com.nutrisci;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.nutrisci.controller.ProfileControllerTest;
import com.nutrisci.controller.MealControllerTest;
import com.nutrisci.ui.CompareMealsPanelTest;
import com.nutrisci.visual.ChartGenerationTest;
import com.nutrisci.visual.CfgComplianceTest;

@Suite
@SelectClasses({
    ProfileControllerTest.class,
    MealControllerTest.class,
    CompareMealsPanelTest.class,
    ChartGenerationTest.class,
    CfgComplianceTest.class
})
class IntegrationTestSuite {
}