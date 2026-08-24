package mezz.jei.common.platform;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling.NineSlice;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface IPlatformRenderHelper {
   Font getFontRenderer(Minecraft var1, ItemStack var2);

   boolean shouldRender(MobEffectInstance var1);

   TextureAtlasSprite getParticleIcon(BakedModel var1);

   ItemColors getItemColors();

   Optional<NativeImage> getMainImage(TextureAtlasSprite var1);

   void renderTooltip(GuiGraphics var1, List<Either<FormattedText, TooltipComponent>> var2, int var3, int var4, Font var5, ItemStack var6);

   Component getName(TagKey<?> var1);

   BakedModel createLimitedQuadItemModel(BakedModel var1);

   @Nullable
   TextureAtlasSprite getTextureAtlasSprite(BlockState var1);

   void blitSprite(GuiGraphics var1, TextureAtlasSprite var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10);

   void blitNineSlicedSprite(GuiGraphics var1, TextureAtlasSprite var2, NineSlice var3, int var4, int var5, int var6, int var7);
}
