package dev.latvian.mods.kubejs.component;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@RemapPrefixForJS("kjs$")
@ReturnsSelf
public interface AttributeModifierFunctions {
   ItemAttributeModifiers kjs$getAttributeModifiers();

   default boolean kjs$hasAttributeModifier(Holder<Attribute> attribute, ResourceLocation id) {
      for (Entry entry : this.kjs$getAttributeModifiers().modifiers()) {
         if (entry.matches(attribute, id)) {
            return true;
         }
      }

      return false;
   }

   @Nullable
   default AttributeModifier kjs$getAttributeModifier(Holder<Attribute> attribute, ResourceLocation id) {
      for (Entry entry : this.kjs$getAttributeModifiers().modifiers()) {
         if (entry.matches(attribute, id)) {
            return entry.modifier();
         }
      }

      return null;
   }

   @HideFromJS
   void kjs$setAttributeModifiers(ItemAttributeModifiers modifiers);

   default void kjs$addAttributeModifier(Holder<Attribute> attribute, AttributeModifier mod, EquipmentSlotGroup slot) {
      this.kjs$setAttributeModifiers(this.kjs$getAttributeModifiers().withModifierAdded(attribute, mod, slot));
   }

   @NonExtendable
   default void kjs$setAttributeModifiers(List<Entry> modifiers) {
      this.kjs$setAttributeModifiers(new ItemAttributeModifiers(modifiers, false));
   }

   @NonExtendable
   default void kjs$setAttributeModifiersWithTooltip(List<Entry> modifiers) {
      this.kjs$setAttributeModifiers(new ItemAttributeModifiers(modifiers, true));
   }

   @Info("Sets the attack speed of this item to the given value, **removing** all other modifiers to attack speed.\nNote that players have a default attack speed of 4.0, so this modifier is added on top of that.\n(Example: Swords have an attack speed of -2.4, leading to a total value of 1.6 without any other changes.)\n")
   default void kjs$setAttackSpeed(double speed) {
      ItemAttributeModifiers oldMods = this.kjs$getAttributeModifiers();
      ArrayList<Entry> list = new ArrayList<>(oldMods.modifiers().size());

      for (Entry entry : oldMods.modifiers()) {
         if (!entry.attribute().equals(Attributes.ATTACK_SPEED)) {
            list.add(entry);
         }
      }

      list.add(new Entry(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, speed, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND));
      this.kjs$setAttributeModifiers(new ItemAttributeModifiers(list, oldMods.showInTooltip()));
   }

   @Info("Sets the attack damage of this item to the given value, **removing** all other modifiers to attack damage.\nNote that since players have a default attack damage of 1.0, total damage will be (dmg + 1.0) before other modifiers.\n(In practice, this simply means that most weapons have this value set to 1 less than what you might think.)\n")
   default void kjs$setAttackDamage(double dmg) {
      ItemAttributeModifiers oldMods = this.kjs$getAttributeModifiers();
      ArrayList<Entry> list = new ArrayList<>(oldMods.modifiers().size());

      for (Entry entry : oldMods.modifiers()) {
         if (!entry.attribute().equals(Attributes.ATTACK_DAMAGE)) {
            list.add(entry);
         }
      }

      list.add(new Entry(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, dmg, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND));
      this.kjs$setAttributeModifiers(new ItemAttributeModifiers(list, oldMods.showInTooltip()));
   }

   default double kjs$getAttackDamage() {
      double base = this.kjs$getBaseAttackDamage();
      double sum = base;

      for (Entry entry : this.kjs$getAttributeModifiers().modifiers()) {
         if (!entry.matches(Attributes.ATTACK_DAMAGE, Item.BASE_ATTACK_DAMAGE_ID)) {
            AttributeModifier mod = entry.modifier();
            double d1 = mod.amount();

            sum += switch (mod.operation()) {
               case ADD_VALUE -> d1;
               case ADD_MULTIPLIED_BASE -> d1 * base;
               case ADD_MULTIPLIED_TOTAL -> d1 * sum;
               default -> throw new MatchException(null, null);
            };
         }
      }

      return sum;
   }

   default double kjs$getAttackSpeed() {
      double base = this.kjs$getBaseAttackSpeed();
      double sum = base;

      for (Entry entry : this.kjs$getAttributeModifiers().modifiers()) {
         if (!entry.matches(Attributes.ATTACK_SPEED, Item.BASE_ATTACK_SPEED_ID)) {
            AttributeModifier mod = entry.modifier();
            double d1 = mod.amount();

            sum += switch (mod.operation()) {
               case ADD_VALUE -> d1;
               case ADD_MULTIPLIED_BASE -> d1 * base;
               case ADD_MULTIPLIED_TOTAL -> d1 * sum;
               default -> throw new MatchException(null, null);
            };
         }
      }

      return sum;
   }

   @Info("Overrides the *base* attack speed of this item to be the given value, keeping other modifiers intact.\nNote that players have a default attack speed of 4.0, so this modifier is added on top of that.\n")
   default void kjs$setBaseAttackSpeed(double speed) {
      this.kjs$addAttributeModifier(
         Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, speed, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND
      );
   }

   @Info("Overrides the *base* attack damage of this item to be the given value, keeping other modifiers intact.\nNote that since players have a default attack damage of 1.0, total damage will be (dmg + 1.0) before other modifiers.\n")
   default void kjs$setBaseAttackDamage(double dmg) {
      this.kjs$addAttributeModifier(
         Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, dmg, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND
      );
   }

   default double kjs$getBaseAttackDamage() {
      for (Entry modifier : this.kjs$getAttributeModifiers().modifiers()) {
         if (modifier.matches(Attributes.ATTACK_DAMAGE, Item.BASE_ATTACK_DAMAGE_ID)) {
            return modifier.modifier().amount();
         }
      }

      return 0.0;
   }

   default double kjs$getBaseAttackSpeed() {
      for (Entry modifier : this.kjs$getAttributeModifiers().modifiers()) {
         if (modifier.matches(Attributes.ATTACK_SPEED, Item.BASE_ATTACK_SPEED_ID)) {
            return modifier.modifier().amount();
         }
      }

      return 0.0;
   }
}
