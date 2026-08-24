package dev.isxander.yacl3.dsl

import dev.isxander.yacl3.api.Binding
import dev.isxander.yacl3.api.Controller
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionEventListener
import dev.isxander.yacl3.api.OptionFlag
import dev.isxander.yacl3.api.StateManager
import dev.isxander.yacl3.api.Option.Builder
import dev.isxander.yacl3.api.controller.ControllerBuilder
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.network.chat.Component

@SourceDebugExtension(["SMAP\nImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Impl.kt\ndev/isxander/yacl3/dsl/OptionDslImpl\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,310:1\n1#2:311\n*E\n"])
public class OptionDslImpl<T>(optionId: String, groupKey: String, builder: Builder<Any> = Option.createBuilder()) : OptionDsl<T>, Option.Builder<T> {
   public open val optionId: String
   private final val builder: Builder<Any>
   public open val optionKey: String
   public open val thisOption: CompletableFuture<Option<Any>>
   public open val built: CompletableFuture<Option<Any>>

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

   public override fun build(): Option<Any> {
      val var1: Option = this.builder.build();
      this.getThisOption().complete(var1);
      return var1;
   }

   public override fun name(name: Component): Builder<Any> {
      return this.builder.name(name);
   }

   public override fun description(description: OptionDescription): Builder<Any> {
      return this.builder.description(description);
   }

   public override fun description(descriptionFunction: Function<Any, OptionDescription>): Builder<Any> {
      return this.builder.description(descriptionFunction);
   }

   public override fun controller(controllerBuilder: Function<Option<Any>, ControllerBuilder<Any>>): Builder<Any> {
      return this.builder.controller(controllerBuilder);
   }

   public override fun customController(control: Function<Option<Any>, Controller<Any>>): Builder<Any> {
      return this.builder.customController(control);
   }

   public override fun stateManager(stateManager: StateManager<Any>): Builder<Any> {
      return this.builder.stateManager(stateManager);
   }

   public override fun binding(binding: Binding<Any>): Builder<Any> {
      return this.builder.binding(binding);
   }

   public override fun binding(def: Any, getter: Supplier<Any>, setter: Consumer<Any>): Builder<Any> {
      return this.builder.binding((T)def, getter, setter);
   }

   public override fun available(available: Boolean): Builder<Any> {
      return this.builder.available(available);
   }

   public override fun flag(vararg flag: OptionFlag): Builder<Any> {
      return this.builder.flag(flag);
   }

   public override fun flags(flags: MutableCollection<out OptionFlag>): Builder<Any> {
      return this.builder.flags(flags);
   }

   public override fun addListener(listener: OptionEventListener<Any>): Builder<Any> {
      return this.builder.addListener(listener);
   }

   public override fun addListeners(listeners: MutableCollection<OptionEventListener<Any>>): Builder<Any> {
      return this.builder.addListeners(listeners);
   }

   @Deprecated(message = "Deprecated in Java")
   public override fun instant(instant: Boolean): Builder<Any> {
      return this.builder.instant(instant);
   }

   @Deprecated(message = "Deprecated in Java")
   public override fun listener(listener: BiConsumer<Option<Any>, Any>): Builder<Any> {
      return this.builder.listener(listener);
   }

   @Deprecated(message = "Deprecated in Java")
   public override fun listeners(listeners: MutableCollection<BiConsumer<Option<Any>, Any>>): Builder<Any> {
      return this.builder.listeners(listeners);
   }
}
