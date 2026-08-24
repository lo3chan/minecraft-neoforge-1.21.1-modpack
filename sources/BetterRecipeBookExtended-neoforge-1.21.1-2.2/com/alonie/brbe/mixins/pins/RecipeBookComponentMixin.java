package com.alonie.brbe.mixins.pins;

import com.alonie.brbe.generic.pins.PinnableRecipeCollection;
import com.alonie.brbe.interfaces.IPinningComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({RecipeBookComponent.class})
public abstract class RecipeBookComponentMixin implements IPinningComponent<PinnableRecipeCollection> {
}
