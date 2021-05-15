package com.anti_captcha.Api;

import com.anti_captcha.AnticaptchaBase;
import com.anti_captcha.ApiResponse.TaskResultResponse;
import com.anti_captcha.Helper.DebugHelper;
import com.anti_captcha.IAnticaptchaTaskProtocol;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class HCaptchaProxyless extends AnticaptchaBase implements IAnticaptchaTaskProtocol {
    URL websiteUrl;
    String websiteKey;

    public JSONObject getPostData() {
        JSONObject postData = new JSONObject();

        try {
            postData.put("type", "HCaptchaTaskProxyless");
            postData.put("websiteURL", websiteUrl.toString());
            postData.put("websiteKey", websiteKey);
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

    @SuppressWarnings("unused")
    public void setWebsiteUrl(URL websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    @SuppressWarnings("unused")
    public void setWebsiteKey(String websiteKey) {
        this.websiteKey = websiteKey;
    }
}
