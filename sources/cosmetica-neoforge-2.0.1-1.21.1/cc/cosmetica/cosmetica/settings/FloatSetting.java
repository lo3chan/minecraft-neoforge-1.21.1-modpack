package cc.cosmetica.cosmetica.settings;

import cc.cosmetica.cosmetica.gui.widget.SliderWidget;
import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Component;
import java.util.Objects;
import java.util.function.Function;

public class FloatSetting extends Setting<Float> {
   private final float precision;
   private final Text description;

   public FloatSetting(String key, float defaultValue, float precision) {
      super(key, defaultValue);
      this.precision = precision;
      this.description = Text.translatable(key + ".description", new String[0]);
   }

   @Override
   public Component createController() {
      final State<Float> temp = new State(this.get());
      return (new SliderWidget(temp, this.precision, f -> Text.literal(String.format("%.1f", f))) {
         @Override
         public void mouseReleased(double x, double y, int button) {
            if (!Objects.equals(temp.peek(), FloatSetting.this.get())) {
               FloatSetting.this.set((Float)temp.peek());
            }
         }
      }).setDisabled(this.getManagement() != Setting.Management.USER);
   }

   public Text createDescription(Float value) {
      return this.description;
   }
}
