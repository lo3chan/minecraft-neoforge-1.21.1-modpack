package dev.isxander.yacl3.dsl

import dev.isxander.yacl3.api.ButtonOption
import dev.isxander.yacl3.api.LabelOption
import dev.isxander.yacl3.api.Option
import java.util.concurrent.CompletableFuture
import kotlin.properties.ReadOnlyProperty
import net.minecraft.network.chat.Component

public interface OptionRegistrar {
   public val registeringLabel: RegisterableDelegateProvider<LabelOption>

   public abstract fun <T, OPT : Option<T>> register(id: String, option: OPT): OPT {
   }

   public abstract fun <T> register(id: String, block: (OptionDsl<T>) -> Unit): Option<T> {
   }

   public abstract fun <T> registering(id: String? = null, block: (OptionDsl<T>) -> Unit): RegisterableActionDelegateProvider<OptionDsl<T>, Option<T>> {
   }

   public abstract fun <T> futureRef(id: String): CompletableFuture<Option<T>> {
   }

   public abstract fun <T> futureRef(): RegisterableDelegateProvider<CompletableFuture<Option<T>>> {
   }

   public abstract fun <T> ref(id: String? = null): ReadOnlyProperty<Any?, Option<T>?> {
   }

   public abstract fun registerLabel(id: String): LabelOption {
   }

   public abstract fun registerLabel(id: String, text: Component): LabelOption {
   }

   public abstract fun registerLabel(id: String, builder: (TextLineBuilderDsl) -> Unit): LabelOption {
   }

   public abstract fun registerButton(id: String, block: (ButtonOptionDsl) -> Unit): ButtonOption {
   }

   public abstract fun registeringButton(id: String? = null, block: (ButtonOptionDsl) -> Unit): RegisterableActionDelegateProvider<
         ButtonOptionDsl,
         ButtonOption
      > {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls
}
