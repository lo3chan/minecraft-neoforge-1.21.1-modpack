/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.client.config.structure.Option
 *  net.minecraft.resources.ResourceLocation
 */
package me.flashyreese.mods.reeses_sodium_options.client.gui.state;

import net.caffeinemc.mods.sodium.client.config.structure.Option;
import net.minecraft.resources.ResourceLocation;

public record SearchResultEntry(String tabKey, ResourceLocation optionId, Option option) {
}

