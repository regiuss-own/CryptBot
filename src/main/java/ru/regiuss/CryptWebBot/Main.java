package ru.regiuss.CryptWebBot;

import ru.regiuss.CryptWebBot.Configurations.*;
import org.fusesource.jansi.*;
import java.nio.file.*;
import ru.regiuss.CryptWebBot.Utils.*;
import ru.regiuss.CryptWebBot.Models.*;
import ru.regiuss.CryptWebBot.Bot.*;
import java.io.*;

public class Main
{
    public static void main(final String[] args) throws IOException, InterruptedException {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        Settings.VERSION = "1.62";
        if (args.length > 0 && args[0].equals("updated")) {
            Utils.SaveDefaultFilesResource();
        }
        AnsiConsole.systemInstall();
        System.out.println("░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░");
        System.out.println("░█████╗░██████╗░██╗░░░██╗██████╗░████████╗██████╗░░█████╗░████████╗");
        System.out.println("██╔══██╗██╔══██╗╚██╗░██╔╝██╔══██╗╚══██╔══╝██╔══██╗██╔══██╗╚══██╔══╝");
        System.out.println("██║░░╚═╝██████╔╝░╚████╔╝░██████╔╝░░░██║░░░██████╦╝██║░░██║░░░██║░░░");
        System.out.println("██║░░██╗██╔══██╗░░╚██╔╝░░██╔═══╝░░░░██║░░░██╔══██╗██║░░██║░░░██║░░░");
        System.out.println("╚█████╔╝██║░░██║░░░██║░░░██║░░░░░░░░██║░░░██████╦╝╚█████╔╝░░░██║░░░");
        System.out.println("░╚════╝░╚═╝░░╚═╝░░░╚═╝░░░╚═╝░░░░░░░░╚═╝░░░╚═════╝░░╚════╝░░░░╚═╝░░░");
        System.out.println("░░░░░░░░░░░░░░░░░░░░░░░░ " + Colors.CYAN + "V " + Settings.VERSION + " BY REGIUSS" + Colors.RESET + " ░░░░░░░░░░░░░░░░░░░░░░░░\n");
        System.out.println(Colors.YELLOW + "Настройка..." + Colors.RESET);
        try {
            Settings.LoadSettings();
        }
        catch (Exception e2) {
            System.out.println(Colors.RED + "Ошибка в файле конфигурации. Удалите текущий config.yml для создания нового" + Colors.RESET);
            Utils.ConsolePause();
            return;
        }
        System.out.println(Colors.GREEN + "Настройка завершена!");
        System.out.println(Colors.YELLOW + "Проверка наличия обновлений...");
        final String last_version = Utils.CheckUpdate();
        if (!last_version.equals(Settings.VERSION)) {
            System.out.println(Colors.YELLOW + "Доступна новая версия программы!");
            try {
                new ProcessBuilder("cmd", "/c", "start javaw -jar " + Paths.get("Update.jar", new String[0]) + " " + last_version ).inheritIO().start();
            }
            catch (Exception e3) {
                System.out.println(Colors.RED + "Ошибка при обновлении программы");
                Utils.ConsolePause();
            }
            return;
        }
        System.out.println(Colors.GREEN + "Вы используете последнюю версию!");
        System.out.println(Colors.YELLOW + "Загрузка аккаунтов..." + Colors.RESET);
        final File accounts = new File("./accounts.txt");
        FileReader fr;
        try {
            fr = new FileReader(accounts);
        }
        catch (FileNotFoundException e4) {
            ConsoleMessage.out("Файл с аккаунтами(accounts.txt) не найден", ConsoleMessage.Type.ERROR);
            Utils.ConsolePause();
            return;
        }
        final BufferedReader br = new BufferedReader(fr);
        int account_count = 0;
        try {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.toCharArray()[0] == '#') {
                    continue;
                }
                final String[] data = line.split(":");
                final Bot bot = new Bot(new WaxAccount(data[0], data[1], data[2]));
                bot.start();
                ++account_count;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Utils.ConsolePause();
        }
        System.out.println("Загружено аккаунтов: " + Colors.CYAN + account_count + Colors.RESET);
    }
}
