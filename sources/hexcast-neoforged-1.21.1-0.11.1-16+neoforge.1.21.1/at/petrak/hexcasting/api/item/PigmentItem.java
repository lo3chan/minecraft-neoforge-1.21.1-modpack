package at.petrak.hexcasting.api.item;

import at.petrak.hexcasting.api.pigment.ColorProvider;
import java.util.UUID;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus.OverrideOnly;

@OverrideOnly
public interface PigmentItem {
   ColorProvider provideColor(ItemStack var1, UUID var2);
}
