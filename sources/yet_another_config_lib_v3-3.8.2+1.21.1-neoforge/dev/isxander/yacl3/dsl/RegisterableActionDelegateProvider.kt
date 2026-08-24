package dev.isxander.yacl3.dsl

import kotlin.reflect.KProperty

public class RegisterableActionDelegateProvider<Dsl, Return>(registerFunction: (String, (Any) -> Unit) -> Any, action: (Any) -> Unit, name: String?) {
   private final val registerFunction: (String, (Any) -> Unit) -> Any
   private final val action: (Any) -> Unit
   private final val name: String?

   init {
      this.registerFunction = registerFunction;
      this.action = action;
      this.name = name;
   }

   public operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ExistingDelegateProvider<Any> {
      val var10000: ExistingDelegateProvider = new ExistingDelegateProvider;
      var var10003: java.lang.String = this.name;
      if (this.name == null) {
         var10003 = property.getName();
      }

      var10000./* $VF: Unable to resugar constructor */<init>(this.registerFunction.invoke(var10003, this.action));
      return var10000;
   }
}
