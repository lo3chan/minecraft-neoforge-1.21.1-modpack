package fuzs.puzzleslib.api.resources.v1;

import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import fuzs.puzzleslib.api.core.v1.ModContainer;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.Pack.Metadata;
import net.minecraft.server.packs.repository.Pack.Position;
import net.minecraft.world.flag.FeatureFlagSet;

public final class PackResourcesHelper {
   private PackResourcesHelper() {
   }

   public static Component getPackTitle(PackType packType) {
      return Component.literal("Generated " + (packType == PackType.CLIENT_RESOURCES ? "Resource" : "Data") + " Pack");
   }

   public static Component getPackDescription(String modId) {
      return (Component)ModLoaderEnvironment.INSTANCE
         .getModContainer(modId)
         .map(ModContainer::getDisplayName)
         .map(name -> Component.literal("Resources for " + name))
         .orElseGet(() -> Component.literal("Resources (" + modId + ")"));
   }

   public static ResourceLocation getBuiltInPack(ResourceLocation resourceLocation, PackType packType) {
      return resourceLocation.withPrefix(
         packType.getDirectory() + "/" + resourceLocation.getNamespace() + "/" + (packType == PackType.CLIENT_RESOURCES ? "resourcepacks" : "datapacks") + "/"
      );
   }

   public static RepositorySource buildClientPack(ResourceLocation id, Supplier<AbstractModPackResources> factory, boolean hidden) {
      return buildClientPack(id, factory, true, Position.TOP, hidden, hidden);
   }

   public static RepositorySource buildClientPack(
      ResourceLocation id, Supplier<AbstractModPackResources> factory, boolean required, Position position, boolean fixedPosition, boolean hidden
   ) {
      return consumer -> consumer.accept(
         AbstractModPackResources.buildPack(
            PackType.CLIENT_RESOURCES,
            id,
            factory,
            getPackTitle(PackType.CLIENT_RESOURCES),
            getPackDescription(id.getNamespace()),
            required,
            position,
            fixedPosition,
            hidden,
            FeatureFlagSet.of()
         )
      );
   }

   public static RepositorySource buildClientPack(
      ResourceLocation id,
      Supplier<AbstractModPackResources> factory,
      Component title,
      Component description,
      boolean required,
      Position position,
      boolean fixedPosition,
      boolean hidden
   ) {
      return consumer -> consumer.accept(
         AbstractModPackResources.buildPack(
            PackType.CLIENT_RESOURCES, id, factory, title, description, required, position, fixedPosition, hidden, FeatureFlagSet.of()
         )
      );
   }

   public static RepositorySource buildServerPack(ResourceLocation id, Supplier<AbstractModPackResources> factory, boolean hidden) {
      return buildServerPack(id, factory, true, Position.TOP, hidden, hidden);
   }

   public static RepositorySource buildServerPack(
      ResourceLocation id, Supplier<AbstractModPackResources> factory, boolean required, Position position, boolean fixedPosition, boolean hidden
   ) {
      return consumer -> consumer.accept(
         AbstractModPackResources.buildPack(
            PackType.SERVER_DATA,
            id,
            factory,
            getPackTitle(PackType.SERVER_DATA),
            getPackDescription(id.getNamespace()),
            required,
            position,
            fixedPosition,
            hidden,
            FeatureFlagSet.of()
         )
      );
   }

   public static RepositorySource buildServerPack(
      ResourceLocation id,
      Supplier<AbstractModPackResources> factory,
      Component title,
      Component description,
      boolean required,
      Position position,
      boolean fixedPosition,
      boolean hidden
   ) {
      return consumer -> consumer.accept(
         AbstractModPackResources.buildPack(
            PackType.SERVER_DATA, id, factory, title, description, required, position, fixedPosition, hidden, FeatureFlagSet.of()
         )
      );
   }

   public static Metadata createPackInfo(
      ResourceLocation resourceLocation, Component descriptionComponent, PackCompatibility packCompatibility, FeatureFlagSet featureFlagSet, boolean hidden
   ) {
      return CommonAbstractions.INSTANCE.createPackInfo(resourceLocation, descriptionComponent, packCompatibility, featureFlagSet, hidden);
   }
}
