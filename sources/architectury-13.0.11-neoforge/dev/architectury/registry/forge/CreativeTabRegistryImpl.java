package dev.architectury.registry.forge;

import com.google.common.base.Suppliers;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import dev.architectury.platform.hooks.EventBusesHooks;
import dev.architectury.registry.CreativeTabOutput;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredSupplier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.Builder;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.neoforged.neoforge.common.CreativeModeTabRegistry;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;

public class CreativeTabRegistryImpl {
   private static final Logger LOGGER = LogManager.getLogger(CreativeTabRegistryImpl.class);
   private static final List<Consumer<BuildCreativeModeTabContentsEvent>> BUILD_CONTENTS_LISTENERS = new ArrayList<>();
   private static final Multimap<CreativeTabRegistryImpl.TabKey, Supplier<ItemStack>> APPENDS = MultimapBuilder.hashKeys().arrayListValues().build();

   public static void event(BuildCreativeModeTabContentsEvent event) {
      for (Consumer<BuildCreativeModeTabContentsEvent> listener : BUILD_CONTENTS_LISTENERS) {
         listener.accept(event);
      }
   }

   @Experimental
   public static CreativeModeTab create(Consumer<Builder> callback) {
      Builder builder = CreativeModeTab.builder();
      callback.accept(builder);
      return builder.build();
   }

   @Experimental
   public static DeferredSupplier<CreativeModeTab> ofBuiltin(CreativeModeTab tab) {
      ResourceLocation key = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(tab);
      if (key == null) {
         throw new IllegalArgumentException("Builtin tab %s is not registered!".formatted(tab));
      } else {
         return new DeferredSupplier<CreativeModeTab>() {
            @Override
            public ResourceLocation getRegistryId() {
               return Registries.CREATIVE_MODE_TAB.location();
            }

            @Override
            public ResourceLocation getId() {
               return BuiltInRegistries.CREATIVE_MODE_TAB.getKey(tab);
            }

            @Override
            public boolean isPresent() {
               return true;
            }

            public CreativeModeTab get() {
               return tab;
            }
         };
      }
   }

   @Experimental
   public static DeferredSupplier<CreativeModeTab> defer(ResourceLocation name) {
      return new DeferredSupplier<CreativeModeTab>() {
         @Nullable
         private CreativeModeTab tab;

         @Override
         public ResourceLocation getRegistryId() {
            return Registries.CREATIVE_MODE_TAB.location();
         }

         @Override
         public ResourceLocation getId() {
            return name;
         }

         public CreativeModeTab get() {
            this.resolve();
            if (this.tab == null) {
               throw new IllegalStateException("Creative tab %s was not registered yet!".formatted(name));
            } else {
               return this.tab;
            }
         }

         @Override
         public boolean isPresent() {
            this.resolve();
            return this.tab != null;
         }

         private void resolve() {
            if (this.tab == null) {
               this.tab = (CreativeModeTab)BuiltInRegistries.CREATIVE_MODE_TAB.get(name);
            }
         }
      };
   }

   public static void modify(DeferredSupplier<CreativeModeTab> tab, CreativeTabRegistry.ModifyTabCallback filler) {
      BUILD_CONTENTS_LISTENERS.add(event -> {
         if (tab.isPresent()) {
            if (event.getTab().equals(tab.get())) {
               filler.accept(event.getFlags(), wrapTabOutput(event), event.hasPermissions());
            }
         } else if (Objects.equals(CreativeModeTabRegistry.getName(event.getTab()), tab.getId())) {
            filler.accept(event.getFlags(), wrapTabOutput(event), event.hasPermissions());
         }
      });
   }

   private static CreativeTabOutput wrapTabOutput(BuildCreativeModeTabContentsEvent event) {
      return new CreativeTabOutput() {
         @Override
         public void acceptAfter(ItemStack after, ItemStack stack, TabVisibility visibility) {
            event.insertAfter(after, stack, visibility);
         }

         @Override
         public void acceptBefore(ItemStack before, ItemStack stack, TabVisibility visibility) {
            event.insertBefore(before, stack, visibility);
         }
      };
   }

   @Experimental
   public static void appendStack(DeferredSupplier<CreativeModeTab> tab, Supplier<ItemStack> item) {
      APPENDS.put(new CreativeTabRegistryImpl.TabKey.SupplierTabKey(tab), item);
   }

   static {
      EventBusesHooks.whenAvailable("architectury", bus -> bus.addListener(CreativeTabRegistryImpl::event));
      BUILD_CONTENTS_LISTENERS.add(event -> {
         for (Entry<CreativeTabRegistryImpl.TabKey, Collection<Supplier<ItemStack>>> keyEntry : APPENDS.asMap().entrySet()) {
            Supplier<List<ItemStack>> stacks = Suppliers.memoize(() -> keyEntry.getValue().stream().map(Supplier::get).toList());
            if (keyEntry.getKey() instanceof CreativeTabRegistryImpl.TabKey.SupplierTabKey supplierTabKey) {
               if (Objects.equals(CreativeModeTabRegistry.getName(event.getTab()), supplierTabKey.supplier().getId())) {
                  for (ItemStack stack : stacks.get()) {
                     event.accept(stack, TabVisibility.PARENT_AND_SEARCH_TABS);
                  }
               }
            } else if (keyEntry.getKey() instanceof CreativeTabRegistryImpl.TabKey.DirectTabKey directTabKey && event.getTab().equals(directTabKey.tab())) {
               for (ItemStack stack : stacks.get()) {
                  event.accept(stack, TabVisibility.PARENT_AND_SEARCH_TABS);
               }
            }
         }
      });
   }

   private interface TabKey {
      public record DirectTabKey(CreativeModeTab tab) implements CreativeTabRegistryImpl.TabKey {
         @Override
         public boolean equals(Object o) {
            if (this == o) {
               return true;
            } else {
               return o instanceof CreativeTabRegistryImpl.TabKey.DirectTabKey that ? this.tab == that.tab : false;
            }
         }

         @Override
         public int hashCode() {
            return System.identityHashCode(this.tab);
         }
      }

      public record SupplierTabKey(DeferredSupplier<CreativeModeTab> supplier) implements CreativeTabRegistryImpl.TabKey {
         @Override
         public boolean equals(Object o) {
            if (this == o) {
               return true;
            } else {
               return o instanceof CreativeTabRegistryImpl.TabKey.SupplierTabKey that ? Objects.equals(this.supplier.getId(), that.supplier.getId()) : false;
            }
         }

         @Override
         public int hashCode() {
            return Objects.hash(this.supplier.getId());
         }
      }
   }
}
