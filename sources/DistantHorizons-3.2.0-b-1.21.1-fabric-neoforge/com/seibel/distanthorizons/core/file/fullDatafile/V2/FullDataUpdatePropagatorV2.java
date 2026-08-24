package com.seibel.distanthorizons.core.file.fullDatafile.V2;

import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.render.renderer.AbstractDebugWireframeRenderer;
import com.seibel.distanthorizons.core.render.renderer.IDebugRenderable;
import com.seibel.distanthorizons.core.util.ThreadUtil;
import com.seibel.distanthorizons.core.util.threading.PriorityTaskPicker;
import com.seibel.distanthorizons.core.util.threading.ThreadPoolUtil;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class FullDataUpdatePropagatorV2 implements IDebugRenderable, AutoCloseable {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final IMinecraftClientWrapper MC_CLIENT = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class);
   protected static final int PROPAGATE_QUEUE_THREAD_DELAY_IN_MS = 250;
   public static final int NUMBER_OF_PARENT_UPDATE_TASKS_PER_THREAD = 10;
   private final Set<Long> updatingPosSet = ConcurrentHashMap.newKeySet();
   public final ThreadPoolExecutor updateQueueProcessor;
   private final AtomicBoolean isShutdownRef = new AtomicBoolean(false);
   private final String levelId;
   private final FullDataSourceProviderV2 provider;
   private final FullDataUpdaterV2 dataUpdater;

   public static int getMaxPropagateTaskCount() {
      return 10 * Config.Common.MultiThreading.numberOfThreads.get();
   }

   public FullDataUpdatePropagatorV2(FullDataSourceProviderV2 provider, FullDataUpdaterV2 dataUpdater, String levelId) {
      this.provider = provider;
      this.dataUpdater = dataUpdater;
      this.levelId = levelId;
      this.updateQueueProcessor = ThreadUtil.makeSingleThreadPool("Update Propagate Queue [" + this.levelId + "]");
      this.updateQueueProcessor.execute(this::runUpdateQueue);
   }

   private void runUpdateQueue() {
      while (!Thread.interrupted()) {
         try {
            Thread.sleep(250L);
            PriorityTaskPicker.Executor executor = ThreadPoolUtil.getUpdatePropagatorExecutor();
            if (executor != null && !executor.isTerminated()) {
               DhBlockPos targetBlockPos = DhBlockPos.ZERO;
               if (MC_CLIENT != null && MC_CLIENT.playerExists()) {
                  targetBlockPos = MC_CLIENT.getPlayerBlockPos();
               }

               this.runParentUpdates(executor, targetBlockPos);
               if (Config.Common.LodBuilding.Experimental.upsampleLowerDetailLodsToFillHoles.get()) {
                  this.runChildUpdates(executor, targetBlockPos);
               }
            }
         } catch (InterruptedException var3) {
            Thread.currentThread().interrupt();
         } catch (Exception var4) {
            LOGGER.error("Unexpected error in the parent update queue thread. Error: " + var4.getMessage(), var4);
         }
      }
   }

   private void runParentUpdates(PriorityTaskPicker.Executor executor, DhBlockPos targetBlockPos) {
      int maxUpdateTaskCount = getMaxPropagateTaskCount();
      if (executor.getQueueSize() <= maxUpdateTaskCount && this.updatingPosSet.size() <= maxUpdateTaskCount) {
         LongArrayList parentUpdatePosList = this.provider.repo.getPositionsToUpdate(targetBlockPos.getX(), targetBlockPos.getZ(), maxUpdateTaskCount);
         HashMap<Long, HashSet<Long>> updatePosByParentPos = new HashMap<>();
         LongListIterator var6 = parentUpdatePosList.iterator();

         while (var6.hasNext()) {
            Long pos = (Long)var6.next();
            updatePosByParentPos.compute(DhSectionPos.getParentPos(pos), (parentPos, updatePosSet) -> {
               if (updatePosSet == null) {
                  updatePosSet = new HashSet<>();
               }

               updatePosSet.add(pos);
               return updatePosSet;
            });
         }

         for (Long parentUpdatePos : updatePosByParentPos.keySet()) {
            if (this.updatingPosSet.size() > maxUpdateTaskCount || executor.getQueueSize() > maxUpdateTaskCount) {
               break;
            }

            if (this.updatingPosSet.add(parentUpdatePos)) {
               try {
                  executor.execute(
                     () -> {
                        ReentrantLock parentWriteLock = this.dataUpdater.updateLockProvider.getLock(parentUpdatePos);
                        boolean parentLocked = false;

                        try {
                           if (parentWriteLock.tryLock()) {
                              parentLocked = true;
                              this.dataUpdater.lockedPosSet.add(parentUpdatePos);
                              FullDataSourceV2 parentDataSource = this.provider.get(parentUpdatePos);

                              try {
                                 if (parentDataSource != null) {
                                    for (Long childPos : updatePosByParentPos.get(parentUpdatePos)) {
                                       ReentrantLock childReadLock = this.dataUpdater.updateLockProvider.getLock(childPos);

                                       try {
                                          childReadLock.lock();
                                          this.dataUpdater.lockedPosSet.add(childPos);
                                          FullDataSourceV2 childDataSource = this.provider.get(childPos);

                                          try {
                                             if (childDataSource != null) {
                                                parentDataSource.updateFromDataSource(childDataSource);
                                             }
                                          } catch (Throwable var30) {
                                             if (childDataSource != null) {
                                                try {
                                                   childDataSource.close();
                                                } catch (Throwable var29) {
                                                   var30.addSuppressed(var29);
                                                }
                                             }

                                             throw var30;
                                          }

                                          if (childDataSource != null) {
                                             childDataSource.close();
                                          }
                                       } catch (Exception var31) {
                                          LOGGER.error(
                                             "Unexpected in parent update propagation for parent pos: ["
                                                + DhSectionPos.toString(parentUpdatePos)
                                                + "], child pos: ["
                                                + DhSectionPos.toString(parentUpdatePos)
                                                + "], Error: ["
                                                + var31.getMessage()
                                                + "].",
                                             var31
                                          );
                                       } finally {
                                          this.provider.repo.setApplyToParent(childPos, false);
                                          childReadLock.unlock();
                                          this.dataUpdater.lockedPosSet.remove(childPos);
                                       }
                                    }

                                    if (DhSectionPos.getDetailLevel(parentUpdatePos) < 15) {
                                       parentDataSource.applyToParent = true;
                                    }

                                    this.dataUpdater.updateDataSource(parentDataSource);
                                 }
                              } catch (Throwable var33) {
                                 if (parentDataSource != null) {
                                    try {
                                       parentDataSource.close();
                                    } catch (Throwable var28) {
                                       var33.addSuppressed(var28);
                                    }
                                 }

                                 throw var33;
                              }

                              if (parentDataSource != null) {
                                 parentDataSource.close();
                              }
                           }
                        } finally {
                           if (parentLocked) {
                              parentWriteLock.unlock();
                              this.dataUpdater.lockedPosSet.remove(parentUpdatePos);
                           }

                           this.updatingPosSet.remove(parentUpdatePos);
                        }
                     }
                  );
               } catch (RejectedExecutionException var9) {
               } catch (Exception var10) {
                  this.updatingPosSet.remove(parentUpdatePos);
                  throw var10;
               }
            }
         }
      }
   }

   private void runChildUpdates(PriorityTaskPicker.Executor executor, DhBlockPos targetBlockPos) {
      int maxUpdateTaskCount = getMaxPropagateTaskCount();
      if (executor.getQueueSize() < maxUpdateTaskCount && this.updatingPosSet.size() < maxUpdateTaskCount) {
         LongArrayList childUpdatePosList = this.provider.repo.getChildPositionsToUpdate(targetBlockPos.getX(), targetBlockPos.getZ(), maxUpdateTaskCount);
         LongListIterator var5 = childUpdatePosList.iterator();

         while (var5.hasNext()) {
            long parentUpdatePos = (Long)var5.next();
            if (this.updatingPosSet.size() > maxUpdateTaskCount || executor.getQueueSize() > maxUpdateTaskCount) {
               break;
            }

            if (this.updatingPosSet.add(parentUpdatePos)) {
               try {
                  executor.execute(
                     () -> {
                        ReentrantLock parentReadLock = this.dataUpdater.updateLockProvider.getLock(parentUpdatePos);
                        boolean parentLocked = false;

                        try {
                           if (parentReadLock.tryLock()) {
                              parentLocked = true;
                              this.dataUpdater.lockedPosSet.add(parentUpdatePos);
                              FullDataSourceV2 parentDataSource = this.provider.get(parentUpdatePos);

                              try {
                                 if (parentDataSource != null) {
                                    for (int i = 0; i < 4; i++) {
                                       long childPos = DhSectionPos.getChildByIndex(parentUpdatePos, i);
                                       ReentrantLock childWriteLock = this.dataUpdater.updateLockProvider.getLock(childPos);

                                       try {
                                          childWriteLock.lock();
                                          this.dataUpdater.lockedPosSet.add(childPos);
                                          FullDataSourceV2 childDataSource = this.provider.get(childPos);

                                          try {
                                             if (childDataSource != null) {
                                                childDataSource.updateFromDataSource(parentDataSource);
                                                if (DhSectionPos.getDetailLevel(childPos) != 6) {
                                                   childDataSource.applyToChildren = true;
                                                }

                                                this.dataUpdater.updateDataSource(childDataSource);
                                             }
                                          } catch (Throwable var31) {
                                             if (childDataSource != null) {
                                                try {
                                                   childDataSource.close();
                                                } catch (Throwable var30) {
                                                   var31.addSuppressed(var30);
                                                }
                                             }

                                             throw var31;
                                          }

                                          if (childDataSource != null) {
                                             childDataSource.close();
                                          }
                                       } catch (Exception var32) {
                                          LOGGER.error(
                                             "Unexpected in child update propagation for parent pos: ["
                                                + DhSectionPos.toString(parentUpdatePos)
                                                + "], child pos: ["
                                                + DhSectionPos.toString(parentUpdatePos)
                                                + "], Error: ["
                                                + var32.getMessage()
                                                + "].",
                                             var32
                                          );
                                       } finally {
                                          this.provider.repo.setApplyToChild(parentUpdatePos, false);
                                          childWriteLock.unlock();
                                          this.dataUpdater.lockedPosSet.remove(childPos);
                                       }
                                    }
                                 }
                              } catch (Throwable var34) {
                                 if (parentDataSource != null) {
                                    try {
                                       parentDataSource.close();
                                    } catch (Throwable var29) {
                                       var34.addSuppressed(var29);
                                    }
                                 }

                                 throw var34;
                              }

                              if (parentDataSource != null) {
                                 parentDataSource.close();
                              }
                           }
                        } finally {
                           if (parentLocked) {
                              parentReadLock.unlock();
                              this.dataUpdater.lockedPosSet.remove(parentUpdatePos);
                           }

                           this.updatingPosSet.remove(parentUpdatePos);
                        }
                     }
                  );
               } catch (RejectedExecutionException var9) {
               } catch (Exception var10) {
                  this.updatingPosSet.remove(parentUpdatePos);
                  throw var10;
               }
            }
         }
      }
   }

   @Override
   public void debugRender(AbstractDebugWireframeRenderer renderer) {
      this.updatingPosSet.forEach(pos -> renderer.renderBox(new AbstractDebugWireframeRenderer.Box(pos, -32.0F, 80.0F, 0.2F, Color.MAGENTA)));
   }

   @Override
   public void close() {
      if (this.updateQueueProcessor != null) {
         this.updateQueueProcessor.shutdownNow();
      }
   }
}
