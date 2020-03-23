package com.testcases;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.test.base.LocalDriverManager;
import com.test.base.TestBase;
import com.log.reporter.LogReporter;
import com.pageobjects.*;

public class TC_003 extends TestBase{
    //String dirProject = System.getProperty("user.dir");
	
    @Parameters({"recording","screenShotFlag"})
    @Test
    public void testMethod1(boolean recording,boolean screenShotFlag) {
		WebDriver driver = LocalDriverManager.getDriver();
		LogReporter objLogReporter = new LogReporter(TestBase.pathProject,driver);
		try {	
			objLogReporter.logInfo("Test 1 starts",screenShotFlag);
			HomePage hp=new HomePage(driver);
			objLogReporter.isEqual(hp.homePageDisplayed(),true,"Test for Assert",screenShotFlag);
		}catch (Exception e) {
			objLogReporter.logError("Failed",screenShotFlag);
		}
    }
}
