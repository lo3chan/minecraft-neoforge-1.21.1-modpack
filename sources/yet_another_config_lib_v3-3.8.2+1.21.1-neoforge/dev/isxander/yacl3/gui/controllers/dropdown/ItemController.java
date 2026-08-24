package dev.isxander.yacl3.gui.controllers.dropdown;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.utils.ItemRegistryHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ItemController extends AbstractDropdownController<Item> {
   public ItemController(Option<Item> option) {
      super(option);
   }

   @Override
   public String getString() {
      return BuiltInRegistries.ITEM.getKey(this.option.pendingValue()).toString();
   }

   @Override
   public void setFromString(String value) {
      this.option.requestSet(ItemRegistryHelper.getItemFromName(value, this.option.pendingValue()));
   }

   @Override
   public Component formatValue() {
      return Component.literal(this.getString());
   }

   @Override
   public boolean isValueValid(String value) {
      return ItemRegistryHelper.isRegisteredItem(value);
   }

   @Override
   protected String getValidValue(String value, int offset) {
      return ItemRegistryHelper.getMatchingItemResourceLocations(value)
         .skip(offset)
         .findFirst()
         .<String>map(ResourceLocation::toString)
         .orElseGet(this::getString);
   }

   @Override
   public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
      return new ItemControllerElement(this, screen, widgetDimension);
   }
}
