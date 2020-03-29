package com.github.fluffycop.itemeffect.cmd;

import com.github.fluffycop.itemeffect.FXCompound;
import com.github.fluffycop.itemeffect.ItemEffectPlugin;
import me.ialistannen.mininbt.ItemNBTUtil;
import me.ialistannen.mininbt.NBTWrappers;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static com.github.fluffycop.itemeffect.cmd.CmdItemEffect.error;
import static com.github.fluffycop.itemeffect.cmd.CmdItemEffect.msg;

public class CmdItemEffectList implements ItemEffectExecutor {
    ItemEffectPlugin pl;

    public CmdItemEffectList(ItemEffectPlugin pl) {
        this.pl = pl;
    }

    @Override
    public void execute(Player p, String[] args) {
        if(!p.hasPermission("itemeffect.list")) {
            error(p, "You do not have permission to do that");
            return;
        }
        ItemStack item = p.getInventory().getItemInMainHand();
        NBTWrappers.NBTTagCompound nbt = ItemNBTUtil.getTag(item);
        if(nbt.hasKey("ItemEffects")) {
            FXCompound comp = new FXCompound((NBTWrappers.NBTTagCompound) nbt.get("ItemEffects"));
            if(comp.getSize() != 0) {
                for(int i = 0; i < comp.getSize(); i++) {
                    msg(p, ChatColor.valueOf(((NBTWrappers.NBTTagString)comp.getColors().get(i)).getString())
                           + ((NBTWrappers.NBTTagString)comp.getKeys().get(i)).getString() + " -> " +
                           ((NBTWrappers.NBTTagString)comp.getEffects().get(i)).getString() + " " +
                           ((NBTWrappers.NBTTagInt)comp.getIntensities().get(i)).getAsInt());
                }
                return;
            }
        }
        msg(p, ChatColor.GRAY + "There are no item effects on this item");
    }
}
