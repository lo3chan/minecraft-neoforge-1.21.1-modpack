/*
 * Decompiled with CFR 0.152.
 */
package net.caffeinemc.mods.sodium.client.console.message;

import net.caffeinemc.mods.sodium.client.console.message.MessageLevel;

public record Message(MessageLevel level, String text, boolean translated, double duration) {
}

