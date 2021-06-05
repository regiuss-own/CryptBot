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
                    ConsoleMessage.out("Ошибка при входе в WAX WALLET ACCOUNT, следующая попытка через " + waitMin + " сек.", ConsoleMessage.Type.INFO, this.waxAccount.getLogin());
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
                    ConsoleMessage.out("Ошибка при получении информации о alienworlds аккаунте. Бот остановлен", ConsoleMessage.Type.ERROR);
                    e.printStackTrace();
                }
                ConsoleMessage.out("Успешный вход в WAX WALLET ACCOUNT", ConsoleMessage.Type.SUCCESS, this.waxAccount.getAccountName());
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
            ConsoleMessage.out("Превышен лимит попыток для входа в Wax Account, бот остановлен", ConsoleMessage.Type.ERROR);
            continue;
        }
    }

    public WaxAccount getWaxAccount() {
        return this.waxAccount;
    }
}
