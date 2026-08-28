/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.color.block.BlockColors
 *  net.minecraft.client.color.item.ItemColors
 *  net.minecraft.client.renderer.ItemModelShaper
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
 *  net.minecraft.client.renderer.texture.SpriteContents
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.color;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import mezz.jei.common.platform.IPlatformRenderHelper;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.ErrorUtil;
import mezz.jei.library.color.ColorThief;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public final class ColorGetter {
    private static final Logger LOGGER = LogManager.getLogger();

    public List<Integer> getColors(ItemStack itemStack, int colorCount) {
        try {
            return this.unsafeGetColors(itemStack, colorCount);
        }
        catch (LinkageError | RuntimeException e) {
            String itemStackInfo = ErrorUtil.getItemStackInfo(itemStack);
            LOGGER.warn("Failed to get color name for {}", (Object)itemStackInfo, (Object)e);
            return Collections.emptyList();
        }
    }

    private List<Integer> unsafeGetColors(ItemStack itemStack, int colorCount) {
        Item item = itemStack.getItem();
        if (itemStack.isEmpty()) {
            return Collections.emptyList();
        }
        if (item instanceof BlockItem) {
            BlockItem itemBlock = (BlockItem)item;
            Block block = itemBlock.getBlock();
            if (block == null) {
                return Collections.emptyList();
            }
            return this.getBlockColors(block, colorCount);
        }
        return this.getItemColors(itemStack, colorCount);
    }

    private List<Integer> getItemColors(ItemStack itemStack, int colorCount) {
        IPlatformRenderHelper renderHelper = Services.PLATFORM.getRenderHelper();
        ItemColors itemColors = renderHelper.getItemColors();
        int renderColor = itemColors.getColor(itemStack, 0);
        TextureAtlasSprite textureAtlasSprite = ColorGetter.getTextureAtlasSprite(itemStack);
        if (textureAtlasSprite == null) {
            return Collections.emptyList();
        }
        return this.getColors(textureAtlasSprite, renderColor, colorCount);
    }

    private List<Integer> getBlockColors(Block block, int colorCount) {
        BlockState blockState = block.defaultBlockState();
        BlockColors blockColors = Minecraft.getInstance().getBlockColors();
        int renderColor = blockColors.getColor(blockState, null, null, 0);
        IPlatformRenderHelper renderHelper = Services.PLATFORM.getRenderHelper();
        TextureAtlasSprite textureAtlasSprite = renderHelper.getTextureAtlasSprite(blockState);
        if (textureAtlasSprite == null) {
            return Collections.emptyList();
        }
        return this.getColors(textureAtlasSprite, renderColor, colorCount);
    }

    public List<Integer> getColors(TextureAtlasSprite textureAtlasSprite, int renderColor, int colorCount) {
        if (colorCount <= 0) {
            return Collections.emptyList();
        }
        return ColorGetter.getNativeImage(textureAtlasSprite).map(bufferedImage -> {
            int[][] palette;
            ArrayList<Integer> colors = new ArrayList<Integer>(colorCount);
            for (int[] colorInt : palette = ColorThief.getPalette(bufferedImage, colorCount, 2, false)) {
                int red = (int)((float)(colorInt[0] - 1) * (float)(renderColor >> 16 & 0xFF) / 255.0f);
                int green = (int)((float)(colorInt[1] - 1) * (float)(renderColor >> 8 & 0xFF) / 255.0f);
                int blue = (int)((float)(colorInt[2] - 1) * (float)(renderColor & 0xFF) / 255.0f);
                red = Mth.clamp((int)red, (int)0, (int)255);
                green = Mth.clamp((int)green, (int)0, (int)255);
                blue = Mth.clamp((int)blue, (int)0, (int)255);
                int color = 0xFF000000 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
                colors.add(color);
            }
            return colors;
        }).orElseGet(Collections::emptyList);
    }

    private static Optional<NativeImage> getNativeImage(TextureAtlasSprite textureAtlasSprite) {
        SpriteContents contents = textureAtlasSprite.contents();
        int iconWidth = contents.width();
        int iconHeight = contents.height();
        if (iconWidth <= 0 || iconHeight <= 0) {
            return Optional.empty();
        }
        IPlatformRenderHelper renderHelper = Services.PLATFORM.getRenderHelper();
        return renderHelper.getMainImage(textureAtlasSprite);
    }

    @Nullable
    private static TextureAtlasSprite getTextureAtlasSprite(ItemStack itemStack) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemModelShaper itemModelMesher = itemRenderer.getItemModelShaper();
        BakedModel itemModel = itemModelMesher.getItemModel(itemStack);
        IPlatformRenderHelper renderHelper = Services.PLATFORM.getRenderHelper();
        TextureAtlasSprite particleTexture = renderHelper.getParticleIcon(itemModel);
        if (particleTexture.atlasLocation().equals((Object)MissingTextureAtlasSprite.getLocation())) {
            return null;
        }
        return particleTexture;
    }
}

