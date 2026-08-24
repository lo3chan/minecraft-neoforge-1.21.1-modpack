package fuzs.puzzleslib.api.chat.v1;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

@Deprecated
public class ComponentHelper {
   public static Component toComponent(FormattedText formattedText) {
      return fuzs.puzzleslib.api.util.v1.ComponentHelper.getAsComponent(formattedText);
   }

   public static Component toComponent(FormattedCharSequence formattedCharSequence) {
      return fuzs.puzzleslib.api.util.v1.ComponentHelper.getAsComponent(formattedCharSequence);
   }

   public static String toString(FormattedText formattedText) {
      return fuzs.puzzleslib.api.util.v1.ComponentHelper.getAsString(formattedText);
   }

   public static String toString(FormattedCharSequence formattedCharSequence) {
      return fuzs.puzzleslib.api.util.v1.ComponentHelper.getAsString(formattedCharSequence);
   }
}
