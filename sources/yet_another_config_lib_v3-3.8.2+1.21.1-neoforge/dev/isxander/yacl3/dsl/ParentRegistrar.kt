package dev.isxander.yacl3.dsl

import java.util.concurrent.CompletableFuture
import kotlin.properties.ReadOnlyProperty

public interface ParentRegistrar<T, DSL, INNER> {
   public val futureRef: ReadOnlyProperty<Any?, CompletableFuture<Any>>
   public val ref: ReadOnlyProperty<Any?, Any?>

   public abstract fun register(id: String, registrant: Any): Any {
   }

   public abstract fun register(id: String, block: (Any) -> Unit): Any {
   }

   public abstract fun registering(id: String? = null, block: (Any) -> Unit): RegisterableActionDelegateProvider<Any, Any> {
   }

   public abstract fun futureRef(id: String): CompletableFuture<Any> {
   }

   public abstract fun ref(id: String): Any? {
   }

   public abstract operator fun get(id: String): CompletableFuture<Any> {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls
}
