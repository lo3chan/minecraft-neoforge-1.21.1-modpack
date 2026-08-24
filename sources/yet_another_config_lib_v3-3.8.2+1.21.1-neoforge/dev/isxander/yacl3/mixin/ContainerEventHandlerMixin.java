package dev.isxander.yacl3.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import java.util.List;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({ContainerEventHandler.class})
public interface ContainerEventHandlerMixin {
   @Redirect(
      method = {"nextFocusPathVaguelyInDirection", "nextFocusPathInDirection"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/components/events/ContainerEventHandler;children()Ljava/util/List;"
      )
   )
   default List<?> modifyFocusCandidates(ContainerEventHandler instance, @Local(argsOnly = true) ScreenDirection direction) {
      return direction.getAxis() == ScreenAxis.HORIZONTAL
         ? instance.children().stream().filter(child -> !(child instanceof TabNavigationBar)).toList()
         : instance.children();
   }
}
