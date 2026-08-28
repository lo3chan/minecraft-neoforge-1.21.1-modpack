/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  com.mojang.datafixers.util.Either
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.color.item.ItemColors
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.resources.metadata.gui.GuiSpriteScaling$NineSlice
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.inventory.tooltip.TooltipComponent
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
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
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
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
    public Font getFontRenderer(Minecraft var1, ItemStack var2);

    public boolean shouldRender(MobEffectInstance var1);

    public TextureAtlasSprite getParticleIcon(BakedModel var1);

    public ItemColors getItemColors();

    public Optional<NativeImage> getMainImage(TextureAtlasSprite var1);

    public void renderTooltip(GuiGraphics var1, List<Either<FormattedText, TooltipComponent>> var2, int var3, int var4, Font var5, ItemStack var6);

    public Component getName(TagKey<?> var1);

    public BakedModel createLimitedQuadItemModel(BakedModel var1);

    @Nullable
    public TextureAtlasSprite getTextureAtlasSprite(BlockState var1);

    public void blitSprite(GuiGraphics var1, TextureAtlasSprite var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10);

    public void blitNineSlicedSprite(GuiGraphics var1, TextureAtlasSprite var2, GuiSpriteScaling.NineSlice var3, int var4, int var5, int var6, int var7);
}

