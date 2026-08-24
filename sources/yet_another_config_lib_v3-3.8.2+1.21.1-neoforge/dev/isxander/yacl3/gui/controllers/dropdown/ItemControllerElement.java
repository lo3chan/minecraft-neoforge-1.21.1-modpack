package dev.isxander.yacl3.gui.controllers.dropdown;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.utils.ItemRegistryHelper;
import dev.isxander.yacl3.gui.utils.MiscUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class ItemControllerElement extends AbstractDropdownControllerElement<Item, ResourceLocation> {
   private final ItemController itemController;
   protected Item currentItem = null;
   protected Map<ResourceLocation, Item> matchingItems = new HashMap<>();

   public ItemControllerElement(ItemController control, YACLScreen screen, Dimension<Integer> dim) {
      super(control, screen, dim);
      this.itemController = control;
   }

   @Override
   protected void drawValueText(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      Dimension<Integer> oldDimension = this.getDimension();
      this.setDimension(this.getDimension().withWidth(this.getDimension().width() - this.getDecorationPadding()));
      super.drawValueText(graphics, mouseX, mouseY, delta);
      this.setDimension(oldDimension);
      if (this.currentItem != null) {
         graphics.renderFakeItem(
            new ItemStack(this.currentItem), this.getDimension().xLimit() - this.getXPadding() - this.getDecorationPadding() + 2, this.getDimension().y() + 2
         );
      }
   }

   @Override
   public List<ResourceLocation> computeMatchingValues() {
      List<ResourceLocation> identifiers = ItemRegistryHelper.getMatchingItemResourceLocations(this.inputField).toList();
      this.currentItem = ItemRegistryHelper.getItemFromName(this.inputField, null);

      for (ResourceLocation identifier : identifiers) {
         this.matchingItems.put(identifier, MiscUtil.getFromRegistry(BuiltInRegistries.ITEM, identifier));
      }

      return identifiers;
   }

   protected void renderDropdownEntry(GuiGraphics graphics, Dimension<Integer> entryDimension, ResourceLocation identifier) {
      super.renderDropdownEntry(graphics, entryDimension, identifier);
      graphics.renderFakeItem(new ItemStack((ItemLike)this.matchingItems.get(identifier)), entryDimension.xLimit() - 2, entryDimension.y() + 1);
   }

   public String getString(ResourceLocation identifier) {
      return identifier.toString();
   }

   @Override
   protected int getDecorationPadding() {
      return 16;
   }

   @Override
   protected int getDropdownEntryPadding() {
      return 4;
   }

   @Override
   protected int getControlWidth() {
      return super.getControlWidth() + this.getDecorationPadding();
   }

   @Override
   protected Component getValueText() {
      if (this.inputField.isEmpty() || this.itemController == null) {
         return super.getValueText();
      } else {
         return (Component)(this.inputFieldFocused ? Component.literal(this.inputField) : this.itemController.option().pendingValue().getDescription());
      }
   }
}
