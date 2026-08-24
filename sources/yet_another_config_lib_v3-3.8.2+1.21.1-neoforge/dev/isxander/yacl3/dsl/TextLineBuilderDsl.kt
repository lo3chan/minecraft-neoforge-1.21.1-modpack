package dev.isxander.yacl3.dsl

import kotlin.jvm.internal.Ref.BooleanRef
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

public interface TextLineBuilderDsl {
   public abstract fun text(component: Component) {
   }

   public abstract fun text(block: () -> Component) {
   }

   public abstract operator fun Component.unaryPlus() {
   }

   public companion object {
      public fun createText(block: (TextLineBuilderDsl) -> Unit): Component {
         val text: MutableComponent = Component.empty();
         val first: BooleanRef = new BooleanRef();
         first.element = true;
         block.invoke(new TextLineBuilderDsl.Delegate(TextLineBuilderDsl.Companion::createText$lambda$0));
         return text as Component;
      }

      @JvmStatic
      fun `createText$lambda$0`(`$first`: BooleanRef, `$text`: MutableComponent, it: Component): Unit {
         if (!`$first`.element) {
            `$text`.append(CommonComponents.NEW_LINE);
         }

         `$text`.append(it);
         `$first`.element = false;
         return Unit.INSTANCE;
      }
   }

   public class Delegate(tooltipFunction: (Component) -> Unit) : TextLineBuilderDsl {
      private final val tooltipFunction: (Component) -> Unit

      init {
         this.tooltipFunction = tooltipFunction;
      }

      public override fun text(component: Component) {
         this.tooltipFunction.invoke(component);
      }

      public override fun text(block: () -> Component) {
         this.text(block.invoke() as Component);
      }

      public override operator fun Component.unaryPlus() {
         this.text(`$this$unaryPlus`);
      }
   }
}
