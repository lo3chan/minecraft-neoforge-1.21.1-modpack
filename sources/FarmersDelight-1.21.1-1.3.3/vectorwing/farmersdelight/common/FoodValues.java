package vectorwing.farmersdelight.common;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import vectorwing.farmersdelight.common.registry.ModEffects;

public class FoodValues {
   public static final int BRIEF_DURATION = 600;
   public static final int SHORT_DURATION = 1200;
   public static final int MEDIUM_DURATION = 3600;
   public static final int LONG_DURATION = 6000;
   public static final FoodProperties CABBAGE = new Builder().nutrition(2).saturationModifier(0.4F).build();
   public static final FoodProperties TOMATO = new Builder().nutrition(1).saturationModifier(0.3F).build();
   public static final FoodProperties ONION = new Builder().nutrition(2).saturationModifier(0.4F).build();
   public static final FoodProperties APPLE_CIDER = new Builder()
      .alwaysEdible()
      .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 1200, 0), 1.0F)
      .build();
   public static final FoodProperties FRIED_EGG = new Builder().nutrition(4).saturationModifier(0.4F).build();
   public static final FoodProperties TOMATO_SAUCE = new Builder().nutrition(4).saturationModifier(0.4F).build();
   public static final FoodProperties WHEAT_DOUGH = new Builder()
      .nutrition(2)
      .saturationModifier(0.3F)
      .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3F)
      .build();
   public static final FoodProperties RAW_PASTA = new Builder()
      .nutrition(2)
      .saturationModifier(0.3F)
      .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3F)
      .build();
   public static final FoodProperties PIE_CRUST = new Builder().nutrition(2).saturationModifier(0.2F).build();
   public static final FoodProperties PUMPKIN_SLICE = new Builder().nutrition(3).saturationModifier(0.3F).build();
   public static final FoodProperties CABBAGE_LEAF = new Builder().nutrition(1).saturationModifier(0.4F).fast().build();
   public static final FoodProperties MINCED_BEEF = new Builder().nutrition(2).saturationModifier(0.3F).fast().build();
   public static final FoodProperties BEEF_PATTY = new Builder().nutrition(4).saturationModifier(0.8F).fast().build();
   public static final FoodProperties CHICKEN_CUTS = new Builder()
      .nutrition(1)
      .saturationModifier(0.3F)
      .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3F)
      .fast()
      .build();
   public static final FoodProperties COOKED_CHICKEN_CUTS = new Builder().nutrition(3).saturationModifier(0.6F).fast().build();
   public static final FoodProperties BACON = new Builder().nutrition(2).saturationModifier(0.3F).fast().build();
   public static final FoodProperties COOKED_BACON = new Builder().nutrition(4).saturationModifier(0.8F).fast().build();
   public static final FoodProperties COD_SLICE = new Builder().nutrition(1).saturationModifier(0.1F).fast().build();
   public static final FoodProperties COOKED_COD_SLICE = new Builder().nutrition(3).saturationModifier(0.5F).fast().build();
   public static final FoodProperties SALMON_SLICE = new Builder().nutrition(1).saturationModifier(0.1F).fast().build();
   public static final FoodProperties COOKED_SALMON_SLICE = new Builder().nutrition(3).saturationModifier(0.8F).fast().build();
   public static final FoodProperties MUTTON_CHOPS = new Builder().nutrition(1).saturationModifier(0.3F).fast().build();
   public static final FoodProperties COOKED_MUTTON_CHOPS = new Builder().nutrition(3).saturationModifier(0.8F).fast().build();
   public static final FoodProperties HAM = new Builder().nutrition(5).saturationModifier(0.3F).build();
   public static final FoodProperties SMOKED_HAM = new Builder().nutrition(10).saturationModifier(0.8F).build();
   public static final FoodProperties POPSICLE = new Builder().nutrition(3).saturationModifier(0.2F).fast().alwaysEdible().build();
   public static final FoodProperties COOKIES = new Builder().nutrition(2).saturationModifier(0.1F).fast().build();
   public static final FoodProperties CAKE_SLICE = new Builder()
      .nutrition(2)
      .saturationModifier(0.1F)
      .fast()
      .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 400, 0, false, false), 1.0F)
      .build();
   public static final FoodProperties PIE_SLICE = new Builder()
      .nutrition(3)
      .saturationModifier(0.3F)
      .fast()
      .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 0, false, false), 1.0F)
      .build();
   public static final FoodProperties FRUIT_SALAD = new Builder()
      .nutrition(6)
      .saturationModifier(0.6F)
      .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 0), 1.0F)
      .build();
   public static final FoodProperties GLOW_BERRY_CUSTARD = new Builder()
      .nutrition(7)
      .saturationModifier(0.6F)
      .alwaysEdible()
      .effect(() -> new MobEffectInstance(MobEffects.GLOWING, 100, 0), 1.0F)
      .build();
   public static final FoodProperties MIXED_SALAD = new Builder()
      .nutrition(6)
      .saturationModifier(0.6F)
      .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 0), 1.0F)
      .build();
   public static final FoodProperties NETHER_SALAD = new Builder()
      .nutrition(5)
      .saturationModifier(0.4F)
      .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 240, 0), 0.3F)
      .build();
   public static final FoodProperties BARBECUE_STICK = new Builder().nutrition(8).saturationModifier(0.9F).build();
   public static final FoodProperties EGG_SANDWICH = new Builder().nutrition(8).saturationModifier(0.8F).build();
   public static final FoodProperties CHICKEN_SANDWICH = new Builder().nutrition(10).saturationModifier(0.8F).build();
   public static final FoodProperties HAMBURGER = new Builder().nutrition(11).saturationModifier(0.8F).build();
   public static final FoodProperties BACON_SANDWICH = new Builder().nutrition(10).saturationModifier(0.8F).build();
   public static final FoodProperties MUTTON_WRAP = new Builder().nutrition(10).saturationModifier(0.8F).build();
   public static final FoodProperties DUMPLINGS = new Builder().nutrition(8).saturationModifier(0.8F).build();
   public static final FoodProperties STUFFED_POTATO = new Builder().nutrition(10).saturationModifier(0.7F).build();
   public static final FoodProperties CABBAGE_ROLLS = new Builder().nutrition(5).saturationModifier(0.5F).build();
   public static final FoodProperties SALMON_ROLL = new Builder().nutrition(7).saturationModifier(0.6F).build();
   public static final FoodProperties COD_ROLL = new Builder().nutrition(7).saturationModifier(0.6F).build();
   public static final FoodProperties KELP_ROLL = new FoodProperties(12, 12.0F, false, 2.4F, Optional.empty(), List.of());
   public static final FoodProperties KELP_ROLL_SLICE = new Builder().nutrition(6).saturationModifier(0.5F).fast().build();
   public static final FoodProperties COOKED_RICE = new Builder().nutrition(6).saturationModifier(0.4F).effect(() -> nourishment(600), 1.0F).build();
   public static final FoodProperties BONE_BROTH = new Builder().nutrition(8).saturationModifier(0.7F).effect(() -> nourishment(1200), 1.0F).build();
   public static final FoodProperties BEEF_STEW = new Builder().nutrition(12).saturationModifier(0.8F).effect(() -> nourishment(3600), 1.0F).build();
   public static final FoodProperties VEGETABLE_SOUP = new Builder().nutrition(12).saturationModifier(0.8F).effect(() -> nourishment(3600), 1.0F).build();
   public static final FoodProperties FISH_STEW = new Builder().nutrition(12).saturationModifier(0.8F).effect(() -> nourishment(3600), 1.0F).build();
   public static final FoodProperties ONION_SOUP = new Builder().nutrition(12).saturationModifier(0.8F).effect(() -> nourishment(3600), 1.0F).build();
   public static final FoodProperties CHICKEN_SOUP = new Builder().nutrition(12).saturationModifier(0.8F).effect(() -> nourishment(3600), 1.0F).build();
   public static final FoodProperties FRIED_RICE = new Builder().nutrition(12).saturationModifier(0.8F).effect(() -> nourishment(3600), 1.0F).build();
   public static final FoodProperties PUMPKIN_SOUP = new Builder().nutrition(14).saturationModifier(0.75F).effect(() -> nourishment(6000), 1.0F).build();
   public static final FoodProperties BAKED_COD_STEW = new Builder().nutrition(14).saturationModifier(0.75F).effect(() -> nourishment(6000), 1.0F).build();
   public static final FoodProperties NOODLE_SOUP = new Builder().nutrition(14).saturationModifier(0.75F).effect(() -> nourishment(6000), 1.0F).build();
   public static final FoodProperties BACON_AND_EGGS = new Builder().nutrition(10).saturationModifier(0.6F).effect(() -> nourishment(1200), 1.0F).build();
   public static final FoodProperties RATATOUILLE = new Builder().nutrition(10).saturationModifier(0.6F).effect(() -> nourishment(1200), 1.0F).build();
   public static final FoodProperties STEAK_AND_POTATOES = new Builder().nutrition(12).saturationModifier(0.8F).effect(() -> nourishment(3600), 1.0F).build();
   public static final FoodProperties PASTA_WITH_MEATBALLS = new Builder().nutrition(12).saturationModifier(0.8F).effect(() -> nourishment(3600), 1.0F).build();
   public static final FoodProperties PASTA_WITH_MUTTON_CHOP = new Builder()
      .nutrition(12)
      .saturationModifier(0.8F)
      .effect(() -> nourishment(3600), 1.0F)
      .build();
   public static final FoodProperties MUSHROOM_RICE = new Builder().nutrition(12).saturationModifier(0.8F).effect(() -> nourishment(3600), 1.0F).build();
   public static final FoodProperties ROASTED_MUTTON_CHOPS = new Builder()
      .nutrition(14)
      .saturationModifier(0.75F)
      .effect(() -> nourishment(6000), 1.0F)
      .build();
   public static final FoodProperties VEGETABLE_NOODLES = new Builder().nutrition(14).saturationModifier(0.75F).effect(() -> nourishment(6000), 1.0F).build();
   public static final FoodProperties SQUID_INK_PASTA = new Builder().nutrition(14).saturationModifier(0.75F).effect(() -> nourishment(6000), 1.0F).build();
   public static final FoodProperties GRILLED_SALMON = new Builder().nutrition(14).saturationModifier(0.75F).effect(() -> nourishment(3600), 1.0F).build();
   public static final FoodProperties ROAST_CHICKEN = new Builder().nutrition(14).saturationModifier(0.75F).effect(() -> nourishment(6000), 1.0F).build();
   public static final FoodProperties STUFFED_PUMPKIN = new Builder().nutrition(14).saturationModifier(0.75F).effect(() -> nourishment(6000), 1.0F).build();
   public static final FoodProperties HONEY_GLAZED_HAM = new Builder().nutrition(14).saturationModifier(0.75F).effect(() -> nourishment(6000), 1.0F).build();
   public static final FoodProperties SHEPHERDS_PIE = new Builder().nutrition(14).saturationModifier(0.75F).effect(() -> nourishment(6000), 1.0F).build();
   public static final FoodProperties GLEAMING_SALAD = new Builder().nutrition(14).saturationModifier(0.75F).effect(() -> nourishment(6000), 1.0F).build();
   public static final FoodProperties DOG_FOOD = new Builder().nutrition(4).saturationModifier(0.2F).build();
   public static final Map<Item, FoodProperties> VANILLA_SOUP_EFFECTS = new com.google.common.collect.ImmutableMap.Builder()
      .put(Items.MUSHROOM_STEW, new Builder().effect(() -> nourishment(3600), 1.0F).build())
      .put(Items.BEETROOT_SOUP, new Builder().effect(() -> nourishment(3600), 1.0F).build())
      .put(Items.RABBIT_STEW, new Builder().effect(() -> nourishment(6000), 1.0F).build())
      .build();
   public static final FoodProperties RABBIT_STEW_BUFF = new Builder()
      .nutrition(14)
      .saturationModifier(0.75F)
      .effect(() -> nourishment(6000), 1.0F)
      .usingConvertsTo(Items.BOWL)
      .build();

   public static MobEffectInstance nourishment(int duration) {
      return new MobEffectInstance(ModEffects.NOURISHMENT, duration, 0, false, false);
   }
}
