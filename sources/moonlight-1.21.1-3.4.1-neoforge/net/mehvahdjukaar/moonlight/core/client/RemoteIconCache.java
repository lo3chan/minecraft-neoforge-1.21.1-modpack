package net.mehvahdjukaar.moonlight.core.client;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.mehvahdjukaar.moonlight.api.client.gui.ModIcons;
import net.mehvahdjukaar.moonlight.api.resources.textures.SpriteUtils;
import net.mehvahdjukaar.moonlight.api.util.FileDownloadUtils;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public final class RemoteIconCache {
   private static final Map<String, Optional<ModIcons.Icon>> CACHE = new ConcurrentHashMap<>();

   @Nullable
   public static ModIcons.Icon get(String key, String url) {
      Optional<ModIcons.Icon> cached = CACHE.get(key);
      if (cached != null) {
         return cached.orElse(null);
      } else {
         if (CACHE.putIfAbsent(key, Optional.empty()) == null) {
            startLoad(key, url);
         }

         return null;
      }
   }

   private static void startLoad(String key, String url) {
      Thread t = new Thread(() -> {
         try {
            byte[] bytes = FileDownloadUtils.readBytes(url);
            NativeImage image = SpriteUtils.readImage(bytes);
            Minecraft mc = Minecraft.getInstance();
            mc.execute(() -> {
               try {
                  ResourceLocation id = Moonlight.res("remote_mod_icon/" + key.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_.-]", "_"));
                  mc.getTextureManager().register(id, new DynamicTexture(image));
                  CACHE.put(key, Optional.of(new ModIcons.Icon(id, image.getWidth(), image.getHeight())));
               } catch (Exception var4x) {
                  image.close();
                  Moonlight.LOGGER.warn("Failed to register remote icon for {}", key, var4x);
               }
            });
         } catch (Exception var5) {
            Moonlight.LOGGER.warn("Failed to fetch remote icon for {} from {}: {}", key, url, var5.toString());
         }
      }, "Moonlight Icon Fetcher " + key);
      t.setDaemon(true);
      t.start();
   }
}
