package net.nycto_team.overpacked.registry;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nycto_team.overpacked.util.Utils;

public class ModTabs {
   public static final DeferredRegister<CreativeModeTab> reg = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "overpacked");
   public static final Supplier<CreativeModeTab> tab = reg(
      "overpacked",
      CreativeModeTab.builder()
         .icon(() -> new ItemStack((ItemLike)ModItems.giant_backpack.get()))
         .title(Component.translatable("overpacked"))
         .displayItems((params, output) -> {
            output.accept((ItemLike)ModItems.giant_backpack.get());
            output.accept((ItemLike)ModItems.white_giant_backpack.get());
            output.accept((ItemLike)ModItems.orange_giant_backpack.get());
            output.accept((ItemLike)ModItems.magenta_giant_backpack.get());
            output.accept((ItemLike)ModItems.light_blue_giant_backpack.get());
            output.accept((ItemLike)ModItems.yellow_giant_backpack.get());
            output.accept((ItemLike)ModItems.lime_giant_backpack.get());
            output.accept((ItemLike)ModItems.pink_giant_backpack.get());
            output.accept((ItemLike)ModItems.gray_giant_backpack.get());
            output.accept((ItemLike)ModItems.light_gray_giant_backpack.get());
            output.accept((ItemLike)ModItems.cyan_giant_backpack.get());
            output.accept((ItemLike)ModItems.purple_giant_backpack.get());
            output.accept((ItemLike)ModItems.blue_giant_backpack.get());
            output.accept((ItemLike)ModItems.brown_giant_backpack.get());
            output.accept((ItemLike)ModItems.green_giant_backpack.get());
            output.accept((ItemLike)ModItems.red_giant_backpack.get());
            output.accept((ItemLike)ModItems.black_giant_backpack.get());
            if (Utils.dye_depot) {
               output.accept((ItemLike)ModDDItems.maroon_giant_backpack.get());
               output.accept((ItemLike)ModDDItems.rose_giant_backpack.get());
               output.accept((ItemLike)ModDDItems.coral_giant_backpack.get());
               output.accept((ItemLike)ModDDItems.indigo_giant_backpack.get());
               output.accept((ItemLike)ModDDItems.navy_giant_backpack.get());
               output.accept((ItemLike)ModDDItems.slate_giant_backpack.get());
               output.accept((ItemLike)ModDDItems.olive_giant_backpack.get());
               output.accept((ItemLike)ModDDItems.amber_giant_backpack.get());
               output.accept((ItemLike)ModDDItems.beige_giant_backpack.get());
               output.accept((ItemLike)ModDDItems.teal_giant_backpack.get());
               output.accept((ItemLike)ModDDItems.mint_giant_backpack.get());
               output.accept((ItemLike)ModDDItems.aqua_giant_backpack.get());
               output.accept((ItemLike)ModDDItems.verdant_giant_backpack.get());
               output.accept((ItemLike)ModDDItems.forest_giant_backpack.get());
               output.accept((ItemLike)ModDDItems.ginger_giant_backpack.get());
               output.accept((ItemLike)ModDDItems.tan_giant_backpack.get());
            }

            output.accept((ItemLike)ModItems.backpack_pocket.get());
         })
         .build()
   );

   public static void Register(IEventBus bus) {
      reg.register(bus);
   }

   private static Supplier<CreativeModeTab> reg(String name, CreativeModeTab value) {
      return reg.register(name, () -> value);
   }
}
