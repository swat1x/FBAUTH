package me.swat1x.fbauth.management;

import me.swat1x.fbauth.management.config.ConfigManager;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;

public class FileManager {

    private static Configuration configFile = null;

    private Plugin plugin;

    public FileManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        configFile = null;

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        File file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists()) {
            try {
                Throwable t = null;

                try {
                    InputStream in = plugin.getResourceAsStream("config.yml");

                    try {
                        Files.copy(in, file.toPath(), new CopyOption[0]);
                    } finally {
                        if (in != null) {
                            in.close();
                        }

                    }

                    if (in != null) {
                        in.close();
                    }
                } finally {
                    if (t == null) {
                        t = new Throwable();
                    } else {
                        Throwable t2 = new Throwable();
                        if (t != t2) {
                            t.addSuppressed(t2);
                        }
                    }

                }

                if (t == null) {
                    new Throwable();
                } else {
                    Throwable t2 = new Throwable();
                    if (t != t2) {
                        t.addSuppressed(t2);
                    }
                }
            } catch (IOException var17) {
                var17.printStackTrace();
            }
        }

        try {
            configFile = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(plugin.getDataFolder(), "config.yml"));
        } catch (IOException var14) {
            var14.printStackTrace();
        }

    }

    public static Configuration getConfigFile() {
        return configFile;
    }

}
