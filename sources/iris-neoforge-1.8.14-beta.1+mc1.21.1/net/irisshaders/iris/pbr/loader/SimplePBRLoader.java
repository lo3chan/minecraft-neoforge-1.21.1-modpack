package net.irisshaders.iris.pbr.loader;

import java.io.IOException;
import net.irisshaders.iris.mixin.texture.SimpleTextureAccessor;
import net.irisshaders.iris.pbr.texture.PBRType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

public class SimplePBRLoader implements PBRTextureLoader<SimpleTexture> {
   public void load(SimpleTexture texture, ResourceManager resourceManager, PBRTextureLoader.PBRTextureConsumer pbrTextureConsumer) {
      ResourceLocation location = ((SimpleTextureAccessor)texture).getLocation();
      AbstractTexture normalTexture = this.createPBRTexture(location, resourceManager, PBRType.NORMAL);
      AbstractTexture specularTexture = this.createPBRTexture(location, resourceManager, PBRType.SPECULAR);
      if (normalTexture != null) {
         pbrTextureConsumer.acceptNormalTexture(normalTexture);
      }

      if (specularTexture != null) {
         pbrTextureConsumer.acceptSpecularTexture(specularTexture);
      }
   }

   @Nullable
   protected AbstractTexture createPBRTexture(ResourceLocation imageLocation, ResourceManager resourceManager, PBRType pbrType) {
      ResourceLocation pbrImageLocation = imageLocation.withPath(pbrType::appendSuffix);
      SimpleTexture pbrTexture = new SimpleTexture(pbrImageLocation);

      try {
         pbrTexture.load(resourceManager);
         return pbrTexture;
      } catch (IOException var7) {
         return null;
      }
   }
}
