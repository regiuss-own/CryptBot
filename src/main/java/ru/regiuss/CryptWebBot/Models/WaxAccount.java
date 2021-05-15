package ru.regiuss.CryptWebBot.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.regiuss.CryptWebBot.Configurations.Settings;
import ru.regiuss.CryptWebBot.Utils.ConsoleMessage;
import ru.regiuss.CryptWebBot.Utils.Utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class WaxAccount {
    protected String login;
    protected String password;
    protected String fa2token;
    protected String token;
    protected CookieManager session;
    protected String account_name;
    //protected WebDriver driver;

    public WaxAccount(String login, String password, String fa2token){
        this.login = login;
        this.password = password;
        this.fa2token = fa2token;
        this.session = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(this.session);

        //ChromeOptions chromeProfile = new ChromeOptions();
        //chromeProfile.addArguments("headless");

        //this.driver = new ChromeDriver(chromeProfile);
    }

    public String getLogin() {
        return login;
    }

    public String getFa2token() {
        return fa2token;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    /**
     * Получить токен аккаунта
     * Необходима предварительная авторизация Login()
     * @return String token
     */
    public String getToken() {
        return this.token;
    }
    public void setToken(String token) throws URISyntaxException {
        this.token = token;
        session.getCookieStore().add(
                new URI("wax.io"),
                new HttpCookie("session_token", this.token)
        );
    }
    /*public WebDriver getDriver() {
        return this.driver;
    }*/
    public String getPassword() {
        return password;
    }

    public CookieManager getSession() {
        return session;
    }


    public JSONObject GetAccountResources() throws IOException {
        URL url = new URL("https://wax.greymass.com/v1/chain/get_account");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty("user-agent", "Mozilla/5.0");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("origin", "https://wax.bloks.io");
        con.setRequestMethod("POST");

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        JSONObject data = new JSONObject();
        data.put("account_name",account_name);
        //ConsoleMessage.out(data.toString(), ConsoleMessage.Type.DEBUG);
        out.writeBytes(data.toString());
        out.flush();
        out.close();

        BufferedReader in = new BufferedReader(
                new BufferedReader(new InputStreamReader(con.getResponseCode() / 100 == 2 ? con.getInputStream() : con.getErrorStream())));
        String inputLine;
        if ((inputLine = in.readLine()) != null) {
            ConsoleMessage.out(inputLine, ConsoleMessage.Type.DEBUG);
            return new JSONObject(inputLine);
        }
        in.close();
        con.disconnect();
        return null;


        /*Process p = Runtime.getRuntime().exec(String.format("py %s\\cloudflare.py WaxWalletResources %s",System.getProperty("user.dir"), account_name));
        p.waitFor();

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));

        String s = null;
        String res = "";
        while ((s = stdInput.readLine()) != null) {
            ConsoleMessage.out(s, ConsoleMessage.Type.DEBUG);
            res = s;
        }

        ConsoleMessage.out(res, ConsoleMessage.Type.DEBUG);
        return new JSONObject(res);*/
    }

    public JSONArray GetActions() throws IOException {
        URL url = new URL("https://wax.greymass.com/v1/history/get_actions");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty("origin", "https://wax.bloks.io");
        con.setRequestMethod("POST");

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        JSONObject data = new JSONObject();
        data.put("account_name",account_name);
        data.put("offset",-100);
        data.put("pos",-1);
        //ConsoleMessage.out(data.toString(), ConsoleMessage.Type.DEBUG);
        out.writeBytes(data.toString());
        out.flush();
        out.close();

        BufferedReader in = new BufferedReader(
                new BufferedReader(new InputStreamReader(con.getResponseCode() / 100 == 2 ? con.getInputStream() : con.getErrorStream())));
        String inputLine;
        String res = "";
        while ((inputLine = in.readLine()) != null) {
            //ConsoleMessage.out(inputLine, ConsoleMessage.Type.DEBUG);
            res += inputLine;
            //return new JSONObject(inputLine).getJSONArray("actions");
        }
        in.close();
        con.disconnect();
        return new JSONObject(res).getJSONArray("actions");
    }

    /**
     * Авторизация на сайте
     * @return true/false успех авторизации
     */
    public boolean Login() throws IOException, JSONException, InterruptedException {
        String captcha = null;

        //ConsoleMessage.out("CAPTCHA_TOKEN: " + captcha, ConsoleMessage.Type.DEBUG);
        if(Settings.CAPTCHA_TYPE == 1){
            captcha = Utils.GetCapthcaToken();
            captcha = Utils.SolveCaptcha(captcha, this.login);
        }else{
            captcha = Utils.SolveCaptchaV2(this.login);
        }

        //ConsoleMessage.out(captcha, ConsoleMessage.Type.SUCCESS);

        if(captcha == null){
            return false;
        }

        Process p = Runtime.getRuntime().exec(String.format("py %s\\cloudflare.py WaxWalletLogin %s %s %s %s",System.getProperty("user.dir"), this.login, this.password, this.fa2token, captcha));
        p.waitFor();

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));

        String s = null;
        String res = "";
        while ((s = stdInput.readLine()) != null) {
            ConsoleMessage.out(s, ConsoleMessage.Type.DEBUG);
            res = s;
        }

        ConsoleMessage.out(res, ConsoleMessage.Type.DEBUG);
        JSONObject sj = new JSONObject(res);

        try {
            setToken(sj.getString("token"));
            ConsoleMessage.out("TOKEN: " + this.token, ConsoleMessage.Type.DEBUG);
        }catch (Exception e){
            return false;
        }

        return true;

        /*url = new URL("https://all-access.wax.io/api/session");
        con = (HttpURLConnection) url.openConnection();
        con.setInstanceFollowRedirects(true);
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36 Edg/90.0.818.51");
        con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");*/
        //con.setRequestProperty("Accept", "application/json, text/plain, */*");
        /*con.setRequestProperty("Origin", "https://all-access.wax.io");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        String s = "{\"password\":\"" + this.password + "\",\"username\":\"" + this.login + "\",\"g-recaptcha-response\":\"" + captcha + "\",\"redirectTo\":\"\"}";
        ConsoleMessage.out(s, ConsoleMessage.Type.DEBUG);
        out.writeBytes(s);
        out.flush();
        out.close();

        in = new BufferedReader(
                new BufferedReader(new InputStreamReader(con.getResponseCode() / 100 == 2 ? con.getInputStream() : con.getErrorStream())));
        while ((inputLine = in.readLine()) != null) {
                ConsoleMessage.out(inputLine, ConsoleMessage.Type.DEBUG);
                try{
                    token2fa = new JSONObject(inputLine).get("token2fa").toString();
                }catch (Exception e){
                    ConsoleMessage.out(e.getMessage(), ConsoleMessage.Type.ERROR);
                    return false;
                }
        }
        in.close();
        ConsoleMessage.out("TOKEN2FA: " + token2fa, ConsoleMessage.Type.INFO);

        //ConsoleMessage.out("ResponseCode2: " + con.getResponseCode(), ConsoleMessage.Type.DEBUG);
        con.disconnect();


        url = new URL("https://all-access.wax.io/api/session/2fa");
        con = (HttpURLConnection) url.openConnection();
        con.setInstanceFollowRedirects(true);
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");*/
        //con.setRequestProperty("Accept", "application/json, text/plain, */*");
        /*con.setRequestMethod("POST");
        con.setDoOutput(true);
        out = new DataOutputStream(con.getOutputStream());
        String code = Utils.getTOTPCode(this.fa2token);
        ConsoleMessage.out("2FACODE: " + code, ConsoleMessage.Type.DEBUG);
        s = "{\"code\":\"" + code + "\",\"token2fa\":\"" + token2fa + "\"}";
        ConsoleMessage.out(s, ConsoleMessage.Type.DEBUG);
        out.writeBytes(s);
        out.flush();
        out.close();

        in = new BufferedReader(
                new BufferedReader(new InputStreamReader(con.getResponseCode() / 100 == 2 ? con.getInputStream() : con.getErrorStream())));
        while ((inputLine = in.readLine()) != null) {
            ConsoleMessage.out(inputLine, ConsoleMessage.Type.DEBUG);
        }
        in.close();
        //ConsoleMessage.out("ResponseCode3: " + con.getResponseCode(), ConsoleMessage.Type.DEBUG);
        con.disconnect();


        url = new URL("https://all-access.wax.io/api/session");
        con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestMethod("GET");
        //ConsoleMessage.out("ResponseCode4: " + con.getResponseCode(), ConsoleMessage.Type.DEBUG);
        if(con.getResponseCode() / 100 == 2){
            ConsoleMessage.out("Success OAuth", ConsoleMessage.Type.SUCCESS);
        }else{
            ConsoleMessage.out("Error OAuth WAX", ConsoleMessage.Type.ERROR);
        }
        in = new BufferedReader(
                new BufferedReader(new InputStreamReader(con.getResponseCode() / 100 == 2 ? con.getInputStream() : con.getErrorStream())));
        while ((inputLine = in.readLine()) != null) {
            if(con.getResponseCode() / 100 == 2){
                JSONObject info = new JSONObject(inputLine);
                ConsoleMessage.out("EMAIL: " + info.get("email").toString(), ConsoleMessage.Type.INFO);
                ConsoleMessage.out("TOKEN: " + info.get("token").toString(), ConsoleMessage.Type.INFO);
                this.token = info.get("token").toString();
            }else{
                ConsoleMessage.out(inputLine, ConsoleMessage.Type.ERROR);
            }
        }
        in.close();
        con.disconnect();
        return true;*/
    }
}
