package at.petrak.hexcasting.common.items.storage;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.item.VariantItem;
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

public class ItemFocus extends Item implements IotaHolderItem, VariantItem {
   public static final ResourceLocation OVERLAY_PRED = HexAPI.modLoc("overlay_layer");
   public static final ResourceLocation VARIANT_PRED = HexAPI.modLoc("variant");
   public static final int NUM_VARIANTS = 8;
   public static final String TAG_DATA = "data";
   public static final String TAG_SEALED = "sealed";

   public ItemFocus(Properties pProperties) {
      super(pProperties);
   }

   @Nullable
   @Override
   public CompoundTag readIotaTag(ItemStack stack) {
      return NBTHelper.getCompound(stack, "data");
   }

   public String getDescriptionId(ItemStack stack) {
      return super.getDescriptionId(stack) + (NBTHelper.getBoolean(stack, "sealed") ? ".sealed" : "");
   }

   @Nullable
   @Override
   public Iota emptyIota(ItemStack stack) {
      return new NullIota();
   }

   @Override
   public boolean writeable(ItemStack stack) {
      return !NBTHelper.getBoolean(stack, "sealed");
   }

   @Override
   public boolean canWrite(ItemStack stack, Iota datum) {
      return datum == null || !NBTHelper.getBoolean(stack, "sealed");
   }

   @Override
   public void writeDatum(ItemStack stack, Iota datum) {
      if (datum == null) {
         NBTHelper.remove(stack, "data");
         NBTHelper.remove(stack, "sealed");
      } else if (!isSealed(stack)) {
         NBTHelper.put(stack, "data", IotaType.serialize(datum));
      }
   }

   public void appendHoverText(ItemStack pStack, TooltipContext pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
      IotaHolderItem.appendHoverText(this, pStack, pTooltipComponents, pIsAdvanced);
   }

   public static boolean isSealed(ItemStack stack) {
      return NBTHelper.getBoolean(stack, "sealed");
   }

   public static void seal(ItemStack stack) {
      NBTHelper.putBoolean(stack, "sealed", true);
   }

   @Override
   public int numVariants() {
      return 8;
   }

   @Override
   public void setVariant(ItemStack stack, int variant) {
      if (!isSealed(stack)) {
         NBTHelper.putInt(stack, "variant", this.clampVariant(variant));
      }
   }
}
