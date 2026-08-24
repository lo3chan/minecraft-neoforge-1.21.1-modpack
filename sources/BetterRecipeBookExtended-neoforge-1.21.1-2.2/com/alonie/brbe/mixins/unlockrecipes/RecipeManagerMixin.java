package com.alonie.brbe.mixins.unlockrecipes;

import com.alonie.brbe.interfaces.unlockrecipes.IMixinRecipeManager;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({RecipeManager.class})
public class RecipeManagerMixin implements IMixinRecipeManager {
   @Unique
   private final Set<ResourceLocation> brbe$serverUnlockedRecipes = new HashSet<>();

   @Override
   public Set<ResourceLocation> brbe$getServerUnlockedRecipes() {
      return this.brbe$serverUnlockedRecipes;
   }
}
