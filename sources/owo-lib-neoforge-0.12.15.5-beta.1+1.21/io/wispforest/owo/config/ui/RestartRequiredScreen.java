package io.wispforest.owo.config.ui;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Surface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class RestartRequiredScreen extends BaseUIModelScreen<FlowLayout> {
   protected final Screen parent;

   public RestartRequiredScreen(Screen parent) {
      super(FlowLayout.class, BaseUIModelScreen.DataSource.asset(ResourceLocation.fromNamespaceAndPath("owo", "restart_required")));
      this.parent = parent;
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   protected void build(FlowLayout rootComponent) {
      if (this.minecraft.level == null) {
         rootComponent.surface(Surface.OPTIONS_BACKGROUND);
      }

      ((ButtonComponent)rootComponent.childById(ButtonComponent.class, "exit-button")).onPress(button -> Minecraft.getInstance().stop());
      ((ButtonComponent)rootComponent.childById(ButtonComponent.class, "ignore-button")).onPress(button -> this.onClose());
   }
}
