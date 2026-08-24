package dev.isxander.yacl3.dsl

import java.util.concurrent.CompletableFuture
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@SourceDebugExtension(["SMAP\nImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Impl.kt\ndev/isxander/yacl3/dsl/ParentRegistrarImpl\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,310:1\n1#2:311\n*E\n"])
public class ParentRegistrarImpl<T, DSL extends Buildable<T>, INNER>(adder: (Any, String) -> Unit,
      dslFactory: (String) -> Any,
      getter: (String) -> CompletableFuture<Any>,
      innerGetter: (String) -> CompletableFuture<Any>
   ) :
   ParentRegistrar<T, DSL, INNER> {
   private final val adder: (Any, String) -> Unit
   private final val dslFactory: (String) -> Any
   private final val getter: (String) -> CompletableFuture<Any>
   private final val innerGetter: (String) -> CompletableFuture<Any>

   public open val futureRef: ReadOnlyProperty<Any?, CompletableFuture<Any>>
      public open get() {
         return ParentRegistrarImpl::_get_futureRef_$lambda$0;
      }


   public open val ref: ReadOnlyProperty<Any?, Any?>
      public open get() {
         return ParentRegistrarImpl::_get_ref_$lambda$0;
      }


   init {
      this.adder = adder;
      this.dslFactory = dslFactory;
      this.getter = getter;
      this.innerGetter = innerGetter;
   }

   public override fun register(id: String, registrant: Any): Any {
      this.adder.invoke(registrant, id);
      return (T)registrant;
   }

   public override fun register(id: String, block: (Any) -> Unit): Any {
      val var3: Any = this.dslFactory.invoke(id);
      block.invoke(var3);
      return this.register(id, (T)(var3 as Buildable).build());
   }

   public override fun registering(id: String?, block: (Any) -> Unit): RegisterableActionDelegateProvider<Any, Any> {
      return new RegisterableActionDelegateProvider<>(
         (
            new Function2<java.lang.String, Function1<? super DSL, ? extends Unit>, T>(this) {
               {
                  super(
                     2,
                     receiver,
                     ParentRegistrarImpl::class.java,
                     "register",
                     "register(Ljava/lang/String;Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;",
                     0
                  );
               }

               public final T invoke(java.lang.String p0, Function1<? super DSL, Unit> p1) {
                  return (T)(this.receiver as ParentRegistrarImpl).register(p0, p1);
               }
            }
         ) as (java.lang.String?, ((DSL?) -> Unit)?) -> T,
         block,
         id
      );
   }

   public override fun futureRef(id: String): CompletableFuture<Any> {
      return this.getter.invoke(id) as CompletableFuture<T>;
   }

   public override fun ref(id: String): Any? {
      return this.futureRef(id).getNow(null);
   }

   public override operator fun get(id: String): CompletableFuture<Any> {
      return this.innerGetter.invoke(id) as CompletableFuture<INNER>;
   }

   @JvmStatic
   fun `_get_futureRef_$lambda$0`(`this$0`: ParentRegistrarImpl, var1: Any, property: KProperty): CompletableFuture {
      return `this$0`.futureRef(property.getName());
   }

   @JvmStatic
   fun `_get_ref_$lambda$0`(`this$0`: ParentRegistrarImpl, var1: Any, property: KProperty): Any {
      return `this$0`.ref(property.getName());
   }
}
