package at.petrak.hexcasting.api.pigment;

import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryAccess.Frozen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record FrozenPigment(ItemStack item, UUID owner) {
   public static final String TAG_STACK = "stack";
   public static final String TAG_ITEM_ID = "item_id";
   public static final String TAG_OWNER = "owner";
   public static final Supplier<FrozenPigment> DEFAULT = () -> new FrozenPigment(new ItemStack(HexItems.DEFAULT_PIGMENT), Util.NIL_UUID);

   public CompoundTag serializeToNBT() {
      CompoundTag out = new CompoundTag();
      out.put("stack", HexUtils.serializeToNBT(this.item));
      out.putString("item_id", BuiltInRegistries.ITEM.getKey(this.item.getItem()).toString());
      out.putUUID("owner", this.owner);
      return out;
   }

   public static FrozenPigment fromNBT(CompoundTag tag) {
      if (tag.isEmpty()) {
         return DEFAULT.get();
      } else {
         try {
            ItemStack stack;
            if (tag.contains("item_id")) {
               Item item = (Item)BuiltInRegistries.ITEM.get(ResourceLocation.parse(tag.getString("item_id")));
               stack = new ItemStack(item);
            } else {
               CompoundTag stackTag = tag.getCompound("stack");
               Frozen access = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
               stack = ItemStack.parseOptional(access, stackTag);
            }

            if (stack.isEmpty()) {
               return DEFAULT.get();
            } else {
               UUID uuid = tag.getUUID("owner");
               return new FrozenPigment(stack, uuid);
            }
         } catch (NullPointerException var4) {
            return DEFAULT.get();
         }
      }
   }

   public ColorProvider getColorProvider() {
      return IXplatAbstractions.INSTANCE.getColorProvider(this);
   }
}
