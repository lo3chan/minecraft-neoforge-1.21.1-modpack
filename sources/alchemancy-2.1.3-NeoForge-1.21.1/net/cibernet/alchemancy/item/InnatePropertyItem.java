package net.cibernet.alchemancy.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.cibernet.alchemancy.item.components.InfusedPropertiesComponent;
import net.cibernet.alchemancy.item.components.PropertyDataComponent;
import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.properties.data.modifiers.PropertyModifierType;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

public class InnatePropertyItem extends Item {
   public final int useTime;
   public final UseAnim useAnim;
   @Nullable
   public final InnatePropertyItem.Tooltip tooltip;
   public final Ingredient repairMaterial;
   public static final ArrayList<Item> TOGGLEABLE_ITEMS = new ArrayList<>();

   private InnatePropertyItem(
      Properties properties, int useTime, UseAnim useAnim, boolean toggleable, @Nullable InnatePropertyItem.Tooltip tooltip, Ingredient repairMaterial
   ) {
      super(properties);
      this.useTime = useTime;
      this.useAnim = useAnim;
      this.tooltip = tooltip;
      this.repairMaterial = repairMaterial;
      if (toggleable) {
         TOGGLEABLE_ITEMS.add(this);
      }
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      if (this.tooltip != null) {
         this.tooltip.apply(stack, context, tooltipComponents, tooltipFlag);
      }
   }

   public UseAnim getUseAnimation(ItemStack stack) {
      return this.useAnim;
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return this.useTime == 0 ? super.getUseDuration(stack, entity) : this.useTime;
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      if (this.useTime > 0) {
         ItemStack itemstack = player.getItemInHand(hand);
         player.startUsingItem(hand);
         return InteractionResultHolder.consume(itemstack);
      } else {
         return super.use(level, player, hand);
      }
   }

   public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
      return super.isValidRepairItem(stack, repairCandidate);
   }

   public static class Builder {
      private final ArrayList<Holder<Property>> properties = new ArrayList<>();
      private int useTime = 0;
      private UseAnim useAnim = UseAnim.NONE;
      private int stacksTo = 64;
      private int infusionSlots = -1;
      private int durability = -1;
      private InnatePropertyItem.Tooltip tooltip = null;
      private Ingredient repairMaterial = Ingredient.EMPTY;
      private final Map<Holder<Property>, Map<Holder<PropertyModifierType<?>>, Object>> modifiers = new HashMap<>();
      private PropertyDataComponent propertyData = new PropertyDataComponent(new HashMap<>());

      @SafeVarargs
      public final InnatePropertyItem.Builder withProperties(Holder<Property>... properties) {
         this.properties.addAll(List.of(properties));
         return this;
      }

      public InnatePropertyItem.Builder use(int useTime, UseAnim useAnim) {
         this.useTime = useTime;
         this.useAnim = useAnim;
         return this;
      }

      public InnatePropertyItem.Builder infusionSlots(int slots) {
         this.infusionSlots = slots;
         return this;
      }

      public InnatePropertyItem.Builder stacksTo(int stack) {
         this.stacksTo = stack;
         return this;
      }

      public InnatePropertyItem.Builder durability(int durability) {
         this.durability = durability;
         return this;
      }

      public InnatePropertyItem.Builder durability(int durability, Ingredient repairMaterial) {
         this.repairMaterial = repairMaterial;
         return this.durability(durability);
      }

      public InnatePropertyItem.Builder tooltip(InnatePropertyItem.Tooltip tooltip) {
         this.tooltip = tooltip;
         return this;
      }

      public <T> InnatePropertyItem.Builder addModifier(
         Holder<Property> property, DeferredHolder<PropertyModifierType<?>, PropertyModifierType<T>> modifier, T value
      ) {
         if (!this.modifiers.containsKey(property)) {
            this.modifiers.put(property, new HashMap<>());
         }

         this.modifiers.get(property).put(modifier, value);
         return this;
      }

      public <T, P extends Property & IDataHolder<T>> InnatePropertyItem.Builder addData(DeferredHolder<Property, P> property, CompoundTag value) {
         PropertyDataComponent.Mutable data = new PropertyDataComponent.Mutable(this.propertyData);
         data.setDataNbt(property, value);
         this.propertyData = data.toImmutable();
         return this;
      }

      public InnatePropertyItem.Builder toggleable(boolean enabledByDefault) {
         this.withProperties(AlchemancyProperties.TOGGLEABLE);
         if (!enabledByDefault) {
            this.addData(AlchemancyProperties.TOGGLEABLE, new CompoundTag() {
               {
                  this.putBoolean("active", false);
               }
            });
         }

         return this;
      }

      public InnatePropertyItem.Builder auxiliary(boolean ignoreInfused) {
         this.withProperties(AlchemancyProperties.AUXILIARY);
         if (ignoreInfused) {
            this.addModifier(AlchemancyProperties.AUXILIARY, AlchemancyProperties.Modifiers.IGNORE_INFUSED, true);
         }

         return this;
      }

      public InnatePropertyItem build() {
         return this.build(new Properties());
      }

      public InnatePropertyItem build(Properties itemProperties) {
         if (this.infusionSlots >= 0) {
            itemProperties.component(AlchemancyItems.Components.INFUSION_SLOTS, this.infusionSlots);
         }

         if (this.durability >= 0) {
            itemProperties.durability(this.durability);
         }

         return new InnatePropertyItem(
            itemProperties.stacksTo(this.stacksTo)
               .component(AlchemancyItems.Components.INNATE_PROPERTIES, new InfusedPropertiesComponent(this.properties))
               .component(AlchemancyItems.Components.PROPERTY_MODIFIERS, new PropertyModifierComponent(this.modifiers))
               .component(AlchemancyItems.Components.PROPERTY_DATA, this.propertyData),
            this.useTime,
            this.useAnim,
            this.properties.contains(AlchemancyProperties.TOGGLEABLE),
            this.tooltip,
            this.repairMaterial
         );
      }
   }

   public interface Tooltip {
      void apply(ItemStack var1, TooltipContext var2, List<Component> var3, TooltipFlag var4);
   }
}
