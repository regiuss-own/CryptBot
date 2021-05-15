package ru.regiuss.CryptWebBot.Bot;

import ru.regiuss.CryptWebBot.Utils.ConsoleMessage;
import ru.regiuss.CryptWebBot.Utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Mine {

    private List<Integer> rand_arr;
    private List<Integer> combined;
    private List<Integer> last_mine_arr;
    private boolean good;
    private int itr;
    private long startTime;
    private long endTime;

    public Mine(List<Integer> account, int difficulty, String last_mine_tx, String account_name) {
        String last_mine_tx_s = last_mine_tx.substring(0,16);
        rand_arr = new ArrayList<>();
        combined = new ArrayList<>();
        last_mine_arr = Utils.HexToList(last_mine_tx_s);
        startTime = System.currentTimeMillis();
        good = false;
        itr = 0;
        String hex_digest = "";
        int last = 0;
        ConsoleMessage.out(String.format("Performing work with difficulty %s, last tx is %s...", difficulty, last_mine_tx_s), ConsoleMessage.Type.DEBUG, account_name);
        while (!good){
            rand_arr = Utils.getRand();
            //rand_arr = Arrays.asList(50,208,159,205,37,212,118,71);
            combined.clear();
            combined.addAll(account);
            combined.addAll(last_mine_arr);
            combined.addAll(rand_arr);

            hex_digest = Utils.combinedToHex(combined);
            //ConsoleMessage.out(hex_digest, ConsoleMessage.Type.DEBUG);

            good = hex_digest.substring(0, 4).equals("0000");

            if(good)last = Integer.parseInt(hex_digest.substring(4, 5), 16);

            good &= (last <= difficulty);
            itr++;
            if (itr % 1000000 == 0){
                ConsoleMessage.out(String.format("Still mining - tried %s iterations", itr), ConsoleMessage.Type.DEBUG,account_name);
            }

            if (!good){
                hex_digest = "";
            }
        }

        endTime = System.currentTimeMillis();
        String rand_str = Utils.toHex(rand_arr);
        ConsoleMessage.out("Майнинг завершён - " + rand_str + ". " + itr + " итераций (" + (endTime-startTime) + " мс)",
        ConsoleMessage.Type.SUCCESS, account_name);
    }

    public List<Integer> getRand_arr() {
        return rand_arr;
    }

    public List<Integer> getCombined() {
        return combined;
    }

    public List<Integer> getLast_mine_arr() {
        return last_mine_arr;
    }

    public boolean isGood() {
        return good;
    }

    public int getItr() {
        return itr;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }
}
