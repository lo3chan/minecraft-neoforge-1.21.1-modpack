package dev.isxander.yacl3.dsl

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.OptionGroup
import java.util.concurrent.CompletableFuture
import net.minecraft.network.chat.Component

public interface CategoryDsl : Buildable<ConfigCategory> {
   public val categoryKey: String
   public val categoryId: String
   public val thisCategory: CompletableFuture<ConfigCategory>
   public val groups: ParentRegistrar<OptionGroup, GroupDsl, OptionRegistrar>
   public val rootOptions: OptionRegistrar

   public abstract fun name(component: Component) {
   }

   public abstract fun name(block: () -> Component) {
   }

   public abstract fun tooltip(vararg component: Component) {
   }

   public abstract fun tooltip(block: (TextLineBuilderDsl) -> Unit) {
   }
}
