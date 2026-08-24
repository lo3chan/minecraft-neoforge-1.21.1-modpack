package cc.cosmetica.cosmetica.settings;

import cc.cosmetica.cosmetica.gui.widget.CycleButton;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Component;

public class EnumSetting<T extends Enum<T>> extends Setting<T> {
   private final String translationKeyBase;

   public EnumSetting(String key, T defaultValue) {
      super(key, defaultValue);
      this.translationKeyBase = key;
   }

   private T cycleEnum() {
      T value = this.get();
      T[] values = value.getDeclaringClass().getEnumConstants();
      return values[(value.ordinal() + 1) % values.length];
   }

   @Override
   public Component createController() {
      return new CycleButton<T>(this, this::cycleEnum, this.translationKeyBase);
   }

   public Text createDescription(T value) {
      return null;
   }
}
