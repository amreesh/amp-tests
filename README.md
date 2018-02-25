# amp-tests

## What are we trying to achieve?
We want to be able to automate mobile web tests and generate HAR file from a mobile phone Chrome browser.
The HAR file contains lot of information about the network performance. To be able to capture the HAR file
we need three components: 1/ Selenium for test automation 2/Appium for mobile web test automation 3/BrowserMob proxy
which is a proxy server that intercepts the connection between your device and the Appium server (running on your test machine).

## Requirements
- Appium
- BrowserMob Proxy (can be run as standalone or embedded, for my case it's included in the pom.xml)
- the rest is in the pom.xml

## Important
For the BrowserMob proxy to be able to intercept and decrypt requests from your device (mobile phone), we need to install the BrowserMob certificate ca-certificate-rsa.cer file in your browser or HTTP client.

Certificate can be downloaded from here:
https://github.com/lightbody/browsermob-proxy/blob/master/browsermob-core/src/main/resources/sslSupport/ca-certificate-rsa.cer

Follow online instructions to install a certificate on Android.

You also need to configure the WiFi proxy settings on your device:
1. Get the IP of your machine (for e.g. laptop)
2. On your device, open the WiFi advanced options and look for proxy settings. The proxy server is your laptop and the port is the same as when you create a BrowserMob proxy instance in the code.

## Run the code
1. Install and launch Appium
2. Do a mvn build to get the dependencies
3. Run the BrowserMobTest

Make sure your device is connected:
 >$adb devices
