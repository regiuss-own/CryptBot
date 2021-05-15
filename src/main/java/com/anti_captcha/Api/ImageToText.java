package com.anti_captcha.Api;

import com.anti_captcha.AnticaptchaBase;
import com.anti_captcha.ApiResponse.TaskResultResponse;
import com.anti_captcha.Helper.DebugHelper;
import com.anti_captcha.Helper.StringHelper;
import com.anti_captcha.IAnticaptchaTaskProtocol;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class ImageToText extends AnticaptchaBase implements IAnticaptchaTaskProtocol {
    private Boolean phrase;
    private Boolean case_;
    private NumericOption numeric = NumericOption.NO_REQUIREMENTS;
    private Integer math;
    private Integer minLength;
    private Integer maxLength;
    private String bodyBase64;

    public void setPhrase(Boolean phrase) {
        this.phrase = phrase;
    }

    public void setCase_(Boolean case_) {
        this.case_ = case_;
    }

    public void setNumeric(NumericOption numeric) {
        this.numeric = numeric;
    }

    public void setMath(Integer math) {
        this.math = math;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public enum NumericOption {
        NO_REQUIREMENTS,
        NUMBERS_ONLY,
        ANY_LETTERS_EXCEPT_NUMBERS
    }

    public void setFilePath(String filePath) {
        File f = new File(filePath);

        if (!f.exists() || f.isDirectory()) {
            DebugHelper.out("File " + filePath + " not found", DebugHelper.Type.ERROR);
        } else {
            bodyBase64 = StringHelper.imageFileToBase64String(filePath);

            if (bodyBase64 == null) {
                DebugHelper.out(
                        "Could not convert the file \" + value + \" to base64. Is this an image file?",
                        DebugHelper.Type.ERROR
                );
            }
        }
    }

    public Boolean getPhrase() {
        return phrase;
    }

    public Boolean getCase_() {
        return case_;
    }

    public NumericOption getNumeric() {
        return numeric;
    }

    public Integer getMath() {
        return math;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    @Override
    public JSONObject getPostData() {

        if (bodyBase64 == null || bodyBase64.length() == 0) {
            return null;
        }

        JSONObject postData = new JSONObject();

        try {
            postData.put("type", "ImageToTextTask");
            postData.put("body", bodyBase64.replace("\r", "").replace("\n", ""));
            postData.put("phrase", phrase);
            postData.put("case", case_);
            postData.put("numeric", numeric.equals(NumericOption.NO_REQUIREMENTS) ? 0 : numeric.equals(NumericOption.NUMBERS_ONLY) ? 1 : 2);
            postData.put("math", math);
            postData.put("minLength", minLength);
            postData.put("maxLength", maxLength);
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

    public void setBodyBase64(String bodyBase64) {
        this.bodyBase64 = bodyBase64;
    }
}
