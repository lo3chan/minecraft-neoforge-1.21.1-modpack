package dev.isxander.yacl3.dsl

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.YetAnotherConfigLib
import java.util.concurrent.CompletableFuture
import net.minecraft.network.chat.Component

public interface RootDsl {
   public val rootKey: String
   public val rootId: String
   public val thisRoot: CompletableFuture<YetAnotherConfigLib>
   public val categories: ParentRegistrar<ConfigCategory, CategoryDsl, ParentRegistrar<OptionGroup, GroupDsl, OptionRegistrar>>

   public abstract fun title(component: Component) {
   }

   public abstract fun title(block: () -> Component) {
   }

   public abstract fun screenInit(block: () -> Unit) {
   }

   public abstract fun save(block: () -> Unit) {
   }
}
