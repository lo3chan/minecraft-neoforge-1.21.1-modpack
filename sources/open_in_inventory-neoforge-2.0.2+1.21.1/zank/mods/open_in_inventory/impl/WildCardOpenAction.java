package zank.mods.open_in_inventory.impl;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import zank.mods.open_in_inventory.api.OpenAction;

public record WildCardOpenAction(Item item) implements OpenAction {
   public static final Codec<WildCardOpenAction> CODEC = BuiltInRegistries.ITEM.byNameCodec().xmap(WildCardOpenAction::new, WildCardOpenAction::item);

   @Override
   public ItemStack stack() {
      return this.item.getDefaultInstance();
   }

   @Override
   public boolean sneak() {
      return false;
   }

   @Override
   public boolean match(ItemStack stack) {
      return stack.is(this.item);
   }
}
