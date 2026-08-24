package net.blay09.mods.inventoryessentials;

import java.util.HashSet;
import java.util.Set;
import net.blay09.mods.inventoryessentials.data.IgnoredData;
import net.blay09.mods.inventoryessentials.mixin.AbstractContainerMenuAccessor;
import net.blay09.mods.inventoryessentials.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.inventoryessentials.mixin.CreativeModeInventoryScreenAccessor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

public class InventoryEssentialsIgnores {
   private static final Set<String> ignoredScreenClasses = new HashSet<>();
   private static final Set<String> ignoredMenuClasses = new HashSet<>();
   private static final Set<String> ignored = new HashSet<>();
   private static final Set<ResourceLocation> ignoredMenuTypes = new HashSet<>();

   public static boolean shouldIgnoreScreen(Screen screen) {
      if (!(screen instanceof AbstractContainerScreenAccessor)) {
         return true;
      } else {
         AbstractContainerScreen<?> containerScreen = (AbstractContainerScreen<?>)screen;
         if (InventoryEssentialsExtensions.shouldIgnoreScreen(containerScreen)) {
            return true;
         } else if (ignoredScreenClasses.contains(screen.getClass().getName())) {
            return true;
         } else {
            AbstractContainerMenu menu = containerScreen.getMenu();
            if (ignoredMenuClasses.contains(menu.getClass().getName())) {
               return true;
            } else {
               MenuType<?> menuType = ((AbstractContainerMenuAccessor)menu).balm$getMenuType();
               ResourceLocation typeId = menuType != null ? BuiltInRegistries.MENU.getKey(menuType) : null;
               return typeId != null && ignoredMenuTypes.contains(typeId);
            }
         }
      }
   }

   public static boolean shouldIgnoreSlot(AbstractContainerScreen<?> screen, @Nullable Slot slot) {
      if (slot == null) {
         return true;
      } else if (InventoryEssentialsExtensions.shouldIgnoreSlot(screen, slot)) {
         return true;
      } else if (ignored.contains(slot.getClass().getName())) {
         return true;
      } else if (slot instanceof ResultSlot || slot.container instanceof ResultContainer) {
         return true;
      } else {
         return !(screen instanceof CreativeModeInventoryScreenAccessor creativeAccessor)
            ? false
            : !(slot.container instanceof Inventory) && slot.container == creativeAccessor.getCONTAINER();
      }
   }

   public static void addIgnoredMenuType(ResourceLocation menuId) {
      ignoredMenuTypes.add(menuId);
   }

   public static void addIgnoredMenuClass(String menuClass) {
      ignoredMenuClasses.add(menuClass);
   }

   public static void addIgnoredScreenClass(String screenClass) {
      ignoredScreenClasses.add(screenClass);
   }

   public static void addIgnoredSlotClass(String slotClass) {
      ignored.add(slotClass);
   }

   public static void addIgnoredData(IgnoredData ignoredData) {
      ignoredData.ignoredMenuClasses.forEach(InventoryEssentialsIgnores::addIgnoredMenuClass);
      ignoredData.ignoredMenuTypes.stream().<ResourceLocation>map(ResourceLocation::parse).forEach(InventoryEssentialsIgnores::addIgnoredMenuType);
      ignoredData.ignoredScreenClasses.forEach(InventoryEssentialsIgnores::addIgnoredScreenClass);
      ignoredData.ignoredSlotClasses.forEach(InventoryEssentialsIgnores::addIgnoredSlotClass);
   }
}
