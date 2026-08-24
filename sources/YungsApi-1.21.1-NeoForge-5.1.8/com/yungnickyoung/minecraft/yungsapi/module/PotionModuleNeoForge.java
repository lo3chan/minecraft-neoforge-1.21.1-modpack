package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterPotion;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.PotionAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.registries.RegisterEvent.RegisterHelper;
import org.jetbrains.annotations.NotNull;

public class PotionModuleNeoForge {
   public static final List<IBrewingRecipe> BREWING_RECIPES = new ArrayList<>();

   public static void processEntries() {
      YungsApiNeoForge.loadingContextEventBus
         .addListener(
            YungsApiNeoForge.buildAutoRegistrar(
               Registries.POTION, AutoRegistrationManager.POTIONS, PotionModuleNeoForge::buildPotion, PotionModuleNeoForge::registerPotion
            )
         );
      NeoForge.EVENT_BUS.addListener(PotionModuleNeoForge::registerBrewingRecipes);
   }

   private static Potion buildPotion(AutoRegisterField data) {
      AutoRegisterPotion autoRegisterPotion = (AutoRegisterPotion)data.object();
      Potion potion = autoRegisterPotion.get();
      if (((PotionAccessor)potion).getName() == null) {
         String name = data.name().getNamespace() + "." + data.name().getPath();
         ((PotionAccessor)potion).setName(name);
      }

      return potion;
   }

   private static void registerPotion(AutoRegisterField data, Potion potion, RegisterHelper<Potion> helper) {
      Holder<Potion> holder = Registry.registerForHolder(BuiltInRegistries.POTION, data.name(), potion);
      ((AutoRegisterPotion)data.object()).setHolder(holder);
   }

   private static void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
      BREWING_RECIPES.forEach(recipe -> event.getBuilder().addRecipe(recipe));
   }

   public record BrewingRecipe(Holder<Potion> input, Supplier<Item> ingredient, Holder<Potion> output) implements IBrewingRecipe {
      public boolean isInput(ItemStack itemStack) {
         PotionContents potionContents = (PotionContents)itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
         return potionContents.is(this.input);
      }

      public boolean isIngredient(ItemStack itemStack) {
         return itemStack.getItem() == this.ingredient.get();
      }

      @ParametersAreNonnullByDefault
      @NotNull
      public ItemStack getOutput(ItemStack inputStack, ItemStack ingredientStack) {
         return this.isInput(inputStack) && this.isIngredient(ingredientStack)
            ? PotionContents.createItemStack(inputStack.getItem(), this.output)
            : ItemStack.EMPTY;
      }
   }
}
