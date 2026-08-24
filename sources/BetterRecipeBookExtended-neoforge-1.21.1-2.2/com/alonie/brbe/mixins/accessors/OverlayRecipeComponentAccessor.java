package com.alonie.brbe.mixins.accessors;

import java.util.List;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({OverlayRecipeComponent.class})
public interface OverlayRecipeComponentAccessor {
   @Accessor("recipeButtons")
   List<AbstractWidget> getRecipeButtons();

   @Accessor("time")
   float getTime();

   @Accessor("isFurnaceMenu")
   boolean isFurnaceMenu();
}
