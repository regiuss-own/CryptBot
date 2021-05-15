package ru.regiuss.CryptWebBot.Utils;

import org.apache.commons.logging.impl.Log4JLogger;
import ru.regiuss.CryptWebBot.Configurations.Settings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class ConsoleMessage {
    public enum Type {
        ERROR,
        INFO,
        SUCCESS,
        DEBUG
    }

    private static Logger logger = LogManager.getLogger(ConsoleMessage.class);


    /**
     * Вывод сообщения в консоль
     * @param text Текст сообщения
     * @param type Тип сообщения ERROR, INFO, SUCCESS, DEBUG
     */
    public static void out(String text, Type type){
        out(text, type, null);
    }

    public static void out(String text, Type type, String sender){
        String outText = "";
        DateFormat format = new SimpleDateFormat("[HH:mm:ss]");
        outText += (format.format(System.currentTimeMillis()));

        String logOutText = outText;

        if(type.equals(Type.SUCCESS)){
            outText += Colors.GREEN + "[SUCCESS]";
            logOutText += "[SUCCESS]";
        }
        if(type.equals(Type.INFO)){
            outText += Colors.YELLOW + "[INFO]";
            logOutText += "[INFO]";
        }
        if(type.equals(Type.DEBUG)){
            outText += Colors.WHITE + "[DEBUG]";
            logOutText += "[DEBUG]";
        }
        if(type.equals(Type.ERROR)){
            outText += Colors.RED + "[ERROR]";
            logOutText += "[ERROR]";
        }

        if(sender != null){
            if(sender.length() > 15)sender = sender.substring(0, 15);
            outText += Colors.CYAN + "[" + sender + "]";
            logOutText += "[" + sender + "]";
        }

        outText += Colors.RESET;

        outText += " " + text;
        logOutText += " " + text;

        logger.info(logOutText);
        if(type.equals(Type.DEBUG) && !Settings.DEBUG)return;

        System.out.println(outText);
    }
}
