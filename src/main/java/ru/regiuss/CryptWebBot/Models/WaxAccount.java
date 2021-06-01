package ru.regiuss.CryptWebBot.Models;

import java.util.*;
import java.net.*;
import java.io.*;
import ru.regiuss.CryptWebBot.Utils.*;
import org.json.*;

public class WaxAccount
{
    protected String login;
    protected String password;
    protected String fa2token;
    protected String token;
    protected CookieManager session;
    protected JSONArray pubKeys;
    protected String accountName;
    protected List<Integer> accountNameArray;
    
    public WaxAccount(final String login, final String password, final String fa2token) {
        this.login = login;
        this.password = password;
        this.fa2token = fa2token;
        CookieHandler.setDefault(this.session = new CookieManager(null, CookiePolicy.ACCEPT_ALL));
    }
    
    public String getAccountName() {
        return this.accountName;
    }
    
    public String getLogin() {
        return this.login;
    }
    
    public List<Integer> getAccountNameArray() {
        return this.accountNameArray;
    }
    
    public void setAccountNameArray(final List<Integer> accountNameArray) {
        this.accountNameArray = accountNameArray;
    }
    
    public JSONArray getPubKeys() {
        return this.pubKeys;
    }
    
    public void setPubKeys(final JSONArray pubKeys) {
        this.pubKeys = pubKeys;
    }
    
    public void setAccount_name(final String account_name) {
        this.accountName = account_name;
    }
    
    public String getToken() {
        return this.token;
    }
    
    public void setToken(final String token) throws URISyntaxException {
        this.token = token;
        this.session.getCookieStore().add(new URI("wax.io"), new HttpCookie("session_token", this.token));
    }
    
    public JSONObject GetAccountResources() throws IOException {
        final URL url = new URL("https://wax.greymass.com/v1/chain/get_account");
        final HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty("user-agent", "Mozilla/5.0");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("origin", "https://wax.bloks.io");
        con.setRequestMethod("POST");
        con.setConnectTimeout(5000);
        con.setDoOutput(true);
        final DataOutputStream out = new DataOutputStream(con.getOutputStream());
        final JSONObject data = new JSONObject();
        data.put("account_name", (Object)this.accountName);
        out.writeBytes(data.toString());
        out.flush();
        out.close();
        final BufferedReader in = new BufferedReader(new BufferedReader(new InputStreamReader((con.getResponseCode() / 100 == 2) ? con.getInputStream() : con.getErrorStream())));
        final String inputLine;
        if ((inputLine = in.readLine()) != null) {
            ConsoleMessage.out(inputLine, ConsoleMessage.Type.DEBUG);
            return new JSONObject(inputLine);
        }
        in.close();
        con.disconnect();
        return null;
    }
    
    public JSONArray GetActions() throws IOException {
        final URL url = new URL("https://wax.greymass.com/v1/history/get_actions");
        final HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty("origin", "https://wax.bloks.io");
        con.setRequestMethod("POST");
        con.setConnectTimeout(5000);
        con.setDoOutput(true);
        final DataOutputStream out = new DataOutputStream(con.getOutputStream());
        final JSONObject data = new JSONObject();
        data.put("account_name", (Object)this.accountName);
        data.put("offset", -100);
        data.put("pos", -1);
        out.writeBytes(data.toString());
        out.flush();
        out.close();
        final BufferedReader in = new BufferedReader(new BufferedReader(new InputStreamReader((con.getResponseCode() / 100 == 2) ? con.getInputStream() : con.getErrorStream())));
        final StringBuilder res = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            res.append(inputLine);
        }
        in.close();
        con.disconnect();
        return new JSONObject(res.toString()).getJSONArray("actions");
    }
    
    public boolean Login() throws IOException, JSONException, InterruptedException {
        final String captcha = Utils.SolveCaptchaV2(this.login);
        if (captcha == null) {
            return false;
        }
        final Process p = Runtime.getRuntime().exec(String.format("py %s\\cloudflare.py WaxWalletLogin %s %s %s %s", System.getProperty("user.dir"), this.login, this.password, this.fa2token, captcha));
        p.waitFor();
        final BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        final BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        String s;
        while ((s = stdError.readLine()) != null) {
            ConsoleMessage.out(s, ConsoleMessage.Type.DEBUG);
        }
        String res = "";
        while ((s = stdInput.readLine()) != null) {
            ConsoleMessage.out(s, ConsoleMessage.Type.DEBUG);
            res = s;
        }
        ConsoleMessage.out(res, ConsoleMessage.Type.DEBUG);
        final JSONObject sj = new JSONObject(res);
        try {
            this.setToken(sj.getString("token"));
            ConsoleMessage.out("TOKEN: " + this.token, ConsoleMessage.Type.DEBUG);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
}
