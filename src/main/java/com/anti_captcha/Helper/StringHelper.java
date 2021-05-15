package com.anti_captcha.Helper;

import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.bind.DatatypeConverter;

public class StringHelper {
    public static String toCamelCase(String s) {
        String[] parts = s.split("_");
        String camelCaseString = "";

        for (String part : parts) {
            camelCaseString += part.substring(0, 1).toUpperCase() + part.substring(1).toLowerCase();
        }

        return camelCaseString.substring(0, 1).toLowerCase() + camelCaseString.substring(1);
    }

    public static String imageFileToBase64String(String path) {
        try {
            return DatatypeConverter.printBase64Binary(Files.readAllBytes(Paths.get(path)));
        } catch (Exception e) {
            return null;
        }
    }
}
