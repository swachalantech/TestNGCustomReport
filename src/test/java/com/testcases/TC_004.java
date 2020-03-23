package com.testcases;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.test.base.LocalDriverManager;
import com.test.base.TestBase;
import com.utilities.ResponseDBConnection;
import com.utilities.XLUtils;
import com.log.reporter.LogReporter;
import com.pageobjects.*;

public class TC_004 extends TestBase {

	WebDriver driver = null;
	boolean recording = false;
	boolean screenShotFlag = true;

	@BeforeClass
	public void beforeInvocation(ITestContext context) {

		String xmlRecording = context.getSuite().getXmlSuite().getAllParameters().get("recording");
		String xmlScreenShotFlag = context.getSuite().getXmlSuite().getAllParameters().get("screenShotFlag");
		recording = Boolean.parseBoolean(xmlRecording);
		screenShotFlag = Boolean.parseBoolean(xmlScreenShotFlag);
	}

	@Test(dataProvider = "localizationData")
	public void verifyLangs(String nameLang,String verify) throws InterruptedException {
		driver = LocalDriverManager.getDriver();
		LogReporter objLogReporter = new LogReporter(TestBase.pathProject, driver);

		try {
			objLogReporter.startRecording(recording);
			objLogReporter.logInfo("Test 2 starts",screenShotFlag);
			LocalizationPage lp=new LocalizationPage(driver,objLogReporter);
			driver.get(prop.getProperty("localizationURL"));
			
			lp.clickLangs(nameLang);
			objLogReporter.isEqual(true,lp.verifyLangs(verify),"Successfully verified :-> "+verify+" ,for language:-> "+nameLang, screenShotFlag);
			
			
		} catch (Exception e) {
			objLogReporter.logError("Failed" + e.getMessage(), screenShotFlag);
		} finally {
			objLogReporter.stopRecording(recording);
		}
	}

	@DataProvider(name = "localizationData")
	String[][] getData() throws IOException {
		String path = System.getProperty("user.dir") + "/src/test/java/com/testData/TestData.xlsx";

		int rownum = XLUtils.getRowCount(path, "TC_004");
		int colcount = XLUtils.getCellCount(path, "TC_004", 1);

		String localizationData[][] = new String[rownum][colcount];

		for (int i = 1; i <= rownum; i++) {
			for (int j = 0; j < colcount; j++) {
				localizationData[i - 1][j] = XLUtils.getCellData(path, "TC_004", i, j);
			}
		}
		return localizationData;
	}
}
