package vectorwing.farmersdelight.common.loot.modifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import vectorwing.farmersdelight.common.block.PieBlock;

public class PastrySlicingModifier extends LootModifier {
   public static final Supplier<MapCodec<PastrySlicingModifier>> CODEC = Suppliers.memoize(
      () -> RecordCodecBuilder.mapCodec(
         inst -> codecStart(inst)
            .and(BuiltInRegistries.ITEM.byNameCodec().fieldOf("slice").forGetter(m -> m.pastrySlice))
            .apply(inst, PastrySlicingModifier::new)
      )
   );
   public static final int MAX_CAKE_BITES = 7;
   public static final int MAX_PIE_BITES = 4;
   private final Item pastrySlice;

   public PastrySlicingModifier(LootItemCondition[] conditions, Item pastrySlice) {
      super(conditions);
      this.pastrySlice = pastrySlice;
   }

   @Nonnull
   protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
      BlockState state = (BlockState)context.getParamOrNull(LootContextParams.BLOCK_STATE);
      if (state != null) {
         Block targetBlock = state.getBlock();
         if (targetBlock instanceof CakeBlock) {
            int bites = (Integer)state.getValue(CakeBlock.BITES);
            generatedLoot.add(new ItemStack(this.pastrySlice, 7 - bites));
         } else if (targetBlock instanceof PieBlock) {
            int bites = (Integer)state.getValue(PieBlock.BITES);
            generatedLoot.add(new ItemStack(this.pastrySlice, 4 - bites));
         }
      }

      return generatedLoot;
   }

   public MapCodec<? extends IGlobalLootModifier> codec() {
      return CODEC.get();
   }
}
