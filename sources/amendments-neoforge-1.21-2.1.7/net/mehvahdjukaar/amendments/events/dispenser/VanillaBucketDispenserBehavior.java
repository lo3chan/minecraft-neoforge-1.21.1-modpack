package net.mehvahdjukaar.amendments.events.dispenser;

import net.mehvahdjukaar.moonlight.api.util.DispenserHelper.AdditionalDispenserBehavior;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class VanillaBucketDispenserBehavior extends AdditionalDispenserBehavior {
   protected VanillaBucketDispenserBehavior(Item item) {
      super(item);
   }

   protected InteractionResultHolder<ItemStack> customBehavior(BlockSource blockSource, ItemStack itemStack) {
      return null;
   }
}
