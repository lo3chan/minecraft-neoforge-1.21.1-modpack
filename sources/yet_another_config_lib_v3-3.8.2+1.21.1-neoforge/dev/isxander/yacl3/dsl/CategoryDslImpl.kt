package dev.isxander.yacl3.dsl

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.ConfigCategory.Builder
import java.util.LinkedHashMap
import java.util.Map.Entry
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.network.chat.Component

@SourceDebugExtension(["SMAP\nImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Impl.kt\ndev/isxander/yacl3/dsl/CategoryDslImpl\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 3 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 5 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,310:1\n507#2,7:311\n507#2,7:320\n221#3,2:318\n221#3,2:327\n1#4:329\n14060#5,2:330\n*S KotlinDebug\n*F\n+ 1 Impl.kt\ndev/isxander/yacl3/dsl/CategoryDslImpl\n*L\n150#1:311,7\n152#1:320,7\n151#1:318,2\n153#1:327,2\n140#1:330,2\n*E\n"])
public class CategoryDslImpl(categoryId: String, parent: RootDsl) : CategoryDsl {
   public open val categoryId: String
   private final val parent: RootDsl
   public open val categoryKey: String
   public open val thisCategory: CompletableFuture<ConfigCategory>
   public open val built: CompletableFuture<ConfigCategory>
   private final val builder: Builder
   private final val groupFutures: MutableMap<String, CompletableFuture<GroupDsl>>
   private final val rootOptFutures: MutableMap<String, CompletableFuture<Option<*>>>
   public open val groups: ParentRegistrar<OptionGroup, GroupDsl, OptionRegistrar>
   public open val rootOptions: OptionRegistrar

   init {
      this.categoryId = categoryId;
      this.parent = parent;
      this.categoryKey = "${this.parent.getRootKey()}.category.${this.getCategoryId()}";
      this.thisCategory = new CompletableFuture<>();
      this.built = this.getThisCategory();
      this.builder = ConfigCategory.createBuilder();
      this.groupFutures = new LinkedHashMap<>();
      this.rootOptFutures = new LinkedHashMap<>();
      this.builder.name(Component.translatable(this.getCategoryKey()) as Component);
      this.groups = new ParentRegistrarImpl<>(
         CategoryDslImpl::groups$lambda$0, CategoryDslImpl::groups$lambda$1, CategoryDslImpl::groups$lambda$2, CategoryDslImpl::groups$lambda$3
      );
      this.rootOptions = new OptionRegistrarImpl(CategoryDslImpl::rootOptions$lambda$0, CategoryDslImpl::rootOptions$lambda$1, "${this.getCategoryKey()}.root");
   }

   private fun createGroupFuture(id: String): CompletableFuture<GroupDsl> {
      val var10000: Any = this.groupFutures.computeIfAbsent(id, CategoryDslImpl::createGroupFuture$lambda$1);
      return var10000 as CompletableFuture<GroupDsl>;
   }

   private fun createRootOptFuture(id: String): CompletableFuture<Option<*>> {
      val var10000: Any = this.rootOptFutures.computeIfAbsent(id, CategoryDslImpl::createRootOptFuture$lambda$1);
      return var10000 as CompletableFuture<Option<?>>;
   }

   public override fun name(component: Component) {
      this.builder.name(component);
   }

   public override fun name(block: () -> Component) {
      this.name(block.invoke() as Component);
   }

   public override fun tooltip(block: (TextLineBuilderDsl) -> Unit) {
      this.builder.tooltip(TextLineBuilderDsl.Companion.createText(block));
   }

   public override fun tooltip(vararg component: Component) {
      this.tooltip(CategoryDslImpl::tooltip$lambda$0);
   }

   public open fun build(): ConfigCategory {
      val var1: ConfigCategory = this.builder.build();
      this.getThisCategory().complete(var1);
      this.checkUnresolvedFutures();
      return var1;
   }

   private fun checkUnresolvedFutures() {
      var `$this$forEach$iv`: java.util.Map = this.groupFutures;
      var `result$iv`: LinkedHashMap = new LinkedHashMap();

      for (Entry entry$iv : $this$filterValues$iv.entrySet()) {
         if (!(it.getValue() as CompletableFuture).isDone()) {
            `result$iv`.put(it.getKey(), it.getValue());
         }
      }

      for (Entry element$iv : result$iv.entrySet()) {
         ImplKt.access$getLOGGER$p().error("Future group ${this.getCategoryId()}/${var17.getKey()} was referenced but was never built.");
      }

      `$this$forEach$iv` = this.rootOptFutures;
      `result$iv` = new LinkedHashMap();

      for (Entry entry$ivx : $this$filterValues$iv.entrySet()) {
         if (!(`entry$ivx`.getValue() as CompletableFuture).isDone()) {
            `result$iv`.put(`entry$ivx`.getKey(), `entry$ivx`.getValue());
         }
      }

      for (Entry element$iv : result$iv.entrySet()) {
         ImplKt.access$getLOGGER$p().error("Future option ${this.getCategoryId()}/root/${var19.getKey()} was referenced but was never built.");
      }
   }

   @JvmStatic
   fun `createGroupFuture$lambda$0`(it: java.lang.String): CompletableFuture {
      return new CompletableFuture();
   }

   @JvmStatic
   fun `createGroupFuture$lambda$1`(`$tmp0`: Function1, p0: Any): CompletableFuture {
      return `$tmp0`.invoke(p0) as CompletableFuture<GroupDsl>;
   }

   @JvmStatic
   fun `createRootOptFuture$lambda$0`(it: java.lang.String): CompletableFuture {
      return new CompletableFuture();
   }

   @JvmStatic
   fun `createRootOptFuture$lambda$1`(`$tmp0`: Function1, p0: Any): CompletableFuture {
      return `$tmp0`.invoke(p0) as CompletableFuture<Option<?>>;
   }

   @JvmStatic
   fun `groups$lambda$2$0`(it: GroupDsl): CompletionStage {
      return it.getBuilt();
   }

   @JvmStatic
   fun `groups$lambda$2$1`(`$tmp0`: Function1, p0: Any): CompletionStage {
      return `$tmp0`.invoke(p0) as CompletionStage;
   }

   @JvmStatic
   fun `groups$lambda$3$0`(it: GroupDsl): OptionRegistrar {
      return it.getOptions();
   }

   @JvmStatic
   fun `groups$lambda$3$1`(`$tmp0`: Function1, p0: Any): OptionRegistrar {
      return `$tmp0`.invoke(p0) as OptionRegistrar;
   }

   @JvmStatic
   fun `groups$lambda$0`(`this$0`: CategoryDslImpl, group: OptionGroup, var2: java.lang.String): Unit {
      `this$0`.builder.group(group);
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `groups$lambda$1`(`this$0`: CategoryDslImpl, id: java.lang.String): GroupDsl {
      val var2: GroupDslImpl = new GroupDslImpl(id, `this$0`);
      `this$0`.createGroupFuture(id).complete(var2);
      return var2;
   }

   @JvmStatic
   fun `groups$lambda$2`(`this$0`: CategoryDslImpl, id: java.lang.String): CompletableFuture {
      val var10000: CompletableFuture = `this$0`.createGroupFuture(id).thenCompose(CategoryDslImpl::groups$lambda$2$1);
      return var10000;
   }

   @JvmStatic
   fun `groups$lambda$3`(`this$0`: CategoryDslImpl, id: java.lang.String): CompletableFuture {
      val var10000: CompletableFuture = `this$0`.createGroupFuture(id).thenApply(CategoryDslImpl::groups$lambda$3$1);
      return var10000;
   }

   @JvmStatic
   fun `rootOptions$lambda$0`(`this$0`: CategoryDslImpl, option: Option, id: java.lang.String): Unit {
      val var3: ConfigCategory.Builder = `this$0`.builder.option(option);
      `this$0`.createRootOptFuture(id).complete(option);
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `rootOptions$lambda$1`(`this$0`: CategoryDslImpl, id: java.lang.String): CompletableFuture {
      return `this$0`.createRootOptFuture(id);
   }

   @JvmStatic
   fun `tooltip$lambda$0`(`$component`: Array<Component>, `$this$tooltip`: TextLineBuilderDsl): Unit {
      for (Object element$iv : $component) {
         `$this$tooltip`.unaryPlus((Component)`element$iv`);
      }

      return Unit.INSTANCE;
   }
}
