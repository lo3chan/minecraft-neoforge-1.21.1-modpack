package io.wispforest.owo.ui.util;

import io.wispforest.owo.Owo;
import io.wispforest.owo.ops.TextOps;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.parsing.UIModelLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.components.toasts.Toast.Visibility;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class UIErrorToast implements Toast {
   private final List<FormattedCharSequence> errorMessage;
   private final Font textRenderer = Minecraft.getInstance().font;
   private final int width;

   public UIErrorToast(Throwable error) {
      List<Component> texts = this.initText(
         String.valueOf(error.getMessage()),
         consumer -> {
            StackTraceElement stackTop = error.getStackTrace()[0];
            String[] errorLocation = stackTop.getClassName().split("\\.");
            consumer.accept(
               Component.literal("Type: ")
                  .withStyle(ChatFormatting.RED)
                  .append(Component.literal(error.getClass().getSimpleName()).withStyle(ChatFormatting.GRAY))
            );
            consumer.accept(
               Component.literal("Thrown by: ")
                  .withStyle(ChatFormatting.RED)
                  .append(Component.literal(errorLocation[errorLocation.length - 1] + ":" + stackTop.getLineNumber()).withStyle(ChatFormatting.GRAY))
            );
         }
      );
      this.width = Math.min(240, TextOps.width(this.textRenderer, texts) + 8);
      this.errorMessage = this.wrap(texts);
   }

   public UIErrorToast(String message) {
      List<Component> texts = this.initText(message, consumer -> consumer.accept(Component.literal("No context provided").withStyle(ChatFormatting.GRAY)));
      this.width = Math.min(240, TextOps.width(this.textRenderer, texts) + 8);
      this.errorMessage = this.wrap(texts);
   }

   public static void report(String message) {
      logErrorsDuringInitialLoad();
      Minecraft.getInstance().getToasts().addToast(new UIErrorToast(message));
   }

   public static void report(Throwable error) {
      logErrorsDuringInitialLoad();
      Minecraft.getInstance().getToasts().addToast(new UIErrorToast(error));
   }

   private static void logErrorsDuringInitialLoad() {
      if (!UIModelLoader.hasCompletedInitialLoad()) {
         Throwable throwable = new Throwable();
         Owo.LOGGER
            .error(
               "An owo-ui error has occurred during the initial resource reload (on thread {}). This is likely a bug caused by *some* other mod initializing an owo-config screen significantly too early - please report it at https://github.com/wisp-forest/owo-lib/issues",
               Thread.currentThread().getName(),
               throwable
            );
      }
   }

   public Visibility render(GuiGraphics context, ToastComponent manager, long startTime) {
      OwoUIDrawContext owoContext = OwoUIDrawContext.of(context);
      owoContext.fill(0, 0, this.width(), this.height(), 1996488704);
      owoContext.drawRectOutline(0, 0, this.width(), this.height(), -1476460544);
      int xOffset = this.width() / 2 - this.textRenderer.width(this.errorMessage.get(0)) / 2;
      owoContext.drawString(this.textRenderer, this.errorMessage.get(0), 4 + xOffset, 4, 16777215);

      for (int i = 1; i < this.errorMessage.size(); i++) {
         owoContext.drawString(this.textRenderer, this.errorMessage.get(i), 4, 4 + i * 11, 16777215, false);
      }

      return startTime > 10000L ? Visibility.HIDE : Visibility.SHOW;
   }

   public int height() {
      return 6 + this.errorMessage.size() * 11;
   }

   public int width() {
      return this.width;
   }

   private List<Component> initText(String errorMessage, Consumer<Consumer<Component>> contextAppender) {
      ArrayList<Component> texts = new ArrayList<>();
      texts.add(Component.literal("owo-ui error").withStyle(ChatFormatting.RED));
      texts.add(Component.literal(" "));
      contextAppender.accept(texts::add);
      texts.add(Component.literal(" "));
      texts.add(Component.literal(errorMessage));
      texts.add(Component.literal(" "));
      texts.add(Component.literal("Check your log for details").withStyle(ChatFormatting.GRAY));
      return texts;
   }

   private List<FormattedCharSequence> wrap(List<Component> message) {
      ArrayList<FormattedCharSequence> list = new ArrayList<>();

      for (Component text : message) {
         list.addAll(this.textRenderer.split(text, this.width() - 8));
      }

      return list;
   }

   public Object getToken() {
      return UIErrorToast.Type.VERY_TYPE;
   }

   static enum Type {
      VERY_TYPE;
   }
}
