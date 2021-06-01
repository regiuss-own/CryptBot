package ru.regiuss.CryptWebBot.Bot;

import java.util.*;
import ru.regiuss.CryptWebBot.Utils.*;

public class Mine
{
    private List<Integer> rand_arr;
    private List<Integer> combined;
    private List<Integer> last_mine_arr;
    private boolean good;
    private int itr;
    private long startTime;
    private long endTime;
    
    public Mine(final List<Integer> account, final int difficulty, final String last_mine_tx, final String account_name) {
        final String last_mine_tx_s = last_mine_tx.substring(0, 16);
        this.rand_arr = new ArrayList<Integer>();
        this.combined = new ArrayList<Integer>();
        this.last_mine_arr = Utils.HexToList(last_mine_tx_s);
        this.startTime = System.currentTimeMillis();
        this.good = false;
        this.itr = 0;
        String hex_digest = "";
        int last = 0;
        ConsoleMessage.out(String.format("Performing work with difficulty %s, last tx is %s...", difficulty, last_mine_tx_s), ConsoleMessage.Type.DEBUG, account_name);
        while (!this.good) {
            this.rand_arr = Utils.getRand();
            this.combined.clear();
            this.combined.addAll(account);
            this.combined.addAll(this.last_mine_arr);
            this.combined.addAll(this.rand_arr);
            hex_digest = Utils.combinedToHex(this.combined);
            this.good = hex_digest.substring(0, 4).equals("0000");
            if (this.good) {
                last = Integer.parseInt(hex_digest.substring(4, 5), 16);
            }
            this.good &= (last <= difficulty);
            ++this.itr;
            if (this.itr % 1000000 == 0) {
                ConsoleMessage.out(String.format("\u0412\u0441\u0451 \u0435\u0449\u0451 \u0434\u043e\u0431\u044b\u0432\u0430\u044e - \u043f\u0440\u043e\u0439\u0434\u0435\u043d\u043e %s \u0438\u0442\u0435\u0440\u0430\u0446\u0438\u0439", this.itr), ConsoleMessage.Type.INFO, account_name);
            }
            if (!this.good) {
                hex_digest = "";
            }
        }
        this.endTime = System.currentTimeMillis();
        final String rand_str = Utils.toHex(this.rand_arr);
        ConsoleMessage.out(Colors.GREEN + "\u041c\u0430\u0439\u043d\u0438\u043d\u0433 \u0437\u0430\u0432\u0435\u0440\u0448\u0451\u043d - " + rand_str + ". " + this.itr + " \u0438\u0442\u0435\u0440\u0430\u0446\u0438\u0439 (" + (this.endTime - this.startTime) + " \u043c\u0441)" + Colors.RESET, ConsoleMessage.Type.SUCCESS, account_name);
    }
    
    public List<Integer> getRand_arr() {
        return this.rand_arr;
    }
    
    public List<Integer> getCombined() {
        return this.combined;
    }
    
    public List<Integer> getLast_mine_arr() {
        return this.last_mine_arr;
    }
    
    public boolean isGood() {
        return this.good;
    }
    
    public int getItr() {
        return this.itr;
    }
    
    public long getStartTime() {
        return this.startTime;
    }
    
    public long getEndTime() {
        return this.endTime;
    }
}
