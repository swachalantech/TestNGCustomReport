package com.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class DownloadPage {

	WebDriver ldriver;

	@FindBy(xpath = "//a[contains(@href,'file-sample_100kB')]")
	WebElement linkDownload;

	public DownloadPage(WebDriver rdriver) {
		ldriver = rdriver;
		PageFactory.initElements(ldriver, this);
	}

	public DownloadPage clickDownloadLink(){
		linkDownload.click();
		return new DownloadPage(ldriver);
	}
}
