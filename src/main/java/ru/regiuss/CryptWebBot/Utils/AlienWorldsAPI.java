package ru.regiuss.CryptWebBot.Utils;

import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.regiuss.CryptWebBot.Utils.ConsoleMessage;
import ru.regiuss.CryptWebBot.Utils.Utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AlienWorldsAPI {
    /**
     * Получить информацию о пользователе по токену
     */
    public static JSONObject GetUserInfo(String token) throws IOException, JSONException, InterruptedException {
        Process p = Runtime.getRuntime().exec(String.format("py %s\\cloudflare.py GetUserInfo %s",System.getProperty("user.dir"), token));
        p.waitFor();

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));

        String s = null;
        String res = "";
        while ((s = stdInput.readLine()) != null) {
            res = s;
        }

        ConsoleMessage.out(res, ConsoleMessage.Type.DEBUG);
        return new JSONObject(res);

        /*URL url = new URL("https://api-idm.wax.io/v1/accounts/auto-accept/login");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();

        con.setRequestProperty("user-agent", "Mozilla/5.0");
        con.setRequestProperty("cookie", "session_token=" + token);
        con.setRequestProperty("origin", "https://play.alienworlds.io");

        con.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(
                new BufferedReader(new InputStreamReader(con.getResponseCode() / 100 == 2 ? con.getInputStream() : con.getErrorStream())));
        String inputLine;
        if ((inputLine = in.readLine()) != null) {
            ConsoleMessage.out(inputLine, ConsoleMessage.Type.DEBUG);
            return new JSONObject(inputLine);
        }
        in.close();
        con.disconnect();
        return null;*/
    }

    /**
     * Получить баланс по названию аккаунта
     */
    public static String GetUserBalance(String userAccount) throws IOException, JSONException {
        URL url = new URL("https://api.waxsweden.org/v1/chain/get_table_rows");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty("user-agent", "Mozilla/5.0");
        con.setRequestProperty("origin", "https://play.alienworlds.io");
        con.setRequestMethod("POST");

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        JSONObject data = new JSONObject();
        data.put("code","alien.worlds");
        data.put("index_position", 1);
        data.put("json", true);
        data.put("key_type", "");
        data.put("limit", 1);
        data.put("lower_bound", "");
        data.put("reverse", false);
        data.put("scope", userAccount);
        data.put("show_payer", false);
        data.put("table", "accounts");
        data.put("table_key", "");
        data.put("upper_bound", "");
        //ConsoleMessage.out(data.toString(), ConsoleMessage.Type.DEBUG);
        out.writeBytes(data.toString());
        out.flush();
        out.close();

        BufferedReader in = new BufferedReader(
                new BufferedReader(new InputStreamReader(con.getResponseCode() / 100 == 2 ? con.getInputStream() : con.getErrorStream())));
        String inputLine;
        if ((inputLine = in.readLine()) != null) {
            ConsoleMessage.out(inputLine, ConsoleMessage.Type.DEBUG);
            try{
                return new JSONObject(inputLine).getJSONArray("rows").getJSONObject(0).getString("balance");
            }catch (Exception e){
                return null;
            }
        }
        in.close();
        con.disconnect();
        return null;
    }


    /**
     * https://api.waxsweden.org/v1/chain/get_block
     */
    public static JSONObject GetBlock(int block_id) throws IOException, JSONException {
        URL url = new URL("https://api.waxsweden.org/v1/chain/get_block");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty("user-agent", "Mozilla/5.0");
        con.setRequestProperty("origin", "https://play.alienworlds.io");
        con.setRequestMethod("POST");

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        JSONObject data = new JSONObject();
        data.put("block_num_or_id", block_id);

        //ConsoleMessage.out(data.toString(), ConsoleMessage.Type.DEBUG);
        out.writeBytes(data.toString());
        out.flush();
        out.close();

        BufferedReader in = new BufferedReader(
                new BufferedReader(new InputStreamReader(con.getResponseCode() / 100 == 2 ? con.getInputStream() : con.getErrorStream())));
        String inputLine;
        if ((inputLine = in.readLine()) != null) {
            ConsoleMessage.out(inputLine.length() > 200 ? inputLine.substring(0, 200) : inputLine, ConsoleMessage.Type.DEBUG);
            return new JSONObject(inputLine);
        }
        in.close();
        con.disconnect();
        return null;
    }

    /**
     * https://api.waxsweden.org/v1/chain/get_info
     */
    public static JSONObject GetInfo() throws IOException, JSONException {
        URL url = new URL("https://api.waxsweden.org/v1/chain/get_info");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty("user-agent", "Mozilla/5.0");
        con.setRequestProperty("origin", "https://play.alienworlds.io");
        con.setRequestMethod("POST");


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
    }

    /**
     * Получить последний майнинг по названию аккаунта
     */
    public static JSONObject GetUserLastMine(String userAccount) throws IOException, JSONException {
        URL url = new URL("https://api.waxsweden.org/v1/chain/get_table_rows");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty("user-agent", "Mozilla/5.0");
        con.setRequestProperty("origin", "https://play.alienworlds.io");
        con.setRequestMethod("POST");

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        JSONObject data = new JSONObject();
        data.put("code","m.federation");
        data.put("index_position", 1);
        data.put("json", true);
        data.put("key_type", "");
        data.put("limit", 10);
        data.put("lower_bound", userAccount);
        data.put("reverse", false);
        data.put("scope", "m.federation");
        data.put("show_payer", false);
        data.put("table", "miners");
        data.put("table_key", "");
        data.put("upper_bound", userAccount);
        //ConsoleMessage.out(data.toString(), ConsoleMessage.Type.DEBUG);
        out.writeBytes(data.toString());
        out.flush();
        out.close();

        BufferedReader in = new BufferedReader(
                new BufferedReader(new InputStreamReader(con.getResponseCode() / 100 == 2 ? con.getInputStream() : con.getErrorStream())));
        String inputLine;
        if ((inputLine = in.readLine()) != null) {
            ConsoleMessage.out(inputLine, ConsoleMessage.Type.DEBUG);
            try{
                return new JSONObject(inputLine).getJSONArray("rows").getJSONObject(0);
            }catch (Exception e){
                return null;
            }
        }
        in.close();
        con.disconnect();
        return null;
    }


    public static JSONObject getBagMiningParams(JSONArray bag) throws JSONException {
        JSONObject mining_params = new JSONObject();
        int delay = 0, difficulty = 0, ease = 0;

        int min_delay = 65535;

        for (int b=0; b < bag.length(); b++){
            if (bag.getJSONObject(b).getInt("delay") < min_delay){
                min_delay = bag.getJSONObject(b).getInt("delay");
            }
            delay += bag.getJSONObject(b).getInt("delay");
            difficulty += bag.getJSONObject(b).getInt("difficulty");
            ease += bag.getJSONObject(b).getInt("ease")/10;
        }

        if (bag.length() == 2){
            delay -= (min_delay / 2);
        }
        else if (bag.length() == 3){
            delay -= min_delay;
        }

        mining_params.put("delay", delay);
        mining_params.put("difficulty", difficulty);
        mining_params.put("ease", ease);

        return mining_params;
    }

    /**
     * Отправить транзакцию для подтверждения майнинга
     * @param packed_trx упакованная транзакция Utils.toHex();
     * @param signatures сигнатуры GetSignatures()
     * @return Информация о статусе транзакции
     * @throws IOException
     * @throws JSONException
     */
    public static JSONObject PushTransaction(String packed_trx, JSONArray signatures) throws IOException, JSONException {
        URL url = new URL("https://api.waxsweden.org/v1/chain/push_transaction");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty("user-agent", "Mozilla/5.0");
        con.setRequestProperty("origin", "https://play.alienworlds.io");
        con.setRequestMethod("POST");

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        JSONObject data = new JSONObject();
        data.put("compression",0);
        data.put("packed_context_free_data", "");
        data.put("packed_trx", packed_trx);
        data.put("signatures", signatures);
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
    }


    public static JSONArray GetInventory(String account_name) throws IOException, JSONException {
        URL url = new URL("https://api.waxsweden.org/v1/chain/get_table_rows");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty("user-agent", "Mozilla/5.0");
        con.setRequestProperty("origin", "https://play.alienworlds.io");
        con.setRequestMethod("POST");

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        JSONObject data = new JSONObject();
        data.put("json",true);
        data.put("code", "m.federation");
        data.put("scope", "m.federation");
        data.put("table", "bags");
        data.put("table_key", "");
        data.put("lower_bound", account_name);
        data.put("upper_bound", account_name);
        data.put("index_position", 1);
        data.put("key_type", "");
        data.put("limit", 10);
        data.put("reverse", false);
        data.put("show_payer", false);
        //ConsoleMessage.out(data.toString(), ConsoleMessage.Type.DEBUG);
        out.writeBytes(data.toString());
        out.flush();
        out.close();

        BufferedReader in = new BufferedReader(
                new BufferedReader(new InputStreamReader(con.getResponseCode() / 100 == 2 ? con.getInputStream() : con.getErrorStream())));
        String inputLine;
        if ((inputLine = in.readLine()) != null) {
            ConsoleMessage.out("GetInventory: " + inputLine, ConsoleMessage.Type.DEBUG);
            return new JSONObject(inputLine).getJSONArray("rows").getJSONObject(0).getJSONArray("items");
        }
        in.close();
        con.disconnect();
        return null;
    }

    public static String GetCurrentLand(String account_name) throws IOException, JSONException {
        URL url = new URL("https://api.waxsweden.org/v1/chain/get_table_rows");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty("user-agent", "Mozilla/5.0");
        con.setRequestProperty("origin", "https://play.alienworlds.io");
        con.setRequestMethod("POST");

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        JSONObject data = new JSONObject();
        data.put("json",true);
        data.put("code", "m.federation");
        data.put("scope", "m.federation");
        data.put("table", "miners");
        data.put("table_key", "");
        data.put("lower_bound", account_name);
        data.put("upper_bound", account_name);
        data.put("index_position", 1);
        data.put("key_type", "");
        data.put("limit", 10);
        data.put("reverse", false);
        data.put("show_payer", false);
        //ConsoleMessage.out(data.toString(), ConsoleMessage.Type.DEBUG);
        out.writeBytes(data.toString());
        out.flush();
        out.close();

        BufferedReader in = new BufferedReader(
                new BufferedReader(new InputStreamReader(con.getResponseCode() / 100 == 2 ? con.getInputStream() : con.getErrorStream())));
        String inputLine;
        if ((inputLine = in.readLine()) != null) {
            ConsoleMessage.out("GetCurrentLand: " + inputLine, ConsoleMessage.Type.DEBUG);
            return new JSONObject(inputLine).getJSONArray("rows").getJSONObject(0).getString("current_land");
        }
        in.close();
        con.disconnect();
        return null;
    }

    public static JSONObject GetItemInfo(String id) throws IOException, JSONException {
        URL url = new URL("https://wax.api.atomicassets.io/atomicassets/v1/assets/" + id);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty("user-agent", "Mozilla/5.0");
        con.setRequestProperty("origin", "https://play.alienworlds.io");
        con.setRequestMethod("POST");

        BufferedReader in = new BufferedReader(
                new BufferedReader(new InputStreamReader(con.getResponseCode() / 100 == 2 ? con.getInputStream() : con.getErrorStream())));
        String inputLine;
        if ((inputLine = in.readLine()) != null) {
            ConsoleMessage.out("GetItemInfo: " + inputLine, ConsoleMessage.Type.DEBUG);
            try {
                return new JSONObject(inputLine).getJSONObject("data").getJSONObject("data");
            }catch (Exception e){
                ConsoleMessage.out("GetItemInfo json error: " + e.getMessage(), ConsoleMessage.Type.DEBUG);
                return null;
            }
        }
        in.close();
        con.disconnect();
        return null;
    }

    /**
     * Получить сигнатры для отправки транзакции
     * @param transaction Транзакция
     * @param captcha Капча
     * @param token Токен пользователя
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public static JSONObject GetSignatures(List<Integer> transaction, String captcha, String token) throws IOException, JSONException, InterruptedException {
        Process p = Runtime.getRuntime().exec(String.format("py %s\\cloudflare.py GetSignatures %s %s %s",System.getProperty("user.dir"), transaction.toString().replace(" ", ""), captcha, token));
        p.waitFor();

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));

        String s = null;
        String res = "";
        while ((s = stdInput.readLine()) != null) {
            ConsoleMessage.out("GetSignatures: " + s, ConsoleMessage.Type.DEBUG);
            res = s;
        }

        ConsoleMessage.out(res, ConsoleMessage.Type.DEBUG);
        return new JSONObject(res);


        /*URL url = new URL("https://public-wax-on.wax.io/wam/sign");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty("user-agent", "Mozilla/5.0");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("content-type", "application/json;charset=UTF-8");
        con.setRequestProperty("origin", "https://all-access.wax.io");
        con.setRequestProperty("cookie", "session_token="+token);
        con.setRequestProperty("x-access-token", token);
        con.setRequestMethod("POST");

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        JSONObject data = new JSONObject();
        data.put("description","jwt is insecure");
        data.put("g-recaptcha-response", captcha);
        data.put("website", "play.alienworlds.io");
        data.put("serializedTransaction", transaction);
        ConsoleMessage.out(data.toString(), ConsoleMessage.Type.DEBUG);
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
        return null;*/
    }



    public static JSONObject GetRequiredKeys(JSONArray available_keys, int block_num, int block_prefix, String data, String name, long expiration) throws IOException, JSONException {

        JSONObject transaction = new JSONObject();
        JSONArray actions = new JSONArray();
        JSONObject action = new JSONObject();

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.000");
        String expForm = (format.format(expiration - 3*60*60*1000)).replace(" ", "T");
        //ConsoleMessage.out("expForm: " + expForm, ConsoleMessage.Type.DEBUG);

        transaction.put("ref_block_num", block_num);
        transaction.put("ref_block_prefix", block_prefix);
        transaction.put("expiration", expForm);

        action.put("account", "m.federation");
        action.put("name", "mine");
        action.put("data", data);
        action.put("authorization", new JSONArray("[{actor: \"" + name + "\", permission: \"active\"}]"));


        actions.put(action);
        transaction.put("actions", actions);


        URL url = new URL("https://api.waxsweden.org/v1/chain/get_required_keys");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty("user-agent", "Mozilla/5.0");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("content-type", "text/plain;charset=UTF-8");
        con.setRequestProperty("origin", "https://play.alienworlds.io");
        con.setRequestMethod("POST");

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        JSONObject dataSend = new JSONObject();
        dataSend.put("available_keys", available_keys);
        dataSend.put("transaction", transaction);
        //ConsoleMessage.out(dataSend.toString(), ConsoleMessage.Type.DEBUG);
        out.writeBytes(dataSend.toString());
        out.flush();
        out.close();

        BufferedReader in = new BufferedReader(
                new BufferedReader(new InputStreamReader(con.getResponseCode() / 100 == 2 ? con.getInputStream() : con.getErrorStream())));
        String inputLine;
        if ((inputLine = in.readLine()) != null) {
            ConsoleMessage.out("GetRequiredKeys: " + inputLine, ConsoleMessage.Type.DEBUG);
            return new JSONObject(inputLine);
        }
        in.close();
        con.disconnect();
        return null;
    }

    /**
     * Получить неупакованную транзакцию
     * @param account Аккаунт Utils.getArrayName()
     * @param rand_array Успешный случайный масств при майнинге Mine()
     * @param id id блокировки
     * @return
     */
    public static List<Integer> GetTransactionArray(List<Integer> account, List<Integer> rand_array, String id, long expiration){
        List<Integer> res = Utils.GetArrOfTimeStamp(expiration);
        List<Integer> blockList = Utils.GetArrOfHexFromTheEnd(id.substring(4,8), 2);
        List<Integer> refBlockList = Utils.HexToList(id.substring(16,24));

        res.addAll(blockList); //2
        res.addAll(refBlockList); //4
        res.addAll(Arrays.asList(0,0,0,0,1,48,169,203,230,170,164,22,144,0,0,0,0,0,160,166,147,1));
        res.addAll(account);
        res.addAll(Arrays.asList(0,0,0,0,168,237,50,50,17));
        res.addAll(account);
        res.add(8);
        res.addAll(rand_array);
        res.add(0);
        return res;
    }
}
