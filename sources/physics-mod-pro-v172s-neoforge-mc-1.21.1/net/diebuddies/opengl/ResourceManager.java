package net.diebuddies.opengl;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import net.diebuddies.render.shader.ShaderResourceProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.Nullable;

public class ResourceManager {
   private static final int THREAD_POOL_SIZE = 1;
   private Object2ObjectMap<ResourceLocation, Texture> textures;
   private ExecutorService asynchronousLoadingExecutor;
   private List<Future<Runnable>> tasks = new ObjectArrayList();
   private Set<Texture> destructionQueue;

   public ResourceManager() {
      this.textures = new Object2ObjectOpenHashMap();
      this.asynchronousLoadingExecutor = Executors.newFixedThreadPool(1);
      this.destructionQueue = new ObjectOpenHashSet();
   }

   public void update() {
      for (int i = 0; i < this.tasks.size(); i++) {
         Future<Runnable> result = this.tasks.get(i);
         if (result.isDone()) {
            try {
               Runnable event = result.get();
               if (event != null) {
                  event.run();
               }
            } catch (InterruptedException var8) {
               var8.printStackTrace();
            } catch (ExecutionException var9) {
               var9.printStackTrace();
            } finally {
               this.tasks.remove(i--);
            }
         }
      }

      Iterator<Texture> it = this.destructionQueue.iterator();

      while (it.hasNext()) {
         Texture texture = it.next();
         if (texture.getID() != -1) {
            texture.destroy();
            it.remove();
         }
      }
   }

   public boolean isLoading() {
      return !this.tasks.isEmpty();
   }

   private void doAsynchronous(Callable<Runnable> runnable) {
      this.tasks.add(this.asynchronousLoadingExecutor.submit(runnable));
   }

   public Texture loadTexture(final ResourceLocation path, boolean immediate, final TextureFilter filter) {
      if (!immediate) {
         final Texture texture = new Texture(-1);
         this.textures.put(path, texture);
         this.doAsynchronous(new Callable<Runnable>() {
            public Runnable call() throws Exception {
               try {
                  TextureData[] tmpData = null;

                  try (InputStream stream = ResourceManager.this.processResourceAsStream(path)) {
                     tmpData = new TextureData[]{Texture.loadTextureData(stream)};
                  }

                  final TextureData[] data = tmpData;
                  return new Runnable() {
                     @Override
                     public void run() {
                        texture.set(Texture.loadTexture(data[0], filter));

                        for (int i = 0; i < data.length; i++) {
                           data[i].destroy();
                        }
                     }
                  };
               } catch (IOException var7) {
                  var7.printStackTrace();
                  return null;
               }
            }
         });
         return texture;
      } else {
         try {
            TextureData[] data = null;

            try (InputStream stream = this.processResourceAsStream(path)) {
               data = new TextureData[]{Texture.loadTextureData(stream)};
            }

            Texture texture = new Texture(0);
            texture.set(Texture.loadTexture(data[0], filter));

            for (int i = 0; i < data.length; i++) {
               data[i].destroy();
            }

            this.textures.put(path, texture);
            return texture;
         } catch (IOException var10) {
            var10.printStackTrace();
            return null;
         }
      }
   }

   public Texture getTexture(ResourceLocation path, boolean immediate, TextureFilter filter) {
      Texture texture = (Texture)this.textures.get(path);
      return texture == null ? this.loadTexture(path, immediate, filter) : texture;
   }

   public Texture getTexture(ResourceLocation path, boolean immediate) {
      return this.getTexture(path, immediate, null);
   }

   public Texture getTexture(ResourceLocation path) {
      return this.getTexture(path, false);
   }

   public void destroy() {
      this.asynchronousLoadingExecutor.shutdown();
      this.update();
      ObjectIterator var1 = this.textures.values().iterator();

      while (var1.hasNext()) {
         Texture texture = (Texture)var1.next();
         texture.destroy();
      }

      this.textures.clear();
   }

   public void destroyTexture(ResourceLocation imageLocation) {
      Texture texture = (Texture)this.textures.remove(imageLocation);
      if (texture != null) {
         if (texture.getID() != -1) {
            texture.destroy();
         } else {
            this.destructionQueue.add(texture);
         }
      }
   }

   private boolean isResourceUrlValid(String string, @Nullable URL url) throws IOException {
      return url != null && (url.getProtocol().equals("jar") || this.validatePath(new File(url.getFile()), string));
   }

   private boolean validatePath(File file, String string) throws IOException {
      String canonicalPath = file.getCanonicalPath();
      return canonicalPath.endsWith(string);
   }

   private InputStream processResourceAsStream(ResourceLocation resourceLocation) {
      String path = this.createPath(resourceLocation);

      try {
         URL url = ShaderResourceProvider.class.getResource(path);
         return this.isResourceUrlValid(path, url) ? url.openStream() : ShaderResourceProvider.class.getResourceAsStream(path);
      } catch (IOException var4) {
         return ShaderResourceProvider.class.getResourceAsStream(path);
      }
   }

   private String createPath(ResourceLocation resourceLocation) {
      return "/" + PackType.CLIENT_RESOURCES.getDirectory() + "/physicsmod/" + resourceLocation.getPath();
   }

   public int getLoadedTexturesSize() {
      return this.textures.size();
   }
}
