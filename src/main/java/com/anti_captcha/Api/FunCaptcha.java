package com.anti_captcha.Api;

import com.anti_captcha.AnticaptchaBase;
import com.anti_captcha.ApiResponse.TaskResultResponse;
import com.anti_captcha.Helper.DebugHelper;
import com.anti_captcha.IAnticaptchaTaskProtocol;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class FunCaptcha extends AnticaptchaBase implements IAnticaptchaTaskProtocol {
    private String proxyLogin;
    private String proxyPassword;
    private Integer proxyPort;
    private ProxyTypeOption proxyType;
    private String userAgent;
    private String proxyAddress;
    private URL websiteUrl;
    private String websitePublicKey;

    public void setProxyLogin(String proxyLogin) {
        this.proxyLogin = proxyLogin;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    public void setProxyType(ProxyTypeOption proxyType) {
        this.proxyType = proxyType;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void setProxyAddress(String proxyAddress) {
        this.proxyAddress = proxyAddress;
    }

    @Override
    public JSONObject getPostData() {
        JSONObject postData = new JSONObject();

        if (proxyType == null || proxyPort == null || proxyPort < 1 || proxyPort > 65535
                || proxyAddress == null || proxyAddress.length() == 0) {
            DebugHelper.out("Proxy data is incorrect!", DebugHelper.Type.ERROR);

            return null;
        }

        try {
            postData.put("type", "FunCaptchaTask");
            postData.put("websiteURL", websiteUrl);
            postData.put("websitePublicKey", websitePublicKey);
            postData.put("proxyType", proxyType.toString().toLowerCase());
            postData.put("proxyAddress", proxyAddress);
            postData.put("proxyPort", proxyPort);
            postData.put("proxyLogin", proxyLogin);
            postData.put("proxyPassword", proxyPassword);
            postData.put("userAgent", userAgent);
        } catch (JSONException e) {
            DebugHelper.out("JSON compilation error: " + e.getMessage(), DebugHelper.Type.ERROR);

            return null;
        }

        return postData;
    }

    @Override
    public TaskResultResponse.SolutionData getTaskSolution() {
        return taskInfo.getSolution();
    }

    public void setWebsiteUrl(URL websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public void setWebsitePublicKey(String websitePublicKey) {
        this.websitePublicKey = websitePublicKey;
    }
}
