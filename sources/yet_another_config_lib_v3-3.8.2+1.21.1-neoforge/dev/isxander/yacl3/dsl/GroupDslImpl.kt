package dev.isxander.yacl3.dsl

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.OptionGroup.Builder
import java.util.LinkedHashMap
import java.util.Map.Entry
import java.util.concurrent.CompletableFuture
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.network.chat.Component

@SourceDebugExtension(["SMAP\nImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Impl.kt\ndev/isxander/yacl3/dsl/GroupDslImpl\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 3 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,310:1\n507#2,7:311\n221#3,2:318\n1#4:320\n*S KotlinDebug\n*F\n+ 1 Impl.kt\ndev/isxander/yacl3/dsl/GroupDslImpl\n*L\n217#1:311,7\n218#1:318,2\n*E\n"])
public class GroupDslImpl(groupId: String, parent: CategoryDsl) : GroupDsl {
   public open val groupId: String
   private final val parent: CategoryDsl
   public open val groupKey: String
   public open val thisGroup: CompletableFuture<OptionGroup>
   public open val built: CompletableFuture<OptionGroup>
   private final val builder: Builder
   private final val optionFutures: MutableMap<String, CompletableFuture<Option<*>>>

   public open var collapsed: Boolean
      internal open set(value) {
         this.collapsed = value;
         this.builder.collapsed(value);
      }


   public open val options: OptionRegistrar

   init {
      this.groupId = groupId;
      this.parent = parent;
      this.groupKey = "${this.parent.getCategoryKey()}.group.${this.getGroupId()}";
      this.thisGroup = new CompletableFuture<>();
      this.built = this.getThisGroup();
      this.builder = OptionGroup.createBuilder();
      this.optionFutures = new LinkedHashMap<>();
      this.builder.name(Component.translatable(this.getGroupKey()) as Component);
      this.setCollapsed(false);
      this.options = new OptionRegistrarImpl(GroupDslImpl::options$lambda$0, GroupDslImpl::options$lambda$1, this.getGroupKey());
   }

   private fun createOptionFuture(id: String): CompletableFuture<Option<*>> {
      val var10000: Any = this.optionFutures.computeIfAbsent(id, GroupDslImpl::createOptionFuture$lambda$1);
      return var10000 as CompletableFuture<Option<?>>;
   }

   public override fun name(component: Component) {
      this.builder.name(component);
   }

   public override fun name(block: () -> Component) {
      this.name(block.invoke() as Component);
   }

   public override fun description(description: OptionDescription) {
      this.builder.description(description);
   }

   public override fun descriptionBuilder(block: (dev.isxander.yacl3.api.OptionDescription.Builder) -> Unit) {
      val var10000: OptionGroup.Builder = this.builder;
      val var2: OptionDescription.Builder = OptionDescription.createBuilder();
      block.invoke(var2);
      var10000.description(var2.build());
   }

   public override fun dev.isxander.yacl3.api.OptionDescription.Builder.addDefaultText(lines: Int?) {
      ExtensionsKt.addDefaultText(`$this$addDefaultText`, "${this.getGroupKey()}.description", lines);
   }

   public override fun collapsed(collapsed: Boolean) {
      this.setCollapsed(collapsed);
   }

   public open fun build(): OptionGroup {
      val var1: OptionGroup = this.builder.build();
      this.getThisGroup().complete(var1);
      this.checkUnresolvedFutures();
      return var1;
   }

   private fun checkUnresolvedFutures() {
      val `$this$forEach$iv`: java.util.Map = this.optionFutures;
      val `result$iv`: LinkedHashMap = new LinkedHashMap();

      for (Entry entry$iv : $this$filterValues$iv.entrySet()) {
         if (!(it.getValue() as CompletableFuture).isDone()) {
            `result$iv`.put(it.getKey(), it.getValue());
         }
      }

      for (Entry element$iv : result$iv.entrySet()) {
         ImplKt.access$getLOGGER$p()
            .error("Future option ${this.parent.getCategoryId()}/${this.getGroupId()}/${var11.getKey()} was referenced but was never built.");
      }
   }

   @JvmStatic
   fun `createOptionFuture$lambda$0`(it: java.lang.String): CompletableFuture {
      return new CompletableFuture();
   }

   @JvmStatic
   fun `createOptionFuture$lambda$1`(`$tmp0`: Function1, p0: Any): CompletableFuture {
      return `$tmp0`.invoke(p0) as CompletableFuture<Option<?>>;
   }

   @JvmStatic
   fun `options$lambda$0`(`this$0`: GroupDslImpl, option: Option, id: java.lang.String): Unit {
      val var3: OptionGroup.Builder = `this$0`.builder.option(option);
      `this$0`.createOptionFuture(id).complete(option);
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `options$lambda$1`(`this$0`: GroupDslImpl, id: java.lang.String): CompletableFuture {
      return `this$0`.createOptionFuture(id);
   }
}
