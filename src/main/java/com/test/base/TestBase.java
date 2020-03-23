package com.test.base;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;

import com.log.reporter.LogReporter;

import bsh.StringUtil;


public class TestBase {
	
	public static Properties prop;
	public static String pathProject;
	public TestBase(){
		try {
			prop = new Properties();
			FileInputStream ipd = new FileInputStream("src/main/java/com/qa/config/config.properties");
			prop.load(ipd);
			pathProject = System.getProperty("user.dir");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterMethod
	public synchronized void afterMethod(ITestResult result) {
		ArrayList<String> list = new ArrayList<String>();
		try {
			list.add(result.getInstanceName());
			list.add(result.getName());
			if(result.isSuccess()) {
				list.add("SUCCESS");
			}else {
				list.add("FAILED");
			}
			list.add(duration(result.getEndMillis()-result.getStartMillis()));
			list.add(LogReporter.hMap.get(Thread.currentThread().getId()));
			list.add(LogReporter.hMapVid.get(Thread.currentThread().getId()));
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			LogReporter.detailTests.add(list);
		}
	}
	
	public String duration(long milliSeconds) {
		long millis = milliSeconds;
	    String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
	            TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
	            TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	    return hms;
	}
	
	@AfterSuite
	public void prepareSummaryReport(ITestContext context) {
		try {			
			LogReporter.prepareSummaryReport(context);
		}catch (Exception e) {
			
		}
	}
}
