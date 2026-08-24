package snownee.jade.impl.config.entry;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.gui.config.OptionsList;
import snownee.jade.gui.config.value.OptionValue;

public class EnumConfigEntry<E extends Enum<E>> extends ConfigEntry<E> {
   public EnumConfigEntry(ResourceLocation id, E defaultValue) {
      super(id, defaultValue);
   }

   @Override
   public boolean isValidValue(Object value) {
      if (value.getClass() == String.class) {
         try {
            Enum.valueOf(this.getDefaultValue().getClass(), (String)value);
            return true;
         } catch (Throwable var3) {
            return false;
         }
      } else {
         return value.getClass() == this.getDefaultValue().getClass();
      }
   }

   @Override
   public void setValue(Object value) {
      if (value.getClass() == String.class) {
         value = Enum.valueOf(this.getDefaultValue().getClass(), (String)value);
      }

      super.setValue(value);
   }

   @Override
   public OptionValue<?> createUI(OptionsList options, String optionName, BiConsumer<ResourceLocation, Object> setter) {
      return options.choices(optionName, this::getValue, (Consumer<E>)(e -> setter.accept(this.id, e)), builder -> builder.withTooltip(e -> {
         String key = OptionsList.Entry.makeKey(optionName + "_" + e.name().toLowerCase(Locale.ENGLISH) + "_desc");
         return !I18n.exists(key) ? null : Tooltip.create(Component.translatable(key));
      }));
   }
}
