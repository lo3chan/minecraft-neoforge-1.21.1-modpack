package dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.corgitaco.enhancedcelestials2core.api.EnhancedCelestialsRegistry;
import dev.corgitaco.enhancedcelestials2core.core.EC2Constants;
import dev.corgitaco.enhancedcelestials2core.platform.services.RegistrationService;
import net.minecraft.core.Registry;

public final class LunarEventModifierTypes {
   public static final Codec<LunarEventModifier> CODEC = EnhancedCelestialsRegistry.LUNAR_EVENT_MODIFIER_TYPE
      .byNameCodec()
      .dispatch("type", LunarEventModifier::type, LunarEventModifierType::codec);
   public static final LunarEventModifierType<SkyLightColorModifier> SKY_LIGHT_COLOR = register("sky_light_color", SkyLightColorModifier.CODEC);
   public static final LunarEventModifierType<MoonTextureColorModifier> MOON_TEXTURE_COLOR = register("moon_texture_color", MoonTextureColorModifier.CODEC);
   public static final LunarEventModifierType<GlowColorModifier> GLOW_COLOR = register("glow_color", GlowColorModifier.CODEC);
   public static final LunarEventModifierType<MoonSizeModifier> MOON_SIZE = register("moon_size", MoonSizeModifier.CODEC);
   public static final LunarEventModifierType<MoonTextureModifier> MOON_TEXTURE = register("moon_texture", MoonTextureModifier.CODEC);
   public static final LunarEventModifierType<SoundTrackModifier> SOUND_TRACK = register("sound_track", SoundTrackModifier.CODEC);
   public static final LunarEventModifierType<TextComponentsModifier> TEXT_COMPONENTS = register("text_components", TextComponentsModifier.CODEC);
   public static final LunarEventModifierType<NameColorModifier> NAME_COLOR = register("name_color", NameColorModifier.CODEC);
   public static final LunarEventModifierType<SpawnCategoryMultiplierModifier> SPAWN_CATEGORY_MULTIPLIER = register(
      "spawn_category_multiplier", SpawnCategoryMultiplierModifier.CODEC
   );
   public static final LunarEventModifierType<MobEffectsModifier> MOB_EFFECTS = register("mob_effects", MobEffectsModifier.CODEC);
   public static final LunarEventModifierType<BlockSleepingModifier> BLOCK_SLEEPING = register("block_sleeping", BlockSleepingModifier.CODEC);
   public static final LunarEventModifierType<DisableBiomeSpawnSettingsModifier> DISABLE_BIOME_SPAWN_SETTINGS = register(
      "disable_biome_spawn_settings", DisableBiomeSpawnSettingsModifier.CODEC
   );
   public static final LunarEventModifierType<ForceSurfaceSpawningModifier> FORCE_SURFACE_SPAWNING = register(
      "force_surface_spawning", ForceSurfaceSpawningModifier.CODEC
   );
   public static final LunarEventModifierType<SlimesSpawnEverywhereModifier> SLIMES_SPAWN_EVERYWHERE = register(
      "slimes_spawn_everywhere", SlimesSpawnEverywhereModifier.CODEC
   );
   public static final LunarEventModifierType<MobSpawnSettingsModifier> MOB_SPAWN_SETTINGS = register("mob_spawn_settings", MobSpawnSettingsModifier.CODEC);
   public static final LunarEventModifierType<BlockItemDropModifier> BLOCK_ITEM_DROP = register("block_item_drop", BlockItemDropModifier.CODEC);
   public static final LunarEventModifierType<EntityDropModifier> ENTITY_DROP = register("entity_drop", EntityDropModifier.CODEC);
   public static final LunarEventModifierType<AnvilCostModifier> ANVIL_COST = register("anvil_cost", AnvilCostModifier.CODEC);
   public static final LunarEventModifierType<EnchantmentCostModifier> ENCHANTMENT_COST = register("enchantment_cost", EnchantmentCostModifier.CODEC);
   public static final LunarEventModifierType<ExperienceModifier> EXPERIENCE = register("experience", ExperienceModifier.CODEC);
   public static final LunarEventModifierType<BeaconRadiusModifier> BEACON_RADIUS = register("beacon_radius", BeaconRadiusModifier.CODEC);
   public static final LunarEventModifierType<VillageSiegeProbabilityModifier> VILLAGE_SIEGE_PROBABILITY = register(
      "village_siege_probability", VillageSiegeProbabilityModifier.CODEC
   );
   public static final LunarEventModifierType<MobSpawnDistancesModifier> MOB_SPAWN_DISTANCES = register("mob_spawn_distances", MobSpawnDistancesModifier.CODEC);
   public static final LunarEventModifierType<MobSpawnEquipmentModifier> MOB_SPAWN_EQUIPMENT = register("mob_spawn_equipment", MobSpawnEquipmentModifier.CODEC);
   public static final LunarEventModifierType<ExistingMobEquipmentModifier> EXISTING_MOB_EQUIPMENT = register(
      "existing_mob_equipment", ExistingMobEquipmentModifier.CODEC
   );
   @Deprecated(
      since = "2.0.2.0",
      forRemoval = true
   )
   public static final LunarEventModifierType<BlockItemDropModifier> ITEM_DROP = register("item_drop", warnLegacyItemDrop(BlockItemDropModifier.CODEC));

   private LunarEventModifierTypes() {
   }

   private static MapCodec<BlockItemDropModifier> warnLegacyItemDrop(MapCodec<BlockItemDropModifier> codec) {
      return codec.xmap(
         modifier -> {
            EC2Constants.LOGGER
               .warn(
                  "Lunar event modifier type '{}:item_drop' is deprecated and will be removed, use '{}:block_item_drop' instead.",
                  "enhancedcelestials2core",
                  "enhancedcelestials2core"
               );
            return modifier;
         },
         modifier -> modifier
      );
   }

   public static <T extends LunarEventModifier> LunarEventModifierType<T> register(String id, MapCodec<T> codec) {
      MapCodec<T> settingsCodec = codec.fieldOf("settings");
      LunarEventModifierType<T> type = () -> settingsCodec;
      RegistrationService.INSTANCE
         .register((Registry<LunarEventModifierType<T>>)EnhancedCelestialsRegistry.LUNAR_EVENT_MODIFIER_TYPE, "enhancedcelestials2core", id, () -> type);
      return type;
   }

   public static void loadClass() {
   }
}
