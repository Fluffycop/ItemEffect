package com.github.fluffycop.itemeffect.cmd;

import com.github.fluffycop.itemeffect.ItemEffectPlugin;
import me.ialistannen.mininbt.ItemNBTUtil;
import me.ialistannen.mininbt.NBTWrappers;
import me.ialistannen.mininbt.NBTWrappers.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import static com.github.fluffycop.itemeffect.cmd.CmdItemEffect.*;

public class CmdItemEffectSet implements ItemEffectExecutor {
    ItemEffectPlugin pl;

    private static final String USAGE = "/itemeffect set <color> <text> <effect> <intensity>";

    public CmdItemEffectSet(ItemEffectPlugin pl) {
        this.pl = pl;
    }

    /*
    validate perms
    validate held item
    validate args length
    validate input
     */
    @Override
    public void execute(Player p, String[] args) {
        if(!p.hasPermission("itemeffect.set")) { //perm
            error(p, "You do not have permission to do that");
            return;
        }
        ItemStack item = p.getInventory().getItemInMainHand();
        if(args.length < 5) {
            error(p, USAGE);
            return;
        }
        ChatColor color;
        try {
            color = ChatColor.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            error(p, args[1] + " is not a valid color");
            return;
        }
        PotionEffectType type = PotionEffectType.getByName(args[3].toUpperCase());
        if(type == null) {
            error(p, args[3] + " is not a valid potion effect");
            return;
        }
        int intensity;
        try {
            intensity = Integer.parseInt(args[4]);
        } catch (NumberFormatException e) {
            error(p, args[4] + " is not a valid number");
            return;
        }
        NBTWrappers.NBTTagCompound nbt = ItemNBTUtil.getTag(item);
        NBTWrappers.NBTTagCompound comp;
        NBTTagList colors;
        NBTTagList keys;
        NBTTagList effects;
        NBTTagList intensities;

        if(nbt.hasKey("ItemEffects")) {
            comp = (NBTWrappers.NBTTagCompound) nbt.get("ItemEffects");
            colors = (NBTTagList) comp.get("Colors");
            keys = (NBTTagList) comp.get("Keys");
            effects = (NBTTagList) comp.get("Effects");
            intensities = (NBTTagList) comp.get("Intensities");
        } else {
            comp = new NBTWrappers.NBTTagCompound();
            colors = new NBTTagList();
            keys = new NBTTagList();
            effects = new NBTTagList();
            intensities = new NBTTagList();
        }

        int index = -1;
        for(int i = 0; i < keys.size(); i++) {
            if(((NBTTagString)keys.get(i)).getString().toLowerCase().equals(args[2].toLowerCase())) {
                index = i;
                break;
            }
        }
        if(index != -1) {
            colors.getRawList().set(index, new NBTTagString(color.name()));
            keys.getRawList().set(index, new NBTTagString(args[2]));
            effects.getRawList().set(index, new NBTTagString(type.getName()));
            intensities.getRawList().set(index, new NBTTagInt(intensity));
        } else {
            colors.add(new NBTTagString(color.name()));
            keys.add(new NBTWrappers.NBTTagString(args[2]));
            effects.add(new NBTTagString(type.getName()));
            intensities.add(new NBTWrappers.NBTTagInt(intensity));
        }
        comp.set("Colors", colors);
        comp.set("Keys", keys);
        comp.set("Effects", effects);
        comp.set("Intensities", intensities);
        nbt.set("ItemEffects", comp);
        item = ItemNBTUtil.setNBTTag(nbt, item);
        updateLore(item);
        p.getInventory().setItemInMainHand(item);
        msg(p, ChatColor.GREEN + "Applied item effect "+args[2]+" with potion effect "+args[3]+" "+args[4]);
    }
}
