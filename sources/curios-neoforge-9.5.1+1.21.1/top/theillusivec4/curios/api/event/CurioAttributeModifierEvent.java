package top.theillusivec4.curios.api.event;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import java.util.Collection;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

public class CurioAttributeModifierEvent extends Event {
   private final ItemStack stack;
   private final SlotContext slotContext;
   private final ResourceLocation id;
   private final Multimap<Holder<Attribute>, AttributeModifier> originalModifiers;
   private Multimap<Holder<Attribute>, AttributeModifier> unmodifiableModifiers;
   @Nullable
   private Multimap<Holder<Attribute>, AttributeModifier> modifiableModifiers;

   public CurioAttributeModifierEvent(ItemStack stack, SlotContext slotContext, ResourceLocation id, Multimap<Holder<Attribute>, AttributeModifier> modifiers) {
      this.stack = stack;
      this.slotContext = slotContext;
      this.unmodifiableModifiers = this.originalModifiers = modifiers;
      this.id = id;
   }

   public Multimap<Holder<Attribute>, AttributeModifier> getModifiers() {
      return this.unmodifiableModifiers;
   }

   public Multimap<Holder<Attribute>, AttributeModifier> getOriginalModifiers() {
      return this.originalModifiers;
   }

   private Multimap<Holder<Attribute>, AttributeModifier> getModifiableMap() {
      if (this.modifiableModifiers == null) {
         this.modifiableModifiers = LinkedHashMultimap.create(this.originalModifiers);
         this.unmodifiableModifiers = Multimaps.unmodifiableMultimap(this.modifiableModifiers);
      }

      return this.modifiableModifiers;
   }

   public boolean addModifier(Holder<Attribute> attribute, AttributeModifier modifier) {
      return this.getModifiableMap().put(attribute, modifier);
   }

   public boolean removeModifier(Holder<Attribute> attribute, AttributeModifier modifier) {
      return this.getModifiableMap().remove(attribute, modifier);
   }

   public Collection<AttributeModifier> removeAttribute(Holder<Attribute> attribute) {
      return this.getModifiableMap().removeAll(attribute);
   }

   public void clearModifiers() {
      this.getModifiableMap().clear();
   }

   public SlotContext getSlotContext() {
      return this.slotContext;
   }

   public ItemStack getItemStack() {
      return this.stack;
   }

   public ResourceLocation getId() {
      return this.id;
   }
}
