/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.texture.TextureAtlas
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.resources.ResourceLocation
 */
package mezz.jei.common.gui.elements;

import java.util.function.Supplier;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.common.gui.textures.JeiGuiSpriteManager;
import mezz.jei.common.platform.IPlatformRenderHelper;
import mezz.jei.common.platform.Services;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public class DrawableSprite
implements IDrawableStatic {
    private final Supplier<TextureAtlasSprite> spriteSupplier;
    private final int width;
    private final int height;

    public DrawableSprite(JeiGuiSpriteManager spriteManager, ResourceLocation spriteId) {
        this(() -> spriteManager.getSprite(spriteId));
    }

    public DrawableSprite(JeiGuiSpriteManager spriteManager, ResourceLocation spriteId, int width, int height) {
        this(() -> spriteManager.getSprite(spriteId), width, height);
    }

    public DrawableSprite(TextureAtlas textureAtlas, ResourceLocation spriteId) {
        this(() -> textureAtlas.getSprite(spriteId));
    }

    public DrawableSprite(TextureAtlas textureAtlas, ResourceLocation spriteId, int width, int height) {
        this(() -> textureAtlas.getSprite(spriteId), width, height);
    }

    DrawableSprite(Supplier<TextureAtlasSprite> spriteSupplier) {
        this(spriteSupplier, 0, 0);
    }

    DrawableSprite(Supplier<TextureAtlasSprite> spriteSupplier, int width, int height) {
        if (width < 0 || height < 0 || width == 0 != (height == 0)) {
            throw new IllegalArgumentException("DrawableSprite size must be positive, or both dimensions must be 0 to use the sprite size");
        }
        this.spriteSupplier = spriteSupplier;
        this.width = width;
        this.height = height;
    }

    @Override
    public int getWidth() {
        TextureAtlasSprite sprite = this.spriteSupplier.get();
        return this.getWidth(sprite);
    }

    @Override
    public int getHeight() {
        TextureAtlasSprite sprite = this.spriteSupplier.get();
        return this.getHeight(sprite);
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
        this.draw(guiGraphics, xOffset, yOffset, 0, 0, 0, 0);
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset, int maskTop, int maskBottom, int maskLeft, int maskRight) {
        TextureAtlasSprite sprite = this.spriteSupplier.get();
        int width = this.getWidth(sprite);
        int height = this.getHeight(sprite);
        int uWidth = width - (maskRight + maskLeft);
        int vHeight = height - (maskBottom + maskTop);
        IPlatformRenderHelper renderHelper = Services.PLATFORM.getRenderHelper();
        renderHelper.blitSprite(guiGraphics, sprite, width, height, maskLeft, maskTop, xOffset + maskLeft, yOffset + maskTop, uWidth, vHeight);
    }

    private int getWidth(TextureAtlasSprite sprite) {
        if (this.width > 0) {
            return this.width;
        }
        return sprite.contents().width();
    }

    private int getHeight(TextureAtlasSprite sprite) {
        if (this.height > 0) {
            return this.height;
        }
        return sprite.contents().height();
    }
}

