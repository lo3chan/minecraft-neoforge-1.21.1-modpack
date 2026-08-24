package cc.cosmetica.cosmetica.mixin.button;

import cc.cosmetica.cosmetica.gui.HomeScreen;
import cc.cosmetica.kupe.api.Screens;
import cc.cosmetica.kupe.api.Text;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({OptionsScreen.class})
public abstract class OptionsScreenMixin extends Screen {
   protected OptionsScreenMixin(Component component) {
      super(component);
   }

   @Redirect(
      at = @At(
         value = "INVOKE",
         ordinal = 0,
         target = "Lnet/minecraft/client/gui/screens/options/OptionsScreen;openScreenButton(Lnet/minecraft/network/chat/Component;Ljava/util/function/Supplier;)Lnet/minecraft/client/gui/components/Button;"
      ),
      method = {"init()V"}
   )
   private Button onInit(OptionsScreen instance, Component arg, Supplier<Screen> supplier) {
      return Button.builder(Text.translatable("button.cosmetica.home", new String[0]).toMinecraftComponent(), button -> Screens.setScreen(HomeScreen.ID))
         .size(150, 20)
         .build();
   }
}
