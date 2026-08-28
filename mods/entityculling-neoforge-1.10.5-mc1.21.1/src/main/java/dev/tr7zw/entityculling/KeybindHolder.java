/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.tr7zw.transition.loader.ModLoaderUtil
 *  dev.tr7zw.transition.mc.GeneralUtil
 *  net.minecraft.client.KeyMapping
 */
package dev.tr7zw.entityculling;

import dev.tr7zw.transition.loader.ModLoaderUtil;
import dev.tr7zw.transition.mc.GeneralUtil;
import net.minecraft.client.KeyMapping;

public final class KeybindHolder {
    public static final KeybindHolder INSTANCE = new KeybindHolder();
    private boolean initialized = false;
    public final KeyMapping keybind = GeneralUtil.createKeyMapping((String)"key.entityculling.toggle", (int)-1, (String)"text.entityculling.title");
    public final KeyMapping keybindBoxes = GeneralUtil.createKeyMapping((String)"key.entityculling.toggleBoxes", (int)-1, (String)"text.entityculling.title");

    private KeybindHolder() {
    }

    public void registerKeybinds() {
        if (this.initialized) {
            return;
        }
        this.initialized = true;
        ModLoaderUtil.registerKeybind((KeyMapping)this.keybind);
    }
}

