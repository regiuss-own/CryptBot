package ru.regiuss.CryptWebBot.Utils;

import com.anti_captcha.Api.RecaptchaV2EnterpriseProxyless;
import com.anti_captcha.Helper.DebugHelper;
import com.google.common.hash.Hashing;
import de.taimos.totp.TOTP;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.regiuss.CryptWebBot.Configurations.Settings;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Utils {
    /**
     * Отправка капчи на сервис anti-captcha.com для решения
     */
    public static String SolveCaptcha(String captcha, String accountName) throws MalformedURLException, InterruptedException {
        RecaptchaV2EnterpriseProxyless api = new RecaptchaV2EnterpriseProxyless();
        api.setClientKey(Settings.ANTICAPTCHA_API_KEY);
        api.setWebsiteUrl(new URL("https://all-access.wax.io/"));
        api.setWebsiteKey("6LdaB7UUAAAAAD2w3lLYRQJqsoup5BsYXI2ZIpFF");

        String captchaResponse = null;

        //DebugHelper.setVerboseMode(true);

        ConsoleMessage.out("Отправка капчи...", ConsoleMessage.Type.INFO, accountName);

        JSONObject enterprisePayload = new JSONObject();
        try {
            enterprisePayload.put("s", captcha);
        } catch (Exception e) {
            ConsoleMessage.out("JSON error: "+e.getMessage(), ConsoleMessage.Type.ERROR);
            return null;
        }
        api.setEnterprisePayload(enterprisePayload);

        if (!api.createTask()) {
            ConsoleMessage.out(
                    "Ошибка при отправке капчи: " + api.getErrorMessage(),
                    ConsoleMessage.Type.ERROR, accountName
            );
        } else if (!api.waitForResult()) {
            ConsoleMessage.out("Капча не разгадана", ConsoleMessage.Type.ERROR, accountName);
        } else {
            captchaResponse = api.getTaskSolution().getGRecaptchaResponse();
            ConsoleMessage.out("CAPTCHA_RESULT: " + captchaResponse, ConsoleMessage.Type.DEBUG, accountName);
        }
        return captchaResponse;
    }


    public static String SolveCaptchaV2(String accountName) throws IOException, JSONException, InterruptedException {
        URL url = new URL("https://api.capmonster.cloud/createTask");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("POST");

        try {
            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            JSONObject data = new JSONObject();
            JSONObject task = new JSONObject();
            task.put("type", "NoCaptchaTaskProxyless");
            task.put("websiteURL", "https://all-access.wax.io/");
            task.put("websiteKey", "6LdaB7UUAAAAAD2w3lLYRQJqsoup5BsYXI2ZIpFF");

            data.put("clientKey",Settings.CAPMONSTER_API_KEY);
            data.put("task", task);
            ConsoleMessage.out("SolveCaptchaV2 createTaskParams: " + data.toString(), ConsoleMessage.Type.DEBUG);
            out.writeBytes(data.toString());
            out.flush();
            out.close();
        }catch (Exception e){
            return null;
        }

        BufferedReader in = new BufferedReader(
                new BufferedReader(new InputStreamReader(con.getResponseCode() / 100 == 2 ? con.getInputStream() : con.getErrorStream())));
        String inputLine;
        JSONObject response = null;
        if ((inputLine = in.readLine()) != null) {
            ConsoleMessage.out("SolveCaptchaV2 createTaskResponse: " + inputLine, ConsoleMessage.Type.DEBUG);
            response =  new JSONObject(inputLine);
        }
        in.close();
        con.disconnect();

        if(response.getInt("errorId") != 0){
            ConsoleMessage.out("Ошибка капчи код: " + response.toString(), ConsoleMessage.Type.ERROR, accountName);
            return null;
        }
        int taskID = response.getInt("taskId");
        int count = 0;
        while (true){
            response = GetCaptchaTaskResult(taskID);
            if(response.getString("status").equals("ready"))break;
            count++;
            if(count >= 120)return null;
            Thread.sleep(5000);
        }


        return response.getJSONObject("solution").getString("gRecaptchaResponse");
    }


    public static JSONObject GetCaptchaTaskResult(int taskId) throws IOException, JSONException {
        URL url = new URL("https://api.capmonster.cloud/getTaskResult/");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("POST");

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        JSONObject data = new JSONObject();

        data.put("clientKey",Settings.CAPMONSTER_API_KEY);
        data.put("taskId", taskId);

        ConsoleMessage.out("SolveCaptchaV2 GetTaskResultParams: " + data.toString(), ConsoleMessage.Type.DEBUG);
        out.writeBytes(data.toString());
        out.flush();
        out.close();

        BufferedReader in = new BufferedReader(
                new BufferedReader(new InputStreamReader(con.getResponseCode() / 100 == 2 ? con.getInputStream() : con.getErrorStream())));
        String inputLine;
        if ((inputLine = in.readLine()) != null) {
            ConsoleMessage.out("SolveCaptchaV2 GetTaskResultResponse: " + inputLine, ConsoleMessage.Type.DEBUG);
            return new JSONObject(inputLine);
        }
        in.close();
        con.disconnect();
        return null;
    }

    /**
     * Открытие браузера в фоне для получения токена капчи
     */
    public static String GetCapthcaToken(){

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("headless");
        chromeOptions.addArguments("--log-level=3");
        chromeOptions.addArguments("--silent");
        chromeOptions.addArguments("--disable-logging");
        WebDriver driver = new ChromeDriver(chromeOptions);
        String res = null;
        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
        for (int i = 0; i < 3; i++) {
            //driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
            driver.get("https://all-access.wax.io/");

            //WebDriverWait wait = new WebDriverWait(driver, 120);
            //WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("recaptcha-inner")));

            //driver.switchTo().frame(driver.findElement(By.className("recaptcha-inner")).findElement(By.tagName("iframe")));

            for(WebElement e : driver.findElements(By.className("recaptcha-inner"))){
                driver.switchTo().frame(e.findElement(By.tagName("iframe")));
                res = driver.findElement(By.id("recaptcha-token")).getAttribute("value");
            }
            driver.switchTo().defaultContent();
            if(res != null)break;
        }
        driver.close();
        driver.quit();
        return res;
    }

    public static List<Integer> GetArrOfHexFromTheEnd(String str, int count){
        List<Integer> res = new ArrayList<>();
        for (int i = str.length(); i > 0 ; i-=2) {
            res.add(Integer.parseInt(str.substring(i-2, i), 16));
            if(res.size() >= count)break;
        }
        return res;
    }

    public static String GetReverseString(String str, int length){
        String res = "";
        for (int i = str.length(); i > 0; i-=length) {
            res += str.substring(i-length, i);
        }
        return res;
    }

    public static List<Integer> GetArrOfTimeStamp(long ts){
        return HexToList(GetReverseString(Long.toHexString(ts),2));
    }

    /**
     * Получение 2fa кода по секретному ключу
     */
    public static String getTOTPCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }

    /**
     * Преобразовать массив в ХЭШ
     * @param buffer массив целых чисел
     * @return String hex
     */
    public static String toHex(List<Integer> buffer){
        List<String> res = new ArrayList<>();
        for(int o : buffer){
            if(o > 256)o -= o/256 * 256;
            StringBuilder s = new StringBuilder(Integer.toHexString(o));
            while (s.length() < 2){
                s.insert(0, "0");
            }
            res.add(s.toString());
        }
        return String.join("", res);
    }

    /**
     * Получить массив их ХЭША
     * @param str Хэш
     * @return List
     */
    public static List<Integer> HexToList(String str){
        char[] strChar = str.toCharArray();
        List<Integer> res = new ArrayList<>();
        String num;
        for (int i = 0; i < str.length(); i+=2) {
            num = Character.toString(strChar[i]) + Character.toString(strChar[i+1]);
            res.add(Integer.parseInt(num, 16));
        }
        return res;
    }

    /**
     * Получить массив из 8 случайных элементов
     * @return List<Integer>
     */
    public static List<Integer> getRand(){
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            res.add((int)Math.floor(Math.random() * 255));
        }
        return res;
    }

    /**
     * Вспомогательный метод для getArrayName
     * @param c Номер символа
     * @return
     */
    public static int charToSymbol (int c) {
        if (c >= (int)'a' && c <= (int)'z') {
            return (c - (int)'a') + 6;
        }
        if (c >= (int)'1' && c <= (int)'5') {
            return (c - (int)'1') + 1;
        }
        return 0;
    };

    /**
     * Получть ХЭШ массива
     * @param a Массив
     * @return ХЭЭ массива
     */
    public static String combinedToHex(List<Integer> a){
        Iterator<Integer> iterator = a.iterator();
        byte[] byteArray = new byte[24];
        int index = 0;

        while(iterator.hasNext())
        {
            Integer i = iterator.next();
            //ConsoleMessage.out(i.byteValue() + "", ConsoleMessage.Type.DEBUG);
            byteArray[index] = (i.byteValue());
            index++;
        }
        return Hashing.sha256().hashBytes(byteArray).toString();
    }

    /**
     * Получить массив по имени
     * @param s Строка имя
     * @return
     */
    public static List<Integer> getArrayName(String s){
        int[] a = new int[8];
        int bit = 63;
        for (int i = 0; i < s.length(); ++i) {
            int c = charToSymbol(s.toCharArray()[i]);
            if (bit < 5) {
                c = c << 1;
            }
            for (int j = 4; j >= 0; --j) {
                if (bit >= 0) {
                    a[(int)Math.floor(bit / 8)] |= ((c >> j) & 1) << (bit % 8);
                    --bit;
                }
            }
        }
        List<Integer> res = new ArrayList<>();
        for(int num : a){
            res.add(num);
        }
        return res;
    };

    public static String CheckUpdate() throws IOException {
        URL url = new URL("https://api.github.com/repos/ReGius-igmt/CryptBot/releases/latest");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(
                new BufferedReader(new InputStreamReader(con.getResponseCode() / 100 == 2 ? con.getInputStream() : con.getErrorStream())));
        String inputLine;
        String res = "";
        if ((inputLine = in.readLine()) != null) {
            res += inputLine;
        }
        in.close();
        con.disconnect();
        return new JSONObject(res).getString("tag_name");
    }

    public static void ConsolePause() throws IOException, InterruptedException {
        new ProcessBuilder("cmd", "/c", "pause").inheritIO().start().waitFor();
    }
}
