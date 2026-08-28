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
 *  net.minecraft.client.renderer.block.BlockModelShaper
 *  net.minecraft.client.renderer.block.BlockRenderDispatcher
 *  net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
 *  net.minecraft.client.renderer.texture.SpriteContents
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
 *  net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
 *  net.neoforged.neoforge.client.extensions.common.IClientItemExtensions$FontContext
 *  net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions
 *  net.neoforged.neoforge.client.model.data.ModelData
 *  net.neoforged.neoforge.common.Tags
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.neoforge.platform;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Optional;
import mezz.jei.common.platform.IPlatformRenderHelper;
import mezz.jei.neoforge.platform.NeoForgeLimitedQuadItemModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
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
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

public class RenderHelper
implements IPlatformRenderHelper {
    @Override
    public Font getFontRenderer(Minecraft minecraft, ItemStack itemStack) {
        IClientItemExtensions renderProperties = IClientItemExtensions.of((ItemStack)itemStack);
        Font fontRenderer = renderProperties.getFont(itemStack, IClientItemExtensions.FontContext.TOOLTIP);
        if (fontRenderer != null) {
            return fontRenderer;
        }
        return minecraft.font;
    }

    @Override
    public boolean shouldRender(MobEffectInstance potionEffect) {
        IClientMobEffectExtensions effectRenderer = IClientMobEffectExtensions.of((MobEffectInstance)potionEffect);
        return effectRenderer.isVisibleInInventory(potionEffect);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(BakedModel bakedModel) {
        return bakedModel.getParticleIcon(ModelData.EMPTY);
    }

    @Override
    public ItemColors getItemColors() {
        return Minecraft.getInstance().getItemColors();
    }

    @Override
    public Optional<NativeImage> getMainImage(TextureAtlasSprite sprite) {
        SpriteContents contents = sprite.contents();
        NativeImage[] frames = contents.byMipLevel;
        if (frames.length == 0) {
            return Optional.empty();
        }
        NativeImage frame = frames[0];
        return Optional.ofNullable(frame);
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, List<Either<FormattedText, TooltipComponent>> elements, int x, int y, Font font, ItemStack stack) {
        guiGraphics.renderComponentTooltipFromElements(font, elements, x, y, stack);
    }

    @Override
    public Component getName(TagKey<?> tagKey) {
        String tagTranslationKey = Tags.getTagTranslationKey(tagKey);
        return Component.translatableWithFallback((String)tagTranslationKey, (String)("#" + String.valueOf(tagKey.location())));
    }

    @Override
    public BakedModel createLimitedQuadItemModel(BakedModel bakedModel) {
        return NeoForgeLimitedQuadItemModel.wrap(bakedModel);
    }

    @Override
    @Nullable
    public TextureAtlasSprite getTextureAtlasSprite(BlockState blockState) {
        Minecraft minecraft = Minecraft.getInstance();
        BlockRenderDispatcher blockRendererDispatcher = minecraft.getBlockRenderer();
        BlockModelShaper blockModelShapes = blockRendererDispatcher.getBlockModelShaper();
        BakedModel blockModel = blockModelShapes.getBlockModel(blockState);
        TextureAtlasSprite textureAtlasSprite = this.getParticleIcon(blockModel);
        if (textureAtlasSprite.atlasLocation().equals((Object)MissingTextureAtlasSprite.getLocation())) {
            return null;
        }
        return textureAtlasSprite;
    }

    @Override
    public void blitSprite(GuiGraphics guiGraphics, TextureAtlasSprite sprite, int textureWidth, int textureHeight, int uPosition, int vPosition, int x, int y, int uWidth, int vHeight) {
        guiGraphics.blitSprite(sprite, textureWidth, textureHeight, uPosition, vPosition, x, y, 0, uWidth, vHeight);
    }

    @Override
    public void blitNineSlicedSprite(GuiGraphics guiGraphics, TextureAtlasSprite sprite, GuiSpriteScaling.NineSlice scaling, int xOffset, int yOffset, int width, int height) {
        guiGraphics.blitNineSlicedSprite(sprite, scaling, xOffset, yOffset, 0, width, height);
    }
}

