package com.test.base;
 
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
 
class LocalDriverFactory {
    static WebDriver createInstance(String browserName) {
        WebDriver driver = null;
        if (browserName.toLowerCase().contains("firefox")) {
            driver = new FirefoxDriver();
        }
        if (browserName.toLowerCase().contains("internet")) {
            driver = new InternetExplorerDriver();
        }
        if (browserName.toLowerCase().contains("chrome")) {
        	System.setProperty("webdriver.chrome.driver","C:\\Users\\pta7\\Desktop\\HKRRNK\\inetbankingV1-master\\Drivers\\chromedriver.exe");
            driver = new ChromeDriver();
            driver.get("https://www.google.com/");
        }
        return driver;
    }
}