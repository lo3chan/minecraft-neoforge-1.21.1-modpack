package dev.isxander.yacl3.config.v3

import com.mojang.serialization.Codec
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.dsl.OptionDsl
import dev.isxander.yacl3.dsl.OptionRegistrar
import kotlin.jvm.functions.Function1
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import org.jetbrains.annotations.ApiStatus.Experimental

public final var value: T
   public final get() {
      return (T)`$this$value`.get();
   }

   public final set(value) {
      `$this$value`.set(value);
   }


public final val default: T
   public final get() {
      return (T)`$this$default`.defaultValue();
   }


public final val fieldName: String
   public final get() {
      val var10000: java.lang.String = `$this$fieldName`.fieldName();
      return var10000;
   }


@Experimental
public fun <T> EntryAddable.register(default: T, codec: Codec<T>): PropertyDelegateProvider<EntryAddable, ReadOnlyProperty<EntryAddable, ConfigEntry<T>>> {
   return KotlinExtsKt::register$lambda$0;
}

public fun <T : CodecConfig<T>> EntryAddable.register(fieldName: String? = null, configInstance: T): PropertyDelegateProvider<EntryAddable, T> {
   return KotlinExtsKt::register$lambda$1;
}

@JvmSynthetic
fun `register$default`(var0: EntryAddable, var1: java.lang.String, var2: CodecConfig, var3: Int, var4: Any): PropertyDelegateProvider {
   if ((var3 and 1) != 0) {
      var1 = null;
   }

   return register(var0, var1, var2);
}

public operator fun <T : CodecConfig<T>> T.getValue(thisRef: CodecConfig<*>?, property: KProperty<*>): T {
   return (T)`$this$getValue`;
}

@Experimental
public fun <T : Any> OptionRegistrar.register(configEntry: ConfigEntry<T>, block: (OptionDsl<T>) -> Unit): Option<T> {
   return `$this$register`.register(getFieldName(configEntry), KotlinExtsKt::register$lambda$2);
}

fun `register$lambda$0$0`(`$entry`: ConfigEntry, var1: EntryAddable, var2: KProperty): ConfigEntry {
   return `$entry`;
}

fun `register$lambda$0`(`$default`: Any, `$codec`: Codec, thisRef: EntryAddable, property: KProperty): ReadOnlyProperty {
   return KotlinExtsKt::register$lambda$0$0;
}

fun `register$lambda$1`(`$fieldName`: java.lang.String, `$configInstance`: CodecConfig, thisRef: EntryAddable, property: KProperty): CodecConfig {
   var var10001: java.lang.String = `$fieldName`;
   if (`$fieldName` == null) {
      var10001 = property.getName();
   }

   thisRef.register(var10001, `$configInstance`);
   return `$configInstance`;
}

fun `register$lambda$2`(`$configEntry`: ConfigEntry, `$block`: Function1, `$this$register`: OptionDsl): Unit {
   `$this$register`.binding(`$configEntry`.asBinding());
   `$block`.invoke(`$this$register`);
   return Unit.INSTANCE;
}
