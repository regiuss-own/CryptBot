package com.anti_captcha.Api;

import com.anti_captcha.ApiResponse.TaskResultResponse;
import com.anti_captcha.Helper.DebugHelper;
import org.json.JSONException;
import org.json.JSONObject;

public class RecaptchaV3EnterpriseProxyless extends RecaptchaV3Proxyless {

    @Override
    public JSONObject getPostData() {
        JSONObject postData = super.getPostData();

        try {
            postData.put("isEnteprise", true);
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
