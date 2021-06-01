package ru.regiuss.CryptWebBot.Utils;

import ru.regiuss.CryptWebBot.Configurations.*;
import java.text.*;
import org.apache.logging.log4j.*;

public class ConsoleMessage
{
    private static final Logger logger;
    
    public static void out(final String text, final Type type) {
        out(text, type, null);
    }
    
    public static void out(final String text, final Type type, String sender) {
        String outText = "";
        final DateFormat format = new SimpleDateFormat("[HH:mm:ss]");
        String logOutText;
        outText = (logOutText = outText + format.format(System.currentTimeMillis()));
        if (sender != null) {
            if (sender.length() > 15) {
                sender = sender.substring(0, 15);
            }
            outText = outText + Colors.CYAN + "[" + sender + "]";
            logOutText = logOutText + "[" + sender + "]";
        }
        if (type.equals(Type.SUCCESS)) {
            outText = outText + Colors.GREEN + "[SUC]";
            logOutText += "[SUCCESS]";
        }
        if (type.equals(Type.INFO)) {
            outText = outText + Colors.YELLOW + "[INF]";
            logOutText += "[INFO]";
        }
        if (type.equals(Type.DEBUG)) {
            outText = outText + Colors.WHITE + "[DEB]";
            logOutText += "[DEBUG]";
        }
        if (type.equals(Type.ERROR)) {
            outText = outText + Colors.RED + "[ERR]";
            logOutText += "[ERROR]";
        }
        outText += Colors.RESET;
        outText = outText + " " + text;
        logOutText = logOutText + " " + text;
        ConsoleMessage.logger.info(logOutText);
        if (type.equals(Type.DEBUG) && !Settings.DEBUG) {
            return;
        }
        System.out.println(outText);
    }
    
    static {
        logger = LogManager.getLogger((Class)ConsoleMessage.class);
    }
    
    public enum Type
    {
        ERROR, 
        INFO, 
        SUCCESS, 
        DEBUG;
    }
}
