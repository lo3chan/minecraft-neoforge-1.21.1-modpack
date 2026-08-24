package dev.isxander.yacl3.api;

import dev.isxander.yacl3.gui.image.ImageRenderer;
import dev.isxander.yacl3.impl.OptionDescriptionImpl;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public interface OptionDescription {
   OptionDescription EMPTY = new OptionDescriptionImpl(CommonComponents.EMPTY, CompletableFuture.completedFuture(Optional.empty()));

   Component text();

   CompletableFuture<Optional<ImageRenderer>> image();

   static OptionDescription.Builder createBuilder() {
      return new OptionDescriptionImpl.BuilderImpl();
   }

   static OptionDescription of(Component... description) {
      return createBuilder().text(description).build();
   }

   public interface Builder {
      OptionDescription.Builder text(Component... var1);

      OptionDescription.Builder text(Collection<? extends Component> var1);

      OptionDescription.Builder image(ResourceLocation var1, int var2, int var3);

      OptionDescription.Builder image(ResourceLocation var1, float var2, float var3, int var4, int var5, int var6, int var7);

      OptionDescription.Builder image(Path var1, ResourceLocation var2);

      OptionDescription.Builder webpImage(ResourceLocation var1);

      OptionDescription.Builder webpImage(Path var1, ResourceLocation var2);

      OptionDescription.Builder customImage(CompletableFuture<Optional<ImageRenderer>> var1);

      default OptionDescription.Builder customImage(ImageRenderer image) {
         return this.customImage(CompletableFuture.completedFuture(Optional.of(image)));
      }

      @Deprecated
      OptionDescription.Builder gifImage(ResourceLocation var1);

      @Deprecated
      OptionDescription.Builder gifImage(Path var1, ResourceLocation var2);

      OptionDescription build();
   }
}
