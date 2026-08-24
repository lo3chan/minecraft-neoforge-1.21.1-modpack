package io.wispforest.owo.blockentity;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class LinearProcess<T> {
   private final Int2ObjectMap<BiConsumer<LinearProcessExecutor<T>, T>> clientEventTable = new Int2ObjectOpenHashMap();
   private final Int2ObjectMap<LinearProcessExecutor.ProcessStep<T>> clientProcessStepTable = new Int2ObjectOpenHashMap();
   private final Int2ObjectMap<BiConsumer<LinearProcessExecutor<T>, T>> serverEventTable = new Int2ObjectOpenHashMap();
   private final Int2ObjectMap<LinearProcessExecutor.ProcessStep<T>> serverProcessStepTable = new Int2ObjectOpenHashMap();
   private Predicate<LinearProcessExecutor<T>> condition = tLinearProcessExecutor -> true;
   private final int processLength;
   private boolean finished = false;

   public LinearProcess(int processLength) {
      this.processLength = processLength;
   }

   public LinearProcessExecutor<T> createExecutor(T target) {
      if (!this.finished) {
         throw new IllegalStateException("Illegal attempt to create executor for unfinished process");
      } else {
         return new LinearProcessExecutor<>(target, this.processLength, this.condition, this.serverProcessStepTable);
      }
   }

   public void configureExecutor(LinearProcessExecutor<T> executor, boolean client) {
      if (!this.finished) {
         throw new IllegalStateException("Illegal attempt to configure executor using unfinished process");
      } else {
         if (client) {
            executor.configure(this.clientEventTable, this.clientProcessStepTable);
         } else {
            executor.configure(this.serverEventTable, this.serverProcessStepTable);
         }
      }
   }

   public void addCommonStep(int when, int length, BiConsumer<LinearProcessExecutor<T>, T> executor) {
      this.checkForIllegalModification();
      LinearProcessExecutor.ProcessStep<T> step = new LinearProcessExecutor.ProcessStep<>(length, executor);
      this.clientProcessStepTable.put(when, step);
      this.serverProcessStepTable.put(when, step);
   }

   public void addClientStep(int when, int length, BiConsumer<LinearProcessExecutor<T>, T> executor) {
      this.checkForIllegalModification();
      LinearProcessExecutor.ProcessStep<T> step = new LinearProcessExecutor.ProcessStep<>(length, executor);
      this.clientProcessStepTable.put(when, step);
   }

   public void addServerStep(int when, int length, BiConsumer<LinearProcessExecutor<T>, T> executor) {
      this.checkForIllegalModification();
      LinearProcessExecutor.ProcessStep<T> step = new LinearProcessExecutor.ProcessStep<>(length, executor);
      this.serverProcessStepTable.put(when, step);
   }

   public void addCommonEvent(int when, BiConsumer<LinearProcessExecutor<T>, T> executor) {
      this.eventAtIndex(when, this.clientEventTable, executor);
      this.eventAtIndex(when, this.serverEventTable, executor);
   }

   public void addClientEvent(int when, BiConsumer<LinearProcessExecutor<T>, T> executor) {
      this.eventAtIndex(when, this.clientEventTable, executor);
   }

   public void addServerEvent(int when, BiConsumer<LinearProcessExecutor<T>, T> executor) {
      this.eventAtIndex(when, this.serverEventTable, executor);
   }

   public void whenFinishedCommon(BiConsumer<LinearProcessExecutor<T>, T> executor) {
      this.eventAtIndex(-2, this.clientEventTable, executor);
      this.eventAtIndex(-2, this.serverEventTable, executor);
   }

   public void whenFinishedServer(BiConsumer<LinearProcessExecutor<T>, T> executor) {
      this.eventAtIndex(-2, this.serverEventTable, executor);
   }

   public void whenFinishedClient(BiConsumer<LinearProcessExecutor<T>, T> executor) {
      this.eventAtIndex(-2, this.clientEventTable, executor);
   }

   public void onCancelledCommon(BiConsumer<LinearProcessExecutor<T>, T> executor) {
      this.eventAtIndex(-1, this.clientEventTable, executor);
      this.eventAtIndex(-1, this.serverEventTable, executor);
   }

   public void onCancelledServer(BiConsumer<LinearProcessExecutor<T>, T> executor) {
      this.eventAtIndex(-1, this.serverEventTable, executor);
   }

   public void onCancelledClient(BiConsumer<LinearProcessExecutor<T>, T> executor) {
      this.eventAtIndex(-1, this.clientEventTable, executor);
   }

   public void runConditionally(Predicate<LinearProcessExecutor<T>> condition) {
      this.condition = condition;
   }

   public void finish() {
      this.finished = true;
   }

   private void checkForIllegalModification() {
      if (this.finished) {
         throw new IllegalStateException("Illegal attempt to modify finished process");
      }
   }

   private void eventAtIndex(int index, Int2ObjectMap<BiConsumer<LinearProcessExecutor<T>, T>> eventTable, BiConsumer<LinearProcessExecutor<T>, T> executor) {
      this.checkForIllegalModification();
      eventTable.put(index, executor);
   }
}
