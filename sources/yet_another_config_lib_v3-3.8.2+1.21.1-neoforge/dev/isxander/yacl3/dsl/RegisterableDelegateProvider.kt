package dev.isxander.yacl3.dsl

import kotlin.reflect.KProperty

public class RegisterableDelegateProvider<R>(registerFunction: (String) -> Any, id: String?) {
   private final val registerFunction: (String) -> Any
   private final val id: String?

   init {
      this.registerFunction = registerFunction;
      this.id = id;
   }

   public operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ExistingDelegateProvider<Any> {
      val var10000: ExistingDelegateProvider = new ExistingDelegateProvider;
      var var10003: java.lang.String = this.id;
      if (this.id == null) {
         var10003 = property.getName();
      }

      var10000./* $VF: Unable to resugar constructor */<init>(this.registerFunction.invoke(var10003));
      return var10000;
   }
}
