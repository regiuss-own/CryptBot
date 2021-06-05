package ru.regiuss.CryptWebBot.Utils;

import java.net.*;
import ru.regiuss.CryptWebBot.Configurations.*;
import org.json.*;
import org.apache.commons.codec.binary.*;
import de.taimos.totp.*;
import com.google.common.hash.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;

public class Utils
{
    public static String SolveCaptchaV2(final String accountName) throws IOException, JSONException, InterruptedException {
        final URL url = new URL("https://api.capmonster.cloud/createTask");
        final HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("POST");
        try {
            con.setDoOutput(true);
            final DataOutputStream out = new DataOutputStream(con.getOutputStream());
            final JSONObject data = new JSONObject();
            final JSONObject task = new JSONObject();
            task.put("type", "NoCaptchaTaskProxyless");
            task.put("websiteURL", "https://all-access.wax.io/");
            task.put("websiteKey", "6LdaB7UUAAAAAD2w3lLYRQJqsoup5BsYXI2ZIpFF");
            data.put("clientKey", Settings.CAPMONSTER_API_KEY);
            data.put("task", task);
            ConsoleMessage.out("SolveCaptchaV2 createTaskParams: " + data.toString(), ConsoleMessage.Type.DEBUG);
            out.writeBytes(data.toString());
            out.flush();
            out.close();
        }
        catch (Exception e) {
            return null;
        }
        final BufferedReader in = new BufferedReader(new BufferedReader(new InputStreamReader((con.getResponseCode() / 100 == 2) ? con.getInputStream() : con.getErrorStream())));
        JSONObject response = null;
        final String inputLine;
        if ((inputLine = in.readLine()) != null) {
            ConsoleMessage.out("SolveCaptchaV2 createTaskResponse: " + inputLine, ConsoleMessage.Type.DEBUG);
            response = new JSONObject(inputLine);
        }
        in.close();
        con.disconnect();
        if (response == null) {
            return null;
        }
        if (response.getInt("errorId") != 0) {
            ConsoleMessage.out("Ошибка капчи код: " + response.toString(), ConsoleMessage.Type.ERROR, accountName);
            return null;
        }
        final int taskID = response.getInt("taskId");
        int count = 0;
        while (true) {
            response = GetCaptchaTaskResult(taskID);
            if (response == null) {
                return null;
            }
            if (response.getString("status").equals("ready")) {
                return response.getJSONObject("solution").getString("gRecaptchaResponse");
            }
            if (++count >= 120) {
                return null;
            }
            Thread.sleep(5000L);
        }
    }

    public static JSONObject GetCaptchaTaskResult(final int taskId) throws IOException, JSONException {
        final URL url = new URL("https://api.capmonster.cloud/getTaskResult/");
        final HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        final DataOutputStream out = new DataOutputStream(con.getOutputStream());
        final JSONObject data = new JSONObject();
        data.put("clientKey", (Object)Settings.CAPMONSTER_API_KEY);
        data.put("taskId", taskId);
        ConsoleMessage.out("SolveCaptchaV2 GetTaskResultParams: " + data.toString(), ConsoleMessage.Type.DEBUG);
        out.writeBytes(data.toString());
        out.flush();
        out.close();
        final BufferedReader in = new BufferedReader(new BufferedReader(new InputStreamReader((con.getResponseCode() / 100 == 2) ? con.getInputStream() : con.getErrorStream())));
        final String inputLine;
        if ((inputLine = in.readLine()) != null) {
            ConsoleMessage.out("SolveCaptchaV2 GetTaskResultResponse: " + inputLine, ConsoleMessage.Type.DEBUG);
            return new JSONObject(inputLine);
        }
        in.close();
        con.disconnect();
        return null;
    }

    public static List<Integer> GetArrOfHexFromTheEnd(final String str, final int count) {
        final List<Integer> res = new ArrayList<Integer>();
        for (int i = str.length(); i > 0; i -= 2) {
            res.add(Integer.parseInt(str.substring(i - 2, i), 16));
            if (res.size() >= count) {
                break;
            }
        }
        return res;
    }

    public static String GetReverseString(final String str, final int length) {
        final StringBuilder res = new StringBuilder();
        for (int i = str.length(); i > 0; i -= length) {
            res.append(str, i - length, i);
        }
        return res.toString();
    }

    public static List<Integer> GetArrOfTimeStamp(final long ts) {
        return HexToList(GetReverseString(Long.toHexString(ts), 2));
    }

    public static String getTOTPCode(final String secretKey) {
        final Base32 base32 = new Base32();
        final byte[] bytes = base32.decode(secretKey);
        final String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }

    public static String toHex(final List<Integer> buffer) {
        final List<String> res = new ArrayList<String>();
        for (int o : buffer) {
            if (o > 256) {
                o -= o / 256 * 256;
            }
            final StringBuilder s = new StringBuilder(Integer.toHexString(o));
            while (s.length() < 2) {
                s.insert(0, "0");
            }
            res.add(s.toString());
        }
        return String.join("", res);
    }

    public static List<Integer> HexToList(final String str) {
        final char[] strChar = str.toCharArray();
        final List<Integer> res = new ArrayList<Integer>();
        for (int i = 0; i < str.length(); i += 2) {
            final String num = strChar[i] + Character.toString(strChar[i + 1]);
            res.add(Integer.parseInt(num, 16));
        }
        return res;
    }

    public static List<Integer> getRand() {
        final List<Integer> res = new ArrayList<Integer>();
        for (int i = 0; i < 8; ++i) {
            res.add((int)Math.floor(Math.random() * 255.0));
        }
        return res;
    }

    public static int charToSymbol(final int c) {
        if (c >= 97 && c <= 122) {
            return c - 97 + 6;
        }
        if (c >= 49 && c <= 53) {
            return c - 49 + 1;
        }
        return 0;
    }

    public static String combinedToHex(final List<Integer> a) {
        final Iterator<Integer> iterator = a.iterator();
        final byte[] byteArray = new byte[24];
        int index = 0;
        while (iterator.hasNext()) {
            final Integer i = iterator.next();
            byteArray[index] = i.byteValue();
            ++index;
        }
        return Hashing.sha256().hashBytes(byteArray).toString();
    }

    public static List<Integer> getArrayName(final String s) {
        final int[] a = new int[8];
        int bit = 63;
        for (int i = 0; i < s.length(); ++i) {
            int c = charToSymbol(s.toCharArray()[i]);
            if (bit < 5) {
                c <<= 1;
            }
            for (int j = 4; j >= 0; --j) {
                if (bit >= 0) {
                    final int[] array = a;
                    final int n = (int)Math.floor(bit / 8);
                    array[n] |= (c >> j & 0x1) << bit % 8;
                    --bit;
                }
            }
        }
        final List<Integer> res = new ArrayList<Integer>();
        for (final int num : a) {
            res.add(num);
        }
        return res;
    }

    public static String CheckUpdate() throws IOException {
        final URL url = new URL("https://api.github.com/repos/ReGius-igmt/CryptBot/releases/latest");
        final HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("GET");
        final BufferedReader in = new BufferedReader(new BufferedReader(new InputStreamReader((con.getResponseCode() / 100 == 2) ? con.getInputStream() : con.getErrorStream())));
        String res = "";
        final String inputLine;
        if ((inputLine = in.readLine()) != null) {
            res += inputLine;
        }
        in.close();
        con.disconnect();
        return new JSONObject(res).getString("tag_name");
    }

    public static void SaveDefaultFilesResource() throws IOException {
        final List<String> files = Arrays.asList("cloudflare.py", "Update.jar");
        for (final String fileName : files) {
            Files.copy(Objects.requireNonNull(Utils.class.getClassLoader().getResourceAsStream(fileName)), Paths.get(fileName, new String[0]), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void ConsolePause() throws IOException, InterruptedException {
        new ProcessBuilder("cmd", "/c", "pause").inheritIO().start().waitFor();
    }
}
