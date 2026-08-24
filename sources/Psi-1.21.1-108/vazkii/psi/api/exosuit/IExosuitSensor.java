package vazkii.psi.api.exosuit;

import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface IExosuitSensor {
   String getEventType(ItemStack var1);

   @OnlyIn(Dist.CLIENT)
   int getColor(ItemStack var1);
}
