package net.blay09.mods.balm.platform.compatibility.recipeviewer;

import java.util.List;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;

public interface RecipeViewerOcclusionProvider<T extends AbstractContainerScreen<?>> {
   List<Rect2i> getOcclusions(T var1);
}
