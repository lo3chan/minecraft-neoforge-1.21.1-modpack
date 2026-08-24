package io.wispforest.owo.ext;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch.Builder;
import org.jetbrains.annotations.ApiStatus.Experimental;

public interface OwoItem {
   @Experimental
   default void deriveStackComponents(DataComponentMap source, Builder target) {
   }
}
