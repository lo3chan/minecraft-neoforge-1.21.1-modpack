package dev.isxander.yacl3.dsl

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionDescription.Builder
import java.util.concurrent.CompletableFuture

public interface OptionDsl<T> : Option.Builder<T>, Buildable<Option<T>> {
   public val optionKey: String
   public val optionId: String
   public val thisOption: CompletableFuture<Option<Any>>

   public open fun Builder.addDefaultText(lines: Int? = null) {
      ExtensionsKt.addDefaultText(`$this$addDefaultText`, "${this.getOptionKey()}.description", lines);
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @Deprecated
      @JvmStatic
      fun <T> addDefaultText(`$this`: OptionDsl<T>, `$receiver`: OptionDescription.Builder, lines: Int?) {
         OptionDsl.access$addDefaultText$jd(`$this`, `$receiver`, lines);
      }
   }
}
