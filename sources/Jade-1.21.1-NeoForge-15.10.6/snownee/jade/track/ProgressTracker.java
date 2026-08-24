package snownee.jade.track;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

public class ProgressTracker {
   private final ListMultimap<ResourceLocation, TrackInfo> map = ArrayListMultimap.create();

   public <T extends TrackInfo> T getOrCreate(ResourceLocation tag, Class<T> type, Supplier<T> supplier) {
      List<TrackInfo> infos = this.map.get(tag);
      T info = null;

      for (TrackInfo o : infos) {
         if (!o.updatedThisTick && type.isInstance(o)) {
            info = (T)type.cast(o);
            break;
         }
      }

      if (info == null) {
         info = (T)supplier.get();
         this.map.put(tag, info);
      }

      info.updatedThisTick = true;
      return info;
   }

   public void tick() {
      if (!this.map.isEmpty()) {
         this.map.values().removeIf(info -> {
            if (info.updatedThisTick && info.alive) {
               info.tick();
               info.updatedThisTick = false;
               return false;
            } else {
               return true;
            }
         });
      }
   }

   public void clear() {
      this.map.clear();
   }
}
