package com.seibel.distanthorizons.common.render.openGl.glObject.buffer;

import com.seibel.distanthorizons.api.enums.config.EDhApiGpuUploadMethod;
import com.seibel.distanthorizons.common.render.openGl.glObject.GLProxy;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftGLWrapper;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.jar.EPlatform;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.logging.f3.F3Screen;
import com.seibel.distanthorizons.core.render.RenderThreadTaskHandler;
import com.seibel.distanthorizons.core.util.LodUtil;
import com.seibel.distanthorizons.core.util.ThreadUtil;
import com.seibel.distanthorizons.core.util.objects.Pair;
import com.seibel.distanthorizons.coreapi.ModInfo;
import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL44;

public class GLBuffer implements AutoCloseable {
   private static final DhLogger LOGGER = new DhLoggerBuilder()
      .fileLevelConfig(Config.Common.Logging.logRendererGLEventToFile)
      .chatLevelConfig(Config.Common.Logging.logRendererGLEventToChat)
      .build();
   private static final MinecraftGLWrapper GLMC = MinecraftGLWrapper.INSTANCE;
   private static final boolean LOG_PHANTOM_RECOVERY = ModInfo.IS_DEV_BUILD;
   private static final boolean LOG_PHANTOM_ALLOCATION_STACKS = false;
   public static final double BUFFER_EXPANSION_MULTIPLIER = 1.3;
   public static final double BUFFER_SHRINK_TRIGGER = 1.6900000000000002;
   public static final int MAC_UPLOAD_CHUNK_BYTES = 262144;
   public static final int MAC_UPLOAD_CHUNK_THRESHOLD = 262144;
   public static AtomicInteger bufferCount = new AtomicInteger(0);
   private static final int PHANTOM_REF_CHECK_TIME_IN_MS = 5000;
   private static final ConcurrentHashMap<PhantomReference<? extends GLBuffer>, Integer> PHANTOM_TO_BUFFER_ID = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<Integer, PhantomReference<? extends GLBuffer>> BUFFER_ID_TO_PHANTOM = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<Integer, String> BUFFER_ID_TO_ALLOCATION_STRING = new ConcurrentHashMap<>();
   private static final ReferenceQueue<GLBuffer> PHANTOM_REFERENCE_QUEUE = new ReferenceQueue<>();
   private static final ThreadPoolExecutor CLEANUP_THREAD = ThreadUtil.makeSingleDaemonThreadPool("GLBuffer Cleanup");
   protected volatile int id = 0;
   protected int size = 0;
   protected boolean bufferStorage;
   protected boolean isMapped = false;
   public final StampedLock renderStampLock = new StampedLock();

   public final int getId() {
      return this.id;
   }

   public int getSize() {
      return this.size;
   }

   public GLBuffer(boolean isBufferStorage) {
      this.destroyOldAndCreate(isBufferStorage);
   }

   public int getBufferBindingTarget() {
      return 36662;
   }

   public void bind() {
      GL33.glBindBuffer(this.getBufferBindingTarget(), this.id);
   }

   public void unbind() {
      GL33.glBindBuffer(this.getBufferBindingTarget(), 0);
   }

   protected void destroyOldAndCreate(boolean asBufferStorage) {
      if (!GLProxy.runningOnRenderThread()) {
         LodUtil.assertNotReach("Thread [" + Thread.currentThread() + "] tried to create a GLBuffer outside the MC render thread.");
      }

      long writeStamp = this.renderStampLock.writeLock();

      try {
         int oldId = this.id;
         this.id = GLMC.glGenBuffers();
         if (oldId != 0) {
            tryRemoveBufferIdFromPhantom(oldId);
            destroyBufferIdNow(oldId, "destroyOldAndCreate");
         }

         this.bufferStorage = asBufferStorage;
         bufferCount.getAndIncrement();
         PhantomReference<GLBuffer> phantom = new PhantomReference<>(this, PHANTOM_REFERENCE_QUEUE);
         PHANTOM_TO_BUFFER_ID.put(phantom, this.id);
         BUFFER_ID_TO_PHANTOM.put(this.id, phantom);
         this.updateAllocationStackTrace();
      } finally {
         this.renderStampLock.unlock(writeStamp);
      }
   }

   protected void destroyAsync() {
      long writeStamp = this.renderStampLock.writeLock();

      try {
         if (this.id != 0) {
            int idToDelete = this.id;
            tryRemoveBufferIdFromPhantom(idToDelete);
            this.id = 0;
            this.size = 0;
            RenderThreadTaskHandler.INSTANCE.queueRunningOnRenderThread("GLBuffer destroyAsync", () -> destroyBufferIdNow(idToDelete, "destroyAsync"));
            return;
         }
      } finally {
         this.renderStampLock.unlock(writeStamp);
      }
   }

   private static void destroyBufferIdNow(int id, String cause) {
      if (id == 0) {
         LOGGER.warn("Attempted to destroy a buffer with ID 0, VRAM memory leaks may occur, cause: [" + cause + "].");
      } else {
         bufferCount.decrementAndGet();
         GLMC.glDeleteBuffers(id);
         if (Config.Client.Advanced.Debugging.logBufferGarbageCollection.get()) {
            LOGGER.info("destroyed buffer [" + id + "], remaining: [" + BUFFER_ID_TO_PHANTOM.size() + "], cause: [" + cause + "].");
         }
      }
   }

   private static void tryRemoveBufferIdFromPhantom(int id) {
      BUFFER_ID_TO_ALLOCATION_STRING.remove(id);
      PhantomReference<? extends GLBuffer> phantom = BUFFER_ID_TO_PHANTOM.remove(id);
      if (phantom != null) {
         phantom.clear();
         Integer phantomId = PHANTOM_TO_BUFFER_ID.remove(phantom);
         if (phantomId == null) {
            LOGGER.warn("No Phantom->ID binding stored for ID [" + id + "]");
         }
      } else {
         LOGGER.warn("Unable to remove phantom GLBuffer with ID [" + id + "], buffer may have already been deleted.");
      }
   }

   public void uploadBuffer(ByteBuffer bb, EDhApiGpuUploadMethod uploadMethod, int maxExpansionSize, int bufferHint) {
      LodUtil.assertTrue(!uploadMethod.useEarlyMapping, "UploadMethod signal that this should use Mapping instead of uploadBuffer!");
      int bbSize = bb.limit() - bb.position();
      if (bbSize > maxExpansionSize) {
         LodUtil.assertNotReach("maxExpansionSize is [" + maxExpansionSize + "] but buffer size is [" + bbSize + "]!");
      }

      if (bbSize != 0) {
         int vao = GL33.glGetInteger(34229);
         int vbo = GL33.glGetInteger(34964);
         int ebo = GL33.glGetInteger(34965);

         try {
            this.createOrChangeBufferTypeForUpload(uploadMethod);
            switch (uploadMethod) {
               case AUTO:
                  LodUtil.assertNotReach("GpuUploadMethod AUTO must be resolved before call to uploadBuffer()!");
               case BUFFER_STORAGE:
                  this.uploadBufferStorage(bb);
                  break;
               case DATA:
                  this.uploadBufferData(bb, bufferHint);
                  break;
               case SUB_DATA:
                  this.uploadSubData(bb, maxExpansionSize, bufferHint);
                  break;
               default:
                  LodUtil.assertNotReach("Unknown GpuUploadMethod!");
            }
         } finally {
            GL33.glBindVertexArray(GL33.glIsVertexArray(vao) ? vao : 0);
            GL33.glBindBuffer(34962, GL33.glIsBuffer(vbo) ? vbo : 0);
            GL33.glBindBuffer(34963, GL33.glIsBuffer(ebo) ? ebo : 0);
         }
      }
   }

   protected void uploadBufferStorage(ByteBuffer bb) {
      LodUtil.assertTrue(this.bufferStorage, "Buffer is not bufferStorage but its trying to use bufferStorage upload method!");
      int bbSize = bb.limit() - bb.position();
      this.destroyOldAndCreate(true);
      this.bind();
      GL44.glBufferStorage(this.getBufferBindingTarget(), bb, 0);
      this.size = bbSize;
   }

   protected void uploadBufferData(ByteBuffer bb, int bufferDataHint) {
      LodUtil.assertTrue(!this.bufferStorage, "Buffer is bufferStorage but its trying to use bufferData upload method!");
      int bbSize = bb.limit() - bb.position();
      int target = this.getBufferBindingTarget();
      if (shouldUploadToGpuInChunks(bbSize)) {
         GL33.glBufferData(target, bbSize, bufferDataHint);
         subDataUploadInChunks(target, 0, bb, 262144);
      } else {
         GL33.glBufferData(target, bb, bufferDataHint);
      }

      this.size = bbSize;
      this.updateAllocationStackTrace();
   }

   protected void uploadSubData(ByteBuffer bb, int maxExpansionSize, int bufferDataHint) {
      LodUtil.assertTrue(!this.bufferStorage, "Buffer is bufferStorage but its trying to use subData upload method!");
      int bbSize = bb.limit() - bb.position();
      int target = this.getBufferBindingTarget();
      if (this.size < bbSize || this.size > bbSize * 1.6900000000000002) {
         int newSize = (int)(bbSize * 1.3);
         if (newSize > maxExpansionSize) {
            newSize = maxExpansionSize;
         }

         GL33.glBufferData(target, newSize, bufferDataHint);
         this.size = newSize;
      }

      if (shouldUploadToGpuInChunks(bbSize)) {
         subDataUploadInChunks(target, 0, bb, 262144);
      } else {
         GL33.glBufferSubData(target, 0L, bb);
      }

      this.updateAllocationStackTrace();
   }

   @Override
   public void close() {
      this.destroyAsync();
   }

   @Override
   public String toString() {
      return (this.bufferStorage ? "" : "Static-")
         + this.getClass().getSimpleName()
         + "[id:"
         + this.id
         + ",size:"
         + this.size
         + (this.isMapped ? ",MAPPED" : "")
         + "]";
   }

   private void createOrChangeBufferTypeForUpload(EDhApiGpuUploadMethod uploadMethod) {
      if (uploadMethod.useBufferStorage != this.bufferStorage) {
         this.bind();
         this.destroyOldAndCreate(uploadMethod.useBufferStorage);
         this.bind();
      } else {
         if (this.id == 0) {
            this.destroyOldAndCreate(this.bufferStorage);
         }

         this.bind();
      }
   }

   private static boolean shouldUploadToGpuInChunks(int byteCount) {
      return EPlatform.get() == EPlatform.MACOS && byteCount > 262144;
   }

   private static void subDataUploadInChunks(int target, int baseOffset, ByteBuffer bb, int chunkBytes) {
      int origPos = bb.position();
      int origLimit = bb.limit();

      try {
         int total = origLimit - origPos;
         int uploaded = 0;

         while (uploaded < total) {
            int chunk = Math.min(chunkBytes, total - uploaded);
            bb.position(origPos + uploaded);
            bb.limit(origPos + uploaded + chunk);
            GL33.glBufferSubData(target, baseOffset + uploaded, bb);
            uploaded += chunk;
            if (uploaded < total) {
               GL33.glFlush();
            }
         }
      } finally {
         bb.limit(origLimit);
         bb.position(origPos);
      }
   }

   public void updateAllocationStackTrace() {
   }

   private static void runPhantomReferenceCleanupLoop() {
      ArrayList<Pair<String, AtomicInteger>> allocationStackTraceCountPairList = new ArrayList<>();

      while (true) {
         allocationStackTraceCountPairList.clear();

         try {
            try {
               Thread.sleep(5000L);
            } catch (InterruptedException var5) {
            }

            int collectedCount = 0;

            for (Reference<? extends GLBuffer> phantomRef = PHANTOM_REFERENCE_QUEUE.poll(); phantomRef != null; phantomRef = PHANTOM_REFERENCE_QUEUE.poll()) {
               Integer idRef = PHANTOM_TO_BUFFER_ID.remove(phantomRef);
               if (idRef != null) {
                  BUFFER_ID_TO_PHANTOM.remove(idRef);
                  int id = idRef;
                  RenderThreadTaskHandler.INSTANCE
                     .queueRunningOnRenderThread("GLBuffer phantom destroy", () -> destroyBufferIdNow(id, "runPhantomReferenceCleanupLoop"));
               } else {
                  LOGGER.warn("Failed to find Buffer ID for phantom reference: [" + phantomRef + "]");
               }

               collectedCount++;
            }

            if (LOG_PHANTOM_RECOVERY && collectedCount != 0) {
               LOGGER.warn("GLBuffer phantom recovered: [" + F3Screen.NUMBER_FORMAT.format((long)collectedCount) + "].");
            }
         } catch (Exception var6) {
            LOGGER.error("Unexpected error in buffer cleanup thread: [" + var6.getMessage() + "].", var6);
         }
      }
   }

   static {
      CLEANUP_THREAD.execute(() -> runPhantomReferenceCleanupLoop());
   }
}
