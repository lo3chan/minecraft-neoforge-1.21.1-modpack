package net.cibernet.alchemancy.registries;

import net.cibernet.alchemancy.properties.Property;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;

public class AlchemancyTags {
   private static <T> TagKey<T> registerCommonTag(ResourceKey<Registry<T>> registry, String key) {
      return TagKey.create(registry, ResourceLocation.fromNamespaceAndPath("c", key));
   }

   private static <T> TagKey<T> registerTag(ResourceKey<Registry<T>> registry, String key) {
      return TagKey.create(registry, ResourceLocation.fromNamespaceAndPath("alchemancy", key));
   }

   public static class Blocks {
      public static final TagKey<Block> ALCHEMANCY_CRYSTAL_CATALYSTS = AlchemancyTags.registerTag(Registries.BLOCK, "alchemancy_crystal_catalysts");
      public static final TagKey<Block> INCORRECT_FOR_LEAD_TOOL = AlchemancyTags.registerTag(Registries.BLOCK, "incorrect_for_lead_tool");
      public static final TagKey<Block> INCORRECT_FOR_DREAMSTEEL_TOOL = AlchemancyTags.registerTag(Registries.BLOCK, "incorrect_for_dreamsteel_tool");
      public static final TagKey<Block> INCORRECT_FOR_FLAME_EMPEROR_TOOL = AlchemancyTags.registerTag(Registries.BLOCK, "incorrect_for_flame_emperor_tool");
      public static final TagKey<Block> SUPPORTS_BLAZEBLOOM = AlchemancyTags.registerTag(Registries.BLOCK, "supports_blazebloom");
      public static final TagKey<Block> REQUIRED_FOR_BLAZEBLOOM_GENERATION = AlchemancyTags.registerTag(Registries.BLOCK, "required_for_blazebloom_generation");
      public static final TagKey<Block> WAYFINDING_TARGETABLE = AlchemancyTags.registerTag(Registries.BLOCK, "wayfinding_targetable");
      public static final TagKey<Block> BROKEN_BY_HARDENED = AlchemancyTags.registerTag(Registries.BLOCK, "broken_by_hardened");
      public static final TagKey<Block> MAGNETIC_STICKS_TO = AlchemancyTags.registerTag(Registries.BLOCK, "magnetic_sticks_to");
      public static final TagKey<Block> CANNOT_ENCAPSULATE = AlchemancyTags.registerTag(Registries.BLOCK, "cannot_encapsulate");
      public static final TagKey<Block> ENCAPSULATING_ALWAYS_PLACES = AlchemancyTags.registerTag(Registries.BLOCK, "encapsulating_always_places");
   }

   public static class DamageTypes {
      public static final TagKey<DamageType> TRIGGERS_ON_HIT_EFFECTS = AlchemancyTags.registerTag(Registries.DAMAGE_TYPE, "triggers_on_hit_effects");
      public static final TagKey<DamageType> TRIGGERS_ON_PROJECTILE_HIT_EFFECTS = AlchemancyTags.registerTag(
         Registries.DAMAGE_TYPE, "triggers_on_projectile_hit_effects"
      );
      public static final TagKey<DamageType> SHOCK_DAMAGE = AlchemancyTags.registerTag(Registries.DAMAGE_TYPE, "shock_damage");
      public static final TagKey<DamageType> ARCANE_DAMAGE = AlchemancyTags.registerTag(Registries.DAMAGE_TYPE, "arcane_damage");
      public static final TagKey<DamageType> AFFECTED_BY_MAGIC_RESISTANT = AlchemancyTags.registerTag(Registries.DAMAGE_TYPE, "affected_by_magic_resistant");
      public static final TagKey<DamageType> AFFECTED_BY_SMOOTH = AlchemancyTags.registerTag(Registries.DAMAGE_TYPE, "affected_by_smooth");
   }

   public static class DataComponents {
      public static final TagKey<DataComponentType<?>> DISABLES_COMPACT = AlchemancyTags.registerTag(Registries.DATA_COMPONENT_TYPE, "disables_compact");
      public static final TagKey<DataComponentType<?>> UNTOGGLEABLE = AlchemancyTags.registerTag(Registries.DATA_COMPONENT_TYPE, "untoggleable");
      public static final TagKey<DataComponentType<?>> FRAGMENTED_DOES_NOT_CLONE = AlchemancyTags.registerTag(
         Registries.DATA_COMPONENT_TYPE, "fragmented_does_not_clone"
      );
      public static final TagKey<DataComponentType<?>> DISABLED_BY_DEAD = AlchemancyTags.registerTag(Registries.DATA_COMPONENT_TYPE, "disabled_by_dead");
   }

   public static class Dimensions {
      public static final TagKey<DimensionType> DEPTH_DWELLER_EFFECTIVE = AlchemancyTags.registerTag(Registries.DIMENSION_TYPE, "depth_dweller_effective");
      public static final TagKey<DimensionType> WAYFINDING_POINTS_TO_ORIGIN = AlchemancyTags.registerTag(
         Registries.DIMENSION_TYPE, "wayfinding_points_to_origin"
      );
   }

   public static final class Enchantments {
      public static final TagKey<Enchantment> BUFFS_BURNING = AlchemancyTags.registerTag(Registries.ENCHANTMENT, "buffs_burning");
   }

   public static class EntityTypes {
      public static final TagKey<EntityType<?>> TEMPTED_BY_SWEET = AlchemancyTags.registerTag(Registries.ENTITY_TYPE, "tempted_by_sweet_property");
      public static final TagKey<EntityType<?>> TEMPTED_BY_WEALTHY = AlchemancyTags.registerTag(Registries.ENTITY_TYPE, "tempted_by_wealthy_property");
      public static final TagKey<EntityType<?>> SCARED_BY_SCARY = AlchemancyTags.registerTag(Registries.ENTITY_TYPE, "scared_by_scary_property");
      public static final TagKey<EntityType<?>> AGGROED_BY_SEEDED = AlchemancyTags.registerTag(Registries.ENTITY_TYPE, "aggroed_by_seeded_property");
      public static final TagKey<EntityType<?>> CANNOT_CAPTURE = AlchemancyTags.registerTag(Registries.ENTITY_TYPE, "cannot_capture");
      public static final TagKey<EntityType<?>> AFFECTED_BY_FRIENDLY = AlchemancyTags.registerTag(Registries.ENTITY_TYPE, "affected_by_friendly");
      public static final TagKey<EntityType<?>> PULLED_IN_BY_MAGNETIC = AlchemancyTags.registerTag(Registries.ENTITY_TYPE, "pulled_in_by_magnetic");
      public static final TagKey<EntityType<?>> TEMPTED_BY_PUTRID = AlchemancyTags.registerTag(Registries.ENTITY_TYPE, "tempted_by_putrid");
      public static final TagKey<EntityType<?>> UNMOVABLE = AlchemancyTags.registerTag(Registries.ENTITY_TYPE, "unmovable");
   }

   public static class Items {
      public static final TagKey<Item> REPAIRS_LEAD = AlchemancyTags.registerTag(Registries.ITEM, "repairs_lead");
      public static final TagKey<Item> REPAIRS_DREAMSTEEL = AlchemancyTags.registerTag(Registries.ITEM, "repairs_dreamsteel");
      public static final TagKey<Item> REPAIRS_FLAME_EMPEROR_TOOL = AlchemancyTags.registerTag(Registries.ITEM, "repairs_flame_emperor_tool");
      public static final TagKey<Item> REPAIRS_UNSHAPED_CLAY = AlchemancyTags.registerTag(Registries.ITEM, "repairs_unshaped_clay");
      public static final TagKey<Item> RESTORES_WAXED = AlchemancyTags.registerTag(Registries.ITEM, "restores_waxed");
      public static final TagKey<Item> IMMUNE_TO_INFUSIONS = AlchemancyTags.registerTag(Registries.ITEM, "immune_to_infusions");
      public static final TagKey<Item> REMOVES_INFUSIONS = AlchemancyTags.registerTag(Registries.ITEM, "removes_infusions");
      public static final TagKey<Item> DISABLES_INFUSION_ABILITIES = AlchemancyTags.registerTag(Registries.ITEM, "disables_infusion_abilities");
      public static final TagKey<Item> IGNORED_BY_INFUSION_FLASK = AlchemancyTags.registerTag(Registries.ITEM, "ignored_by_infusion_flask");
      public static final TagKey<Item> APPLIES_CHROMA_TINT = AlchemancyTags.registerTag(Registries.ITEM, "applies_chroma_tint");
      public static final TagKey<Item> INCREASES_SHOCK_DAMAGE_RECEIVED = AlchemancyTags.registerTag(Registries.ITEM, "doubles_shock_damage_received");
      public static final TagKey<Item> TINT_BASE_LAYER = AlchemancyTags.registerTag(Registries.ITEM, "tint_base_layer");
      public static final TagKey<Item> DONT_TINT_BASE_LAYER = AlchemancyTags.registerTag(Registries.ITEM, "dont_tint_base_layer");
      public static final TagKey<Item> INCREASES_RESIZED = AlchemancyTags.registerTag(Registries.ITEM, "increases_resized");
      public static final TagKey<Item> DECREASES_RESIZED = AlchemancyTags.registerTag(Registries.ITEM, "decreases_resized");
      public static final TagKey<Item> INFUSION_REMOVES_GLINT = AlchemancyTags.registerTag(Registries.ITEM, "infusion_removes_glint");
      public static final TagKey<Item> CANNOT_TINT = AlchemancyTags.registerTag(Registries.ITEM, "cannot_tint");
      public static final TagKey<Item> VALUABLE_UNDYING_SOURCE = AlchemancyTags.registerTag(Registries.ITEM, "valuable_undying_source");
      public static final TagKey<Item> DISABLES_COMPACT = AlchemancyTags.registerTag(Registries.ITEM, "disables_compact");
      public static final TagKey<Item> TRIGGERS_HEARTY = AlchemancyTags.registerTag(Registries.ITEM, "triggers_hearty_on_use");
      public static final TagKey<Item> CODEX_DISCOVERY_ON_PICKUP = AlchemancyTags.registerTag(Registries.ITEM, "codex_discovery_on_pickup");
      public static final TagKey<Item> IS_INFUSED = AlchemancyTags.registerTag(Registries.ITEM, "is_infused");
   }

   public static class MobEffects {
      public static final TagKey<MobEffect> BLOCKS_OMINOUS = AlchemancyTags.registerTag(Registries.MOB_EFFECT, "blocks_ominous");
   }

   public static class Properties {
      public static final TagKey<Property> DISABLES_BLOCK_ATTACK_IN_CREATIVE = AlchemancyTags.registerTag(
         AlchemancyProperties.REGISTRY_KEY, "disables_block_attack_in_creative"
      );
      public static final TagKey<Property> SLOTLESS = AlchemancyTags.registerTag(AlchemancyProperties.REGISTRY_KEY, "slotless");
      public static final TagKey<Property> DISABLED = AlchemancyTags.registerTag(AlchemancyProperties.REGISTRY_KEY, "disabled");
      public static final TagKey<Property> CODEX_UNOBTAINABLE = AlchemancyTags.registerTag(AlchemancyProperties.REGISTRY_KEY, "codex_unobtainable");
      public static final TagKey<Property> CODEX_HIDDEN = AlchemancyTags.registerTag(AlchemancyProperties.REGISTRY_KEY, "codex_hidden");
      public static final TagKey<Property> EXCLUDED_FROM_RANDOMIZER = AlchemancyTags.registerTag(AlchemancyProperties.REGISTRY_KEY, "excluded_from_randomizer");
      public static final TagKey<Property> RETAINED_BY_UNSHAPED_CLAY = AlchemancyTags.registerTag(
         AlchemancyProperties.REGISTRY_KEY, "retained_by_unshaped_clay"
      );
      public static final TagKey<Property> DISABLES_SPARKLING = AlchemancyTags.registerTag(AlchemancyProperties.REGISTRY_KEY, "disables_sparkling");
      public static final TagKey<Property> AFFECTED_BY_MAGNETIC = AlchemancyTags.registerTag(AlchemancyProperties.REGISTRY_KEY, "affected_by_magnetic");
      public static final TagKey<Property> AFFECTED_BY_DIVINE_CLEANSE = AlchemancyTags.registerTag(
         AlchemancyProperties.REGISTRY_KEY, "affected_by_divine_cleanse"
      );
      public static final TagKey<Property> CHANGES_GUST_JET_WIND_COLOR = AlchemancyTags.registerTag(
         AlchemancyProperties.REGISTRY_KEY, "changes_gust_jet_wind_color"
      );
      public static final TagKey<Property> PREVENTS_ENDERMAN_AGGRO = AlchemancyTags.registerTag(AlchemancyProperties.REGISTRY_KEY, "prevents_enderman_aggro");
      public static final TagKey<Property> IGNORED_BY_INFUSION_FLASK = AlchemancyTags.registerTag(
         AlchemancyProperties.REGISTRY_KEY, "ignored_by_infusion_flask"
      );
      public static final TagKey<Property> CANNOT_CLONE_DATA = AlchemancyTags.registerTag(AlchemancyProperties.REGISTRY_KEY, "cannot_clone_data");
      public static final TagKey<Property> CLONED_BY_OVERGROWTH = AlchemancyTags.registerTag(AlchemancyProperties.REGISTRY_KEY, "cloned_by_overgrowth");
      public static final TagKey<Property> BURNT_UP_BY_WILDFIRE = AlchemancyTags.registerTag(AlchemancyProperties.REGISTRY_KEY, "burnt_up_by_wildfire");
   }
}
