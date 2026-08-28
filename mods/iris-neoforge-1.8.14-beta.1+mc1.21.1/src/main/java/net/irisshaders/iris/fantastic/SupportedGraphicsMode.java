/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 *  net.minecraft.client.GraphicsStatus
 *  net.minecraft.client.OptionInstance
 */
package net.irisshaders.iris.fantastic;

import net.irisshaders.iris.Iris;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.OptionInstance;

public enum SupportedGraphicsMode {
    FAST,
    FANCY;


    public static SupportedGraphicsMode fromVanilla(OptionInstance<GraphicsStatus> status) {
        return switch ((GraphicsStatus)status.get()) {
            default -> throw new MatchException(null, null);
            case GraphicsStatus.FAST -> FAST;
            case GraphicsStatus.FANCY -> FANCY;
            case GraphicsStatus.FABULOUS -> {
                Iris.logger.warn("Detected Fabulous Graphics being used somehow, changing to Fancy!");
                status.set((Object)GraphicsStatus.FANCY);
                yield FANCY;
            }
        };
    }

    public GraphicsStatus toVanilla() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> GraphicsStatus.FAST;
            case 1 -> GraphicsStatus.FANCY;
        };
    }
}

