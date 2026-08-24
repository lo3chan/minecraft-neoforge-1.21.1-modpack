package vazkii.psi.client.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class CallbackTextFieldWidget extends EditBox {
   protected final CallbackTextFieldWidget.IPressable pressable;

   public CallbackTextFieldWidget(Font font, int x, int y, int width, int height, CallbackTextFieldWidget.IPressable pressable) {
      super(font, x, y, width, height, null, Component.empty());
      this.pressable = pressable;
   }

   public void insertText(@NotNull String textToWrite) {
      super.insertText(textToWrite);
      this.onPress();
   }

   public void deleteChars(int num) {
      super.deleteChars(num);
      this.onPress();
   }

   public void deleteWords(int num) {
      super.deleteWords(num);
      this.onPress();
   }

   public void onPress() {
      this.pressable.onPress(this);
   }

   @OnlyIn(Dist.CLIENT)
   public interface IPressable {
      void onPress(AbstractWidget var1);
   }
}
