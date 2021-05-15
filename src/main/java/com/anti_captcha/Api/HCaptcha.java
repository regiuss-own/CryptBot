package com.anti_captcha.Api;

import com.anti_captcha.AnticaptchaBase;
import com.anti_captcha.ApiResponse.TaskResultResponse;
import com.anti_captcha.Helper.DebugHelper;
import com.anti_captcha.IAnticaptchaTaskProtocol;

import org.json.JSONException;
import org.json.JSONObject;

public class HCaptcha extends HCaptchaProxyless implements IAnticaptchaTaskProtocol {
    private AnticaptchaBase.ProxyTypeOption proxyType = AnticaptchaBase.ProxyTypeOption.HTTP;
    private String proxyAddress;
    private Integer proxyPort;
    private String proxyLogin;
    private String proxyPassword;
    private String userAgent;
    private String cookies;

    @Override
    public JSONObject getPostData() {
        JSONObject postData = new JSONObject();

        try {
            postData.put("type", "HCaptchaTask");
            postData.put("websiteURL", websiteUrl.toString());
            postData.put("websiteKey", websiteKey);
            postData.put("proxyType", proxyType.toString().toLowerCase());
            postData.put("proxyAddress", proxyAddress);
            postData.put("proxyPort", proxyPort);
            postData.put("proxyLogin", proxyLogin);
            postData.put("proxyPassword", proxyPassword);
            postData.put("userAgent", userAgent);
            postData.put("cookies", cookies);
        } catch (JSONException e) {
            DebugHelper.out("JSON compilation error: " + e.getMessage(), DebugHelper.Type.ERROR);

            return null;
        }

        return postData;
    }

    @SuppressWarnings("unused")
    public void setProxyType(AnticaptchaBase.ProxyTypeOption proxyType) {
        this.proxyType = proxyType;
    }

    @SuppressWarnings("unused")
    public void setProxyAddress(String proxyAddress) {
        this.proxyAddress = proxyAddress;
    }

    @SuppressWarnings("unused")
    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    @SuppressWarnings("unused")
    public void setProxyLogin(String proxyLogin) {
        this.proxyLogin = proxyLogin;
    }

    @SuppressWarnings("unused")
    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    @SuppressWarnings("unused")
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @SuppressWarnings("unused")
    public void setCookies(String cookies) {
        this.cookies = cookies;
    }

    @Override
    public TaskResultResponse.SolutionData getTaskSolution() {
        return taskInfo.getSolution();
    }
}