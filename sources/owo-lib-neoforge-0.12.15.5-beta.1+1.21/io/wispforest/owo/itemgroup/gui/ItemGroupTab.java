package io.wispforest.owo.itemgroup.gui;

import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Output;

public record ItemGroupTab(Icon icon, Component name, ItemGroupTab.ContentSupplier contentSupplier, ResourceLocation texture, boolean primary)
   implements OwoItemGroup.ButtonDefinition {
   public static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath("owo", "textures/gui/tabs.png");

   @Override
   public Component tooltip() {
      return this.name;
   }

   @FunctionalInterface
   public interface ContentSupplier {
      void addItems(ItemDisplayParameters var1, Output var2);
   }
}
