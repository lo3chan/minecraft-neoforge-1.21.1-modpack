/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.minecraft.client.renderer.texture.SpriteContents
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.client.resources.TextureAtlasHolder
 *  net.minecraft.client.resources.metadata.animation.AnimationMetadataSection
 *  net.minecraft.client.resources.metadata.gui.GuiMetadataSection
 *  net.minecraft.client.resources.metadata.gui.GuiSpriteScaling
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.metadata.MetadataSectionSerializer
 *  net.minecraft.server.packs.resources.ResourceMetadata
 *  org.slf4j.Logger
 */
package mezz.jei.common.gui.textures;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import mezz.jei.common.Constants;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.gui.GuiMetadataSection;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.ResourceMetadata;
import org.slf4j.Logger;

public class JeiGuiSpriteManager
extends TextureAtlasHolder {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<ResourceLocation, GuiMetadataSection> BUNDLED_METADATA_BY_SPRITE = new ConcurrentHashMap<ResourceLocation, GuiMetadataSection>();

    public JeiGuiSpriteManager(TextureManager textureManager) {
        super(textureManager, Constants.LOCATION_JEI_GUI_TEXTURE_ATLAS, Constants.JEI_GUI_TEXTURE_ATLAS_ID, Set.of(AnimationMetadataSection.SERIALIZER, GuiMetadataSection.TYPE));
    }

    public TextureAtlasSprite getSprite(ResourceLocation location) {
        return super.getSprite(location);
    }

    public GuiSpriteScaling getSpriteScaling(TextureAtlasSprite sprite) {
        SpriteContents contents = sprite.contents();
        return JeiGuiSpriteManager.getSpriteScaling(contents.name(), contents.metadata());
    }

    static GuiSpriteScaling getSpriteScaling(ResourceLocation spriteId, ResourceMetadata metadata) {
        return metadata.getSection((MetadataSectionSerializer)GuiMetadataSection.TYPE).orElseGet(() -> JeiGuiSpriteManager.getBundledMetadata(spriteId)).scaling();
    }

    private static GuiMetadataSection getBundledMetadata(ResourceLocation spriteId) {
        if (!spriteId.getNamespace().equals("jei")) {
            return GuiMetadataSection.DEFAULT;
        }
        return BUNDLED_METADATA_BY_SPRITE.computeIfAbsent(spriteId, JeiGuiSpriteManager::loadBundledMetadata);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static GuiMetadataSection loadBundledMetadata(ResourceLocation spriteId) {
        String metadataPath = "/assets/jei/textures/jei/atlas/gui/" + spriteId.getPath() + ".png.mcmeta";
        try (InputStream inputStream = JeiGuiSpriteManager.class.getResourceAsStream(metadataPath);){
            if (inputStream == null) {
                GuiMetadataSection guiMetadataSection2 = GuiMetadataSection.DEFAULT;
                return guiMetadataSection2;
            }
            GuiMetadataSection guiMetadataSection = ResourceMetadata.fromJsonStream((InputStream)inputStream).getSection((MetadataSectionSerializer)GuiMetadataSection.TYPE).orElse(GuiMetadataSection.DEFAULT);
            return guiMetadataSection;
        }
        catch (IOException | RuntimeException e) {
            LOGGER.error("Failed to load JEI's bundled GUI metadata for {}", (Object)spriteId, (Object)e);
            return GuiMetadataSection.DEFAULT;
        }
    }
}

