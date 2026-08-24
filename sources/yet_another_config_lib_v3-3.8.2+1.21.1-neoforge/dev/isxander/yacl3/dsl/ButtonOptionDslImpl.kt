package dev.isxander.yacl3.dsl

import dev.isxander.yacl3.api.ButtonOption
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.ButtonOption.Builder
import dev.isxander.yacl3.gui.YACLScreen
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer
import java.util.function.Consumer
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.network.chat.Component

@SourceDebugExtension(["SMAP\nImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Impl.kt\ndev/isxander/yacl3/dsl/ButtonOptionDslImpl\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,310:1\n1#2:311\n*E\n"])
public class ButtonOptionDslImpl(optionId: String, groupKey: String, builder: Builder = ButtonOption.createBuilder()) : ButtonOptionDsl, ButtonOption.Builder {
   public open val optionId: String
   private final val builder: Builder
   public open val optionKey: String
   public open val thisOption: CompletableFuture<ButtonOption>
   public open val built: CompletableFuture<ButtonOption>

   init {
      this.optionId = optionId;
      this.builder = builder;
      this.optionKey = "$groupKey.option.${this.getOptionId()}";
      this.thisOption = new CompletableFuture<>();
      this.built = this.getThisOption();
      this.builder.name(Component.translatable(this.getOptionKey()) as Component);
   }

   public override fun dev.isxander.yacl3.api.OptionDescription.Builder.addDefaultText(lines: Int?) {
      ExtensionsKt.addDefaultText(`$this$addDefaultText`, "${this.getOptionKey()}.description", lines);
   }

   public override fun build(): ButtonOption {
      val var1: ButtonOption = this.builder.build();
      this.getThisOption().complete(var1);
      return var1;
   }

   public override fun name(name: Component): Builder {
      return this.builder.name(name);
   }

   public override fun text(text: Component): Builder {
      return this.builder.text(text);
   }

   public override fun description(description: OptionDescription): Builder {
      return this.builder.description(description);
   }

   public override fun action(action: BiConsumer<YACLScreen, ButtonOption>): Builder {
      return this.builder.action(action);
   }

   @Deprecated(message = "Deprecated in Java")
   public override fun action(action: Consumer<YACLScreen>): Builder {
      return this.builder.action(action);
   }

   public override fun available(available: Boolean): Builder {
      return this.builder.available(available);
   }
}
