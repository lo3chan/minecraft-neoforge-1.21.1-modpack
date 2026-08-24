package dev.isxander.yacl3.dsl

import dev.isxander.yacl3.api.ButtonOption
import dev.isxander.yacl3.api.LabelOption
import dev.isxander.yacl3.api.Option
import java.util.concurrent.CompletableFuture
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import net.minecraft.network.chat.Component

@SourceDebugExtension(["SMAP\nImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Impl.kt\ndev/isxander/yacl3/dsl/OptionRegistrarImpl\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,310:1\n1#2:311\n*E\n"])
public class OptionRegistrarImpl(adder: (Option<*>, String) -> Unit, getter: (String) -> CompletableFuture<Option<*>>, groupKey: String) : OptionRegistrar {
   private final val adder: (Option<*>, String) -> Unit
   private final val getter: (String) -> CompletableFuture<Option<*>>
   private final val groupKey: String
   public open val registeringLabel: RegisterableDelegateProvider<LabelOption>

   init {
      this.adder = adder;
      this.getter = getter;
      this.groupKey = groupKey;
      this.registeringLabel = new RegisterableDelegateProvider<>((new Function1<java.lang.String, LabelOption>(this) {
         {
            super(1, receiver, OptionRegistrarImpl::class.java, "registerLabel", "registerLabel(Ljava/lang/String;)Ldev/isxander/yacl3/api/LabelOption;", 0);
         }

         public final LabelOption invoke(java.lang.String p0) {
            return (this.receiver as OptionRegistrarImpl).registerLabel(p0);
         }
      }) as (java.lang.String?) -> LabelOption, null);
   }

   public override fun <T, OPT : Option<T>> register(id: String, option: OPT): OPT {
      this.adder.invoke(option, id);
      return (OPT)option;
   }

   public override fun <T> register(id: String, block: (OptionDsl<T>) -> Unit): Option<T> {
      val var3: OptionDslImpl = new OptionDslImpl(id, this.groupKey, null, 4, null);
      block.invoke(var3);
      return this.register(id, var3.build());
   }

   public override fun <T> registering(id: String?, block: (OptionDsl<T>) -> Unit): RegisterableActionDelegateProvider<OptionDsl<T>, Option<T>> {
      return new RegisterableActionDelegateProvider<>(
         (
            new Function2<java.lang.String, Function1<? super OptionDsl<T>, ? extends Unit>, Option<T>>(this) {
               {
                  super(
                     2,
                     receiver,
                     OptionRegistrarImpl::class.java,
                     "register",
                     "register(Ljava/lang/String;Lkotlin/jvm/functions/Function1;)Ldev/isxander/yacl3/api/Option;",
                     0
                  );
               }

               public final Option<T> invoke(java.lang.String p0, Function1<? super OptionDsl<T>, Unit> p1) {
                  return (this.receiver as OptionRegistrarImpl).register(p0, p1);
               }
            }
         ) as Function2,
         block,
         id
      );
   }

   public override fun <T> futureRef(id: String): CompletableFuture<Option<T>> {
      val var10000: Any = this.getter.invoke(id);
      return var10000 as CompletableFuture<Option<T>>;
   }

   public override fun <T> futureRef(): RegisterableDelegateProvider<CompletableFuture<Option<T>>> {
      return new RegisterableDelegateProvider<>(OptionRegistrarImpl::futureRef$lambda$0, null);
   }

   public override fun <T> ref(id: String?): ReadOnlyProperty<Any?, Option<T>?> {
      return OptionRegistrarImpl::ref$lambda$0;
   }

   public override fun registerLabel(id: String): LabelOption {
      val var10002: LabelOption = LabelOption.create(Component.translatable("${this.groupKey}.label.$id") as Component);
      val var10000: Option = this.register(id, var10002);
      return var10000 as LabelOption;
   }

   public override fun registerLabel(id: String, text: Component): LabelOption {
      val var10002: LabelOption = LabelOption.create(text);
      val var10000: Option = this.register(id, var10002);
      return var10000 as LabelOption;
   }

   public override fun registerLabel(id: String, builder: (TextLineBuilderDsl) -> Unit): LabelOption {
      return this.registerLabel(id, TextLineBuilderDsl.Companion.createText(builder));
   }

   public override fun registerButton(id: String, block: (ButtonOptionDsl) -> Unit): ButtonOption {
      val var3: ButtonOptionDslImpl = new ButtonOptionDslImpl(id, this.groupKey, null, 4, null);
      block.invoke(var3);
      return this.register(id, var3.build());
   }

   public override fun registeringButton(id: String?, block: (ButtonOptionDsl) -> Unit): RegisterableActionDelegateProvider<ButtonOptionDsl, ButtonOption> {
      return new RegisterableActionDelegateProvider<>(
         (
            new Function2<java.lang.String, Function1<? super ButtonOptionDsl, ? extends Unit>, ButtonOption>(this) {
               {
                  super(
                     2,
                     receiver,
                     OptionRegistrarImpl::class.java,
                     "registerButton",
                     "registerButton(Ljava/lang/String;Lkotlin/jvm/functions/Function1;)Ldev/isxander/yacl3/api/ButtonOption;",
                     0
                  );
               }

               public final ButtonOption invoke(java.lang.String p0, Function1<? super ButtonOptionDsl, Unit> p1) {
                  return (this.receiver as OptionRegistrarImpl).registerButton(p0, p1);
               }
            }
         ) as (java.lang.String?, ((ButtonOptionDsl?) -> Unit)?) -> ButtonOption,
         block,
         id
      );
   }

   @JvmStatic
   fun `futureRef$lambda$0`(`this$0`: OptionRegistrarImpl, it: java.lang.String): CompletableFuture {
      return `this$0`.futureRef(it);
   }

   @JvmStatic
   fun `ref$lambda$0`(`this$0`: OptionRegistrarImpl, `$id`: java.lang.String, var2: Any, property: KProperty): Option {
      var var10001: java.lang.String = `$id`;
      if (`$id` == null) {
         var10001 = property.getName();
      }

      return `this$0`.futureRef(var10001).getNow(null) as Option;
   }
}
