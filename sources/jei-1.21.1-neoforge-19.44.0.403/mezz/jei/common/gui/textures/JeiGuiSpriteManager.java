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
import net.minecraft.server.packs.resources.ResourceMetadata;
import org.slf4j.Logger;

public class JeiGuiSpriteManager extends TextureAtlasHolder {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<ResourceLocation, GuiMetadataSection> BUNDLED_METADATA_BY_SPRITE = new ConcurrentHashMap<>();

   public JeiGuiSpriteManager(TextureManager textureManager) {
      super(
         textureManager,
         Constants.LOCATION_JEI_GUI_TEXTURE_ATLAS,
         Constants.JEI_GUI_TEXTURE_ATLAS_ID,
         Set.of(AnimationMetadataSection.SERIALIZER, GuiMetadataSection.TYPE)
      );
   }

   public TextureAtlasSprite getSprite(ResourceLocation location) {
      return super.getSprite(location);
   }

   public GuiSpriteScaling getSpriteScaling(TextureAtlasSprite sprite) {
      SpriteContents contents = sprite.contents();
      return getSpriteScaling(contents.name(), contents.metadata());
   }

   static GuiSpriteScaling getSpriteScaling(ResourceLocation spriteId, ResourceMetadata metadata) {
      return metadata.getSection(GuiMetadataSection.TYPE).orElseGet(() -> getBundledMetadata(spriteId)).scaling();
   }

   private static GuiMetadataSection getBundledMetadata(ResourceLocation spriteId) {
      return !spriteId.getNamespace().equals("jei")
         ? GuiMetadataSection.DEFAULT
         : BUNDLED_METADATA_BY_SPRITE.computeIfAbsent(spriteId, JeiGuiSpriteManager::loadBundledMetadata);
   }

   private static GuiMetadataSection loadBundledMetadata(ResourceLocation spriteId) {
      String metadataPath = "/assets/jei/textures/jei/atlas/gui/" + spriteId.getPath() + ".png.mcmeta";

      try {
         GuiMetadataSection var3;
         try (InputStream inputStream = JeiGuiSpriteManager.class.getResourceAsStream(metadataPath)) {
            if (inputStream == null) {
               return GuiMetadataSection.DEFAULT;
            }

            var3 = ResourceMetadata.fromJsonStream(inputStream).getSection(GuiMetadataSection.TYPE).orElse(GuiMetadataSection.DEFAULT);
         }

         return var3;
      } catch (RuntimeException | IOException var7) {
         LOGGER.error("Failed to load JEI's bundled GUI metadata for {}", spriteId, var7);
         return GuiMetadataSection.DEFAULT;
      }
   }
}
