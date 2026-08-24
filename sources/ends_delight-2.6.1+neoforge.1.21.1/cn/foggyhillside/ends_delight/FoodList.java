package cn.foggyhillside.ends_delight;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.Builder;
import vectorwing.farmersdelight.common.registry.ModEffects;

public class FoodList {
   public static final FoodProperties CHORUS_FRUIT_GRAIN = new Builder().nutrition(1).saturationModifier(0.6F).fast().build();
   public static final FoodProperties CHORUS_FRUIT_MILK_TEA = new Builder().nutrition(6).saturationModifier(0.5F).alwaysEdible().build();
   public static final FoodProperties BUBBLE_TEA = new Builder().nutrition(8).saturationModifier(0.5F).alwaysEdible().build();
   public static final FoodProperties DRAGON_BREATH_SODA = new Builder()
      .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1200, 1), 1.0F)
      .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 0), 1.0F)
      .alwaysEdible()
      .build();
   public static final FoodProperties SHULKER_MEAT = new Builder()
      .nutrition(4)
      .saturationModifier(0.5F)
      .effect(() -> new MobEffectInstance(MobEffects.LEVITATION, 80, 1), 1.0F)
      .alwaysEdible()
      .build();
   public static final FoodProperties SHULKER_MEAT_SLICE = new Builder()
      .nutrition(2)
      .saturationModifier(0.6F)
      .effect(() -> new MobEffectInstance(MobEffects.LEVITATION, 40, 1), 1.0F)
      .fast()
      .alwaysEdible()
      .build();
   public static final FoodProperties STIR_FRIED_SHULKER_MEAT = new Builder()
      .nutrition(10)
      .saturationModifier(0.8F)
      .effect(() -> new MobEffectInstance(MobEffects.LEVITATION, 100, 1), 1.0F)
      .effect(() -> new MobEffectInstance(ModEffects.NOURISHMENT, 3600, 0), 1.0F)
      .alwaysEdible()
      .build();
   public static final FoodProperties FRIED_DRAGON_EGG = new Builder()
      .nutrition(20)
      .saturationModifier(0.5F)
      .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 4800, 2), 1.0F)
      .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 4800, 2), 1.0F)
      .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 4800, 2), 1.0F)
      .alwaysEdible()
      .build();
   public static final FoodProperties DRAGON_LEG = new Builder().nutrition(8).saturationModifier(0.6F).build();
   public static final FoodProperties SMOKED_DRAGON_LEG = new Builder()
      .nutrition(15)
      .saturationModifier(0.6F)
      .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 300, 1), 1.0F)
      .build();
   public static final FoodProperties CHORUS_FRUIT_POPSICLE = new Builder().nutrition(3).saturationModifier(0.2F).alwaysEdible().build();
   public static final FoodProperties CHORUS_FRUIT_WINE = new Builder().nutrition(0).saturationModifier(0.0F).alwaysEdible().build();
   public static final FoodProperties DRAGON_BREATH_AND_CHORUS_SOUP = new Builder()
      .nutrition(16)
      .saturationModifier(0.6F)
      .alwaysEdible()
      .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 400, 1), 1.0F)
      .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1200, 1), 1.0F)
      .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 0), 1.0F)
      .effect(() -> new MobEffectInstance(ModEffects.COMFORT, 3600, 0), 1.0F)
      .build();
   public static final FoodProperties LIQUID_DRAGON_EGG = new Builder()
      .nutrition(14)
      .saturationModifier(0.5F)
      .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 1200, 0), 1.0F)
      .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1200, 0), 1.0F)
      .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 0), 1.0F)
      .effect(() -> new MobEffectInstance(MobEffects.BLINDNESS, 400, 0), 1.0F)
      .alwaysEdible()
      .build();
   public static final FoodProperties STUFFED_RICE_CAKE = new Builder().nutrition(6).saturationModifier(0.7F).alwaysEdible().build();
   public static final FoodProperties RAW_ENDERMITE_MEAT = new Builder().nutrition(3).saturationModifier(0.6F).build();
   public static final FoodProperties DRIED_ENDERMITE_MEAT = new Builder().nutrition(5).saturationModifier(0.6F).build();
   public static final FoodProperties RAW_DRAGON_MEAT = new Builder().nutrition(8).saturationModifier(0.6F).build();
   public static final FoodProperties END_BARBECUE_STICK = new Builder()
      .nutrition(10)
      .saturationModifier(0.7F)
      .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 1), 1.0F)
      .alwaysEdible()
      .build();
   public static final FoodProperties ROASTED_DRAGON_MEAT = new Builder()
      .nutrition(15)
      .saturationModifier(0.6F)
      .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 300, 1), 1.0F)
      .build();
   public static final FoodProperties CHORUS_FLOWER_TEA = new Builder()
      .nutrition(0)
      .saturationModifier(0.0F)
      .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 1), 1.0F)
      .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 0), 1.0F)
      .alwaysEdible()
      .build();
   public static final FoodProperties ROASTED_SHULKER_MEAT = new Builder()
      .nutrition(6)
      .saturationModifier(0.5F)
      .effect(() -> new MobEffectInstance(MobEffects.LEVITATION, 80, 1), 1.0F)
      .alwaysEdible()
      .build();
   public static final FoodProperties ROASTED_SHULKER_MEAT_SLICE = new Builder()
      .nutrition(3)
      .saturationModifier(0.8F)
      .effect(() -> new MobEffectInstance(MobEffects.LEVITATION, 40, 1), 1.0F)
      .fast()
      .alwaysEdible()
      .build();
   public static final FoodProperties RAW_DRAGON_MEAT_CUTS = new Builder().nutrition(3).saturationModifier(0.6F).fast().build();
   public static final FoodProperties ROASTED_DRAGON_MEAT_CUTS = new Builder()
      .nutrition(6)
      .saturationModifier(0.6F)
      .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 1), 1.0F)
      .fast()
      .build();
   public static final FoodProperties DRAGON_MEAT_STEW = new Builder()
      .nutrition(10)
      .saturationModifier(0.6F)
      .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 1500, 2), 1.0F)
      .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 300, 1), 1.0F)
      .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 1), 1.0F)
      .effect(() -> new MobEffectInstance(ModEffects.NOURISHMENT, 6000, 0), 1.0F)
      .build();
   public static final FoodProperties CHORUS_SUCCULENT = new Builder().nutrition(2).saturationModifier(0.8F).fast().build();
   public static final FoodProperties END_MIXED_SALAD = new Builder()
      .nutrition(10)
      .saturationModifier(0.6F)
      .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 120, 0), 1.0F)
      .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 0), 1.0F)
      .build();
   public static final FoodProperties ASSORTED_SALAD = new Builder()
      .nutrition(14)
      .saturationModifier(0.5F)
      .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 200, 0), 1.0F)
      .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 500, 0), 1.0F)
      .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 500, 0), 1.0F)
      .build();
   public static final FoodProperties CHORUS_FLOWER_PIE = new Builder()
      .nutrition(4)
      .saturationModifier(0.6F)
      .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 0), 1.0F)
      .build();
   public static final FoodProperties STEAMED_DRAGON_EGG = new Builder()
      .nutrition(12)
      .saturationModifier(0.6F)
      .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 1200, 2), 1.0F)
      .effect(() -> new MobEffectInstance(ModEffects.NOURISHMENT, 6000, 0), 1.0F)
      .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 1200, 2), 1.0F)
      .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1200, 2), 1.0F)
      .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 2), 1.0F)
      .build();
   public static final FoodProperties GRILLED_SHULKER = new Builder()
      .nutrition(8)
      .saturationModifier(0.6F)
      .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 0), 1.0F)
      .effect(() -> new MobEffectInstance(MobEffects.LEVITATION, 40, 1), 1.0F)
      .effect(() -> new MobEffectInstance(ModEffects.NOURISHMENT, 6000, 0), 1.0F)
      .build();
   public static final FoodProperties ROASTED_DRAGON_STEAK = new Builder()
      .nutrition(18)
      .saturationModifier(0.5F)
      .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 500, 1), 1.0F)
      .effect(() -> new MobEffectInstance(ModEffects.NOURISHMENT, 6000, 0), 1.0F)
      .alwaysEdible()
      .build();
   public static final FoodProperties CHORUS_SAUCE = new Builder().nutrition(3).saturationModifier(0.5F).build();
   public static final FoodProperties CHORUS_COOKIE = new Builder().nutrition(2).saturationModifier(0.6F).build();
   public static final FoodProperties ENDER_CONGEE = new Builder()
      .nutrition(10)
      .saturationModifier(0.6F)
      .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 300, 1), 1.0F)
      .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 900, 1), 1.0F)
      .effect(() -> new MobEffectInstance(MobEffects.HEALTH_BOOST, 900, 2), 1.0F)
      .effect(() -> new MobEffectInstance(ModEffects.COMFORT, 3600, 0), 1.0F)
      .alwaysEdible()
      .build();
   public static final FoodProperties DRAGON_LEG_WITH_SAUCE = new Builder()
      .nutrition(10)
      .saturationModifier(0.7F)
      .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 1500, 2), 1.0F)
      .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 300, 1), 1.0F)
      .effect(() -> new MobEffectInstance(ModEffects.NOURISHMENT, 6000, 0), 1.0F)
      .build();
   public static final FoodProperties ENDERMAN_GRISTLE = new Builder().nutrition(2).saturationModifier(0.8F).fast().alwaysEdible().build();
   public static final FoodProperties RAW_ENDER_SAUSAGE = new Builder().nutrition(6).saturationModifier(0.6F).alwaysEdible().build();
   public static final FoodProperties ENDER_SAUSAGE = new Builder()
      .nutrition(9)
      .saturationModifier(0.6F)
      .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 160, 1), 1.0F)
      .alwaysEdible()
      .build();
   public static final FoodProperties ENDERMAN_GRISTLE_STEW = new Builder()
      .nutrition(7)
      .saturationModifier(0.7F)
      .effect(() -> new MobEffectInstance(ModEffects.NOURISHMENT, 3600, 0), 1.0F)
      .alwaysEdible()
      .build();
   public static final FoodProperties SHULKER_SOUP = new Builder()
      .nutrition(8)
      .saturationModifier(0.6F)
      .effect(() -> new MobEffectInstance(MobEffects.LEVITATION, 100, 1), 1.0F)
      .effect(() -> new MobEffectInstance(ModEffects.COMFORT, 3600, 0), 1.0F)
      .alwaysEdible()
      .build();
   public static final FoodProperties ENDER_NOODLE = new Builder()
      .nutrition(8)
      .saturationModifier(0.6F)
      .effect(() -> new MobEffectInstance(MobEffects.LEVITATION, 50, 1), 1.0F)
      .effect(() -> new MobEffectInstance(ModEffects.COMFORT, 3600, 0), 1.0F)
      .alwaysEdible()
      .build();
   public static final FoodProperties SHULKER_OMELETTE_MIXTURE = new Builder()
      .nutrition(5)
      .saturationModifier(0.5F)
      .effect(() -> new MobEffectInstance(MobEffects.LEVITATION, 100, 1), 1.0F)
      .alwaysEdible()
      .build();
   public static final FoodProperties SHULKER_OMELETTE = new Builder()
      .nutrition(11)
      .saturationModifier(0.6F)
      .effect(() -> new MobEffectInstance(MobEffects.LEVITATION, 100, 1), 1.0F)
      .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 0), 1.0F)
      .alwaysEdible()
      .build();
   public static final FoodProperties ENDER_BAMBOO_RICE = new Builder()
      .nutrition(15)
      .saturationModifier(0.6F)
      .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 300, 1), 1.0F)
      .effect(() -> new MobEffectInstance(ModEffects.NOURISHMENT, 3600, 0), 1.0F)
      .build();
}
