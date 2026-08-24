package dev.isxander.yacl3.dsl

import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.OptionDescription.Builder
import java.util.concurrent.CompletableFuture
import net.minecraft.network.chat.Component

public interface GroupDsl : Buildable<OptionGroup> {
   public val groupKey: String
   public val groupId: String
   public val thisGroup: CompletableFuture<OptionGroup>
   public val options: OptionRegistrar

   public var collapsed: Boolean
      internal final set

   public abstract fun name(component: Component) {
   }

   public abstract fun name(block: () -> Component) {
   }

   public abstract fun description(description: OptionDescription) {
   }

   public abstract fun descriptionBuilder(block: (Builder) -> Unit) {
   }

   public open fun Builder.addDefaultText(lines: Int? = null) {
      ExtensionsKt.addDefaultText(`$this$addDefaultText`, "${this.getGroupKey()}.description", lines);
   }

   public abstract fun collapsed(collapsed: Boolean) {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @Deprecated
      @JvmStatic
      fun addDefaultText(`$this`: GroupDsl, `$receiver`: OptionDescription.Builder, lines: Int?) {
         GroupDsl.access$addDefaultText$jd(`$this`, `$receiver`, lines);
      }
   }
}
