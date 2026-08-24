package com.alonie.brbe.mixins.accessors;

import java.util.Set;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({RecipeCollection.class})
public interface RecipeCollectionAccessor {
   @Accessor("fitsDimensions")
   Set<RecipeHolder<?>> getFitsDimensions();

   @Accessor("craftable")
   Set<RecipeHolder<?>> brbe$getCraftable();

   @Invoker("canCraft")
   void invokeCanCraft(StackedContents var1, int var2, int var3, RecipeBook var4);
}
