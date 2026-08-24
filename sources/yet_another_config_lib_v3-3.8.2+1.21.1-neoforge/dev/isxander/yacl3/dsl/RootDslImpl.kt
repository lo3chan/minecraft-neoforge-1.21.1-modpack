package dev.isxander.yacl3.dsl

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.YetAnotherConfigLib
import dev.isxander.yacl3.api.YetAnotherConfigLib.Builder
import dev.isxander.yacl3.gui.YACLScreen
import java.util.LinkedHashMap
import java.util.Map.Entry
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import kotlin.jvm.functions.Function0
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.network.chat.Component

@SourceDebugExtension(["SMAP\nImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Impl.kt\ndev/isxander/yacl3/dsl/RootDslImpl\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 3 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,310:1\n507#2,7:311\n221#3,2:318\n1#4:320\n*S KotlinDebug\n*F\n+ 1 Impl.kt\ndev/isxander/yacl3/dsl/RootDslImpl\n*L\n88#1:311,7\n89#1:318,2\n*E\n"])
public class RootDslImpl(rootId: String) : RootDsl, Buildable<YetAnotherConfigLib> {
   public open val rootId: String
   public open val rootKey: String
   public open val thisRoot: CompletableFuture<YetAnotherConfigLib>
   public open val built: CompletableFuture<YetAnotherConfigLib>
   private final val builder: Builder
   private final val categoryFutures: MutableMap<String, CompletableFuture<CategoryDsl>>
   public open val categories: ParentRegistrar<ConfigCategory, CategoryDsl, ParentRegistrar<OptionGroup, GroupDsl, OptionRegistrar>>

   init {
      this.rootId = rootId;
      this.rootKey = "yacl3.config.${this.getRootId()}";
      this.thisRoot = new CompletableFuture<>();
      this.built = this.getThisRoot();
      this.builder = YetAnotherConfigLib.createBuilder();
      this.categoryFutures = new LinkedHashMap<>();
      this.builder.title(Component.translatable("${this.getRootKey()}.title") as Component);
      this.categories = new ParentRegistrarImpl<>(
         RootDslImpl::categories$lambda$0, RootDslImpl::categories$lambda$1, RootDslImpl::categories$lambda$2, RootDslImpl::categories$lambda$3
      );
   }

   private fun createFuture(id: String): CompletableFuture<CategoryDsl> {
      val var10000: Any = this.categoryFutures.computeIfAbsent(id, RootDslImpl::createFuture$lambda$1);
      return var10000 as CompletableFuture<CategoryDsl>;
   }

   public override fun title(component: Component) {
      this.builder.title(component);
   }

   public override fun title(block: () -> Component) {
      this.title(block.invoke() as Component);
   }

   public override fun screenInit(block: () -> Unit) {
      this.builder.screenInit(RootDslImpl::screenInit$lambda$0);
   }

   public override fun save(block: () -> Unit) {
      this.builder.save(RootDslImpl::save$lambda$0);
   }

   public open fun build(): YetAnotherConfigLib {
      val var1: YetAnotherConfigLib = this.builder.build();
      this.getThisRoot().complete(var1);
      this.checkUnresolvedFutures();
      return var1;
   }

   private fun checkUnresolvedFutures() {
      val `$this$forEach$iv`: java.util.Map = this.categoryFutures;
      val `result$iv`: LinkedHashMap = new LinkedHashMap();

      for (Entry entry$iv : $this$filterValues$iv.entrySet()) {
         if (!(it.getValue() as CompletableFuture).isDone()) {
            `result$iv`.put(it.getKey(), it.getValue());
         }
      }

      for (Entry element$iv : result$iv.entrySet()) {
         ImplKt.access$getLOGGER$p().error("Future category ${var11.getKey()} was referenced but was never built.");
      }
   }

   @JvmStatic
   fun `createFuture$lambda$0`(it: java.lang.String): CompletableFuture {
      return new CompletableFuture();
   }

   @JvmStatic
   fun `createFuture$lambda$1`(`$tmp0`: Function1, p0: Any): CompletableFuture {
      return `$tmp0`.invoke(p0) as CompletableFuture<CategoryDsl>;
   }

   @JvmStatic
   fun `categories$lambda$2$0`(it: CategoryDsl): CompletionStage {
      return it.getBuilt();
   }

   @JvmStatic
   fun `categories$lambda$2$1`(`$tmp0`: Function1, p0: Any): CompletionStage {
      return `$tmp0`.invoke(p0) as CompletionStage;
   }

   @JvmStatic
   fun `categories$lambda$3$0`(it: CategoryDsl): ParentRegistrar {
      return it.getGroups();
   }

   @JvmStatic
   fun `categories$lambda$3$1`(`$tmp0`: Function1, p0: Any): ParentRegistrar {
      return `$tmp0`.invoke(p0) as ParentRegistrar;
   }

   @JvmStatic
   fun `categories$lambda$0`(`this$0`: RootDslImpl, category: ConfigCategory, var2: java.lang.String): Unit {
      `this$0`.builder.category(category);
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `categories$lambda$1`(`this$0`: RootDslImpl, id: java.lang.String): CategoryDsl {
      val var2: CategoryDslImpl = new CategoryDslImpl(id, `this$0`);
      `this$0`.createFuture(id).complete(var2);
      return var2;
   }

   @JvmStatic
   fun `categories$lambda$2`(`this$0`: RootDslImpl, id: java.lang.String): CompletableFuture {
      val var10000: CompletableFuture = `this$0`.createFuture(id).thenCompose(RootDslImpl::categories$lambda$2$1);
      return var10000;
   }

   @JvmStatic
   fun `categories$lambda$3`(`this$0`: RootDslImpl, id: java.lang.String): CompletableFuture {
      val var10000: CompletableFuture = `this$0`.createFuture(id).thenApply(RootDslImpl::categories$lambda$3$1);
      return var10000;
   }

   @JvmStatic
   fun `screenInit$lambda$0`(`$block`: Function0, it: YACLScreen) {
      `$block`.invoke();
   }

   @JvmStatic
   fun `save$lambda$0`(`$block`: Function0) {
      `$block`.invoke();
   }
}
