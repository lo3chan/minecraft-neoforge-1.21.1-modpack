package fuzs.puzzleslib.neoforge.impl.core.context;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingContext;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingPhase;
import fuzs.puzzleslib.api.biome.v1.BiomeModificationContext;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v2.context.BiomeModificationsContext;
import fuzs.puzzleslib.api.resources.v1.DynamicPackResources;
import fuzs.puzzleslib.api.resources.v1.PackResourcesHelper;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import fuzs.puzzleslib.neoforge.impl.biome.BiomeLoadingContextNeoForge;
import fuzs.puzzleslib.neoforge.impl.biome.ClimateSettingsContextNeoForge;
import fuzs.puzzleslib.neoforge.impl.biome.GenerationSettingsContextNeoForge;
import fuzs.puzzleslib.neoforge.impl.biome.MobSpawnSettingsContextNeoForge;
import fuzs.puzzleslib.neoforge.impl.biome.SpecialEffectsContextNeoForge;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.JsonCodecProvider;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifier.Phase;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public final class BiomeModificationsContextNeoForgeImpl implements BiomeModificationsContext, fuzs.puzzleslib.api.core.v1.context.BiomeModificationsContext {
   public static final ExistingFileHelper EMPTY_FILE_HANDLER = new ExistingFileHelper(Collections.emptySet(), Collections.emptySet(), false, null, null);
   private final Multimap<BiomeLoadingPhase, Entry<Predicate<BiomeLoadingContext>, Consumer<BiomeModificationContext>>> biomeModifications = HashMultimap.create();
   private final String modId;
   private final IEventBus eventBus;
   private final Set<ContentRegistrationFlags> contentRegistrationFlags;

   public BiomeModificationsContextNeoForgeImpl(String modId, IEventBus eventBus, Set<ContentRegistrationFlags> contentRegistrationFlags) {
      this.modId = modId;
      this.eventBus = eventBus;
      this.contentRegistrationFlags = contentRegistrationFlags;
   }

   @Override
   public void register(BiomeLoadingPhase biomeLoadingPhase, Predicate<BiomeLoadingContext> biomeSelector, Consumer<BiomeModificationContext> biomeModifier) {
      if (this.contentRegistrationFlags.contains(ContentRegistrationFlags.BIOME_MODIFICATIONS)) {
         Objects.requireNonNull(biomeLoadingPhase, "biome loading phase is null");
         Objects.requireNonNull(biomeSelector, "biome selector is null");
         Objects.requireNonNull(biomeModifier, "biome modifier is null");
         this.biomeModifications.put(biomeLoadingPhase, Map.entry(biomeSelector, biomeModifier));
      } else {
         ContentRegistrationFlags.throwForFlag(ContentRegistrationFlags.BIOME_MODIFICATIONS);
      }
   }

   @Deprecated
   public void registerProviderPack() {
      if (this.contentRegistrationFlags.contains(ContentRegistrationFlags.BIOME_MODIFICATIONS)) {
         BiomeModifier biomeModifier = new BiomeModificationsContextNeoForgeImpl.BiomeModifierImpl();
         Holder<MapCodec<? extends BiomeModifier>> holder = this.registerBiomeModifier(biomeModifier);
         this.eventBus
            .addListener(
               event -> {
                  if (event.getPackType() == PackType.SERVER_DATA) {
                     event.addRepositorySource(
                        PackResourcesHelper.buildServerPack(
                           holder.getKey().location(),
                           DynamicPackResources.create(
                              context -> new JsonCodecProvider<BiomeModifier>(
                                 context.getPackOutput(),
                                 Target.DATA_PACK,
                                 Keys.BIOME_MODIFIERS.location().toString().replace(':', '/'),
                                 PackType.SERVER_DATA,
                                 BiomeModifier.DIRECT_CODEC,
                                 context.getRegistries(),
                                 context.getModId(),
                                 EMPTY_FILE_HANDLER
                              ) {
                                 protected void gather() {
                                    this.unconditional(holder.getKey().location(), biomeModifier);
                                 }
                              }
                           ),
                           true
                        )
                     );
                  }
               }
            );
      }
   }

   @Override
   public void registerBiomeModification(
      BiomeLoadingPhase biomeLoadingPhase, Predicate<BiomeLoadingContext> biomeSelector, Consumer<BiomeModificationContext> biomeModifier
   ) {
      if (!this.contentRegistrationFlags.contains(ContentRegistrationFlags.BIOME_MODIFICATIONS)) {
         Objects.requireNonNull(biomeLoadingPhase, "biome loading phase is null");
         Objects.requireNonNull(biomeSelector, "biome selector is null");
         Objects.requireNonNull(biomeModifier, "biome modifier is null");
         if (this.biomeModifications.isEmpty()) {
            this.registerDataProvider();
         }

         this.biomeModifications.put(biomeLoadingPhase, Map.entry(biomeSelector, biomeModifier));
      } else {
         ContentRegistrationFlags.throwForFlag(ContentRegistrationFlags.BIOME_MODIFICATIONS);
      }
   }

   private void registerDataProvider() {
      BiomeModifier biomeModifierImpl = new BiomeModificationsContextNeoForgeImpl.BiomeModifierImpl();
      Holder<MapCodec<? extends BiomeModifier>> holder = this.registerBiomeModifier(biomeModifierImpl);
      DataProviderHelper.registerDataProviders(
         this.modId,
         context -> new JsonCodecProvider<BiomeModifier>(
            context.getPackOutput(),
            Target.DATA_PACK,
            Keys.BIOME_MODIFIERS.location().toString().replace(':', '/'),
            PackType.SERVER_DATA,
            BiomeModifier.DIRECT_CODEC,
            context.getRegistries(),
            context.getModId(),
            context.getFileHelper()
         ) {
            protected void gather() {
               this.unconditional(holder.getKey().location(), biomeModifierImpl);
            }
         }
      );
   }

   private Holder<MapCodec<? extends BiomeModifier>> registerBiomeModifier(BiomeModifier biomeModifier) {
      DeferredRegister<MapCodec<? extends BiomeModifier>> deferredRegister = DeferredRegister.create(Keys.BIOME_MODIFIER_SERIALIZERS, this.modId);
      deferredRegister.register(this.eventBus);
      return deferredRegister.register("biome_modifications", biomeModifier::codec);
   }

   private class BiomeModifierImpl implements BiomeModifier {
      private static final Map<Phase, BiomeLoadingPhase> BIOME_PHASE_CONVERSIONS = Maps.immutableEnumMap(
         ImmutableMap.of(
            Phase.ADD,
            BiomeLoadingPhase.ADDITIONS,
            Phase.REMOVE,
            BiomeLoadingPhase.REMOVALS,
            Phase.MODIFY,
            BiomeLoadingPhase.MODIFICATIONS,
            Phase.AFTER_EVERYTHING,
            BiomeLoadingPhase.POST_PROCESSING
         )
      );
      private final MapCodec<? extends BiomeModifier> codec = MapCodec.unit(this);

      public void modify(Holder<Biome> holder, Phase phase, Builder builder) {
         BiomeLoadingPhase biomeLoadingPhase = BIOME_PHASE_CONVERSIONS.get(phase);
         if (biomeLoadingPhase != null) {
            Collection<Entry<Predicate<BiomeLoadingContext>, Consumer<BiomeModificationContext>>> biomeModification = BiomeModificationsContextNeoForgeImpl.this.biomeModifications
               .get(biomeLoadingPhase);
            if (!biomeModification.isEmpty()) {
               MinecraftServer minecraftServer = ServerLifecycleHooks.getCurrentServer();
               Objects.requireNonNull(minecraftServer, "minecraft server is null");
               RegistryAccess registryAccess = minecraftServer.registryAccess();
               BiomeLoadingContext biomeLoadingContext = new BiomeLoadingContextNeoForge(registryAccess, holder);
               BiomeModificationContext biomeModificationContext = createModificationContext(registryAccess, builder);

               for (Entry<Predicate<BiomeLoadingContext>, Consumer<BiomeModificationContext>> entry : biomeModification) {
                  if (entry.getKey().test(biomeLoadingContext)) {
                     entry.getValue().accept(biomeModificationContext);
                  }
               }
            }
         }
      }

      private static BiomeModificationContext createModificationContext(RegistryAccess registryAccess, Builder builder) {
         ClimateSettingsContextNeoForge climateSettings = new ClimateSettingsContextNeoForge(builder.getClimateSettings());
         SpecialEffectsContextNeoForge specialEffects = new SpecialEffectsContextNeoForge(builder.getSpecialEffects());
         GenerationSettingsContextNeoForge generationSettings = new GenerationSettingsContextNeoForge(registryAccess, builder.getGenerationSettings());
         MobSpawnSettingsContextNeoForge mobSpawnSettings = new MobSpawnSettingsContextNeoForge(builder.getMobSpawnSettings());
         return new BiomeModificationContext(climateSettings, specialEffects, generationSettings, mobSpawnSettings);
      }

      public MapCodec<? extends BiomeModifier> codec() {
         return this.codec;
      }
   }
}
