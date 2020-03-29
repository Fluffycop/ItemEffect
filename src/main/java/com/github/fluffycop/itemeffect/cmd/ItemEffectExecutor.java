package com.github.fluffycop.itemeffect.cmd;

import org.bukkit.entity.Player;

import java.util.List;

public interface ItemEffectExecutor {
    void execute(Player p, String[] args);
}
