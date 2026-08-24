package fuzs.puzzleslib.api.core.v1;

import fuzs.puzzleslib.api.core.v1.context.BlockInteractionsContext;
import fuzs.puzzleslib.api.core.v1.context.BuildCreativeModeTabContentsContext;
import fuzs.puzzleslib.api.core.v1.context.CompostableBlocksContext;
import fuzs.puzzleslib.api.core.v1.context.CreativeModeTabContext;
import fuzs.puzzleslib.api.core.v1.context.DataPackRegistriesContext;
import fuzs.puzzleslib.api.core.v1.context.EntityAttributesContext;
import fuzs.puzzleslib.api.core.v1.context.EntityAttributesCreateContext;
import fuzs.puzzleslib.api.core.v1.context.EntityAttributesModifyContext;
import fuzs.puzzleslib.api.core.v1.context.FlammableBlocksContext;
import fuzs.puzzleslib.api.core.v1.context.FuelBurnTimesContext;
import fuzs.puzzleslib.api.core.v1.context.GameRegistriesContext;
import fuzs.puzzleslib.api.core.v1.context.GameplayContentContext;
import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.api.core.v1.context.PayloadTypesContext;
import fuzs.puzzleslib.api.core.v1.context.SpawnPlacementsContext;
import fuzs.puzzleslib.api.core.v1.context.VillagerTradesContext;
import fuzs.puzzleslib.api.core.v2.context.BiomeModificationsContext;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

public interface ModConstructor extends BaseModConstructor {
   static void construct(String modId, Supplier<ModConstructor> modConstructorSupplier) {
      construct(ResourceLocation.fromNamespaceAndPath(modId, "common"), modConstructorSupplier);
   }

   static void construct(ResourceLocation resourceLocation, Supplier<ModConstructor> modConstructorSupplier) {
      ModConstructorImpl.construct(resourceLocation, modConstructorSupplier, ProxyImpl.get()::getModConstructorImpl, ModContext::runBeforeConstruction);
   }

   default void onConstructMod() {
   }

   default void onCommonSetup() {
   }

   default void onRegisterPayloadTypes(PayloadTypesContext context) {
   }

   default void onRegisterEntityAttributes(EntityAttributesContext context) {
   }

   default void onRegisterSpawnPlacements(SpawnPlacementsContext context) {
   }

   @Deprecated
   default void onEntityAttributeCreation(EntityAttributesCreateContext context) {
   }

   @Deprecated
   default void onEntityAttributeModification(EntityAttributesModifyContext context) {
   }

   default void onRegisterBiomeModifications(BiomeModificationsContext context) {
   }

   @Deprecated
   default void onRegisterBiomeModifications(fuzs.puzzleslib.api.core.v1.context.BiomeModificationsContext context) {
   }

   default void onRegisterGameplayContent(GameplayContentContext context) {
   }

   @Deprecated
   default void onRegisterFuelBurnTimes(FuelBurnTimesContext context) {
   }

   @Deprecated
   default void onRegisterFlammableBlocks(FlammableBlocksContext context) {
   }

   @Deprecated
   default void onRegisterCompostableBlocks(CompostableBlocksContext context) {
   }

   @Deprecated
   default void onRegisterBlockInteractions(BlockInteractionsContext context) {
   }

   @Deprecated
   default void onRegisterCreativeModeTabs(CreativeModeTabContext context) {
   }

   @Deprecated
   default void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsContext context) {
   }

   default void onAddDataPackFinders(PackRepositorySourcesContext context) {
   }

   default void onRegisterGameRegistries(GameRegistriesContext context) {
   }

   default void onRegisterDataPackRegistries(DataPackRegistriesContext context) {
   }

   default void onRegisterVillagerTrades(VillagerTradesContext context) {
   }
}
