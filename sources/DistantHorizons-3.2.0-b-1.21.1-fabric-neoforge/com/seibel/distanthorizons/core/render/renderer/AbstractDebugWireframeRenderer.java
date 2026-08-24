package com.seibel.distanthorizons.core.render.renderer;

import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.config.types.ConfigEntry;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.render.RenderParams;
import com.seibel.distanthorizons.core.util.math.DhMat4f;
import com.seibel.distanthorizons.core.util.math.DhVec3f;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IBindable;
import java.awt.Color;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.PriorityBlockingQueue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractDebugWireframeRenderer implements IBindable {
   protected static final DhLogger RATE_LIMITED_LOGGER = new DhLoggerBuilder().maxCountPerSecond(1).build();
   protected static final IMinecraftRenderWrapper MC_RENDER = SingletonInjector.INSTANCE.get(IMinecraftRenderWrapper.class);
   protected final AbstractDebugWireframeRenderer.RendererLists rendererLists = new AbstractDebugWireframeRenderer.RendererLists();
   protected final PriorityBlockingQueue<AbstractDebugWireframeRenderer.BoxParticle> particles = new PriorityBlockingQueue<>();
   protected DhMat4f dhMvmProjMatrixThisFrame;
   protected DhVec3f camPosFloatThisFrame;

   public void render(RenderParams renderParams) {
      this.dhMvmProjMatrixThisFrame = new DhMat4f(renderParams.dhMvmProjMatrix);
      this.camPosFloatThisFrame = new DhVec3f(renderParams.exactCameraPosition);
      this.rendererLists.render(this);
      AbstractDebugWireframeRenderer.BoxParticle head = null;

      while ((head = this.particles.poll()) != null && head.isDead()) {
      }

      if (head != null) {
         this.particles.add(head);
      }

      for (AbstractDebugWireframeRenderer.BoxParticle particle : this.particles) {
         this.renderBox(particle.createNewRenderBox());
      }
   }

   public abstract void renderBox(AbstractDebugWireframeRenderer.Box box);

   public void makeParticle(AbstractDebugWireframeRenderer.BoxParticle particle) {
      if (Config.Client.Advanced.Debugging.DebugWireframe.enableRendering.get()) {
         this.particles.add(particle);
      }
   }

   public void register(IDebugRenderable renderable, ConfigEntry<Boolean> config) {
      this.addRenderer(renderable, config);
   }

   public void addRenderer(IDebugRenderable renderable, ConfigEntry<Boolean> config) {
      this.rendererLists.addRenderable(renderable, config);
   }

   public void unregister(IDebugRenderable renderable, ConfigEntry<Boolean> config) {
      this.removeRenderer(renderable, config);
   }

   public void removeRenderer(IDebugRenderable renderable, ConfigEntry<Boolean> config) {
      this.rendererLists.removeRenderable(renderable, config);
   }

   public void clearRenderables() {
      this.rendererLists.clearRenderables();
   }

   public static final class Box {
      public DhVec3f minPos;
      public DhVec3f maxPos;
      public Color color;

      public Box(long pos, float minY, float maxY, float marginPercent, Color color) {
         float edgeOffset = DhSectionPos.getBlockWidth(pos) * marginPercent;
         int minBlockPosX = DhSectionPos.getMinCornerBlockX(pos);
         int minBlockPosZ = DhSectionPos.getMinCornerBlockZ(pos);
         int maxBlockPosX = minBlockPosX + DhSectionPos.getBlockWidth(pos);
         int maxBlockPosZ = minBlockPosZ + DhSectionPos.getBlockWidth(pos);
         this.minPos = new DhVec3f(minBlockPosX + edgeOffset, minY, minBlockPosZ + edgeOffset);
         this.maxPos = new DhVec3f(maxBlockPosX - edgeOffset, maxY, maxBlockPosZ - edgeOffset);
         this.color = color;
      }

      public Box(DhVec3f minPos, DhVec3f maxPos, Color color) {
         this.minPos = minPos;
         this.maxPos = maxPos;
         this.color = color;
      }
   }

   public static final class BoxParticle implements Comparable<AbstractDebugWireframeRenderer.BoxParticle> {
      public AbstractDebugWireframeRenderer.Box box;
      public long startMsTime;
      public long durationInMs;
      public float yChange;

      private BoxParticle(AbstractDebugWireframeRenderer.Box box, long startMsTime, long durationInMs, float yChange) {
         this.box = box;
         this.startMsTime = startMsTime;
         this.durationInMs = durationInMs;
         this.yChange = yChange;
      }

      public BoxParticle(AbstractDebugWireframeRenderer.Box box, double secondDuration, float yChange) {
         this(box, System.currentTimeMillis(), (long)(secondDuration * 1000.0), yChange);
      }

      public int compareTo(@NotNull AbstractDebugWireframeRenderer.BoxParticle particle) {
         return Long.compare(this.startMsTime + this.durationInMs, particle.startMsTime + particle.durationInMs);
      }

      public AbstractDebugWireframeRenderer.Box createNewRenderBox() {
         long nowMs = System.currentTimeMillis();
         float percent = (float)(nowMs - this.startMsTime) / (float)this.durationInMs;
         percent = (float)Math.pow(percent, 4.0);
         float yDiff = this.yChange * percent;
         return new AbstractDebugWireframeRenderer.Box(
            new DhVec3f(this.box.minPos.x, this.box.minPos.y + yDiff, this.box.minPos.z),
            new DhVec3f(this.box.maxPos.x, this.box.maxPos.y + yDiff, this.box.maxPos.z),
            this.box.color
         );
      }

      public boolean isDead() {
         return System.currentTimeMillis() - this.startMsTime > this.durationInMs;
      }
   }

   protected static class RendererLists {
      public final LinkedList<WeakReference<IDebugRenderable>> generalRenderableList = new LinkedList<>();
      private final HashMap<ConfigEntry<Boolean>, LinkedList<WeakReference<IDebugRenderable>>> renderableListByConfig = new HashMap<>();

      public void addRenderable(IDebugRenderable renderable, @Nullable ConfigEntry<Boolean> config) {
         synchronized (this) {
            if (config != null) {
               if (!this.renderableListByConfig.containsKey(config)) {
                  this.renderableListByConfig.put(config, new LinkedList<>());
               }

               LinkedList<WeakReference<IDebugRenderable>> renderableList = this.renderableListByConfig.get(config);
               renderableList.add(new WeakReference<>(renderable));
            } else {
               this.generalRenderableList.add(new WeakReference<>(renderable));
            }
         }
      }

      public void removeRenderable(IDebugRenderable renderable, @Nullable ConfigEntry<Boolean> config) {
         synchronized (this) {
            if (config != null) {
               if (this.renderableListByConfig.containsKey(config)) {
                  LinkedList<WeakReference<IDebugRenderable>> renderableList = this.renderableListByConfig.get(config);
                  this.removeRenderableFromInternalList(renderableList, renderable);
               }
            } else {
               this.removeRenderableFromInternalList(this.generalRenderableList, renderable);
            }
         }
      }

      private void removeRenderableFromInternalList(LinkedList<WeakReference<IDebugRenderable>> rendererList, IDebugRenderable renderable) {
         Iterator<WeakReference<IDebugRenderable>> iterator = rendererList.iterator();

         while (iterator.hasNext()) {
            WeakReference<IDebugRenderable> renderableRef = iterator.next();
            if (renderableRef.get() == null) {
               iterator.remove();
            } else if (renderableRef.get() == renderable) {
               iterator.remove();
               return;
            }
         }
      }

      public void clearRenderables() {
         for (ConfigEntry<Boolean> config : this.renderableListByConfig.keySet()) {
            LinkedList<WeakReference<IDebugRenderable>> renderableList = this.renderableListByConfig.get(config);
            if (config.get() && renderableList != null) {
               renderableList.clear();
            }
         }
      }

      public void render(AbstractDebugWireframeRenderer debugRenderer) {
         this.renderList(debugRenderer, this.generalRenderableList);

         for (ConfigEntry<Boolean> config : this.renderableListByConfig.keySet()) {
            LinkedList<WeakReference<IDebugRenderable>> renderableList = this.renderableListByConfig.get(config);
            if (config.get() && renderableList != null && renderableList.size() != 0) {
               this.renderList(debugRenderer, renderableList);
            }
         }
      }

      private void renderList(AbstractDebugWireframeRenderer debugRenderer, LinkedList<WeakReference<IDebugRenderable>> rendererList) {
         synchronized (this) {
            try {
               Iterator<WeakReference<IDebugRenderable>> iterator = rendererList.iterator();

               while (iterator.hasNext()) {
                  WeakReference<IDebugRenderable> ref = iterator.next();
                  IDebugRenderable renderable = ref.get();
                  if (renderable == null) {
                     iterator.remove();
                  } else {
                     renderable.debugRender(debugRenderer);
                  }
               }
            } catch (Exception var8) {
               AbstractDebugWireframeRenderer.RATE_LIMITED_LOGGER.error("Unexpected Debug renderer error, Error: " + var8.getMessage(), var8);
            }
         }
      }
   }
}
