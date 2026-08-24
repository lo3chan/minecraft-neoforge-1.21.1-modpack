package net.joefoxe.hexerei.item;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.DisplayItemsGenerator;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItemGroup {
   public static final DeferredRegister<CreativeModeTab> ITEM_GROUP = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "hexerei");
   private static final List<DeferredHolder<Item, Item>> BLACKLIST = List.of(
      ModItems.BROOM_KEYCHAIN_BASE, ModItems.CROW_BLANK_AMULET_TRINKET, ModItems.CROW_BLANK_AMULET_TRINKET_FRAME
   );
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> HEXEREI_GROUP = ITEM_GROUP.register(
      "hexerei_tab",
      () -> CreativeModeTab.builder()
         .icon(() -> ((Item)ModItems.MIXING_CAULDRON.get()).getDefaultInstance())
         .title(Component.translatable("itemGroup.hexereiModTab"))
         .displayItems(new DisplayItemsGenerator() {
            public void accept(ItemDisplayParameters itemDisplayParameters, Output output) {
               ModItems.ITEMS.getEntries().forEach(entry -> {
                  ItemStack stack = ((Item)entry.get()).getDefaultInstance();
                  if (!ModItemGroup.isBlacklist((Item)entry.get())) {
                     output.accept(stack);
                  }

                  if (stack.is((Item)ModItems.INFUSED_FABRIC_BLOCK.get())) {
                     for (DyeColor color : DyeColor.values()) {
                        if (!color.getName().equals("white")) {
                           ItemStack newStack = ((Item)ModItems.INFUSED_FABRIC_BLOCK.get()).getDefaultInstance();
                           CompoundTag tag = ((CustomData)newStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
                           tag.putString("color", color.getName());
                           newStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                           output.accept(newStack);
                        }
                     }
                  } else if (stack.is((Item)ModItems.INFUSED_FABRIC_CARPET.get())) {
                     for (DyeColor colorx : DyeColor.values()) {
                        if (!colorx.getName().equals("white")) {
                           ItemStack newStack = ((Item)ModItems.INFUSED_FABRIC_CARPET.get()).getDefaultInstance();
                           CompoundTag tag = ((CustomData)newStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
                           tag.putString("color", colorx.getName());
                           newStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                           output.accept(newStack);
                        }
                     }
                  }
               });
            }
         })
         .build()
   );

   private static boolean isBlacklist(Item item) {
      AtomicBoolean blacklisted = new AtomicBoolean(false);
      BLACKLIST.forEach(registryObject -> {
         if (item == registryObject.get()) {
            blacklisted.set(true);
         }
      });
      return blacklisted.get();
   }
}
