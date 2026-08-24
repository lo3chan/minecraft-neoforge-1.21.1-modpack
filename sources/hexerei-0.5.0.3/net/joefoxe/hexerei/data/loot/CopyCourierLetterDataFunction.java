package net.joefoxe.hexerei.data.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.tileentity.CourierLetterTile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyCourierLetterDataFunction extends LootItemConditionalFunction {
   public static final MapCodec<CopyCourierLetterDataFunction> CODEC = RecordCodecBuilder.mapCodec(
      p_298065_ -> commonFields(p_298065_).apply(p_298065_, CopyCourierLetterDataFunction::new)
   );

   protected CopyCourierLetterDataFunction(List<LootItemCondition> predicates) {
      super(predicates);
   }

   protected ItemStack run(ItemStack stack, LootContext context) {
      BlockEntity blockEntity = (BlockEntity)context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
      if (blockEntity instanceof CourierLetterTile courierLetterTile) {
         CompoundTag tag = courierLetterTile.saveData(new CompoundTag(), Hexerei.DynamicRegistries.get());
         if (!tag.isEmpty()) {
            BlockItem.setBlockEntityData(stack, courierLetterTile.getType(), courierLetterTile.save(new CompoundTag(), Hexerei.DynamicRegistries.get()));
         }

         return stack;
      } else {
         return stack;
      }
   }

   public LootItemFunctionType<CopyCourierLetterDataFunction> getType() {
      return (LootItemFunctionType<CopyCourierLetterDataFunction>)ModItems.COPY_LETTER_DATA.get();
   }
}
