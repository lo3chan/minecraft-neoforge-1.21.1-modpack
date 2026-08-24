package fuzs.puzzleslib.impl.item;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.item.v2.CreativeModeTabConfigurator;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.CreativeModeTab.Builder;
import net.minecraft.world.item.CreativeModeTab.DisplayItemsGenerator;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.jetbrains.annotations.Nullable;

@Deprecated
public final class CreativeModeTabConfiguratorImpl implements CreativeModeTabConfigurator {
   private static final Item[] POTION_ITEMS = new Item[]{Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION, Items.TIPPED_ARROW};
   private final ResourceLocation resourceLocation;
   @Nullable
   private Supplier<ItemStack> icon;
   @Nullable
   private Supplier<ItemStack[]> icons;
   private DisplayItemsGenerator displayItemsGenerator = (itemDisplayParameters, output) -> {};
   private boolean hasSearchBar;
   private boolean appendEnchantmentsAndPotions;

   public CreativeModeTabConfiguratorImpl(ResourceLocation resourceLocation) {
      this.resourceLocation = resourceLocation;
   }

   public ResourceLocation getResourceLocation() {
      return this.resourceLocation;
   }

   @Nullable
   public Supplier<ItemStack[]> getIcons() {
      return this.icons;
   }

   public boolean isHasSearchBar() {
      return this.hasSearchBar;
   }

   @Override
   public CreativeModeTabConfigurator icon(Supplier<ItemStack> icon) {
      this.icon = icon;
      return this;
   }

   @Override
   public CreativeModeTabConfigurator icons(Supplier<ItemStack[]> icons) {
      this.icons = icons;
      return this;
   }

   @Override
   public CreativeModeTabConfigurator displayItems(DisplayItemsGenerator generator) {
      this.displayItemsGenerator = generator;
      return this;
   }

   @Override
   public CreativeModeTabConfigurator withSearchBar() {
      this.hasSearchBar = true;
      return this;
   }

   @Override
   public CreativeModeTabConfigurator appendEnchantmentsAndPotions() {
      this.appendEnchantmentsAndPotions = true;
      return this;
   }

   public void configure(Builder builder) {
      String translationKey = "itemGroup.%s.%s".formatted(this.resourceLocation.getNamespace(), this.resourceLocation.getPath());
      builder.title(Component.translatable(translationKey));
      if (this.icon != null) {
         builder.icon(this.icon);
      } else {
         Objects.requireNonNull(this.icons, "both icon suppliers are null");
         if (ModLoaderEnvironment.INSTANCE.getModLoader().isFabricLike()) {
            builder.icon(() -> {
               ItemStack[] icons = this.icons.get();
               Preconditions.checkState(icons.length > 0, "icons is empty");
               return icons[0];
            });
         }
      }

      if (this.appendEnchantmentsAndPotions) {
         builder.displayItems((itemDisplayParameters, output) -> {
            this.displayItemsGenerator.accept(itemDisplayParameters, output);
            appendAllEnchantments(this.resourceLocation.getNamespace(), itemDisplayParameters.holders(), output::accept);
            appendAllPotions(this.resourceLocation.getNamespace(), itemDisplayParameters.holders(), output::accept);
         });
      } else {
         builder.displayItems(this.displayItemsGenerator);
      }
   }

   private static void appendAllEnchantments(String namespace, Provider holders, Consumer<ItemStack> itemStacks) {
      Comparator<Reference<Enchantment>> comparator = Comparator.comparing(entry -> entry.key().location().getPath());
      holders.lookup(Registries.ENCHANTMENT)
         .stream()
         .<Reference>flatMap(HolderLookup::listElements)
         .filter(entry -> entry.key().location().getNamespace().equals(namespace))
         .sorted(comparator)
         .forEach(
            holder -> itemStacks.accept(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(holder, ((Enchantment)holder.value()).getMaxLevel())))
         );
   }

   private static void appendAllPotions(String namespace, Provider holders, Consumer<ItemStack> itemStacks) {
      List<Reference<Potion>> potions = holders.lookup(Registries.POTION)
         .stream()
         .<Reference<Potion>>flatMap(HolderLookup::listElements)
         .filter(entry -> entry.key().location().getNamespace().equals(namespace))
         .filter(holder -> !((Potion)holder.value()).getEffects().isEmpty())
         .sorted(Comparator.comparing(holder -> (MobEffectInstance)((Potion)holder.value()).getEffects().get(0)))
         .toList();

      for (Item item : POTION_ITEMS) {
         for (Reference<Potion> potion : potions) {
            itemStacks.accept(PotionContents.createItemStack(item, potion));
         }
      }
   }
}
