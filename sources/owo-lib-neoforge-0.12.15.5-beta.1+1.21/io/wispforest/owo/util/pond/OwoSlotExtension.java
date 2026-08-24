package io.wispforest.owo.util.pond;

import io.wispforest.owo.ui.core.PositionedRectangle;
import org.jetbrains.annotations.Nullable;

public interface OwoSlotExtension {
   void owo$setDisabledOverride(boolean var1);

   boolean owo$getDisabledOverride();

   void owo$setScissorArea(@Nullable PositionedRectangle var1);

   @Nullable
   PositionedRectangle owo$getScissorArea();
}
