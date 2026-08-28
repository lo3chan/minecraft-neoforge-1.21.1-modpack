/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.core.component.DataComponentPatch
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.TooltipFlag
 */
package mezz.jei.common.platform;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

public interface IPlatformFluidHelperInternal<T>
extends IPlatformFluidHelper<T> {
    public Optional<TextureAtlasSprite> getStillFluidSprite(T var1);

    public Component getDisplayName(T var1);

    public int getColorTint(T var1);

    public long getAmount(T var1);

    public DataComponentPatch getComponentsPatch(T var1);

    public void getTooltip(List<Component> var1, T var2, TooltipFlag var3);

    public T copy(T var1);

    public T copyWithAmount(T var1, long var2);

    public T normalize(T var1);

    public Optional<T> getContainedFluid(ITypedIngredient<?> var1);

    public Codec<T> getCodec();
}

