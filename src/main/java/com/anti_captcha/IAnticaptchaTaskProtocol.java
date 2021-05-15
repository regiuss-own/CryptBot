package com.anti_captcha;

import com.anti_captcha.ApiResponse.TaskResultResponse;

import org.json.JSONObject;

public interface IAnticaptchaTaskProtocol {
    JSONObject getPostData();

    TaskResultResponse.SolutionData getTaskSolution();
}
