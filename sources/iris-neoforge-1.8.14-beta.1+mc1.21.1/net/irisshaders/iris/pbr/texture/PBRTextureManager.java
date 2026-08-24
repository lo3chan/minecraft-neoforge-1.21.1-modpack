package net.irisshaders.iris.pbr.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.nio.file.Path;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gl.state.StateUpdateNotifiers;
import net.irisshaders.iris.mixin.GlStateManagerAccessor;
import net.irisshaders.iris.pbr.TextureTracker;
import net.irisshaders.iris.pbr.loader.PBRTextureLoader;
import net.irisshaders.iris.pbr.loader.PBRTextureLoaderRegistry;
import net.irisshaders.iris.targets.backed.NativeImageBackedSingleColorTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.Dumpable;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class PBRTextureManager {
   public static final PBRTextureManager INSTANCE = new PBRTextureManager();
   private static Runnable normalTextureChangeListener;
   private static Runnable specularTextureChangeListener;
   private final Int2ObjectMap<PBRTextureHolder> holders = new Int2ObjectOpenHashMap();
   private final PBRTextureManager.PBRTextureConsumerImpl consumer = new PBRTextureManager.PBRTextureConsumerImpl();
   private NativeImageBackedSingleColorTexture defaultNormalTexture;
   private NativeImageBackedSingleColorTexture defaultSpecularTexture;
   private final PBRTextureHolder defaultHolder = new PBRTextureHolder() {
      @NotNull
      @Override
      public AbstractTexture normalTexture() {
         return PBRTextureManager.this.defaultNormalTexture;
      }

      @NotNull
      @Override
      public AbstractTexture specularTexture() {
         return PBRTextureManager.this.defaultSpecularTexture;
      }
   };

   private PBRTextureManager() {
   }

   private static void dumpTexture(Dumpable dumpable, ResourceLocation id, Path path) {
      try {
         dumpable.dumpContents(id, path);
      } catch (IOException var4) {
         Iris.logger.error("Failed to dump texture {}", id, var4);
      }
   }

   private static void closeTexture(AbstractTexture texture) {
      try {
         texture.close();
      } catch (Exception var2) {
      }

      texture.releaseId();
   }

   public static void notifyPBRTexturesChanged() {
      if (normalTextureChangeListener != null) {
         normalTextureChangeListener.run();
      }

      if (specularTextureChangeListener != null) {
         specularTextureChangeListener.run();
      }
   }

   public void init() {
      this.defaultNormalTexture = new NativeImageBackedSingleColorTexture(PBRType.NORMAL.getDefaultValue());
      this.defaultSpecularTexture = new NativeImageBackedSingleColorTexture(PBRType.SPECULAR.getDefaultValue());
   }

   public PBRTextureHolder getHolder(int id) {
      PBRTextureHolder holder = (PBRTextureHolder)this.holders.get(id);
      return holder == null ? this.defaultHolder : holder;
   }

   public PBRTextureHolder getOrLoadHolder(int id) {
      PBRTextureHolder holder = (PBRTextureHolder)this.holders.get(id);
      if (holder == null) {
         holder = this.loadHolder(id);
         this.holders.put(id, holder);
      }

      return holder;
   }

   private PBRTextureHolder loadHolder(int id) {
      AbstractTexture texture = TextureTracker.INSTANCE.getTexture(id);
      if (texture != null) {
         Class<? extends AbstractTexture> clazz = (Class<? extends AbstractTexture>)texture.getClass();
         PBRTextureLoader loader = PBRTextureLoaderRegistry.INSTANCE.getLoader(clazz);
         if (loader != null) {
            int previousTextureBinding = GlStateManagerAccessor.getTEXTURES()[GlStateManagerAccessor.getActiveTexture()].binding;
            this.consumer.clear();

            PBRTextureHolder e;
            try {
               loader.load(texture, Minecraft.getInstance().getResourceManager(), this.consumer);
               e = this.consumer.toHolder();
            } catch (Exception var10) {
               Iris.logger.debug("Failed to load PBR textures for texture " + id, var10);
               return this.defaultHolder;
            } finally {
               GlStateManager._bindTexture(previousTextureBinding);
            }

            return e;
         }
      }

      return this.defaultHolder;
   }

   public void onDeleteTexture(int id) {
      PBRTextureHolder holder = (PBRTextureHolder)this.holders.remove(id);
      if (holder != null) {
         this.closeHolder(holder);
      }
   }

   public void dumpTextures(Path path) {
      ObjectIterator var2 = this.holders.values().iterator();

      while (var2.hasNext()) {
         PBRTextureHolder holder = (PBRTextureHolder)var2.next();
         if (holder != this.defaultHolder) {
            this.dumpHolder(holder, path);
         }
      }
   }

   private void dumpHolder(PBRTextureHolder holder, Path path) {
      AbstractTexture normalTexture = holder.normalTexture();
      AbstractTexture specularTexture = holder.specularTexture();
      if (normalTexture != this.defaultNormalTexture && normalTexture instanceof PBRDumpable dumpable) {
         dumpTexture(dumpable, dumpable.getDefaultDumpLocation(), path);
      }

      if (specularTexture != this.defaultSpecularTexture && specularTexture instanceof PBRDumpable dumpable) {
         dumpTexture(dumpable, dumpable.getDefaultDumpLocation(), path);
      }
   }

   public void clear() {
      ObjectIterator var1 = this.holders.values().iterator();

      while (var1.hasNext()) {
         PBRTextureHolder holder = (PBRTextureHolder)var1.next();
         if (holder != this.defaultHolder) {
            this.closeHolder(holder);
         }
      }

      this.holders.clear();
   }

   public void close() {
      this.clear();
      this.defaultNormalTexture.close();
      this.defaultSpecularTexture.close();
   }

   private void closeHolder(PBRTextureHolder holder) {
      AbstractTexture normalTexture = holder.normalTexture();
      AbstractTexture specularTexture = holder.specularTexture();
      if (normalTexture != this.defaultNormalTexture) {
         closeTexture(normalTexture);
      }

      if (specularTexture != this.defaultSpecularTexture) {
         closeTexture(specularTexture);
      }
   }

   static {
      StateUpdateNotifiers.normalTextureChangeNotifier = listener -> normalTextureChangeListener = listener;
      StateUpdateNotifiers.specularTextureChangeNotifier = listener -> specularTextureChangeListener = listener;
   }

   private class PBRTextureConsumerImpl implements PBRTextureLoader.PBRTextureConsumer {
      private AbstractTexture normalTexture;
      private AbstractTexture specularTexture;
      private boolean changed;

      @Override
      public void acceptNormalTexture(@NotNull AbstractTexture texture) {
         this.normalTexture = texture;
         this.changed = true;
      }

      @Override
      public void acceptSpecularTexture(@NotNull AbstractTexture texture) {
         this.specularTexture = texture;
         this.changed = true;
      }

      public void clear() {
         this.normalTexture = PBRTextureManager.this.defaultNormalTexture;
         this.specularTexture = PBRTextureManager.this.defaultSpecularTexture;
         this.changed = false;
      }

      public PBRTextureHolder toHolder() {
         return (PBRTextureHolder)(this.changed
            ? new PBRTextureManager.PBRTextureHolderImpl(this.normalTexture, this.specularTexture)
            : PBRTextureManager.this.defaultHolder);
      }
   }

   private record PBRTextureHolderImpl(@NotNull AbstractTexture normalTexture, @NotNull AbstractTexture specularTexture) implements PBRTextureHolder {
   }
}
