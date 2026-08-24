package cc.cosmetica.cosmetica.settings;

import cc.cosmetica.cosmetica.gui.widget.CycleButton;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Component;
import java.util.ArrayList;
import java.util.List;

public class BooleanSetting extends Setting<Boolean> {
   private final boolean defaultText;
   private final String baseKey;
   private final List<BooleanSetting.ManagedSetting<?>> dependents = new ArrayList<>();

   public BooleanSetting(String key, Boolean defaultValue, boolean defaultText) {
      super(key, defaultValue);
      this.defaultText = defaultText;
      this.baseKey = key;
   }

   public <T> BooleanSetting forceWhenOff(Setting<T> other, T value) {
      this.dependents.add(new BooleanSetting.ManagedSetting<>(other, value));
      return this;
   }

   @Override
   protected void onUpdate() {
      if (this.get()) {
         this.dependents.forEach(BooleanSetting.ManagedSetting::release);
      } else {
         this.dependents.forEach(BooleanSetting.ManagedSetting::manage);
      }
   }

   private boolean cycleBoolean() {
      return !this.get();
   }

   @Override
   public boolean hasDescription() {
      return this.defaultText;
   }

   @Override
   public Component createController() {
      return new CycleButton<Boolean>(this, this::cycleBoolean, this.defaultText ? null : this.baseKey);
   }

   public Text createDescription(Boolean value) {
      return Text.translatable(this.baseKey + "." + value + ".description", new String[0]);
   }

   private static class ManagedSetting<T> {
      private final Setting<T> setting;
      private final T value;

      public ManagedSetting(Setting<T> setting, T value) {
         this.setting = setting;
         this.value = value;
      }

      public void manage() {
         this.setting.parentManage(this.value);
      }

      public void release() {
         this.setting.parentManage(null);
      }
   }
}
