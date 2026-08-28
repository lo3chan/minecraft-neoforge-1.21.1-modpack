/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.system.MemoryUtil
 */
package net.caffeinemc.mods.sodium.client.platform.windows.api.version;

import org.lwjgl.system.MemoryUtil;

public record LanguageCodePage(int languageId, int codePage) {
    static final int STRIDE = 4;

    static LanguageCodePage decode(long address) {
        int value = MemoryUtil.memGetInt((long)address);
        int languageId = value & 0xFFFF;
        int codePage = (value & 0xFFFF0000) >> 16;
        return new LanguageCodePage(languageId, codePage);
    }
}

