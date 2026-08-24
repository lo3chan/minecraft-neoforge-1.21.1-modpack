package net.blay09.mods.balm.neoforge.item;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.item.BalmItems;
import net.blay09.mods.balm.common.NamespaceResolver;
import net.blay09.mods.balm.common.StaticNamespaceResolver;
import net.blay09.mods.balm.neoforge.DeferredRegisters;
import net.blay09.mods.balm.neoforge.ModBusEventRegisters;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

public record NeoForgeBalmItems(NamespaceResolver namespaceResolver) implements BalmItems {
   private static final Set<ResourceLocation> managedCreativeTabs = new HashSet<>();

   @Override
   public DeferredObject<Item> registerItem(Function<ResourceLocation, Item> supplier, ResourceLocation identifier, @Nullable ResourceLocation creativeTab) {
      DeferredRegister<Item> register = DeferredRegisters.get(Registries.ITEM, identifier.getNamespace());
      DeferredHolder<Item, Item> registryObject = register.register(identifier.getPath(), supplier);
      if (creativeTab != null) {
         this.getActiveRegistrations().creativeTabContents.put(creativeTab, (Supplier<ItemLike[]>)() -> new ItemLike[]{(ItemLike)registryObject.get()});
      }

      return new DeferredObject<>(identifier, registryObject, registryObject::isBound);
   }

   @Override
   public DeferredObject<CreativeModeTab> registerCreativeModeTab(Supplier<ItemStack> iconSupplier, ResourceLocation identifier) {
      managedCreativeTabs.add(identifier);
      DeferredRegister<CreativeModeTab> register = DeferredRegisters.get(Registries.CREATIVE_MODE_TAB, identifier.getNamespace());
      DeferredHolder<CreativeModeTab, CreativeModeTab> registryObject = register.register(
         identifier.getPath(),
         () -> {
            MutableComponent displayName = Component.translatable("itemGroup." + identifier.toString().replace(':', '.'));
            NeoForgeBalmItems.Registrations registrations = this.getActiveRegistrations();
            return CreativeModeTab.builder()
               .title(displayName)
               .icon(iconSupplier)
               .displayItems((enabledFeatures, entries) -> registrations.buildCreativeTabContents(identifier, entries))
               .build();
         }
      );
      return new DeferredObject<>(identifier, registryObject, registryObject::isBound);
   }

   @Override
   public void addToCreativeModeTab(ResourceLocation tabIdentifier, Supplier<ItemLike[]> itemsSupplier) {
      this.getActiveRegistrations().creativeTabContents.put(tabIdentifier, itemsSupplier);
   }

   @Override
   public void setCreativeModeTabSorting(ResourceLocation tabIdentifier, Comparator<ItemLike> comparator) {
      this.getActiveRegistrations().creativeTabSorting.put(tabIdentifier, comparator);
   }

   private NeoForgeBalmItems.Registrations getActiveRegistrations() {
      return ModBusEventRegisters.getRegistrations(this.namespaceResolver.getDefaultNamespace(), NeoForgeBalmItems.Registrations.class);
   }

   @Override
   public BalmItems scoped(String modId) {
      return new NeoForgeBalmItems(new StaticNamespaceResolver(modId));
   }

   public static class Registrations {
      public final Multimap<ResourceLocation, Supplier<ItemLike[]>> creativeTabContents = ArrayListMultimap.create();
      private final Map<ResourceLocation, Comparator<ItemLike>> creativeTabSorting = new HashMap<>();

      public void buildCreativeTabContents(ResourceLocation tabIdentifier, Output entries) {
         Collection<Supplier<ItemLike[]>> itemStackArraySuppliers = this.creativeTabContents.get(tabIdentifier);
         Comparator<ItemLike> comparator = this.creativeTabSorting.get(tabIdentifier);
         if (!itemStackArraySuppliers.isEmpty()) {
            itemStackArraySuppliers.forEach(it -> {
               List<ItemLike> itemStacks = Arrays.asList(it.get());

               for (ItemLike itemStack : comparator != null ? itemStacks.stream().sorted(comparator).toList() : itemStacks) {
                  entries.accept(itemStack);
               }
            });
         }
      }

      @SubscribeEvent
      public void buildOtherCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
         ResourceLocation creativeModeTabId = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(event.getTab());
         if (creativeModeTabId != null && !NeoForgeBalmItems.managedCreativeTabs.contains(creativeModeTabId)) {
            this.buildCreativeTabContents(creativeModeTabId, event);
         }
      }
   }
}
