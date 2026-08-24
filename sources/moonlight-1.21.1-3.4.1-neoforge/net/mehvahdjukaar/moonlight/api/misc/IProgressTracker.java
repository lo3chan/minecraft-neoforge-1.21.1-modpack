package net.mehvahdjukaar.moonlight.api.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public interface IProgressTracker {
   IProgressTracker.Task subtask(int var1);

   static IProgressTracker.Tree createTree(int totalSteps) {
      return new IProgressTracker.Tree(totalSteps);
   }

   public interface Task extends IProgressTracker {
      void step();
   }

   public static final class Tree implements IProgressTracker.Task {
      private volatile List<IProgressTracker.Tree> subtasks = List.of();
      private final int totalSteps;
      private final AtomicInteger completedSteps = new AtomicInteger(0);

      public Tree(int totalSteps) {
         this.totalSteps = totalSteps;
      }

      @Override
      public IProgressTracker.Task subtask(int totalSteps) {
         IProgressTracker.Tree child = new IProgressTracker.Tree(totalSteps);
         synchronized (this) {
            List<IProgressTracker.Tree> next = new ArrayList<>(this.subtasks);
            next.add(child);
            this.subtasks = Collections.unmodifiableList(next);
            return child;
         }
      }

      @Override
      public void step() {
         int prev;
         int next;
         do {
            prev = this.completedSteps.get();
            if (prev >= this.totalSteps) {
               return;
            }

            next = prev + 1;
         } while (!this.completedSteps.compareAndSet(prev, next));
      }

      public float getProgress() {
         if (this.totalSteps == 0) {
            return 1.0F;
         } else {
            List<IProgressTracker.Tree> snapshot = this.subtasks;
            int localCompleted = this.completedSteps.get();
            if (snapshot.isEmpty()) {
               return (float)localCompleted / this.totalSteps;
            } else {
               float sum = 0.0F;

               for (IProgressTracker.Tree sub : snapshot) {
                  sum += sub.getProgress();
               }

               return sum / snapshot.size();
            }
         }
      }

      public int countLeaves() {
         List<IProgressTracker.Tree> snapshot = this.subtasks;
         if (snapshot.isEmpty()) {
            return 1;
         } else {
            int sum = 0;

            for (IProgressTracker.Tree sub : snapshot) {
               sum += sub.countLeaves();
            }

            return sum;
         }
      }
   }
}
