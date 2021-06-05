package ru.regiuss.CryptWebBot.Configurations;

import java.io.*;

public class Settings
{
    static YamlConfigRunner cfg;
    public static boolean DEBUG;
    public static String CAPMONSTER_API_KEY;
    public static String VERSION;
    public static int WAIT_LOGIN_WAX_ACCOUNT;
    public static int WAIT_LOGIN_WAX_ACCOUNT_5;
    public static int LOGIN_WAX_ACCOUNT_COUNT;
    public static int SIGNATURES_COUNT;
    public static int SIGNATURES_WAIT_ON_ERROR;
    public static int MINE_WAIT_RESOURCES_ERROR;
    public static int MINE_TOO_SOON_ERROR;
    public static int MINE_OTHER_ERROR;
    public static boolean RESOURCES_CHECK;
    public static int RESOURCES_WAIT;
    public static int RESOURCES_MIN;

    public static void LoadSettings() throws IOException {
        Settings.cfg = new YamlConfigRunner();
        Settings.DEBUG = (boolean)Settings.cfg.get("DEBUG");
        Settings.CAPMONSTER_API_KEY = (String)Settings.cfg.get("Сaptcha.CapmonsterAPIKey");
        Settings.WAIT_LOGIN_WAX_ACCOUNT = (int)Settings.cfg.get("WaxAccountLogin.wait_login_wax_account");
        Settings.WAIT_LOGIN_WAX_ACCOUNT_5 = (int)Settings.cfg.get("WaxAccountLogin.wait_login_wax_account_5");
        Settings.LOGIN_WAX_ACCOUNT_COUNT = (int)Settings.cfg.get("WaxAccountLogin.try_count");
        Settings.SIGNATURES_COUNT = (int)Settings.cfg.get("Signatures.try_count");
        Settings.SIGNATURES_WAIT_ON_ERROR = (int)Settings.cfg.get("Signatures.wait_on_error");
        Settings.MINE_WAIT_RESOURCES_ERROR = (int)Settings.cfg.get("Errors.ResourcesWait");
        Settings.MINE_TOO_SOON_ERROR = (int)Settings.cfg.get("Errors.MineTooSoon");
        Settings.MINE_OTHER_ERROR = (int)Settings.cfg.get("Errors.OtherError");
        Settings.RESOURCES_CHECK = (boolean)Settings.cfg.get("Resources.check");
        Settings.RESOURCES_WAIT = (int)Settings.cfg.get("Resources.wait");
        Settings.RESOURCES_MIN = (int)Settings.cfg.get("Resources.min");
    }
}
