package ru.regiuss.CryptWebBot.Configurations;

import java.io.IOException;

public class Settings {
    static YamlConfigRunner cfg;

    public static boolean DEBUG;
    public static String ANTICAPTCHA_API_KEY;
    public static String CAPMONSTER_API_KEY;
    public static int CAPTCHA_TYPE;
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
    public static boolean CHECK_UPDATE;
    public static boolean AUTO_UPDATE;

    public static void LoadSettings() throws IOException {
        cfg = new YamlConfigRunner();
        Settings.DEBUG = (Boolean) cfg.get("DEBUG");
        Settings.ANTICAPTCHA_API_KEY = (String) cfg.get("Сaptcha.AntiCaptchaAPIKey");
        Settings.CAPMONSTER_API_KEY = (String) cfg.get("Сaptcha.CapmonsterAPIKey");
        Settings.CAPTCHA_TYPE = (Integer) cfg.get("Сaptcha.type");
        Settings.WAIT_LOGIN_WAX_ACCOUNT = (Integer) cfg.get("WaxAccountLogin.wait_login_wax_account");
        Settings.WAIT_LOGIN_WAX_ACCOUNT_5 = (Integer) cfg.get("WaxAccountLogin.wait_login_wax_account_5");
        Settings.LOGIN_WAX_ACCOUNT_COUNT = (Integer) cfg.get("WaxAccountLogin.try_count");
        Settings.SIGNATURES_COUNT = (Integer) cfg.get("Signatures.try_count");
        Settings.SIGNATURES_WAIT_ON_ERROR = (Integer) cfg.get("Signatures.wait_on_error");
        Settings.MINE_WAIT_RESOURCES_ERROR = (Integer) cfg.get("Errors.ResourcesWait");
        Settings.MINE_TOO_SOON_ERROR = (Integer) cfg.get("Errors.MineTooSoon");
        Settings.MINE_OTHER_ERROR = (Integer) cfg.get("Errors.OtherError");
        Settings.RESOURCES_CHECK = (Boolean) cfg.get("Resources.check");
        Settings.RESOURCES_WAIT = (Integer) cfg.get("Resources.wait");
        Settings.RESOURCES_MIN = (Integer) cfg.get("Resources.min");
        Settings.CHECK_UPDATE = (Boolean) cfg.get("CheckUpdate");
        Settings.AUTO_UPDATE = (Boolean) cfg.get("AutoUpdate");
    }
}
