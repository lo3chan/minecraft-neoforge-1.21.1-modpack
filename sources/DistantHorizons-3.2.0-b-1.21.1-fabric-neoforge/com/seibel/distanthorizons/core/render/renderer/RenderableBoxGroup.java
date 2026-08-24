package com.seibel.distanthorizons.core.render.renderer;

import com.seibel.distanthorizons.api.interfaces.render.IDhApiRenderableBoxGroup;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiRenderParam;
import com.seibel.distanthorizons.api.objects.math.DhApiVec3d;
import com.seibel.distanthorizons.api.objects.render.DhApiRenderableBox;
import com.seibel.distanthorizons.api.objects.render.DhApiRenderableBoxGroupShading;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.render.RenderThreadTaskHandler;
import com.seibel.distanthorizons.core.util.threading.PriorityTaskPicker;
import com.seibel.distanthorizons.core.util.threading.ThreadPoolUtil;
import com.seibel.distanthorizons.core.wrapperInterfaces.IWrapperFactory;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.objects.IDhGenericObjectVertexBufferContainer;
import java.io.Closeable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

public class RenderableBoxGroup extends AbstractList<DhApiRenderableBox> implements IDhApiRenderableBoxGroup, Closeable {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final IMinecraftRenderWrapper MC_RENDER = SingletonInjector.INSTANCE.get(IMinecraftRenderWrapper.class);
   private static final IWrapperFactory WRAPPER_FACTORY = SingletonInjector.INSTANCE.get(IWrapperFactory.class);
   public static final AtomicInteger NEXT_ID_ATOMIC_INT = new AtomicInteger(0);
   public final long id;
   public final String resourceLocationNamespace;
   public final String resourceLocationPath;
   public final boolean positionBoxesRelativeToGroupOrigin;
   private final List<DhApiRenderableBox> boxList;
   private final List<DhApiRenderableBox> uploadBoxList;
   private final DhApiVec3d originBlockPos;
   public boolean active = true;
   public boolean ssaoEnabled = true;
   private boolean vertexDataDirty = false;
   public byte skyLight = 15;
   public byte blockLight = 0;
   public DhApiRenderableBoxGroupShading shading = DhApiRenderableBoxGroupShading.getDefaultShaded();
   @Nullable
   public Consumer<DhApiRenderParam> beforeRenderFunc;
   public Consumer<DhApiRenderParam> afterRenderFunc;
   public IDhGenericObjectVertexBufferContainer vertexBufferContainer = WRAPPER_FACTORY.createGenericObjectVboContainer();
   private IDhGenericObjectVertexBufferContainer altVertexBufferContainer = WRAPPER_FACTORY.createGenericObjectVboContainer();

   @Override
   public long getId() {
      return this.id;
   }

   @Override
   public String getResourceLocationNamespace() {
      return this.resourceLocationNamespace;
   }

   @Override
   public String getResourceLocationPath() {
      return this.resourceLocationPath;
   }

   @Override
   public void setOriginBlockPos(DhApiVec3d pos) {
      this.originBlockPos.x = pos.x;
      this.originBlockPos.y = pos.y;
      this.originBlockPos.z = pos.z;
   }

   @Override
   public DhApiVec3d getOriginBlockPos() {
      return new DhApiVec3d(this.originBlockPos.x, this.originBlockPos.y, this.originBlockPos.z);
   }

   @Override
   public void setSkyLight(int skyLight) {
      if (skyLight >= 0 && skyLight <= 15) {
         this.skyLight = (byte)skyLight;
      } else {
         throw new IllegalArgumentException("Sky light [" + skyLight + "] must be between [" + 0 + "] and [" + 15 + "] (inclusive).");
      }
   }

   @Override
   public int getSkyLight() {
      return this.skyLight;
   }

   @Override
   public void setBlockLight(int blockLight) {
      if (blockLight >= 0 && blockLight <= 15) {
         this.blockLight = (byte)blockLight;
      } else {
         throw new IllegalArgumentException("Block light [" + blockLight + "] must be between [" + 0 + "] and [" + 15 + "] (inclusive).");
      }
   }

   @Override
   public int getBlockLight() {
      return this.blockLight;
   }

   @Override
   public void setPreRenderFunc(Consumer<DhApiRenderParam> func) {
      this.beforeRenderFunc = func;
   }

   @Override
   public void setPostRenderFunc(Consumer<DhApiRenderParam> func) {
      this.afterRenderFunc = func;
   }

   @Override
   public void setActive(boolean active) {
      this.active = active;
   }

   @Override
   public boolean isActive() {
      return this.active;
   }

   @Override
   public void setSsaoEnabled(boolean ssaoEnabled) {
      this.ssaoEnabled = ssaoEnabled;
   }

   @Override
   public boolean isSsaoEnabled() {
      return this.ssaoEnabled;
   }

   @Override
   public void setShading(DhApiRenderableBoxGroupShading shading) {
      this.shading = shading;
   }

   @Override
   public DhApiRenderableBoxGroupShading getShading() {
      return this.shading;
   }

   public RenderableBoxGroup(String resourceLocation, DhApiVec3d originBlockPos, List<DhApiRenderableBox> boxList, boolean positionBoxesRelativeToGroupOrigin) throws IllegalArgumentException {
      String[] splitResourceLocation = resourceLocation.split(":");
      if (splitResourceLocation.length != 2) {
         throw new IllegalArgumentException(
            "Resource Location must be a string that's separated by a single colon, for example: [DistantHorizons:Beacons], your namespace ["
               + resourceLocation
               + "], contains ["
               + (splitResourceLocation.length - 1)
               + "] colons."
         );
      } else {
         this.resourceLocationNamespace = splitResourceLocation[0];
         this.resourceLocationPath = splitResourceLocation[1];
         this.id = NEXT_ID_ATOMIC_INT.getAndIncrement();
         this.boxList = Collections.synchronizedList(new ArrayList<>(boxList));
         this.uploadBoxList = Collections.synchronizedList(new ArrayList<>(boxList));
         this.originBlockPos = originBlockPos;
         this.positionBoxesRelativeToGroupOrigin = positionBoxesRelativeToGroupOrigin;
      }
   }

   @Override
   public void triggerBoxChange() {
      this.vertexDataDirty = true;
   }

   public void tryUpdateInstancedDataAsync() {
      if (this.altVertexBufferContainer.getState() == IDhGenericObjectVertexBufferContainer.EState.READY_TO_UPLOAD) {
         this.altVertexBufferContainer.uploadDataToGpu();
         this.altVertexBufferContainer.setState(IDhGenericObjectVertexBufferContainer.EState.RENDER);
         IDhGenericObjectVertexBufferContainer temp = this.vertexBufferContainer;
         this.vertexBufferContainer = this.altVertexBufferContainer;
         this.altVertexBufferContainer = temp;
      } else {
         PriorityTaskPicker.Executor executor = ThreadPoolUtil.getRenderLoadingExecutor();
         if (executor != null && !executor.isTerminated()) {
            if (this.vertexDataDirty) {
               if (this.altVertexBufferContainer.getState() != IDhGenericObjectVertexBufferContainer.EState.UPDATING_DATA) {
                  this.altVertexBufferContainer.setState(IDhGenericObjectVertexBufferContainer.EState.UPDATING_DATA);
                  this.uploadBoxList.clear();
                  synchronized (this.uploadBoxList) {
                     this.uploadBoxList.addAll(this.boxList);
                  }

                  try {
                     executor.runTask(() -> {
                        try {
                           this.altVertexBufferContainer.updateVertexData(this.uploadBoxList);
                           this.altVertexBufferContainer.setState(IDhGenericObjectVertexBufferContainer.EState.READY_TO_UPLOAD);
                           this.vertexDataDirty = false;
                        } catch (Exception var2) {
                           LOGGER.error("Unexpected error updating instanced VBO data for: [" + this + "], error: [" + var2.getMessage() + "].", var2);
                           this.altVertexBufferContainer.setState(IDhGenericObjectVertexBufferContainer.EState.ERROR);
                        }
                     });
                  } catch (RejectedExecutionException var4) {
                     this.altVertexBufferContainer.setState(IDhGenericObjectVertexBufferContainer.EState.NEW);
                  }
               }
            }
         }
      }
   }

   public void preRender(DhApiRenderParam renderEventParam) {
      if (this.beforeRenderFunc != null) {
         this.beforeRenderFunc.accept(renderEventParam);
      }
   }

   public void postRender(DhApiRenderParam renderEventParam) {
      if (this.afterRenderFunc != null) {
         this.afterRenderFunc.accept(renderEventParam);
      }
   }

   public boolean add(DhApiRenderableBox box) {
      return this.boxList.add(box);
   }

   public DhApiRenderableBox get(int index) {
      return this.boxList.get(index);
   }

   @Override
   public int size() {
      return this.boxList.size();
   }

   @Override
   public boolean removeIf(Predicate<? super DhApiRenderableBox> filter) {
      return this.boxList.removeIf(filter);
   }

   @Override
   public boolean remove(Object obj) {
      return this.boxList.remove(obj);
   }

   public DhApiRenderableBox remove(int index) {
      return this.boxList.remove(index);
   }

   @Override
   public void replaceAll(UnaryOperator<DhApiRenderableBox> operator) {
      this.boxList.replaceAll(operator);
   }

   @Override
   public void sort(Comparator<? super DhApiRenderableBox> comparator) {
      this.boxList.sort(comparator);
   }

   @Override
   public void forEach(Consumer<? super DhApiRenderableBox> action) {
      this.boxList.forEach(action);
   }

   @Override
   public Spliterator<DhApiRenderableBox> spliterator() {
      return this.boxList.spliterator();
   }

   @Override
   public Stream<DhApiRenderableBox> stream() {
      return this.boxList.stream();
   }

   @Override
   public Stream<DhApiRenderableBox> parallelStream() {
      return this.boxList.parallelStream();
   }

   @Override
   public void clear() {
      this.boxList.clear();
   }

   @Override
   public String toString() {
      return "["
         + this.resourceLocationNamespace
         + ":"
         + this.resourceLocationPath
         + "]  ID:["
         + this.id
         + "], pos:[("
         + this.originBlockPos.x
         + ", "
         + this.originBlockPos.y
         + ", "
         + this.originBlockPos.z
         + ")], size:["
         + this.size()
         + "], active:["
         + this.active
         + "]";
   }

   @Override
   public void close() {
      RenderThreadTaskHandler.INSTANCE.queueRunningOnRenderThread("RenderBoxGroup Close", () -> {
         this.vertexBufferContainer.close();
         this.altVertexBufferContainer.close();
      });
   }
}
