package com.anti_captcha.Api;

import com.anti_captcha.AnticaptchaBase;
import com.anti_captcha.ApiResponse.TaskResultResponse;
import com.anti_captcha.Helper.DebugHelper;
import com.anti_captcha.Helper.StringHelper;
import com.anti_captcha.IAnticaptchaTaskProtocol;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class SquareCaptcha extends AnticaptchaBase implements IAnticaptchaTaskProtocol {
    private String bodyBase64;
    private String objectName = "";
    private int rowsCount = 3;
    private int columnsCount = 3;

    @Override
    public JSONObject getPostData() {

        if (bodyBase64 == null || bodyBase64.length() == 0) {
            return null;
        }

        JSONObject postData = new JSONObject();

        try {
            postData.put("type", "SquareNetTask");
            postData.put("body", bodyBase64.replace("\r", "").replace("\n", ""));
            postData.put("objectName", objectName);
            postData.put("rowsCount", rowsCount);
            postData.put("columnsCount", columnsCount);
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

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public void setRowsCount(int rowsCount) {
        this.rowsCount = rowsCount;
    }

    public void setColumnsCount(int columnsCount) {
        this.columnsCount = columnsCount;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean setFilePath(String filename) {
        File f = new File(filename);

        if (f.exists() && !f.isDirectory()) {
            if (f.length() > 100) {
                bodyBase64 = StringHelper.imageFileToBase64String(filename);

                return true;
            } else {
                DebugHelper.out("file " + filename + " is too small or empty", DebugHelper.Type.ERROR);
            }
        } else {
            DebugHelper.out("file " + filename + " not found", DebugHelper.Type.ERROR);
        }

        return false;
    }
}
