package alternate.current.util.profiler;

import alternate.current.AlternateCurrentMod;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.apache.logging.log4j.Logger;

public class ACProfiler implements Profiler {
   private static final Logger LOGGER = AlternateCurrentMod.LOGGER;
   private final Stack<Integer> indexStack = new Stack<>();
   private final List<String> locations = new ArrayList<>();
   private final List<Long> times = new ArrayList<>();
   private boolean started;

   @Override
   public void start() {
      if (this.started) {
         LOGGER.warn("profiling already started!");
      } else {
         this.indexStack.clear();
         this.locations.clear();
         this.times.clear();
         this.started = true;
         this.push("total");
      }
   }

   @Override
   public void end() {
      if (this.started) {
         this.pop();
         this.started = false;
         if (!this.indexStack.isEmpty()) {
            LOGGER.warn("profiling ended before stack was fully popped, did something go wrong?");
         }

         ProfilerResults.add(this.locations, this.times);
      } else {
         LOGGER.warn("profiling already ended!");
      }
   }

   @Override
   public void push(String location) {
      if (this.started) {
         this.indexStack.add(this.times.size());
         this.locations.add(location);
         this.times.add(System.nanoTime());
      } else {
         LOGGER.error("cannot push " + location + " as profiling hasn't started!");
      }
   }

   @Override
   public void pop() {
      if (this.started) {
         Integer index = this.indexStack.pop();
         if (index == null) {
            LOGGER.error("no element to pop!");
         } else {
            long startTime = this.times.get(index);
            long endTime = System.nanoTime();
            this.times.set(index, endTime - startTime);
         }
      } else {
         LOGGER.error("cannot pop as profiling hasn't started!");
      }
   }

   @Override
   public void swap(String location) {
      this.pop();
      this.push(location);
   }
}
