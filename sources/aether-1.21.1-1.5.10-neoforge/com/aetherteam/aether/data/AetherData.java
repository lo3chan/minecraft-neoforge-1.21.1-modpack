package com.aetherteam.aether.data;

import com.aetherteam.aether.data.generators.AetherAdvancementData;
import com.aetherteam.aether.data.generators.AetherBlockStateData;
import com.aetherteam.aether.data.generators.AetherDataMapData;
import com.aetherteam.aether.data.generators.AetherItemModelData;
import com.aetherteam.aether.data.generators.AetherLanguageData;
import com.aetherteam.aether.data.generators.AetherLootModifierData;
import com.aetherteam.aether.data.generators.AetherLootTableData;
import com.aetherteam.aether.data.generators.AetherRecipeData;
import com.aetherteam.aether.data.generators.AetherRegistrySets;
import com.aetherteam.aether.data.generators.AetherSoundData;
import com.aetherteam.aether.data.generators.tags.AetherBiomeTagData;
import com.aetherteam.aether.data.generators.tags.AetherBlockTagData;
import com.aetherteam.aether.data.generators.tags.AetherDamageTypeTagData;
import com.aetherteam.aether.data.generators.tags.AetherEntityTagData;
import com.aetherteam.aether.data.generators.tags.AetherFluidTagData;
import com.aetherteam.aether.data.generators.tags.AetherItemTagData;
import com.aetherteam.aether.data.generators.tags.AetherSoundTagData;
import com.aetherteam.aether.data.generators.tags.AetherStructureTagData;
import com.aetherteam.aether.data.resources.AetherMobCategory;
import com.google.common.reflect.Reflection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.DetectedVersion;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class AetherData {
   public static void dataSetup(GatherDataEvent event) {
      DataGenerator generator = event.getGenerator();
      ExistingFileHelper fileHelper = event.getExistingFileHelper();
      CompletableFuture<Provider> lookupProvider = event.getLookupProvider();
      PackOutput packOutput = generator.getPackOutput();
      Reflection.initialize(new Class[]{AetherMobCategory.class});
      generator.addProvider(event.includeClient(), new AetherBlockStateData(packOutput, fileHelper));
      generator.addProvider(event.includeClient(), new AetherItemModelData(packOutput, fileHelper));
      generator.addProvider(event.includeClient(), new AetherLanguageData(packOutput));
      generator.addProvider(event.includeClient(), new AetherSoundData(packOutput, fileHelper));
      generator.addProvider(event.includeServer(), new AetherRecipeData(packOutput, lookupProvider));
      generator.addProvider(event.includeServer(), AetherLootTableData.create(packOutput, lookupProvider));
      generator.addProvider(event.includeServer(), new AetherLootModifierData(packOutput, lookupProvider));
      generator.addProvider(event.includeServer(), new AetherAdvancementData(packOutput, lookupProvider, fileHelper));
      generator.addProvider(event.includeServer(), new AetherDataMapData(packOutput, lookupProvider));
      DatapackBuiltinEntriesProvider registrySets = new AetherRegistrySets(packOutput, lookupProvider);
      CompletableFuture<Provider> registryProvider = registrySets.getRegistryProvider();
      generator.addProvider(event.includeServer(), registrySets);
      AetherBlockTagData blockTags = new AetherBlockTagData(packOutput, lookupProvider, fileHelper);
      generator.addProvider(event.includeServer(), blockTags);
      generator.addProvider(event.includeServer(), new AetherItemTagData(packOutput, lookupProvider, blockTags.contentsGetter(), fileHelper));
      generator.addProvider(event.includeServer(), new AetherEntityTagData(packOutput, lookupProvider, fileHelper));
      generator.addProvider(event.includeServer(), new AetherFluidTagData(packOutput, lookupProvider, fileHelper));
      generator.addProvider(event.includeServer(), new AetherBiomeTagData(packOutput, lookupProvider, fileHelper));
      generator.addProvider(event.includeServer(), new AetherStructureTagData(packOutput, registryProvider, fileHelper));
      generator.addProvider(event.includeServer(), new AetherDamageTypeTagData(packOutput, registryProvider, fileHelper));
      generator.addProvider(event.includeServer(), new AetherSoundTagData(packOutput, registryProvider, fileHelper));
      generator.addProvider(
         true,
         new PackMetadataGenerator(packOutput)
            .add(
               PackMetadataSection.TYPE,
               new PackMetadataSection(
                  Component.translatable("pack.aether.mod.description"),
                  DetectedVersion.BUILT_IN.getPackVersion(PackType.SERVER_DATA),
                  Optional.of(new InclusiveRange(0, 2147483647))
               )
            )
      );
   }
}
