package com.seibel.distanthorizons.core.render.QuadTree;

import com.seibel.distanthorizons.api.enums.config.EDhApiMaxHorizontalResolution;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.config.listeners.IConfigListener;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.enums.EDhDirection;
import com.seibel.distanthorizons.core.file.fullDatafile.V2.FullDataSourceProviderV2;
import com.seibel.distanthorizons.core.file.fullDatafile.V2.FullDataUpdatePropagatorV2;
import com.seibel.distanthorizons.core.generation.tasks.DataSourceRetrievalResult;
import com.seibel.distanthorizons.core.generation.tasks.ERetrievalResultState;
import com.seibel.distanthorizons.core.level.DhClientServerLevel;
import com.seibel.distanthorizons.core.level.IDhClientLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos2D;
import com.seibel.distanthorizons.core.render.CameraZoom;
import com.seibel.distanthorizons.core.render.RenderThreadTaskHandler;
import com.seibel.distanthorizons.core.render.renderer.AbstractDebugWireframeRenderer;
import com.seibel.distanthorizons.core.render.renderer.BeaconRenderHandler;
import com.seibel.distanthorizons.core.render.renderer.IDebugRenderable;
import com.seibel.distanthorizons.core.sql.dto.BeaconBeamDTO;
import com.seibel.distanthorizons.core.sql.repo.BeaconBeamRepo;
import com.seibel.distanthorizons.core.util.LodUtil;
import com.seibel.distanthorizons.core.util.RenderUtil;
import com.seibel.distanthorizons.core.util.ThreadUtil;
import com.seibel.distanthorizons.core.util.WorldGenUtil;
import com.seibel.distanthorizons.core.util.objects.quadTree.QuadNode;
import com.seibel.distanthorizons.core.util.objects.quadTree.QuadTree;
import com.seibel.distanthorizons.core.util.threading.PriorityTaskPicker;
import com.seibel.distanthorizons.core.util.threading.ThreadPoolUtil;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhGenericRenderer;
import com.seibel.distanthorizons.coreapi.util.MathUtil;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.WillNotClose;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LodQuadTree extends QuadTree<LodRenderSection> implements IDebugRenderable, IConfigListener, AutoCloseable {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final AbstractDebugWireframeRenderer DEBUG_RENDERER = SingletonInjector.INSTANCE.get(AbstractDebugWireframeRenderer.class);
   private static final ThreadPoolExecutor FULL_DATA_RETRIEVAL_QUEUE_THREAD = ThreadUtil.makeSingleDaemonThreadPool("LodQuadTree Data Retrieval Queue");
   public final int blockRenderDistanceDiameter;
   @WillNotClose
   private final FullDataSourceProviderV2 fullDataSourceProvider;
   private final ConcurrentLinkedQueue<Long> sectionsToReload = new ConcurrentLinkedQueue<>();
   private final IDhClientLevel level;
   private final ReentrantLock treeTickLock = new ReentrantLock();
   private final AtomicBoolean requeueAllRetrievalTasksRef = new AtomicBoolean(false);
   private final AtomicBoolean queueThreadRunningRef = new AtomicBoolean(false);
   private final ArrayList<BeaconRenderHandler.BeaconBeamWithWidth> beaconList = new ArrayList<>();
   @Nullable
   private final BeaconRenderHandler beaconRenderHandler;
   @Nullable
   private final BeaconBeamRepo beaconBeamRepo;
   @NotNull
   private CompletableFuture<Void> beaconUpdateFuture = CompletableFuture.completedFuture(null);
   private byte maxLeafRenderDetailLevel;
   private byte minRootRenderDetailLevel;
   private double detailDropOffDistanceUnit;
   private double detailDropOffLogBase;
   private final CameraZoom cameraZoom = CameraZoom.createNotZoomed();
   private final Set<Long> missingGenerationPosSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
   private final Set<Long> queuedGenerationPosSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
   private final ArrayList<Long> sortedMissingPosList = new ArrayList<>();
   private final ArrayList<LodRenderSection> debugNodeList = new ArrayList<>();
   private final QuadTreeTickNodeHolder tickNodeHolder = new QuadTreeTickNodeHolder();
   private ArrayList<LodRenderSection> enabledSections = new ArrayList<>();
   private ArrayList<LodRenderSection> altEnabledSections = new ArrayList<>();
   private final ReentrantLock enabledRenderSectionLock = new ReentrantLock();

   public LodQuadTree(
      IDhClientLevel level, int viewDiameterInBlocks, int initialPlayerBlockX, int initialPlayerBlockZ, FullDataSourceProviderV2 fullDataSourceProvider
   ) {
      super(viewDiameterInBlocks, 64, new DhBlockPos2D(initialPlayerBlockX, initialPlayerBlockZ), (byte)6);
      DEBUG_RENDERER.register(this, Config.Client.Advanced.Debugging.DebugWireframe.showQuadTreeRenderStatus);
      this.level = level;
      this.fullDataSourceProvider = fullDataSourceProvider;
      this.blockRenderDistanceDiameter = viewDiameterInBlocks;
      IDhGenericRenderer genericObjectRenderer = this.level.getGenericRenderer();
      this.beaconRenderHandler = genericObjectRenderer != null ? new BeaconRenderHandler(genericObjectRenderer) : null;
      this.beaconBeamRepo = this.level.getBeaconBeamRepo();
      Config.Common.WorldGenerator.enableDistantGeneration.addListener(this);
      Config.Server.enableServerGeneration.addListener(this);
   }

   public void populateListWithEnabledRenderSections(ArrayList<LodRenderSection> tempProcessNodeList) {
      try {
         this.enabledRenderSectionLock.lock();
         tempProcessNodeList.clear();

         for (int i = 0; i < this.enabledSections.size(); i++) {
            tempProcessNodeList.add(this.enabledSections.get(i));
         }
      } finally {
         this.enabledRenderSectionLock.unlock();
      }
   }

   public void tryTick(DhBlockPos2D playerPos) {
      if (this.level != null) {
         if (this.treeTickLock.tryLock()) {
            this.updateDetailLevelVariables();

            try {
               this.updateAllRenderSections(playerPos);
            } catch (Exception var6) {
               LOGGER.error(
                  "Quad Tree tick exception for level: [" + this.level.getLevelWrapper().getDhIdentifier() + "], error: [" + var6.getMessage() + "].", var6
               );
            } finally {
               this.treeTickLock.unlock();
            }
         }
      }
   }

   private void updateAllRenderSections(DhBlockPos2D playerPos) {
      this.tickNodeHolder.clear();
      this.setCenterBlockPos(playerPos, renderSection -> {
         if (renderSection != null) {
            this.fullDataSourceProvider.removeRetrievalRequestIf(genPos -> DhSectionPos.contains(renderSection.pos, genPos));
            this.missingGenerationPosSet.removeIf(genPos -> DhSectionPos.contains(renderSection.pos, genPos));
            this.queuedGenerationPosSet.removeIf(genPos -> DhSectionPos.contains(renderSection.pos, genPos));
            renderSection.close();
         }
      }, renderSection -> {
         if (renderSection != null) {
            renderSection.renderDataDirty = true;
         }
      });
      LongIterator rootPosIterator = this.rootNodePosIterator();

      while (rootPosIterator.hasNext()) {
         long rootPos = rootPosIterator.nextLong();
         if (this.getNode(rootPos) == null) {
            this.setValue(rootPos, new LodRenderSection(rootPos, this, this.level, this.fullDataSourceProvider));
         }

         QuadNode<LodRenderSection> rootNode = this.getNode(rootPos);
         LodUtil.assertTrue(rootNode != null, "All root nodes should have been created by this point.");
         this.recursivelyUpdateRenderSectionNode(playerPos, rootNode, null, rootNode, rootNode.sectionPos);
      }

      if (this.requeueAllRetrievalTasksRef.getAndSet(false)) {
         Iterator<QuadNode<LodRenderSection>> nodeIterator = this.nodeIterator();

         while (nodeIterator.hasNext()) {
            QuadNode<LodRenderSection> node = nodeIterator.next();
            if (node != null && node.value != null) {
               node.value.queuedMissingSectionsForRetrieval = false;
            }
         }
      }

      this.reloadQueuedSections();
      this.loadQueuedSections(playerPos, this.tickNodeHolder.getLoadSections());
      this.altEnabledSections.clear();

      for (QuadNode<LodRenderSection> node : this.tickNodeHolder.getEnabledNodes()) {
         if (node != null && node.value != null) {
            node.value.setRenderingEnabled(true);
            this.altEnabledSections.add(node.value);
         }
      }

      for (QuadNode<LodRenderSection> nodex : this.tickNodeHolder.getEnableDeleteChildrenNodes()) {
         if (nodex != null && nodex.value != null) {
            nodex.value.setRenderingEnabled(true);
            this.altEnabledSections.add(nodex.value);
         }
      }

      try {
         this.enabledRenderSectionLock.lock();
         ArrayList<LodRenderSection> temp = this.enabledSections;
         this.enabledSections = this.altEnabledSections;
         this.altEnabledSections = temp;
      } finally {
         this.enabledRenderSectionLock.unlock();
      }

      for (QuadNode<LodRenderSection> nodexx : this.tickNodeHolder.getDisableNodes()) {
         if (nodexx != null && nodexx.value != null) {
            nodexx.value.setRenderingEnabled(false);
         }
      }

      for (QuadNode<LodRenderSection> nodexxx : this.tickNodeHolder.getEnableDeleteChildrenNodes()) {
         if (nodexxx != null && nodexxx.value != null && nodexxx.getDirectChildCount() != 0) {
            RenderThreadTaskHandler.INSTANCE
               .queueRunningOnRenderThread("LodQuadTree delayed child cleanup", () -> node.deleteAllChildren(childRenderSection -> {
                  if (childRenderSection != null) {
                     childRenderSection.setRenderingEnabled(false);
                     childRenderSection.close();
                  }
               }));
         }
      }

      this.tryRefreshRenderingBeaconsAsync(playerPos);
      if (threadPoolCanAcceptWorldGenTasks() && this.fullDataSourceProvider.canQueueRetrievalNow() && !this.queueThreadRunningRef.get()) {
         this.queueThreadRunningRef.set(true);
         ArrayList<QuadNode<LodRenderSection>> worldGenNodes = this.tickNodeHolder.getWorldGenNodesNearToFar(playerPos);
         FULL_DATA_RETRIEVAL_QUEUE_THREAD.execute(() -> {
            try {
               for (int i = 0; i < worldGenNodes.size(); i++) {
                  QuadNode<LodRenderSection> nodexxxx = worldGenNodes.get(i);
                  if (nodexxxx != null && nodexxxx.value != null && !nodexxxx.value.queuedMissingSectionsForRetrieval) {
                     nodexxxx.value.queuedMissingSectionsForRetrieval = true;
                     this.tryQueuePosForRetrieval(nodexxxx.value.pos);
                  }
               }

               this.startQueuedRetrievalTasks(playerPos);
            } catch (Exception var8x) {
               LOGGER.error("Unexpected error starting queued retrieval tasks, error: [" + var8x.getMessage() + "].", var8x);
            } finally {
               this.queueThreadRunningRef.set(false);
            }
         });
      }
   }

   private boolean recursivelyUpdateRenderSectionNode(
      @NotNull DhBlockPos2D playerPos,
      @NotNull QuadNode<LodRenderSection> rootNode,
      @Nullable QuadNode<LodRenderSection> parentNode,
      @Nullable QuadNode<LodRenderSection> quadNode,
      long sectionPos
   ) {
      quadNode = this.tryAddNodeToTree(rootNode, quadNode, sectionPos);
      if (!this.isSectionPosInBounds(quadNode.sectionPos)) {
         this.tickNodeHolder.addDisableNode(quadNode);
         this.recursivelyDisableChildNodes(quadNode);
         return true;
      } else {
         LodRenderSection renderSection = quadNode.value;
         if (renderSection == null) {
            renderSection = new LodRenderSection(sectionPos, this, this.level, this.fullDataSourceProvider);
            quadNode.setValue(sectionPos, renderSection);
         }

         if (!renderSection.gpuUploadInProgress() && !renderSection.gpuUploadComplete()) {
            this.tickNodeHolder.addLoadSection(renderSection);
         }

         byte expectedDetailLevel = this.calcExpectedDetailLevel(playerPos, quadNode.sectionPos);
         expectedDetailLevel = (byte)Math.min(expectedDetailLevel, this.minRootRenderDetailLevel);
         expectedDetailLevel = (byte)(expectedDetailLevel + 6);
         if (DhSectionPos.getDetailLevel(quadNode.sectionPos) > expectedDetailLevel) {
            return this.onDetailLevelTooLow(playerPos, rootNode, quadNode);
         } else if (DhSectionPos.getDetailLevel(quadNode.sectionPos) == expectedDetailLevel
            || DhSectionPos.getDetailLevel(quadNode.sectionPos) == expectedDetailLevel - 1) {
            return this.onDesiredDetailLevel(quadNode, parentNode);
         } else if (this.cameraZoom.magnification > 1.0) {
            return this.onDesiredDetailLevel(quadNode, parentNode);
         } else {
            throw new IllegalStateException("LodQuadTree shouldn't be updating renderSections below the expected detail level: [" + expectedDetailLevel + "].");
         }
      }
   }

   private boolean onDetailLevelTooLow(
      @NotNull DhBlockPos2D playerPos, @NotNull QuadNode<LodRenderSection> rootNode, @NotNull QuadNode<LodRenderSection> quadNode
   ) {
      int childNodeRenderCount = 0;

      for (int i = 0; i < 4; i++) {
         long childPos = DhSectionPos.getChildByIndex(quadNode.sectionPos, i);
         QuadNode<LodRenderSection> childNode = quadNode.getChildByIndex(i);
         boolean childCanRender = this.recursivelyUpdateRenderSectionNode(playerPos, rootNode, quadNode, childNode, childPos);
         if (childCanRender) {
            childNodeRenderCount++;
         }
      }

      boolean isRootNode = quadNode == rootNode;
      if (isRootNode) {
         this.tickNodeHolder.addDisableNode(quadNode);
         return false;
      } else if (childNodeRenderCount >= 4) {
         this.tickNodeHolder.addDisableNode(quadNode);
         return true;
      } else {
         boolean nodeCanRender = quadNode.value != null && quadNode.value.canRender();
         if (nodeCanRender) {
            this.tickNodeHolder.addEnableNode(quadNode);
            this.recursivelyDisableChildNodes(quadNode);
         } else {
            this.tickNodeHolder.addDisableNode(quadNode);
         }

         return nodeCanRender;
      }
   }

   private boolean onDesiredDetailLevel(@NotNull QuadNode<LodRenderSection> quadNode, @Nullable QuadNode<LodRenderSection> parentNode) {
      if (!this.isSectionPosInBounds(quadNode.sectionPos)) {
         return true;
      } else if (quadNode.value != null && quadNode.value.canRender()) {
         if (!this.tickNodeHolder.getEnabledNodes().contains(parentNode)) {
            this.tickNodeHolder.addEnableDeleteChildrenNode(quadNode);
            return true;
         } else {
            this.tickNodeHolder.addDisableNode(quadNode);
            return false;
         }
      } else {
         this.tickNodeHolder.addDisableNode(quadNode);
         return false;
      }
   }

   @NotNull
   private QuadNode<LodRenderSection> tryAddNodeToTree(
      @NotNull QuadNode<LodRenderSection> rootNode, @Nullable QuadNode<LodRenderSection> quadNode, long sectionPos
   ) {
      if (quadNode == null) {
         rootNode.setValue(sectionPos, new LodRenderSection(sectionPos, this, this.level, this.fullDataSourceProvider));
         quadNode = rootNode.getNode(sectionPos);
      }

      if (quadNode == null) {
         LodUtil.assertNotReach("Unable to add node with pos [" + DhSectionPos.toString(sectionPos) + "] to tree root [" + rootNode + "].");
      }

      return quadNode;
   }

   private void recursivelyDisableChildNodes(@NotNull QuadNode<LodRenderSection> quadNode) {
      for (int i = 0; i < 4; i++) {
         QuadNode<LodRenderSection> childNode = quadNode.getChildByIndex(i);
         this.tickNodeHolder.removeEnableAndDisableNode(childNode);
         if (childNode != null) {
            this.recursivelyDisableChildNodes(childNode);
         }
      }
   }

   private void reloadQueuedSections() {
      HashSet<Long> positionsToRequeue = new HashSet<>();

      Long pos;
      while ((pos = this.sectionsToReload.poll()) != null) {
         if (!positionsToRequeue.contains(pos)) {
            LodRenderSection renderSection = this.tryGetValue(pos);
            if (renderSection != null
               && renderSection.gpuUploadComplete()
               && (renderSection.gpuUploadInProgress() || !renderSection.uploadRenderDataToGpuAsync())) {
               positionsToRequeue.add(pos);
            }
         }
      }

      this.sectionsToReload.addAll(positionsToRequeue);
   }

   private void loadQueuedSections(DhBlockPos2D playerPos, HashSet<LodRenderSection> nodesNeedingLoading) {
      ArrayList<LodRenderSection> loadSectionList = new ArrayList<>(nodesNeedingLoading);
      loadSectionList.sort((a, b) -> {
         byte aDetailLevel = DhSectionPos.getDetailLevel(a.pos);
         byte bDetailLevel = DhSectionPos.getDetailLevel(b.pos);
         if (aDetailLevel != bDetailLevel) {
            return Byte.compare(bDetailLevel, aDetailLevel);
         } else {
            int aDist = DhSectionPos.getManhattanBlockDistance(a.pos, playerPos);
            int bDist = DhSectionPos.getManhattanBlockDistance(b.pos, playerPos);
            return Integer.compare(aDist, bDist);
         }
      });

      for (int i = 0; i < loadSectionList.size(); i++) {
         LodRenderSection renderSection = loadSectionList.get(i);
         if (!renderSection.gpuUploadInProgress() && !renderSection.gpuUploadComplete()) {
            renderSection.uploadRenderDataToGpuAsync();
         }
      }
   }

   private void startQueuedRetrievalTasks(DhBlockPos2D playerPos) {
      this.sortedMissingPosList.clear();
      this.sortedMissingPosList.addAll(this.missingGenerationPosSet);
      this.sortedMissingPosList.sort((posA, posB) -> {
         int aDist = DhSectionPos.getManhattanBlockDistance(posA, playerPos);
         int bDist = DhSectionPos.getManhattanBlockDistance(posB, playerPos);
         return Integer.compare(aDist, bDist);
      });

      for (int i = 0; i < this.sortedMissingPosList.size() && this.fullDataSourceProvider.canQueueRetrievalNow(); i++) {
         long missingPos = this.sortedMissingPosList.get(i);
         boolean posInRange = WorldGenUtil.isPosInWorldGenRange(
            missingPos,
            Config.Common.WorldGenerator.generationCenterChunkX.get(),
            Config.Common.WorldGenerator.generationCenterChunkZ.get(),
            Config.Common.WorldGenerator.generationMaxChunkRadius.get()
         );
         if (posInRange) {
            CompletableFuture<DataSourceRetrievalResult> genFuture = this.fullDataSourceProvider.queuePositionForRetrieval(missingPos);
            boolean positionQueued = genFuture != null && !genFuture.isCompletedExceptionally();
            if (positionQueued) {
               this.queuedGenerationPosSet.add(missingPos);
               this.missingGenerationPosSet.remove(missingPos);
               genFuture.exceptionally(throwable -> {
                  this.queuedGenerationPosSet.remove(missingPos);
                  this.missingGenerationPosSet.add(missingPos);
                  return null;
               });
               genFuture.thenAccept(result -> {
                  this.queuedGenerationPosSet.remove(missingPos);
                  if (result.state == ERetrievalResultState.REQUIRES_SPLITTING) {
                     DhSectionPos.forEachChild(missingPos, childPos -> this.tryQueuePosForRetrieval(childPos));
                  }
               });
            }
         }
      }

      int totalWorldGenChunkCount = 0;
      int totalWorldGenTaskCount = 0;

      for (int ix = 0; ix < this.sortedMissingPosList.size(); ix++) {
         long missingPos = this.sortedMissingPosList.get(ix);
         int sectionWidthInChunks = DhSectionPos.getChunkWidth(missingPos);
         totalWorldGenChunkCount += sectionWidthInChunks * sectionWidthInChunks;
         totalWorldGenTaskCount++;
      }

      this.fullDataSourceProvider.setEstimatedRemainingRetrievalChunkCount(totalWorldGenChunkCount);
      this.fullDataSourceProvider.setTotalRetrievalPositionCount(totalWorldGenTaskCount);
   }

   @Override
   public void onConfigValueSet() {
      boolean generatorEnabled = this.level instanceof DhClientServerLevel
         ? Config.Common.WorldGenerator.enableDistantGeneration.get()
         : Config.Server.enableServerGeneration.get();
      if (generatorEnabled) {
         this.requeueAllRetrievalTasksRef.set(true);
      } else {
         this.missingGenerationPosSet.clear();
         this.queuedGenerationPosSet.clear();
         this.requeueAllRetrievalTasksRef.set(false);
      }
   }

   private void tryQueuePosForRetrieval(long pos) {
      LongArrayList missingPosList = this.fullDataSourceProvider.getPositionsToRetrieve(pos);
      if (missingPosList != null) {
         for (int i = 0; i < missingPosList.size(); i++) {
            long missingPos = missingPosList.getLong(i);
            if (!this.queuedGenerationPosSet.contains(missingPos)) {
               this.missingGenerationPosSet.add(missingPos);
            }
         }
      }
   }

   private static boolean threadPoolCanAcceptWorldGenTasks() {
      PriorityTaskPicker.Executor renderLoadExecutor = ThreadPoolUtil.getRenderLoadingExecutor();
      if (renderLoadExecutor != null && renderLoadExecutor.getQueueSize() < FullDataUpdatePropagatorV2.getMaxPropagateTaskCount() / 2) {
         PriorityTaskPicker.Executor fileHandlerExecutor = ThreadPoolUtil.getFileHandlerExecutor();
         return fileHandlerExecutor != null && fileHandlerExecutor.getQueueSize() < FullDataUpdatePropagatorV2.getMaxPropagateTaskCount() / 2;
      } else {
         return false;
      }
   }

   private void tryRefreshRenderingBeaconsAsync(DhBlockPos2D playerPos) {
      if (this.beaconBeamRepo != null && this.beaconRenderHandler != null) {
         AbstractExecutorService executor = ThreadPoolUtil.getFileHandlerExecutor();
         if (executor != null) {
            if (this.beaconUpdateFuture.isDone()) {
               CompletableFuture<Void> future = new CompletableFuture<>();
               this.beaconUpdateFuture = future;

               try {
                  executor.execute(() -> {
                     this.refreshRenderingBeacons(playerPos);

                     try {
                        Thread.sleep(2000L);
                     } catch (InterruptedException var4) {
                     }

                     future.complete(null);
                  });
               } catch (RejectedExecutionException var5) {
                  future.completeExceptionally(var5);
               }
            }
         }
      }
   }

   private void refreshRenderingBeacons(DhBlockPos2D playerPos) {
      if (this.beaconBeamRepo != null && this.beaconRenderHandler != null) {
         try {
            synchronized (this.beaconList) {
               int blockDistanceRadius = this.blockRenderDistanceDiameter / 2;
               int minBlockPosX = playerPos.x - blockDistanceRadius;
               int minBlockPosZ = playerPos.z - blockDistanceRadius;
               int maxBlockPosX = playerPos.x + blockDistanceRadius;
               int maxBlockPosZ = playerPos.z + blockDistanceRadius;
               ArrayList<BeaconBeamDTO> dbBeacons = this.beaconBeamRepo.getAllBeamsInBlockPosRange(minBlockPosX, maxBlockPosX, minBlockPosZ, maxBlockPosZ);
               ArrayList<BeaconRenderHandler.BeaconBeamWithWidth> newBeaconList = new ArrayList<>(this.beaconList.size());

               for (BeaconBeamDTO beaconBeam : dbBeacons) {
                  byte beaconDetailLevel = this.calcExpectedDetailLevel(playerPos, beaconBeam.blockPos.getX(), beaconBeam.blockPos.getZ());
                  newBeaconList.add(new BeaconRenderHandler.BeaconBeamWithWidth(beaconBeam, beaconDetailLevel));
               }

               boolean replaceBeacons = false;
               if (this.beaconList.size() != newBeaconList.size()) {
                  replaceBeacons = true;
               } else {
                  this.beaconList.sort(BeaconRenderHandler.NegativeInfiniteBlockPosComparator.INSTANCE);
                  newBeaconList.sort(BeaconRenderHandler.NegativeInfiniteBlockPosComparator.INSTANCE);

                  for (int i = 0; i < this.beaconList.size(); i++) {
                     BeaconRenderHandler.BeaconBeamWithWidth oldBeam = this.beaconList.get(i);
                     BeaconRenderHandler.BeaconBeamWithWidth newBeam = newBeaconList.get(i);
                     if (!oldBeam.equals(newBeam)) {
                        replaceBeacons = true;
                        break;
                     }
                  }
               }

               if (replaceBeacons) {
                  this.beaconList.clear();
                  this.beaconList.addAll(newBeaconList);
                  this.beaconRenderHandler.replaceRenderingBeacons(this.beaconList);
               }
            }
         } catch (Exception var16) {
            LOGGER.error("Unexpected issue updating beacons, error: [" + var16.getMessage() + "].", var16);
         }
      }
   }

   public byte calcExpectedDetailLevel(DhBlockPos2D playerPos, long sectionPos) {
      double sectionBlockRadius = DhSectionPos.getBlockWidth(sectionPos) * (Math.sqrt(2.0) / 2.0);
      return this.calcExpectedDetailLevel(
         playerPos, DhSectionPos.getCenterBlockPosX(sectionPos), DhSectionPos.getCenterBlockPosZ(sectionPos), sectionBlockRadius
      );
   }

   public byte calcExpectedDetailLevel(DhBlockPos2D playerPos, int targetBlockPosX, int targetBlockPosZ) {
      return this.calcExpectedDetailLevel(playerPos, targetBlockPosX, targetBlockPosZ, 0.0);
   }

   private byte calcExpectedDetailLevel(DhBlockPos2D playerPos, int targetBlockPosX, int targetBlockPosZ, double targetBlockRadius) {
      double blockDistance = playerPos.dist(targetBlockPosX, targetBlockPosZ);
      if (this.cameraZoom.magnification > 1.0
         && this.cameraZoom.coneIntersectsCircle(playerPos.x, playerPos.z, targetBlockPosX, targetBlockPosZ, targetBlockRadius)) {
         blockDistance /= this.cameraZoom.magnification;
         EDhApiMaxHorizontalResolution maxHorizontalResolution = Config.Client.Advanced.Graphics.Quality.maxHorizontalResolution.get();
         return this.calcDetailLevelFromDistance(blockDistance, maxHorizontalResolution.detailLevel);
      } else {
         return this.calcDetailLevelFromDistance(blockDistance);
      }
   }

   private void updateDetailLevelVariables() {
      this.detailDropOffDistanceUnit = Config.Client.Advanced.Graphics.Quality.horizontalQuality.get().distanceUnitInBlocks * 16;
      this.detailDropOffLogBase = Math.log(Config.Client.Advanced.Graphics.Quality.horizontalQuality.get().quadraticBase);
      RenderUtil.updateCameraZoom(this.cameraZoom);
      this.maxLeafRenderDetailLevel = Config.Client.Advanced.Graphics.Quality.maxHorizontalResolution.get().detailLevel;
      byte minSectionDetailLevel = this.calcDetailLevelFromDistance(this.blockRenderDistanceDiameter);
      minSectionDetailLevel = (byte)Math.min(--minSectionDetailLevel, this.treeRootDetailLevel);
      this.minRootRenderDetailLevel = (byte)Math.max(minSectionDetailLevel, this.maxLeafRenderDetailLevel);
   }

   private byte calcDetailLevelFromDistance(double blockDistance) {
      return this.calcDetailLevelFromDistance(blockDistance, this.maxLeafRenderDetailLevel);
   }

   private byte calcDetailLevelFromDistance(double blockDistance, byte maxDetailLevel) {
      int detailLevel = (int)(Math.log(blockDistance / this.detailDropOffDistanceUnit) / this.detailDropOffLogBase);
      return (byte)MathUtil.clamp(maxDetailLevel, detailLevel, 15);
   }

   public void clearRenderDataCache() {
      try {
         this.treeTickLock.lock();
         LOGGER.info("Disposing render data...");
         Iterator<QuadNode<LodRenderSection>> nodeIterator = this.nodeIterator();

         while (nodeIterator.hasNext()) {
            QuadNode<LodRenderSection> quadNode = nodeIterator.next();
            if (quadNode.value != null) {
               quadNode.value.close();
               quadNode.value = null;
            }
         }

         LOGGER.info("Render data cleared, please wait a moment for everything to reload...");
      } catch (Exception var6) {
         LOGGER.error("Unexpected error when clearing LodQuadTree render cache: " + var6.getMessage(), var6);
      } finally {
         this.treeTickLock.unlock();
      }
   }

   public void queuePosToReload(long pos) {
      this.sectionsToReload.add(pos);

      for (EDhDirection direction : EDhDirection.CARDINAL_COMPASS) {
         long adjacentPos = DhSectionPos.getAdjacentPos(pos, direction);
         this.sectionsToReload.add(adjacentPos);
      }
   }

   @Override
   public void debugRender(AbstractDebugWireframeRenderer debugRenderer) {
      this.populateListWithEnabledRenderSections(this.debugNodeList);

      for (int i = 0; i < this.debugNodeList.size(); i++) {
         LodRenderSection renderSection = this.debugNodeList.get(i);
         Color color = Color.BLACK;
         if (renderSection.gpuUploadInProgress()) {
            color = Color.ORANGE;
         } else if (!renderSection.canRender()) {
            color = Color.PINK;
         } else if (renderSection.renderBufferContainer.hasNonNullVbos()) {
            if (renderSection.renderBufferContainer.vboBufferCount() != 0) {
               color = Color.GREEN;
            } else {
               color = Color.RED;
            }
         }

         int levelMinY = this.level.getLevelWrapper().getMinHeight();
         int levelMaxY = this.level.getLevelWrapper().getMaxHeight();
         int levelHeightRange = levelMaxY - levelMinY;
         int maxY = levelMaxY - levelHeightRange / 2;
         debugRenderer.renderBox(new AbstractDebugWireframeRenderer.Box(renderSection.pos, levelMinY, maxY, 0.05F, color));
      }
   }

   @Override
   public void close() {
      DEBUG_RENDERER.unregister(this, Config.Client.Advanced.Debugging.DebugWireframe.showQuadTreeRenderStatus);
      Config.Common.WorldGenerator.enableDistantGeneration.removeListener(this);
      Config.Server.enableServerGeneration.removeListener(this);
      ThreadPoolExecutor mainCleanupExecutor = ThreadPoolUtil.getCleanupExecutor();
      mainCleanupExecutor.execute(() -> {
         this.treeTickLock.lock();

         try {
            Iterator<QuadNode<LodRenderSection>> nodeIterator = this.nodeIterator();

            while (nodeIterator.hasNext()) {
               QuadNode<LodRenderSection> quadNode = nodeIterator.next();
               LodRenderSection renderSection = quadNode.value;
               if (renderSection != null) {
                  renderSection.close();
                  quadNode.value = null;
               }
            }
         } finally {
            this.treeTickLock.unlock();
         }
      });
   }
}
