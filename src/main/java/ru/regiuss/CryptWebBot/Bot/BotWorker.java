package ru.regiuss.CryptWebBot.Bot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.regiuss.CryptWebBot.Configurations.Settings;
import ru.regiuss.CryptWebBot.Utils.AlienWorldsAPI;
import ru.regiuss.CryptWebBot.Utils.Colors;
import ru.regiuss.CryptWebBot.Utils.ConsoleMessage;
import ru.regiuss.CryptWebBot.Utils.Utils;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class BotWorker {
    Bot bot;

    public BotWorker(Bot bot) {
        this.bot = bot;
    }

    public void run() throws IOException, JSONException, InterruptedException {
        int available = 0;
        JSONArray actions = bot.getWaxAccount().GetActions();
        long last_action_time = Timestamp.valueOf(LocalDateTime.parse(actions.getJSONObject(actions.length()-1).getString("block_time"))).getTime() + (3 * 60 * 60 * 1000);
        while (Settings.RESOURCES_CHECK){
            JSONObject resources = bot.getWaxAccount().GetAccountResources();
            if(resources == null){
                ConsoleMessage.out("Ресурсы не получены повтор", ConsoleMessage.Type.ERROR, bot.alienWorldsAccountName);
                return;
            }

            if(System.currentTimeMillis() - last_action_time < 24*60*60*1000){
                available = resources.getJSONObject("cpu_limit").getInt("available");
            }else{
                available = resources.getJSONObject("cpu_limit").getInt("max");
            }

            if(available > Settings.RESOURCES_MIN){
                ConsoleMessage.out("Количество ресурсов CPU " + available + " > " + Settings.RESOURCES_MIN + " старт", ConsoleMessage.Type.SUCCESS, bot.getAlienWorldsAccountName());
                break;
            }
            ConsoleMessage.out("Количество ресурсов CPU " + available + " < "+ Settings.RESOURCES_MIN + " повторная попытка через " + Settings.RESOURCES_WAIT + " сек.", ConsoleMessage.Type.INFO, bot.getAlienWorldsAccountName());
            Thread.sleep(Settings.RESOURCES_WAIT * 1000);
        }

        JSONObject last_mine = AlienWorldsAPI.GetUserLastMine(bot.getAlienWorldsAccountName());
        if(last_mine == null){
            ConsoleMessage.out("Информация о последнем майнинге не получена, повтор...", ConsoleMessage.Type.ERROR, bot.alienWorldsAccountName);
            return;
        }
        long last_mine_time = Timestamp.valueOf(LocalDateTime.parse(last_mine.getString("last_mine"))).getTime() + (3 * 60 * 60 * 1000);
        int difficulty = 0;

        JSONArray inventory = new JSONArray();
        JSONArray invItems = AlienWorldsAPI.GetInventory(bot.getAlienWorldsAccountName());
        for (int i = 0; i < invItems.length(); i++) {
            inventory.put(AlienWorldsAPI.GetItemInfo(invItems.getString(i)));
            if(inventory.get(i) == null){
                ConsoleMessage.out("Не удалось получить информацию по предмету, перезапуск...", ConsoleMessage.Type.INFO, bot.getAlienWorldsAccountName());
            }

        }
        JSONObject bagMiningParams = AlienWorldsAPI.getBagMiningParams(inventory);
        JSONObject landInfo = AlienWorldsAPI.GetItemInfo(AlienWorldsAPI.GetCurrentLand(bot.getAlienWorldsAccountName()));

        difficulty += bagMiningParams.getInt("difficulty");
        int delay = bagMiningParams.getInt("delay") * (landInfo.getInt("delay") / 10);

        difficulty += landInfo.getInt("difficulty");

        ConsoleMessage.out(System.currentTimeMillis() + " " + last_mine_time, ConsoleMessage.Type.DEBUG);
        long wite_delay = System.currentTimeMillis() - last_mine_time;
        if(wite_delay < delay*1000){
            ConsoleMessage.out("До следующего майнинга " + (wite_delay/1000) + " сек.", ConsoleMessage.Type.INFO, bot.getAlienWorldsAccountName());
            Thread.sleep(wite_delay);
        }

        ConsoleMessage.out("Начинаю майнинг. Сложность: " + difficulty + ". Пауза после майнинга: " + delay + " сек.", ConsoleMessage.Type.INFO, bot.getAlienWorldsAccountName());
        Mine main = new Mine(bot.alienWorldsAccountArray, difficulty, last_mine.getString("last_mine_tx"), bot.getAlienWorldsAccountName());

        List<Integer> transaction = null;

        JSONObject signatures = null;
        JSONObject info = null;
        JSONObject block = null;
        long tsExpiration = 0;
        for (int i = 0; i < Settings.SIGNATURES_COUNT; i++) {

            info = AlienWorldsAPI.GetInfo();

            block = AlienWorldsAPI.GetBlock(info.getInt("head_block_num")-3);

            tsExpiration = System.currentTimeMillis() + 900000;
            transaction = AlienWorldsAPI.GetTransactionArray(bot.alienWorldsAccountArray, main.getRand_arr(), block.getString("id"), tsExpiration/1000);

            AlienWorldsAPI.GetRequiredKeys(
                    bot.getPubKeys(),
                    Integer.parseInt(block.getString("id").substring(4, 8), 16),
                    block.getInt("ref_block_prefix"),
                    Utils.toHex(transaction).substring(98, 132),
                    bot.getAlienWorldsAccountName(),
                    tsExpiration
            );

            ConsoleMessage.out("TRY SIGNATURES " + (i+1), ConsoleMessage.Type.DEBUG, bot.alienWorldsAccountName);
            String captcha = null;
            if(Settings.CAPTCHA_TYPE == 1){
                captcha = Utils.SolveCaptcha(Utils.GetCapthcaToken(), bot.alienWorldsAccountName);
            }else{
                captcha = Utils.SolveCaptchaV2(bot.alienWorldsAccountName);
            }
            if(captcha == null){
                ConsoleMessage.out("Капча не распознана, повтор", ConsoleMessage.Type.INFO, bot.alienWorldsAccountName);
                continue;
            }
            signatures = AlienWorldsAPI.GetSignatures(transaction, captcha, bot.waxAccount.getToken());
            try{
                if(signatures != null && signatures.getString("error") == null)break;
                else{
                    ConsoleMessage.out("Сигнатура не получена, повтор через " + Settings.SIGNATURES_WAIT_ON_ERROR + " сек.", ConsoleMessage.Type.INFO, bot.alienWorldsAccountName);
                    Thread.sleep(Settings.SIGNATURES_WAIT_ON_ERROR*1000);
                }
            }catch (Exception e){
                ConsoleMessage.out("Сигнатуры найдены!", ConsoleMessage.Type.SUCCESS, bot.alienWorldsAccountName);
                break;
            }
        }
        //bot.getWaxAccount().GetAccountResources();
        JSONObject ended = AlienWorldsAPI.PushTransaction(Utils.toHex(transaction), signatures.getJSONArray("signatures"));
        if(ended.has("error")){
            JSONObject errorInfo = ended.getJSONObject("error");
            int wait_delay_error;
            switch (errorInfo.getInt("code")){
                case(3050003):
                    wait_delay_error = Settings.MINE_TOO_SOON_ERROR;
                    ConsoleMessage.out("MINE_TOO_SOON, повторный запуск через " + wait_delay_error+ " сек.", ConsoleMessage.Type.ERROR, bot.getAlienWorldsAccountName());
                    break;
                case(3080004):
                    wait_delay_error = Settings.MINE_WAIT_RESOURCES_ERROR;
                    ConsoleMessage.out("Transaction exceeded the current CPU usage limit imposed on the transaction, повторный запуск через " + wait_delay_error+ " сек.", ConsoleMessage.Type.ERROR, bot.getAlienWorldsAccountName());
                    break;
                default:
                    wait_delay_error = Settings.MINE_OTHER_ERROR;
                    ConsoleMessage.out("Неизвестная ошибка при отправуе транзакции, повторный запуск через " + wait_delay_error+ " сек.", ConsoleMessage.Type.ERROR, bot.getAlienWorldsAccountName());
                    break;
            }
            Thread.sleep(wait_delay_error*1000);
            return;
        }

        ConsoleMessage.out(ended.toString(), ConsoleMessage.Type.DEBUG);
        ConsoleMessage.out("Баланс: " + AlienWorldsAPI.GetUserBalance(bot.getAlienWorldsAccountName()), ConsoleMessage.Type.SUCCESS, bot.getAlienWorldsAccountName());
        ConsoleMessage.out("Транзакция успешно обработана, следующий запуск через " + delay + " сек.", ConsoleMessage.Type.SUCCESS, bot.getAlienWorldsAccountName());
        Thread.sleep(delay*1000);
    }
}
