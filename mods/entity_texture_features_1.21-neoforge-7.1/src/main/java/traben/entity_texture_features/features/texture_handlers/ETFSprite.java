/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.texture.SpriteContents
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.resources.metadata.animation.FrameSize
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.server.packs.resources.ResourceMetadata
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.texture_handlers;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFUtils2;

public class ETFSprite {
    public final boolean isETFAltered;
    private final TextureAtlasSprite sprite;
    private final TextureAtlasSprite emissiveSprite;

    public ETFSprite(@NotNull TextureAtlasSprite originalSprite, @NotNull ETFTexture etfTexture, boolean isNotVariant) {
        Optional resource;
        ResourceLocation emissiveId;
        SpriteContents contents;
        if (isNotVariant) {
            this.sprite = originalSprite;
            this.isETFAltered = false;
        } else {
            ResourceLocation variantId = etfTexture.getTextureIdentifier(null);
            Optional resource2 = Minecraft.getInstance().getResourceManager().getResource(variantId);
            if (resource2.isPresent()) {
                TextureAtlasSprite possibleVariant = null;
                contents = ETFSprite.load(ETFUtils2.res(String.valueOf(variantId) + "-etf_sprite"), (Resource)resource2.get());
                try {
                    if (contents != null) {
                        possibleVariant = new TextureAtlasSprite(variantId, contents, contents.width(), contents.height(), 0, 0);
                    }
                }
                finally {
                    if (contents != null) {
                        contents.close();
                    }
                }
                this.sprite = Objects.requireNonNullElse(possibleVariant, originalSprite);
                this.isETFAltered = possibleVariant != null;
            } else {
                this.sprite = originalSprite;
                this.isETFAltered = false;
            }
        }
        TextureAtlasSprite possibleEmissive = null;
        if (etfTexture.eSuffix != null && (emissiveId = etfTexture.getEmissiveIdentifierOfCurrentState()) != null && (resource = Minecraft.getInstance().getResourceManager().getResource(emissiveId)).isPresent()) {
            contents = ETFSprite.load(ETFUtils2.res(String.valueOf(emissiveId) + "-etf_sprite"), (Resource)resource.get());
            try {
                if (contents != null) {
                    possibleEmissive = new TextureAtlasSprite(emissiveId, contents, contents.width(), contents.height(), 0, 0);
                }
            }
            finally {
                if (contents != null) {
                    contents.close();
                }
            }
        }
        this.emissiveSprite = possibleEmissive;
    }

    @Nullable
    public static SpriteContents load(ResourceLocation id, Resource resource) {
        NativeImage nativeImage;
        ResourceMetadata animationResourceMetadata;
        try {
            animationResourceMetadata = resource.metadata();
        }
        catch (Exception var8) {
            return null;
        }
        try (InputStream inputStream = resource.open();){
            nativeImage = NativeImage.read((InputStream)inputStream);
        }
        catch (IOException var10) {
            return null;
        }
        FrameSize spriteDimensions = new FrameSize(nativeImage.getWidth(), nativeImage.getHeight());
        if (Mth.isMultipleOf((int)nativeImage.getWidth(), (int)spriteDimensions.width()) && Mth.isMultipleOf((int)nativeImage.getHeight(), (int)spriteDimensions.height())) {
            return new SpriteContents(id, spriteDimensions, nativeImage, animationResourceMetadata);
        }
        nativeImage.close();
        return null;
    }

    @NotNull
    public TextureAtlasSprite getEmissive() {
        return this.emissiveSprite;
    }

    public boolean isEmissive() {
        return this.emissiveSprite != null;
    }

    @NotNull
    public TextureAtlasSprite getSpriteVariant() {
        return this.sprite;
    }
}

