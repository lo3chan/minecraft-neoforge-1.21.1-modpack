package dev.isxander.yacl3.dsl

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

public class ExistingDelegateProvider<Return>(delegate: Any) : ReadOnlyProperty<Object, Return> {
   private final val delegate: Any

   init {
      this.delegate = (Return)delegate;
   }

   public open operator fun getValue(thisRef: Any?, property: KProperty<*>): Any {
      return this.delegate;
   }
}
