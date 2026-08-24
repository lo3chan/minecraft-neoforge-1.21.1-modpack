package fuzs.puzzleslib.neoforge.api.data.v2;

import com.google.common.base.CaseFormat;
import fuzs.puzzleslib.neoforge.api.data.v2.core.NeoForgeDataProviderContext;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantment.Builder;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.ExistingFileHelper.ResourceType;
import org.apache.commons.lang3.StringUtils;

@Deprecated
public abstract class AbstractBuiltInDataProvider<T> implements DataProvider {
   private final PackOutput output;
   private final String modId;
   private final CompletableFuture<Provider> registries;
   private final ResourceKey<? extends Registry<T>> registryKey;
   private final ExistingFileHelper fileHelper;
   private final ResourceType resourceType;
   private BootstrapContext<T> bootstrapContext;

   public AbstractBuiltInDataProvider(ResourceKey<? extends Registry<T>> registryKey, NeoForgeDataProviderContext context) {
      this(registryKey, context.getModId(), context.getPackOutput(), context.getRegistries(), context.getFileHelper());
   }

   public AbstractBuiltInDataProvider(
      ResourceKey<? extends Registry<T>> registryKey, String modId, PackOutput output, CompletableFuture<Provider> registries, ExistingFileHelper fileHelper
   ) {
      this.registryKey = registryKey;
      this.output = output;
      this.modId = modId;
      this.registries = registries;
      this.fileHelper = fileHelper;
      this.resourceType = new ResourceType(PackType.SERVER_DATA, ".json", registryKey.location().getPath());
   }

   protected final void add(ResourceKey<T> resourceKey, T value) {
      this.fileHelper.trackGenerated(resourceKey.location(), this.resourceType);
      this.bootstrapContext.register(resourceKey, value);
   }

   protected abstract void addBootstrap(BootstrapContext<T> var1);

   public CompletableFuture<?> run(CachedOutput output) {
      return new DatapackBuiltinEntriesProvider(this.output, this.registries, new RegistrySetBuilder().add(this.registryKey, context -> {
         this.bootstrapContext = context;
         this.addBootstrap(context);
      }), Collections.singleton(this.modId)).run(output);
   }

   public String getName() {
      String name = this.registryKey.location().getPath().replaceAll("\\W", "_");
      name = (String)CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.UPPER_CAMEL).convert(name);
      name = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(name), ' ');
      return name + " Built In Data";
   }

   public abstract static class DamageTypes extends AbstractBuiltInDataProvider<DamageType> {
      public DamageTypes(NeoForgeDataProviderContext context) {
         super(Registries.DAMAGE_TYPE, context);
      }

      public DamageTypes(String modId, PackOutput output, CompletableFuture<Provider> lookupProvider, ExistingFileHelper fileHelper) {
         super(Registries.DAMAGE_TYPE, modId, output, lookupProvider, fileHelper);
      }

      protected void add(ResourceKey<DamageType> resourceKey) {
         this.add(resourceKey, new DamageType(resourceKey.location().getPath(), 0.1F));
      }

      protected void add(ResourceKey<DamageType> resourceKey, DamageEffects damageEffects) {
         this.add(resourceKey, new DamageType(resourceKey.location().getPath(), 0.1F, damageEffects));
      }
   }

   public abstract static class Enchantments extends AbstractBuiltInDataProvider<Enchantment> {
      public Enchantments(NeoForgeDataProviderContext context) {
         super(Registries.ENCHANTMENT, context);
      }

      protected static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> resourceKey, Builder builder) {
         context.register(resourceKey, builder.build(resourceKey.location()));
      }

      protected void add(ResourceKey<Enchantment> resourceKey, Builder builder) {
         this.add(resourceKey, builder.build(resourceKey.location()));
      }
   }

   public abstract static class TrimMaterials extends AbstractBuiltInDataProvider<TrimMaterial> {
      public TrimMaterials(NeoForgeDataProviderContext context) {
         super(Registries.TRIM_MATERIAL, context);
      }

      public TrimMaterials(String modId, PackOutput output, CompletableFuture<Provider> lookupProvider, ExistingFileHelper fileHelper) {
         super(Registries.TRIM_MATERIAL, modId, output, lookupProvider, fileHelper);
      }

      protected void add(ResourceKey<TrimMaterial> resourceKey, Item ingredient, int descriptionColor, float itemModelIndex) {
         this.add(resourceKey, ingredient, descriptionColor, itemModelIndex, Collections.emptyMap());
      }

      protected void add(
         ResourceKey<TrimMaterial> resourceKey,
         Item ingredient,
         int descriptionColor,
         float itemModelIndex,
         Map<Holder<ArmorMaterial>, String> overrideArmorMaterials
      ) {
         Component description = Component.translatable(Util.makeDescriptionId("trim_material", resourceKey.location()))
            .withStyle(Style.EMPTY.withColor(descriptionColor));
         TrimMaterial trimMaterial = TrimMaterial.create(resourceKey.location().getPath(), ingredient, itemModelIndex, description, overrideArmorMaterials);
         this.add(resourceKey, trimMaterial);
      }
   }
}
