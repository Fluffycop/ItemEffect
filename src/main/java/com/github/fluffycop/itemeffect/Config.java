package com.github.fluffycop.itemeffect;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.EquipmentSlot;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class Config {
    ItemEffectPlugin pl;
    File file;
    FileConfiguration cfg;

    public Config(ItemEffectPlugin pl) {
        this.pl = pl;
        file = new File(pl.getDataFolder() + File.separator + "config.yml");
    }

    public void setup() {
        firstRun(pl.getResource("config.yml"));
        cfg = new YamlConfiguration();
        try {
            cfg.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            pl.getLogger().severe("An unexpected error occurred while trying to load config.yml");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(pl);
        }
    }

    public void load() {
        maxEquipped = cfg.getInt("max-equipped", 5);
        for(String slotStr : cfg.getStringList("allowed-slots")) {
            allowedSlots.add(EquipmentSlot.valueOf(slotStr));
        }
    }

    int maxEquipped;
    Set<EquipmentSlot> allowedSlots = new HashSet<>();

    private void firstRun(InputStream defaultContent) {
        if (!this.file.exists()) {
            this.file.getParentFile().mkdirs();
            this.copy(defaultContent, this.file);
        }
    }

    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];

            int len;
            while((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            out.close();
            in.close();
        } catch (IOException var6) {
            throw new RuntimeException(var6);
        }
    }
}
