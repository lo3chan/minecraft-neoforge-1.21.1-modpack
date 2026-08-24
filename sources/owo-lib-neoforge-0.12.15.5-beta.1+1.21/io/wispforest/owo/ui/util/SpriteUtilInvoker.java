package io.wispforest.owo.ui.util;

import io.wispforest.owo.Owo;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.neoforged.fml.ModList;

public class SpriteUtilInvoker {
   private static final MethodHandle MARK_SPRITE_ACTIVE = getMarkSpriteActive();

   public static void markSpriteActive(TextureAtlasSprite sprite) {
      try {
         MARK_SPRITE_ACTIVE.invoke((TextureAtlasSprite)sprite);
      } catch (Throwable var2) {
         throw new RuntimeException(var2);
      }
   }

   private static MethodHandle getMarkSpriteActive() {
      if (ModList.get().isLoaded("sodium")) {
         try {
            Class<?> spriteUtil = Class.forName("me.jellysquid.mods.sodium.client.render.texture.SpriteUtil");
            Method m = spriteUtil.getMethod("markSpriteActive", TextureAtlasSprite.class);
            m.setAccessible(true);
            return MethodHandles.lookup().unreflect(m);
         } catch (Exception var2) {
            Owo.LOGGER.error("Couldn't get SpriteUtil.markSpriteActive from Sodium", var2);
         }
      }

      return MethodHandles.empty(MethodType.methodType(void.class, TextureAtlasSprite.class));
   }
}
