package net.mehvahdjukaar.amendments.events.dispenser;

import net.mehvahdjukaar.moonlight.api.util.DispenserHelper.AdditionalDispenserBehavior;
import net.minecraft.world.item.Item;

public abstract class CauldronDispenserBehavior extends AdditionalDispenserBehavior {
   protected CauldronDispenserBehavior(Item item) {
      super(item);
   }
}
