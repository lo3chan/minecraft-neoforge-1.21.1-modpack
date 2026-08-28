/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.MinecraftServer
 */
package mezz.jei.common.platform;

import java.util.Optional;
import net.minecraft.server.MinecraftServer;

public interface IPlatformWorldHelper {
    public Optional<String> getLevelId(MinecraftServer var1);
}

