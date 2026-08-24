package dev.corgitaco.enhancedcelestials2defaultlunarevents.core.lunarevent;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import corgitaco.corgilib.entity.condition.AnyCondition;
import dev.corgitaco.enhancedcelestials2core.api.EnhancedCelestialsRegistry;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.DropSettings;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarTextComponents;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.MobEffectInstanceBuilder;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.AnvilCostModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.BeaconRadiusModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.BlockItemDropModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.BlockSleepingModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.EnchantmentCostModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.ExperienceModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.ForceSurfaceSpawningModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.LunarEventModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.MobEffectsModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.MobSpawnDistancesModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.MoonSizeModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.MoonTextureColorModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.NameColorModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.SkyLightColorModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.SlimesSpawnEverywhereModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.SoundTrackModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.SpawnCategoryMultiplierModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.TextComponentsModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.VillageSiegeProbabilityModifier;
import dev.corgitaco.enhancedcelestials2core.util.CustomTranslationTextComponent;
import dev.corgitaco.enhancedcelestials2defaultlunarevents.EnhancedCelestialsDefaultLunarEvents;
import dev.corgitaco.enhancedcelestials2defaultlunarevents.api.ECItemTags;
import dev.corgitaco.enhancedcelestials2defaultlunarevents.core.ECSounds;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Items;

public class StandardLunarEventModifiers {
   public static final Map<ResourceKey<LunarEventModifier>, StandardLunarEventModifiers.LunarEventModifierFactory> LUNAR_EVENT_MODIFIER_FACTORIES = new Reference2ObjectOpenHashMap();
   public static final ResourceKey<LunarEventModifier> SUPER_MOON_SKY_LIGHT_COLOR = createModifier(
      "super_moon_sky_light_color", () -> new SkyLightColorModifier("6766ff")
   );
   public static final ResourceKey<LunarEventModifier> SUPER_MOON_MOON_TEXTURE_COLOR = createModifier(
      "super_moon_moon_texture_color", () -> new MoonTextureColorModifier("ffffff")
   );
   public static final ResourceKey<LunarEventModifier> SUPER_MOON_MOON_SIZE = createModifier("super_moon_moon_size", () -> new MoonSizeModifier(40.0F));
   public static final ResourceKey<LunarEventModifier> SUPER_MOON_TEXT_COMPONENTS = createModifier(
      "super_moon_text_components",
      () -> new TextComponentsModifier(
         new LunarTextComponents(
            new CustomTranslationTextComponent("enhancedcelestials2defaultlunarevents.notification.super_moon.rise", new CustomTranslationTextComponent[0]),
            new CustomTranslationTextComponent("enhancedcelestials2defaultlunarevents.notification.super_moon.set", new CustomTranslationTextComponent[0])
         )
      )
   );
   public static final ResourceKey<LunarEventModifier> SUPER_MOON_SLIMES_SPAWN_EVERYWHERE = createModifier(
      "super_moon_slimes_spawn_everywhere", SlimesSpawnEverywhereModifier::new
   );
   public static final ResourceKey<LunarEventModifier> BLOOD_MOON_SKY_LIGHT_COLOR = createModifier(
      "blood_moon_sky_light_color", () -> new SkyLightColorModifier("FF746C")
   );
   public static final ResourceKey<LunarEventModifier> BLOOD_MOON_MOON_TEXTURE_COLOR = createModifier(
      "blood_moon_moon_texture_color", () -> new MoonTextureColorModifier("990000")
   );
   public static final ResourceKey<LunarEventModifier> BLOOD_MOON_SOUND_TRACK = createModifier(
      "blood_moon_sound_track", () -> new SoundTrackModifier(ECSounds.BLOOD_MOON.get())
   );
   public static final ResourceKey<LunarEventModifier> BLOOD_MOON_TEXT_COMPONENTS = createModifier(
      "blood_moon_text_components",
      () -> new TextComponentsModifier(
         new LunarTextComponents(
            new CustomTranslationTextComponent(
               "enhancedcelestials2defaultlunarevents.notification.blood_moon.rise",
               Style.EMPTY.applyFormat(ChatFormatting.RED),
               new CustomTranslationTextComponent[0]
            ),
            new CustomTranslationTextComponent("enhancedcelestials2defaultlunarevents.notification.blood_moon.set", new CustomTranslationTextComponent[0])
         )
      )
   );
   public static final ResourceKey<LunarEventModifier> BLOOD_MOON_NAME_COLOR = createModifier(
      "blood_moon_name_color", () -> new NameColorModifier(ChatFormatting.RED)
   );
   public static final ResourceKey<LunarEventModifier> BLOOD_MOON_SPAWN_CATEGORY_MULTIPLIER = createModifier(
      "blood_moon_spawn_category_multiplier", () -> new SpawnCategoryMultiplierModifier(Map.of(MobCategory.MONSTER, 2.25))
   );
   public static final ResourceKey<LunarEventModifier> BLOOD_MOON_FORCE_SURFACE_SPAWNING = createModifier(
      "blood_moon_force_surface_spawning", ForceSurfaceSpawningModifier::new
   );
   public static final ResourceKey<LunarEventModifier> BLOOD_MOON_BLOCK_SLEEPING = createModifier(
      "blood_moon_block_sleeping", () -> new BlockSleepingModifier(AnyCondition.INSTANCE)
   );
   public static final ResourceKey<LunarEventModifier> BLOOD_MOON_VILLAGE_SIEGE_PROBABILITY = createModifier(
      "blood_moon_village_siege_probability", () -> new VillageSiegeProbabilityModifier(0.5)
   );
   public static final ResourceKey<LunarEventModifier> BLOOD_MOON_MOB_SPAWN_DISTANCES = createModifier(
      "blood_moon_mob_spawn_distances", () -> new MobSpawnDistancesModifier(Map.of(MobCategory.MONSTER, 64))
   );
   public static final ResourceKey<LunarEventModifier> SUPER_BLOOD_MOON_SKY_LIGHT_COLOR = createModifier(
      "super_blood_moon_sky_light_color", () -> new SkyLightColorModifier("ff584f")
   );
   public static final ResourceKey<LunarEventModifier> SUPER_BLOOD_MOON_MOON_TEXTURE_COLOR = createModifier(
      "super_blood_moon_moon_texture_color", () -> new MoonTextureColorModifier("FF0000")
   );
   public static final ResourceKey<LunarEventModifier> SUPER_BLOOD_MOON_MOON_SIZE = createModifier(
      "super_blood_moon_moon_size", () -> new MoonSizeModifier(40.0F)
   );
   public static final ResourceKey<LunarEventModifier> SUPER_BLOOD_MOON_SOUND_TRACK = createModifier(
      "super_blood_moon_sound_track", () -> new SoundTrackModifier(ECSounds.BLOOD_MOON.get())
   );
   public static final ResourceKey<LunarEventModifier> SUPER_BLOOD_MOON_TEXT_COMPONENTS = createModifier(
      "super_blood_moon_text_components",
      () -> new TextComponentsModifier(
         new LunarTextComponents(
            new CustomTranslationTextComponent(
               "enhancedcelestials2defaultlunarevents.notification.super_blood_moon.rise",
               Style.EMPTY.applyFormat(ChatFormatting.RED).applyFormat(ChatFormatting.BOLD),
               new CustomTranslationTextComponent[0]
            ),
            new CustomTranslationTextComponent("enhancedcelestials2defaultlunarevents.notification.blood_moon.set", new CustomTranslationTextComponent[0])
         )
      )
   );
   public static final ResourceKey<LunarEventModifier> SUPER_BLOOD_MOON_NAME_COLOR = createModifier(
      "super_blood_moon_name_color", () -> new NameColorModifier(ChatFormatting.RED)
   );
   public static final ResourceKey<LunarEventModifier> SUPER_BLOOD_MOON_SPAWN_CATEGORY_MULTIPLIER = createModifier(
      "super_blood_moon_spawn_category_multiplier", () -> new SpawnCategoryMultiplierModifier(Map.of(MobCategory.MONSTER, 4.5))
   );
   public static final ResourceKey<LunarEventModifier> SUPER_BLOOD_MOON_FORCE_SURFACE_SPAWNING = createModifier(
      "super_blood_moon_force_surface_spawning", ForceSurfaceSpawningModifier::new
   );
   public static final ResourceKey<LunarEventModifier> SUPER_BLOOD_MOON_BLOCK_SLEEPING = createModifier(
      "super_blood_moon_block_sleeping", () -> new BlockSleepingModifier(AnyCondition.INSTANCE)
   );
   public static final ResourceKey<LunarEventModifier> SUPER_BLOOD_MOON_VILLAGE_SIEGE_PROBABILITY = createModifier(
      "super_blood_moon_village_siege_probability", () -> new VillageSiegeProbabilityModifier(1.0)
   );
   public static final ResourceKey<LunarEventModifier> SUPER_BLOOD_MOON_MOB_SPAWN_DISTANCES = createModifier(
      "super_blood_moon_mob_spawn_distances", () -> new MobSpawnDistancesModifier(Map.of(MobCategory.MONSTER, 32))
   );
   public static final ResourceKey<LunarEventModifier> HARVEST_MOON_SKY_LIGHT_COLOR = createModifier(
      "harvest_moon_sky_light_color", () -> new SkyLightColorModifier("99833b")
   );
   public static final ResourceKey<LunarEventModifier> HARVEST_MOON_MOON_TEXTURE_COLOR = createModifier(
      "harvest_moon_moon_texture_color", () -> new MoonTextureColorModifier("665828")
   );
   public static final ResourceKey<LunarEventModifier> HARVEST_MOON_SOUND_TRACK = createModifier(
      "harvest_moon_sound_track", () -> new SoundTrackModifier(ECSounds.HARVEST_MOON.get())
   );
   public static final ResourceKey<LunarEventModifier> HARVEST_MOON_TEXT_COMPONENTS = createModifier(
      "harvest_moon_text_components",
      () -> new TextComponentsModifier(
         new LunarTextComponents(
            new CustomTranslationTextComponent(
               "enhancedcelestials2defaultlunarevents.notification.harvest_moon.rise",
               Style.EMPTY.applyFormat(ChatFormatting.YELLOW),
               new CustomTranslationTextComponent[0]
            ),
            new CustomTranslationTextComponent("enhancedcelestials2defaultlunarevents.notification.harvest_moon.set", new CustomTranslationTextComponent[0])
         )
      )
   );
   public static final ResourceKey<LunarEventModifier> HARVEST_MOON_NAME_COLOR = createModifier(
      "harvest_moon_name_color", () -> new NameColorModifier(ChatFormatting.YELLOW)
   );
   public static final ResourceKey<LunarEventModifier> HARVEST_MOON_ITEM_DROP = createModifier(
      "harvest_moon_item_drop",
      () -> new BlockItemDropModifier(
         new DropSettings(
            List.of(
               Pair.of(2, Map.of(Either.left(ECItemTags.HARVEST_MOON_CROPS), 2.0)),
               Pair.of(1, Map.of(Either.right(Items.WHEAT.builtInRegistryHolder().key()), 2.0))
            )
         )
      )
   );
   public static final ResourceKey<LunarEventModifier> SUPER_HARVEST_MOON_SKY_LIGHT_COLOR = createModifier(
      "super_harvest_moon_sky_light_color", () -> new SkyLightColorModifier("FFDB63")
   );
   public static final ResourceKey<LunarEventModifier> SUPER_HARVEST_MOON_MOON_TEXTURE_COLOR = createModifier(
      "super_harvest_moon_moon_texture_color", () -> new MoonTextureColorModifier("FFDB63")
   );
   public static final ResourceKey<LunarEventModifier> SUPER_HARVEST_MOON_MOON_SIZE = createModifier(
      "super_harvest_moon_moon_size", () -> new MoonSizeModifier(40.0F)
   );
   public static final ResourceKey<LunarEventModifier> SUPER_HARVEST_MOON_SOUND_TRACK = createModifier(
      "super_harvest_moon_sound_track", () -> new SoundTrackModifier(ECSounds.HARVEST_MOON.get())
   );
   public static final ResourceKey<LunarEventModifier> SUPER_HARVEST_MOON_TEXT_COMPONENTS = createModifier(
      "super_harvest_moon_text_components",
      () -> new TextComponentsModifier(
         new LunarTextComponents(
            new CustomTranslationTextComponent(
               "enhancedcelestials2defaultlunarevents.notification.super_harvest_moon.rise",
               Style.EMPTY.applyFormat(ChatFormatting.YELLOW).applyFormat(ChatFormatting.BOLD),
               new CustomTranslationTextComponent[0]
            ),
            new CustomTranslationTextComponent(
               "enhancedcelestials2defaultlunarevents.notification.super_harvest_moon.set", new CustomTranslationTextComponent[0]
            )
         )
      )
   );
   public static final ResourceKey<LunarEventModifier> SUPER_HARVEST_MOON_NAME_COLOR = createModifier(
      "super_harvest_moon_name_color", () -> new NameColorModifier(ChatFormatting.YELLOW)
   );
   public static final ResourceKey<LunarEventModifier> SUPER_HARVEST_MOON_ITEM_DROP = createModifier(
      "super_harvest_moon_item_drop",
      () -> new BlockItemDropModifier(
         new DropSettings(
            List.of(
               Pair.of(2, Map.of(Either.left(ECItemTags.HARVEST_MOON_CROPS), 4.0)),
               Pair.of(1, Map.of(Either.right(Items.WHEAT.builtInRegistryHolder().key()), 4.0))
            )
         )
      )
   );
   public static final ResourceKey<LunarEventModifier> BLUE_MOON_SKY_LIGHT_COLOR = createModifier(
      "blue_moon_sky_light_color", () -> new SkyLightColorModifier("009999")
   );
   public static final ResourceKey<LunarEventModifier> BLUE_MOON_MOON_TEXTURE_COLOR = createModifier(
      "blue_moon_moon_texture_color", () -> new MoonTextureColorModifier("009999")
   );
   public static final ResourceKey<LunarEventModifier> BLUE_MOON_SOUND_TRACK = createModifier(
      "blue_moon_sound_track", () -> new SoundTrackModifier(ECSounds.BLUE_MOON.get())
   );
   public static final ResourceKey<LunarEventModifier> BLUE_MOON_TEXT_COMPONENTS = createModifier(
      "blue_moon_text_components",
      () -> new TextComponentsModifier(
         new LunarTextComponents(
            new CustomTranslationTextComponent(
               "enhancedcelestials2defaultlunarevents.notification.blue_moon.rise",
               Style.EMPTY.applyFormat(ChatFormatting.AQUA),
               new CustomTranslationTextComponent[0]
            ),
            new CustomTranslationTextComponent("enhancedcelestials2defaultlunarevents.notification.blue_moon.set", new CustomTranslationTextComponent[0])
         )
      )
   );
   public static final ResourceKey<LunarEventModifier> BLUE_MOON_NAME_COLOR = createModifier(
      "blue_moon_name_color", () -> new NameColorModifier(ChatFormatting.AQUA)
   );
   public static final ResourceKey<LunarEventModifier> BLUE_MOON_MOB_EFFECTS = createModifier(
      "blue_moon_mob_effects",
      () -> new MobEffectsModifier(
         List.of(Pair.of(AnyCondition.INSTANCE, new MobEffectInstanceBuilder((MobEffect)MobEffects.LUCK.value(), 1210, 0, true, false, false)))
      )
   );
   public static final ResourceKey<LunarEventModifier> BLUE_MOON_ANVIL_COST = createModifier("blue_moon_anvil_cost", () -> new AnvilCostModifier(0.5));
   public static final ResourceKey<LunarEventModifier> BLUE_MOON_ENCHANTMENT_COST = createModifier(
      "blue_moon_enchantment_cost", () -> new EnchantmentCostModifier(0.5)
   );
   public static final ResourceKey<LunarEventModifier> BLUE_MOON_EXPERIENCE = createModifier("blue_moon_experience", () -> new ExperienceModifier(2.0));
   public static final ResourceKey<LunarEventModifier> BLUE_MOON_BEACON_RADIUS = createModifier("blue_moon_beacon_radius", () -> new BeaconRadiusModifier(1.5));
   public static final ResourceKey<LunarEventModifier> SUPER_BLUE_MOON_SKY_LIGHT_COLOR = createModifier(
      "super_blue_moon_sky_light_color", () -> new SkyLightColorModifier("00ffff")
   );
   public static final ResourceKey<LunarEventModifier> SUPER_BLUE_MOON_MOON_TEXTURE_COLOR = createModifier(
      "super_blue_moon_moon_texture_color", () -> new MoonTextureColorModifier("00ffff")
   );
   public static final ResourceKey<LunarEventModifier> SUPER_BLUE_MOON_MOON_SIZE = createModifier(
      "super_blue_moon_moon_size", () -> new MoonSizeModifier(40.0F)
   );
   public static final ResourceKey<LunarEventModifier> SUPER_BLUE_MOON_SOUND_TRACK = createModifier(
      "super_blue_moon_sound_track", () -> new SoundTrackModifier(ECSounds.BLUE_MOON.get())
   );
   public static final ResourceKey<LunarEventModifier> SUPER_BLUE_MOON_TEXT_COMPONENTS = createModifier(
      "super_blue_moon_text_components",
      () -> new TextComponentsModifier(
         new LunarTextComponents(
            new CustomTranslationTextComponent(
               "enhancedcelestials2defaultlunarevents.notification.super_blue_moon.rise",
               Style.EMPTY.applyFormat(ChatFormatting.AQUA),
               new CustomTranslationTextComponent[0]
            ),
            new CustomTranslationTextComponent("enhancedcelestials2defaultlunarevents.notification.super_blue_moon.set", new CustomTranslationTextComponent[0])
         )
      )
   );
   public static final ResourceKey<LunarEventModifier> SUPER_BLUE_MOON_NAME_COLOR = createModifier(
      "super_blue_moon_name_color", () -> new NameColorModifier(ChatFormatting.AQUA)
   );
   public static final ResourceKey<LunarEventModifier> SUPER_BLUE_MOON_MOB_EFFECTS = createModifier(
      "super_blue_moon_mob_effects",
      () -> new MobEffectsModifier(
         List.of(Pair.of(AnyCondition.INSTANCE, new MobEffectInstanceBuilder((MobEffect)MobEffects.LUCK.value(), 1210, 4, true, false, false)))
      )
   );
   public static final ResourceKey<LunarEventModifier> SUPER_BLUE_MOON_ANVIL_COST = createModifier(
      "super_blue_moon_anvil_cost", () -> new AnvilCostModifier(0.25)
   );
   public static final ResourceKey<LunarEventModifier> SUPER_BLUE_MOON_ENCHANTMENT_COST = createModifier(
      "super_blue_moon_enchantment_cost", () -> new EnchantmentCostModifier(0.25)
   );
   public static final ResourceKey<LunarEventModifier> SUPER_BLUE_MOON_EXPERIENCE = createModifier(
      "super_blue_moon_experience", () -> new ExperienceModifier(4.0)
   );
   public static final ResourceKey<LunarEventModifier> SUPER_BLUE_MOON_BEACON_RADIUS = createModifier(
      "super_blue_moon_beacon_radius", () -> new BeaconRadiusModifier(2.0)
   );

   public static ResourceKey<LunarEventModifier> createModifier(String id, Supplier<LunarEventModifier> modifier) {
      ResourceKey<LunarEventModifier> key = ResourceKey.create(
         EnhancedCelestialsRegistry.LUNAR_EVENT_MODIFIER_KEY, EnhancedCelestialsDefaultLunarEvents.createLocation(id)
      );
      LUNAR_EVENT_MODIFIER_FACTORIES.put(key, context -> modifier.get());
      return key;
   }

   public static void loadClass() {
   }

   @FunctionalInterface
   public interface LunarEventModifierFactory {
      LunarEventModifier generate(BootstrapContext<LunarEventModifier> var1);
   }
}
