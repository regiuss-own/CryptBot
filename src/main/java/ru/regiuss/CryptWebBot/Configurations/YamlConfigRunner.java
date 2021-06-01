package ru.regiuss.CryptWebBot.Configurations;

import java.util.*;
import org.yaml.snakeyaml.*;
import java.nio.file.*;
import java.io.*;

public class YamlConfigRunner
{
    Map<String, Object> obj;
    Yaml yaml;
    
    public YamlConfigRunner() throws IOException {
        this.obj = null;
        this.yaml = new Yaml();
        File f = new File(String.valueOf(Paths.get("config.yml", new String[0])));
        if (!f.exists() || f.isDirectory()) {
            final InputStream is = this.getClass().getClassLoader().getResourceAsStream("config.yml");
            if (is == null) {
                return;
            }
            Files.copy(is, Paths.get("config.yml", new String[0]), StandardCopyOption.REPLACE_EXISTING);
            f = new File(String.valueOf(Paths.get("config.yml", new String[0])));
        }
        try (final InputStream in = new FileInputStream(f)) {
            this.obj = (Map<String, Object>)this.yaml.load(in);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Object get(final String str) {
        Object res = this.obj;
        final String[] args = str.split("\\.");
        try {
            for (final String p : args) {
                res = ((Map)res).get(p);
            }
            return res;
        }
        catch (Exception e) {
            return null;
        }
    }
}
