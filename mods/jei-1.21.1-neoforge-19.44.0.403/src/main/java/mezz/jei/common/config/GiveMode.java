/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.config;

public enum GiveMode {
    INVENTORY,
    MOUSE_PICKUP;

    public static final GiveMode defaultGiveMode;

    static {
        defaultGiveMode = MOUSE_PICKUP;
    }
}

