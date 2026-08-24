package dev.isxander.yacl3.impl;

import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.debug.DebugProperties;
import dev.isxander.yacl3.gui.image.ImageRenderer;
import dev.isxander.yacl3.gui.image.ImageRendererManager;
import dev.isxander.yacl3.gui.image.impl.AnimatedDynamicTextureImage;
import dev.isxander.yacl3.gui.image.impl.DynamicTextureImage;
import dev.isxander.yacl3.gui.image.impl.ResourceTextureImage;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.Validate;

public record OptionDescriptionImpl(Component text, CompletableFuture<Optional<ImageRenderer>> image) implements OptionDescription {
   public static class BuilderImpl implements OptionDescription.Builder {
      private final List<Component> descriptionLines = new ArrayList<>();
      private CompletableFuture<Optional<ImageRenderer>> image = CompletableFuture.completedFuture(Optional.empty());
      private boolean imageUnset = true;

      @Override
      public OptionDescription.Builder text(Component... description) {
         this.descriptionLines.addAll(Arrays.asList(description));
         return this;
      }

      @Override
      public OptionDescription.Builder text(Collection<? extends Component> lines) {
         this.descriptionLines.addAll(lines);
         return this;
      }

      @Override
      public OptionDescription.Builder image(ResourceLocation image, int width, int height) {
         Validate.isTrue(this.imageUnset, "Image already set!", new Object[0]);
         Validate.isTrue(width > 0, "Width must be greater than 0!", new Object[0]);
         Validate.isTrue(height > 0, "Height must be greater than 0!", new Object[0]);
         this.image = ImageRendererManager.registerOrGetImage(image, () -> ResourceTextureImage.createFactory(image, 0.0F, 0.0F, width, height, width, height))
            .thenApply(Optional::of);
         this.imageUnset = false;
         return this;
      }

      @Override
      public OptionDescription.Builder image(ResourceLocation image, float u, float v, int width, int height, int textureWidth, int textureHeight) {
         Validate.isTrue(this.imageUnset, "Image already set!", new Object[0]);
         Validate.isTrue(width > 0, "Width must be greater than 0!", new Object[0]);
         Validate.isTrue(height > 0, "Height must be greater than 0!", new Object[0]);
         this.image = ImageRendererManager.registerOrGetImage(
               image, () -> ResourceTextureImage.createFactory(image, u, v, width, height, textureWidth, textureHeight)
            )
            .thenApply(Optional::of);
         this.imageUnset = false;
         return this;
      }

      @Override
      public OptionDescription.Builder image(Path path, ResourceLocation uniqueLocation) {
         Validate.isTrue(this.imageUnset, "Image already set!", new Object[0]);
         this.image = ImageRendererManager.registerOrGetImage(
               uniqueLocation, () -> DynamicTextureImage.fromPath(path, uniqueLocation, DebugProperties.IMAGE_FILTERING)
            )
            .thenApply(Optional::of);
         this.imageUnset = false;
         return this;
      }

      @Override
      public OptionDescription.Builder gifImage(ResourceLocation image) {
         Validate.isTrue(this.imageUnset, "Image already set!", new Object[0]);
         this.image = ImageRendererManager.registerOrGetImage(image, () -> AnimatedDynamicTextureImage.createGIFFromTexture(image)).thenApply(Optional::of);
         this.imageUnset = false;
         return this;
      }

      @Override
      public OptionDescription.Builder gifImage(Path path, ResourceLocation uniqueLocation) {
         Validate.isTrue(this.imageUnset, "Image already set!", new Object[0]);
         this.image = ImageRendererManager.registerOrGetImage(uniqueLocation, () -> AnimatedDynamicTextureImage.createGIFFromPath(path, uniqueLocation))
            .thenApply(Optional::of);
         this.imageUnset = false;
         return this;
      }

      @Override
      public OptionDescription.Builder webpImage(ResourceLocation image) {
         Validate.isTrue(this.imageUnset, "Image already set!", new Object[0]);
         this.image = ImageRendererManager.registerOrGetImage(image, () -> AnimatedDynamicTextureImage.createWEBPFromTexture(image)).thenApply(Optional::of);
         this.imageUnset = false;
         return this;
      }

      @Override
      public OptionDescription.Builder webpImage(Path path, ResourceLocation uniqueLocation) {
         Validate.isTrue(this.imageUnset, "Image already set!", new Object[0]);
         this.image = ImageRendererManager.registerOrGetImage(uniqueLocation, () -> AnimatedDynamicTextureImage.createWEBPFromPath(path, uniqueLocation))
            .thenApply(Optional::of);
         this.imageUnset = false;
         return this;
      }

      @Override
      public OptionDescription.Builder customImage(CompletableFuture<Optional<ImageRenderer>> image) {
         Validate.notNull(image, "Image cannot be null!", new Object[0]);
         Validate.isTrue(this.imageUnset, "Image already set!", new Object[0]);
         this.image = image;
         this.imageUnset = false;
         return this;
      }

      @Override
      public OptionDescription build() {
         MutableComponent concatenatedDescription = Component.empty();
         Iterator<Component> iter = this.descriptionLines.iterator();

         while (iter.hasNext()) {
            concatenatedDescription.append(iter.next());
            if (iter.hasNext()) {
               concatenatedDescription.append("\n");
            }
         }

         return new OptionDescriptionImpl(concatenatedDescription, this.image);
      }
   }
}
