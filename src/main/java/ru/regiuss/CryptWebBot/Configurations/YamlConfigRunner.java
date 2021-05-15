package ru.regiuss.CryptWebBot.Configurations;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public class YamlConfigRunner {

    Map<String, Object> obj = null;
    Yaml yaml;

    public YamlConfigRunner() throws IOException {
        yaml = new Yaml();
        File f = new File(String.valueOf(Paths.get("config.yml")));
        if(!f.exists() || f.isDirectory()) {
            InputStream is = getClass().getClassLoader().getResourceAsStream("config.yml");
            if(is == null)return;
            Files.copy(is, Paths.get("config.yml"), StandardCopyOption.REPLACE_EXISTING);
            f = new File(String.valueOf(Paths.get("config.yml")));
        }
        try(InputStream in = new FileInputStream(f)){
            this.obj = yaml.load(in);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public Object get(String str){
        Object res = obj;
        String[] args = str.split("\\.");
        try {
            for(String p : args){
                res = ((Map<?, ?>) res).get(p);
            }
            return res;
        }catch (Exception e){
            return null;
        }
    }
}
