package at.petrak.hexcasting.forge;

import at.petrak.hexcasting.api.advancements.HexAdvancementTriggers;
import at.petrak.hexcasting.api.mod.HexConfig;
import at.petrak.hexcasting.api.mod.HexStatistics;
import at.petrak.hexcasting.common.blocks.behavior.HexComposting;
import at.petrak.hexcasting.common.blocks.behavior.HexStrippables;
import at.petrak.hexcasting.common.casting.PatternRegistryManifest;
import at.petrak.hexcasting.common.casting.actions.spells.OpFlight;
import at.petrak.hexcasting.common.casting.actions.spells.great.OpAltiora;
import at.petrak.hexcasting.common.entities.HexEntities;
import at.petrak.hexcasting.common.items.ItemJewelerHammer;
import at.petrak.hexcasting.common.lib.HexAttributes;
import at.petrak.hexcasting.common.lib.HexBlockEntities;
import at.petrak.hexcasting.common.lib.HexBlockSetTypes;
import at.petrak.hexcasting.common.lib.HexBlocks;
import at.petrak.hexcasting.common.lib.HexCommands;
import at.petrak.hexcasting.common.lib.HexCreativeTabs;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.lib.HexLootFunctions;
import at.petrak.hexcasting.common.lib.HexMobEffects;
import at.petrak.hexcasting.common.lib.HexParticles;
import at.petrak.hexcasting.common.lib.HexPotions;
import at.petrak.hexcasting.common.lib.HexRegistries;
import at.petrak.hexcasting.common.lib.HexSounds;
import at.petrak.hexcasting.common.lib.hex.HexActions;
import at.petrak.hexcasting.common.lib.hex.HexArithmetics;
import at.petrak.hexcasting.common.lib.hex.HexContinuationTypes;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import at.petrak.hexcasting.common.lib.hex.HexSpecialHandlers;
import at.petrak.hexcasting.common.misc.AkashicTreeGrower;
import at.petrak.hexcasting.common.misc.BrainsweepingEvents;
import at.petrak.hexcasting.common.misc.PlayerPositionRecorder;
import at.petrak.hexcasting.common.misc.RegisterMisc;
import at.petrak.hexcasting.common.recipe.HexRecipeStuffRegistry;
import at.petrak.hexcasting.forge.cap.CapSyncers;
import at.petrak.hexcasting.forge.interop.curios.CuriosApiInterop;
import at.petrak.hexcasting.forge.lib.ForgeHexArgumentTypeRegistry;
import at.petrak.hexcasting.forge.lib.ForgeHexLootMods;
import at.petrak.hexcasting.forge.network.ForgePacketHandler;
import at.petrak.hexcasting.forge.network.MsgBrainsweepAck;
import at.petrak.hexcasting.interop.HexInterop;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import org.apache.commons.lang3.tuple.Pair;

@Mod("hexcasting")
public class ForgeHexInitializer {
   private static IEventBus modEventBus;
   private static ModContainer modContainer;

   public ForgeHexInitializer(IEventBus modBus, ModContainer container) {
      modEventBus = modBus;
      modContainer = container;
      initConfig();
      initRegistries();
      initRegistry();
      initListeners();
   }

   private static void initConfig() {
      Pair<ForgeHexConfig, ModConfigSpec> config = new Builder().configure(ForgeHexConfig::new);
      Pair<ForgeHexConfig.Client, ModConfigSpec> clientConfig = new Builder().configure(ForgeHexConfig.Client::new);
      Pair<ForgeHexConfig.Server, ModConfigSpec> serverConfig = new Builder().configure(ForgeHexConfig.Server::new);
      HexConfig.setCommon((HexConfig.CommonConfigAccess)config.getLeft());
      HexConfig.setClient((HexConfig.ClientConfigAccess)clientConfig.getLeft());
      HexConfig.setServer((HexConfig.ServerConfigAccess)serverConfig.getLeft());
      modContainer.registerConfig(Type.COMMON, (IConfigSpec)config.getRight());
      modContainer.registerConfig(Type.CLIENT, (IConfigSpec)clientConfig.getRight());
      modContainer.registerConfig(Type.SERVER, (IConfigSpec)serverConfig.getRight());
   }

   public static void initRegistries() {
      if (BuiltInRegistries.REGISTRY instanceof MappedRegistry<?> rootRegistry) {
         rootRegistry.unfreeze();
         IXplatAbstractions.INSTANCE.getActionRegistry();
         IXplatAbstractions.INSTANCE.getSpecialHandlerRegistry();
         IXplatAbstractions.INSTANCE.getIotaTypeRegistry();
         IXplatAbstractions.INSTANCE.getArithmeticRegistry();
         IXplatAbstractions.INSTANCE.getContinuationTypeRegistry();
         IXplatAbstractions.INSTANCE.getEvalSoundRegistry();
         rootRegistry.freeze();
      }
   }

   private static void initRegistry() {
      bind(Registries.SOUND_EVENT, HexSounds::registerSounds);
      HexBlockSetTypes.registerBlocks(BlockSetType::register);
      bind(Registries.CREATIVE_MODE_TAB, HexCreativeTabs::registerCreativeTabs);
      bind(Registries.BLOCK, HexBlocks::registerBlocks);
      bind(Registries.ITEM, HexBlocks::registerBlockItems);
      bind(Registries.BLOCK_ENTITY_TYPE, HexBlockEntities::registerTiles);
      bind(Registries.ITEM, HexItems::registerItems);
      bind(Registries.RECIPE_SERIALIZER, HexRecipeStuffRegistry::registerSerializers);
      bind(Registries.RECIPE_TYPE, HexRecipeStuffRegistry::registerTypes);
      bind(Registries.TRIGGER_TYPE, HexAdvancementTriggers::registerTriggers);
      bind(Registries.ENTITY_TYPE, HexEntities::registerEntities);
      bind(Registries.ATTRIBUTE, HexAttributes::register);
      bind(Registries.MOB_EFFECT, HexMobEffects::register);
      bind(Registries.POTION, HexPotions::register);
      HexPotions.addRecipes();
      bind(Registries.PARTICLE_TYPE, HexParticles::registerParticles);
      bind(HexRegistries.IOTA_TYPE, HexIotaTypes::registerTypes);
      bind(HexRegistries.ACTION, HexActions::register);
      bind(HexRegistries.SPECIAL_HANDLER, HexSpecialHandlers::register);
      bind(HexRegistries.ARITHMETIC, HexArithmetics::register);
      bind(HexRegistries.CONTINUATION_TYPE, HexContinuationTypes::registerContinuations);
      bind(HexRegistries.EVAL_SOUND, HexEvalSounds::register);
      ForgeHexArgumentTypeRegistry.ARGUMENT_TYPES.register(getModEventBus());
      ForgeHexLootMods.REGISTRY.register(getModEventBus());
      RegisterMisc.register();
   }

   private static <T> void bind(ResourceKey<? extends Registry<T>> registry, Consumer<BiConsumer<T, ResourceLocation>> source) {
      getModEventBus().addListener(event -> {
         if (registry.equals(event.getRegistryKey())) {
            source.accept((t, rl) -> event.register(registry, rl, () -> t));
         }
      });
   }

   private static void initListeners() {
      IEventBus modBus = getModEventBus();
      IEventBus evBus = NeoForge.EVENT_BUS;
      if (FMLLoader.getDist() == Dist.CLIENT) {
         modBus.register(ForgeHexClientInitializer.class);
      }

      modBus.addListener(evt -> evt.enqueueWork(() -> {
         HexComposting.setup();
         HexStrippables.init();
         AkashicTreeGrower.init();
         HexInterop.init();
      }));
      modBus.addListener(ForgePacketHandler::init);
      if (ModList.get().isLoaded("curios")) {
         modBus.addListener(CuriosApiInterop::onInterModEnqueue);
      }

      modBus.addListener(evt -> {
         HexBlocks.registerBlockCreativeTab(evt::accept, evt.getTab());
         HexItems.registerItemCreativeTab(evt, evt.getTab());
      });
      modBus.addListener(evt -> {
         if (evt.getRegistryKey().equals(Registries.ITEM)) {
            HexStatistics.register();
            HexLootFunctions.registerSerializers((lift, id) -> Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, id, lift));
         }
      });
      evBus.addListener(evt -> {
         InteractionResult res = BrainsweepingEvents.interactWithBrainswept(evt.getEntity(), evt.getLevel(), evt.getHand(), evt.getTarget(), null);
         if (res.consumesAction()) {
            evt.setCanceled(true);
            evt.setCancellationResult(res);
         }
      });
      evBus.addListener(evt -> BrainsweepingEvents.copyBrainsweepPostTransformation(evt.getEntity(), evt.getOutcome()));
      evBus.addListener(evt -> {
         if (evt.getEntity() instanceof ServerPlayer splayer) {
            OpFlight.tickDownFlight(splayer);
            OpAltiora.checkPlayerCollision(splayer);
         }
      });
      evBus.addListener(evt -> {
         if (evt.getLevel() instanceof ServerLevel world) {
            PlayerPositionRecorder.updateAllPlayers(world);
         }
      });
      evBus.addListener(evt -> PatternRegistryManifest.processRegistry(evt.getServer().overworld()));
      evBus.addListener(evt -> HexCommands.register(evt.getDispatcher()));
      evBus.addListener(evt -> {
         Optional<BlockPos> pos = evt.getPosition();
         if (!pos.isEmpty()) {
            evt.setCanceled(ItemJewelerHammer.shouldFailToBreak(evt.getEntity(), evt.getState(), pos.get()));
         }
      });
      evBus.addListener(evt -> {
         Entity target = evt.getTarget();
         if (evt.getTarget() instanceof ServerPlayer serverPlayer && target instanceof Mob mob && IXplatAbstractions.INSTANCE.isBrainswept(mob)) {
            ForgePacketHandler.sendToPlayer(serverPlayer, MsgBrainsweepAck.of(mob));
         }
      });
      evBus.addListener(evt -> {
         if (!evt.isSimulated() && evt.getItemAbility() == ItemAbilities.AXE_STRIP) {
            BlockState bs = evt.getState();
            Block output = HexStrippables.STRIPPABLES.get(bs.getBlock());
            if (output != null) {
               evt.setFinalState(output.withPropertiesOf(bs));
            }
         }
      });
      evBus.register(CapSyncers.class);
      modBus.addListener(e -> {
         e.add(EntityType.PLAYER, BuiltInRegistries.ATTRIBUTE.wrapAsHolder(HexAttributes.GRID_ZOOM));
         e.add(EntityType.PLAYER, BuiltInRegistries.ATTRIBUTE.wrapAsHolder(HexAttributes.SCRY_SIGHT));
      });
   }

   private static IEventBus getModEventBus() {
      return modEventBus;
   }
}
