/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.Registry
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.resources.ResourceKey
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.common.util;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

public class RegistryUtil {
    private static final Map<ResourceKey<? extends Registry<?>>, Registry<?>> REGISTRY_CACHE = new HashMap();
    @Nullable
    private static RegistryAccess REGISTRY_ACCESS;

    public static <T> Registry<T> getRegistry(ResourceKey<? extends Registry<T>> key) {
        Registry<?> registry = REGISTRY_CACHE.get(key);
        if (registry == null) {
            registry = RegistryUtil.getRegistryUncached(key);
            REGISTRY_CACHE.put(key, registry);
        }
        Registry<?> castRegistry = registry;
        return castRegistry;
    }

    private static Registry<?> getRegistryUncached(ResourceKey<? extends Registry<?>> key) {
        RegistryAccess registryAccess = RegistryUtil.getRegistryAccess();
        return registryAccess.registryOrThrow(key);
    }

    public static RegistryAccess getRegistryAccess() {
        if (REGISTRY_ACCESS == null) {
            Minecraft minecraft = Minecraft.getInstance();
            ClientLevel level = minecraft.level;
            if (level == null) {
                throw new IllegalStateException("Could not get registry, registry access is unavailable because the level is currently null");
            }
            REGISTRY_ACCESS = level.registryAccess();
        }
        return REGISTRY_ACCESS;
    }

    public static void setRegistryAccess(@Nullable RegistryAccess registryAccess) {
        REGISTRY_ACCESS = registryAccess;
        REGISTRY_CACHE.clear();
    }
}

