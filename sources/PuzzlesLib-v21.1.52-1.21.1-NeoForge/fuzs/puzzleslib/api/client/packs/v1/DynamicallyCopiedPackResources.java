package fuzs.puzzleslib.api.client.packs.v1;

import com.mojang.blaze3d.platform.NativeImage;
import fuzs.puzzleslib.api.resources.v1.AbstractModPackResources;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class DynamicallyCopiedPackResources extends AbstractModPackResources {
   private final ResourceManager resourceManager;
   private final VanillaPackResources vanillaPackResources;
   private final Map<ResourceLocation, DynamicallyCopiedPackResources.TextureCopy> textures;

   protected DynamicallyCopiedPackResources(DynamicallyCopiedPackResources.TextureCopy... textures) {
      Minecraft minecraft = Minecraft.getInstance();
      this.resourceManager = minecraft.getResourceManager();
      this.vanillaPackResources = minecraft.getVanillaPackResources();
      this.textures = Stream.of(textures).collect(Collectors.toMap(DynamicallyCopiedPackResources.TextureCopy::destinationLocation, Function.identity()));
   }

   @Nullable
   @Override
   public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation resourceLocation) {
      if (this.textures.containsKey(resourceLocation)) {
         DynamicallyCopiedPackResources.TextureCopy textureCopy = this.textures.get(resourceLocation);
         Optional<Resource> vanillaResource = this.resourceManager.getResource(textureCopy.vanillaLocation());
         if (vanillaResource.isPresent()) {
            try {
               NativeImage nativeImage = NativeImage.read(vanillaResource.get().open());

               IoSupplier var6;
               label56: {
                  try {
                     if (nativeImage.getWidth() / nativeImage.getHeight() != textureCopy.vanillaImageWidth() / textureCopy.vanillaImageHeight()) {
                        var6 = this.vanillaPackResources.getResource(packType, textureCopy.vanillaLocation());
                        break label56;
                     }
                  } catch (Throwable var9) {
                     if (nativeImage != null) {
                        try {
                           nativeImage.close();
                        } catch (Throwable var8) {
                           var9.addSuppressed(var8);
                        }
                     }

                     throw var9;
                  }

                  if (nativeImage != null) {
                     nativeImage.close();
                  }

                  return vanillaResource.get()::open;
               }

               if (nativeImage != null) {
                  nativeImage.close();
               }

               return var6;
            } catch (IOException var10) {
               return vanillaResource.get()::open;
            }
         }
      }

      return null;
   }

   @Override
   public Set<String> getNamespaces(PackType packType) {
      return this.textures.keySet().stream().<String>map(ResourceLocation::getNamespace).collect(Collectors.toSet());
   }

   public static Supplier<AbstractModPackResources> create(DynamicallyCopiedPackResources.TextureCopy... textures) {
      return () -> new DynamicallyCopiedPackResources(textures);
   }

   public record TextureCopy(ResourceLocation vanillaLocation, ResourceLocation destinationLocation, int vanillaImageWidth, int vanillaImageHeight) {
      public TextureCopy(ResourceLocation vanillaLocation, ResourceLocation destinationLocation, int vanillaImageWidth, int vanillaImageHeight) {
         if (vanillaLocation.getNamespace().equals(destinationLocation.getNamespace())) {
            throw new IllegalStateException("%s and %s share same namespace".formatted(vanillaLocation, destinationLocation));
         } else if (!vanillaLocation.getPath().endsWith(".png")) {
            throw new IllegalArgumentException("%s is no texture location".formatted(vanillaLocation));
         } else if (!destinationLocation.getPath().endsWith(".png")) {
            throw new IllegalArgumentException("%s is no texture location".formatted(destinationLocation));
         } else {
            this.vanillaLocation = vanillaLocation;
            this.destinationLocation = destinationLocation;
            this.vanillaImageWidth = vanillaImageWidth;
            this.vanillaImageHeight = vanillaImageHeight;
         }
      }
   }
}
