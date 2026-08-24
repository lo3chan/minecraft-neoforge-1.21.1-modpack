package vectorwing.farmersdelight.common.registry;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
   public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "farmersdelight");
   public static final Supplier<CreativeModeTab> TAB_FARMERS_DELIGHT = CREATIVE_TABS.register(
      "farmersdelight",
      () -> CreativeModeTab.builder()
         .title(Component.translatable("itemGroup.farmersdelight"))
         .icon(() -> new ItemStack((ItemLike)ModBlocks.STOVE.get()))
         .displayItems((parameters, output) -> ModItems.CREATIVE_TAB_ITEMS.forEach(item -> output.accept((ItemLike)item.get())))
         .build()
   );
}
