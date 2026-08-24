package net.joefoxe.hexerei.data.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.tileentity.CofferTile;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyCofferDataFunction extends LootItemConditionalFunction {
   public static final MapCodec<CopyCofferDataFunction> CODEC = RecordCodecBuilder.mapCodec(
      p_298065_ -> commonFields(p_298065_).apply(p_298065_, CopyCofferDataFunction::new)
   );

   protected CopyCofferDataFunction(List<LootItemCondition> predicates) {
      super(predicates);
   }

   protected ItemStack run(ItemStack stack, LootContext context) {
      BlockEntity blockEntity = (BlockEntity)context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
      if (blockEntity instanceof CofferTile coffer) {
         CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
         if (coffer.cofferId != null) {
            tag.putUUID("CofferId", coffer.cofferId);
         }

         tag.putInt("ButtonToggled", coffer.buttonToggled);
         stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
         stack.set(DataComponents.DYED_COLOR, new DyedItemColor(coffer.dyeColor, true));
         Component customName = coffer.getCustomName();
         if (customName != null && !customName.getString().isEmpty()) {
            stack.set(DataComponents.CUSTOM_NAME, customName);
         }

         return stack;
      } else {
         return stack;
      }
   }

   public LootItemFunctionType<CopyCofferDataFunction> getType() {
      return (LootItemFunctionType<CopyCofferDataFunction>)ModItems.COPY_COFFER_DATA.get();
   }
}
