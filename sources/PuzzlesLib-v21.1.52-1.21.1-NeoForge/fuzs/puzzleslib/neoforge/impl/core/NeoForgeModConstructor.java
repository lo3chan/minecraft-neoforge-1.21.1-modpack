package fuzs.puzzleslib.neoforge.impl.core;

import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v2.context.BiomeModificationsContext;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import fuzs.puzzleslib.impl.item.CopyComponentsRecipe;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.impl.core.context.AbstractNeoForgeContext;
import fuzs.puzzleslib.neoforge.impl.core.context.BiomeModificationsContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.core.context.BlockInteractionsContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.core.context.CompostableBlocksContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.core.context.CreativeModeTabContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.core.context.CreativeTabContentsContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.core.context.DataPackRegistriesContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.core.context.DataPackSourcesContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.core.context.EntityAttributesContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.core.context.EntityAttributesCreateContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.core.context.EntityAttributesModifyContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.core.context.FlammableBlocksContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.core.context.FuelBurnTimesContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.core.context.GameRegistriesContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.core.context.GameplayContentContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.core.context.SpawnPlacementsContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.core.context.VillagerTradesContextNeoForgeImpl;
import java.util.Set;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class NeoForgeModConstructor implements ModConstructorImpl<ModConstructor> {
   public void construct(String modId, ModConstructor modConstructor, Set<ContentRegistrationFlags> contentRegistrationFlags) {
      NeoForgeModContainerHelper.getOptionalModEventBus(modId)
         .ifPresent(
            eventBus -> {
               EntityAttributesContextNeoForgeImpl[] entityAttributesContext = new EntityAttributesContextNeoForgeImpl[1];
               modConstructor.onConstructMod();
               modConstructor.onRegisterGameplayContent(new GameplayContentContextNeoForgeImpl(modId, eventBus));
               BiomeModificationsContextNeoForgeImpl biomeModificationsContext = new BiomeModificationsContextNeoForgeImpl(
                  modId, eventBus, contentRegistrationFlags
               );
               biomeModificationsContext.registerProviderPack();
               modConstructor.onRegisterBiomeModifications((BiomeModificationsContext)biomeModificationsContext);
               modConstructor.onRegisterCompostableBlocks(new CompostableBlocksContextNeoForgeImpl(modId));
               modConstructor.onRegisterCreativeModeTabs(new CreativeModeTabContextNeoForgeImpl(eventBus));
               if (contentRegistrationFlags.contains(ContentRegistrationFlags.COPY_RECIPES)) {
                  DeferredRegister<RecipeSerializer<?>> deferredRegister = DeferredRegister.create(Registries.RECIPE_SERIALIZER, modId);
                  deferredRegister.register(eventBus);
                  CopyComponentsRecipe.registerSerializers(deferredRegister::register);
               }

               eventBus.addListener(event -> event.enqueueWork(() -> {
                  modConstructor.onCommonSetup();
                  modConstructor.onRegisterVillagerTrades(new VillagerTradesContextNeoForgeImpl());
                  modConstructor.onRegisterBiomeModifications((fuzs.puzzleslib.api.core.v1.context.BiomeModificationsContext)biomeModificationsContext);
                  modConstructor.onRegisterFuelBurnTimes(new FuelBurnTimesContextNeoForgeImpl());
                  modConstructor.onRegisterFlammableBlocks(new FlammableBlocksContextNeoForgeImpl());
                  modConstructor.onRegisterBlockInteractions(new BlockInteractionsContextNeoForgeImpl());
               }));
               eventBus.addListener(event -> modConstructor.onRegisterPayloadTypes(NeoForgeProxy.get().createPayloadTypesContext(modId, event)));
               eventBus.addListener(event -> modConstructor.onRegisterSpawnPlacements(new SpawnPlacementsContextNeoForgeImpl(event)));
               eventBus.addListener(
                  event -> {
                     AbstractNeoForgeContext.computeIfAbsent(
                           entityAttributesContext, EntityAttributesContextNeoForgeImpl::new, modConstructor::onRegisterEntityAttributes
                        )
                        .registerForEvent(event);
                     modConstructor.onEntityAttributeCreation(new EntityAttributesCreateContextNeoForgeImpl(event::put));
                  }
               );
               eventBus.addListener(
                  event -> {
                     AbstractNeoForgeContext.computeIfAbsent(
                           entityAttributesContext, EntityAttributesContextNeoForgeImpl::new, modConstructor::onRegisterEntityAttributes
                        )
                        .registerForEvent(event);
                     modConstructor.onEntityAttributeModification(new EntityAttributesModifyContextNeoForgeImpl(event::add));
                  }
               );
               eventBus.addListener(evt -> modConstructor.onBuildCreativeModeTabContents(new CreativeTabContentsContextNeoForgeImpl(evt)));
               eventBus.addListener(event -> {
                  if (event.getPackType() == PackType.SERVER_DATA) {
                     modConstructor.onAddDataPackFinders(new DataPackSourcesContextNeoForgeImpl(event));
                  }
               });
               eventBus.addListener(event -> modConstructor.onRegisterGameRegistries(new GameRegistriesContextNeoForgeImpl(event)));
               eventBus.addListener(event -> modConstructor.onRegisterDataPackRegistries(new DataPackRegistriesContextNeoForgeImpl(event)));
            }
         );
   }
}
