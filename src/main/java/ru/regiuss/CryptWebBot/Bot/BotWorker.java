package ru.regiuss.CryptWebBot.Bot;

import java.time.*;
import java.sql.*;
import ru.regiuss.CryptWebBot.Configurations.*;
import ru.regiuss.CryptWebBot.Utils.*;
import java.util.*;
import java.io.*;
import org.json.*;

public class BotWorker
{
    Bot bot;
    
    public BotWorker(final Bot bot) {
        this.bot = bot;
    }
    
    public void run() throws IOException, JSONException, InterruptedException {
        JSONArray actions;
        try {
            actions = this.bot.getWaxAccount().GetActions();
        }
        catch (Exception e) {
            ConsoleMessage.out("\u041e\u0448\u0438\u0431\u043a\u0430 \u043f\u0440\u0438 \u043f\u043e\u043b\u0443\u0447\u0435\u043d\u0438\u0438 \u043f\u043e\u0441\u0435\u0434\u043d\u0438\u0445 \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u0439, \u043f\u043e\u0432\u0442\u043e\u0440", ConsoleMessage.Type.ERROR, this.bot.getWaxAccount().getAccountName());
            ConsoleMessage.out(e.getMessage(), ConsoleMessage.Type.DEBUG, this.bot.getWaxAccount().getAccountName());
            return;
        }
        final long last_action_time = Timestamp.valueOf(LocalDateTime.parse(actions.getJSONObject(actions.length() - 1).getString("block_time"))).getTime() + 10800000L;
        while (Settings.RESOURCES_CHECK) {
            JSONObject resources = null;
            try {
                resources = this.bot.getWaxAccount().GetAccountResources();
            }
            catch (Exception e2) {
                ConsoleMessage.out("\u0420\u0435\u0441\u0443\u0440\u0441\u044b \u043d\u0435 \u043f\u043e\u043b\u0443\u0447\u0435\u043d\u044b \u043f\u043e\u0432\u0442\u043e\u0440", ConsoleMessage.Type.ERROR, this.bot.waxAccount.getAccountName());
                ConsoleMessage.out(e2.getMessage(), ConsoleMessage.Type.DEBUG, this.bot.waxAccount.getAccountName());
                return;
            }
            if (resources == null) {
                ConsoleMessage.out("\u0420\u0435\u0441\u0443\u0440\u0441\u044b \u043d\u0435 \u043f\u043e\u043b\u0443\u0447\u0435\u043d\u044b \u043f\u043e\u0432\u0442\u043e\u0440", ConsoleMessage.Type.ERROR, this.bot.waxAccount.getAccountName());
                return;
            }
            int available;
            if (System.currentTimeMillis() - last_action_time < 86400000L) {
                available = resources.getJSONObject("cpu_limit").getInt("available");
            }
            else {
                available = resources.getJSONObject("cpu_limit").getInt("max");
            }
            if (available > Settings.RESOURCES_MIN) {
                ConsoleMessage.out("\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432 CPU " + available + " > " + Settings.RESOURCES_MIN + " \u0441\u0442\u0430\u0440\u0442", ConsoleMessage.Type.SUCCESS, this.bot.waxAccount.getAccountName());
                break;
            }
            ConsoleMessage.out("\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432 CPU " + available + " < " + Settings.RESOURCES_MIN + " \u043f\u043e\u0432\u0442\u043e\u0440\u043d\u0430\u044f \u043f\u043e\u043f\u044b\u0442\u043a\u0430 \u0447\u0435\u0440\u0435\u0437 " + Settings.RESOURCES_WAIT + " \u0441\u0435\u043a.", ConsoleMessage.Type.INFO, this.bot.waxAccount.getAccountName());
            Thread.sleep(Settings.RESOURCES_WAIT * 1000L);
        }
        JSONObject last_mine;
        try {
            last_mine = AlienWorldsAPI.GetUserLastMine(this.bot.waxAccount.getAccountName());
        }
        catch (Exception e2) {
            ConsoleMessage.out("\u0418\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044f \u043e \u043f\u043e\u0441\u043b\u0435\u0434\u043d\u0435\u043c \u043c\u0430\u0439\u043d\u0438\u043d\u0433\u0435 \u043d\u0435 \u043f\u043e\u043b\u0443\u0447\u0435\u043d\u0430, \u043f\u043e\u0432\u0442\u043e\u0440...", ConsoleMessage.Type.ERROR, this.bot.getWaxAccount().getAccountName());
            ConsoleMessage.out(e2.getMessage(), ConsoleMessage.Type.DEBUG, this.bot.getWaxAccount().getAccountName());
            return;
        }
        if (last_mine == null) {
            ConsoleMessage.out("\u0418\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044f \u043e \u043f\u043e\u0441\u043b\u0435\u0434\u043d\u0435\u043c \u043c\u0430\u0439\u043d\u0438\u043d\u0433\u0435 \u043d\u0435 \u043f\u043e\u043b\u0443\u0447\u0435\u043d\u0430, \u043f\u043e\u0432\u0442\u043e\u0440...", ConsoleMessage.Type.ERROR, this.bot.waxAccount.getAccountName());
            return;
        }
        final long last_mine_time = Timestamp.valueOf(LocalDateTime.parse(last_mine.getString("last_mine"))).getTime() + 10800000L;
        int difficulty = 0;
        final JSONArray inventory = new JSONArray();
        final JSONArray invItems = AlienWorldsAPI.GetInventory(this.bot.waxAccount.getAccountName());
        if (invItems == null) {
            ConsoleMessage.out("\u0418\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044f \u043e \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u0430\u0445 \u043d\u0435 \u043f\u043e\u043b\u0443\u0447\u0435\u043d\u0430, \u043f\u043e\u0432\u0442\u043e\u0440...", ConsoleMessage.Type.ERROR, this.bot.waxAccount.getAccountName());
            return;
        }
        for (int i = 0; i < invItems.length(); ++i) {
            final JSONObject itemInfo = AlienWorldsAPI.GetItemInfo(invItems.getString(i));
            if (itemInfo == null) {
                ConsoleMessage.out("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u0438\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044e \u043f\u043e \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u0443, \u043f\u0435\u0440\u0435\u0437\u0430\u043f\u0443\u0441\u043a...", ConsoleMessage.Type.INFO, this.bot.waxAccount.getAccountName());
            }
            inventory.put((Object)itemInfo);
        }
        JSONObject bagMiningParams;
        JSONObject landInfo;
        try {
            bagMiningParams = AlienWorldsAPI.getBagMiningParams(inventory);
            landInfo = AlienWorldsAPI.GetItemInfo(AlienWorldsAPI.GetCurrentLand(this.bot.waxAccount.getAccountName()));
        }
        catch (Exception e3) {
            ConsoleMessage.out("\u0418\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044f \u043e \u043e\u0441\u0442\u0440\u043e\u0432\u0435 \u0438\u043b\u0438 \u043f\u0430\u0440\u0430\u043c\u0435\u0442\u0440\u0430\u043c \u043c\u0430\u0439\u043d\u0438\u043d\u0433\u0430 \u043d\u0435 \u043f\u043e\u043b\u0443\u0447\u0435\u043d\u0430, \u043f\u043e\u0432\u0442\u043e\u0440...", ConsoleMessage.Type.ERROR, this.bot.waxAccount.getAccountName());
            ConsoleMessage.out(e3.getMessage(), ConsoleMessage.Type.DEBUG, this.bot.waxAccount.getAccountName());
            return;
        }
        if (landInfo == null) {
            ConsoleMessage.out("\u0418\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044f \u043e \u043e\u0441\u0442\u0440\u043e\u0432\u0435 \u043d\u0435 \u043f\u043e\u043b\u0443\u0447\u0435\u043d\u0430, \u043f\u043e\u0432\u0442\u043e\u0440...", ConsoleMessage.Type.ERROR, this.bot.waxAccount.getAccountName());
            return;
        }
        difficulty += bagMiningParams.getInt("difficulty");
        final int delay = bagMiningParams.getInt("delay") * (landInfo.getInt("delay") / 10);
        difficulty += landInfo.getInt("difficulty");
        ConsoleMessage.out(System.currentTimeMillis() + " " + last_mine_time, ConsoleMessage.Type.DEBUG);
        final long wite_delay = System.currentTimeMillis() - last_mine_time;
        if (wite_delay < delay * 1000L) {
            ConsoleMessage.out("\u0414\u043e \u0441\u043b\u0435\u0434\u0443\u044e\u0449\u0435\u0433\u043e \u043c\u0430\u0439\u043d\u0438\u043d\u0433\u0430 " + wite_delay / 1000L + " \u0441\u0435\u043a.", ConsoleMessage.Type.INFO, this.bot.waxAccount.getAccountName());
            Thread.sleep(wite_delay);
        }
        ConsoleMessage.out("\u041d\u0430\u0447\u0438\u043d\u0430\u044e \u043c\u0430\u0439\u043d\u0438\u043d\u0433. \u0421\u043b\u043e\u0436\u043d\u043e\u0441\u0442\u044c: " + difficulty + ". \u041f\u0430\u0443\u0437\u0430 \u043f\u043e\u0441\u043b\u0435 \u043c\u0430\u0439\u043d\u0438\u043d\u0433\u0430: " + delay + " \u0441\u0435\u043a.", ConsoleMessage.Type.INFO, this.bot.waxAccount.getAccountName());
        final Mine main = new Mine(this.bot.waxAccount.getAccountNameArray(), difficulty, last_mine.getString("last_mine_tx"), this.bot.waxAccount.getAccountName());
        List<Integer> transaction = null;
        JSONObject signatures = null;
        for (int j = 0; j < Settings.SIGNATURES_COUNT; ++j) {
            final JSONObject info = AlienWorldsAPI.GetInfo();
            if (info == null) {
                ConsoleMessage.out("\u0418\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044f \u043e \u0441\u0435\u0440\u0432\u0435\u0440\u0435 \u043d\u0435 \u043f\u043e\u043b\u0443\u0447\u0435\u043d\u0430, \u043f\u043e\u0432\u0442\u043e\u0440...", ConsoleMessage.Type.ERROR, this.bot.waxAccount.getAccountName());
            }
            else {
                final JSONObject block = AlienWorldsAPI.GetBlock(info.getInt("head_block_num") - 3);
                if (block == null) {
                    ConsoleMessage.out("\u0418\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044f \u043e \u0431\u043b\u043e\u043a\u0435 \u043d\u0435 \u043f\u043e\u043b\u0443\u0447\u0435\u043d\u0430, \u043f\u043e\u0432\u0442\u043e\u0440...", ConsoleMessage.Type.ERROR, this.bot.waxAccount.getAccountName());
                }
                else {
                    final long tsExpiration = System.currentTimeMillis() + 900000L;
                    transaction = AlienWorldsAPI.GetTransactionArray(this.bot.waxAccount.getAccountNameArray(), main.getRand_arr(), block.getString("id"), tsExpiration / 1000L);
                    AlienWorldsAPI.GetRequiredKeys(this.bot.waxAccount.getPubKeys(), Integer.parseInt(block.getString("id").substring(4, 8), 16), block.getInt("ref_block_prefix"), Utils.toHex(transaction).substring(98, 132), this.bot.waxAccount.getAccountName(), tsExpiration);
                    ConsoleMessage.out("TRY SIGNATURES " + (j + 1), ConsoleMessage.Type.DEBUG, this.bot.waxAccount.getAccountName());
                    final String captcha = Utils.SolveCaptchaV2(this.bot.waxAccount.getAccountName());
                    if (captcha == null) {
                        ConsoleMessage.out("\u041a\u0430\u043f\u0447\u0430 \u043d\u0435 \u0440\u0430\u0441\u043f\u043e\u0437\u043d\u0430\u043d\u0430, \u043f\u043e\u0432\u0442\u043e\u0440", ConsoleMessage.Type.INFO, this.bot.waxAccount.getAccountName());
                    }
                    else {
                        signatures = AlienWorldsAPI.GetSignatures(transaction, captcha, this.bot.waxAccount.getToken());
                        try {
                            if (signatures.getString("error") == null) {
                                break;
                            }
                            ConsoleMessage.out("\u0421\u0438\u0433\u043d\u0430\u0442\u0443\u0440\u0430 \u043d\u0435 \u043f\u043e\u043b\u0443\u0447\u0435\u043d\u0430, \u043f\u043e\u0432\u0442\u043e\u0440 \u0447\u0435\u0440\u0435\u0437 " + Settings.SIGNATURES_WAIT_ON_ERROR + " \u0441\u0435\u043a.", ConsoleMessage.Type.INFO, this.bot.waxAccount.getAccountName());
                            Thread.sleep(Settings.SIGNATURES_WAIT_ON_ERROR * 1000L);
                        }
                        catch (Exception e5) {
                            ConsoleMessage.out("\u0421\u0438\u0433\u043d\u0430\u0442\u0443\u0440\u044b \u043d\u0430\u0439\u0434\u0435\u043d\u044b!", ConsoleMessage.Type.SUCCESS, this.bot.waxAccount.getAccountName());
                            break;
                        }
                    }
                }
            }
        }
        if (transaction == null) {
            ConsoleMessage.out("\u0422\u0440\u0430\u043d\u0437\u0430\u043a\u0446\u0438\u044f \u043d\u0435 \u0441\u043e\u0437\u0434\u0430\u043d\u0430, \u043f\u043e\u0432\u0442\u043e\u0440..,", ConsoleMessage.Type.ERROR, this.bot.waxAccount.getAccountName());
            return;
        }
        assert signatures != null;
        JSONObject ended;
        try {
            ended = AlienWorldsAPI.PushTransaction(Utils.toHex(transaction), signatures.getJSONArray("signatures"));
        }
        catch (Exception e4) {
            ConsoleMessage.out("\u041e\u0448\u0438\u0431\u043a\u0430 \u043f\u0440\u0438 \u043e\u0442\u043f\u0440\u0430\u0432\u043a\u0435 \u0442\u0440\u0430\u043d\u0437\u0430\u043a\u0446\u0438\u0438, \u043f\u043e\u0432\u0442\u043e\u0440", ConsoleMessage.Type.ERROR, this.bot.getWaxAccount().getAccountName());
            ConsoleMessage.out(e4.getMessage(), ConsoleMessage.Type.DEBUG, this.bot.getWaxAccount().getAccountName());
            return;
        }
        assert ended != null;
        if (ended.has("error")) {
            final JSONObject errorInfo = ended.getJSONObject("error");
            int wait_delay_error = 0;
            switch (errorInfo.getInt("code")) {
                case 3050003: {
                    wait_delay_error = Settings.MINE_TOO_SOON_ERROR;
                    ConsoleMessage.out("MINE_TOO_SOON, \u043f\u043e\u0432\u0442\u043e\u0440\u043d\u044b\u0439 \u0437\u0430\u043f\u0443\u0441\u043a \u0447\u0435\u0440\u0435\u0437 " + wait_delay_error + " \u0441\u0435\u043a.", ConsoleMessage.Type.ERROR, this.bot.waxAccount.getAccountName());
                    break;
                }
                case 3080004: {
                    wait_delay_error = Settings.MINE_WAIT_RESOURCES_ERROR;
                    ConsoleMessage.out("Transaction exceeded the current CPU usage limit imposed on the transaction, \u043f\u043e\u0432\u0442\u043e\u0440\u043d\u044b\u0439 \u0437\u0430\u043f\u0443\u0441\u043a \u0447\u0435\u0440\u0435\u0437 " + wait_delay_error + " \u0441\u0435\u043a.", ConsoleMessage.Type.ERROR, this.bot.waxAccount.getAccountName());
                    break;
                }
                case 3080001: {
                    wait_delay_error = Settings.MINE_WAIT_RESOURCES_ERROR;
                    ConsoleMessage.out("Account using more than allotted RAM usage, \u043f\u043e\u0432\u0442\u043e\u0440\u043d\u044b\u0439 \u0437\u0430\u043f\u0443\u0441\u043a \u0447\u0435\u0440\u0435\u0437 " + wait_delay_error + " \u0441\u0435\u043a.", ConsoleMessage.Type.ERROR, this.bot.waxAccount.getAccountName());
                    break;
                }
                default: {
                    wait_delay_error = Settings.MINE_OTHER_ERROR;
                    ConsoleMessage.out("\u041d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u0430\u044f \u043e\u0448\u0438\u0431\u043a\u0430 \u043f\u0440\u0438 \u043e\u0442\u043f\u0440\u0430\u0432\u043a\u0435 \u0442\u0440\u0430\u043d\u0437\u0430\u043a\u0446\u0438\u0438, \u043f\u043e\u0432\u0442\u043e\u0440\u043d\u044b\u0439 \u0437\u0430\u043f\u0443\u0441\u043a \u0447\u0435\u0440\u0435\u0437 " + wait_delay_error + " \u0441\u0435\u043a.", ConsoleMessage.Type.ERROR, this.bot.waxAccount.getAccountName());
                    break;
                }
            }
            Thread.sleep(wait_delay_error * 1000L);
            return;
        }
        ConsoleMessage.out(ended.toString(), ConsoleMessage.Type.DEBUG);
        ConsoleMessage.out("\u0411\u0430\u043b\u0430\u043d\u0441: " + AlienWorldsAPI.GetUserBalance(this.bot.waxAccount.getAccountName()), ConsoleMessage.Type.SUCCESS, this.bot.waxAccount.getAccountName());
        ConsoleMessage.out("\u0422\u0440\u0430\u043d\u0437\u0430\u043a\u0446\u0438\u044f \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u043e\u0431\u0440\u0430\u0431\u043e\u0442\u0430\u043d\u0430, \u0441\u043b\u0435\u0434\u0443\u044e\u0449\u0438\u0439 \u0437\u0430\u043f\u0443\u0441\u043a \u0447\u0435\u0440\u0435\u0437 " + delay + " \u0441\u0435\u043a.", ConsoleMessage.Type.SUCCESS, this.bot.waxAccount.getAccountName());
        Thread.sleep(delay * 1000L);
    }
}
