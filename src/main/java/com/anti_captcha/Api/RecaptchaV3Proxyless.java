package com.anti_captcha.Api;

import com.anti_captcha.AnticaptchaBase;
import com.anti_captcha.ApiResponse.TaskResultResponse;
import com.anti_captcha.Helper.DebugHelper;
import com.anti_captcha.IAnticaptchaTaskProtocol;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class RecaptchaV3Proxyless extends AnticaptchaBase implements IAnticaptchaTaskProtocol {
    private URL websiteUrl;
    private String websiteKey;
    private String pageAction;
    private Double minScore = 0.3;

    public void setWebsiteUrl(URL websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public void setWebsiteKey(String websiteKey) {
        this.websiteKey = websiteKey;
    }

    public String getPageAction() {
        return pageAction;
    }

    public void setPageAction(String pageAction) {
        this.pageAction = pageAction;
    }

    public Double getMinScore() {
        return minScore;
    }

    public void setMinScore(Double minScore) {
        if (!minScore.equals(0.3) && !minScore.equals(0.7) && !minScore.equals(0.9)) {
            DebugHelper.out("minScore must be one of these: 0.3, 0.7, 0.9; you passed " + minScore + "; 0.3 will be set", DebugHelper.Type.ERROR);
        } else {
            this.minScore = minScore;
        }
    }

    @Override
    public JSONObject getPostData() {
        JSONObject postData = new JSONObject();

        try {
            postData.put("type", "RecaptchaV3TaskProxyless");
            postData.put("websiteURL", websiteUrl.toString());
            postData.put("websiteKey", websiteKey);
            postData.put("pageAction", pageAction);
            postData.put("minScore", minScore);
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
}
