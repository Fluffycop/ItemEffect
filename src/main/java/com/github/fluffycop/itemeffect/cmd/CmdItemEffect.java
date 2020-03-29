package com.github.fluffycop.itemeffect.cmd;

import com.github.fluffycop.itemeffect.FXCompound;
import com.github.fluffycop.itemeffect.ItemEffectPlugin;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.ialistannen.mininbt.ItemNBTUtil;
import me.ialistannen.mininbt.NBTWrappers;
import me.ialistannen.mininbt.NBTWrappers.*;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import me.lucko.commodore.file.CommodoreFileFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CmdItemEffect implements CommandExecutor, TabCompleter {
    final ItemEffectExecutor set;
    final ItemEffectExecutor remove;
    final ItemEffectExecutor list;

    ItemEffectPlugin pl;

    public CmdItemEffect(ItemEffectPlugin pl) {
        this.pl = pl;
        set = new CmdItemEffectSet(pl);
        remove = new CmdItemEffectRemove(pl);
        list = new CmdItemEffectList(pl);

        commodoreCompletions();
    }

    private void commodoreCompletions() {
        Commodore commodore = CommodoreProvider.getCommodore(pl);
        LiteralCommandNode<?> itemeffectCmd;
        try {
            itemeffectCmd = CommodoreFileFormat.parse(pl.getResource("itemeffect.commodore"));
        } catch (IOException e) {
            e.printStackTrace();
            pl.getLogger().severe("Failed to read 'itemeffect.commodore' from plugin resources. This should be impossible...");
            Bukkit.getPluginManager().disablePlugin(pl);
            return;
        }
        commodore.register(pl.getCommand("itemeffect"), itemeffectCmd);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!p.hasPermission("itemeffect.cmd")) {
                error(p, "You do not have permission to do that");
                return true;
            }
            if(p.getInventory().getItemInMainHand().getType() == Material.AIR) {
                error(p, "You must be holding an item to do that");
                return true;
            }
            if (args.length == 0) {
                msg(p, HELP_MSG);
                return true;
            } else {
                switch(args[0]) {
                    case "set": {
                        set.execute(p, args);
                        break;
                    }
                    case "remove": {
                        remove.execute(p, args);
                        break;
                    }
                    case "list": {
                        list.execute(p, args);
                        break;
                    }
                    default: {
                        msg(p, HELP_MSG);
                        break;
                    }
                }
            }
        } else {
            error(sender, "You must be a player to do that command");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            if (args.length == 1) {
                return Stream.of("set", "remove", "list")
                        .filter(s -> s.startsWith(args[0]))
                        .collect(Collectors.toList());
            }
            switch (args[0]) {
                case "set": {
                    if(args.length == 2) {
                        return Arrays.stream(ChatColor.values())
                                .map(Enum::name)
                                .filter(s -> s.startsWith(args[1].toUpperCase()))
                                .collect(Collectors.toList());
                    } else if (args.length == 4) {
                        return Arrays.stream(PotionEffectType.values())
                                .map(PotionEffectType::getName)
                                .filter(s -> s.toUpperCase().startsWith(args[1].toUpperCase()))
                                .collect(Collectors.toList());
                    }
                }
                case "remove": {
                    if(args.length == 2) {
                        Player player = (Player) sender;
                        ItemStack item = player.getInventory().getItemInMainHand();
                        NBTTagCompound nbt;
                        FXCompound comp;
                        if(item.getType() != Material.AIR &&
                           (nbt = ItemNBTUtil.getTag(item)).hasKey("ItemEffects") &&
                           (comp = new FXCompound((NBTTagCompound) nbt.get("ItemEffects"))).getSize() > 0) {
                            return comp.getKeys().getRawList().stream()
                                    .map(base -> ((NBTTagString)base).getString())
                                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                                    .collect(Collectors.toList());
                        }
                    }
                }
            }
        }
        return Collections.emptyList();
    }

    private static String HELP_MSG =
            ChatColor.LIGHT_PURPLE + "====ItemEffects====\n" +
            ChatColor.BLUE + "/itemeffect set <color> <text> <effect> <intensity>" + ChatColor.AQUA + " Adds the specified item effect to the item in your hand\n" +
            ChatColor.BLUE + "/itemeffect remove <text>" + ChatColor.AQUA + " Removes the specified item effect from the item in your hand\n" +
            ChatColor.BLUE + "/itemeffect list" + ChatColor.AQUA + " Lists all the item effects of the item in your hand";

    static void msg(CommandSender sender, String msg) {
        sender.sendMessage(msg);
    }

    static void error(CommandSender sender, String msg) {
        sender.sendMessage(ChatColor.RED + msg);
    }

    static void updateLore(ItemStack item) {
        NBTWrappers.NBTTagCompound nbt = ItemNBTUtil.getTag(item);
        if(nbt.hasKey("ItemEffects")) {
            List<String> newLore = new ArrayList<>();
            FXCompound comp = new FXCompound((NBTWrappers.NBTTagCompound) nbt.get("ItemEffects"));
            for(int i = 0; i < comp.getSize(); i++) {
                ChatColor color = ChatColor.valueOf(((NBTTagString)comp.getColors().get(i)).getString());
                String label = ((NBTTagString)comp.getKeys().get(i)).getString();
                newLore.add(color + label);
            }
            item.setLore(newLore);
        }
    }
}
