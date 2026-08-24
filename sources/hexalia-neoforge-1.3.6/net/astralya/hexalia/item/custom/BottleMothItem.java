package net.astralya.hexalia.item.custom;

import java.util.List;
import net.astralya.hexalia.component.ModComponents;
import net.astralya.hexalia.component.item.MothData;
import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.entity.custom.SilkMothEntity;
import net.astralya.hexalia.entity.custom.variant.SilkMothVariant;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class BottleMothItem extends Item {
   public BottleMothItem(Properties properties) {
      super(properties);
   }

   private static MothData ensureData(ItemStack stack) {
      MothData data = (MothData)stack.get((DataComponentType)ModComponents.MOTH.get());
      if (data == null) {
         data = new MothData("", 0);
         stack.set((DataComponentType)ModComponents.MOTH.get(), data);
      }

      return data;
   }

   public InteractionResult useOn(UseOnContext context) {
      Level level = context.getLevel();
      Player player = context.getPlayer();
      ItemStack stack = context.getItemInHand();
      BlockPos pos = context.getClickedPos();
      Direction face = context.getClickedFace();
      if (!level.isClientSide && player != null) {
         BlockPos spawnPos = pos.relative(face);
         SilkMothEntity moth = new SilkMothEntity((EntityType<? extends Animal>)ModEntities.SILK_MOTH.get(), level);
         MothData data = ensureData(stack);
         moth.setVariant(SilkMothVariant.byId(data.variantId()));
         if (!data.name().isEmpty()) {
            moth.setCustomName(Component.literal(data.name()));
         }

         moth.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0.0F, 0.0F);
         level.addFreshEntity(moth);
         level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
         stack.shrink(1);
         player.getInventory().add(new ItemStack((ItemLike)ModItems.RUSTIC_BOTTLE.get()));
         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> lines, TooltipFlag flag) {
      MothData data = ensureData(stack);
      if (!data.name().isEmpty()) {
         lines.add(Component.translatable("tooltip.hexalia.bottled_moth", new Object[]{data.name()}).withStyle(Style.EMPTY.withItalic(true).withColor(5635925)));
      }
   }
}
