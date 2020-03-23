package com.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.log.reporter.LogReporter;
import com.test.base.TestBase;

public class LoginPage {

	WebDriver ldriver;
	LogReporter objLogReporter = null;
	public LoginPage(WebDriver rdriver,LogReporter objLogReporter)
	{
		ldriver=rdriver;
		PageFactory.initElements(rdriver, this);
		this.objLogReporter= objLogReporter;
	}
		
	@FindBy(name="uid")
	//@CacheLookup
	WebElement txtUserName;
	
	@FindBy(name="password")
	//@CacheLookup
	WebElement txtPassword;
	
	@FindBy(name="btnLogin")
	//@CacheLookup
	WebElement btnLogin;
	
	
	@FindBy(xpath="/html/body/div[3]/div/ul/li[15]/a")
	//@CacheLookup
	WebElement lnkLogout;
	
	
	
	public void setUserName(String uname)
	{
		txtUserName.sendKeys(uname);
	}
	
	public void setPassword(String pwd)
	{
		txtPassword.sendKeys(pwd);
	}
	
	
	public void clickSubmit()
	{
		objLogReporter.logInfo("**I am from LOGINNNNNNNN**",true);
		btnLogin.click();
	}	
	
	public void clickLogout()
	{
		lnkLogout.click();
	}	
}









