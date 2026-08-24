package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ItemTooltipCallback {
   EventInvoker<ItemTooltipCallback> EVENT = EventInvoker.lookup(ItemTooltipCallback.class);

   void onItemTooltip(ItemStack var1, List<Component> var2, TooltipContext var3, @Nullable Player var4, TooltipFlag var5);
}
