package io.wispforest.owo.renderdoc;

import io.wispforest.owo.ops.TextOps;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.CheckboxComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.core.VerticalAlignment;
import io.wispforest.owo.ui.util.CommandOpenedScreen;
import java.util.EnumSet;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RenderdocScreen extends BaseOwoScreen<FlowLayout> implements CommandOpenedScreen {
   private int ticks = 0;
   private boolean setCaptureKey = false;
   @Nullable
   private RenderDoc.Key scheduledKey = null;
   private ButtonComponent captureKeyButton = null;
   private LabelComponent captureLabel = null;

   @NotNull
   @Override
   protected OwoUIAdapter<FlowLayout> createAdapter() {
      return OwoUIAdapter.create(this, Containers::verticalFlow);
   }

   protected void build(FlowLayout rootComponent) {
      rootComponent.surface(Surface.VANILLA_TRANSLUCENT);
      EnumSet<RenderDoc.OverlayOption> overlayState = RenderDoc.getOverlayOptions();
      rootComponent.child(
            Containers.verticalFlow(Sizing.content(), Sizing.content())
               .child(Components.label(Component.literal("RenderDoc Controls")).shadow(true).margins(Insets.top(5).withBottom(10)))
               .child(Components.label(Component.literal("Such has been disabled at the request of ATM!")).shadow(true).margins(Insets.top(0).withBottom(5)))
               .child(Components.button(Component.literal("Close"), buttonComponent -> this.onClose()))
               .child(
                  Containers.grid(Sizing.content(), Sizing.content(), 2, 2)
                     .child(overlayControl(Component.nullToEmpty("Enabled"), overlayState, RenderDoc.OverlayOption.ENABLED), 0, 0)
                     .child(overlayControl(Component.nullToEmpty("Capture List"), overlayState, RenderDoc.OverlayOption.CAPTURE_LIST), 0, 1)
                     .child(overlayControl(Component.nullToEmpty("Frame Rate"), overlayState, RenderDoc.OverlayOption.FRAME_RATE), 1, 0)
                     .child(overlayControl(Component.nullToEmpty("Frame Number"), overlayState, RenderDoc.OverlayOption.FRAME_NUMBER), 1, 1)
               )
               .child(
                  Components.box(Sizing.fixed(175), Sizing.fixed(1)).color(Color.ofFormatting(ChatFormatting.DARK_GRAY)).fill(true).margins(Insets.vertical(5))
               )
               .child(
                  Containers.grid(Sizing.content(), Sizing.content(), 2, 2)
                     .child(
                        Components.button(Component.nullToEmpty("Launch UI"), button -> RenderDoc.launchReplayUI(true))
                           .horizontalSizing(Sizing.fixed(90))
                           .margins(Insets.of(2)),
                        0,
                        0
                     )
                     .child((this.captureKeyButton = Components.button(Component.nullToEmpty("Capture Hotkey"), button -> {
                        button.active = false;
                        button.setMessage(Component.nullToEmpty("Press..."));
                        this.setCaptureKey = true;
                     })).horizontalSizing(Sizing.fixed(90)).margins(Insets.of(2)), 1, 0)
                     .child(
                        Components.button(Component.nullToEmpty("Capture Frame"), button -> RenderDoc.triggerCapture())
                           .horizontalSizing(Sizing.fixed(90))
                           .margins(Insets.of(2)),
                        0,
                        1
                     )
                     .child(this.captureLabel = Components.label(this.createCapturesText()), 1, 1)
                     .verticalAlignment(VerticalAlignment.CENTER)
                     .horizontalAlignment(HorizontalAlignment.CENTER)
               )
               .horizontalAlignment(HorizontalAlignment.CENTER)
               .padding(Insets.of(5))
               .surface(Surface.flat(1996488704).and(Surface.outline(1996488704)))
         )
         .verticalAlignment(VerticalAlignment.CENTER)
         .horizontalAlignment(HorizontalAlignment.CENTER);
   }

   public void tick() {
      super.tick();
      if (++this.ticks % 10 == 0) {
         if (this.scheduledKey != null) {
            RenderDoc.setCaptureKeys(this.scheduledKey);
            this.scheduledKey = null;
         }

         this.captureLabel.text(this.createCapturesText());
      }
   }

   @Override
   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.setCaptureKey) {
         this.captureKeyButton.active = true;
         this.captureKeyButton.setMessage(Component.nullToEmpty("Capture Hotkey"));
         this.setCaptureKey = false;
         RenderDoc.Key key = RenderDoc.Key.fromGLFW(keyCode);
         if (key != null) {
            this.ticks = 0;
            this.scheduledKey = key;
            return true;
         }
      }

      return super.keyPressed(keyCode, scanCode, modifiers);
   }

   private Component createCapturesText() {
      return TextOps.withColor("Captures: §" + RenderDoc.getNumCaptures(), TextOps.color(ChatFormatting.WHITE), 55295);
   }

   private static CheckboxComponent overlayControl(Component name, EnumSet<RenderDoc.OverlayOption> state, RenderDoc.OverlayOption option) {
      CheckboxComponent checkbox = Components.checkbox(name);
      checkbox.margins(Insets.of(3)).horizontalSizing(Sizing.fixed(100));
      checkbox.checked(state.contains(option));
      checkbox.onChanged(enabled -> {
         if (enabled) {
            RenderDoc.enableOverlayOptions(option);
         } else {
            RenderDoc.disableOverlayOptions(option);
         }
      });
      return checkbox;
   }
}
