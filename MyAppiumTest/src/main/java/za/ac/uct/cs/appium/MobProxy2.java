package za.ac.uct.cs.appium;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;


import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.CaptureType;

public class MobProxy2 {


    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
setup();
	}



    public static void setup() {

       System.setProperty("webdriver.chrome.driver","/Users/amreesh/Documents/Dev/appium-test/java/MyAppiumTest/tools/chromedriver");
        // start the proxy
       WebDriver driver;
       BrowserMobProxyServer server;
        
        server = new BrowserMobProxyServer();
        server.start();

        // get the Selenium proxy object
        int port = server.getPort();
        //Proxy SeleniumProxy = ClientUtil.createSeleniumProxy(server);

        // configure it as a desired capability
	// DesiredCapabilities seleniumCapabilities = new DesiredCapabilities();
	// seleniumCapabilities.setCapability(CapabilityType.PROXY, SeleniumProxy);
	// seleniumCapabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

	//BasicConfigurator.configure();
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("network.proxy.type", 1);
        profile.setPreference("network.proxy.http", "localhost");
        profile.setPreference("network.proxy.http_port", port);
        profile.setPreference("network.proxy.ssl", "localhost");
        profile.setPreference("network.proxy.ssl_port", port);
        profile.setPreference("network.proxy.ftp", "localhost");
        profile.setPreference("network.proxy.ftp_port", port);
        profile.setPreference("network.proxy.share_proxy_settings", true);
        FirefoxOptions options = new FirefoxOptions();
       	options.setProfile(profile);
        driver = new FirefoxDriver(options);

        // start the browser up
        //driver = new FirefoxDriver();

        //Enable capture types for the Proxy
        HashSet<CaptureType> enable = new HashSet<CaptureType>();
        enable.add(CaptureType.REQUEST_HEADERS);
        enable.add(CaptureType.REQUEST_CONTENT);
        enable.add(CaptureType.RESPONSE_HEADERS);
        enable.add(CaptureType.RESPONSE_CONTENT);
        server.enableHarCaptureTypes(enable);

        // Write Port Number to the console
        System.out.println("Port started:" + port);
        
        
        // create a new HAR with the label "Rentalcars.com"
        server.newHar("Rentalcars.com");

        // Launch Browser
        driver.get("https://Rentalcars.com");

        //Wait for the page to load
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        //Set up har variable
        Har har = server.getHar();

        //Write Har to string
        java.io.StringWriter writer = new java.io.StringWriter();
        try {
            har.writeTo(writer);            
        } catch (IOException e) {
            e.printStackTrace();
        }

        String harAsString = writer.toString();
        System.out.println("1============" + harAsString);
        
        
    }


    public void MbProxy() throws InterruptedException {



    }	
	
}
