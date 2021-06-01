package ru.regiuss.CryptWebBot.Utils;

import java.net.*;
import java.io.*;
import org.json.*;
import java.text.*;
import java.util.*;

public class AlienWorldsAPI
{
    public static JSONObject GetUserInfo(final String token) throws IOException, JSONException, InterruptedException {
        final Process p = Runtime.getRuntime().exec(String.format("py %s\\cloudflare.py GetUserInfo %s", System.getProperty("user.dir"), token));
        p.waitFor();
        final BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String res = "";
        String s;
        while ((s = stdInput.readLine()) != null) {
            res = s;
        }
        ConsoleMessage.out(res, ConsoleMessage.Type.DEBUG);
        return new JSONObject(res);
    }
    
    public static String GetUserBalance(final String userAccount) throws IOException, JSONException {
        final URL url = new URL("https://api.waxsweden.org/v1/chain/get_table_rows");
        final HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty("user-agent", "Mozilla/5.0");
        con.setRequestProperty("origin", "https://play.alienworlds.io");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        final DataOutputStream out = new DataOutputStream(con.getOutputStream());
        final JSONObject data = new JSONObject();
        data.put("code", (Object)"alien.worlds");
        data.put("index_position", 1);
        data.put("json", true);
        data.put("key_type", (Object)"");
        data.put("limit", 1);
        data.put("lower_bound", (Object)"");
        data.put("reverse", false);
        data.put("scope", (Object)userAccount);
        data.put("show_payer", false);
        data.put("table", (Object)"accounts");
        data.put("table_key", (Object)"");
        data.put("upper_bound", (Object)"");
        out.writeBytes(data.toString());
        out.flush();
        out.close();
        final BufferedReader in = new BufferedReader(new BufferedReader(new InputStreamReader((con.getResponseCode() / 100 == 2) ? con.getInputStream() : con.getErrorStream())));
        final String inputLine;
        if ((inputLine = in.readLine()) != null) {
            ConsoleMessage.out(inputLine, ConsoleMessage.Type.DEBUG);
            try {
                return new JSONObject(inputLine).getJSONArray("rows").getJSONObject(0).getString("balance");
            }
            catch (Exception e) {
                return null;
            }
        }
        in.close();
        con.disconnect();
        return null;
    }
    
    public static JSONObject GetBlock(final int block_id) throws IOException, JSONException {
        final URL url = new URL("https://api.waxsweden.org/v1/chain/get_block");
        final HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty("user-agent", "Mozilla/5.0");
        con.setRequestProperty("origin", "https://play.alienworlds.io");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        final DataOutputStream out = new DataOutputStream(con.getOutputStream());
        final JSONObject data = new JSONObject();
        data.put("block_num_or_id", block_id);
        out.writeBytes(data.toString());
        out.flush();
        out.close();
        final BufferedReader in = new BufferedReader(new BufferedReader(new InputStreamReader((con.getResponseCode() / 100 == 2) ? con.getInputStream() : con.getErrorStream())));
        final String inputLine;
        if ((inputLine = in.readLine()) != null) {
            ConsoleMessage.out((inputLine.length() > 200) ? inputLine.substring(0, 200) : inputLine, ConsoleMessage.Type.DEBUG);
            return new JSONObject(inputLine);
        }
        in.close();
        con.disconnect();
        return null;
    }
    
    public static JSONObject GetInfo() throws IOException, JSONException {
        final URL url = new URL("https://api.waxsweden.org/v1/chain/get_info");
        final HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty("user-agent", "Mozilla/5.0");
        con.setRequestProperty("origin", "https://play.alienworlds.io");
        con.setRequestMethod("POST");
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
    
    public static JSONObject GetUserLastMine(final String userAccount) throws IOException, JSONException {
        final URL url = new URL("https://api.waxsweden.org/v1/chain/get_table_rows");
        final HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty("user-agent", "Mozilla/5.0");
        con.setRequestProperty("origin", "https://play.alienworlds.io");
        con.setRequestMethod("POST");
        con.setConnectTimeout(5000);
        con.setDoOutput(true);
        final DataOutputStream out = new DataOutputStream(con.getOutputStream());
        final JSONObject data = new JSONObject();
        data.put("code", (Object)"m.federation");
        data.put("index_position", 1);
        data.put("json", true);
        data.put("key_type", (Object)"");
        data.put("limit", 10);
        data.put("lower_bound", (Object)userAccount);
        data.put("reverse", false);
        data.put("scope", (Object)"m.federation");
        data.put("show_payer", false);
        data.put("table", (Object)"miners");
        data.put("table_key", (Object)"");
        data.put("upper_bound", (Object)userAccount);
        out.writeBytes(data.toString());
        out.flush();
        out.close();
        final BufferedReader in = new BufferedReader(new BufferedReader(new InputStreamReader((con.getResponseCode() / 100 == 2) ? con.getInputStream() : con.getErrorStream())));
        final String inputLine;
        if ((inputLine = in.readLine()) != null) {
            ConsoleMessage.out(inputLine, ConsoleMessage.Type.DEBUG);
            try {
                return new JSONObject(inputLine).getJSONArray("rows").getJSONObject(0);
            }
            catch (Exception e) {
                return null;
            }
        }
        in.close();
        con.disconnect();
        return null;
    }
    
    public static JSONObject getBagMiningParams(final JSONArray bag) throws JSONException {
        final JSONObject mining_params = new JSONObject();
        int delay = 0;
        int difficulty = 0;
        int ease = 0;
        int min_delay = 65535;
        for (int b = 0; b < bag.length(); ++b) {
            if (bag.getJSONObject(b).getInt("delay") < min_delay) {
                min_delay = bag.getJSONObject(b).getInt("delay");
            }
            delay += bag.getJSONObject(b).getInt("delay");
            difficulty += bag.getJSONObject(b).getInt("difficulty");
            ease += bag.getJSONObject(b).getInt("ease") / 10;
        }
        if (bag.length() == 2) {
            delay -= min_delay / 2;
        }
        else if (bag.length() == 3) {
            delay -= min_delay;
        }
        mining_params.put("delay", delay);
        mining_params.put("difficulty", difficulty);
        mining_params.put("ease", ease);
        return mining_params;
    }
    
    public static JSONObject PushTransaction(final String packed_trx, final JSONArray signatures) throws IOException, JSONException {
        final URL url = new URL("https://api.waxsweden.org/v1/chain/push_transaction");
        final HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty("user-agent", "Mozilla/5.0");
        con.setRequestProperty("origin", "https://play.alienworlds.io");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        final DataOutputStream out = new DataOutputStream(con.getOutputStream());
        final JSONObject data = new JSONObject();
        data.put("compression", 0);
        data.put("packed_context_free_data", (Object)"");
        data.put("packed_trx", (Object)packed_trx);
        data.put("signatures", (Object)signatures);
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
    
    public static JSONArray GetInventory(final String account_name) throws IOException, JSONException {
        final URL url = new URL("https://api.waxsweden.org/v1/chain/get_table_rows");
        final HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty("user-agent", "Mozilla/5.0");
        con.setRequestProperty("origin", "https://play.alienworlds.io");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        final DataOutputStream out = new DataOutputStream(con.getOutputStream());
        final JSONObject data = new JSONObject();
        data.put("json", true);
        data.put("code", (Object)"m.federation");
        data.put("scope", (Object)"m.federation");
        data.put("table", (Object)"bags");
        data.put("table_key", (Object)"");
        data.put("lower_bound", (Object)account_name);
        data.put("upper_bound", (Object)account_name);
        data.put("index_position", 1);
        data.put("key_type", (Object)"");
        data.put("limit", 10);
        data.put("reverse", false);
        data.put("show_payer", false);
        out.writeBytes(data.toString());
        out.flush();
        out.close();
        final BufferedReader in = new BufferedReader(new BufferedReader(new InputStreamReader((con.getResponseCode() / 100 == 2) ? con.getInputStream() : con.getErrorStream())));
        final String inputLine;
        if ((inputLine = in.readLine()) != null) {
            ConsoleMessage.out("GetInventory: " + inputLine, ConsoleMessage.Type.DEBUG);
            return new JSONObject(inputLine).getJSONArray("rows").getJSONObject(0).getJSONArray("items");
        }
        in.close();
        con.disconnect();
        return null;
    }
    
    public static String GetCurrentLand(final String account_name) throws IOException, JSONException {
        final URL url = new URL("https://api.waxsweden.org/v1/chain/get_table_rows");
        final HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty("user-agent", "Mozilla/5.0");
        con.setRequestProperty("origin", "https://play.alienworlds.io");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        final DataOutputStream out = new DataOutputStream(con.getOutputStream());
        final JSONObject data = new JSONObject();
        data.put("json", true);
        data.put("code", (Object)"m.federation");
        data.put("scope", (Object)"m.federation");
        data.put("table", (Object)"miners");
        data.put("table_key", (Object)"");
        data.put("lower_bound", (Object)account_name);
        data.put("upper_bound", (Object)account_name);
        data.put("index_position", 1);
        data.put("key_type", (Object)"");
        data.put("limit", 10);
        data.put("reverse", false);
        data.put("show_payer", false);
        out.writeBytes(data.toString());
        out.flush();
        out.close();
        final BufferedReader in = new BufferedReader(new BufferedReader(new InputStreamReader((con.getResponseCode() / 100 == 2) ? con.getInputStream() : con.getErrorStream())));
        final String inputLine;
        if ((inputLine = in.readLine()) != null) {
            ConsoleMessage.out("GetCurrentLand: " + inputLine, ConsoleMessage.Type.DEBUG);
            return new JSONObject(inputLine).getJSONArray("rows").getJSONObject(0).getString("current_land");
        }
        in.close();
        con.disconnect();
        return null;
    }
    
    public static JSONObject GetItemInfo(final String id) throws IOException, JSONException {
        final URL url = new URL("https://wax.api.atomicassets.io/atomicassets/v1/assets/" + id);
        final HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty("user-agent", "Mozilla/5.0");
        con.setRequestProperty("origin", "https://play.alienworlds.io");
        con.setRequestMethod("POST");
        final BufferedReader in = new BufferedReader(new BufferedReader(new InputStreamReader((con.getResponseCode() / 100 == 2) ? con.getInputStream() : con.getErrorStream())));
        final String inputLine;
        if ((inputLine = in.readLine()) != null) {
            ConsoleMessage.out("GetItemInfo: " + inputLine, ConsoleMessage.Type.DEBUG);
            try {
                return new JSONObject(inputLine).getJSONObject("data").getJSONObject("data");
            }
            catch (Exception e) {
                ConsoleMessage.out("GetItemInfo json error: " + e.getMessage(), ConsoleMessage.Type.DEBUG);
                return null;
            }
        }
        in.close();
        con.disconnect();
        return null;
    }
    
    public static JSONObject GetSignatures(final List<Integer> transaction, final String captcha, final String token) throws IOException, JSONException, InterruptedException {
        final Process p = Runtime.getRuntime().exec(String.format("py %s\\cloudflare.py GetSignatures %s %s %s", System.getProperty("user.dir"), transaction.toString().replace(" ", ""), captcha, token));
        p.waitFor();
        final BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String res = "";
        String s;
        while ((s = stdInput.readLine()) != null) {
            ConsoleMessage.out("GetSignatures: " + s, ConsoleMessage.Type.DEBUG);
            res = s;
        }
        ConsoleMessage.out(res, ConsoleMessage.Type.DEBUG);
        return new JSONObject(res);
    }
    
    public static void GetRequiredKeys(final JSONArray available_keys, final int block_num, final int block_prefix, final String data, final String name, final long expiration) throws IOException, JSONException {
        final JSONObject transaction = new JSONObject();
        final JSONArray actions = new JSONArray();
        final JSONObject action = new JSONObject();
        final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.000");
        final String expForm = format.format(expiration - 10800000L).replace(" ", "T");
        transaction.put("ref_block_num", block_num);
        transaction.put("ref_block_prefix", block_prefix);
        transaction.put("expiration", (Object)expForm);
        action.put("account", (Object)"m.federation");
        action.put("name", (Object)"mine");
        action.put("data", (Object)data);
        action.put("authorization", (Object)new JSONArray("[{actor: \"" + name + "\", permission: \"active\"}]"));
        actions.put((Object)action);
        transaction.put("actions", (Object)actions);
        final URL url = new URL("https://api.waxsweden.org/v1/chain/get_required_keys");
        final HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty("user-agent", "Mozilla/5.0");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("content-type", "text/plain;charset=UTF-8");
        con.setRequestProperty("origin", "https://play.alienworlds.io");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        final DataOutputStream out = new DataOutputStream(con.getOutputStream());
        final JSONObject dataSend = new JSONObject();
        dataSend.put("available_keys", (Object)available_keys);
        dataSend.put("transaction", (Object)transaction);
        out.writeBytes(dataSend.toString());
        out.flush();
        out.close();
        final BufferedReader in = new BufferedReader(new BufferedReader(new InputStreamReader((con.getResponseCode() / 100 == 2) ? con.getInputStream() : con.getErrorStream())));
        final String inputLine;
        if ((inputLine = in.readLine()) != null) {
            ConsoleMessage.out("GetRequiredKeys: " + inputLine, ConsoleMessage.Type.DEBUG);
            try {
                new JSONObject(inputLine);
            }
            catch (Exception e) {
                ConsoleMessage.out(e.getMessage(), ConsoleMessage.Type.DEBUG, name);
            }
            return;
        }
        in.close();
        con.disconnect();
    }
    
    public static List<Integer> GetTransactionArray(final List<Integer> account, final List<Integer> rand_array, final String id, final long expiration) {
        final List<Integer> res = Utils.GetArrOfTimeStamp(expiration);
        final List<Integer> blockList = Utils.GetArrOfHexFromTheEnd(id.substring(4, 8), 2);
        final List<Integer> refBlockList = Utils.HexToList(id.substring(16, 24));
        res.addAll(blockList);
        res.addAll(refBlockList);
        res.addAll(Arrays.asList(0, 0, 0, 0, 1, 48, 169, 203, 230, 170, 164, 22, 144, 0, 0, 0, 0, 0, 160, 166, 147, 1));
        res.addAll(account);
        res.addAll(Arrays.asList(0, 0, 0, 0, 168, 237, 50, 50, 17));
        res.addAll(account);
        res.add(8);
        res.addAll(rand_array);
        res.add(0);
        return res;
    }
}
