/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.components.Tooltip
 *  net.minecraft.util.FormattedCharSequence
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package traben.entity_texture_features.mixin.mixins.accessor;

import java.util.List;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={Tooltip.class})
public interface TooltipAccessor {
    @Accessor(value="cachedTooltip")
    public void setCachedTooltip(List<FormattedCharSequence> var1);
}

