package com.aetherteam.aether.loot.modifiers;

import com.aetherteam.aether.AetherTags;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public class EnchantedGrassModifier extends LootModifier {
   public static final MapCodec<EnchantedGrassModifier> CODEC = RecordCodecBuilder.mapCodec(
      instance -> LootModifier.codecStart(instance)
         .and(ItemStack.CODEC.fieldOf("item").forGetter(modifier -> modifier.item))
         .apply(instance, EnchantedGrassModifier::new)
   );
   public final ItemStack item;

   public EnchantedGrassModifier(LootItemCondition[] conditions, ItemStack item) {
      super(conditions);
      this.item = item;
   }

   public ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> lootStacks, LootContext context) {
      Vec3 originVec = (Vec3)context.getParamOrNull(LootContextParams.ORIGIN);
      if (originVec != null
         && context.getLevel().getBlockState(BlockPos.containing(originVec).below()).is(AetherTags.Blocks.ENCHANTED_GRASS)
         && context.getRandom().nextBoolean()) {
         Optional<ItemStack> itemStack = lootStacks.stream().filter(stack -> stack.is(this.item.getItem())).findFirst();
         itemStack.ifPresent(stack -> lootStacks.add(new ItemStack(stack.getItem(), 1)));
      }

      return lootStacks;
   }

   public MapCodec<? extends IGlobalLootModifier> codec() {
      return CODEC;
   }
}
