package io.wispforest.owo.ui.base;

import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.core.Size;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.components.toasts.Toast.Visibility;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public abstract class BaseOwoToast<R extends ParentComponent> implements Toast {
   protected final R rootComponent;
   protected final BaseOwoToast.VisibilityPredicate<R> visibilityPredicate;
   protected int virtualWidth = 1000;
   protected int virtualHeight = 1000;

   protected BaseOwoToast(Supplier<R> components, BaseOwoToast.VisibilityPredicate<R> predicate) {
      this.rootComponent = components.get();
      this.visibilityPredicate = predicate;
      this.rootComponent.inflate(Size.of(this.virtualWidth, this.virtualHeight));
      this.rootComponent.mount(null, 0, 0);
   }

   protected BaseOwoToast(Supplier<R> rootComponent, Duration timeout) {
      this(rootComponent, BaseOwoToast.VisibilityPredicate.timeout(timeout));
   }

   public Visibility render(GuiGraphics context, ToastComponent manager, long startTime) {
      Minecraft client = Minecraft.getInstance();
      DeltaTracker tickCounter = Minecraft.getInstance().getTimer();
      this.rootComponent.draw(OwoUIDrawContext.of(context), -1000, -1000, tickCounter.getGameTimeDeltaPartialTick(false), tickCounter.getGameTimeDeltaTicks());
      return this.visibilityPredicate.test(this, startTime);
   }

   public int height() {
      return this.rootComponent.fullSize().height();
   }

   public int width() {
      return this.rootComponent.fullSize().width();
   }

   @FunctionalInterface
   public interface VisibilityPredicate<R extends ParentComponent> {
      Visibility test(BaseOwoToast<R> var1, long var2);

      static <R extends ParentComponent> BaseOwoToast.VisibilityPredicate<R> timeout(Duration timeout) {
         return (toast, startTime) -> System.currentTimeMillis() - startTime <= timeout.get(ChronoUnit.MILLIS) ? Visibility.HIDE : Visibility.SHOW;
      }
   }
}
