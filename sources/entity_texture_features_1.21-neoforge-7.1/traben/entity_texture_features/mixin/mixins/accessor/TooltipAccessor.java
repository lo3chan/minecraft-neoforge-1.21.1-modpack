package traben.entity_texture_features.mixin.mixins.accessor;

import java.util.List;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Tooltip.class})
public interface TooltipAccessor {
   @Accessor("cachedTooltip")
   void setCachedTooltip(List<FormattedCharSequence> var1);
}
