package com.github.fluffycop.itemeffect.cmd;

import com.github.fluffycop.itemeffect.FXCompound;
import com.github.fluffycop.itemeffect.ItemEffectPlugin;
import me.ialistannen.mininbt.ItemNBTUtil;
import me.ialistannen.mininbt.NBTWrappers;
import me.ialistannen.mininbt.NBTWrappers.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static com.github.fluffycop.itemeffect.cmd.CmdItemEffect.*;

public class CmdItemEffectRemove implements ItemEffectExecutor {
    ItemEffectPlugin pl;

    private static final String USAGE = "/itemeffect remove <text>";

    public CmdItemEffectRemove(ItemEffectPlugin pl) {
        this.pl = pl;
    }

    @Override
    public void execute(Player p, String[] args) {
        if(!p.hasPermission("itemeffect.remove")) {
            error(p, "You do not have permission to do that");
            return;
        }
        if(args.length < 2) {
            error(p, USAGE);
            return;
        }
        ItemStack item = p.getInventory().getItemInMainHand();
        NBTWrappers.NBTTagCompound nbt = ItemNBTUtil.getTag(item);
        if(nbt.hasKey("ItemEffects")) {
            FXCompound comp = new FXCompound((NBTWrappers.NBTTagCompound) nbt.get("ItemEffects"));
            int index = -1;
            NBTTagList keys = comp.getKeys();
            for(int i = 0; i < keys.getRawList().size(); i++) {
                if(((NBTTagString)keys.getRawList().get(i)).getString().toLowerCase().equals(args[1].toLowerCase())) {
                    index = i;
                    break;
                }
            }
            if(index != -1) {
                keys.getRawList().remove(index);
                comp.setKeys(keys);
                NBTTagList colors = comp.getColors();
                colors.getRawList().remove(index);
                comp.setColors(colors);
                NBTTagList effects = comp.getEffects();
                effects.getRawList().remove(index);
                comp.setEffects(effects);
                NBTTagList intensities = comp.getIntensities();
                intensities.getRawList().remove(index);
                comp.setIntensities(intensities);
                nbt.set("ItemEffects", comp.getComp());
                item = ItemNBTUtil.setNBTTag(nbt, item);
                updateLore(item);
                p.getInventory().setItemInMainHand(item);
                msg(p, ChatColor.GREEN + "Removed item effect with tag " + args[1]);
                return;
            }
        }
        error(p, "There are no item effects with label " + args[1]);
    }
}
