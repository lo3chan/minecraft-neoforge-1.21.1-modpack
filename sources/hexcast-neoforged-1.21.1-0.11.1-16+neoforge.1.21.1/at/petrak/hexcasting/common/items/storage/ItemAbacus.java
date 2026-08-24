package at.petrak.hexcasting.common.items.storage;

import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.lib.HexSounds;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ItemAbacus extends Item implements IotaHolderItem {
   public static final String TAG_VALUE = "value";

   public ItemAbacus(Properties pProperties) {
      super(pProperties);
   }

   @Nullable
   @Override
   public CompoundTag readIotaTag(ItemStack stack) {
      DoubleIota datum = new DoubleIota(NBTHelper.getDouble(stack, "value"));
      return IotaType.serialize(datum);
   }

   @Override
   public boolean writeable(ItemStack stack) {
      return false;
   }

   @Override
   public boolean canWrite(ItemStack stack, Iota datum) {
      return false;
   }

   @Override
   public void writeDatum(ItemStack stack, Iota datum) {
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (player.isShiftKeyDown()) {
         double oldNum = NBTHelper.getDouble(stack, "value");
         NBTHelper.remove(stack, "value");
         player.playSound(HexSounds.ABACUS_SHAKE, 1.0F, 1.0F);
         String key = "hexcasting.tooltip.abacus.reset";
         if (oldNum == 69.0) {
            key = key + ".nice";
         }

         player.displayClientMessage(Component.translatable(key), true);
         return InteractionResultHolder.sidedSuccess(stack, world.isClientSide);
      } else {
         return InteractionResultHolder.pass(stack);
      }
   }

   public void appendHoverText(ItemStack pStack, TooltipContext pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
      IotaHolderItem.appendHoverText(this, pStack, pTooltipComponents, pIsAdvanced);
   }
}
