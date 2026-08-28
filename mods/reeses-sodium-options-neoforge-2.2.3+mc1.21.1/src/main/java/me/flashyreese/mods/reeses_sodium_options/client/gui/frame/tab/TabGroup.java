/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.client.config.structure.ModOptions
 */
package me.flashyreese.mods.reeses_sodium_options.client.gui.frame.tab;

import java.util.List;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.tab.Tab;
import net.caffeinemc.mods.sodium.client.config.structure.ModOptions;

record TabGroup(String id, ModOptions modOptions, List<Tab<?>> tabs) {
}

