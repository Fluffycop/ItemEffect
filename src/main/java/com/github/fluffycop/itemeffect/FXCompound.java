package com.github.fluffycop.itemeffect;

import me.ialistannen.mininbt.NBTWrappers.*;

public class FXCompound {
    NBTTagCompound comp;

    public NBTTagCompound getComp() {
        return comp;
    }

    public FXCompound(NBTTagCompound comp) {
        this.comp = comp;
    }

    public NBTTagList getColors() {
        checkNull("Colors");
        return (NBTTagList) comp.get("Colors");
    }

    public NBTTagList getKeys() {
        checkNull("Keys");
        return (NBTTagList) comp.get("Keys");
    }

    public NBTTagList getEffects() {
        checkNull("Effects");
        return (NBTTagList) comp.get("Effects");
    }

    public NBTTagList getIntensities() {
        checkNull("Intensities");
        return (NBTTagList) comp.get("Intensities");
    }

    public void setColors(INBTBase list) {
        comp.set("Colors", list);
    }

    public void setEffects(INBTBase list) {
        comp.set("Effects", list);
    }

    public void setKeys(INBTBase list) {
        comp.set("Keys", list);
    }

    public void setIntensities(INBTBase list) {
        comp.set("Intensities", list);
    }

    public int getSize() {
        return getKeys().size();
    }

    private void checkNull(String key) {
        if(!comp.hasKey(key)) {
            comp.set(key, new NBTTagList());
        }
    }
}
