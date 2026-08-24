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

public interface IPlatformFluidHelperInternal<T> extends IPlatformFluidHelper<T> {
   Optional<TextureAtlasSprite> getStillFluidSprite(T var1);

   Component getDisplayName(T var1);

   int getColorTint(T var1);

   long getAmount(T var1);

   DataComponentPatch getComponentsPatch(T var1);

   void getTooltip(List<Component> var1, T var2, TooltipFlag var3);

   T copy(T var1);

   T copyWithAmount(T var1, long var2);

   T normalize(T var1);

   Optional<T> getContainedFluid(ITypedIngredient<?> var1);

   Codec<T> getCodec();
}
