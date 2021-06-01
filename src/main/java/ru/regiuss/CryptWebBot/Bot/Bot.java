package ru.regiuss.CryptWebBot.Bot;

import ru.regiuss.CryptWebBot.Models.*;
import ru.regiuss.CryptWebBot.Configurations.*;
import ru.regiuss.CryptWebBot.Utils.*;
import java.io.*;
import org.json.*;

public class Bot extends Thread
{
    WaxAccount waxAccount;
    boolean RUNED;
    
    public Bot(final WaxAccount waxAccount) {
        this.RUNED = false;
        this.waxAccount = waxAccount;
    }
    
    @Override
    public void run() {
        int tryCount = 1;
        int waitMin = Settings.WAIT_LOGIN_WAX_ACCOUNT;
        while (true) {
            while (tryCount < Settings.LOGIN_WAX_ACCOUNT_COUNT) {
                ConsoleMessage.out("TRY LOGIN WAX WALLET " + tryCount, ConsoleMessage.Type.DEBUG, this.waxAccount.getLogin());
                try {
                    if (!this.waxAccount.Login()) {
                        throw new Exception("NoLoginAccount");
                    }
                }
                catch (Exception e) {
                    if (tryCount % 5 == 0) {
                        waitMin = Settings.WAIT_LOGIN_WAX_ACCOUNT_5;
                    }
                    ConsoleMessage.out("\u041e\u0448\u0438\u0431\u043a\u0430 \u043f\u0440\u0438 \u0432\u0445\u043e\u0434\u0435 \u0432 WAX WALLET ACCOUNT, \u0441\u043b\u0435\u0434\u0443\u044e\u0449\u0430\u044f \u043f\u043e\u043f\u044b\u0442\u043a\u0430 \u0447\u0435\u0440\u0435\u0437 " + waitMin + " \u0441\u0435\u043a.", ConsoleMessage.Type.INFO, this.waxAccount.getLogin());
                    try {
                        Thread.sleep(waitMin * 1000L);
                    }
                    catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    waitMin = Settings.WAIT_LOGIN_WAX_ACCOUNT;
                    ++tryCount;
                    continue;
                }
                try {
                    final JSONObject userAccountJSON = AlienWorldsAPI.GetUserInfo(this.waxAccount.getToken());
                    this.waxAccount.setAccount_name(userAccountJSON.getString("userAccount"));
                    this.waxAccount.setPubKeys(userAccountJSON.getJSONArray("pubKeys"));
                }
                catch (Exception e) {
                    ConsoleMessage.out("\u041e\u0448\u0438\u0431\u043a\u0430 \u043f\u0440\u0438 \u043f\u043e\u043b\u0443\u0447\u0435\u043d\u0438\u0438 \u0438\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u0438 \u043e alienworlds \u0430\u043a\u043a\u0430\u0443\u043d\u0442\u0435. \u0411\u043e\u0442 \u043e\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d", ConsoleMessage.Type.ERROR);
                    e.printStackTrace();
                }
                ConsoleMessage.out("\u0423\u0441\u043f\u0435\u0448\u043d\u044b\u0439 \u0432\u0445\u043e\u0434 \u0432 WAX WALLET ACCOUNT", ConsoleMessage.Type.SUCCESS, this.waxAccount.getAccountName());
                this.waxAccount.setAccountNameArray(Utils.getArrayName(this.waxAccount.getAccountName()));
                final BotWorker work = new BotWorker(this);
                this.RUNED = true;
                while (this.RUNED) {
                    try {
                        work.run();
                    }
                    catch (IOException | JSONException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return;
            }
            ConsoleMessage.out("\u041f\u0440\u0435\u0432\u044b\u0448\u0435\u043d \u043b\u0438\u043c\u0438\u0442 \u043f\u043e\u043f\u044b\u0442\u043e\u043a \u0434\u043b\u044f \u0432\u0445\u043e\u0434\u0430 \u0432 Wax Account, \u0431\u043e\u0442 \u043e\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d", ConsoleMessage.Type.ERROR);
            continue;
        }
    }
    
    public WaxAccount getWaxAccount() {
        return this.waxAccount;
    }
}
