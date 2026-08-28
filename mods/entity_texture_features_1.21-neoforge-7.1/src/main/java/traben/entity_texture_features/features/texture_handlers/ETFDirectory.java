/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceManager
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.texture_handlers;

import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.utils.ETFUtils2;

public enum ETFDirectory {
    DOES_NOT_EXIST(null),
    ETF(new String[]{"textures", "etf/random"}),
    OLD_OPTIFINE(new String[]{"textures/entity", "optifine/mob"}),
    OPTIFINE(new String[]{"textures", "optifine/random"}),
    VANILLA(null);

    private final String[] replaceStrings;

    private ETFDirectory(String[] replaceStrings) {
        this.replaceStrings = replaceStrings;
    }

    public static HashMap<@NotNull ResourceLocation, @NotNull ETFDirectory> getCache() {
        return ETFManager.getInstance().ETF_DIRECTORY_CACHE;
    }

    @Nullable
    public static ResourceLocation getDirectoryVersionOf(@Nullable ResourceLocation vanillaIdentifier) {
        if (vanillaIdentifier == null) {
            return null;
        }
        ETFDirectory directory = ETFDirectory.getDirectoryOf(vanillaIdentifier);
        return switch (directory.ordinal()) {
            case 0 -> null;
            case 4 -> vanillaIdentifier;
            default -> ETFDirectory.getIdentifierAsDirectory(vanillaIdentifier, directory);
        };
    }

    @NotNull
    public static ETFDirectory getDirectoryOf(@NotNull ResourceLocation vanillaIdentifier) {
        HashMap<@NotNull ResourceLocation, ETFDirectory> cache = ETFDirectory.getCache();
        ETFDirectory value = cache.get(vanillaIdentifier);
        if (value == null) {
            value = ETFDirectory.findDirectoryOf(vanillaIdentifier);
            cache.put(vanillaIdentifier, value);
        }
        return value;
    }

    @NotNull
    private static ETFDirectory findDirectoryOf(ResourceLocation vanillaIdentifier) {
        String path = vanillaIdentifier.getPath();
        ResourceManager resources = Minecraft.getInstance().getResourceManager();
        if (path.contains("etf/random/entity") && resources.getResource(vanillaIdentifier).isPresent()) {
            return ETF;
        }
        if (path.contains("optifine/random/entity") && resources.getResource(vanillaIdentifier).isPresent()) {
            return OPTIFINE;
        }
        if (path.contains("optifine/mob") && resources.getResource(vanillaIdentifier).isPresent()) {
            return OLD_OPTIFINE;
        }
        ArrayList<ETFDirectory> foundDirectories = new ArrayList<ETFDirectory>();
        if (resources.getResource(ETFDirectory.getIdentifierAsDirectory(vanillaIdentifier, VANILLA)).isPresent()) {
            foundDirectories.add(VANILLA);
        }
        if (resources.getResource(ETFDirectory.getIdentifierAsDirectory(vanillaIdentifier, OLD_OPTIFINE)).isPresent()) {
            foundDirectories.add(OLD_OPTIFINE);
        }
        if (resources.getResource(ETFDirectory.getIdentifierAsDirectory(vanillaIdentifier, OPTIFINE)).isPresent()) {
            foundDirectories.add(OPTIFINE);
        }
        if (resources.getResource(ETFDirectory.getIdentifierAsDirectory(vanillaIdentifier, ETF)).isPresent()) {
            foundDirectories.add(ETF);
        }
        if (foundDirectories.isEmpty()) {
            return DOES_NOT_EXIST;
        }
        if (foundDirectories.size() == 1) {
            return (ETFDirectory)((Object)foundDirectories.get(0));
        }
        HashMap resourcePackNames = new HashMap();
        for (ETFDirectory directory : foundDirectories) {
            resources.getResource(ETFDirectory.getIdentifierAsDirectory(vanillaIdentifier, directory)).ifPresent(value -> resourcePackNames.put(value.sourcePackId(), directory));
        }
        String returnedPack = ETFUtils2.returnNameOfHighestPackFromTheseMultiple(resourcePackNames.keySet().toArray(new String[0]));
        return returnedPack != null ? (ETFDirectory)((Object)resourcePackNames.get(returnedPack)) : VANILLA;
    }

    @NotNull
    public static ResourceLocation getIdentifierAsDirectory(ResourceLocation identifier, ETFDirectory directory) {
        if (directory.doesReplace()) {
            return ETFUtils2.res(identifier.getNamespace(), identifier.getPath().replace(directory.replaceStrings[0], directory.replaceStrings[1]));
        }
        return identifier;
    }

    public boolean doesReplace() {
        return this.replaceStrings != null;
    }
}

