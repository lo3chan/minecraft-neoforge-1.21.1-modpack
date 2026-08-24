package com.alonie.brbe.interfaces.unlockrecipes;

import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public interface IMixinRecipeManager {
   Set<ResourceLocation> brbe$getServerUnlockedRecipes();
}
