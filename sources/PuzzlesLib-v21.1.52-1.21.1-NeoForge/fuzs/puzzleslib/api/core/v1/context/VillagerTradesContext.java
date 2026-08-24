package fuzs.puzzleslib.api.core.v1.context;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface VillagerTradesContext {
   void registerVillagerTrades(VillagerProfession var1, VillagerTradesContext.VillagerLevel var2, Consumer<List<ItemListing>> var3);

   void registerWanderingTrades(VillagerTradesContext.WanderingTradesPool var1, ItemListing... var2);

   public static enum VillagerLevel {
      NOVICE(1),
      APPRENTICE(2),
      JOURNEYMAN(3),
      EXPERT(4),
      MASTER(5);

      private final int level;

      private VillagerLevel(int level) {
         this.level = level;
      }

      @Internal
      public int getLevel() {
         return this.level;
      }
   }

   public static enum WanderingTradesPool {
      PURCHASES,
      COMMON_SALES,
      SPECIAL_SALES;
   }
}
