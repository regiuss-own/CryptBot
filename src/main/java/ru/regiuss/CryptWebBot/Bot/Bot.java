package ru.regiuss.CryptWebBot.Bot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.regiuss.CryptWebBot.Configurations.Settings;
import ru.regiuss.CryptWebBot.Models.WaxAccount;
import ru.regiuss.CryptWebBot.Utils.AlienWorldsAPI;
import ru.regiuss.CryptWebBot.Utils.ConsoleMessage;
import ru.regiuss.CryptWebBot.Utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Bot extends Thread{
    WaxAccount waxAccount;
    boolean AUTHORIZED = false;
    boolean RUNED = false;
    String alienWorldsAccountName;
    List<Integer> alienWorldsAccountArray;
    JSONArray pubKeys;

    public Bot(WaxAccount waxAccount) {
        this.waxAccount = waxAccount;
        //this.setDaemon(true);
    }

    @Override
    public void run() {
        int tryCount = 1;
        int waitMin = Settings.WAIT_LOGIN_WAX_ACCOUNT;
        while (true) {
            if(tryCount >= Settings.LOGIN_WAX_ACCOUNT_COUNT){
                ConsoleMessage.out("Превышен лимит попыток для входа в Wax Account, бот остановлен", ConsoleMessage.Type.ERROR);
                break;
            }
            ConsoleMessage.out("TRY LOGIN WAX WALLET " + tryCount, ConsoleMessage.Type.DEBUG, waxAccount.getLogin());
            try {
                if(AUTHORIZED = waxAccount.Login()){
                    break;
                }else{
                    throw new Exception("NoLoginAccount");
                }
            } catch (Exception e) {
                if(tryCount % 5 == 0)waitMin=Settings.WAIT_LOGIN_WAX_ACCOUNT_5;
                ConsoleMessage.out("Ошибка при входе в WAX WALLET ACCOUNT, следующая попытка через " + waitMin + " сек.", ConsoleMessage.Type.INFO, waxAccount.getLogin());
                try {
                    Thread.sleep(waitMin*1000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
            waitMin = Settings.WAIT_LOGIN_WAX_ACCOUNT;
            tryCount++;
        }

        //if(AUTHORIZED){
        try{
            JSONObject userAccountJSON = AlienWorldsAPI.GetUserInfo(this.waxAccount.getToken());
            this.alienWorldsAccountName = userAccountJSON.getString("userAccount");
            waxAccount.setAccount_name(this.alienWorldsAccountName);
            this.pubKeys = userAccountJSON.getJSONArray("pubKeys");
        }catch (Exception e){
            ConsoleMessage.out("Ошибка при получении информации о alienworlds аккаунте. Бот остановлен", ConsoleMessage.Type.ERROR);
            e.printStackTrace();
        }

        ConsoleMessage.out("Успешных вход в WAX WALLET ACCOUNT", ConsoleMessage.Type.SUCCESS, alienWorldsAccountName);

        alienWorldsAccountArray = Utils.getArrayName(alienWorldsAccountName);

        BotWorker work = new BotWorker(this);
        RUNED = true;
        while(RUNED){
            try {
                work.run();
            } catch (IOException | JSONException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        //}
        //else ConsoleMessage.out("BOT STOPPED, AUTHORIZATION FAILED", ConsoleMessage.Type.ERROR, waxAccount.getLogin());
    }

    public WaxAccount getWaxAccount() {
        return waxAccount;
    }

    public boolean isAUTHORIZED() {
        return AUTHORIZED;
    }

    public String getAlienWorldsAccountName() {
        return alienWorldsAccountName;
    }

    public List<Integer> getAlienWorldsAccountArray() {
        return alienWorldsAccountArray;
    }

    public JSONArray getPubKeys() {
        return pubKeys;
    }
}
