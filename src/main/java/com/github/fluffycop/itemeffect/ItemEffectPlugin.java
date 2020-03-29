package com.github.fluffycop.itemeffect;

import com.github.fluffycop.itemeffect.cmd.CmdItemEffect;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemEffectPlugin extends JavaPlugin {

    Config cfg;

    @Override
    public void onEnable() {
        setupConfig();
        setupRunnable();
        setupCommands();
    }

    private void setupConfig() {
        cfg = new Config(this);
        cfg.setup();
        cfg.load();
    }

    private void setupRunnable() {
         Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new EffectTask(this), EffectTask.DURATION, EffectTask.DURATION);
    }

    private void setupCommands() {
        CmdItemEffect cmd;
        getCommand("itemeffect").setExecutor(cmd = new CmdItemEffect(this));
        getCommand("itemeffect").setTabCompleter(cmd);
    }
}
