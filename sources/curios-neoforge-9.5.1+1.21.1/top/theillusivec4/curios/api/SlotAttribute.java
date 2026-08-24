package top.theillusivec4.curios.api;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Direct;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.TooltipFlag;

public class SlotAttribute extends Attribute {
   private static final Map<String, Holder<? extends Attribute>> SLOT_ATTRIBUTES = new HashMap<>();
   private final String identifier;

   public static Holder<Attribute> getOrCreate(String id) {
      return SLOT_ATTRIBUTES.computeIfAbsent(id, k -> new Direct(new SlotAttribute(id)));
   }

   protected SlotAttribute(String identifier) {
      super("curios.identifier." + identifier, 0.0);
      this.identifier = identifier;
   }

   public String getIdentifier() {
      return this.identifier;
   }

   @Nonnull
   public MutableComponent toComponent(@Nonnull AttributeModifier modif, @Nonnull TooltipFlag flag) {
      double value = modif.amount();
      String key = value > 0.0 ? "curios.modifiers.slots.plus" : "curios.modifiers.slots.take";
      if (value > 1.0) {
         key = key + ".multiple";
      }

      ChatFormatting color = this.getStyle(value > 0.0);
      Component attrDesc = Component.translatable(this.getDescriptionId());
      Component valueComp = this.toValueComponent(modif.operation(), value, flag);
      MutableComponent comp = Component.translatable(key, new Object[]{valueComp, attrDesc}).withStyle(color);
      return comp.append(this.getDebugInfo(modif, flag));
   }
}
