package fuzs.puzzleslib.api.client.gui.v2.tooltip;

import fuzs.puzzleslib.api.client.event.v1.gui.ItemTooltipCallback;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public abstract class ItemTooltipRegistry<T> {
   public static final ItemTooltipRegistry<Block> BLOCK = new ItemTooltipRegistry<Block>() {
      @Nullable
      Block getFromItemStack(ItemStack itemStack) {
         return itemStack.getItem() instanceof BlockItem blockItem ? blockItem.getBlock() : null;
      }

      @Override
      Registry<Block> getRegistry() {
         return BuiltInRegistries.BLOCK;
      }
   };
   public static final ItemTooltipRegistry<Item> ITEM = new ItemTooltipRegistry<Item>() {
      Item getFromItemStack(ItemStack itemStack) {
         return itemStack.getItem();
      }

      @Override
      Registry<Item> getRegistry() {
         return BuiltInRegistries.ITEM;
      }
   };

   ItemTooltipRegistry() {
   }

   @Nullable
   abstract T getFromItemStack(ItemStack var1);

   abstract Registry<T> getRegistry();

   public <V extends T> void registerItemTooltip(V value, Component component) {
      this.registerItemTooltip(value, component);
   }

   public <V extends T> void registerItemTooltip(Class<V> clazz, Component component) {
      this.registerItemTooltip(clazz, component);
   }

   public void registerItemTooltip(TagKey<T> tagKey, Component component) {
      this.registerItemTooltip(tagKey, component);
   }

   public <V extends T> void registerItemTooltip(V value, Component... components) {
      this.registerItemTooltipLines(value, valueX -> Arrays.asList(components));
   }

   public <V extends T> void registerItemTooltip(Class<V> clazz, Component... components) {
      this.registerItemTooltipLines(clazz, valueX -> Arrays.asList(components));
   }

   public void registerItemTooltip(TagKey<T> tagKey, Component... components) {
      this.registerItemTooltipLines(tagKey, valueX -> Arrays.asList(components));
   }

   public <V extends T> void registerItemTooltip(V value, Function<V, Component> componentExtractor) {
      this.registerItemTooltipLines(value, valueX -> Collections.singletonList(componentExtractor.apply((V)valueX)));
   }

   public <V extends T> void registerItemTooltip(Class<V> clazz, Function<V, Component> componentExtractor) {
      this.registerItemTooltipLines(clazz, valueX -> Collections.singletonList(componentExtractor.apply(valueX)));
   }

   public void registerItemTooltip(TagKey<T> tagKey, Function<T, Component> componentExtractor) {
      this.registerItemTooltipLines(tagKey, valueX -> Collections.singletonList(componentExtractor.apply(valueX)));
   }

   public <V extends T> void registerItemTooltipLines(V value, Function<V, List<Component>> componentExtractor) {
      this.registerItemTooltip(
         (Predicate<ItemStack>)(itemStack -> this.getFromItemStack(itemStack) == value),
         (ItemTooltipRegistry.Provider)((itemStack, context, tooltipFlag, player, tooltipLineConsumer) -> componentExtractor.apply(value)
            .forEach(tooltipLineConsumer))
      );
   }

   public <V extends T> void registerItemTooltipLines(Class<V> clazz, Function<V, List<Component>> componentExtractor) {
      this.registerItemTooltip(clazz, (ItemTooltipRegistry.Provider)((itemStack, context, tooltipFlag, player, tooltipLineConsumer) -> {
         T value = this.getFromItemStack(itemStack);
         Objects.requireNonNull(value, "value from item stack " + itemStack + " is null");
         componentExtractor.apply((V)value).forEach(tooltipLineConsumer);
      }));
   }

   public void registerItemTooltipLines(TagKey<T> tagKey, Function<T, List<Component>> componentExtractor) {
      this.registerItemTooltip(tagKey, (ItemTooltipRegistry.Provider)((itemStack, context, tooltipFlag, player, tooltipLineConsumer) -> {
         T value = this.getFromItemStack(itemStack);
         Objects.requireNonNull(value, "value from item stack " + itemStack + " is null");
         componentExtractor.apply(value).forEach(tooltipLineConsumer);
      }));
   }

   public <V extends T> void registerItemTooltip(Class<V> clazz, ItemTooltipRegistry.Provider provider) {
      for (T value : this.getRegistry()) {
         if (clazz.isInstance(value)) {
            this.registerItemTooltip((Predicate<ItemStack>)(itemStack -> this.getFromItemStack(itemStack) == value), provider);
         }
      }
   }

   public void registerItemTooltip(TagKey<T> tagKey, ItemTooltipRegistry.Provider provider) {
      this.registerItemTooltip((Predicate<ItemStack>)(itemStack -> {
         T value = this.getFromItemStack(itemStack);
         return value != null && this.getRegistry().wrapAsHolder(value).is(tagKey);
      }), provider);
   }

   public void registerItemTooltip(Predicate<ItemStack> itemStackFilter, ItemTooltipRegistry.Provider provider) {
      ItemTooltipCallback.EVENT
         .register(
            (itemStack, tooltipLines, tooltipContext, player, tooltipFlag) -> {
               if (tooltipContext != TooltipContext.EMPTY && tooltipContext.registries() != null && itemStackFilter.test(itemStack)) {
                  int originalSize = tooltipLines.size();
                  provider.appendHoverText(
                     itemStack,
                     tooltipContext,
                     tooltipFlag,
                     player,
                     component -> {
                        if (component != null) {
                           tooltipLines.addAll(
                              tooltipLines.isEmpty() ? 0 : 1 + tooltipLines.size() - originalSize, ClientComponentSplitter.splitTooltipComponents(component)
                           );
                        }
                     }
                  );
               }
            }
         );
   }

   @FunctionalInterface
   public interface Provider {
      void appendHoverText(ItemStack var1, TooltipContext var2, TooltipFlag var3, @Nullable Player var4, Consumer<Component> var5);
   }
}
