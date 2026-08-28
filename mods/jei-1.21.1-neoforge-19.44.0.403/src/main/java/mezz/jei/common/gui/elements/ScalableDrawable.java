/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.MeshData
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.texture.SpriteContents
 *  net.minecraft.client.renderer.texture.TextureAtlas
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.resources.metadata.gui.GuiMetadataSection
 *  net.minecraft.client.resources.metadata.gui.GuiSpriteScaling
 *  net.minecraft.client.resources.metadata.gui.GuiSpriteScaling$NineSlice
 *  net.minecraft.client.resources.metadata.gui.GuiSpriteScaling$Tile
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.metadata.MetadataSectionSerializer
 *  net.minecraft.server.packs.resources.ResourceMetadata
 *  org.joml.Matrix4f
 */
package mezz.jei.common.gui.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.lang.runtime.SwitchBootstraps;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import mezz.jei.api.gui.drawable.IScalableDrawable;
import mezz.jei.common.gui.textures.JeiGuiSpriteManager;
import mezz.jei.common.platform.IPlatformRenderHelper;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.ImmutableRect2i;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.gui.GuiMetadataSection;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.ResourceMetadata;
import org.joml.Matrix4f;

public class ScalableDrawable
implements IScalableDrawable {
    private final Supplier<TextureAtlasSprite> spriteSupplier;
    private final Function<TextureAtlasSprite, GuiSpriteScaling> scalingSupplier;

    public ScalableDrawable(JeiGuiSpriteManager spriteManager, ResourceLocation spriteId) {
        this(() -> spriteManager.getSprite(spriteId), spriteManager::getSpriteScaling);
    }

    public ScalableDrawable(TextureAtlas textureAtlas, ResourceLocation spriteId) {
        this(() -> textureAtlas.getSprite(spriteId), ScalableDrawable::getSpriteScaling);
    }

    private ScalableDrawable(Supplier<TextureAtlasSprite> spriteSupplier, Function<TextureAtlasSprite, GuiSpriteScaling> scalingSupplier) {
        this.spriteSupplier = spriteSupplier;
        this.scalingSupplier = scalingSupplier;
    }

    public void draw(GuiGraphics guiGraphics, ImmutableRect2i area) {
        this.draw(guiGraphics, area.getX(), area.getY(), area.getWidth(), area.getHeight());
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset, int width, int height) {
        GuiSpriteScaling scaling;
        TextureAtlasSprite sprite = this.spriteSupplier.get();
        GuiSpriteScaling guiSpriteScaling = scaling = this.scalingSupplier.apply(sprite);
        Objects.requireNonNull(guiSpriteScaling);
        GuiSpriteScaling guiSpriteScaling2 = guiSpriteScaling;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{GuiSpriteScaling.Tile.class, GuiSpriteScaling.NineSlice.class}, (Object)guiSpriteScaling2, n)) {
            case 0: {
                GuiSpriteScaling.Tile tileScaling = (GuiSpriteScaling.Tile)guiSpriteScaling2;
                ScalableDrawable.blitTiledSpriteWithColor(guiGraphics, sprite, tileScaling, xOffset, yOffset, width, height, -1);
                break;
            }
            case 1: {
                GuiSpriteScaling.NineSlice nineSliceScaling = (GuiSpriteScaling.NineSlice)guiSpriteScaling2;
                IPlatformRenderHelper renderHelper = Services.PLATFORM.getRenderHelper();
                renderHelper.blitNineSlicedSprite(guiGraphics, sprite, nineSliceScaling, xOffset, yOffset, width, height);
                break;
            }
            default: {
                IPlatformRenderHelper renderHelper = Services.PLATFORM.getRenderHelper();
                renderHelper.blitSprite(guiGraphics, sprite, width, height, 0, 0, xOffset, yOffset, width, height);
            }
        }
    }

    public static void blitTiledSpriteWithColor(GuiGraphics guiGraphics, TextureAtlasSprite sprite, GuiSpriteScaling.Tile scaling, int xOffset, int yOffset, int width, int height, int color) {
        int tileWidth = scaling.width();
        int tileHeight = scaling.height();
        if (width <= 0 || height <= 0) {
            return;
        }
        if (tileWidth <= 0 || tileHeight <= 0) {
            throw new IllegalArgumentException("Tile size must be positive, got " + tileWidth + "x" + tileHeight);
        }
        for (int xTile = 0; xTile < width; xTile += tileWidth) {
            int uWidth = Math.min(tileWidth, width - xTile);
            for (int yTile = 0; yTile < height; yTile += tileHeight) {
                int vHeight = Math.min(tileHeight, height - yTile);
                ScalableDrawable.blitSprite(guiGraphics, sprite, tileWidth, tileHeight, 0, 0, xOffset + xTile, yOffset + yTile, uWidth, vHeight, color);
            }
        }
    }

    private static void blitSprite(GuiGraphics guiGraphics, TextureAtlasSprite sprite, int textureWidth, int textureHeight, int uPosition, int vPosition, int x, int y, int uWidth, int vHeight, int color) {
        if (uWidth <= 0 || vHeight <= 0) {
            return;
        }
        float u0 = sprite.getU((float)uPosition / (float)textureWidth);
        float u1 = sprite.getU((float)(uPosition + uWidth) / (float)textureWidth);
        float v0 = sprite.getV((float)vPosition / (float)textureHeight);
        float v1 = sprite.getV((float)(vPosition + vHeight) / (float)textureHeight);
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        RenderSystem.setShaderTexture((int)0, (ResourceLocation)sprite.atlasLocation());
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.enableBlend();
        Matrix4f matrix = guiGraphics.pose().last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferBuilder.addVertex(matrix, (float)x, (float)y, 0.0f).setUv(u0, v0).setColor(red, green, blue, alpha);
        bufferBuilder.addVertex(matrix, (float)x, (float)(y + vHeight), 0.0f).setUv(u0, v1).setColor(red, green, blue, alpha);
        bufferBuilder.addVertex(matrix, (float)(x + uWidth), (float)(y + vHeight), 0.0f).setUv(u1, v1).setColor(red, green, blue, alpha);
        bufferBuilder.addVertex(matrix, (float)(x + uWidth), (float)y, 0.0f).setUv(u1, v0).setColor(red, green, blue, alpha);
        BufferUploader.drawWithShader((MeshData)bufferBuilder.buildOrThrow());
        RenderSystem.disableBlend();
    }

    private static GuiSpriteScaling getSpriteScaling(TextureAtlasSprite sprite) {
        SpriteContents contents = sprite.contents();
        ResourceMetadata metadata = contents.metadata();
        return metadata.getSection((MetadataSectionSerializer)GuiMetadataSection.TYPE).orElse(GuiMetadataSection.DEFAULT).scaling();
    }
}

