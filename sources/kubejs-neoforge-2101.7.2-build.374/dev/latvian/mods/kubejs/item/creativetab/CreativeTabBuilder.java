package dev.latvian.mods.kubejs.item.creativetab;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

@ReturnsSelf
public class CreativeTabBuilder extends BuilderBase<CreativeModeTab> {
   public transient CreativeTabIconSupplier icon = CreativeTabIconSupplier.DEFAULT;
   public transient CreativeTabContentSupplier content = CreativeTabContentSupplier.DEFAULT;

   public CreativeTabBuilder(ResourceLocation i) {
      super(i);
   }

   public CreativeModeTab createObject() {
      return CreativeModeTab.builder()
         .title((Component)(this.displayName == null ? Component.translatable(this.getBuilderTranslationKey()) : this.displayName))
         .icon(new CreativeTabIconSupplier.Wrapper(this.icon))
         .displayItems(new CreativeTabContentSupplier.Wrapper(this.content))
         .build();
   }

   public CreativeTabBuilder icon(CreativeTabIconSupplier icon) {
      this.icon = icon;
      return this;
   }

   public CreativeTabBuilder content(CreativeTabContentSupplier content) {
      this.content = content;
      return this;
   }
}
