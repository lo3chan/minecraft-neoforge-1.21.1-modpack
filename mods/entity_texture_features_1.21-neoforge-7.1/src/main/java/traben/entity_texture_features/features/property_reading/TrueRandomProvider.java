/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.server.packs.resources.ResourceManager
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.property_reading;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.features.texture_handlers.ETFDirectory;
import traben.entity_texture_features.utils.ETFUtils2;

public class TrueRandomProvider
implements ETFApi.ETFVariantSuffixProvider {
    private final int[] suffixes;
    private final String packname;
    protected ETFApi.ETFVariantSuffixProvider.EntityRandomSeedFunction entityRandomSeedFunction = ETFEntityRenderState::optifineId;

    private TrueRandomProvider(String secondPack, int[] suffixes) {
        this.suffixes = suffixes;
        this.packname = secondPack;
    }

    @Nullable
    public static TrueRandomProvider of(ResourceLocation vanillaIdentifier) {
        ResourceManager resources = Minecraft.getInstance().getResourceManager();
        ResourceLocation second = ETFDirectory.getDirectoryVersionOf(ETFUtils2.addVariantNumberSuffix(vanillaIdentifier, 2));
        if (second == null) {
            return null;
        }
        @Nullable String secondPack = resources.getResource(second).map(Resource::sourcePackId).orElse(null);
        @Nullable String vanillaPack = resources.getResource(vanillaIdentifier).map(Resource::sourcePackId).orElse(null);
        if (secondPack == null || !secondPack.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(secondPack, vanillaPack))) {
            return null;
        }
        ArrayList<Integer> suffixes = new ArrayList<Integer>();
        suffixes.add(1);
        suffixes.add(2);
        boolean notAllowSkip = !ETF.config().getConfig().optifine_allowWeirdSkipsInTrueRandom;
        for (int i2 = 3; i2 < suffixes.size() + 10; ++i2) {
            if (ETFDirectory.getDirectoryVersionOf(ETFUtils2.addVariantNumberSuffix(vanillaIdentifier, i2)) != null) {
                suffixes.add(i2);
                continue;
            }
            if (notAllowSkip) break;
        }
        if (((Integer)suffixes.get(suffixes.size() - 1)).intValue() != suffixes.size()) {
            ETFUtils2.logWarn("Random suffixes [" + String.valueOf(suffixes) + "] are not sequential for " + String.valueOf(vanillaIdentifier) + " in pack " + secondPack + " this is not recommended but has been enabled in the optifine compat settings.");
        }
        return new TrueRandomProvider(secondPack, suffixes.stream().mapToInt(i -> i).toArray());
    }

    @Nullable
    public String getPackName() {
        return this.packname;
    }

    @Override
    public boolean entityCanUpdate(UUID uuid) {
        return false;
    }

    @Override
    public Set<Integer> getAllSuffixes() {
        return Arrays.stream(this.suffixes).boxed().collect(Collectors.toSet());
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public int getSuffixForETFEntity(ETFEntityRenderState entityToBeTested) {
        if (entityToBeTested == null) {
            return 0;
        }
        return this.suffixes[Math.abs(this.entityRandomSeedFunction.toInt(entityToBeTested)) % this.suffixes.length];
    }

    @Override
    public void setRandomSupplier(ETFApi.ETFVariantSuffixProvider.EntityRandomSeedFunction entityRandomSeedFunction) {
        if (entityRandomSeedFunction != null) {
            this.entityRandomSeedFunction = entityRandomSeedFunction;
        }
    }
}

