package com.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage {

	WebDriver ldriver;

	@FindBy(xpath = "//h1[contains(text(),'Home')]")
	WebElement lblHomePage;

	public HomePage(WebDriver rdriver) {
		ldriver = rdriver;
		PageFactory.initElements(rdriver, this);
	}

	public boolean homePageDisplayed() {
		boolean blnFlg = false;
		try {
			blnFlg = lblHomePage.isDisplayed();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return blnFlg;
	}
}
