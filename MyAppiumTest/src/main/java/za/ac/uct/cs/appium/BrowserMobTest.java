package za.ac.uct.cs.appium;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.BrowserMobProxy;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.CapabilityType;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.CaptureType;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.HarLog;
import net.lightbody.bmp.core.har.HarEntry;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;



public class BrowserMobTest {

    public static void main(String[] args) {

        // start the proxy
        BrowserMobProxy proxy = new BrowserMobProxyServer();
        proxy.start(8888);
        int port = proxy.getPort();


        // get the Selenium proxy object
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);

        // enable more detailed HAR capture, if desired (see CaptureType for the complete list)
        proxy.setHarCaptureTypes(CaptureType.getAllContentCaptureTypes());
        proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
        //proxy.enableHarCaptureTypes(CaptureType.REQUEST_HEADERS, CaptureType.REQUEST_CONTENT);
        //proxy.enableHarCaptureTypes(CaptureType.REQUEST_HEADERS, CaptureType.RESPONSE_CONTENT);



        // configure it as a desired capability
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("deviceName", "0123456789ABCDEF");
        capabilities.setCapability(CapabilityType.VERSION, "5.1");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("browserName", "Chrome");
        capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);
        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

        //Set ChromeDriver location
        System.setProperty("webdriver.chrome.driver","/Users/amreesh/Documents/Dev/appium-test/java/MyAppiumTest/tools/chromedriver");


        //Instantiate Appium Driver
        AppiumDriver<MobileElement> driver = null;
        try {
            driver = new AndroidDriver<MobileElement>(new URL("http://0.0.0.0:4723/wd/hub"), capabilities);

        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        }

        //Open URL in Chrome Browser
        driver.get("https://www.google.co.uk/amp/amp.citizen.co.za/news/south-africa/1798767/anc-top-six-divided-over-exit-as-duarte-and-magashule-back-zuma/");
        //driver.get("http://www.lemauricien.com");

        // create a new HAR with the label "yahoo.com"
        proxy.newHar("citizen.co.za.har");

        // get the HAR data
        Har har = proxy.getHar();

        HarLog log = har.getLog();
        File harFile = new File("/tmp/citizen.co.za.har");
        try {
            har.writeTo(harFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<HarEntry> entries = new CopyOnWriteArrayList<HarEntry>(log.getEntries());
        System.out.println(entries);
        for (HarEntry entry : entries) {
            System.out.println(entry.getRequest().getUrl());
        }

        proxy.stop();
        //driver.quit();

    }

}
