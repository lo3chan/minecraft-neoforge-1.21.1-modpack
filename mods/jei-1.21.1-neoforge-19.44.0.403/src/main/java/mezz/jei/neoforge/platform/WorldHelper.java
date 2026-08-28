/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.MinecraftServer
 */
package mezz.jei.neoforge.platform;

import java.util.Optional;
import mezz.jei.common.platform.IPlatformWorldHelper;
import net.minecraft.server.MinecraftServer;

public class WorldHelper
implements IPlatformWorldHelper {
    @Override
    public Optional<String> getLevelId(MinecraftServer server) {
        return Optional.of(server.storageSource.getLevelId());
    }
}

