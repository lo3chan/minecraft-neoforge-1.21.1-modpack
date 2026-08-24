package com.aetherteam.aether.integration.rei;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.data.resources.registries.AetherDataMaps;
import com.aetherteam.aether.integration.rei.categories.ban.BlockBanRecipeCategory;
import com.aetherteam.aether.integration.rei.categories.ban.ItemBanRecipeCategory;
import com.aetherteam.aether.integration.rei.categories.ban.PlacementBanRecipeDisplay;
import com.aetherteam.aether.integration.rei.categories.block.AetherBlockStateRecipeCategory;
import com.aetherteam.aether.integration.rei.categories.block.BiomeParameterRecipeCategory;
import com.aetherteam.aether.integration.rei.categories.item.AetherCookingRecipeCategory;
import com.aetherteam.aether.integration.rei.categories.item.AetherCookingRecipeDisplay;
import com.aetherteam.aether.inventory.menu.AltarMenu;
import com.aetherteam.aether.inventory.menu.FreezerMenu;
import com.aetherteam.aether.inventory.menu.IncubatorMenu;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.aetherteam.aether.recipe.recipes.ban.BlockBanRecipe;
import com.aetherteam.aether.recipe.recipes.ban.ItemBanRecipe;
import com.aetherteam.aether.recipe.recipes.block.AccessoryFreezableRecipe;
import com.aetherteam.aether.recipe.recipes.block.AmbrosiumRecipe;
import com.aetherteam.aether.recipe.recipes.block.IcestoneFreezableRecipe;
import com.aetherteam.aether.recipe.recipes.block.PlacementConversionRecipe;
import com.aetherteam.aether.recipe.recipes.block.SwetBallRecipe;
import com.aetherteam.aether.recipe.recipes.item.AbstractAetherCookingRecipe;
import com.aetherteam.aether.recipe.recipes.item.AltarRepairRecipe;
import com.aetherteam.aether.recipe.recipes.item.EnchantingRecipe;
import com.aetherteam.aether.recipe.recipes.item.FreezingRecipe;
import com.aetherteam.aether.recipe.recipes.item.IncubationRecipe;
import com.aetherteam.nitrogen.integration.rei.categories.fuel.AbstractFuelCategory;
import com.aetherteam.nitrogen.integration.rei.displays.BlockStateRecipeDisplay;
import com.aetherteam.nitrogen.integration.rei.displays.FuelDisplay;
import java.util.ArrayList;
import java.util.List;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.client.registry.transfer.simple.SimpleTransferHandler;
import me.shedaniel.rei.api.client.registry.transfer.simple.SimpleTransferHandler.IntRange;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

@REIPluginClient
public class AetherREIClientPlugin implements REIClientPlugin {
   private static final ResourceLocation LIT_PROGRESS_TRANSPARENT_TEXTURE = ResourceLocation.fromNamespaceAndPath("aether", "menu/lit_progress_transparent");
   private static final ResourceLocation LIT_PROGRESS_BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath("aether", "menu/lit_progress_background");

   public static List<FuelRecipe> getFuelRecipes() {
      List<FuelRecipe> fuelRecipes = new ArrayList<>();
      BuiltInRegistries.ITEM
         .getDataMap(AetherDataMaps.ALTAR_FUEL)
         .forEach(
            (item, fuel) -> fuelRecipes.add(
               new FuelRecipe(List.of(new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(item))), fuel.burnTime(), (Block)AetherBlocks.ALTAR.get())
            )
         );
      BuiltInRegistries.ITEM
         .getDataMap(AetherDataMaps.FREEZER_FUEL)
         .forEach(
            (item, fuel) -> fuelRecipes.add(
               new FuelRecipe(List.of(new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(item))), fuel.burnTime(), (Block)AetherBlocks.FREEZER.get())
            )
         );
      BuiltInRegistries.ITEM
         .getDataMap(AetherDataMaps.INCUBATOR_FUEL)
         .forEach(
            (item, fuel) -> fuelRecipes.add(
               new FuelRecipe(List.of(new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(item))), fuel.burnTime(), (Block)AetherBlocks.INCUBATOR.get())
            )
         );
      return fuelRecipes;
   }

   public void registerDisplays(DisplayRegistry registry) {
      registry.registerRecipeFiller(
         ItemBanRecipe.class,
         (RecipeType)AetherRecipeTypes.ITEM_PLACEMENT_BAN.get(),
         recipex -> PlacementBanRecipeDisplay.ofItem((ItemBanRecipe)recipex.value())
      );
      registry.registerRecipeFiller(
         BlockBanRecipe.class,
         (RecipeType)AetherRecipeTypes.BLOCK_PLACEMENT_BAN.get(),
         recipex -> PlacementBanRecipeDisplay.ofBlock((BlockBanRecipe)recipex.value())
      );
      registry.registerRecipeFiller(
         AccessoryFreezableRecipe.class,
         (RecipeType)AetherRecipeTypes.ACCESSORY_FREEZABLE.get(),
         recipex -> new BlockStateRecipeDisplay(AetherREIServerPlugin.ACCESSORY_FREEZABLE, (AccessoryFreezableRecipe)recipex.value())
      );
      registry.registerRecipeFiller(
         AmbrosiumRecipe.class,
         (RecipeType)AetherRecipeTypes.AMBROSIUM_ENCHANTING.get(),
         recipex -> new BlockStateRecipeDisplay(AetherREIServerPlugin.AMBROSIUM_ENCHANTING, (AmbrosiumRecipe)recipex.value())
      );
      registry.registerRecipeFiller(
         IcestoneFreezableRecipe.class,
         (RecipeType)AetherRecipeTypes.ICESTONE_FREEZABLE.get(),
         recipex -> new BlockStateRecipeDisplay(AetherREIServerPlugin.ICESTONE_FREEZABLE, (IcestoneFreezableRecipe)recipex.value())
      );
      registry.registerRecipeFiller(
         PlacementConversionRecipe.class,
         (RecipeType)AetherRecipeTypes.PLACEMENT_CONVERSION.get(),
         recipex -> new BlockStateRecipeDisplay(AetherREIServerPlugin.PLACEMENT_CONVERSION, (PlacementConversionRecipe)recipex.value())
      );
      registry.registerRecipeFiller(
         SwetBallRecipe.class,
         (RecipeType)AetherRecipeTypes.SWET_BALL_CONVERSION.get(),
         recipex -> new BlockStateRecipeDisplay(AetherREIServerPlugin.SWET_BALL_CONVERSION, (SwetBallRecipe)recipex.value())
      );

      for (FuelRecipe fuelRecipe : getFuelRecipes()) {
         registry.add(new FuelDisplay(AetherREIServerPlugin.AETHER_FUEL, fuelRecipe.inputItems(), fuelRecipe.burnTime(), fuelRecipe.usageBlock()));
      }

      for (RecipeHolder<? extends AbstractAetherCookingRecipe> recipe : registry.getRecipeManager()
         .getAllRecipesFor((RecipeType)AetherRecipeTypes.ENCHANTING.get())) {
         if (recipe.value() instanceof var enchanting) {
            registry.add(AetherCookingRecipeDisplay.of(AetherREIServerPlugin.ALTAR_ENCHANTING, enchanting));
         } else if (recipe.value() instanceof var repair) {
            registry.add(AetherCookingRecipeDisplay.of(AetherREIServerPlugin.ALTAR_REPAIR, repair));
         }
      }

      registry.registerFiller(
         o -> o instanceof RecipeHolder<? extends Recipe<?>> holder && holder.value() instanceof EnchantingRecipe,
         recipex -> AetherCookingRecipeDisplay.of(AetherREIServerPlugin.ALTAR_ENCHANTING, (EnchantingRecipe & Recipe)((RecipeHolder)recipex).value())
      );
      registry.registerFiller(
         o -> o instanceof RecipeHolder<? extends Recipe<?>> holder && holder.value() instanceof AltarRepairRecipe,
         recipex -> AetherCookingRecipeDisplay.of(AetherREIServerPlugin.ALTAR_REPAIR, (AltarRepairRecipe & Recipe)((RecipeHolder)recipex).value())
      );
      registry.registerRecipeFiller(
         FreezingRecipe.class,
         (RecipeType)AetherRecipeTypes.FREEZING.get(),
         recipex -> AetherCookingRecipeDisplay.of(AetherREIServerPlugin.FREEZING, (FreezingRecipe & Recipe)recipex.value())
      );
      registry.registerRecipeFiller(
         IncubationRecipe.class,
         (RecipeType)AetherRecipeTypes.INCUBATION.get(),
         recipex -> AetherCookingRecipeDisplay.of(AetherREIServerPlugin.INCUBATING, (IncubationRecipe)recipex.value())
      );
   }

   public void registerCategories(CategoryRegistry registry) {
      registry.add(new BlockBanRecipeCategory());
      registry.add(new ItemBanRecipeCategory());
      registry.add(AetherBlockStateRecipeCategory.accessoryFreezable());
      registry.add(AetherBlockStateRecipeCategory.ambrosium());
      registry.add(AetherBlockStateRecipeCategory.icestoneFreezable());
      registry.add(BiomeParameterRecipeCategory.placementConversion());
      registry.add(BiomeParameterRecipeCategory.swetBall());
      registry.add(new AbstractFuelCategory(AetherREIServerPlugin.AETHER_FUEL, LIT_PROGRESS_TRANSPARENT_TEXTURE, LIT_PROGRESS_BACKGROUND_TEXTURE) {
         public Component getTitle() {
            return Component.translatable("gui.aether.jei.fuel");
         }
      });
      registry.add(AetherCookingRecipeCategory.altarRepair());
      registry.add(AetherCookingRecipeCategory.altarEnchanting());
      registry.add(AetherCookingRecipeCategory.freezing());
      registry.add(AetherCookingRecipeCategory.incubating());
      registry.addWorkstations(
         AetherREIServerPlugin.AETHER_FUEL,
         new EntryStack[]{
            EntryStacks.of((ItemLike)AetherBlocks.FREEZER.get()),
            EntryStacks.of((ItemLike)AetherBlocks.ALTAR.get()),
            EntryStacks.of((ItemLike)AetherBlocks.INCUBATOR.get())
         }
      );
      registry.addWorkstations(AetherREIServerPlugin.FREEZING, new EntryStack[]{EntryStacks.of((ItemLike)AetherBlocks.FREEZER.get())});
      registry.addWorkstations(AetherREIServerPlugin.ALTAR_REPAIR, new EntryStack[]{EntryStacks.of((ItemLike)AetherBlocks.ALTAR.get())});
      registry.addWorkstations(AetherREIServerPlugin.ALTAR_ENCHANTING, new EntryStack[]{EntryStacks.of((ItemLike)AetherBlocks.ALTAR.get())});
      registry.addWorkstations(AetherREIServerPlugin.INCUBATING, new EntryStack[]{EntryStacks.of((ItemLike)AetherBlocks.INCUBATOR.get())});
   }

   public void registerTransferHandlers(TransferHandlerRegistry registry) {
      registry.register(SimpleTransferHandler.create(AltarMenu.class, AetherREIServerPlugin.ALTAR_ENCHANTING, new IntRange(0, 1)));
      registry.register(SimpleTransferHandler.create(AltarMenu.class, AetherREIServerPlugin.ALTAR_REPAIR, new IntRange(0, 1)));
      registry.register(SimpleTransferHandler.create(FreezerMenu.class, AetherREIServerPlugin.FREEZING, new IntRange(0, 1)));
      registry.register(SimpleTransferHandler.create(IncubatorMenu.class, AetherREIServerPlugin.INCUBATING, new IntRange(0, 1)));
   }
}
