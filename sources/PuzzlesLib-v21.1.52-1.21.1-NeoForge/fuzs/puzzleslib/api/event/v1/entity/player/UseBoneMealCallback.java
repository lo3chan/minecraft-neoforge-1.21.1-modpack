package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface UseBoneMealCallback {
   EventInvoker<UseBoneMealCallback> EVENT = EventInvoker.lookup(UseBoneMealCallback.class);

   EventResult onUseBoneMeal(Level var1, BlockPos var2, BlockState var3, ItemStack var4);
}
