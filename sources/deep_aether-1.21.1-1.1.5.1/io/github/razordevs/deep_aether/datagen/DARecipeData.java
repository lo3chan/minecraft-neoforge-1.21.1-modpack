package io.github.razordevs.deep_aether.datagen;

import com.aetherteam.aether.AetherTags.Items;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.data.providers.AetherRecipeProvider;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.nitrogen.recipe.BlockStateIngredient;
import com.aetherteam.nitrogen.recipe.builder.BlockStateRecipeBuilder;
import io.github.razordevs.deep_aether.datagen.builder.CombiningRecipeBuilder;
import io.github.razordevs.deep_aether.datagen.builder.PoisonConversionRecipeBuilder;
import io.github.razordevs.deep_aether.datagen.tags.DATags;
import io.github.razordevs.deep_aether.init.DABlocks;
import io.github.razordevs.deep_aether.init.DAItems;
import io.github.razordevs.deep_aether.init.DAMobEffects;
import io.github.razordevs.deep_aether.item.component.DADataComponentTypes;
import io.github.razordevs.deep_aether.item.component.MoaFodder;
import io.github.razordevs.deep_aether.recipe.DABookCategory;
import io.github.razordevs.deep_aether.recipe.FloatyScarfColoring;
import io.github.razordevs.deep_aether.recipe.GlowingSporesRecipe;
import io.github.razordevs.deep_aether.recipe.GoldenSwetBallRecipe;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class DARecipeData extends AetherRecipeProvider {
   public DARecipeData(PackOutput output, CompletableFuture<Provider> lookupProvider) {
      super(output, lookupProvider, "deep_aether");
   }

   protected void buildRecipes(RecipeOutput consumer) {
      woodFromLogs(consumer, (ItemLike)DABlocks.ROSEROOT_WOOD.get(), (ItemLike)DABlocks.ROSEROOT_LOG.get());
      woodFromLogs(consumer, (ItemLike)DABlocks.STRIPPED_ROSEROOT_WOOD.get(), (ItemLike)DABlocks.STRIPPED_ROSEROOT_LOG.get());
      planksFromLogs(consumer, (ItemLike)DABlocks.ROSEROOT_PLANKS.get(), DATags.Items.CRAFTS_ROSEROOT_PLANKS, 4);
      this.stairs(DABlocks.ROSEROOT_STAIRS, DABlocks.ROSEROOT_PLANKS).group("wooden_stairs").save(consumer);
      this.slab((Block)DABlocks.ROSEROOT_SLAB.get(), DABlocks.ROSEROOT_PLANKS).group("wooden_slab").save(consumer);
      this.fence(DABlocks.ROSEROOT_FENCE, DABlocks.ROSEROOT_PLANKS).save(consumer);
      this.fenceGate(DABlocks.ROSEROOT_FENCE_GATE, DABlocks.ROSEROOT_PLANKS).save(consumer);
      doorBuilder((ItemLike)DABlocks.ROSEROOT_DOOR.get(), Ingredient.of(new ItemLike[]{(ItemLike)DABlocks.ROSEROOT_PLANKS.get()}))
         .unlockedBy(getHasName((ItemLike)DABlocks.ROSEROOT_PLANKS.get()), has((ItemLike)DABlocks.ROSEROOT_PLANKS.get()))
         .group("wooden_door")
         .save(consumer);
      trapdoorBuilder((ItemLike)DABlocks.ROSEROOT_TRAPDOOR.get(), Ingredient.of(new ItemLike[]{(ItemLike)DABlocks.ROSEROOT_PLANKS.get()}))
         .unlockedBy(getHasName((ItemLike)DABlocks.ROSEROOT_PLANKS.get()), has((ItemLike)DABlocks.ROSEROOT_PLANKS.get()))
         .group("wooden_trapdoor")
         .save(consumer);
      pressurePlateBuilder(
            RecipeCategory.REDSTONE, (ItemLike)DABlocks.ROSEROOT_PRESSURE_PLATE.get(), Ingredient.of(new ItemLike[]{(ItemLike)DABlocks.ROSEROOT_PLANKS.get()})
         )
         .unlockedBy(getHasName((ItemLike)DABlocks.ROSEROOT_PLANKS.get()), has((ItemLike)DABlocks.ROSEROOT_PLANKS.get()))
         .group("wooden_pressure_plate")
         .save(consumer);
      buttonBuilder((ItemLike)DABlocks.ROSEROOT_BUTTON.get(), Ingredient.of(new ItemLike[]{(ItemLike)DABlocks.ROSEROOT_PLANKS.get()}))
         .unlockedBy(getHasName((ItemLike)DABlocks.ROSEROOT_PLANKS.get()), has((ItemLike)DABlocks.ROSEROOT_PLANKS.get()))
         .group("wooden_button")
         .save(consumer);
      this.sign(consumer, (Item)DAItems.ROSEROOT_SIGN.get(), (Block)DABlocks.ROSEROOT_PLANKS.get());
      this.makeHangingSign(consumer, (Item)DAItems.ROSEROOT_HANGING_SIGN.get(), (Block)DABlocks.STRIPPED_ROSEROOT_LOG.get());
      this.makeBoat(DAItems.ROSEROOT_BOAT, (Block)DABlocks.ROSEROOT_PLANKS.get()).save(consumer);
      this.makeChestBoat((Item)DAItems.ROSEROOT_CHEST_BOAT.get(), (Item)DAItems.ROSEROOT_BOAT.get()).save(consumer);
      woodFromLogs(consumer, (ItemLike)DABlocks.YAGROOT_WOOD.get(), (ItemLike)DABlocks.YAGROOT_LOG.get());
      woodFromLogs(consumer, (ItemLike)DABlocks.STRIPPED_YAGROOT_WOOD.get(), (ItemLike)DABlocks.STRIPPED_YAGROOT_LOG.get());
      planksFromLogs(consumer, (ItemLike)DABlocks.YAGROOT_PLANKS.get(), DATags.Items.CRAFTS_YAGROOT_PLANKS, 4);
      this.stairs(DABlocks.YAGROOT_STAIRS, DABlocks.YAGROOT_PLANKS).group("wooden_stairs").save(consumer);
      this.slab((Block)DABlocks.YAGROOT_SLAB.get(), DABlocks.YAGROOT_PLANKS).group("wooden_slab").save(consumer);
      this.fence(DABlocks.YAGROOT_FENCE, DABlocks.YAGROOT_PLANKS).save(consumer);
      this.fenceGate(DABlocks.YAGROOT_FENCE_GATE, DABlocks.YAGROOT_PLANKS).save(consumer);
      doorBuilder((ItemLike)DABlocks.YAGROOT_DOOR.get(), Ingredient.of(new ItemLike[]{(ItemLike)DABlocks.YAGROOT_PLANKS.get()}))
         .unlockedBy(getHasName((ItemLike)DABlocks.YAGROOT_PLANKS.get()), has((ItemLike)DABlocks.YAGROOT_PLANKS.get()))
         .group("wooden_door")
         .save(consumer);
      trapdoorBuilder((ItemLike)DABlocks.YAGROOT_TRAPDOOR.get(), Ingredient.of(new ItemLike[]{(ItemLike)DABlocks.YAGROOT_PLANKS.get()}))
         .unlockedBy(getHasName((ItemLike)DABlocks.YAGROOT_PLANKS.get()), has((ItemLike)DABlocks.YAGROOT_PLANKS.get()))
         .group("wooden_trapdoor")
         .save(consumer);
      pressurePlateBuilder(
            RecipeCategory.REDSTONE, (ItemLike)DABlocks.YAGROOT_PRESSURE_PLATE.get(), Ingredient.of(new ItemLike[]{(ItemLike)DABlocks.YAGROOT_PLANKS.get()})
         )
         .unlockedBy(getHasName((ItemLike)DABlocks.YAGROOT_PLANKS.get()), has((ItemLike)DABlocks.YAGROOT_PLANKS.get()))
         .group("wooden_pressure_plate")
         .save(consumer);
      buttonBuilder((ItemLike)DABlocks.YAGROOT_BUTTON.get(), Ingredient.of(new ItemLike[]{(ItemLike)DABlocks.YAGROOT_PLANKS.get()}))
         .unlockedBy(getHasName((ItemLike)DABlocks.YAGROOT_PLANKS.get()), has((ItemLike)DABlocks.YAGROOT_PLANKS.get()))
         .group("wooden_button")
         .save(consumer);
      this.sign(consumer, (Item)DAItems.YAGROOT_SIGN.get(), (Block)DABlocks.YAGROOT_PLANKS.get());
      this.makeHangingSign(consumer, (Item)DAItems.YAGROOT_HANGING_SIGN.get(), (Block)DABlocks.STRIPPED_YAGROOT_LOG.get());
      this.makeBoat(DAItems.YAGROOT_BOAT, (Block)DABlocks.YAGROOT_PLANKS.get()).save(consumer);
      this.makeChestBoat((Item)DAItems.YAGROOT_CHEST_BOAT.get(), (Item)DAItems.YAGROOT_BOAT.get()).save(consumer);
      woodFromLogs(consumer, (ItemLike)DABlocks.CRUDEROOT_WOOD.get(), (ItemLike)DABlocks.CRUDEROOT_LOG.get());
      woodFromLogs(consumer, (ItemLike)DABlocks.STRIPPED_CRUDEROOT_WOOD.get(), (ItemLike)DABlocks.STRIPPED_CRUDEROOT_LOG.get());
      planksFromLogs(consumer, (ItemLike)DABlocks.CRUDEROOT_PLANKS.get(), DATags.Items.CRAFTS_CRUDEROOT_PLANKS, 4);
      this.stairs(DABlocks.CRUDEROOT_STAIRS, DABlocks.CRUDEROOT_PLANKS).group("wooden_stairs").save(consumer);
      this.slab((Block)DABlocks.CRUDEROOT_SLAB.get(), DABlocks.CRUDEROOT_PLANKS).group("wooden_slab").save(consumer);
      this.fence(DABlocks.CRUDEROOT_FENCE, DABlocks.CRUDEROOT_PLANKS).save(consumer);
      this.fenceGate(DABlocks.CRUDEROOT_FENCE_GATE, DABlocks.CRUDEROOT_PLANKS).save(consumer);
      doorBuilder((ItemLike)DABlocks.CRUDEROOT_DOOR.get(), Ingredient.of(new ItemLike[]{(ItemLike)DABlocks.CRUDEROOT_PLANKS.get()}))
         .unlockedBy(getHasName((ItemLike)DABlocks.CRUDEROOT_PLANKS.get()), has((ItemLike)DABlocks.CRUDEROOT_PLANKS.get()))
         .group("wooden_door")
         .save(consumer);
      trapdoorBuilder((ItemLike)DABlocks.CRUDEROOT_TRAPDOOR.get(), Ingredient.of(new ItemLike[]{(ItemLike)DABlocks.CRUDEROOT_PLANKS.get()}))
         .unlockedBy(getHasName((ItemLike)DABlocks.CRUDEROOT_PLANKS.get()), has((ItemLike)DABlocks.CRUDEROOT_PLANKS.get()))
         .group("wooden_trapdoor")
         .save(consumer);
      pressurePlateBuilder(
            RecipeCategory.REDSTONE,
            (ItemLike)DABlocks.CRUDEROOT_PRESSURE_PLATE.get(),
            Ingredient.of(new ItemLike[]{(ItemLike)DABlocks.CRUDEROOT_PLANKS.get()})
         )
         .unlockedBy(getHasName((ItemLike)DABlocks.CRUDEROOT_PLANKS.get()), has((ItemLike)DABlocks.CRUDEROOT_PLANKS.get()))
         .group("wooden_pressure_plate")
         .save(consumer);
      buttonBuilder((ItemLike)DABlocks.CRUDEROOT_BUTTON.get(), Ingredient.of(new ItemLike[]{(ItemLike)DABlocks.CRUDEROOT_PLANKS.get()}))
         .unlockedBy(getHasName((ItemLike)DABlocks.CRUDEROOT_PLANKS.get()), has((ItemLike)DABlocks.CRUDEROOT_PLANKS.get()))
         .group("wooden_button")
         .save(consumer);
      this.sign(consumer, (Item)DAItems.CRUDEROOT_SIGN.get(), (Block)DABlocks.CRUDEROOT_PLANKS.get());
      this.makeHangingSign(consumer, (Item)DAItems.CRUDEROOT_HANGING_SIGN.get(), (Block)DABlocks.STRIPPED_CRUDEROOT_LOG.get());
      this.makeBoat(DAItems.CRUDEROOT_BOAT, (Block)DABlocks.CRUDEROOT_PLANKS.get()).save(consumer);
      this.makeChestBoat((Item)DAItems.CRUDEROOT_CHEST_BOAT.get(), (Item)DAItems.CRUDEROOT_BOAT.get()).save(consumer);
      woodFromLogs(consumer, (ItemLike)DABlocks.CONBERRY_WOOD.get(), (ItemLike)DABlocks.CONBERRY_LOG.get());
      woodFromLogs(consumer, (ItemLike)DABlocks.STRIPPED_CONBERRY_WOOD.get(), (ItemLike)DABlocks.STRIPPED_CONBERRY_LOG.get());
      planksFromLogs(consumer, (ItemLike)DABlocks.CONBERRY_PLANKS.get(), DATags.Items.CRAFTS_CONBERRY_PLANKS, 4);
      this.stairs(DABlocks.CONBERRY_STAIRS, DABlocks.CONBERRY_PLANKS).group("wooden_stairs").save(consumer);
      this.slab((Block)DABlocks.CONBERRY_SLAB.get(), DABlocks.CONBERRY_PLANKS).group("wooden_slab").save(consumer);
      this.fence(DABlocks.CONBERRY_FENCE, DABlocks.CONBERRY_PLANKS).save(consumer);
      this.fenceGate(DABlocks.CONBERRY_FENCE_GATE, DABlocks.CONBERRY_PLANKS).save(consumer);
      doorBuilder((ItemLike)DABlocks.CONBERRY_DOOR.get(), Ingredient.of(new ItemLike[]{(ItemLike)DABlocks.CONBERRY_PLANKS.get()}))
         .unlockedBy(getHasName((ItemLike)DABlocks.CONBERRY_PLANKS.get()), has((ItemLike)DABlocks.CONBERRY_PLANKS.get()))
         .group("wooden_door")
         .save(consumer);
      trapdoorBuilder((ItemLike)DABlocks.CONBERRY_TRAPDOOR.get(), Ingredient.of(new ItemLike[]{(ItemLike)DABlocks.CONBERRY_PLANKS.get()}))
         .unlockedBy(getHasName((ItemLike)DABlocks.CONBERRY_PLANKS.get()), has((ItemLike)DABlocks.CONBERRY_PLANKS.get()))
         .group("wooden_trapdoor")
         .save(consumer);
      pressurePlateBuilder(
            RecipeCategory.REDSTONE, (ItemLike)DABlocks.CONBERRY_PRESSURE_PLATE.get(), Ingredient.of(new ItemLike[]{(ItemLike)DABlocks.CONBERRY_PLANKS.get()})
         )
         .unlockedBy(getHasName((ItemLike)DABlocks.CONBERRY_PLANKS.get()), has((ItemLike)DABlocks.CONBERRY_PLANKS.get()))
         .group("wooden_pressure_plate")
         .save(consumer);
      buttonBuilder((ItemLike)DABlocks.CONBERRY_BUTTON.get(), Ingredient.of(new ItemLike[]{(ItemLike)DABlocks.CONBERRY_PLANKS.get()}))
         .unlockedBy(getHasName((ItemLike)DABlocks.CONBERRY_PLANKS.get()), has((ItemLike)DABlocks.CONBERRY_PLANKS.get()))
         .group("wooden_button")
         .save(consumer);
      this.sign(consumer, (Item)DAItems.CONBERRY_SIGN.get(), (Block)DABlocks.CONBERRY_PLANKS.get());
      this.makeHangingSign(consumer, (Item)DAItems.CONBERRY_HANGING_SIGN.get(), (Block)DABlocks.STRIPPED_CONBERRY_LOG.get());
      this.makeBoat(DAItems.CONBERRY_BOAT, (Block)DABlocks.CONBERRY_PLANKS.get()).save(consumer);
      this.makeChestBoat((Item)DAItems.CONBERRY_CHEST_BOAT.get(), (Item)DAItems.CONBERRY_BOAT.get()).save(consumer);
      woodFromLogs(consumer, (ItemLike)DABlocks.SUNROOT_WOOD.get(), (ItemLike)DABlocks.SUNROOT_LOG.get());
      woodFromLogs(consumer, (ItemLike)DABlocks.STRIPPED_SUNROOT_WOOD.get(), (ItemLike)DABlocks.STRIPPED_SUNROOT_LOG.get());
      planksFromLogs(consumer, (ItemLike)DABlocks.SUNROOT_PLANKS.get(), DATags.Items.CRAFTS_SUNROOT_PLANKS, 4);
      this.stairs(DABlocks.SUNROOT_STAIRS, DABlocks.SUNROOT_PLANKS).group("wooden_stairs").save(consumer);
      this.slab((Block)DABlocks.SUNROOT_SLAB.get(), DABlocks.SUNROOT_PLANKS).group("wooden_slab").save(consumer);
      this.fence(DABlocks.SUNROOT_FENCE, DABlocks.SUNROOT_PLANKS).save(consumer);
      this.fenceGate(DABlocks.SUNROOT_FENCE_GATE, DABlocks.SUNROOT_PLANKS).save(consumer);
      doorBuilder((ItemLike)DABlocks.SUNROOT_DOOR.get(), Ingredient.of(new ItemLike[]{(ItemLike)DABlocks.SUNROOT_PLANKS.get()}))
         .unlockedBy(getHasName((ItemLike)DABlocks.SUNROOT_PLANKS.get()), has((ItemLike)DABlocks.SUNROOT_PLANKS.get()))
         .group("wooden_door")
         .save(consumer);
      trapdoorBuilder((ItemLike)DABlocks.SUNROOT_TRAPDOOR.get(), Ingredient.of(new ItemLike[]{(ItemLike)DABlocks.SUNROOT_PLANKS.get()}))
         .unlockedBy(getHasName((ItemLike)DABlocks.SUNROOT_PLANKS.get()), has((ItemLike)DABlocks.SUNROOT_PLANKS.get()))
         .group("wooden_trapdoor")
         .save(consumer);
      pressurePlateBuilder(
            RecipeCategory.REDSTONE, (ItemLike)DABlocks.SUNROOT_PRESSURE_PLATE.get(), Ingredient.of(new ItemLike[]{(ItemLike)DABlocks.SUNROOT_PLANKS.get()})
         )
         .unlockedBy(getHasName((ItemLike)DABlocks.SUNROOT_PLANKS.get()), has((ItemLike)DABlocks.SUNROOT_PLANKS.get()))
         .group("wooden_pressure_plate")
         .save(consumer);
      buttonBuilder((ItemLike)DABlocks.SUNROOT_BUTTON.get(), Ingredient.of(new ItemLike[]{(ItemLike)DABlocks.SUNROOT_PLANKS.get()}))
         .unlockedBy(getHasName((ItemLike)DABlocks.SUNROOT_PLANKS.get()), has((ItemLike)DABlocks.SUNROOT_PLANKS.get()))
         .group("wooden_button")
         .save(consumer);
      this.sign(consumer, (Item)DAItems.SUNROOT_SIGN.get(), (Block)DABlocks.SUNROOT_PLANKS.get());
      this.makeHangingSign(consumer, (Item)DAItems.SUNROOT_HANGING_SIGN.get(), (Block)DABlocks.STRIPPED_SUNROOT_LOG.get());
      this.makeBoat(DAItems.SUNROOT_BOAT, (Block)DABlocks.SUNROOT_PLANKS.get()).save(consumer);
      this.makeChestBoat((Item)DAItems.SUNROOT_CHEST_BOAT.get(), (Item)DAItems.SUNROOT_BOAT.get()).save(consumer);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, (ItemLike)DABlocks.PACKED_AETHER_MUD.get())
         .requires((ItemLike)DABlocks.AETHER_MUD.get())
         .requires((ItemLike)DAItems.AERGLOW_BLOSSOM.get())
         .unlockedBy(getHasName((ItemLike)DABlocks.AETHER_MUD.get()), has((ItemLike)DABlocks.AETHER_MUD.get()))
         .save(consumer);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, (ItemLike)DABlocks.MUDDY_YAGROOT_ROOTS.get())
         .requires((ItemLike)DABlocks.AETHER_MUD.get())
         .requires((ItemLike)DABlocks.YAGROOT_ROOTS.get())
         .unlockedBy(getHasName((ItemLike)DABlocks.AETHER_MUD.get()), has((ItemLike)DABlocks.AETHER_MUD.get()))
         .save(consumer);
      this.stonecuttingRecipe(consumer, RecipeCategory.DECORATIONS, (ItemLike)DABlocks.AETHER_MUD_BRICKS_WALL.get(), (ItemLike)DABlocks.AETHER_MUD_BRICKS.get());
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.AETHER_MUD_BRICKS_STAIRS.get(), (ItemLike)DABlocks.AETHER_MUD_BRICKS.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.AETHER_MUD_BRICKS_SLAB.get(), (ItemLike)DABlocks.AETHER_MUD_BRICKS.get(), 2
      );
      this.brick(consumer, (Block)DABlocks.AETHER_MUD_BRICKS.get(), (Block)DABlocks.PACKED_AETHER_MUD.get());
      this.stairs(DABlocks.AETHER_MUD_BRICKS_STAIRS, DABlocks.AETHER_MUD_BRICKS).save(consumer);
      slab(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.AETHER_MUD_BRICKS_SLAB.get(), (ItemLike)DABlocks.AETHER_MUD_BRICKS.get());
      wall(consumer, RecipeCategory.DECORATIONS, (ItemLike)DABlocks.AETHER_MUD_BRICKS_WALL.get(), (ItemLike)DABlocks.AETHER_MUD_BRICKS.get());
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.COBBLED_ASETERITE_STAIRS.get(), (ItemLike)DABlocks.COBBLED_ASETERITE.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.COBBLED_ASETERITE_SLAB.get(), (ItemLike)DABlocks.COBBLED_ASETERITE.get(), 2
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.COBBLED_ASETERITE_WALL.get(), (ItemLike)DABlocks.COBBLED_ASETERITE.get()
      );
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.ASETERITE_STAIRS.get(), (ItemLike)DABlocks.ASETERITE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.ASETERITE_SLAB.get(), (ItemLike)DABlocks.ASETERITE.get(), 2);
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.ASETERITE_WALL.get(), (ItemLike)DABlocks.ASETERITE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.POLISHED_ASETERITE.get(), (ItemLike)DABlocks.ASETERITE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.POLISHED_ASETERITE_STAIRS.get(), (ItemLike)DABlocks.ASETERITE.get());
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.POLISHED_ASETERITE_STAIRS.get(), (ItemLike)DABlocks.POLISHED_ASETERITE.get()
      );
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.POLISHED_ASETERITE_SLAB.get(), (ItemLike)DABlocks.ASETERITE.get(), 2);
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.POLISHED_ASETERITE_SLAB.get(), (ItemLike)DABlocks.POLISHED_ASETERITE.get(), 2
      );
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.POLISHED_ASETERITE_WALL.get(), (ItemLike)DABlocks.ASETERITE.get());
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.POLISHED_ASETERITE_WALL.get(), (ItemLike)DABlocks.POLISHED_ASETERITE.get()
      );
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.ASETERITE_BRICKS.get(), (ItemLike)DABlocks.ASETERITE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.ASETERITE_BRICKS.get(), (ItemLike)DABlocks.POLISHED_ASETERITE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.ASETERITE_BRICKS_STAIRS.get(), (ItemLike)DABlocks.ASETERITE.get());
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.ASETERITE_BRICKS_STAIRS.get(), (ItemLike)DABlocks.POLISHED_ASETERITE.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.ASETERITE_BRICKS_STAIRS.get(), (ItemLike)DABlocks.ASETERITE_BRICKS.get()
      );
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.ASETERITE_BRICKS_SLAB.get(), (ItemLike)DABlocks.ASETERITE.get(), 2);
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.ASETERITE_BRICKS_SLAB.get(), (ItemLike)DABlocks.POLISHED_ASETERITE.get(), 2
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.ASETERITE_BRICKS_SLAB.get(), (ItemLike)DABlocks.ASETERITE_BRICKS.get(), 2
      );
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.ASETERITE_BRICKS_WALL.get(), (ItemLike)DABlocks.ASETERITE.get());
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.ASETERITE_BRICKS_WALL.get(), (ItemLike)DABlocks.POLISHED_ASETERITE.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.ASETERITE_BRICKS_WALL.get(), (ItemLike)DABlocks.ASETERITE_BRICKS.get()
      );
      this.stairs(DABlocks.COBBLED_ASETERITE_STAIRS, DABlocks.COBBLED_ASETERITE).save(consumer);
      slab(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.COBBLED_ASETERITE_SLAB.get(), (ItemLike)DABlocks.COBBLED_ASETERITE.get());
      wall(consumer, RecipeCategory.DECORATIONS, (ItemLike)DABlocks.COBBLED_ASETERITE_WALL.get(), (ItemLike)DABlocks.COBBLED_ASETERITE.get());
      this.smeltingBlockRecipe((ItemLike)DABlocks.ASETERITE.get(), (ItemLike)DABlocks.COBBLED_ASETERITE.get(), 0.1F).save(consumer);
      this.stairs(DABlocks.ASETERITE_STAIRS, DABlocks.ASETERITE).save(consumer);
      slab(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.ASETERITE_SLAB.get(), (ItemLike)DABlocks.ASETERITE.get());
      wall(consumer, RecipeCategory.DECORATIONS, (ItemLike)DABlocks.ASETERITE_WALL.get(), (ItemLike)DABlocks.ASETERITE.get());
      this.brick(consumer, (Block)DABlocks.POLISHED_ASETERITE.get(), (Block)DABlocks.ASETERITE.get());
      this.stairs(DABlocks.POLISHED_ASETERITE_STAIRS, DABlocks.POLISHED_ASETERITE).save(consumer);
      slab(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.POLISHED_ASETERITE_SLAB.get(), (ItemLike)DABlocks.POLISHED_ASETERITE.get());
      wall(consumer, RecipeCategory.DECORATIONS, (ItemLike)DABlocks.POLISHED_ASETERITE_WALL.get(), (ItemLike)DABlocks.POLISHED_ASETERITE.get());
      this.brick(consumer, (Block)DABlocks.ASETERITE_BRICKS.get(), (Block)DABlocks.POLISHED_ASETERITE.get());
      this.stairs(DABlocks.ASETERITE_BRICKS_STAIRS, DABlocks.ASETERITE_BRICKS).save(consumer);
      slab(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.ASETERITE_BRICKS_SLAB.get(), (ItemLike)DABlocks.ASETERITE_BRICKS.get());
      wall(consumer, RecipeCategory.DECORATIONS, (ItemLike)DABlocks.ASETERITE_BRICKS_WALL.get(), (ItemLike)DABlocks.ASETERITE_BRICKS.get());
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)DABlocks.RAW_CLORITE.get(), 4)
         .define('A', (ItemLike)DABlocks.ASETERITE.get())
         .define('B', (ItemLike)AetherBlocks.HOLYSTONE.get())
         .pattern("AB")
         .pattern("BA")
         .unlockedBy(getHasName((ItemLike)DABlocks.ASETERITE.get()), has((ItemLike)DABlocks.ASETERITE.get()))
         .save(consumer);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)DABlocks.CLORITE_PILLAR.get(), 1)
         .define('A', (ItemLike)DABlocks.CLORITE_SLAB.get())
         .pattern("A")
         .pattern("A")
         .unlockedBy(getHasName((ItemLike)DABlocks.CLORITE.get()), has((ItemLike)DABlocks.CLORITE.get()))
         .save(consumer);
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.RAW_CLORITE_STAIRS.get(), (ItemLike)DABlocks.RAW_CLORITE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.RAW_CLORITE_SLAB.get(), (ItemLike)DABlocks.RAW_CLORITE.get(), 2);
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.RAW_CLORITE_WALL.get(), (ItemLike)DABlocks.RAW_CLORITE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.CLORITE_STAIRS.get(), (ItemLike)DABlocks.CLORITE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.CLORITE_SLAB.get(), (ItemLike)DABlocks.CLORITE.get(), 2);
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.CLORITE_WALL.get(), (ItemLike)DABlocks.CLORITE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.CLORITE_PILLAR.get(), (ItemLike)DABlocks.CLORITE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.POLISHED_CLORITE.get(), (ItemLike)DABlocks.CLORITE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.POLISHED_CLORITE_STAIRS.get(), (ItemLike)DABlocks.CLORITE.get());
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.POLISHED_CLORITE_STAIRS.get(), (ItemLike)DABlocks.POLISHED_CLORITE.get()
      );
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.POLISHED_CLORITE_SLAB.get(), (ItemLike)DABlocks.CLORITE.get(), 2);
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.POLISHED_CLORITE_SLAB.get(), (ItemLike)DABlocks.POLISHED_CLORITE.get(), 2
      );
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.POLISHED_CLORITE_WALL.get(), (ItemLike)DABlocks.CLORITE.get());
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.POLISHED_CLORITE_WALL.get(), (ItemLike)DABlocks.POLISHED_CLORITE.get()
      );
      this.stairs(DABlocks.RAW_CLORITE_STAIRS, DABlocks.RAW_CLORITE).save(consumer);
      slab(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.RAW_CLORITE_SLAB.get(), (ItemLike)DABlocks.RAW_CLORITE.get());
      wall(consumer, RecipeCategory.DECORATIONS, (ItemLike)DABlocks.RAW_CLORITE_WALL.get(), (ItemLike)DABlocks.RAW_CLORITE.get());
      this.stairs(DABlocks.CLORITE_STAIRS, DABlocks.CLORITE).save(consumer);
      slab(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.CLORITE_SLAB.get(), (ItemLike)DABlocks.CLORITE.get());
      wall(consumer, RecipeCategory.DECORATIONS, (ItemLike)DABlocks.CLORITE_WALL.get(), (ItemLike)DABlocks.CLORITE.get());
      this.brick(consumer, (Block)DABlocks.POLISHED_CLORITE.get(), (Block)DABlocks.CLORITE.get());
      this.stairs(DABlocks.POLISHED_CLORITE_STAIRS, DABlocks.POLISHED_CLORITE).save(consumer);
      slab(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.POLISHED_CLORITE_SLAB.get(), (ItemLike)DABlocks.POLISHED_CLORITE.get());
      wall(consumer, RecipeCategory.DECORATIONS, (ItemLike)DABlocks.POLISHED_CLORITE_WALL.get(), (ItemLike)DABlocks.POLISHED_CLORITE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.BIG_HOLYSTONE_BRICKS.get(), (ItemLike)AetherBlocks.HOLYSTONE.get());
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.BIG_HOLYSTONE_BRICKS.get(), (ItemLike)AetherBlocks.HOLYSTONE_BRICKS.get()
      );
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.BIG_HOLYSTONE_BRICKS.get(), (ItemLike)DABlocks.HOLYSTONE_TILES.get());
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.BIG_HOLYSTONE_BRICKS_STAIRS.get(), (ItemLike)AetherBlocks.HOLYSTONE.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.BIG_HOLYSTONE_BRICKS_STAIRS.get(), (ItemLike)AetherBlocks.HOLYSTONE_BRICKS.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.BIG_HOLYSTONE_BRICKS_STAIRS.get(), (ItemLike)DABlocks.HOLYSTONE_TILES.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.BIG_HOLYSTONE_BRICKS_STAIRS.get(), (ItemLike)DABlocks.BIG_HOLYSTONE_BRICKS.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.BIG_HOLYSTONE_BRICKS_SLAB.get(), (ItemLike)AetherBlocks.HOLYSTONE.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.BIG_HOLYSTONE_BRICKS_SLAB.get(), (ItemLike)AetherBlocks.HOLYSTONE_BRICKS.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.BIG_HOLYSTONE_BRICKS_SLAB.get(), (ItemLike)DABlocks.HOLYSTONE_TILES.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.BIG_HOLYSTONE_BRICKS_SLAB.get(), (ItemLike)DABlocks.BIG_HOLYSTONE_BRICKS.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.BIG_HOLYSTONE_BRICKS_WALL.get(), (ItemLike)AetherBlocks.HOLYSTONE.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.BIG_HOLYSTONE_BRICKS_WALL.get(), (ItemLike)AetherBlocks.HOLYSTONE_BRICKS.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.BIG_HOLYSTONE_BRICKS_WALL.get(), (ItemLike)DABlocks.HOLYSTONE_TILES.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.BIG_HOLYSTONE_BRICKS_WALL.get(), (ItemLike)DABlocks.BIG_HOLYSTONE_BRICKS.get()
      );
      this.brick(consumer, (Block)DABlocks.BIG_HOLYSTONE_BRICKS.get(), (Block)DABlocks.HOLYSTONE_TILES.get());
      this.stairs(DABlocks.BIG_HOLYSTONE_BRICKS_STAIRS, DABlocks.BIG_HOLYSTONE_BRICKS).save(consumer);
      slab(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.BIG_HOLYSTONE_BRICKS_SLAB.get(), (ItemLike)DABlocks.BIG_HOLYSTONE_BRICKS.get());
      wall(consumer, RecipeCategory.DECORATIONS, (ItemLike)DABlocks.BIG_HOLYSTONE_BRICKS_WALL.get(), (ItemLike)DABlocks.BIG_HOLYSTONE_BRICKS.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.HOLYSTONE_PILLAR.get(), (ItemLike)AetherBlocks.HOLYSTONE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.HOLYSTONE_PILLAR_UP.get(), (ItemLike)AetherBlocks.HOLYSTONE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.HOLYSTONE_PILLAR_DOWN.get(), (ItemLike)AetherBlocks.HOLYSTONE.get());
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)DABlocks.HOLYSTONE_PILLAR.get(), 2)
         .define('A', (ItemLike)AetherBlocks.HOLYSTONE.get())
         .pattern("A")
         .pattern("A")
         .unlockedBy(getHasName((ItemLike)DABlocks.HOLYSTONE_PILLAR.get()), has((ItemLike)DABlocks.HOLYSTONE_PILLAR.get()))
         .save(consumer);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)DABlocks.HOLYSTONE_PILLAR_UP.get(), 2)
         .define('A', (ItemLike)DABlocks.HOLYSTONE_PILLAR.get())
         .pattern("A")
         .pattern("A")
         .unlockedBy(getHasName((ItemLike)DABlocks.HOLYSTONE_PILLAR_UP.get()), has((ItemLike)DABlocks.HOLYSTONE_PILLAR_UP.get()))
         .save(consumer);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)DABlocks.HOLYSTONE_PILLAR_DOWN.get(), 2)
         .define('A', (ItemLike)DABlocks.HOLYSTONE_PILLAR_UP.get())
         .pattern("A")
         .pattern("A")
         .unlockedBy(getHasName((ItemLike)DABlocks.HOLYSTONE_PILLAR_DOWN.get()), has((ItemLike)DABlocks.HOLYSTONE_PILLAR_DOWN.get()))
         .save(consumer);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)DABlocks.CHISELED_HOLYSTONE.get(), 1)
         .define('A', (ItemLike)AetherBlocks.HOLYSTONE_SLAB.get())
         .pattern("A")
         .pattern("A")
         .unlockedBy(getHasName((ItemLike)DABlocks.CHISELED_HOLYSTONE.get()), has((ItemLike)DABlocks.CHISELED_HOLYSTONE.get()))
         .save(consumer);
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.CHISELED_HOLYSTONE.get(), (ItemLike)AetherBlocks.HOLYSTONE.get());
      ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, (ItemLike)DABlocks.MOSSY_HOLYSTONE_BRICKS.get(), 1)
         .group("mossy_holystone_bricks")
         .requires((ItemLike)AetherBlocks.HOLYSTONE_BRICKS.get())
         .requires(DATags.Items.CRAFTS_MOSSY_BLOCKS)
         .unlockedBy(getHasName((ItemLike)AetherBlocks.HOLYSTONE_BRICKS.get()), has((ItemLike)AetherBlocks.HOLYSTONE_BRICKS.get()))
         .save(consumer, this.name("mossy_holystone_bricks_from_mossy"));
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.MOSSY_HOLYSTONE_BRICKS.get(), (ItemLike)AetherBlocks.MOSSY_HOLYSTONE.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.MOSSY_HOLYSTONE_BRICK_STAIRS.get(), (ItemLike)AetherBlocks.MOSSY_HOLYSTONE.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.MOSSY_HOLYSTONE_BRICK_STAIRS.get(), (ItemLike)DABlocks.MOSSY_HOLYSTONE_BRICKS.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.MOSSY_HOLYSTONE_BRICK_SLAB.get(), (ItemLike)AetherBlocks.MOSSY_HOLYSTONE.get(), 2
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.MOSSY_HOLYSTONE_BRICK_SLAB.get(), (ItemLike)DABlocks.MOSSY_HOLYSTONE_BRICKS.get(), 2
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.MOSSY_HOLYSTONE_BRICK_WALL.get(), (ItemLike)AetherBlocks.MOSSY_HOLYSTONE.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.MOSSY_HOLYSTONE_BRICK_WALL.get(), (ItemLike)DABlocks.MOSSY_HOLYSTONE_BRICKS.get()
      );
      this.brick(consumer, (Block)DABlocks.MOSSY_HOLYSTONE_BRICKS.get(), (Block)AetherBlocks.MOSSY_HOLYSTONE.get());
      this.stairs(DABlocks.MOSSY_HOLYSTONE_BRICK_STAIRS, DABlocks.MOSSY_HOLYSTONE_BRICKS).save(consumer);
      slab(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.MOSSY_HOLYSTONE_BRICK_SLAB.get(), (ItemLike)DABlocks.MOSSY_HOLYSTONE_BRICKS.get());
      wall(consumer, RecipeCategory.DECORATIONS, (ItemLike)DABlocks.MOSSY_HOLYSTONE_BRICK_WALL.get(), (ItemLike)DABlocks.MOSSY_HOLYSTONE_BRICKS.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.HOLYSTONE_TILES.get(), (ItemLike)AetherBlocks.HOLYSTONE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.HOLYSTONE_TILES.get(), (ItemLike)AetherBlocks.HOLYSTONE_BRICKS.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.HOLYSTONE_TILE_STAIRS.get(), (ItemLike)AetherBlocks.HOLYSTONE.get());
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.HOLYSTONE_TILE_STAIRS.get(), (ItemLike)AetherBlocks.HOLYSTONE_BRICKS.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.HOLYSTONE_TILE_STAIRS.get(), (ItemLike)DABlocks.HOLYSTONE_TILES.get()
      );
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.HOLYSTONE_TILE_SLAB.get(), (ItemLike)AetherBlocks.HOLYSTONE.get(), 2);
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.HOLYSTONE_TILE_SLAB.get(), (ItemLike)AetherBlocks.HOLYSTONE_BRICKS.get(), 2
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.HOLYSTONE_TILE_SLAB.get(), (ItemLike)DABlocks.HOLYSTONE_TILES.get(), 2
      );
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.HOLYSTONE_TILE_WALL.get(), (ItemLike)AetherBlocks.HOLYSTONE.get());
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.HOLYSTONE_TILE_WALL.get(), (ItemLike)AetherBlocks.HOLYSTONE_BRICKS.get()
      );
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.HOLYSTONE_TILE_WALL.get(), (ItemLike)DABlocks.HOLYSTONE_TILES.get());
      this.brick(consumer, (Block)DABlocks.HOLYSTONE_TILES.get(), (Block)AetherBlocks.HOLYSTONE_BRICKS.get());
      this.stairs(DABlocks.HOLYSTONE_TILE_STAIRS, DABlocks.HOLYSTONE_TILES).save(consumer);
      slab(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.HOLYSTONE_TILE_SLAB.get(), (ItemLike)DABlocks.HOLYSTONE_TILES.get());
      wall(consumer, RecipeCategory.DECORATIONS, (ItemLike)DABlocks.HOLYSTONE_TILE_WALL.get(), (ItemLike)DABlocks.HOLYSTONE_TILES.get());
      ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, (ItemLike)DABlocks.MOSSY_HOLYSTONE_TILES.get(), 1)
         .group("mossy_holystone_tiles")
         .requires((ItemLike)DABlocks.HOLYSTONE_TILES.get())
         .requires(DATags.Items.CRAFTS_MOSSY_BLOCKS)
         .unlockedBy(getHasName((ItemLike)DABlocks.HOLYSTONE_TILES.get()), has((ItemLike)DABlocks.HOLYSTONE_TILES.get()))
         .save(consumer, this.name("mossy_holystone_tiles_from_mossy"));
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.MOSSY_HOLYSTONE_TILES.get(), (ItemLike)AetherBlocks.MOSSY_HOLYSTONE.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.MOSSY_HOLYSTONE_TILES.get(), (ItemLike)DABlocks.MOSSY_HOLYSTONE_BRICKS.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.MOSSY_HOLYSTONE_TILE_STAIRS.get(), (ItemLike)AetherBlocks.MOSSY_HOLYSTONE.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.MOSSY_HOLYSTONE_TILE_STAIRS.get(), (ItemLike)DABlocks.MOSSY_HOLYSTONE_BRICKS.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.MOSSY_HOLYSTONE_TILE_STAIRS.get(), (ItemLike)DABlocks.MOSSY_HOLYSTONE_TILES.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.MOSSY_HOLYSTONE_TILE_SLAB.get(), (ItemLike)AetherBlocks.MOSSY_HOLYSTONE.get(), 2
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.MOSSY_HOLYSTONE_TILE_SLAB.get(), (ItemLike)DABlocks.MOSSY_HOLYSTONE_BRICKS.get(), 2
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.MOSSY_HOLYSTONE_TILE_SLAB.get(), (ItemLike)DABlocks.MOSSY_HOLYSTONE_TILES.get(), 2
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.MOSSY_HOLYSTONE_TILE_WALL.get(), (ItemLike)AetherBlocks.MOSSY_HOLYSTONE.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.MOSSY_HOLYSTONE_TILE_WALL.get(), (ItemLike)DABlocks.MOSSY_HOLYSTONE_BRICKS.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.MOSSY_HOLYSTONE_TILE_WALL.get(), (ItemLike)DABlocks.MOSSY_HOLYSTONE_TILES.get()
      );
      this.brick(consumer, (Block)DABlocks.MOSSY_HOLYSTONE_TILES.get(), (Block)DABlocks.MOSSY_HOLYSTONE_BRICKS.get());
      this.stairs(DABlocks.MOSSY_HOLYSTONE_TILE_STAIRS, DABlocks.MOSSY_HOLYSTONE_TILES).save(consumer);
      slab(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.MOSSY_HOLYSTONE_TILE_SLAB.get(), (ItemLike)DABlocks.MOSSY_HOLYSTONE_TILES.get());
      wall(consumer, RecipeCategory.DECORATIONS, (ItemLike)DABlocks.MOSSY_HOLYSTONE_TILE_WALL.get(), (ItemLike)DABlocks.MOSSY_HOLYSTONE_TILES.get());
      ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, (ItemLike)AetherBlocks.MOSSY_HOLYSTONE.get(), 1)
         .group("mossy_holystone")
         .requires((ItemLike)AetherBlocks.HOLYSTONE.get())
         .requires(DATags.Items.CRAFTS_MOSSY_BLOCKS)
         .unlockedBy(getHasName((ItemLike)AetherBlocks.HOLYSTONE.get()), has((ItemLike)AetherBlocks.HOLYSTONE.get()))
         .save(consumer, this.name("mossy_holystone"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, Blocks.MOSSY_COBBLESTONE, 1)
         .group("mossy_holystone")
         .requires(Blocks.COBBLESTONE)
         .requires(DATags.Items.CRAFTS_MOSSY_BLOCKS)
         .unlockedBy(getHasName(Blocks.COBBLESTONE), has(Blocks.COBBLESTONE))
         .save(consumer, this.name("mossy_cobblestone"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, Blocks.MOSSY_STONE_BRICKS, 1)
         .group("mossy_holystone")
         .requires(Blocks.STONE_BRICKS)
         .requires(DATags.Items.CRAFTS_MOSSY_BLOCKS)
         .unlockedBy(getHasName(Blocks.STONE_BRICKS), has(Blocks.STONE_BRICKS))
         .save(consumer, this.name("mossy_stone_bricks"));
      this.stairs(DABlocks.NIMBUS_STAIRS, DABlocks.NIMBUS_STONE).save(consumer);
      this.slab((Block)DABlocks.NIMBUS_SLAB.get(), DABlocks.NIMBUS_STONE).save(consumer);
      wall(consumer, RecipeCategory.DECORATIONS, (ItemLike)DABlocks.NIMBUS_WALL.get(), (ItemLike)DABlocks.NIMBUS_STONE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.DECORATIONS, (ItemLike)DABlocks.NIMBUS_WALL.get(), (ItemLike)DABlocks.NIMBUS_STONE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.NIMBUS_STAIRS.get(), (ItemLike)DABlocks.NIMBUS_STONE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.NIMBUS_SLAB.get(), (ItemLike)DABlocks.NIMBUS_STONE.get(), 2);
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.NIMBUS_STONE.get(), (ItemLike)DABlocks.NIMBUS_PILLAR.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.NIMBUS_STONE.get(), (ItemLike)DABlocks.LIGHT_NIMBUS_STONE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.NIMBUS_STONE.get(), (ItemLike)DABlocks.LIGHT_NIMBUS_PILLAR.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.NIMBUS_PILLAR.get(), (ItemLike)DABlocks.NIMBUS_STONE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.NIMBUS_PILLAR.get(), (ItemLike)DABlocks.LIGHT_NIMBUS_STONE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.NIMBUS_PILLAR.get(), (ItemLike)DABlocks.LIGHT_NIMBUS_PILLAR.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.LIGHT_NIMBUS_STONE.get(), (ItemLike)DABlocks.NIMBUS_STONE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.LIGHT_NIMBUS_STONE.get(), (ItemLike)DABlocks.NIMBUS_PILLAR.get());
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.LIGHT_NIMBUS_STONE.get(), (ItemLike)DABlocks.LIGHT_NIMBUS_PILLAR.get()
      );
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.LIGHT_NIMBUS_PILLAR.get(), (ItemLike)DABlocks.NIMBUS_STONE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.LIGHT_NIMBUS_PILLAR.get(), (ItemLike)DABlocks.NIMBUS_PILLAR.get());
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.LIGHT_NIMBUS_PILLAR.get(), (ItemLike)DABlocks.LIGHT_NIMBUS_STONE.get()
      );
      this.smeltingOreRecipe((ItemLike)DAItems.SKYJADE.get(), (ItemLike)DABlocks.SKYJADE_ORE.get(), 1.0F).save(consumer);
      this.blastingOreRecipe((ItemLike)DAItems.SKYJADE.get(), (ItemLike)DABlocks.SKYJADE_ORE.get(), 0.5F).save(consumer, this.name("skjyade_from_blasting"));
      this.makeFullBlock((Item)DAItems.SKYJADE.get(), (Block)DABlocks.SKYJADE_BLOCK.get()).save(consumer, this.name("skyjade_block_from_skyjade"));
      this.materialFromBlock((Block)DABlocks.SKYJADE_BLOCK.get(), (Item)DAItems.SKYJADE.get()).save(consumer, this.name("skyjade_from_skyjade_block"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, (ItemLike)DAItems.SKYJADE_NUGGET.get(), 9)
         .requires((ItemLike)DAItems.SKYJADE.get())
         .unlockedBy(getHasName((ItemLike)DAItems.SKYJADE.get()), has((ItemLike)DAItems.SKYJADE.get()))
         .save(consumer);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)DAItems.SKYJADE.get())
         .define('A', (ItemLike)DAItems.SKYJADE_NUGGET.get())
         .pattern("AAA")
         .pattern("AAA")
         .pattern("AAA")
         .unlockedBy(getHasName((ItemLike)DAItems.SKYJADE_NUGGET.get()), has((ItemLike)DAItems.SKYJADE_NUGGET.get()))
         .save(consumer, this.name("skyjade_from_skyjade_nuggets"));
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)DABlocks.SKYJADE_LANTERN.get())
         .define('A', (ItemLike)DAItems.SKYJADE_NUGGET.get())
         .define('B', (ItemLike)AetherBlocks.AMBROSIUM_TORCH.get())
         .pattern("AAA")
         .pattern("ABA")
         .pattern("AAA")
         .unlockedBy(getHasName((ItemLike)DAItems.SKYJADE_NUGGET.get()), has((ItemLike)DAItems.SKYJADE_NUGGET.get()))
         .save(consumer, this.name("skyjade_lantern_from_skyjade_nuggets"));
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)DABlocks.AMBROSIUM_TIKI_TORCH.get())
         .define('A', (ItemLike)AetherItems.AMBROSIUM_SHARD.get())
         .define('B', (ItemLike)AetherItems.SKYROOT_STICK.get())
         .define('C', (ItemLike)DAItems.CLOUDBLOOM_BOUQUET.get())
         .pattern(" CA")
         .pattern(" BC")
         .pattern("B  ")
         .unlockedBy(getHasName((ItemLike)AetherItems.AMBROSIUM_SHARD.get()), has((ItemLike)AetherItems.AMBROSIUM_SHARD.get()))
         .save(consumer, this.name("ambrosium_tiki_torch"));
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)DABlocks.SKYJADE_CHAIN.get())
         .define('A', (ItemLike)DAItems.SKYJADE_NUGGET.get())
         .define('B', (ItemLike)AetherItems.SKYROOT_STICK.get())
         .pattern("A")
         .pattern("B")
         .pattern("A")
         .unlockedBy(getHasName((ItemLike)DAItems.SKYJADE_NUGGET.get()), has((ItemLike)DAItems.SKYJADE_NUGGET.get()))
         .save(consumer, this.name("skyjade_chain_from_skyjade_nuggets"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)DAItems.SKYJADE_TOOLS_SWORD.get(), 750)
         .group("altar_sword_repair")
         .save(consumer, this.name("skyjade_sword_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)DAItems.SKYJADE_TOOLS_AXE.get(), 750)
         .group("altar_sword_repair")
         .save(consumer, this.name("skyjade_axe_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)DAItems.SKYJADE_TOOLS_PICKAXE.get(), 750)
         .group("altar_sword_repair")
         .save(consumer, this.name("skyjade_pickaxe_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)DAItems.SKYJADE_TOOLS_SHOVEL.get(), 750)
         .group("altar_sword_repair")
         .save(consumer, this.name("skyjade_shovel_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)DAItems.SKYJADE_TOOLS_HOE.get(), 750)
         .group("altar_sword_repair")
         .save(consumer, this.name("skyjade_hoe_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)DAItems.SKYJADE_BOOTS.get(), 750)
         .group("altar_sword_repair")
         .save(consumer, this.name("skyjade_boots_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)DAItems.SKYJADE_LEGGINGS.get(), 750)
         .group("altar_sword_repair")
         .save(consumer, this.name("skyjade_leggings_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)DAItems.SKYJADE_CHESTPLATE.get(), 750)
         .group("altar_sword_repair")
         .save(consumer, this.name("skyjade_chestplate_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)DAItems.SKYJADE_HELMET.get(), 750)
         .group("altar_sword_repair")
         .save(consumer, this.name("skyjade_helmet_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)DAItems.SKYJADE_GLOVES.get(), 750)
         .group("altar_sword_repair")
         .save(consumer, this.name("skyjade_gloves_repairing"));
      this.makeSword(DAItems.SKYJADE_TOOLS_SWORD, DAItems.SKYJADE).save(consumer);
      this.makeAxe(DAItems.SKYJADE_TOOLS_AXE, DAItems.SKYJADE).save(consumer);
      this.makePickaxe(DAItems.SKYJADE_TOOLS_PICKAXE, DAItems.SKYJADE).save(consumer);
      this.makeShovel(DAItems.SKYJADE_TOOLS_SHOVEL, DAItems.SKYJADE).save(consumer);
      this.makeHoe(DAItems.SKYJADE_TOOLS_HOE, DAItems.SKYJADE).save(consumer);
      this.makeBoots(DAItems.SKYJADE_BOOTS, DAItems.SKYJADE).save(consumer);
      this.makeLeggings(DAItems.SKYJADE_LEGGINGS, DAItems.SKYJADE).save(consumer);
      this.makeChestplate(DAItems.SKYJADE_CHESTPLATE, DAItems.SKYJADE).save(consumer);
      this.makeHelmet(DAItems.SKYJADE_HELMET, DAItems.SKYJADE).save(consumer);
      this.makeRing(DAItems.SKYJADE_RING, (Item)DAItems.SKYJADE.get()).save(consumer);
      this.makeGloves(DAItems.SKYJADE_GLOVES, DAItems.SKYJADE).save(consumer);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, (ItemLike)DAItems.STRATUS_INGOT.get())
         .requires((ItemLike)DABlocks.CHROMATIC_AERCLOUD.get(), 5)
         .requires(Items.PROCESSED_GRAVITITE)
         .requires((ItemLike)AetherItems.ZANITE_GEMSTONE.get())
         .requires((ItemLike)AetherItems.AMBROSIUM_SHARD.get())
         .requires((ItemLike)DAItems.SKYJADE.get())
         .unlockedBy(getHasName((ItemLike)DABlocks.STERLING_AERCLOUD.get()), has((ItemLike)DABlocks.STERLING_AERCLOUD.get()))
         .save(consumer);
      this.makeFullBlock((Item)DAItems.STRATUS_INGOT.get(), (Block)DABlocks.STRATUS_BLOCK.get()).save(consumer, this.name("stratus_block_from_stratus"));
      this.materialFromBlock((Block)DABlocks.STRATUS_BLOCK.get(), (Item)DAItems.STRATUS_INGOT.get()).save(consumer, this.name("stratus_from_stratus_block"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)DAItems.STRATUS_SWORD.get(), 1500)
         .group("altar_sword_repair")
         .save(consumer, this.name("stratus_sword_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)DAItems.STRATUS_AXE.get(), 1500)
         .group("altar_sword_repair")
         .save(consumer, this.name("stratus_axe_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)DAItems.STRATUS_PICKAXE.get(), 1500)
         .group("altar_sword_repair")
         .save(consumer, this.name("stratus_pickaxe_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)DAItems.STRATUS_SHOVEL.get(), 1500)
         .group("altar_sword_repair")
         .save(consumer, this.name("stratus_shovel_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)DAItems.STRATUS_HOE.get(), 1500)
         .group("altar_sword_repair")
         .save(consumer, this.name("stratus_hoe_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)DAItems.STRATUS_BOOTS.get(), 1500)
         .group("altar_sword_repair")
         .save(consumer, this.name("stratus_boots_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)DAItems.STRATUS_LEGGINGS.get(), 1500)
         .group("altar_sword_repair")
         .save(consumer, this.name("stratus_leggings_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)DAItems.STRATUS_CHESTPLATE.get(), 1500)
         .group("altar_sword_repair")
         .save(consumer, this.name("stratus_chestplate_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)DAItems.STRATUS_HELMET.get(), 1500)
         .group("altar_sword_repair")
         .save(consumer, this.name("stratus_helmet_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)DAItems.STRATUS_GLOVES.get(), 1500)
         .group("altar_sword_repair")
         .save(consumer, this.name("stratus_gloves_repairing"));
      this.stratusSmithingRecipe(consumer, (Item)AetherItems.GRAVITITE_SWORD.get(), RecipeCategory.COMBAT, (Item)DAItems.STRATUS_SWORD.get());
      this.stratusSmithingRecipe(consumer, (Item)AetherItems.GRAVITITE_AXE.get(), RecipeCategory.COMBAT, (Item)DAItems.STRATUS_AXE.get());
      this.stratusSmithingRecipe(consumer, (Item)AetherItems.GRAVITITE_PICKAXE.get(), RecipeCategory.COMBAT, (Item)DAItems.STRATUS_PICKAXE.get());
      this.stratusSmithingRecipe(consumer, (Item)AetherItems.GRAVITITE_SHOVEL.get(), RecipeCategory.COMBAT, (Item)DAItems.STRATUS_SHOVEL.get());
      this.stratusSmithingRecipe(consumer, (Item)AetherItems.GRAVITITE_HOE.get(), RecipeCategory.COMBAT, (Item)DAItems.STRATUS_HOE.get());
      this.stratusSmithingRecipe(consumer, (Item)AetherItems.GRAVITITE_BOOTS.get(), RecipeCategory.COMBAT, (Item)DAItems.STRATUS_BOOTS.get());
      this.stratusSmithingRecipe(consumer, (Item)AetherItems.GRAVITITE_LEGGINGS.get(), RecipeCategory.COMBAT, (Item)DAItems.STRATUS_LEGGINGS.get());
      this.stratusSmithingRecipe(consumer, (Item)AetherItems.GRAVITITE_CHESTPLATE.get(), RecipeCategory.COMBAT, (Item)DAItems.STRATUS_CHESTPLATE.get());
      this.stratusSmithingRecipe(consumer, (Item)AetherItems.GRAVITITE_HELMET.get(), RecipeCategory.COMBAT, (Item)DAItems.STRATUS_HELMET.get());
      this.stratusSmithingRecipe(consumer, (Item)AetherItems.GRAVITITE_GLOVES.get(), RecipeCategory.COMBAT, (Item)DAItems.STRATUS_GLOVES.get());
      this.stratusSmithingRecipe(consumer, (Item)DAItems.GRAVITITE_RING.get(), RecipeCategory.COMBAT, (Item)DAItems.STRATUS_RING.get());
      this.smeltingFoodRecipe((ItemLike)DAItems.COOKED_QUAIL.get(), (ItemLike)DAItems.RAW_QUAIL.get(), 0.35F).save(consumer);
      this.smeltingFoodRecipe((ItemLike)DAItems.COOKED_AERGLOW_FISH.get(), (ItemLike)DAItems.RAW_AERGLOW_FISH.get(), 0.35F).save(consumer);
      this.SmokingFoodRecipe((ItemLike)DAItems.COOKED_QUAIL.get(), (ItemLike)DAItems.RAW_QUAIL.get(), 0.35F)
         .save(consumer, this.name("cooked_quail_from_smoker"));
      this.SmokingFoodRecipe((ItemLike)DAItems.COOKED_AERGLOW_FISH.get(), (ItemLike)DAItems.RAW_AERGLOW_FISH.get(), 0.35F)
         .save(consumer, this.name("cooked_aerglow_fish_from_smoker"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)DAItems.BLUE_SQUASH_SLICE.get(), 4)
         .requires((ItemLike)DABlocks.BLUE_SQUASH.get(), 1)
         .unlockedBy(getHasName((ItemLike)DABlocks.BLUE_SQUASH.get()), has((ItemLike)DABlocks.BLUE_SQUASH.get()))
         .save(consumer, this.name("slice_from_blue_squash"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)DAItems.GREEN_SQUASH_SLICE.get(), 4)
         .requires((ItemLike)DABlocks.GREEN_SQUASH.get(), 1)
         .unlockedBy(getHasName((ItemLike)DABlocks.GREEN_SQUASH.get()), has((ItemLike)DABlocks.GREEN_SQUASH.get()))
         .save(consumer, this.name("slice_from_green_squash"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)DAItems.PURPLE_SQUASH_SLICE.get(), 4)
         .requires((ItemLike)DABlocks.PURPLE_SQUASH.get(), 1)
         .unlockedBy(getHasName((ItemLike)DABlocks.PURPLE_SQUASH.get()), has((ItemLike)DABlocks.PURPLE_SQUASH.get()))
         .save(consumer, this.name("slice_from_purple_squash"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)DAItems.SQUASH_SEEDS.get(), 1)
         .requires(Ingredient.of(DATags.Items.SQUASH_SLICE), 1)
         .unlockedBy(getHasName(DATags.Items.SQUASH_SLICE), has(DATags.Items.SQUASH_SLICE))
         .save(consumer, this.name("seeds_from_squash_slice"));
      ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, (ItemLike)DABlocks.BLUE_SQUASH.get(), 1)
         .define('V', (ItemLike)DAItems.BLUE_SQUASH_SLICE.get())
         .pattern("VV")
         .pattern("VV")
         .unlockedBy(getHasName((ItemLike)DABlocks.BLUE_SQUASH.get()), has((ItemLike)DABlocks.BLUE_SQUASH.get()))
         .save(consumer, this.name("blue_squash_from_slices"));
      ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, (ItemLike)DABlocks.GREEN_SQUASH.get(), 1)
         .define('V', (ItemLike)DAItems.GREEN_SQUASH_SLICE.get())
         .pattern("VV")
         .pattern("VV")
         .unlockedBy(getHasName((ItemLike)DABlocks.GREEN_SQUASH.get()), has((ItemLike)DABlocks.GREEN_SQUASH.get()))
         .save(consumer, this.name("green_squash_from_slices"));
      ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, (ItemLike)DABlocks.PURPLE_SQUASH.get(), 1)
         .define('V', (ItemLike)DAItems.PURPLE_SQUASH_SLICE.get())
         .pattern("VV")
         .pattern("VV")
         .unlockedBy(getHasName((ItemLike)DABlocks.PURPLE_SQUASH.get()), has((ItemLike)DABlocks.PURPLE_SQUASH.get()))
         .save(consumer, this.name("purple_squash_from_slices"));
      ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, net.minecraft.world.item.Items.CAKE)
         .group("minecraft:cake")
         .define('U', net.neoforged.neoforge.common.Tags.Items.BUCKETS_MILK)
         .define('S', net.minecraft.world.item.Items.SUGAR)
         .define('Y', net.minecraft.world.item.Items.WHEAT)
         .define('O', net.neoforged.neoforge.common.Tags.Items.EGGS)
         .pattern("UUU")
         .pattern("SOS")
         .pattern("YYY")
         .unlockedBy(getHasName((ItemLike)DAItems.QUAIL_EGG.get()), has(net.neoforged.neoforge.common.Tags.Items.EGGS))
         .save(consumer, this.name("cake"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, net.minecraft.world.item.Items.PUMPKIN_PIE)
         .group("minecraft:pumpkin_pie")
         .requires(net.neoforged.neoforge.common.Tags.Items.EGGS)
         .requires(net.minecraft.world.item.Items.PUMPKIN)
         .requires(net.minecraft.world.item.Items.SUGAR)
         .unlockedBy(getHasName((ItemLike)DAItems.QUAIL_EGG.get()), has(net.neoforged.neoforge.common.Tags.Items.EGGS))
         .save(consumer, this.name("pumpkin_pie"));
      this.dye(consumer, net.minecraft.world.item.Items.CYAN_DYE, (Block)DABlocks.AETHER_CATTAILS.get());
      this.dye(consumer, net.minecraft.world.item.Items.CYAN_DYE, (Block)DABlocks.TALL_AETHER_CATTAILS.get(), 2);
      this.dye(consumer, net.minecraft.world.item.Items.PINK_DYE, (Block)DABlocks.AERLAVENDER.get());
      this.dye(consumer, net.minecraft.world.item.Items.PINK_DYE, (Block)DABlocks.TALL_AERLAVENDER.get(), 2);
      this.dye(consumer, net.minecraft.world.item.Items.PURPLE_DYE, (Block)DABlocks.RADIANT_ORCHID.get());
      this.dye(consumer, net.minecraft.world.item.Items.ORANGE_DYE, (Block)DABlocks.GOLDEN_FLOWER.get(), 2);
      this.dye(consumer, net.minecraft.world.item.Items.WHITE_DYE, (Block)DABlocks.ENCHANTED_BLOSSOM.get());
      this.dye(consumer, net.minecraft.world.item.Items.RED_DYE, (Block)DABlocks.SKY_TULIPS.get());
      this.dye(consumer, net.minecraft.world.item.Items.BLUE_DYE, (Block)DABlocks.IASPOVE.get());
      this.dye(consumer, net.minecraft.world.item.Items.ORANGE_DYE, (Block)DABlocks.GOLDEN_ASPESS.get());
      this.dye(consumer, net.minecraft.world.item.Items.PURPLE_DYE, (Block)DABlocks.ECHAISY.get());
      SpecialRecipeBuilder.special(FloatyScarfColoring::new).save(consumer, this.name("floaty_scarf_coloring"));
      this.copyTemplate(consumer, (ItemLike)DAItems.STORMFORGED_SMITHING_TEMPLATE.get(), (ItemLike)DABlocks.NIMBUS_STONE.get());
      this.copyTemplateGravitite(consumer, (ItemLike)DAItems.STORMFORGED_SMITHING_TEMPLATE.get(), (ItemLike)DABlocks.NIMBUS_STONE.get());
      this.makeFullBlock((Item)DAItems.SQUALL_PLATE.get(), (Block)DABlocks.SQUALL_BLOCK.get()).save(consumer, this.name("squall_block_from_squall"));
      this.materialFromBlock((Block)DABlocks.SQUALL_BLOCK.get(), (Item)DAItems.SQUALL_PLATE.get()).save(consumer, this.name("squall_from_squall_block"));
      SimpleCookingRecipeBuilder.smelting(
            Ingredient.of(
               new ItemLike[]{
                  (ItemLike)DAItems.STORMFORGED_BOOTS.get(),
                  (ItemLike)DAItems.STORMFORGED_LEGGINGS.get(),
                  (ItemLike)DAItems.STORMFORGED_CHESTPLATE.get(),
                  (ItemLike)DAItems.STORMFORGED_HELMET.get(),
                  (ItemLike)DAItems.STORMFORGED_GLOVES.get()
               }
            ),
            RecipeCategory.MISC,
            (ItemLike)DAItems.SQUALL_PLATE.get(),
            0.1F,
            200
         )
         .unlockedBy("has_boots", has((ItemLike)DAItems.STORMFORGED_BOOTS.get()))
         .unlockedBy("has_leggings", has((ItemLike)DAItems.STORMFORGED_LEGGINGS.get()))
         .unlockedBy("has_chestplate", has((ItemLike)DAItems.STORMFORGED_CHESTPLATE.get()))
         .unlockedBy("has_helmet", has((ItemLike)DAItems.STORMFORGED_HELMET.get()))
         .unlockedBy("has_gloves", has((ItemLike)DAItems.STORMFORGED_GLOVES.get()))
         .group(getSmeltingRecipeName((ItemLike)DAItems.SQUALL_PLATE.get()))
         .save(consumer, this.name(getSmeltingRecipeName((ItemLike)DAItems.SQUALL_PLATE.get())));
      this.stormSmithingRecipe(consumer, (Item)DAItems.SKYJADE_BOOTS.get(), RecipeCategory.COMBAT, (Item)DAItems.STORMFORGED_BOOTS.get());
      this.stormSmithingRecipe(consumer, (Item)DAItems.SKYJADE_LEGGINGS.get(), RecipeCategory.COMBAT, (Item)DAItems.STORMFORGED_LEGGINGS.get());
      this.stormSmithingRecipe(consumer, (Item)DAItems.SKYJADE_CHESTPLATE.get(), RecipeCategory.COMBAT, (Item)DAItems.STORMFORGED_CHESTPLATE.get());
      this.stormSmithingRecipe(consumer, (Item)DAItems.SKYJADE_HELMET.get(), RecipeCategory.COMBAT, (Item)DAItems.STORMFORGED_HELMET.get());
      this.stormSmithingRecipe(consumer, (Item)DAItems.SKYJADE_GLOVES.get(), RecipeCategory.COMBAT, (Item)DAItems.STORMFORGED_GLOVES.get());
      this.stormSmithingRecipe(consumer, (Item)DAItems.SKYJADE_TOOLS_SWORD.get(), RecipeCategory.COMBAT, (Item)DAItems.STORM_SWORD.get());
      this.stormSmithingRecipe(consumer, net.minecraft.world.item.Items.BOW, RecipeCategory.COMBAT, (Item)DAItems.STORM_BOW.get());
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.AETHER_COARSE_DIRT.get(), 4)
         .define('D', ((Block)AetherBlocks.AETHER_DIRT.get()).asItem())
         .define('G', Blocks.GRAVEL)
         .pattern("DG")
         .pattern("GD")
         .unlockedBy(getHasName((ItemLike)AetherBlocks.AETHER_DIRT.get()), has((ItemLike)AetherBlocks.AETHER_DIRT.get()))
         .save(consumer, this.name("aether_coarse_dirt"));
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, (ItemLike)DAItems.AFTERBURNER.get(), 1)
         .define('O', (ItemLike)DAItems.SUN_CORE.get())
         .define('P', Blocks.OBSIDIAN)
         .pattern(" P ")
         .pattern("POP")
         .pattern(" P ")
         .unlockedBy(getHasName((ItemLike)DAItems.SUN_CORE.get()), has((ItemLike)DAItems.SUN_CORE.get()))
         .save(consumer, this.name("afterburner"));
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, (ItemLike)DABlocks.COMBINER.get(), 1)
         .define('O', (ItemLike)DAItems.SKYJADE.get())
         .define('W', (ItemLike)AetherBlocks.SKYROOT_PLANKS.get())
         .define('H', (ItemLike)AetherBlocks.HOLYSTONE.get())
         .define('A', (ItemLike)AetherItems.AMBROSIUM_SHARD.get())
         .pattern("WHW")
         .pattern("OAO")
         .pattern("HOH")
         .unlockedBy(getHasName((ItemLike)DAItems.SKYJADE.get()), has((ItemLike)DAItems.SKYJADE.get()))
         .save(consumer, this.name("combiner"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, (ItemLike)DAItems.GOLDEN_SWET_BALL.get())
         .requires((ItemLike)DAItems.GOLDEN_GRASS_SEEDS.get())
         .requires((ItemLike)AetherItems.SWET_BALL.get())
         .unlockedBy(getHasName((ItemLike)DAItems.GOLDEN_GRASS_SEEDS.get()), has((ItemLike)DAItems.GOLDEN_GRASS_SEEDS.get()))
         .save(consumer);
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, (ItemLike)AetherItems.POISON_DART.get(), 8)
         .define('D', (ItemLike)AetherItems.GOLDEN_DART.get())
         .define('B', DATags.Items.POISON_BUCKET)
         .pattern("DDD")
         .pattern("DBD")
         .pattern("DDD")
         .unlockedBy(getHasName((ItemLike)AetherItems.GOLDEN_DART.get()), has((ItemLike)AetherItems.GOLDEN_DART.get()))
         .unlockedBy(getHasName(DATags.Items.POISON_BUCKET), has(DATags.Items.POISON_BUCKET))
         .save(consumer, this.name("poison_dart"));
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.CRAFTING_TABLE, 1)
         .group("minecraft:crafting_table")
         .define('P', Items.PLANKS_CRAFTING)
         .pattern("PP")
         .pattern("PP")
         .unlockedBy(getHasName((ItemLike)AetherBlocks.SKYROOT_PLANKS.get()), has((ItemLike)AetherBlocks.SKYROOT_PLANKS.get()))
         .save(consumer, this.name("skyroot_crafting_table"));
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)DABlocks.AERGLOW_BLOSSOM_BLOCK.get(), 1)
         .define('A', (ItemLike)DAItems.AERGLOW_BLOSSOM.get())
         .pattern("AA")
         .pattern("AA")
         .unlockedBy(getHasName((ItemLike)DAItems.AERGLOW_BLOSSOM.get()), has((ItemLike)DAItems.AERGLOW_BLOSSOM.get()))
         .save(consumer);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)DABlocks.AETHER_MOSS_CARPET.get(), 3)
         .define('A', (ItemLike)DABlocks.AETHER_MOSS_BLOCK.get())
         .pattern("AA")
         .unlockedBy(getHasName((ItemLike)DABlocks.AETHER_MOSS_BLOCK.get()), has((ItemLike)DABlocks.AETHER_MOSS_BLOCK.get()))
         .save(consumer);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)DABlocks.CLOUDBLOOM_CARPET.get(), 1)
         .define('A', (ItemLike)DAItems.CLOUDBLOOM_BOUQUET.get())
         .pattern("AA")
         .unlockedBy(getHasName((ItemLike)DAItems.CLOUDBLOOM_BOUQUET.get()), has((ItemLike)DAItems.CLOUDBLOOM_BOUQUET.get()))
         .save(consumer);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.BREWING_STAND, 1)
         .group("minecraft:brewing_stand")
         .define('I', (ItemLike)DAItems.BIO_CRYSTAL.get())
         .define('D', (ItemLike)AetherBlocks.HOLYSTONE.get())
         .pattern(" I ")
         .pattern("DDD")
         .unlockedBy(getHasName((ItemLike)DAItems.BIO_CRYSTAL.get()), has((ItemLike)DAItems.BIO_CRYSTAL.get()))
         .save(consumer, this.name("crystal_brewing_stand"));
      this.copyTemplate(consumer, (ItemLike)DAItems.STRATUS_SMITHING_TEMPLATE.get(), (ItemLike)AetherBlocks.HOLYSTONE.get());
      this.copyTemplateGravitite(consumer, (ItemLike)DAItems.STRATUS_SMITHING_TEMPLATE.get(), (ItemLike)AetherBlocks.HOLYSTONE.get());
      this.makeRing(DAItems.GRAVITITE_RING, Items.PROCESSED_GRAVITITE).save(consumer);
      goldBallRecipe((Block)DABlocks.GOLDEN_GRASS_BLOCK.get(), (Block)AetherBlocks.AETHER_DIRT.get())
         .save(consumer, this.name("golden_grass_block_from_aether_dirt"));
      goldBallRecipe((Block)DABlocks.GOLDEN_GRASS_BLOCK.get(), (Block)AetherBlocks.AETHER_GRASS_BLOCK.get())
         .save(consumer, this.name("golden_grass_block_from_aether_grass_block"));
      glowingSporesRecipe((Block)DABlocks.TALL_GLOWING_GRASS.get(), Blocks.TALL_GRASS).save(consumer, this.name("glowing_tall_grass_from_grass"));
      glowingSporesRecipe((Block)DABlocks.GLOWING_VINE.get(), Blocks.VINE).save(consumer, this.name("glowing_vine_from_vine"));
      this.freezingRecipe(RecipeCategory.MISC, (ItemLike)DAItems.FROZEN_GOLDEN_BERRIES.get(), (ItemLike)DAItems.GOLDEN_BERRIES.get(), 1.0F, 50)
         .save(consumer, this.name("golden_berries_freezing"));
      this.enchantingRecipe(RecipeCategory.MISC, (ItemLike)DAItems.ENCHANTED_ANTIDOTE.get(), (ItemLike)DAItems.ANTIDOTE.get(), 1.0F, 750)
         .save(consumer, this.name("antidote_enchanting"));
      this.enchantingRecipe(RecipeCategory.MISC, net.minecraft.world.item.Items.WHEAT_SEEDS, (ItemLike)DABlocks.GLOWING_SPORES.get(), 1.0F, 50)
         .save(consumer, this.name("glowing_spores"));
      this.enchantingRecipe(RecipeCategory.MISC, (ItemLike)DAItems.SQUASH_SEEDS.get(), net.minecraft.world.item.Items.PUMPKIN_SEEDS, 5.0F, 50)
         .save(consumer, this.name("squash_seeds_enchanting"));
      this.enchantingRecipe(RecipeCategory.MISC, (ItemLike)DABlocks.CLORITE.get(), (ItemLike)DABlocks.RAW_CLORITE.get(), 0.15F, 50)
         .save(consumer, this.name("raw_clorite_enchanting"));
      this.enchantingRecipe(RecipeCategory.MISC, (ItemLike)AetherBlocks.COLD_AERCLOUD.get(), (ItemLike)DABlocks.AERCLOUD_ROOTS.get(), 1.0F, 50)
         .save(consumer, this.name("cold_aercloud_from_aercloud_roots"));
      this.enchantingRecipe(RecipeCategory.MISC, (ItemLike)AetherBlocks.BLUE_AERCLOUD.get(), (ItemLike)DABlocks.BLUE_AERCLOUD_MUSHROOM_BLOCK.get(), 1.0F, 250)
         .save(consumer, this.name("blue_aercloud_from_blue_aercloud_mushroom_blocks"));
      this.enchantingRecipe(RecipeCategory.MISC, (ItemLike)AetherBlocks.GOLDEN_AERCLOUD.get(), (ItemLike)DABlocks.PINK_AERCLOUD_MUSHROOM_BLOCK.get(), 1.0F, 500)
         .save(consumer, this.name("golden_aercloud_from_pink_aercloud_mushroom_blocks"));
      this.enchantingRecipe(RecipeCategory.MISC, (ItemLike)DAItems.REMEDY_BUCKET.get(), (ItemLike)DAItems.PLACEABLE_POISON_BUCKET.get(), 0.35F, 500)
         .save(consumer, this.name("iron_remedy_bucket_enchanting"));
      this.enchantingRecipe(RecipeCategory.BUILDING_BLOCKS, (ItemLike)DABlocks.CHROMATIC_AERCLOUD.get(), (ItemLike)DABlocks.STERLING_AERCLOUD.get(), 2.0F, 2000)
         .save(consumer, this.name("stratus_enchanting"));
      this.hiddenEnchantingRecipe(
            RecipeCategory.MISC, (ItemLike)DAItems.MUSIC_DISC_A_MORNING_WISH.get(), net.minecraft.world.item.Items.MUSIC_DISC_OTHERSIDE, 2.0F, 500
         )
         .save(consumer, this.name("a_moring_wish_enchanting"));
      this.hiddenEnchantingRecipe(RecipeCategory.MISC, (ItemLike)DAItems.MUSIC_DISC_NABOORU.get(), net.minecraft.world.item.Items.MUSIC_DISC_PIGSTEP, 1.0F, 500)
         .save(consumer, this.name("nabooru_enchanting"));
      CombiningRecipeBuilder.combining(DABookCategory.COMBINEABLE_MISC, (ItemLike)DAItems.ANTIDOTE.get(), 0.1F, 100)
         .requires((ItemLike)AetherItems.GOLDEN_AMBER.get())
         .requires((ItemLike)DAItems.FROZEN_GOLDEN_BERRIES.get())
         .requires((ItemLike)DAItems.BIO_CRYSTAL.get())
         .unlockedBy(getHasName((ItemLike)DAItems.BIO_CRYSTAL.get()), has((ItemLike)DAItems.BIO_CRYSTAL.get()))
         .save(consumer);
      ItemStack fodder = new ItemStack((ItemLike)DAItems.MOA_FODDER.get(), 1);
      fodder.set(DADataComponentTypes.MOA_FODDER, new MoaFodder(new MobEffectInstance(DAMobEffects.MOA_BONUS_JUMPS, 14400, 1)));
      CombiningRecipeBuilder.combining(DABookCategory.COMBINEABLE_FODDER, fodder, 0.1F, 100)
         .requires((ItemLike)AetherItems.GOLDEN_AMBER.get())
         .requires((ItemLike)DAItems.FROZEN_GOLDEN_BERRIES.get())
         .requires((ItemLike)DAItems.QUAIL_EGG.get())
         .unlockedBy(getHasName((ItemLike)DAItems.QUAIL_EGG.get()), has((ItemLike)DAItems.QUAIL_EGG.get()))
         .save(consumer);
      fodder.set(DADataComponentTypes.MOA_FODDER, new MoaFodder(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 14400, 1)));
      CombiningRecipeBuilder.combining(DABookCategory.COMBINEABLE_FODDER, fodder, 0.1F, 100)
         .requires((ItemLike)AetherItems.GOLDEN_AMBER.get())
         .requires((ItemLike)DAItems.FROZEN_GOLDEN_BERRIES.get())
         .requires((ItemLike)AetherBlocks.ICESTONE.get())
         .unlockedBy(getHasName((ItemLike)AetherBlocks.ICESTONE.get()), has((ItemLike)AetherBlocks.ICESTONE.get()))
         .save(consumer);
      fodder.set(DADataComponentTypes.MOA_FODDER, new MoaFodder(new MobEffectInstance(MobEffects.JUMP, 14400, 1)));
      CombiningRecipeBuilder.combining(DABookCategory.COMBINEABLE_FODDER, fodder, 0.1F, 100)
         .requires((ItemLike)AetherItems.GOLDEN_AMBER.get())
         .requires((ItemLike)DAItems.FROZEN_GOLDEN_BERRIES.get())
         .requires((ItemLike)AetherItems.BLUE_BERRY.get())
         .unlockedBy(getHasName((ItemLike)AetherItems.BLUE_BERRY.get()), has((ItemLike)AetherItems.BLUE_BERRY.get()))
         .save(consumer);
      PoisonConversionRecipeBuilder.conversion(DABlocks.GREEN_SQUASH.asItem(), DABlocks.PURPLE_SQUASH.asItem())
         .unlockedBy(getHasName((ItemLike)DABlocks.GREEN_SQUASH.get()), has((ItemLike)DABlocks.GREEN_SQUASH.get()))
         .group("poison_squash")
         .save(consumer, this.name("purple_squash_from_green_squash"));
      PoisonConversionRecipeBuilder.conversion(DABlocks.BLUE_SQUASH.asItem(), DABlocks.PURPLE_SQUASH.asItem())
         .unlockedBy(getHasName((ItemLike)DABlocks.BLUE_SQUASH.get()), has((ItemLike)DABlocks.BLUE_SQUASH.get()))
         .group("poison_squash")
         .save(consumer, this.name("purple_squash_from_blue_squash"));
      PoisonConversionRecipeBuilder.conversion(AetherBlocks.QUICKSOIL_GLASS.asItem(), (ItemLike)AetherBlocks.QUICKSOIL.get())
         .unlockedBy(getHasName((ItemLike)AetherBlocks.QUICKSOIL_GLASS.get()), has((ItemLike)AetherBlocks.QUICKSOIL_GLASS.get()))
         .save(consumer, this.name("quicksoil_from_poison"));
      PoisonConversionRecipeBuilder.conversion(AetherItems.ENCHANTED_DART.asItem(), (ItemLike)AetherItems.GOLDEN_DART.get())
         .unlockedBy(getHasName((ItemLike)AetherItems.ENCHANTED_DART.get()), has((ItemLike)AetherItems.ENCHANTED_DART.get()))
         .save(consumer, this.name("golden_dart_from_poison"));
      PoisonConversionRecipeBuilder.conversion(AetherItems.ENCHANTED_DART_SHOOTER.asItem(), (ItemLike)AetherItems.GOLDEN_DART_SHOOTER.get())
         .unlockedBy(getHasName((ItemLike)AetherItems.ENCHANTED_DART_SHOOTER.get()), has((ItemLike)AetherItems.ENCHANTED_DART_SHOOTER.get()))
         .save(consumer, this.name("golden_dart_shooter_from_poison"));
      PoisonConversionRecipeBuilder.conversion(AetherItems.SKYROOT_REMEDY_BUCKET.asItem(), (ItemLike)AetherItems.SKYROOT_POISON_BUCKET.get())
         .unlockedBy(getHasName((ItemLike)AetherItems.SKYROOT_REMEDY_BUCKET.get()), has((ItemLike)AetherItems.SKYROOT_REMEDY_BUCKET.get()))
         .save(consumer, this.name("skyroot_poison_bucket_from_poison"));
      PoisonConversionRecipeBuilder.conversion(DAItems.REMEDY_BUCKET.asItem(), (ItemLike)DAItems.PLACEABLE_POISON_BUCKET.get())
         .unlockedBy(getHasName((ItemLike)DAItems.REMEDY_BUCKET.get()), has((ItemLike)DAItems.REMEDY_BUCKET.get()))
         .save(consumer, this.name("poison_bucket_from_poison"));
      PoisonConversionRecipeBuilder.conversion(AetherItems.ENCHANTED_BERRY.asItem(), (ItemLike)AetherItems.BLUE_BERRY.get())
         .unlockedBy(getHasName((ItemLike)AetherItems.ENCHANTED_BERRY.get()), has((ItemLike)AetherItems.ENCHANTED_BERRY.get()))
         .save(consumer, this.name("blueberry_from_poison"));
      PoisonConversionRecipeBuilder.conversion(AetherBlocks.GOLDEN_AERCLOUD.asItem(), (ItemLike)AetherBlocks.COLD_AERCLOUD.get())
         .unlockedBy(getHasName((ItemLike)AetherBlocks.GOLDEN_AERCLOUD.get()), has((ItemLike)AetherBlocks.GOLDEN_AERCLOUD.get()))
         .save(consumer, this.name("cold_aercloud_from_golden_aercloud"));
      PoisonConversionRecipeBuilder.conversion(DABlocks.CLORITE.asItem(), (ItemLike)DABlocks.RAW_CLORITE.get())
         .unlockedBy(getHasName((ItemLike)DABlocks.CLORITE.get()), has((ItemLike)DABlocks.CLORITE.get()))
         .save(consumer, this.name("raw_clorite_from_poison"));
      PoisonConversionRecipeBuilder.conversion(AetherItems.HEALING_STONE.asItem(), (ItemLike)AetherBlocks.HOLYSTONE.get())
         .unlockedBy(getHasName((ItemLike)AetherItems.HEALING_STONE.get()), has((ItemLike)AetherItems.HEALING_STONE.get()))
         .save(consumer, this.name("holystone_from_poison"));
      PoisonConversionRecipeBuilder.conversion(AetherBlocks.ENCHANTED_GRAVITITE.asItem(), (ItemLike)AetherBlocks.GRAVITITE_ORE.get())
         .unlockedBy(getHasName((ItemLike)AetherBlocks.ENCHANTED_GRAVITITE.get()), has((ItemLike)AetherBlocks.ENCHANTED_GRAVITITE.get()))
         .save(consumer, this.name("gravitite_ore_from_poison"));
      PoisonConversionRecipeBuilder.conversion(net.minecraft.world.item.Items.POTATO, net.minecraft.world.item.Items.POISONOUS_POTATO)
         .unlockedBy(getHasName(net.minecraft.world.item.Items.POTATO), has(net.minecraft.world.item.Items.POTATO))
         .save(consumer, this.name("poisonous_poison_from_poison"));
   }

   protected ShapedRecipeBuilder makeRing(Supplier<? extends Item> ring, TagKey<Item> material) {
      return ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, (ItemLike)ring.get())
         .define('#', material)
         .pattern(" # ")
         .pattern("# #")
         .pattern(" # ")
         .unlockedBy(getHasName(material), has(material));
   }

   protected static String getHasName(TagKey<Item> pItemLike) {
      return "has_" + pItemLike.location().getPath();
   }

   protected void copyTemplate(RecipeOutput p_266734_, ItemLike p_267133_, ItemLike p_267023_) {
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, p_267133_, 2)
         .define('#', net.minecraft.world.item.Items.DIAMOND)
         .define('C', p_267023_)
         .define('S', p_267133_)
         .pattern("#S#")
         .pattern("#C#")
         .pattern("###")
         .unlockedBy(getHasName(p_267133_), has(p_267133_))
         .save(p_266734_);
   }

   protected void copyTemplateGravitite(RecipeOutput p_266734_, ItemLike p_267133_, ItemLike p_267023_) {
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, p_267133_, 2)
         .define('#', Items.PROCESSED_GRAVITITE)
         .define('C', p_267023_)
         .define('S', p_267133_)
         .pattern("#S#")
         .pattern("#C#")
         .pattern("###")
         .unlockedBy(getHasName(p_267133_), has(p_267133_))
         .save(p_266734_, this.name(getItemName(p_267133_) + "_from_gravitite"));
   }

   protected void stonecuttingRecipe(RecipeOutput consumer, RecipeCategory category, ItemLike item, ItemLike ingredient) {
      this.stonecuttingRecipe(consumer, category, item, ingredient, 1);
   }

   protected void stonecuttingRecipe(RecipeOutput consumer, RecipeCategory category, ItemLike item, ItemLike ingredient, int count) {
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(new ItemLike[]{ingredient}), category, item, count)
         .unlockedBy(getHasName(ingredient), has(ingredient))
         .save(consumer, this.name(getConversionRecipeName(item, ingredient) + "_stonecutting"));
   }

   protected void stratusSmithingRecipe(RecipeOutput consumer, Item ingredient, RecipeCategory category, Item item) {
      SmithingTransformRecipeBuilder.smithing(
            Ingredient.of(new ItemLike[]{(ItemLike)DAItems.STRATUS_SMITHING_TEMPLATE.get()}),
            Ingredient.of(new ItemLike[]{ingredient}),
            Ingredient.of(new ItemLike[]{(ItemLike)DAItems.STRATUS_INGOT.get()}),
            category,
            item
         )
         .unlocks("has_stratus_ingot", has((ItemLike)DAItems.STRATUS_INGOT.get()))
         .save(consumer, this.name(getItemName(item)) + "_smithing");
   }

   protected void stormSmithingRecipe(RecipeOutput consumer, Item ingredient, RecipeCategory category, Item item) {
      SmithingTransformRecipeBuilder.smithing(
            Ingredient.of(new ItemLike[]{(ItemLike)DAItems.STORMFORGED_SMITHING_TEMPLATE.get()}),
            Ingredient.of(new ItemLike[]{ingredient}),
            Ingredient.of(new ItemLike[]{(ItemLike)DAItems.SQUALL_PLATE.get()}),
            category,
            item
         )
         .unlocks("has_squall_plate", has((ItemLike)DAItems.SQUALL_PLATE.get()))
         .save(consumer, this.name(getItemName(item)) + "_smithing");
   }

   protected SimpleCookingRecipeBuilder smeltingFoodRecipe(ItemLike result, ItemLike ingredient, float experience) {
      return SimpleCookingRecipeBuilder.smelting(Ingredient.of(new ItemLike[]{ingredient}), RecipeCategory.FOOD, result, experience, 200)
         .unlockedBy(getHasName(ingredient), has(ingredient));
   }

   protected SimpleCookingRecipeBuilder smeltingBlockRecipe(ItemLike result, ItemLike ingredient, float experience) {
      return SimpleCookingRecipeBuilder.smelting(Ingredient.of(new ItemLike[]{ingredient}), RecipeCategory.BUILDING_BLOCKS, result, experience, 200)
         .unlockedBy(getHasName(ingredient), has(ingredient));
   }

   protected SimpleCookingRecipeBuilder SmokingFoodRecipe(ItemLike result, ItemLike ingredient, float experience) {
      return SimpleCookingRecipeBuilder.smoking(Ingredient.of(new ItemLike[]{ingredient}), RecipeCategory.FOOD, result, experience, 100)
         .unlockedBy(getHasName(ingredient), has(ingredient));
   }

   protected ShapedRecipeBuilder makeFullBlock(Item material, Block result) {
      return ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, result)
         .define('#', material)
         .pattern("###")
         .pattern("###")
         .pattern("###")
         .unlockedBy(getHasName(material), has(material));
   }

   protected ShapelessRecipeBuilder materialFromBlock(Block material, Item result) {
      return ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, result, 9).requires(material).unlockedBy(getHasName(material), has(material));
   }

   protected ShapedRecipeBuilder makeBoat(Supplier<? extends Item> boat, Block material) {
      return ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, (ItemLike)boat.get())
         .define('#', material)
         .pattern("# #")
         .pattern("###")
         .unlockedBy(getHasName(material), has(material));
   }

   protected ShapelessRecipeBuilder makeChestBoat(Item chestBoat, Item boat) {
      return ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, chestBoat)
         .requires(boat)
         .requires(net.neoforged.neoforge.common.Tags.Items.CHESTS_WOODEN)
         .unlockedBy(getHasName(chestBoat), has(chestBoat));
   }

   protected void makeHangingSign(RecipeOutput consumer, Item sign, Block log) {
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, sign, 6)
         .group("hanging_sign")
         .define('#', log)
         .define('X', net.minecraft.world.item.Items.CHAIN)
         .pattern("X X")
         .pattern("###")
         .pattern("###")
         .unlockedBy("has_stripped_logs", has(log))
         .save(consumer);
   }

   protected void sign(RecipeOutput consumer, Item sign, Block planks) {
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, sign, 3)
         .group("wooden_sign")
         .define('#', planks)
         .define('/', net.neoforged.neoforge.common.Tags.Items.RODS_WOODEN)
         .pattern("###")
         .pattern("###")
         .pattern(" / ")
         .unlockedBy(getHasName(planks), has(planks))
         .save(consumer);
   }

   protected void brick(RecipeOutput consumer, Block brick, Block stone) {
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, brick, 4)
         .define('A', stone)
         .pattern("AA")
         .pattern("AA")
         .unlockedBy(getHasName(stone), has(stone))
         .save(consumer);
   }

   protected void dye(RecipeOutput consumer, Item dye, Block flower, int count) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, dye, count)
         .requires(flower)
         .unlockedBy(getHasName(flower), has(flower))
         .save(consumer, this.name(getItemName(dye) + "_from_" + getItemName(flower)));
   }

   protected void dye(RecipeOutput consumer, Item dye, Block flower) {
      this.dye(consumer, dye, flower, 1);
   }

   protected static BlockStateRecipeBuilder goldBallRecipe(Block result, Block ingredient) {
      return BlockStateRecipeBuilder.recipe(BlockStateIngredient.of(new Block[]{ingredient}), result, GoldenSwetBallRecipe::new);
   }

   protected static BlockStateRecipeBuilder glowingSporesRecipe(Block result, Block ingredient) {
      return BlockStateRecipeBuilder.recipe(BlockStateIngredient.of(new Block[]{ingredient}), result, GlowingSporesRecipe::new);
   }

   protected RecipeBuilder slab(Block slab, Supplier<? extends Block> material) {
      return slabBuilder(RecipeCategory.BUILDING_BLOCKS, slab, Ingredient.of(new ItemLike[]{(ItemLike)material.get()}))
         .unlockedBy(getHasName((ItemLike)material.get()), has((ItemLike)material.get()));
   }

   protected ResourceLocation name(String name) {
      return ResourceLocation.fromNamespaceAndPath("deep_aether", name);
   }

   protected ResourceLocation packName(String name) {
      return this.packNameSpace(name, "pack");
   }

   protected ResourceLocation packNameSpace(String name, String pack) {
      return ResourceLocation.fromNamespaceAndPath("deep_aether", pack + "/" + name);
   }
}
