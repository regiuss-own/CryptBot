package ru.regiuss.CryptWebBot;

import org.fusesource.jansi.AnsiConsole;
import ru.regiuss.CryptWebBot.Bot.Bot;
import ru.regiuss.CryptWebBot.Configurations.Settings;
import ru.regiuss.CryptWebBot.Models.WaxAccount;
import ru.regiuss.CryptWebBot.Utils.Colors;
import ru.regiuss.CryptWebBot.Utils.ConsoleMessage;
import ru.regiuss.CryptWebBot.Utils.Utils;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        /*WaxAccount s = new WaxAccount("asdsa", "asdas", "sad");
        s.setAccount_name("fqlcu.wam");
        s.GetActions();

        if(true)return;*/
        AnsiConsole.systemInstall();

        System.setProperty("webdriver.chrome.silentOutput", "true");
        System.setProperty("webdriver.chrome.silentLogging", "true");
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);

        Settings.VERSION = "1.42";

        System.out.println("░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░");
        System.out.println("░█████╗░██████╗░██╗░░░██╗██████╗░████████╗██████╗░░█████╗░████████╗");
        System.out.println("██╔══██╗██╔══██╗╚██╗░██╔╝██╔══██╗╚══██╔══╝██╔══██╗██╔══██╗╚══██╔══╝");
        System.out.println("██║░░╚═╝██████╔╝░╚████╔╝░██████╔╝░░░██║░░░██████╦╝██║░░██║░░░██║░░░");
        System.out.println("██║░░██╗██╔══██╗░░╚██╔╝░░██╔═══╝░░░░██║░░░██╔══██╗██║░░██║░░░██║░░░");
        System.out.println("╚█████╔╝██║░░██║░░░██║░░░██║░░░░░░░░██║░░░██████╦╝╚█████╔╝░░░██║░░░");
        System.out.println("░╚════╝░╚═╝░░╚═╝░░░╚═╝░░░╚═╝░░░░░░░░╚═╝░░░╚═════╝░░╚════╝░░░░╚═╝░░░");
        System.out.println("░░░░░░░░░░░░░░░░░░░░░░░░ " + (Colors.CYAN) + "V " + Settings.VERSION + " BY REGIUSS" + (Colors.RESET) + " ░░░░░░░░░░░░░░░░░░░░░░░░\n");

        System.out.println(Colors.YELLOW + "Настройка..." + Colors.RESET);
        try{
            Settings.LoadSettings();
        }catch (Exception e){
            System.out.println(Colors.RED + "Ошибка в файле конфигурации. Удалите текущий config.yml для создания нового" + Colors.RESET);
            return;
        }
        System.out.println(Colors.GREEN + "Настройка завершена!");

        if(Settings.CHECK_UPDATE){
            System.out.println(Colors.YELLOW + "Проверка наличия обновлений...");
            String last_version = Utils.CheckUpdate();
            if(!last_version.equals(Settings.VERSION)){
                System.out.println(Colors.YELLOW + "Доступна новая версия программы!");
                if(Settings.AUTO_UPDATE){
                    try{
                        Runtime.getRuntime().exec("cmd /c start cmd /k java -jar " + Paths.get("Update.jar") + " " + last_version);
                    }catch (Exception e){
                        System.out.println(Colors.RED + "Ошибка при обновлении программы");
                    }
                    return;
                }
            }
            System.out.println(Colors.GREEN + "Вы используете последнюю версию!");
        }
        System.out.println(Colors.YELLOW + "Загрузка аккаунтов..." + Colors.RESET);

        File accounts = new File("./accounts.txt");
        FileReader fr;
        try {
            fr = new FileReader(accounts);
        } catch (FileNotFoundException e) {
            ConsoleMessage.out("Файл с аккаунтами(accounts.txt) не найден", ConsoleMessage.Type.ERROR);
            return;
        }
        BufferedReader br = new BufferedReader(fr);
        String line;
        int account_count = 0;
        List<Bot> bots = new ArrayList<>();
        try {
            while ((line = br.readLine()) != null){
                String[] data = line.split(":");
                //ConsoleMessage.out(line, ConsoleMessage.Type.DEBUG);
                Bot bot = new Bot(new WaxAccount(data[0], data[1],data[2]));
                bots.add(bot);
                bot.start();
                //new WaxAccount(data[0], data[1],data[2]).Login();
                account_count++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Загружено аккаунтов: " + Colors.CYAN + account_count + Colors.RESET);
    }
}