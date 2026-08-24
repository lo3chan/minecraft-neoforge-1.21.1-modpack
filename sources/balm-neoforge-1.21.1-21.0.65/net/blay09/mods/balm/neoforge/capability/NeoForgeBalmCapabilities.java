package net.blay09.mods.balm.neoforge.capability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.blay09.mods.balm.api.capability.BalmCapabilities;
import net.blay09.mods.balm.api.capability.CapabilityType;
import net.blay09.mods.balm.common.NamespaceResolver;
import net.blay09.mods.balm.neoforge.ModBusEventRegisters;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.BaseCapability;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public record NeoForgeBalmCapabilities(NamespaceResolver namespaceResolver) implements BalmCapabilities {
   private static final Map<ResourceLocation, CapabilityType<?, ?, ?>> types = new ConcurrentHashMap<>();
   private static final Map<String, NeoForgeBalmCapabilities.Registrations> registrations = new ConcurrentHashMap<>();

   @Override
   public <TApi, TContext> TApi getCapability(
      Level level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, TContext context, CapabilityType<Block, TApi, TContext> type
   ) {
      BlockCapability<TApi, TContext> capability = (BlockCapability<TApi, TContext>)type.backingType();
      return (TApi)level.getCapability(capability, pos, state, blockEntity, context);
   }

   @Nullable
   @Override
   public <TApi, TContext> TApi getCapability(Entity entity, @Nullable TContext context, CapabilityType<Entity, TApi, TContext> type) {
      EntityCapability<TApi, TContext> capability = (EntityCapability<TApi, TContext>)type.backingType();
      return (TApi)entity.getCapability(capability, context);
   }

   @Override
   public <TScope, TApi, TContext> CapabilityType<TScope, TApi, TContext> registerType(
      ResourceLocation identifier, Class<TScope> scopeClass, Class<TApi> apiClass, Class<TContext> contextClass
   ) {
      if (scopeClass == Block.class) {
         BlockCapability<TApi, TContext> capability = BlockCapability.create(identifier, apiClass, contextClass);
         CapabilityType<TScope, TApi, TContext> type = new CapabilityType<>(identifier, scopeClass, apiClass, contextClass, capability);
         types.put(identifier, type);
         return type;
      } else if (scopeClass == Entity.class) {
         EntityCapability<TApi, TContext> capability = EntityCapability.create(identifier, apiClass, contextClass);
         CapabilityType<TScope, TApi, TContext> type = new CapabilityType<>(identifier, scopeClass, apiClass, contextClass, capability);
         types.put(identifier, type);
         return type;
      } else {
         throw new IllegalArgumentException("Unsupported scope class: " + scopeClass);
      }
   }

   @Override
   public <TScope, TApi, TContext> CapabilityType<TScope, TApi, TContext> getType(
      ResourceLocation identifier, Class<TScope> scopeClass, Class<TApi> apiClass, Class<TContext> contextClass
   ) {
      CapabilityType<?, ?, ?> type = types.get(identifier);
      if (type == null) {
         type = this.registerType(identifier, scopeClass, apiClass, contextClass);
      }

      if (type.scopeClass() != scopeClass) {
         throw new IllegalArgumentException("Incompatible scope for capability " + identifier + ", expected " + type.scopeClass() + " but got " + scopeClass);
      } else if (type.apiClass() != apiClass) {
         throw new IllegalArgumentException("Incompatible API for capability " + identifier + ", expected " + type.apiClass() + " but got " + apiClass);
      } else if (type.contextClass() != contextClass) {
         throw new IllegalArgumentException(
            "Incompatible context for capability " + identifier + ", expected " + type.contextClass() + " but got " + contextClass
         );
      } else {
         return (CapabilityType<TScope, TApi, TContext>)type;
      }
   }

   @Override
   public <TApi, TContext> void registerProvider(
      ResourceLocation identifier,
      CapabilityType<Block, TApi, TContext> type,
      BiFunction<BlockEntity, TContext, TApi> provider,
      Supplier<List<BlockEntityType<?>>> blockEntityTypes
   ) {
      this.getActiveRegistrations().blockEntityProviders.add(new NeoForgeBalmCapabilities.BlockEntityProviderRegistration<>(type, provider, blockEntityTypes));
   }

   @Override
   public <TApi, TContext> void registerFallbackBlockEntityProvider(
      ResourceLocation identifier, CapabilityType<Block, TApi, TContext> type, BiFunction<BlockEntity, TContext, TApi> provider
   ) {
      this.getActiveRegistrations().fallbackBlockEntityProviders.add(new NeoForgeBalmCapabilities.BlockEntityFallbackProviderRegistration<>(type, provider));
   }

   @Override
   public <TApi, TContext> void registerEntityProvider(
      ResourceLocation identifier,
      CapabilityType<Entity, TApi, TContext> type,
      BiFunction<Entity, TContext, TApi> provider,
      Supplier<List<EntityType<?>>> entityTypes
   ) {
      this.getActiveRegistrations().entityProviders.add(new NeoForgeBalmCapabilities.EntityProviderRegistration<>(type, provider, entityTypes));
   }

   @Override
   public <TApi, TContext> void registerFallbackEntityProvider(
      ResourceLocation identifier, CapabilityType<Entity, TApi, TContext> type, BiFunction<Entity, TContext, TApi> provider
   ) {
      this.getActiveRegistrations().fallbackEntityProviders.add(new NeoForgeBalmCapabilities.EntityFallbackProviderRegistration<>(type, provider));
   }

   public <TApi, TContext> CapabilityType<Block, TApi, TContext> addExistingType(ResourceLocation identifier, BaseCapability<TApi, TContext> capability) {
      if (capability instanceof BlockCapability) {
         CapabilityType<Block, TApi, TContext> type = new CapabilityType<>(
            identifier, Block.class, capability.typeClass(), capability.contextClass(), capability
         );
         types.put(identifier, type);
         return type;
      } else {
         throw new IllegalArgumentException("Unsupported capability type " + capability.getClass());
      }
   }

   public <TApi, TContext> CapabilityType<Entity, TApi, TContext> addExistingEntityType(ResourceLocation identifier, BaseCapability<TApi, TContext> capability) {
      if (capability instanceof EntityCapability) {
         CapabilityType<Entity, TApi, TContext> type = new CapabilityType<>(
            identifier, Entity.class, capability.typeClass(), capability.contextClass(), capability
         );
         types.put(identifier, type);
         return type;
      } else {
         throw new IllegalArgumentException("Unsupported capability type " + capability.getClass());
      }
   }

   private NeoForgeBalmCapabilities.Registrations getActiveRegistrations() {
      return ModBusEventRegisters.getRegistrations(this.namespaceResolver.getDefaultNamespace(), NeoForgeBalmCapabilities.Registrations.class);
   }

   record BlockEntityFallbackProviderRegistration<TApi, TContext>(CapabilityType<Block, TApi, TContext> type, BiFunction<BlockEntity, TContext, TApi> provider) {
   }

   record BlockEntityProviderRegistration<TApi, TContext>(
      CapabilityType<Block, TApi, TContext> type, BiFunction<BlockEntity, TContext, TApi> provider, Supplier<List<BlockEntityType<?>>> blockEntityTypes
   ) {
   }

   record EntityFallbackProviderRegistration<TApi, TContext>(CapabilityType<Entity, TApi, TContext> type, BiFunction<Entity, TContext, TApi> provider) {
   }

   record EntityProviderRegistration<TApi, TContext>(
      CapabilityType<Entity, TApi, TContext> type, BiFunction<Entity, TContext, TApi> provider, Supplier<List<EntityType<?>>> entityTypes
   ) {
   }

   public static class Registrations {
      public final List<NeoForgeBalmCapabilities.BlockEntityProviderRegistration<?, ?>> blockEntityProviders = new ArrayList<>();
      public final List<NeoForgeBalmCapabilities.BlockEntityFallbackProviderRegistration<?, ?>> fallbackBlockEntityProviders = new ArrayList<>();
      public final List<NeoForgeBalmCapabilities.EntityProviderRegistration<?, ?>> entityProviders = new ArrayList<>();
      public final List<NeoForgeBalmCapabilities.EntityFallbackProviderRegistration<?, ?>> fallbackEntityProviders = new ArrayList<>();

      @SubscribeEvent
      public void registerCapabilities(RegisterCapabilitiesEvent event) {
         for (NeoForgeBalmCapabilities.BlockEntityProviderRegistration<?, ?> blockEntityProvider : this.blockEntityProviders) {
            this.doRegister(event, blockEntityProvider);
         }

         for (NeoForgeBalmCapabilities.EntityProviderRegistration<?, ?> entityProvider : this.entityProviders) {
            this.doRegister(event, entityProvider);
         }
      }

      @SubscribeEvent(
         priority = EventPriority.LOWEST
      )
      public void registerFallbackCapabilities(RegisterCapabilitiesEvent event) {
         for (NeoForgeBalmCapabilities.BlockEntityFallbackProviderRegistration<?, ?> fallbackBlockEntityProvider : this.fallbackBlockEntityProviders) {
            this.doRegister(event, fallbackBlockEntityProvider);
         }

         for (NeoForgeBalmCapabilities.EntityFallbackProviderRegistration<?, ?> fallbackEntityProvider : this.fallbackEntityProviders) {
            this.doRegister(event, fallbackEntityProvider);
         }
      }

      private <TApi, TContext> void doRegister(
         RegisterCapabilitiesEvent event, final NeoForgeBalmCapabilities.BlockEntityProviderRegistration<TApi, TContext> registration
      ) {
         Block[] blocks = registration.blockEntityTypes.get().stream().flatMap(it -> it.getValidBlocks().stream()).distinct().toArray(Block[]::new);
         BlockCapability<TApi, TContext> capability = (BlockCapability<TApi, TContext>)registration.type().backingType();
         event.registerBlock(capability, new IBlockCapabilityProvider<TApi, TContext>() {
            @Nullable
            public TApi getCapability(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, @Nullable TContext context) {
               return blockEntity != null ? registration.provider.apply(blockEntity, context) : null;
            }
         }, blocks);
      }

      private <TApi, TContext> void doRegister(
         RegisterCapabilitiesEvent event, final NeoForgeBalmCapabilities.BlockEntityFallbackProviderRegistration<TApi, TContext> registration
      ) {
         BlockCapability<TApi, TContext> capability = (BlockCapability<TApi, TContext>)registration.type().backingType();
         Block[] blocks = BuiltInRegistries.BLOCK_ENTITY_TYPE.stream().flatMap(it -> it.getValidBlocks().stream()).distinct().toArray(Block[]::new);
         event.registerBlock(capability, new IBlockCapabilityProvider<TApi, TContext>() {
            @Nullable
            public TApi getCapability(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, @Nullable TContext context) {
               return blockEntity != null ? registration.provider.apply(blockEntity, context) : null;
            }
         }, blocks);
      }

      private <TApi, TContext> void doRegister(
         RegisterCapabilitiesEvent event, NeoForgeBalmCapabilities.EntityProviderRegistration<TApi, TContext> registration
      ) {
         EntityCapability<TApi, TContext> capability = (EntityCapability<TApi, TContext>)registration.type().backingType();

         for (EntityType<?> entityType : registration.entityTypes.get()) {
            this.registerEntity(event, capability, entityType, registration.provider);
         }
      }

      private <TApi, TContext> void doRegister(
         RegisterCapabilitiesEvent event, NeoForgeBalmCapabilities.EntityFallbackProviderRegistration<TApi, TContext> registration
      ) {
         EntityCapability<TApi, TContext> capability = (EntityCapability<TApi, TContext>)registration.type().backingType();

         for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
            this.registerEntity(event, capability, entityType, registration.provider);
         }
      }

      private <TApi, TContext, TEntity extends Entity> void registerEntity(
         RegisterCapabilitiesEvent event, EntityCapability<TApi, TContext> capability, EntityType<?> entityType, BiFunction<Entity, TContext, TApi> provider
      ) {
         event.registerEntity(capability, entityType, (entity, context) -> provider.apply(entity, (TContext)context));
      }
   }
}
