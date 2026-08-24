package fuzs.puzzleslib.neoforge.impl.event;

import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

public final class ForwardingLootTableBuilder extends Builder {
   private final LootTable lootTable;

   public ForwardingLootTableBuilder(LootTable lootTable) {
      this.lootTable = lootTable;
   }

   public Builder withPool(net.minecraft.world.level.storage.loot.LootPool.Builder lootPool) {
      if (!(this.lootTable.pools instanceof ArrayList)) {
         this.lootTable.pools = new ArrayList(this.lootTable.pools);
      }

      this.lootTable.pools.add(lootPool.build());
      return this;
   }

   public Builder setParamSet(LootContextParamSet paramSet) {
      this.lootTable.paramSet = paramSet;
      return this;
   }

   public Builder setRandomSequence(ResourceLocation randomSequence) {
      this.lootTable.randomSequence = Optional.of(randomSequence);
      return this;
   }

   public Builder apply(net.minecraft.world.level.storage.loot.functions.LootItemFunction.Builder functionBuilder) {
      if (!(this.lootTable.functions instanceof ArrayList)) {
         this.lootTable.functions = new ArrayList(this.lootTable.functions);
      }

      this.lootTable.functions.add(functionBuilder.build());
      return this;
   }

   public LootTable build() {
      return this.lootTable;
   }
}
