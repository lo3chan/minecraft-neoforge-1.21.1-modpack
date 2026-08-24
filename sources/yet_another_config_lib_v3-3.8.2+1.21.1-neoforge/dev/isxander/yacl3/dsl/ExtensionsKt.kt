@file:SourceDebugExtension(["SMAP\nExtensions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Extensions.kt\ndev/isxander/yacl3/dsl/ExtensionsKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,84:1\n1#2:85\n*E\n"])

package dev.isxander.yacl3.dsl

import dev.isxander.yacl3.api.Binding
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.Option.Builder
import dev.isxander.yacl3.api.controller.ControllerBuilder
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.reflect.KMutableProperty0
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component

public final var controller: (Option<T>) -> ControllerBuilder<T>
   public final get() {
      throw new UnsupportedOperationException();
   }

   public final set(value) {
      `$this$controller`.controller(ExtensionsKt::_set_controller_$lambda$0);
   }


public final var binding: Binding<T>
   public final get() {
      throw new UnsupportedOperationException();
   }

   public final set(value) {
      `$this$binding`.binding(value);
   }


public final var available: Boolean
   public final get() {
      throw new UnsupportedOperationException();
   }

   public final set(value) {
      `$this$available`.available(value);
   }


public fun <T : Any> Builder<T>.binding(property: KMutableProperty0<T>, default: T) {
   `$this$binding`.binding(var2, ExtensionsKt::binding$lambda$0, ExtensionsKt::binding$lambda$1);
}

public fun <T> Builder<T>.descriptionBuilderDyn(block: (dev.isxander.yacl3.api.OptionDescription.Builder, T) -> Unit) {
   `$this$descriptionBuilderDyn`.description(ExtensionsKt::descriptionBuilderDyn$lambda$0);
}

public fun Builder<*>.descriptionBuilder(block: (dev.isxander.yacl3.api.OptionDescription.Builder) -> Unit) {
   val var2: OptionDescription.Builder = OptionDescription.createBuilder();
   block.invoke(var2);
   `$this$descriptionBuilder`.description(var2.build());
}

public fun dev.isxander.yacl3.api.ButtonOption.Builder.descriptionBuilder(block: (dev.isxander.yacl3.api.OptionDescription.Builder) -> Unit) {
   val var2: OptionDescription.Builder = OptionDescription.createBuilder();
   block.invoke(var2);
   `$this$descriptionBuilder`.description(var2.build());
}

public fun dev.isxander.yacl3.api.OptionGroup.Builder.descriptionBuilder(block: (dev.isxander.yacl3.api.OptionDescription.Builder) -> Unit) {
   val var2: OptionDescription.Builder = OptionDescription.createBuilder();
   block.invoke(var2);
   `$this$descriptionBuilder`.description(var2.build());
}

public fun dev.isxander.yacl3.api.OptionDescription.Builder.addDefaultText(prefix: String, lines: Int? = null) {
   if (lines != null) {
      if (lines == 1) {
         `$this$addDefaultText`.text(Component.translatable(prefix));
      } else {
         var var7: Int = 1;
         val key: Int = lines;
         if (1 <= key) {
            while (true) {
               `$this$addDefaultText`.text(Component.translatable("$prefix.$var7"));
               if (var7 == key) {
                  break;
               }

               var7++;
            }
         }
      }
   } else {
      for (int i = 1; i < 100; i++) {
         val var9: java.lang.String = "$prefix.$var8";
         if (!Language.getInstance().has(var9)) {
            break;
         }

         `$this$addDefaultText`.text(Component.translatable(var9));
      }
   }
}

@JvmSynthetic
fun `addDefaultText$default`(var0: OptionDescription.Builder, var1: java.lang.String, var2: Int, var3: Int, var4: Any) {
   if ((var3 and 2) != 0) {
      var2 = null;
   }

   addDefaultText(var0, var1, var2);
}

public fun Builder<*>.available(block: () -> Boolean) {
   `$this$available`.available(block.invoke() as java.lang.Boolean);
}

public fun dev.isxander.yacl3.api.OptionDescription.Builder.text(block: () -> Component) {
   `$this$text`.text((Component)block.invoke());
}

public fun <T, B : ControllerBuilder<T>> Builder<T>.controller(builder: (Option<T>) -> B, block: (B) -> Unit = ExtensionsKt::controller$lambda$0) {
   `$this$controller`.controller(ExtensionsKt::controller$lambda$1);
}

@JvmSynthetic
fun `controller$default`(var0: Option.Builder, var1: Function1, var2: Function1, var3: Int, var4: Any) {
   if ((var3 and 2) != 0) {
      var2 = ExtensionsKt::controller$lambda$0;
   }

   controller(var0, var1, var2);
}

fun `binding$lambda$0`(`$property`: KMutableProperty0): Any {
   return `$property`.get();
}

fun `binding$lambda$1`(`$property`: KMutableProperty0, it: Any) {
   `$property`.set(it);
}

fun `_set_controller_$lambda$0`(`$tmp0`: Function1, p0: Option): ControllerBuilder {
   return `$tmp0`.invoke(p0) as ControllerBuilder;
}

fun `descriptionBuilderDyn$lambda$0`(`$block`: Function2, it: Any): OptionDescription {
   val var2: OptionDescription.Builder = OptionDescription.createBuilder();
   `$block`.invoke(var2, it);
   return var2.build();
}

fun `controller$lambda$0`(var0: ControllerBuilder): Unit {
   return Unit.INSTANCE;
}

fun `controller$lambda$1`(`$builder`: Function1, `$block`: Function1, it: Option): ControllerBuilder {
   val var3: Any = `$builder`.invoke(it);
   `$block`.invoke(var3);
   return var3 as ControllerBuilder;
}
