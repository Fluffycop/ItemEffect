package com.github.fluffycop.itemeffect;

import me.ialistannen.mininbt.ItemNBTUtil;
import me.ialistannen.mininbt.NBTWrappers;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EffectTask implements Runnable {
    public static int DURATION = 20;

    ItemEffectPlugin pl;

    public EffectTask(ItemEffectPlugin pl) {
        this.pl = pl;
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(p -> {
            PlayerInventory inv = p.getInventory();
            int count = 0;
            Map<PotionEffectType, Integer> effects = new HashMap<>();
            for(EquipmentSlot slot : pl.cfg.allowedSlots) {
                if(count >= pl.cfg.maxEquipped) {
                    break;
                }
                ItemStack fxItem = inv.getItem(slot);
                if(fxItem != null && fxItem.getType() != Material.AIR) {
                    NBTWrappers.NBTTagCompound nbt = ItemNBTUtil.getTag(fxItem);
                    if(nbt.hasKey("ItemEffects")) {
                        FXCompound comp = new FXCompound((NBTWrappers.NBTTagCompound)nbt.get("ItemEffects"));
                        if(comp.getSize() > 0) {
                            count++;
                            List<String> fx = comp.getEffects().getRawList().stream()
                                    .map(b -> ((NBTWrappers.NBTTagString)b).getString())
                                    .collect(Collectors.toList());
                            List<Integer> intensities = comp.getIntensities().getRawList().stream()
                                    .map(i -> ((NBTWrappers.NBTTagInt)i).getAsInt())
                                    .collect(Collectors.toList());
                            for(int i = 0; i < comp.getSize(); i++) {
                                PotionEffectType type = PotionEffectType.getByName(fx.get(i));
                                int intensity = intensities.get(i);
                                effects.putIfAbsent(type, intensity);
                                if(effects.get(type) < intensity) {
                                    effects.put(type, intensity);
                                }
                            }
                        }
                    }
                }
            }
            effects.forEach((type, intensity) -> p.addPotionEffect(new PotionEffect(type, DURATION + 10, intensity - 1)));
        });
    }
}
