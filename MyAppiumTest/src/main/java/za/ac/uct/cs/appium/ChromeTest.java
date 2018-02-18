package za.ac.uct.cs.appium;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.remote.DesiredCapabilities;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;

public class ChromeTest {

    public static void main(String[] args) {

        //Set the Desired Capabilities
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("deviceName", "My Phone");
        caps.setCapability("udid", "0123456789ABCDEF"); //Give Device ID of your mobile phone
        caps.setCapability("platformName", "Android");
        caps.setCapability("platformVersion", "5.1");
        caps.setCapability("browserName", "Chrome");
        caps.setCapability("noReset", true);

        //Set ChromeDriver location
        System.setProperty("webdriver.chrome.driver","../../../../../../../../tools/chromedriver");

        //Instantiate Appium Driver
        AppiumDriver<MobileElement> driver = null;
        try {
            driver = new AndroidDriver<MobileElement>(new URL("http://0.0.0.0:4723/wd/hub"), caps);

        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        }

        //Open URL in Chrome Browser
        driver.get("https://www.google.co.uk/amp/amp.citizen.co.za/news/south-africa/1798767/anc-top-six-divided-over-exit-as-duarte-and-magashule-back-zuma/");
    }
}
