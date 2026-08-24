package io.github.razordevs.deep_aether.datagen.loot.modifiers;

import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.razordevs.deep_aether.DeepAether;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedEntry.Wrapper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public class DAHalloweenLootModifier extends LootModifier {
   public static final Supplier<MapCodec<DAHalloweenLootModifier>> CODEC = Suppliers.memoize(
      () -> RecordCodecBuilder.mapCodec(
         inst -> codecStart(inst)
            .and(Wrapper.codec(ItemStack.CODEC).listOf().fieldOf("items").forGetter(m -> m.items))
            .and(Codec.INT.fieldOf("totalWeight").forGetter(m -> m.totalWeight))
            .and(Codec.FLOAT.fieldOf("chanceToSpawn").forGetter(m -> m.chance))
            .apply(inst, DAHalloweenLootModifier::new)
      )
   );
   public final List<Wrapper<ItemStack>> items;
   public final int totalWeight;
   public final float chance;

   protected DAHalloweenLootModifier(LootItemCondition[] conditionsIn, List<Wrapper<ItemStack>> items, int totalWeight, float chance) {
      super(conditionsIn);
      this.items = items.stream().map(wrapper -> WeightedEntry.wrap(((ItemStack)wrapper.data()).copy(), wrapper.getWeight().asInt())).toList();
      this.totalWeight = totalWeight;
      this.chance = chance;
   }

   protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
      if (context.getLevel().dimension() == AetherDimensions.AETHER_LEVEL && DeepAether.IS_HALLOWEEN && context.getRandom().nextFloat() > this.chance) {
         for (Wrapper<ItemStack> stack : this.items) {
            ((ItemStack)stack.data()).setCount(context.getRandom().nextInt(5));
            generatedLoot.add((ItemStack)stack.data());
         }
      }

      return generatedLoot;
   }

   public MapCodec<? extends IGlobalLootModifier> codec() {
      return CODEC.get();
   }
}
