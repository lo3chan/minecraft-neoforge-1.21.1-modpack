package io.wispforest.owo.blockentity;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.ApiStatus.Internal;

public class LinearProcessExecutor<T> {
   public static final int CANCEL_EVENT_INDEX = -1;
   public static final int FINISH_EVENT_INDEX = -2;
   private final T target;
   private final int processLength;
   private final Predicate<LinearProcessExecutor<T>> condition;
   private Int2ObjectMap<BiConsumer<LinearProcessExecutor<T>, T>> eventTable;
   private Int2ObjectMap<LinearProcessExecutor.ProcessStep<T>> processStepTable;
   private final Set<LinearProcessExecutor.ProcessStep.Info<T>> activeSteps = new HashSet<>();
   private int processTick = 0;

   protected LinearProcessExecutor(
      T target, int processLength, Predicate<LinearProcessExecutor<T>> condition, Int2ObjectMap<LinearProcessExecutor.ProcessStep<T>> serverStepTable
   ) {
      this.target = target;
      this.processLength = processLength;
      this.condition = condition;
      this.eventTable = null;
      this.processStepTable = serverStepTable;
   }

   protected void configure(
      Int2ObjectMap<BiConsumer<LinearProcessExecutor<T>, T>> eventTable, Int2ObjectMap<LinearProcessExecutor.ProcessStep<T>> processStepTable
   ) {
      this.eventTable = eventTable;
      this.processStepTable = processStepTable;
   }

   public void tick() {
      if (this.eventTable == null) {
         throw new IllegalStateException("Illegal attempt to tick unconfigured executor");
      } else if (this.running()) {
         if (!this.cancelIfAppropriate()) {
            if (!this.finishIfAppropriate()) {
               int tableIndex = this.processTick - 1;
               if (this.eventTable.containsKey(tableIndex)) {
                  ((BiConsumer)this.eventTable.get(tableIndex)).accept(this, this.target);
               }

               if (this.processStepTable.containsKey(tableIndex)) {
                  this.activeSteps.add(((LinearProcessExecutor.ProcessStep)this.processStepTable.get(tableIndex)).createInfo(tableIndex));
               }

               this.activeSteps.removeIf(stepInfo -> !stepInfo.tick(this));
               this.processTick++;
            }
         }
      }
   }

   public boolean begin() {
      if (this.processTick != 0) {
         return false;
      } else {
         this.processTick = 1;
         return true;
      }
   }

   public boolean running() {
      return this.processTick > 0;
   }

   public int getProcessTick() {
      return this.processTick;
   }

   public T getTarget() {
      return this.target;
   }

   public boolean cancel() {
      if (!this.running()) {
         return false;
      } else {
         this.processTick = 0;
         this.activeSteps.clear();
         if (this.eventTable.containsKey(-1)) {
            ((BiConsumer)this.eventTable.get(-1)).accept(this, this.target);
         }

         return true;
      }
   }

   private boolean finishIfAppropriate() {
      if (!this.running()) {
         return false;
      } else if (this.processTick < this.processLength) {
         return false;
      } else {
         if (this.eventTable.containsKey(-2)) {
            ((BiConsumer)this.eventTable.get(-2)).accept(this, this.target);
         }

         this.processTick = 0;
         this.activeSteps.clear();
         return true;
      }
   }

   private boolean cancelIfAppropriate() {
      if (this.condition.test(this)) {
         return false;
      } else {
         this.cancel();
         return true;
      }
   }

   public void writeState(CompoundTag targetTag) {
      targetTag.putInt("ProcessTick", this.processTick);
   }

   public void readState(CompoundTag targetTag) {
      this.processTick = targetTag.getInt("ProcessTick");
      this.activeSteps.clear();
      this.processStepTable.forEach((index, step) -> {
         if (this.processTick >= index && this.processTick <= index + step.length) {
            this.activeSteps.add(step.createInfo(index, this.processTick - index));
         }
      });
   }

   @Internal
   public record ProcessStep<T>(int length, BiConsumer<LinearProcessExecutor<T>, T> executor) {
      public LinearProcessExecutor.ProcessStep.Info<T> createInfo(int index) {
         return new LinearProcessExecutor.ProcessStep.Info<>(index, this);
      }

      public LinearProcessExecutor.ProcessStep.Info<T> createInfo(int index, int tick) {
         return new LinearProcessExecutor.ProcessStep.Info<>(index, tick, this);
      }

      public static final class Info<T> {
         private final LinearProcessExecutor.ProcessStep<T> step;
         private final int index;
         private int tick = 0;

         public Info(int index, LinearProcessExecutor.ProcessStep<T> step) {
            this.index = index;
            this.step = step;
         }

         public Info(int index, int tick, LinearProcessExecutor.ProcessStep<T> step) {
            this.index = index;
            this.tick = tick;
            this.step = step;
         }

         public boolean tick(LinearProcessExecutor<T> target) {
            this.tick++;
            if (this.tick == this.step.length) {
               return false;
            } else {
               this.step.executor.accept(target, target.getTarget());
               return true;
            }
         }
      }
   }
}
