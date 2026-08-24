package dev.isxander.yacl3.dsl

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.YetAnotherConfigLib
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2

public fun <T> CompletableFuture<T>.onReady(block: (T) -> Unit): CompletableFuture<T> {
   return (CompletableFuture<T>)`$this$onReady`.whenComplete(APIKt::onReady$lambda$1);
}

public operator fun <T> CompletableFuture<out ParentRegistrar<*, *, T>>.get(id: String): CompletableFuture<T> {
   val var10000: CompletableFuture = `$this$get`.thenCompose(APIKt::get$lambda$1);
   return var10000;
}

public fun <T> CompletableFuture<OptionRegistrar>.futureRef(id: String): CompletableFuture<Option<T>> {
   val var10000: CompletableFuture = `$this$futureRef`.thenCompose(APIKt::futureRef$lambda$1);
   return var10000;
}

public fun <T> CompletableFuture<OptionRegistrar>.futureRef(): RegisterableDelegateProvider<CompletableFuture<Option<T>>> {
   return new RegisterableDelegateProvider<>(APIKt::futureRef$lambda$2, null);
}

public fun YetAnotherConfigLib(id: String, block: (RootDsl) -> Unit): YetAnotherConfigLib {
   val var2: RootDslImpl = new RootDslImpl(id);
   block.invoke(var2);
   return var2.build();
}

fun `onReady$lambda$0`(`$block`: Function1, result: Any, var2: java.lang.Throwable): Unit {
   if (result != null) {
      `$block`.invoke(result);
   }

   return Unit.INSTANCE;
}

fun `onReady$lambda$1`(`$tmp0`: Function2, p0: Any, p1: Any) {
   `$tmp0`.invoke(p0, p1);
}

fun `get$lambda$0`(`$id`: java.lang.String, it: ParentRegistrar): CompletionStage {
   return it.get(`$id`);
}

fun `get$lambda$1`(`$tmp0`: Function1, p0: Any): CompletionStage {
   return `$tmp0`.invoke(p0) as CompletionStage;
}

fun `futureRef$lambda$0`(`$id`: java.lang.String, it: OptionRegistrar): CompletionStage {
   return it.futureRef(`$id`);
}

fun `futureRef$lambda$1`(`$tmp0`: Function1, p0: Any): CompletionStage {
   return `$tmp0`.invoke(p0) as CompletionStage;
}

fun `futureRef$lambda$2`(`$this_futureRef`: CompletableFuture, it: java.lang.String): CompletableFuture {
   return futureRef(`$this_futureRef`, it);
}
