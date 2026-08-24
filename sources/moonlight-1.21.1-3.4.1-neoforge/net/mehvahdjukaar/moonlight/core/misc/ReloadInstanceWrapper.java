package net.mehvahdjukaar.moonlight.core.misc;

import com.mojang.datafixers.util.Unit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.events.EarlyPackReloadEvent;
import net.mehvahdjukaar.moonlight.api.events.MoonlightEventsHelper;
import net.mehvahdjukaar.moonlight.api.misc.IProgressTracker;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ResourceManager;

public class ReloadInstanceWrapper implements ReloadInstance {
   private final CompletableFuture<Unit> beforeTask;
   private final CompletableFuture<ReloadInstance> instanceFuture;
   private final IProgressTracker.Tree progressTracker = IProgressTracker.createTree(1);

   public static ReloadInstance wrap(
      Supplier<ReloadInstance> factory, PackType type, ResourceManager manager, Executor backgroundExecutor, Executor mainExecutor
   ) {
      return new ReloadInstanceWrapper(factory, type, manager, backgroundExecutor, mainExecutor);
   }

   public static void executeEarlyReloadBlocking(PackType type, ResourceManager manager, IProgressTracker progressTracker) {
      MoonlightEventsHelper.postEvent(new EarlyPackReloadEvent(manager.listPacks().toList(), manager, type, progressTracker), EarlyPackReloadEvent.class);
   }

   public ReloadInstanceWrapper(Supplier<ReloadInstance> factory, PackType type, ResourceManager manager, Executor backgroundExecutor, Executor mainExecutor) {
      this.beforeTask = CompletableFuture.supplyAsync(() -> {
         executeEarlyReloadBlocking(type, manager, this.progressTracker);
         return Unit.INSTANCE;
      }, backgroundExecutor);
      this.instanceFuture = this.beforeTask.thenApplyAsync(u -> factory.get(), mainExecutor);
   }

   public CompletableFuture<?> done() {
      return this.instanceFuture.thenCompose(ReloadInstance::done);
   }

   public float getActualProgress() {
      if (!this.beforeTask.isDone()) {
         return this.progressTracker.getProgress();
      } else {
         ReloadInstance actual = this.instanceFuture.getNow(null);
         return actual != null ? actual.getActualProgress() : 1.0F;
      }
   }

   public void checkExceptions() {
      if (this.beforeTask.isDone()) {
         if (this.beforeTask.isCompletedExceptionally()) {
            this.beforeTask.join();
         }

         ReloadInstance actual = this.instanceFuture.getNow(null);
         if (actual != null) {
            actual.checkExceptions();
         }
      }
   }
}
