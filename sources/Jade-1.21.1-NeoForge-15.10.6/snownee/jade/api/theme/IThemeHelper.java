package snownee.jade.api.theme;

import java.util.Collection;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import snownee.jade.JadeInternals;

public interface IThemeHelper {
   static IThemeHelper get() {
      return JadeInternals.getThemeHelper();
   }

   Theme theme();

   default int getNormalColor() {
      return this.theme().text.colors().normal();
   }

   Collection<Theme> getThemes();

   @NotNull
   Theme getTheme(ResourceLocation var1);

   MutableComponent info(Object var1);

   MutableComponent success(Object var1);

   MutableComponent warning(Object var1);

   MutableComponent danger(Object var1);

   MutableComponent failure(Object var1);

   MutableComponent title(Object var1);

   MutableComponent modName(Object var1);

   MutableComponent seconds(int var1, float var2);

   default boolean isLightColorScheme() {
      return this.theme().lightColorScheme;
   }
}
