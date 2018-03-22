package za.ac.uct.cs.appium;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.BrowserMobProxy;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.CapabilityType;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.CaptureType;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.HarLog;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.io.FileReader;
import java.util.concurrent.TimeUnit;


public class BrowserMobTest {

    private BrowserMobProxy proxy;
    private DesiredCapabilities capabilities;
    public static String [] FILE_HEADER_MAPPING = {"site","carousel","original-amp","mobile-site","google-search"};

    public BrowserMobTest(BrowserMobProxy proxy, DesiredCapabilities capabilities) {

        this.proxy = proxy;
        this.capabilities = capabilities;

    }

    public AppiumDriver<MobileElement> initialize() throws MalformedURLException{

        proxy.setTrustAllServers(true);

        proxy.start(8888);
        int port = proxy.getPort();

        // get the Selenium proxy object
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);

        // enable more detailed HAR capture, if desired (see CaptureType for the complete list)
        proxy.setHarCaptureTypes(CaptureType.getAllContentCaptureTypes());
        proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
        //proxy.enableHarCaptureTypes(CaptureType.REQUEST_HEADERS, CaptureType.REQUEST_CONTENT);
        //proxy.enableHarCaptureTypes(CaptureType.REQUEST_HEADERS, CaptureType.RESPONSE_CONTENT);


        ChromeOptions options = new ChromeOptions();
        options.addArguments("incognito");
        options.addArguments("disable-cache");


        // configure it as a desired capability
        capabilities.setCapability("deviceName", "20170101134312");
        capabilities.setCapability(CapabilityType.VERSION, "6.0");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("browserName", "Chrome");
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);
        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

        //Set ChromeDriver location
        System.setProperty("webdriver.chrome.driver","tools/chromedriver");

        //Instantiate Appium Driver
        AppiumDriver<MobileElement> driver = new AndroidDriver<MobileElement>(new URL("http://0.0.0.0:4723/wd/hub"), capabilities);

        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);

        return driver;

    }

    public static void main(String[] args) {


        BrowserMobProxy proxy = new BrowserMobProxyServer();
        DesiredCapabilities capabilities = new DesiredCapabilities();

        BrowserMobTest test = new BrowserMobTest(proxy, capabilities);

        List<CSVRecord> csvRecords;
        AppiumDriver<MobileElement> driver = null;

        // start the proxy
        try {
            driver = test.initialize();
        }catch(MalformedURLException e) {
            System.out.println(e.getMessage());
        }

        String siteName;
        String carouselUrl;
        String originalAmpUrl;
        String mobileSiteUrl;
        String googleSearchUrl;
        String harPath;

        try {
            csvRecords = test.loadCSV("data/amp-pages.csv");

            //Read the CSV file records starting from the second record to skip the header
            for (int i = 1; i < csvRecords.size(); i++) {
                CSVRecord record = csvRecords.get(i);

                siteName = record.get("site");
                carouselUrl = record.get("carousel");
                originalAmpUrl = record.get("original-amp");
                mobileSiteUrl = record.get("mobile-site");
                googleSearchUrl = record.get("google-search");

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss") ;

                //for the carousel
                harPath = "data/" + siteName + "/carousel/" + dateFormat.format(new Date()) + ".har";
                test.generateHar(driver, carouselUrl, siteName, harPath, false );

                //for the original-amp
                harPath = "data/" + siteName + "/original-amp/" + dateFormat.format(new Date()) + ".har";
                test.generateHar(driver, originalAmpUrl, siteName, harPath, false );

                //for the mobile-site
                harPath = "data/" + siteName + "/mobile-site/" + dateFormat.format(new Date()) + ".har";
                test.generateHar(driver, mobileSiteUrl, siteName, harPath, false );

                //for google search
                harPath = "data/" + siteName + "/google-search/" + dateFormat.format(new Date()) + ".har";
                test.generateHar(driver, googleSearchUrl, siteName, harPath, true );


            }

       }catch (IOException e) {
            System.out.println("CSV Read Error");
        }

        test.getProxy().stop();
        //driver.quit();

    }

    public void generateHar(AppiumDriver<MobileElement> driver, String url, String harName,
                            String harPath, boolean isGoogleSearch) {

        this.getProxy().newHar(harName);
        Har har = this.getProxy().getHar();

/*
        int count = 0;
        int maxTries = 3;
        boolean success = false;


        while(true) {
            try {
                // Some Code
                // break out of loop, or return, on success
                driver.get(url);
                success = true;
                break;

            } catch (TimeoutException e) {
                // handle exception
                if (++count == maxTries) {
                    System.out.println("Timeout for " + url);
                }
            } catch (NoSuchSessionException e) {
                if (++count == maxTries) {
                    System.out.println("No such session for " + url);
                }
            }
        }

        if (!success)
            return;

        */

        try {
            driver.get(url);
        }catch (TimeoutException e) {
            // handle exception{
            System.out.println("Timeout for " + url);
        } catch (NoSuchSessionException e) {
            System.out.println("No such session for " + url);
            //driver.navigate().refresh();
        } catch (WebDriverException e) {
            System.out.println("Page crashed for " + url);
            //driver.navigate().refresh();
        }

        //Open URL in Chrome Browser
        if (isGoogleSearch) {

            List<MobileElement> links = driver.findElements(By.tagName("a"));

            for (MobileElement m : links)
            {

                if (m != null && m.getAttribute("data-amp") != null) {
                    System.out.println(m.getAttribute("data-amp"));
                    m.click();
                    break;
                }
            }


        }

        //System.out.println("Entries count :"+  har.getLog().getEntries().size());
        HarLog log = har.getLog();
        File harFile = new File(harPath);
        harFile.getParentFile().mkdirs();

        try {
            harFile.createNewFile();
            har.writeTo(harFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        List<HarEntry> entries = new CopyOnWriteArrayList<HarEntry>(log.getEntries());
        System.out.println(entries);
        for (HarEntry entry : entries) {
            System.out.println(entry.getRequest().getUrl());
        }*/
    }

    public BrowserMobProxy getProxy() {
        return proxy;
    }


    public DesiredCapabilities getCapabilities() {
        return capabilities;
    }

    public List<CSVRecord> loadCSV(String fileName) throws IOException {

        FileReader fileReader = null;

        CSVParser csvFileParser = null;

        //Create the CSVFormat object with the header mapping
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADER_MAPPING);

        List<CSVRecord> csvRecords = null;

        try {

            //initialize FileReader object
            fileReader = new FileReader(fileName);

            //initialize CSVParser object
            csvFileParser = new CSVParser(fileReader, csvFileFormat);

            //Get a list of CSV file records
            csvRecords = csvFileParser.getRecords();

        }
        catch (Exception e) {
            System.out.println("Error in CsvFileReader !!!");
            e.printStackTrace();
        } finally {
            try {
                fileReader.close();
                csvFileParser.close();
            } catch (IOException e) {
                System.out.println("Error while closing fileReader/csvFileParser !!!");
                e.printStackTrace();
            }
        }

        return csvRecords;

    }

}
