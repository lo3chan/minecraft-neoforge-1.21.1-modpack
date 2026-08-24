package dev.kosmx.playerAnim.minecraftApi;

import dev.kosmx.playerAnim.api.IPlayable;
import dev.kosmx.playerAnim.minecraftApi.codec.AnimationCodecs;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@OnlyIn(Dist.CLIENT)
public final class PlayerAnimationRegistry {
   private static final HashMap<ResourceLocation, IPlayable> animations = new HashMap<>();
   private static final Logger logger = LoggerFactory.getLogger(PlayerAnimationRegistry.class);

   @Nullable
   public static IPlayable getAnimation(@NotNull ResourceLocation identifier) {
      return animations.get(identifier);
   }

   @NotNull
   public static Optional<IPlayable> getAnimationOptional(@NotNull ResourceLocation identifier) {
      return Optional.ofNullable(getAnimation(identifier));
   }

   public static Map<ResourceLocation, IPlayable> getAnimations() {
      return Map.copyOf(animations);
   }

   @NotNull
   public static Map<String, IPlayable> getModAnimations(@NotNull String modid) {
      HashMap<String, IPlayable> map = new HashMap<>();

      for (Entry<ResourceLocation, IPlayable> entry : animations.entrySet()) {
         if (entry.getKey().getNamespace().equals(modid)) {
            map.put(entry.getKey().getPath(), entry.getValue());
         }
      }

      return map;
   }

   @Internal
   public static void resourceLoaderCallback(@NotNull ResourceManager manager) {
      animations.clear();

      for (Entry<ResourceLocation, Resource> resource : manager.listResources("player_animations", ignore -> true).entrySet()) {
         String extension = AnimationCodecs.getExtension(resource.getKey().getPath());
         if (extension != null) {
            for (IPlayable animation : AnimationCodecs.deserialize(extension, () -> {
               try {
                  return resource.getValue().open();
               } catch (IOException var2x) {
                  throw new RuntimeException(var2x);
               }
            })) {
               try {
                  animations.put(ResourceLocation.fromNamespaceAndPath(resource.getKey().getNamespace(), serializeTextToString(animation.getName())), animation);
               } catch (Throwable var8) {
                  logger.debug(
                     "Failed to load animation with name space {} and name {}. Either a PAL animation or has an invalid name.",
                     resource.getKey().getNamespace(),
                     animation.getName()
                  );
               }
            }
         }
      }

      for (Entry<ResourceLocation, Resource> resourcex : manager.listResources("player_animation", ignore -> true).entrySet()) {
         String extension = AnimationCodecs.getExtension(resourcex.getKey().getPath());
         if (extension != null) {
            logger.warn(
               "[WARNING FOR MOD DEVS] Animation {} is in wrong directory: \"player_animation\", please place it in \"player_animations\".",
               resourcex.getKey().getPath()
            );

            for (IPlayable animation : AnimationCodecs.deserialize(extension, () -> {
               try {
                  return resource.getValue().open();
               } catch (IOException var2x) {
                  throw new RuntimeException(var2x);
               }
            })) {
               animations.put(ResourceLocation.fromNamespaceAndPath(resourcex.getKey().getNamespace(), serializeTextToString(animation.getName())), animation);
            }
         }
      }
   }

   public static String serializeTextToString(String arg) {
      try {
         MutableComponent component = Serializer.fromJson(arg, RegistryAccess.EMPTY);
         if (component != null) {
            return component.getString();
         }
      } catch (Exception var2) {
      }

      return arg.replace("\"", "");
   }
}
