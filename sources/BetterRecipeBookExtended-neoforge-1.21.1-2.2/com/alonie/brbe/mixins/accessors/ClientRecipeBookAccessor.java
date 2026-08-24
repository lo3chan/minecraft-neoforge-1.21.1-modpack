package com.alonie.brbe.mixins.accessors;

import net.minecraft.client.ClientRecipeBook;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({ClientRecipeBook.class})
public interface ClientRecipeBookAccessor {
   @Invoker("setupCollections")
   void brbe$setupCollections(Iterable<RecipeHolder<?>> var1, RegistryAccess var2);
}
