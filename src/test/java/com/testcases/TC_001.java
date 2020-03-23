package com.testcases;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.test.base.LocalDriverManager;
import com.test.base.TestBase;
import com.utilities.ResponseDBConnection;
import com.log.reporter.LogReporter;
import com.pageobjects.*;

public class TC_001 extends TestBase{
	
	@Test
	@Parameters({"recording","screenShotFlag"})
    public void testMethod1(boolean recording,boolean screenShotFlag) {
		WebDriver driver = LocalDriverManager.getDriver();
		LogReporter objLogReporter = new LogReporter(TestBase.pathProject,driver);
		DownloadPage objDwnldPg = new DownloadPage(driver);
		try {	
			objLogReporter.startRecording(recording);
			objLogReporter.logInfo("Test 1 starts",screenShotFlag);
			driver.get(prop.getProperty("downloadUrl"));
			objLogReporter.logInfo("On Download Page",screenShotFlag);
			objDwnldPg.clickDownloadLink();
			objLogReporter.logInfo("Clicked on download",screenShotFlag);
			objLogReporter.attachFileToReport(prop.getProperty("systemDefaultDownloadPath"));
			objLogReporter.isEqual(true,true,"Test for Assert",screenShotFlag);
			
			ResponseDBConnection dbo = new ResponseDBConnection(objLogReporter);
			dbo.executeQueryAndGetResultSet("","select  * from t_channel limit 5",true);
		}catch (Exception e) {
			objLogReporter.logError("Failed"+e.getMessage(), screenShotFlag);
		}finally {
			objLogReporter.stopRecording(recording);
		}
    }
}
