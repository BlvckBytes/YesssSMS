# YesssSMS

## Introduction
In Austria, we have a mobile-network provider called yesss! - located at [yesss.at](https://www.yesss.at). This provider offers you the possibility to send an SMS only by accessing their webpanel with your login-credentials. This API makes use of this and allows you to do so programmatically.

## Disclaimer
This software was built in April 2020. The site-infrastructure might change over time. If you use this, you *need* to check out the *config.properties* file anyways, so also check out the URL and ID section. There you can tweak some internal resources if the site changes by a little margin. When the difference starts to get too big, please open an issue over here and I will get that resolved and updated!

## Usage
First step, as already described before, you need to look into the *config.properties*. There you can find your login (email or phone-number) and your password to *yesss.at*, these should be familiar to you. If not, check out their website! After thats configured, the API usage is childsplay. I will let the code speak for itself.

```java
YesssAPI.sendSMS( "+436601234567", "Hello, world!" );
``` 

As you can probably tell, the first parameter is the phone-number (needs to be in the shown format) and the second represents the sms message (body text).

If you want to disable (error-)logging (which I would not do, but still), you can use the following API call:

```java
YesssAPI.toggleLogging( false );
```

Thats it! You're up and running already.

## How to include it
This resource is now available through jitpack!
```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.github.BlvckBytes</groupId>
    <artifactId>YesssSMS</artifactId>
    <version>0.5</version>
  </dependency>
</dependencies>
```

## Bugs & Improvements
If you find any bugs, site-infrastructure changes or have improvements on hand, feel free to open up a new issue at any time! Thank you!
