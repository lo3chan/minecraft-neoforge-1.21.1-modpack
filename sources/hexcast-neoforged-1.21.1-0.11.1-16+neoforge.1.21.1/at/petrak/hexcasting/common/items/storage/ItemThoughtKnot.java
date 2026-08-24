package at.petrak.hexcasting.common.items.storage;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.utils.NBTHelper;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import org.jetbrains.annotations.Nullable;

public class ItemThoughtKnot extends Item implements IotaHolderItem {
   public static final ResourceLocation WRITTEN_PRED = HexAPI.modLoc("written");
   public static final String TAG_DATA = "data";

   public ItemThoughtKnot(Properties properties) {
      super(properties);
   }

   @Nullable
   @Override
   public CompoundTag readIotaTag(ItemStack stack) {
      return NBTHelper.getCompound(stack, "data");
   }

   @Override
   public boolean writeable(ItemStack stack) {
      return !NBTHelper.contains(stack, "data");
   }

   @Override
   public boolean canWrite(ItemStack stack, @Nullable Iota iota) {
      return iota != null && !NBTHelper.contains(stack, "data");
   }

   @Override
   public void writeDatum(ItemStack stack, @Nullable Iota iota) {
      if (iota != null) {
         NBTHelper.putCompound(stack, "data", IotaType.serialize(iota));
      }
   }

   public void appendHoverText(ItemStack pStack, TooltipContext pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
      IotaHolderItem.appendHoverText(this, pStack, pTooltipComponents, pIsAdvanced);
   }
}
