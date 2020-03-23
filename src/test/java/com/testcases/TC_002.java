package com.testcases;

import java.io.IOException;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.IInvokedMethod;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.log.reporter.LogReporter;
import com.pageobjects.LoginPage;
import com.test.base.LocalDriverManager;
import com.test.base.TestBase;
import com.utilities.XLUtils;

public class TC_002 extends TestBase{
	
	String dirProject = System.getProperty("user.dir");
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
	 
	
	//@Parameters({"","","recording","screenShotFlag"})
	@Test(dataProvider="LoginData")
	public void loginDDT(String user,String pwd) throws InterruptedException
	{
		driver = LocalDriverManager.getDriver();
		LogReporter objLogReporter = new LogReporter(dirProject,driver);
		
		try {
			objLogReporter.startRecording(recording);
			objLogReporter.logInfo("Test 2 starts",screenShotFlag);
    		driver.get("http://demo.guru99.com/v4/index.php");
			LoginPage lp=new LoginPage(driver,objLogReporter);
			lp.setUserName(user);
			objLogReporter.logInfo("Entered user name:->"+user,screenShotFlag);
			lp.setPassword(pwd);
			objLogReporter.logInfo("Entered password",screenShotFlag);
			lp.clickSubmit();	
			Thread.sleep(3000);
			
			try {
				driver.switchTo().alert().accept();;
				driver.switchTo().defaultContent();
				objLogReporter.logInfo("Clicked on submit",screenShotFlag);
				Assert.assertTrue(false);
			}catch (Exception e) {
				Assert.assertTrue(true);
				lp.clickLogout();
				Thread.sleep(3000);
				try {
					driver.switchTo().alert().accept();//close logout alert
					driver.switchTo().defaultContent();
					objLogReporter.logInfo("Clicked on submit",screenShotFlag);
				}catch (Exception ex) {
				}
			}
		} catch (Exception e) {
			objLogReporter.logError("Failed"+e.getMessage(), screenShotFlag);
		}finally {
			objLogReporter.stopRecording(recording);
		}
	}
	
	
	@DataProvider(name="LoginData")
	String [][] getData() throws IOException
	{
		String path=System.getProperty("user.dir")+"/src/test/java/com/testData/LoginData.xlsx";
		
		int rownum=XLUtils.getRowCount(path, "Sheet1");
		int colcount=XLUtils.getCellCount(path,"Sheet1",1);
		
		String logindata[][]=new String[rownum][colcount];
		
		for(int i=1;i<=rownum;i++)
		{
			for(int j=0;j<colcount;j++)
			{
				logindata[i-1][j]=XLUtils.getCellData(path,"Sheet1", i,j);//1 0
			}
				
		}
	return logindata;
	}
}
