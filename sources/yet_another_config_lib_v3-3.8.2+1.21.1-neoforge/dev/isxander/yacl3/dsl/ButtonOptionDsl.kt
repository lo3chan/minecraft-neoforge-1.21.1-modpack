package dev.isxander.yacl3.dsl

import dev.isxander.yacl3.api.ButtonOption
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionDescription.Builder
import java.util.concurrent.CompletableFuture

public interface ButtonOptionDsl : ButtonOption.Builder, Buildable<ButtonOption> {
   public val optionKey: String
   public val optionId: String
   public val thisOption: CompletableFuture<ButtonOption>

   public open fun Builder.addDefaultText(lines: Int? = null) {
      ExtensionsKt.addDefaultText(`$this$addDefaultText`, "${this.getOptionKey()}.description", lines);
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @Deprecated
      @JvmStatic
      fun addDefaultText(`$this`: ButtonOptionDsl, `$receiver`: OptionDescription.Builder, lines: Int?) {
         ButtonOptionDsl.access$addDefaultText$jd(`$this`, `$receiver`, lines);
      }
   }
}
