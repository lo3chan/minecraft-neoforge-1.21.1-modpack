/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  java.lang.MatchException
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.Holder
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.tags.BiomeTags
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.biome.Biome$Precipitation
 */
package net.irisshaders.iris.uniforms;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.function.IntSupplier;
import java.util.function.ToIntFunction;
import net.irisshaders.iris.gl.uniform.FloatSupplier;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.mixinterface.ExtendedBiome;
import net.irisshaders.iris.parsing.BiomeCategories;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;

public class BiomeUniforms {
    private static final Object2IntMap<ResourceKey<Biome>> biomeMap = new Object2IntOpenHashMap();

    public static Object2IntMap<ResourceKey<Biome>> getBiomeMap() {
        return biomeMap;
    }

    public static void addBiomeUniforms(UniformHolder uniforms) {
        uniforms.uniform1i(UniformUpdateFrequency.PER_TICK, "biome", BiomeUniforms.playerI(player -> biomeMap.getInt(player.level().getBiome(player.blockPosition()).unwrapKey().orElse(null)))).uniform1i(UniformUpdateFrequency.PER_TICK, "biome_category", BiomeUniforms.playerI(player -> {
            Holder holder = player.level().getBiome(player.blockPosition());
            ExtendedBiome extendedBiome = (ExtendedBiome)holder.value();
            if (extendedBiome.getBiomeCategory() == -1) {
                extendedBiome.setBiomeCategory(BiomeUniforms.getBiomeCategory((Holder<Biome>)holder).ordinal());
                return extendedBiome.getBiomeCategory();
            }
            return extendedBiome.getBiomeCategory();
        })).uniform1i(UniformUpdateFrequency.PER_TICK, "biome_precipitation", BiomeUniforms.playerI(player -> {
            Biome.Precipitation precipitation = ((Biome)player.level().getBiome(player.blockPosition()).value()).getPrecipitationAt(player.blockPosition());
            return switch (precipitation) {
                default -> throw new MatchException(null, null);
                case Biome.Precipitation.NONE -> 0;
                case Biome.Precipitation.RAIN -> 1;
                case Biome.Precipitation.SNOW -> 2;
            };
        })).uniform1f(UniformUpdateFrequency.PER_TICK, "rainfall", BiomeUniforms.playerF(player -> ((ExtendedBiome)player.level().getBiome(player.blockPosition()).value()).getDownfall())).uniform1f(UniformUpdateFrequency.PER_TICK, "temperature", BiomeUniforms.playerF(player -> ((Biome)player.level().getBiome(player.blockPosition()).value()).getBaseTemperature()));
    }

    private static BiomeCategories getBiomeCategory(Holder<Biome> holder) {
        if (holder.is(BiomeTags.WITHOUT_WANDERING_TRADER_SPAWNS)) {
            return BiomeCategories.NONE;
        }
        if (holder.is(BiomeTags.HAS_VILLAGE_SNOWY)) {
            return BiomeCategories.ICY;
        }
        if (holder.is(BiomeTags.IS_HILL)) {
            return BiomeCategories.EXTREME_HILLS;
        }
        if (holder.is(BiomeTags.IS_TAIGA)) {
            return BiomeCategories.TAIGA;
        }
        if (holder.is(BiomeTags.IS_OCEAN)) {
            return BiomeCategories.OCEAN;
        }
        if (holder.is(BiomeTags.IS_JUNGLE)) {
            return BiomeCategories.JUNGLE;
        }
        if (holder.is(BiomeTags.IS_FOREST)) {
            return BiomeCategories.FOREST;
        }
        if (holder.is(BiomeTags.IS_BADLANDS)) {
            return BiomeCategories.MESA;
        }
        if (holder.is(BiomeTags.IS_NETHER)) {
            return BiomeCategories.NETHER;
        }
        if (holder.is(BiomeTags.IS_END)) {
            return BiomeCategories.THE_END;
        }
        if (holder.is(BiomeTags.IS_BEACH)) {
            return BiomeCategories.BEACH;
        }
        if (holder.is(BiomeTags.HAS_DESERT_PYRAMID)) {
            return BiomeCategories.DESERT;
        }
        if (holder.is(BiomeTags.IS_RIVER)) {
            return BiomeCategories.RIVER;
        }
        if (holder.is(BiomeTags.HAS_CLOSER_WATER_FOG)) {
            return BiomeCategories.SWAMP;
        }
        if (holder.is(BiomeTags.PLAYS_UNDERWATER_MUSIC)) {
            return BiomeCategories.UNDERGROUND;
        }
        if (holder.is(BiomeTags.WITHOUT_ZOMBIE_SIEGES)) {
            return BiomeCategories.MUSHROOM;
        }
        if (holder.is(BiomeTags.IS_MOUNTAIN)) {
            return BiomeCategories.MOUNTAIN;
        }
        return BiomeCategories.PLAINS;
    }

    static IntSupplier playerI(ToIntFunction<LocalPlayer> function) {
        return () -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) {
                return 0;
            }
            return function.applyAsInt(player);
        };
    }

    static FloatSupplier playerF(ToFloatFunction<LocalPlayer> function) {
        return () -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) {
                return 0.0f;
            }
            return function.applyAsFloat(player);
        };
    }

    @FunctionalInterface
    public static interface ToFloatFunction<T> {
        public float applyAsFloat(T var1);
    }
}

