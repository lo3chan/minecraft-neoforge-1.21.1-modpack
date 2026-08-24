package at.petrak.hexcasting.common.items.storage;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.client.gui.PatternTooltipComponent;
import at.petrak.hexcasting.common.misc.PatternTooltip;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class ItemSlate extends BlockItem implements IotaHolderItem {
   public static final ResourceLocation WRITTEN_PRED = HexAPI.modLoc("written");

   public ItemSlate(Block pBlock, Properties pProperties) {
      super(pBlock, pProperties);
   }

   public Component getName(ItemStack pStack) {
      String key = "block.hexcasting.slate." + (hasPattern(pStack) ? "written" : "blank");
      return Component.translatable(key);
   }

   public static boolean hasPattern(ItemStack stack) {
      CompoundTag bet = NBTHelper.getCompound(stack, "BlockEntityTag");
      return bet == null ? false : bet.contains("pattern", 10) && !bet.getCompound("pattern").isEmpty();
   }

   public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
      if (!hasPattern(stack)) {
         NBTHelper.remove(stack, "BlockEntityTag");
      }

      return false;
   }

   public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
      if (!hasPattern(pStack)) {
         NBTHelper.remove(pStack, "BlockEntityTag");
      }
   }

   @Nullable
   @Override
   public CompoundTag readIotaTag(ItemStack stack) {
      CompoundTag bet = NBTHelper.getCompound(stack, "BlockEntityTag");
      if (bet != null && bet.contains("pattern", 10)) {
         CompoundTag patTag = bet.getCompound("pattern");
         if (patTag.isEmpty()) {
            return null;
         } else {
            CompoundTag out = new CompoundTag();
            out.putString("hexcasting:type", "hexcasting:pattern");
            out.put("hexcasting:data", patTag);
            return out;
         }
      } else {
         return null;
      }
   }

   @Override
   public boolean writeable(ItemStack stack) {
      return true;
   }

   @Override
   public boolean canWrite(ItemStack stack, Iota datum) {
      return datum instanceof PatternIota || datum == null;
   }

   @Override
   public void writeDatum(ItemStack stack, Iota datum) {
      if (this.canWrite(stack, datum)) {
         if (datum == null) {
            CompoundTag beTag = NBTHelper.getCompound(stack, "BlockEntityTag");
            if (beTag != null) {
               beTag.remove("pattern");
            }

            if (beTag != null && !beTag.isEmpty()) {
               NBTHelper.putCompound(stack, "BlockEntityTag", beTag);
            } else {
               NBTHelper.remove(stack, "BlockEntityTag");
            }
         } else if (datum instanceof PatternIota pat) {
            CompoundTag beTagx = NBTHelper.getCompound(stack, "BlockEntityTag");
            if (beTagx == null) {
               beTagx = new CompoundTag();
            }

            beTagx.put("pattern", pat.getPattern().serializeToNBT());
            NBTHelper.putCompound(stack, "BlockEntityTag", beTagx);
         }
      }
   }

   public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
      CompoundTag bet = NBTHelper.getCompound(stack, "BlockEntityTag");
      if (bet != null && bet.contains("pattern", 10)) {
         CompoundTag patTag = bet.getCompound("pattern");
         if (!patTag.isEmpty()) {
            HexPattern pattern = HexPattern.fromNBT(patTag);
            return Optional.of(new PatternTooltip(pattern, PatternTooltipComponent.SLATE_BG));
         }
      }

      return Optional.empty();
   }
}
