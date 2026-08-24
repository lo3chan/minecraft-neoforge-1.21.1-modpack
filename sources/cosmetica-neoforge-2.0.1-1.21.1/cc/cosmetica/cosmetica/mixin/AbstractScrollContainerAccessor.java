package cc.cosmetica.cosmetica.mixin;

import cc.cosmetica.kupe.api.gui.AbstractScrollContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(
   value = {AbstractScrollContainer.class},
   remap = false
)
public interface AbstractScrollContainerAccessor {
   @Accessor("scrollPercent")
   float getScrollPercent();
}
