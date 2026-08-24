package at.petrak.hexcasting.api.item;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.client.ClientTickCounter;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

public interface IotaHolderItem {
   String TAG_OVERRIDE_VISUALLY = "VisualOverride";

   @Nullable
   CompoundTag readIotaTag(ItemStack var1);

   @Nullable
   default Iota readIota(ItemStack stack, ServerLevel world) {
      if (stack.getItem() instanceof IotaHolderItem dh) {
         CompoundTag tag = dh.readIotaTag(stack);
         return tag != null ? IotaType.deserialize(tag, world) : null;
      } else {
         throw new IllegalArgumentException("stack's item must be an IotaHolderItem but was " + stack.getItem());
      }
   }

   @Nullable
   default Iota emptyIota(ItemStack stack) {
      return null;
   }

   default int getColor(ItemStack stack) {
      if (NBTHelper.hasString(stack, "VisualOverride")) {
         String override = NBTHelper.getString(stack, "VisualOverride");
         if (override != null && ResourceLocation.tryParse(override) != null) {
            ResourceLocation key = ResourceLocation.parse(override);
            if (HexIotaTypes.REGISTRY.containsKey(key)) {
               IotaType<?> iotaType = (IotaType<?>)HexIotaTypes.REGISTRY.get(key);
               if (iotaType != null) {
                  return iotaType.color();
               }
            }
         }

         return 0xFF000000 | Mth.hsvToRgb(ClientTickCounter.getTotal() * 2.0F % 360.0F / 360.0F, 0.75F, 1.0F);
      } else {
         CompoundTag tag = this.readIotaTag(stack);
         return tag == null ? -524040 : IotaType.getColor(tag);
      }
   }

   boolean writeable(ItemStack var1);

   boolean canWrite(ItemStack var1, @Nullable Iota var2);

   void writeDatum(ItemStack var1, @Nullable Iota var2);

   static void appendHoverText(IotaHolderItem self, ItemStack stack, List<Component> components, TooltipFlag flag) {
      CompoundTag datumTag = self.readIotaTag(stack);
      if (datumTag != null) {
         Component cmp = IotaType.getDisplay(datumTag);
         components.add(Component.translatable("hexcasting.spelldata.onitem", new Object[]{cmp}));
         if (flag.isAdvanced()) {
            components.add(Component.literal("").append(NbtUtils.toPrettyComponent(datumTag)));
         }
      } else if (NBTHelper.hasString(stack, "VisualOverride")) {
         components.add(
            Component.translatable(
               "hexcasting.spelldata.onitem", new Object[]{Component.translatable("hexcasting.spelldata.anything").withStyle(ChatFormatting.LIGHT_PURPLE)}
            )
         );
      }
   }
}
