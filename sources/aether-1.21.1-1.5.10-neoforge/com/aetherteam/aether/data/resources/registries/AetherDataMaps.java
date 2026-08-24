package com.aetherteam.aether.data.resources.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;

public class AetherDataMaps {
   public static final DataMapType<Item, FurnaceFuel> ALTAR_FUEL = DataMapType.builder(
         ResourceLocation.fromNamespaceAndPath("aether", "altar_fuel"), Registries.ITEM, FurnaceFuel.CODEC
      )
      .synced(FurnaceFuel.BURN_TIME_CODEC, false)
      .build();
   public static final DataMapType<Item, FurnaceFuel> FREEZER_FUEL = DataMapType.builder(
         ResourceLocation.fromNamespaceAndPath("aether", "freezer_fuel"), Registries.ITEM, FurnaceFuel.CODEC
      )
      .synced(FurnaceFuel.BURN_TIME_CODEC, false)
      .build();
   public static final DataMapType<Item, FurnaceFuel> INCUBATOR_FUEL = DataMapType.builder(
         ResourceLocation.fromNamespaceAndPath("aether", "incubator_fuel"), Registries.ITEM, FurnaceFuel.CODEC
      )
      .synced(FurnaceFuel.BURN_TIME_CODEC, false)
      .build();
}
