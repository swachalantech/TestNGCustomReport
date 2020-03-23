package com.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.log.reporter.LogReporter;
import com.test.base.TestBase;

public class LocalizationPage {

	WebDriver ldriver;
	LogReporter objLogReporter = null;
	public LocalizationPage(WebDriver rdriver,LogReporter objLogReporter)
	{
		ldriver=rdriver;
		PageFactory.initElements(rdriver, this);
		this.objLogReporter= objLogReporter;
	}
		
	private String xpathLang = "//a[text()='valLoc']";
	private String xpathVerify = "(//input[@value='valVerify'])[2]";
	
	public void clickLangs(String nameLang)
	{
		ldriver.findElement(By.xpath(xpathLang.replace("valLoc",nameLang))).click();
	}
	
	public boolean verifyLangs(String verifyLang)
	{
		if(ldriver.findElement(By.xpath(xpathVerify.replace("valVerify",verifyLang))).isDisplayed()) {
			return true;
		}else {
			return false;
		}
	}
}









