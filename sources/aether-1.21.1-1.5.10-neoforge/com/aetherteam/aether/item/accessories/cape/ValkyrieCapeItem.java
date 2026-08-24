package com.aetherteam.aether.item.accessories.cape;

import com.aetherteam.aether.item.accessories.abilities.SlowFallAccessory;
import io.wispforest.accessories.api.events.extra.AllowWalkingOnSnow;
import io.wispforest.accessories.api.slot.SlotReference;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class ValkyrieCapeItem extends CapeItem implements SlowFallAccessory, AllowWalkingOnSnow {
   public ValkyrieCapeItem(Properties properties) {
      super("valkyrie_cape", properties);
   }

   public void tick(ItemStack stack, SlotReference reference) {
      this.handleSlowFall(reference.entity());
   }

   public TriState allowWalkingOnSnow(ItemStack stack, SlotReference reference) {
      return TriState.TRUE;
   }
}
