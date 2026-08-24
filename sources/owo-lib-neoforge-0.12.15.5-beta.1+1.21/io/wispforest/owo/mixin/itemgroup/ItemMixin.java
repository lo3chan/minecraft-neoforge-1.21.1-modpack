package io.wispforest.owo.mixin.itemgroup;

import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.util.pond.OwoItemExtensions;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.Item.Properties;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Item.class})
public class ItemMixin implements OwoItemExtensions {
   @Nullable
   protected Supplier<? extends CreativeModeTab> owo$group = () -> null;
   @Unique
   private int owo$tab = 0;
   @Unique
   private BiConsumer<Item, Output> owo$stackGenerator;
   @Unique
   private boolean owo$trackUsageStat = false;

   @Inject(
      method = {"<init>(Lnet/minecraft/world/item/Item$Properties;)V"},
      at = {@At("TAIL")}
   )
   private void grabTab(Properties settings, CallbackInfo ci) {
      this.owo$tab = settings.tab();
      this.owo$stackGenerator = settings.stackGenerator();
      this.owo$group = settings.groupSupplier();
      this.owo$trackUsageStat = settings.shouldTrackUsageStat();
   }

   @Override
   public int owo$tab() {
      return this.owo$tab;
   }

   @Override
   public BiConsumer<Item, Output> owo$stackGenerator() {
      return this.owo$stackGenerator != null ? this.owo$stackGenerator : OwoItemGroup.DEFAULT_STACK_GENERATOR;
   }

   @Override
   public void owo$setGroup(Supplier<CreativeModeTab> group) {
      this.owo$group = group;
   }

   @Nullable
   @Override
   public CreativeModeTab owo$group() {
      return this.owo$group != null ? this.owo$group.get() : null;
   }

   @Override
   public boolean owo$shouldTrackUsageStat() {
      return this.owo$trackUsageStat;
   }
}
