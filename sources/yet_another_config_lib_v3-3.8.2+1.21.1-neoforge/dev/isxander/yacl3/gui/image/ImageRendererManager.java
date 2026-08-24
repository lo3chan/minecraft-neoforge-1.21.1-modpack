package dev.isxander.yacl3.gui.image;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.isxander.yacl3.gui.image.impl.AnimatedDynamicTextureImage;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import dev.isxander.yacl3.platform.YACLConfig;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class ImageRendererManager {
   private static final ExecutorService SINGLE_THREAD_EXECUTOR = Executors.newSingleThreadExecutor(task -> new Thread(task, "YACL Image Prep"));
   private static final Map<ResourceLocation, CompletableFuture<ImageRenderer>> IMAGE_CACHE = new ConcurrentHashMap<>();
   static final Map<ResourceLocation, ImageRenderer> PRELOADED_IMAGE_CACHE = new ConcurrentHashMap<>();
   static final List<ImageRendererManager.PreloadedImageFactory> PRELOADED_IMAGE_FACTORIES = Stream.of(
         YACLConfig.HANDLER.instance().preloadComplexImageFormats
            ? new ImageRendererManager.PreloadedImageFactory(
               location -> location.getPath().endsWith(".webp"), AnimatedDynamicTextureImage::createWEBPFromTexture
            )
            : null,
         YACLConfig.HANDLER.instance().preloadComplexImageFormats
            ? new ImageRendererManager.PreloadedImageFactory(location -> location.getPath().endsWith(".gif"), AnimatedDynamicTextureImage::createGIFFromTexture)
            : null
      )
      .filter(Objects::nonNull)
      .toList();

   public static <T extends ImageRenderer> Optional<T> getImage(ResourceLocation id) {
      if (PRELOADED_IMAGE_CACHE.containsKey(id)) {
         return Optional.of((T)PRELOADED_IMAGE_CACHE.get(id));
      } else {
         return IMAGE_CACHE.containsKey(id) ? Optional.ofNullable((T)IMAGE_CACHE.get(id).getNow(null)) : Optional.empty();
      }
   }

   public static <T extends ImageRenderer> CompletableFuture<T> registerOrGetImage(ResourceLocation id, Supplier<ImageRendererFactory> factorySupplier) {
      if (PRELOADED_IMAGE_CACHE.containsKey(id)) {
         return CompletableFuture.completedFuture((T)PRELOADED_IMAGE_CACHE.get(id));
      } else if (IMAGE_CACHE.containsKey(id)) {
         return (CompletableFuture<T>)IMAGE_CACHE.get(id);
      } else {
         CompletableFuture<ImageRenderer> future = new CompletableFuture<>();
         IMAGE_CACHE.put(id, future);
         ImageRendererFactory factory = factorySupplier.get();
         SINGLE_THREAD_EXECUTOR.submit(
            () -> {
               Supplier<Optional<ImageRendererFactory.ImageSupplier>> supplier = (Supplier<Optional<ImageRendererFactory.ImageSupplier>>)(factory.requiresOffThreadPreparation()
                  ? new ImageRendererManager.CompletedSupplier<>(safelyPrepareFactory(id, factory))
                  : () -> safelyPrepareFactory(id, factory));
               Minecraft.getInstance().execute(() -> completeImageFactory(id, supplier, future));
            }
         );
         return (CompletableFuture<T>)future;
      }
   }

   @Deprecated
   public static <T extends ImageRenderer> CompletableFuture<T> registerImage(ResourceLocation id, ImageRendererFactory factory) {
      return registerOrGetImage(id, () -> factory);
   }

   private static <T extends ImageRenderer> void completeImageFactory(
      ResourceLocation id, Supplier<Optional<ImageRendererFactory.ImageSupplier>> supplier, CompletableFuture<ImageRenderer> future
   ) {
      RenderSystem.assertOnRenderThread();
      ImageRendererFactory.ImageSupplier completableImage = supplier.get().orElse(null);
      if (completableImage != null) {
         if (future.isDone()) {
            YACLConstants.LOGGER.error("Image '{}' was already completed", id);
         } else {
            ImageRenderer image;
            try {
               image = completableImage.completeImage();
            } catch (Exception var6) {
               YACLConstants.LOGGER.error("Failed to create image '{}'", id, var6);
               return;
            }

            future.complete(image);
         }
      }
   }

   public static void closeAll() {
      SINGLE_THREAD_EXECUTOR.shutdownNow();
      IMAGE_CACHE.values().removeIf(future -> {
         if (future.isDone()) {
            future.join().close();
         }

         return true;
      });
   }

   static Optional<ImageRendererFactory.ImageSupplier> safelyPrepareFactory(ResourceLocation id, ImageRendererFactory factory) {
      try {
         return Optional.of(factory.prepareImage());
      } catch (Exception var3) {
         YACLConstants.LOGGER.error("Failed to prepare image '{}'", id, var3);
         IMAGE_CACHE.remove(id);
         return Optional.empty();
      }
   }

   private record CompletedSupplier<T>(T get) implements Supplier<T> {
   }

   public record PreloadedImageFactory(Predicate<ResourceLocation> predicate, Function<ResourceLocation, ImageRendererFactory> factory) {
   }
}
