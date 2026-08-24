package vectorwing.farmersdelight.common.loot.modifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import vectorwing.farmersdelight.common.Configuration;

public class FDAddTableLootModifier extends AddTableLootModifier {
   public static final Supplier<MapCodec<FDAddTableLootModifier>> CODEC = Suppliers.memoize(
      () -> RecordCodecBuilder.mapCodec(
         inst -> codecStart(inst)
            .and(ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("lootTable").forGetter(m -> m.lootTable))
            .apply(inst, FDAddTableLootModifier::new)
      )
   );
   private final ResourceKey<LootTable> lootTable;

   protected FDAddTableLootModifier(LootItemCondition[] conditionsIn, ResourceKey<LootTable> lootTable) {
      super(conditionsIn, lootTable);
      this.lootTable = lootTable;
   }

   @Nonnull
   protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
      if (Configuration.GENERATE_FD_CHEST_LOOT.get()) {
         context.getResolver()
            .get(Registries.LOOT_TABLE, this.lootTable)
            .ifPresent(
               extraTable -> ((LootTable)extraTable.value()).getRandomItemsRaw(context, LootTable.createStackSplitter(context.getLevel(), generatedLoot::add))
            );
      }

      return generatedLoot;
   }
}
