package vectorwing.farmersdelight.data;

import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider.TagLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.tag.CommonTags;
import vectorwing.farmersdelight.common.tag.CompatibilityTags;
import vectorwing.farmersdelight.common.tag.ModTags;

public class ItemTags extends ItemTagsProvider {
   public ItemTags(
      PackOutput output,
      CompletableFuture<Provider> provider,
      CompletableFuture<TagLookup<Block>> blockTagProvider,
      @Nullable ExistingFileHelper existingFileHelper
   ) {
      super(output, provider, blockTagProvider, "farmersdelight", existingFileHelper);
   }

   protected void addTags(@NotNull Provider provider) {
      this.copy(ModTags.Blocks.WILD_CROPS, ModTags.Items.WILD_CROPS);
      this.copy(net.minecraft.tags.BlockTags.SMALL_FLOWERS, net.minecraft.tags.ItemTags.SMALL_FLOWERS);
      this.registerMinecraftTags();
      this.registerModTags();
      this.registerNeoForgeTags();
      this.registerCommonTags();
      this.registerCompatibilityTags();
   }

   private void registerMinecraftTags() {
      this.tag(net.minecraft.tags.ItemTags.BREAKS_DECORATED_POTS).addTag(ModTags.Items.KNIVES);
      this.tag(net.minecraft.tags.ItemTags.TALL_FLOWERS).add(ModItems.WILD_RICE.get());
      this.tag(net.minecraft.tags.ItemTags.PIGLIN_LOVED).add(ModItems.GOLDEN_KNIFE.get());
      this.tag(net.minecraft.tags.ItemTags.SIGNS).addTag(ModTags.Items.CANVAS_SIGNS);
      this.tag(net.minecraft.tags.ItemTags.HANGING_SIGNS).addTag(ModTags.Items.HANGING_CANVAS_SIGNS);
      this.tag(net.minecraft.tags.ItemTags.VILLAGER_PLANTABLE_SEEDS)
         .add(ModItems.CABBAGE_SEEDS.get())
         .add(ModItems.TOMATO_SEEDS.get())
         .add(ModItems.ONION.get());
      this.tag(net.minecraft.tags.ItemTags.DURABILITY_ENCHANTABLE).addTag(ModTags.Items.KNIVES).add(ModItems.SKILLET.get());
      this.tag(net.minecraft.tags.ItemTags.WEAPON_ENCHANTABLE).addTag(ModTags.Items.KNIVES).add(ModItems.SKILLET.get());
      this.tag(net.minecraft.tags.ItemTags.SHARP_WEAPON_ENCHANTABLE).addTag(ModTags.Items.KNIVES).add(ModItems.SKILLET.get());
      this.tag(net.minecraft.tags.ItemTags.FIRE_ASPECT_ENCHANTABLE).addTag(ModTags.Items.KNIVES).add(ModItems.SKILLET.get());
      this.tag(net.minecraft.tags.ItemTags.SWORD_ENCHANTABLE).addTag(ModTags.Items.KNIVES).add(ModItems.SKILLET.get());
      this.tag(net.minecraft.tags.ItemTags.MINING_ENCHANTABLE).addTag(ModTags.Items.KNIVES);
      this.tag(net.minecraft.tags.ItemTags.MINING_LOOT_ENCHANTABLE).addTag(ModTags.Items.KNIVES);
      this.tag(net.minecraft.tags.ItemTags.MEAT)
         .add(ModItems.MINCED_BEEF.get())
         .add(ModItems.BEEF_PATTY.get())
         .add(ModItems.CHICKEN_CUTS.get())
         .add(ModItems.COOKED_CHICKEN_CUTS.get())
         .add(ModItems.BACON.get())
         .add(ModItems.COOKED_BACON.get())
         .add(ModItems.MUTTON_CHOPS.get())
         .add(ModItems.COOKED_MUTTON_CHOPS.get())
         .add(ModItems.HAM.get())
         .add(ModItems.SMOKED_HAM.get())
         .add(ModItems.DOG_FOOD.get());
      this.tag(net.minecraft.tags.ItemTags.CAT_FOOD).add(ModItems.SALMON_SLICE.get()).add(ModItems.COD_SLICE.get());
      this.tag(net.minecraft.tags.ItemTags.CHICKEN_FOOD).add(ModItems.CABBAGE_SEEDS.get()).add(ModItems.TOMATO_SEEDS.get()).add(ModItems.RICE.get());
      this.tag(net.minecraft.tags.ItemTags.PIG_FOOD).add(ModItems.CABBAGE.get()).add(ModItems.TOMATO.get());
      this.tag(net.minecraft.tags.ItemTags.RABBIT_FOOD).add(ModItems.CABBAGE.get());
      this.tag(net.minecraft.tags.ItemTags.PARROT_FOOD).add(ModItems.CABBAGE_SEEDS.get()).add(ModItems.TOMATO_SEEDS.get()).add(ModItems.RICE.get());
      this.tag(net.minecraft.tags.ItemTags.HORSE_TEMPT_ITEMS).add(ModItems.HORSE_FEED.get());
   }

   private void registerModTags() {
      this.tag(ModTags.Items.SNACKS)
         .add(
            new Item[]{
               ModItems.BARBECUE_STICK.get(),
               ModItems.EGG_SANDWICH.get(),
               ModItems.CHICKEN_SANDWICH.get(),
               ModItems.HAMBURGER.get(),
               ModItems.BACON_SANDWICH.get(),
               ModItems.MUTTON_WRAP.get(),
               ModItems.DUMPLINGS.get(),
               ModItems.STUFFED_POTATO.get(),
               ModItems.CABBAGE_ROLLS.get(),
               ModItems.SALMON_ROLL.get(),
               ModItems.COD_ROLL.get(),
               ModItems.KELP_ROLL.get(),
               ModItems.KELP_ROLL_SLICE.get()
            }
         );
      this.tag(ModTags.Items.MEALS)
         .add(
            new Item[]{
               Items.MUSHROOM_STEW,
               Items.BEETROOT_SOUP,
               Items.RABBIT_STEW,
               ModItems.MIXED_SALAD.get(),
               ModItems.COOKED_RICE.get(),
               ModItems.BONE_BROTH.get(),
               ModItems.BEEF_STEW.get(),
               ModItems.VEGETABLE_SOUP.get(),
               ModItems.FISH_STEW.get(),
               ModItems.CHICKEN_SOUP.get(),
               ModItems.FRIED_RICE.get(),
               ModItems.PUMPKIN_SOUP.get(),
               ModItems.BAKED_COD_STEW.get(),
               ModItems.NOODLE_SOUP.get(),
               ModItems.ONION_SOUP.get(),
               ModItems.BACON_AND_EGGS.get(),
               ModItems.RATATOUILLE.get(),
               ModItems.STEAK_AND_POTATOES.get(),
               ModItems.PASTA_WITH_MEATBALLS.get(),
               ModItems.PASTA_WITH_MUTTON_CHOP.get(),
               ModItems.MUSHROOM_RICE.get(),
               ModItems.ROASTED_MUTTON_CHOPS.get(),
               ModItems.VEGETABLE_NOODLES.get(),
               ModItems.SQUID_INK_PASTA.get(),
               ModItems.GRILLED_SALMON.get(),
               ModItems.ROAST_CHICKEN.get(),
               ModItems.STUFFED_PUMPKIN.get(),
               ModItems.HONEY_GLAZED_HAM.get(),
               ModItems.SHEPHERDS_PIE.get(),
               ModItems.GLEAMING_SALAD.get()
            }
         );
      this.tag(ModTags.Items.DRINKS)
         .add(new Item[]{ModItems.MILK_BOTTLE.get(), ModItems.APPLE_CIDER.get(), ModItems.MELON_JUICE.get(), ModItems.HOT_COCOA.get()});
      this.tag(ModTags.Items.SWEETS)
         .add(
            new Item[]{
               Items.CAKE,
               Items.COOKIE,
               ModItems.CAKE_SLICE.get(),
               ModItems.APPLE_PIE_SLICE.get(),
               ModItems.SWEET_BERRY_CHEESECAKE_SLICE.get(),
               ModItems.CHOCOLATE_PIE_SLICE.get(),
               ModItems.PUMPKIN_PIE_SLICE.get(),
               ModItems.SWEET_BERRY_COOKIE.get(),
               ModItems.HONEY_COOKIE.get(),
               ModItems.MELON_POPSICLE.get(),
               ModItems.GLOW_BERRY_CUSTARD.get(),
               ModItems.FRUIT_SALAD.get()
            }
         );
      this.copy(ModTags.Blocks.FEASTS, ModTags.Items.FEASTS);
      this.tag(ModTags.Items.PIES)
         .add(new Item[]{Items.PUMPKIN_PIE, ModItems.APPLE_PIE.get(), ModItems.SWEET_BERRY_CHEESECAKE.get(), ModItems.CHOCOLATE_PIE.get()});
      this.tag(ModTags.Items.KNIVES)
         .add(
            new Item[]{
               ModItems.FLINT_KNIFE.get(), ModItems.IRON_KNIFE.get(), ModItems.DIAMOND_KNIFE.get(), ModItems.GOLDEN_KNIFE.get(), ModItems.NETHERITE_KNIFE.get()
            }
         );
      this.tag(ModTags.Items.KNIFE_ENCHANTABLE).addTag(ModTags.Items.KNIVES);
      this.tag(ModTags.Items.STRAW_HARVESTERS).addTag(ModTags.Items.KNIVES);
      this.tag(ModTags.Items.CANVAS_SIGNS)
         .add(ModItems.CANVAS_SIGN.get())
         .add(ModItems.WHITE_CANVAS_SIGN.get())
         .add(ModItems.ORANGE_CANVAS_SIGN.get())
         .add(ModItems.MAGENTA_CANVAS_SIGN.get())
         .add(ModItems.LIGHT_BLUE_CANVAS_SIGN.get())
         .add(ModItems.YELLOW_CANVAS_SIGN.get())
         .add(ModItems.LIME_CANVAS_SIGN.get())
         .add(ModItems.PINK_CANVAS_SIGN.get())
         .add(ModItems.GRAY_CANVAS_SIGN.get())
         .add(ModItems.LIGHT_GRAY_CANVAS_SIGN.get())
         .add(ModItems.CYAN_CANVAS_SIGN.get())
         .add(ModItems.PURPLE_CANVAS_SIGN.get())
         .add(ModItems.BLUE_CANVAS_SIGN.get())
         .add(ModItems.BROWN_CANVAS_SIGN.get())
         .add(ModItems.GREEN_CANVAS_SIGN.get())
         .add(ModItems.RED_CANVAS_SIGN.get())
         .add(ModItems.BLACK_CANVAS_SIGN.get());
      this.tag(ModTags.Items.HANGING_CANVAS_SIGNS)
         .add(ModItems.HANGING_CANVAS_SIGN.get())
         .add(ModItems.WHITE_HANGING_CANVAS_SIGN.get())
         .add(ModItems.ORANGE_HANGING_CANVAS_SIGN.get())
         .add(ModItems.MAGENTA_HANGING_CANVAS_SIGN.get())
         .add(ModItems.LIGHT_BLUE_HANGING_CANVAS_SIGN.get())
         .add(ModItems.YELLOW_HANGING_CANVAS_SIGN.get())
         .add(ModItems.LIME_HANGING_CANVAS_SIGN.get())
         .add(ModItems.PINK_HANGING_CANVAS_SIGN.get())
         .add(ModItems.GRAY_HANGING_CANVAS_SIGN.get())
         .add(ModItems.LIGHT_GRAY_HANGING_CANVAS_SIGN.get())
         .add(ModItems.CYAN_HANGING_CANVAS_SIGN.get())
         .add(ModItems.PURPLE_HANGING_CANVAS_SIGN.get())
         .add(ModItems.BLUE_HANGING_CANVAS_SIGN.get())
         .add(ModItems.BROWN_HANGING_CANVAS_SIGN.get())
         .add(ModItems.GREEN_HANGING_CANVAS_SIGN.get())
         .add(ModItems.RED_HANGING_CANVAS_SIGN.get())
         .add(ModItems.BLACK_HANGING_CANVAS_SIGN.get());
      this.copy(ModTags.Blocks.CABINETS, ModTags.Items.CABINETS);
      this.copy(ModTags.Blocks.CABINETS_WOODEN, ModTags.Items.CABINETS_WOODEN);
      this.copy(ModTags.Blocks.MUSHROOM_COLONIES, ModTags.Items.MUSHROOM_COLONIES);
      this.tag(ModTags.Items.SERVING_CONTAINERS).add(new Item[]{Items.BOWL, Items.GLASS_BOTTLE, Items.BUCKET});
      this.tag(ModTags.Items.FLAT_ON_CUTTING_BOARD)
         .add(new Item[]{Items.TRIDENT, Items.SPYGLASS})
         .addOptional(ResourceLocation.parse("supplementaries:quiver"))
         .addOptional(ResourceLocation.parse("autumnity:turkey"))
         .addOptional(ResourceLocation.parse("autumnity:cooked_turkey"));
   }

   private void registerNeoForgeTags() {
      this.tag(net.neoforged.neoforge.common.Tags.Items.CROPS).addTag(CommonTags.Items.CROPS_GRAIN);
      this.tag(net.neoforged.neoforge.common.Tags.Items.DRINKS).addTag(ModTags.Items.DRINKS);
      this.tag(net.neoforged.neoforge.common.Tags.Items.FOODS)
         .add(ModItems.TOMATO_SAUCE.get())
         .add(ModItems.PIE_CRUST.get())
         .add(ModItems.PUMPKIN_SLICE.get())
         .add(ModItems.HAM.get())
         .add(ModItems.SMOKED_HAM.get())
         .add(ModItems.DOG_FOOD.get())
         .addTag(ModTags.Items.SNACKS)
         .addTag(ModTags.Items.MEALS)
         .addTag(ModTags.Items.SWEETS)
         .addTag(CommonTags.Items.FOODS_LEAFY_GREEN)
         .addTag(CommonTags.Items.FOODS_DOUGH)
         .addTag(CommonTags.Items.FOODS_PASTA)
         .addTag(CommonTags.Items.FOODS_COOKED_EGG);
      this.tag(net.neoforged.neoforge.common.Tags.Items.FENCES).add(ModItems.ROPE_FENCE.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.FENCE_GATES).add(ModItems.ROPE_FENCE_GATE.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.DRINKS_MILK).add(ModItems.MILK_BOTTLE.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.FOODS_VEGETABLE).add(new Item[]{ModItems.ONION.get(), ModItems.TOMATO.get()});
      this.tag(net.neoforged.neoforge.common.Tags.Items.FOODS_COOKIE).add(new Item[]{ModItems.HONEY_COOKIE.get(), ModItems.SWEET_BERRY_COOKIE.get()});
      this.tag(net.neoforged.neoforge.common.Tags.Items.FOODS_DOUGH).addTag(CommonTags.Items.FOODS_DOUGH_WHEAT);
      this.tag(net.neoforged.neoforge.common.Tags.Items.FOODS_RAW_MEAT)
         .addTags(
            new TagKey[]{
               CommonTags.Items.FOODS_RAW_CHICKEN, CommonTags.Items.FOODS_RAW_PORK, CommonTags.Items.FOODS_RAW_BEEF, CommonTags.Items.FOODS_RAW_MUTTON
            }
         );
      this.tag(net.neoforged.neoforge.common.Tags.Items.FOODS_RAW_FISH)
         .addTags(new TagKey[]{CommonTags.Items.FOODS_RAW_COD, CommonTags.Items.FOODS_RAW_SALMON});
      this.tag(net.neoforged.neoforge.common.Tags.Items.FOODS_COOKED_MEAT)
         .addTags(
            new TagKey[]{
               CommonTags.Items.FOODS_COOKED_CHICKEN,
               CommonTags.Items.FOODS_COOKED_PORK,
               CommonTags.Items.FOODS_COOKED_BEEF,
               CommonTags.Items.FOODS_COOKED_MUTTON
            }
         );
      this.tag(net.neoforged.neoforge.common.Tags.Items.FOODS_COOKED_FISH)
         .addTags(new TagKey[]{CommonTags.Items.FOODS_COOKED_COD, CommonTags.Items.FOODS_COOKED_SALMON});
      this.tag(net.neoforged.neoforge.common.Tags.Items.FOODS_FOOD_POISONING)
         .add(new Item[]{ModItems.WHEAT_DOUGH.get(), ModItems.RAW_PASTA.get(), ModItems.CHICKEN_CUTS.get(), ModItems.NETHER_SALAD.get()});
      this.tag(net.neoforged.neoforge.common.Tags.Items.FOODS_EDIBLE_WHEN_PLACED)
         .add(ModItems.APPLE_PIE.get())
         .add(ModItems.SWEET_BERRY_CHEESECAKE.get())
         .add(ModItems.CHOCOLATE_PIE.get())
         .addTag(ModTags.Items.FEASTS);
      this.tag(net.neoforged.neoforge.common.Tags.Items.FOODS_SOUP)
         .add(ModItems.BONE_BROTH.get())
         .add(ModItems.BEEF_STEW.get())
         .add(ModItems.VEGETABLE_SOUP.get())
         .add(ModItems.CHICKEN_SOUP.get())
         .add(ModItems.FISH_STEW.get())
         .add(ModItems.PUMPKIN_SOUP.get())
         .add(ModItems.BAKED_COD_STEW.get())
         .add(ModItems.NOODLE_SOUP.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.FOODS_PIE)
         .add(ModItems.APPLE_PIE_SLICE.get())
         .add(ModItems.SWEET_BERRY_CHEESECAKE_SLICE.get())
         .add(ModItems.CHOCOLATE_PIE_SLICE.get())
         .add(ModItems.PUMPKIN_PIE_SLICE.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.TOOLS).addTag(CommonTags.Items.TOOLS_KNIFE);
      this.tag(net.neoforged.neoforge.common.Tags.Items.ROPES).add(ModItems.ROPE.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.SEEDS).add(new Item[]{ModItems.CABBAGE_SEEDS.get(), ModItems.RICE.get(), ModItems.TOMATO_SEEDS.get()});
      this.tag(net.neoforged.neoforge.common.Tags.Items.CROPS)
         .addTags(new TagKey[]{CommonTags.Items.CROPS_CABBAGE, CommonTags.Items.CROPS_ONION, CommonTags.Items.CROPS_RICE, CommonTags.Items.CROPS_TOMATO});
      this.tag(net.neoforged.neoforge.common.Tags.Items.STORAGE_BLOCKS)
         .addTags(
            new TagKey[]{
               CommonTags.Items.STORAGE_BLOCKS_CARROT,
               CommonTags.Items.STORAGE_BLOCKS_POTATO,
               CommonTags.Items.STORAGE_BLOCKS_BEETROOT,
               CommonTags.Items.STORAGE_BLOCKS_CABBAGE,
               CommonTags.Items.STORAGE_BLOCKS_TOMATO,
               CommonTags.Items.STORAGE_BLOCKS_ONION,
               CommonTags.Items.STORAGE_BLOCKS_RICE,
               CommonTags.Items.STORAGE_BLOCKS_RICE_PANICLE,
               CommonTags.Items.STORAGE_BLOCKS_STRAW
            }
         );
   }

   public void registerCommonTags() {
      this.tag(CommonTags.Items.CROPS_CABBAGE).add(new Item[]{ModItems.CABBAGE.get(), ModItems.CABBAGE_LEAF.get()});
      this.tag(CommonTags.Items.CROPS_ONION).add(ModItems.ONION.get());
      this.tag(CommonTags.Items.CROPS_TOMATO).add(ModItems.TOMATO.get());
      this.tag(CommonTags.Items.CROPS_RICE).add(ModItems.RICE.get());
      this.tag(CommonTags.Items.FOODS_CABBAGE).add(new Item[]{ModItems.CABBAGE.get(), ModItems.CABBAGE_LEAF.get()});
      this.tag(CommonTags.Items.FOODS_TOMATO).add(ModItems.TOMATO.get());
      this.tag(CommonTags.Items.FOODS_ONION).add(ModItems.ONION.get());
      this.tag(CommonTags.Items.FOODS_DOUGH_WHEAT).add(ModItems.WHEAT_DOUGH.get());
      this.tag(CommonTags.Items.CROPS_GRAIN).add(new Item[]{Items.WHEAT, ModItems.RICE.get()});
      this.tag(CommonTags.Items.FOODS_PASTA).add(ModItems.RAW_PASTA.get());
      this.tag(CommonTags.Items.FOODS_LEAFY_GREEN).addTag(CommonTags.Items.FOODS_CABBAGE);
      this.tag(CommonTags.Items.FOODS_RAW_BACON).add(ModItems.BACON.get());
      this.tag(CommonTags.Items.FOODS_RAW_BEEF).add(new Item[]{Items.BEEF, ModItems.MINCED_BEEF.get()});
      this.tag(CommonTags.Items.FOODS_RAW_CHICKEN).add(new Item[]{Items.CHICKEN, ModItems.CHICKEN_CUTS.get()});
      this.tag(CommonTags.Items.FOODS_RAW_PORK).add(Items.PORKCHOP).addTag(CommonTags.Items.FOODS_RAW_BACON);
      this.tag(CommonTags.Items.FOODS_RAW_MUTTON).add(new Item[]{Items.MUTTON, ModItems.MUTTON_CHOPS.get()});
      this.tag(CommonTags.Items.FOODS_RAW_COD).add(new Item[]{Items.COD, ModItems.COD_SLICE.get()});
      this.tag(CommonTags.Items.FOODS_RAW_SALMON).add(new Item[]{Items.SALMON, ModItems.SALMON_SLICE.get()});
      this.tag(CommonTags.Items.FOODS_SAFE_RAW_FISH).addTag(net.neoforged.neoforge.common.Tags.Items.FOODS_RAW_FISH).remove(Items.PUFFERFISH);
      this.tag(CommonTags.Items.FOODS_COOKED_BACON).add(ModItems.COOKED_BACON.get());
      this.tag(CommonTags.Items.FOODS_COOKED_BEEF).add(new Item[]{Items.COOKED_BEEF, ModItems.BEEF_PATTY.get()});
      this.tag(CommonTags.Items.FOODS_COOKED_CHICKEN).add(new Item[]{Items.COOKED_CHICKEN, ModItems.COOKED_CHICKEN_CUTS.get()});
      this.tag(CommonTags.Items.FOODS_COOKED_PORK).add(Items.COOKED_PORKCHOP).addTag(CommonTags.Items.FOODS_COOKED_BACON);
      this.tag(CommonTags.Items.FOODS_COOKED_MUTTON).add(new Item[]{Items.COOKED_MUTTON, ModItems.COOKED_MUTTON_CHOPS.get()});
      this.tag(CommonTags.Items.FOODS_COOKED_COD).add(new Item[]{Items.COOKED_COD, ModItems.COOKED_COD_SLICE.get()});
      this.tag(CommonTags.Items.FOODS_COOKED_SALMON).add(new Item[]{Items.COOKED_SALMON, ModItems.COOKED_SALMON_SLICE.get()});
      this.tag(CommonTags.Items.FOODS_COOKED_EGG).add(ModItems.FRIED_EGG.get());
      this.tag(CommonTags.Items.STORAGE_BLOCKS_CARROT).add(ModItems.CARROT_CRATE.get());
      this.tag(CommonTags.Items.STORAGE_BLOCKS_POTATO).add(ModItems.POTATO_CRATE.get());
      this.tag(CommonTags.Items.STORAGE_BLOCKS_BEETROOT).add(ModItems.BEETROOT_CRATE.get());
      this.tag(CommonTags.Items.STORAGE_BLOCKS_CABBAGE).add(ModItems.CABBAGE_CRATE.get());
      this.tag(CommonTags.Items.STORAGE_BLOCKS_TOMATO).add(ModItems.TOMATO_CRATE.get());
      this.tag(CommonTags.Items.STORAGE_BLOCKS_ONION).add(ModItems.ONION_CRATE.get());
      this.tag(CommonTags.Items.STORAGE_BLOCKS_RICE).add(ModItems.RICE_BAG.get());
      this.tag(CommonTags.Items.STORAGE_BLOCKS_RICE_PANICLE).add(ModItems.RICE_BALE.get());
      this.tag(CommonTags.Items.STORAGE_BLOCKS_STRAW).add(ModItems.STRAW_BALE.get());
      this.tag(CommonTags.Items.TOOLS_KNIFE)
         .add(
            new Item[]{
               ModItems.FLINT_KNIFE.get(), ModItems.IRON_KNIFE.get(), ModItems.DIAMOND_KNIFE.get(), ModItems.GOLDEN_KNIFE.get(), ModItems.NETHERITE_KNIFE.get()
            }
         );
   }

   public void registerCompatibilityTags() {
      this.tag(CompatibilityTags.CREATE_UPRIGHT_ON_BELT)
         .addTag(ModTags.Items.MEALS)
         .addTag(ModTags.Items.DRINKS)
         .addTag(ModTags.Items.FEASTS)
         .add(ModItems.TOMATO_SAUCE.get())
         .add(ModItems.DOG_FOOD.get())
         .add(ModItems.FRUIT_SALAD.get())
         .add(ModItems.NETHER_SALAD.get())
         .add(ModItems.PIE_CRUST.get())
         .add(ModItems.APPLE_PIE.get())
         .add(ModItems.SWEET_BERRY_CHEESECAKE.get())
         .add(ModItems.CHOCOLATE_PIE.get());
      this.tag(CompatibilityTags.CREATE_CA_PLANT_FOODS).add(ModItems.PUMPKIN_SLICE.get()).add(ModItems.ROTTEN_TOMATO.get()).add(ModItems.RICE_PANICLE.get());
      this.tag(CompatibilityTags.CREATE_CA_PLANTS)
         .add(ModItems.SANDY_SHRUB.get())
         .add(ModItems.BROWN_MUSHROOM_COLONY.get())
         .add(ModItems.RED_MUSHROOM_COLONY.get());
      this.tag(CompatibilityTags.ORIGINS_MEAT)
         .add(ModItems.FRIED_EGG.get())
         .add(ModItems.COD_SLICE.get())
         .add(ModItems.COOKED_COD_SLICE.get())
         .add(ModItems.SALMON_SLICE.get())
         .add(ModItems.COOKED_SALMON_SLICE.get())
         .add(ModItems.BACON_AND_EGGS.get());
      this.tag(CompatibilityTags.SERENE_SEASONS_AUTUMN_CROPS).add(ModItems.CABBAGE_SEEDS.get()).add(ModItems.ONION.get()).add(ModItems.RICE.get());
      this.tag(CompatibilityTags.SERENE_SEASONS_SPRING_CROPS).add(ModItems.ONION.get());
      this.tag(CompatibilityTags.SERENE_SEASONS_SUMMER_CROPS).add(ModItems.TOMATO_SEEDS.get()).add(ModItems.RICE.get());
      this.tag(CompatibilityTags.SERENE_SEASONS_WINTER_CROPS).add(ModItems.CABBAGE_SEEDS.get());
      this.tag(CompatibilityTags.TINKERS_CONSTRUCT_SEEDS).add(ModItems.ONION.get());
   }
}
