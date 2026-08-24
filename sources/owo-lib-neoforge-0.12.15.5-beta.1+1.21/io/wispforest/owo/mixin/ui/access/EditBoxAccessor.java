package io.wispforest.owo.mixin.ui.access;

import net.minecraft.client.gui.components.MultilineTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({MultilineTextField.class})
public interface EditBoxAccessor {
   @Mutable
   @Accessor("width")
   void owo$setWidth(int var1);

   @Accessor("selectCursor")
   void owo$setSelectionEnd(int var1);

   @Accessor("selectCursor")
   int owo$getSelectionEnd();
}
