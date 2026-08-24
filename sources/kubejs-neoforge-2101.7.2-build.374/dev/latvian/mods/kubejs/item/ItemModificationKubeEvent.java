package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.component.ItemComponentFunctions;
import dev.latvian.mods.kubejs.core.DiggerItemKJS;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.TickDuration;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;
import org.jetbrains.annotations.Nullable;

@Info("Invoked after all items are registered to modify them.\n")
public class ItemModificationKubeEvent implements KubeEvent {
   @Info("Modifies items matching the given ingredient.\n\n**NOTE**: tag ingredients are not supported at this time.\n")
   public void modify(ItemPredicate in, Consumer<ItemModificationKubeEvent.ItemModifications> c) {
      in.kjs$getItemTypes().stream().map(ItemModificationKubeEvent.ItemModifications::new).forEach(c);
   }

   @RemapPrefixForJS("kjs$")
   public record ItemModifications(Item item) implements ItemComponentFunctions, ItemBehaviorFunctions {
      @HideFromJS
      public static final Reference2IntOpenHashMap<Item> BURN_TIME_OVERRIDES = new Reference2IntOpenHashMap();

      @Override
      public DataComponentMap kjs$getComponentMap() {
         return this.item.components();
      }

      @HideFromJS
      public <T> ItemComponentFunctions kjs$override(DataComponentType<T> type, @Nullable T value) {
         this.item.kjs$overrideComponent(type, value);
         return this;
      }

      public void setBurnTime(TickDuration i) {
         BURN_TIME_OVERRIDES.put(this.item, i.intTicks());
      }

      public void setCraftingRemainder(Item item) {
         this.item.kjs$setCraftingRemainder(item);
      }

      public void setTier(Consumer<MutableToolTier> builder) {
         if (this.item instanceof TieredItem tiered) {
            Tier oldTier = tiered.tier;
            MutableToolTier tier = (MutableToolTier)Util.make(new MutableToolTier(tiered.tier), builder);
            tiered.tier = tier;
            Builder modifiers = ItemAttributeModifiers.builder();

            for (Entry entry : this.kjs$getAttributeModifiers().modifiers()) {
               if (entry.matches(Attributes.ATTACK_DAMAGE, Item.BASE_ATTACK_DAMAGE_ID)) {
                  double base = entry.modifier().amount() - oldTier.getAttackDamageBonus();
                  modifiers.add(
                     entry.attribute(),
                     new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, base + tier.getAttackDamageBonus(), Operation.ADD_VALUE),
                     entry.slot()
                  );
               } else {
                  modifiers.add(entry.attribute(), entry.modifier(), entry.slot());
               }
            }

            this.kjs$setAttributeModifiers(modifiers.build());
            this.kjs$setMaxDamage(tier.getUses());
            if (tiered instanceof DiggerItemKJS dig) {
               this.kjs$setTool(tier.createToolProperties(dig.kjs$getMineableTag()));
            }
         } else {
            throw new IllegalArgumentException("Item is not a tool/tiered item!");
         }
      }

      public void setNameKey(String key) {
         this.item.kjs$setNameKey(key);
      }

      public void disableRepair() {
         this.item.kjs$setCanRepair(false);
      }

      @Override
      public ItemBehavior kjs$getOrCreateBehavior() {
         ItemBehavior behavior = this.item.kjs$getItemBehavior();
         if (behavior == null) {
            behavior = new ItemBehavior();
            this.item.kjs$setItemBehavior(behavior);
         }

         return behavior;
      }
   }
}
