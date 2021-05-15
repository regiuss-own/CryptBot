package com.anti_captcha;

import com.anti_captcha.Api.CustomCaptcha;
import com.anti_captcha.Api.FunCaptcha;
import com.anti_captcha.Api.GeeTestProxyless;
import com.anti_captcha.Api.HCaptchaProxyless;
import com.anti_captcha.Api.ImageToText;
import com.anti_captcha.Api.NoCaptcha;
import com.anti_captcha.Api.NoCaptchaProxyless;
import com.anti_captcha.Api.RecaptchaV3Proxyless;
import com.anti_captcha.Api.SquareCaptcha;
import com.anti_captcha.Helper.DebugHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    public static void main(String[] args) throws InterruptedException, MalformedURLException, JSONException {
        exampleGetBalance();
        exampleImageToText();
        exampleSquare();
        exampleNoCaptchaProxyless();
        exampleRecaptchaV3Proxyless();
        exampleNoCaptcha();
        exampleCustomCaptcha();
        exampleFuncaptcha();
        exampleGeeTestProxyless();
        exampleHCaptchaProxyless();
    }

    private static void exampleGeeTestProxyless() throws MalformedURLException, InterruptedException {
        DebugHelper.setVerboseMode(true);

        GeeTestProxyless api = new GeeTestProxyless();
        api.setClientKey("1234567890123456789012");
        api.setWebsiteUrl(new URL("https://auth.geetest.com/"));
        api.setWebsiteKey("b6e21f90a91a3c2d4a31fe84e10d0442");
        // "challenge" for testing you can get here: https://www.binance.com/security/getGtCode.html?t=1561554068768
        // you need to get a new "challenge" each time
        api.setWebsiteChallenge("cd0b3b5c33fb951ab364d9e13ccd7bf8");

        if (!api.createTask()) {
            DebugHelper.out(
                    "API v2 send failed. " + api.getErrorMessage(),
                    DebugHelper.Type.ERROR
            );
        } else if (!api.waitForResult()) {
            DebugHelper.out("Could not solve the captcha.", DebugHelper.Type.ERROR);
        } else {
            DebugHelper.out("Result CHALLENGE: " + api.getTaskSolution().getChallenge(), DebugHelper.Type.SUCCESS);
            DebugHelper.out("Result SECCODE: " + api.getTaskSolution().getSeccode(), DebugHelper.Type.SUCCESS);
            DebugHelper.out("Result VALIDATE: " + api.getTaskSolution().getValidate(), DebugHelper.Type.SUCCESS);
        }
    }

    private static void exampleImageToText() throws InterruptedException {
        DebugHelper.setVerboseMode(true);

        ImageToText api = new ImageToText();
        api.setClientKey("1234567890123456789012");
        api.setFilePath("captcha.jpg");

        if (!api.createTask()) {
            DebugHelper.out(
                    "API v2 send failed. " + api.getErrorMessage(),
                    DebugHelper.Type.ERROR
            );
        } else if (!api.waitForResult()) {
            DebugHelper.out("Could not solve the captcha.", DebugHelper.Type.ERROR);
        } else {
            DebugHelper.out("Result: " + api.getTaskSolution().getText(), DebugHelper.Type.SUCCESS);
        }
    }

    private static void exampleSquare() throws InterruptedException {
        DebugHelper.setVerboseMode(true);

        SquareCaptcha api = new SquareCaptcha();
        api.setClientKey("1234567890123456789012");
        api.setFilePath("square.jpg");
        api.setObjectName("FISH / РЫБА");
        api.setColumnsCount(4);
        api.setRowsCount(4);

        if (!api.createTask()) {
            DebugHelper.out(
                    "API v2 send failed. " + api.getErrorMessage(),
                    DebugHelper.Type.ERROR
            );
        } else if (!api.waitForResult()) {
            DebugHelper.out("Could not solve the captcha.", DebugHelper.Type.ERROR);
        } else {
            DebugHelper.out("Result: " + api.getTaskSolution().getCellNumbers(), DebugHelper.Type.SUCCESS);
        }
    }

    private static void exampleNoCaptchaProxyless() throws MalformedURLException, InterruptedException {
        DebugHelper.setVerboseMode(true);

        NoCaptchaProxyless api = new NoCaptchaProxyless();
        api.setClientKey("1234567890123456789012");
        api.setWebsiteUrl(new URL("http://http.myjino.ru/recaptcha/test-get.php"));
        api.setWebsiteKey("6Lc_aCMTAAAAABx7u2W0WPXnVbI_v6ZdbM6rYf16");

        if (!api.createTask()) {
            DebugHelper.out(
                    "API v2 send failed. " + api.getErrorMessage(),
                    DebugHelper.Type.ERROR
            );
        } else if (!api.waitForResult()) {
            DebugHelper.out("Could not solve the captcha.", DebugHelper.Type.ERROR);
        } else {
            DebugHelper.out("Result: " + api.getTaskSolution().getGRecaptchaResponse(), DebugHelper.Type.SUCCESS);
        }
    }

    private static void exampleHCaptchaProxyless() throws MalformedURLException, InterruptedException {
        DebugHelper.setVerboseMode(true);

        HCaptchaProxyless api = new HCaptchaProxyless();
        api.setClientKey("1234567890123456789012");
        api.setWebsiteUrl(new URL("http://democaptcha.com/"));
        api.setWebsiteKey("51829642-2cda-4b09-896c-594f89d700cc");

        if (!api.createTask()) {
            DebugHelper.out(
                    "API v2 send failed. " + api.getErrorMessage(),
                    DebugHelper.Type.ERROR
            );
        } else if (!api.waitForResult()) {
            DebugHelper.out("Could not solve the captcha.", DebugHelper.Type.ERROR);
        } else {
            DebugHelper.out("Result: " + api.getTaskSolution().getGRecaptchaResponse(), DebugHelper.Type.SUCCESS);
        }
    }

    private static void exampleRecaptchaV3Proxyless() throws MalformedURLException, InterruptedException {
        DebugHelper.setVerboseMode(true);

        RecaptchaV3Proxyless api = new RecaptchaV3Proxyless();
        api.setClientKey("1234567890123456789012");
        api.setWebsiteUrl(new URL("http://www.supremenewyork.com"));
        api.setWebsiteKey("6Leva6oUAAAAAMFYqdLAI8kJ5tw7BtkHYpK10RcD");
        api.setPageAction("testPageAction");

        if (!api.createTask()) {
            DebugHelper.out(
                    "API v2 send failed. " + api.getErrorMessage(),
                    DebugHelper.Type.ERROR
            );
        } else if (!api.waitForResult()) {
            DebugHelper.out("Could not solve the captcha.", DebugHelper.Type.ERROR);
        } else {
            DebugHelper.out("Result: " + api.getTaskSolution().getGRecaptchaResponse(), DebugHelper.Type.SUCCESS);
        }
    }

    private static void exampleNoCaptcha() throws MalformedURLException, InterruptedException {
        DebugHelper.setVerboseMode(true);

        NoCaptcha api = new NoCaptcha();
        api.setClientKey("1234567890123456789012");
        api.setWebsiteUrl(new URL("http://http.myjino.ru/recaptcha/test-get.php"));
        api.setWebsiteKey("6Lc_aCMTAAAAABx7u2W0WPXnVbI_v6ZdbM6rYf16");
        api.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/52.0.2743.116");

        // proxy access parameters
        api.setProxyType(NoCaptcha.ProxyTypeOption.HTTP);
        api.setProxyAddress("xx.xxx.xx.xx");
        api.setProxyPort(8282);
        api.setProxyLogin("login");
        api.setProxyPassword("password");

        if (!api.createTask()) {
            DebugHelper.out(
                    "API v2 send failed. " + api.getErrorMessage(),
                    DebugHelper.Type.ERROR
            );
        } else if (!api.waitForResult()) {
            DebugHelper.out("Could not solve the captcha.", DebugHelper.Type.ERROR);
        } else {
            DebugHelper.out("Result: " + api.getTaskSolution().getGRecaptchaResponse(), DebugHelper.Type.SUCCESS);
        }
    }

    private static void exampleGetBalance() {
        DebugHelper.setVerboseMode(true);

        ImageToText api = new ImageToText();
        api.setClientKey("1234567890123456789012");

        Double balance = api.getBalance();

        if (balance == null) {
            DebugHelper.out("GetBalance() failed. " + api.getErrorMessage(), DebugHelper.Type.ERROR);
        } else {
            DebugHelper.out("Balance: " + balance, DebugHelper.Type.SUCCESS);
        }
    }

    private static void exampleCustomCaptcha() throws JSONException, InterruptedException {
        DebugHelper.setVerboseMode(true);
        int randInt = ThreadLocalRandom.current().nextInt(0, 10000);

        JSONArray forms = new JSONArray();

        forms.put(0, new JSONObject());
        forms.getJSONObject(0).put("label", "number");
        forms.getJSONObject(0).put("labelHint", false);
        forms.getJSONObject(0).put("contentType", false);
        forms.getJSONObject(0).put("name", "license_plate");
        forms.getJSONObject(0).put("inputType", "text");
        forms.getJSONObject(0).put("inputOptions", new JSONObject());
        forms.getJSONObject(0).getJSONObject("inputOptions").put("width", "100");
        forms.getJSONObject(0).getJSONObject("inputOptions").put(
                "placeHolder",
                "Enter letters and numbers without spaces"
        );

        forms.put(1, new JSONObject());
        forms.getJSONObject(1).put("label", "Car color");
        forms.getJSONObject(1).put("labelHint", "Select the car color");
        forms.getJSONObject(1).put("contentType", false);
        forms.getJSONObject(1).put("name", "color");
        forms.getJSONObject(1).put("inputType", "select");
        forms.getJSONObject(1).put("inputOptions", new JSONArray());
        forms.getJSONObject(1).getJSONArray("inputOptions").put(0, new JSONObject());
        forms.getJSONObject(1).getJSONArray("inputOptions").getJSONObject(0).put(
                "value",
                "white"
        );
        forms.getJSONObject(1).getJSONArray("inputOptions").getJSONObject(0).put(
                "caption",
                "White color"
        );
        forms.getJSONObject(1).getJSONArray("inputOptions").put(1, new JSONObject());
        forms.getJSONObject(1).getJSONArray("inputOptions").getJSONObject(1).put(
                "value",
                "black"
        );
        forms.getJSONObject(1).getJSONArray("inputOptions").getJSONObject(1).put(
                "caption",
                "Black color"
        );
        forms.getJSONObject(1).getJSONArray("inputOptions").put(2, new JSONObject());
        forms.getJSONObject(1).getJSONArray("inputOptions").getJSONObject(2).put(
                "value",
                "gray"
        );
        forms.getJSONObject(1).getJSONArray("inputOptions").getJSONObject(2).put(
                "caption",
                "Gray color"
        );

        CustomCaptcha api = new CustomCaptcha();
        api.setClientKey("1234567890123456789012");
        api.setImageUrl("https://files.anti-captcha.com/26/41f/c23/7c50ff19.jpg?random=" + randInt);
        api.setAssignment("Enter the licence plate number");
        api.setForms(forms);

        if (!api.createTask()) {
            DebugHelper.out(
                    "API v2 send failed. " + api.getErrorMessage(),
                    DebugHelper.Type.ERROR
            );
        } else if (!api.waitForResult()) {
            DebugHelper.out("Could not solve the captcha.", DebugHelper.Type.ERROR);
        } else {
            JSONObject answers = api.getTaskSolution().getAnswers();
            Iterator<?> keys = answers.keys();

            while (keys.hasNext()) {
                String question = (String) keys.next();
                String answer = answers.getString(question);

                DebugHelper.out(
                        "The answer for the question '" + question + "' : " + answer,
                        DebugHelper.Type.SUCCESS
                );
            }
        }
    }

    private static void exampleFuncaptcha() throws MalformedURLException, InterruptedException {
        DebugHelper.setVerboseMode(true);

        FunCaptcha api = new FunCaptcha();
        api.setClientKey("1234567890123456789012");
        api.setWebsiteUrl(new URL("http://http.myjino.ru/funcaptcha_test/"));
        api.setWebsitePublicKey("DE0B0BB7-1EE4-4D70-1853-31B835D4506B");
        api.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116");

        // proxy access parameters
        api.setProxyType(NoCaptcha.ProxyTypeOption.HTTP);
        api.setProxyAddress("xx.xxx.xx.xx");
        api.setProxyPort(8282);
        api.setProxyLogin("login");
        api.setProxyPassword("password");

        if (!api.createTask()) {
            DebugHelper.out(
                    "API v2 send failed. " + api.getErrorMessage(),
                    DebugHelper.Type.ERROR
            );
        } else if (!api.waitForResult()) {
            DebugHelper.out("Could not solve the captcha.", DebugHelper.Type.ERROR);
        } else {
            DebugHelper.out("Result: " + api.getTaskSolution().getToken(), DebugHelper.Type.SUCCESS);
        }
    }
}
