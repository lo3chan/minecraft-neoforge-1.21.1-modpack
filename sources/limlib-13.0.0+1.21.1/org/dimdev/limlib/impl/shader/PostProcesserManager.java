package org.dimdev.limlib.impl.shader;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public final class PostProcesserManager {
   public static final PostProcesserManager INSTANCE = new PostProcesserManager();
   public static final ResourceLocation RESOURCE_KEY = ResourceLocation.parse("limlib:shaders");
   private final Set<PostProcesser> shaders = new ReferenceOpenHashSet();

   public PostProcesser find(ResourceLocation location) {
      PostProcesser ret = new PostProcesser(location);
      this.shaders.add(ret);
      return ret;
   }

   public void onResolutionChanged(int newWidth, int newHeight) {
      if (!this.shaders.isEmpty()) {
         for (PostProcesser shader : this.shaders) {
            if (shader.isInitialized()) {
               Minecraft client = Minecraft.getInstance();
               shader.shader.resize(client.getWindow().getWidth(), client.getWindow().getHeight());
            }
         }
      }
   }

   public void onResourceManagerReload(ResourceManager mgr) {
      for (PostProcesser shader : this.shaders) {
         shader.init(mgr);
      }
   }

   public void dispose(PostProcesser shader) {
      shader.release();
      this.shaders.remove(shader);
   }
}
