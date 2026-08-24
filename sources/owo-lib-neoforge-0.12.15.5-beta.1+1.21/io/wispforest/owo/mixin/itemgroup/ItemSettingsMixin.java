package io.wispforest.owo.mixin.itemgroup;

import io.wispforest.owo.itemgroup.ItemGroupReference;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.OwoItemSettingsExtension;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.Item.Properties;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({Properties.class})
public class ItemSettingsMixin implements OwoItemSettingsExtension {
   private Supplier<OwoItemGroup> owo$group = null;
   private int owo$tab = 0;
   private BiConsumer<Item, Output> owo$stackGenerator = null;
   private boolean owo$trackUsageStat = false;

   @Override
   public Properties group(ItemGroupReference ref) {
      this.owo$group = ref.groupSup();
      this.owo$tab = ref.tab();
      return (Properties)this;
   }

   @Override
   public Properties group(OwoItemGroup group) {
      this.owo$group = () -> group;
      return (Properties)this;
   }

   @Override
   public Properties group(Supplier<OwoItemGroup> groupSupplier) {
      this.owo$group = groupSupplier;
      return (Properties)this;
   }

   @Override
   public OwoItemGroup group() {
      return this.owo$group != null ? this.owo$group.get() : null;
   }

   @Override
   public Supplier<OwoItemGroup> groupSupplier() {
      return this.owo$group;
   }

   @Override
   public Properties tab(int tab) {
      this.owo$tab = tab;
      return (Properties)this;
   }

   @Override
   public int tab() {
      return this.owo$tab;
   }

   @Override
   public Properties stackGenerator(BiConsumer<Item, Output> generator) {
      this.owo$stackGenerator = generator;
      return (Properties)this;
   }

   @Override
   public BiConsumer<Item, Output> stackGenerator() {
      return this.owo$stackGenerator;
   }

   @Override
   public Properties trackUsageStat() {
      this.owo$trackUsageStat = true;
      return (Properties)this;
   }

   @Override
   public boolean shouldTrackUsageStat() {
      return this.owo$trackUsageStat;
   }
}
