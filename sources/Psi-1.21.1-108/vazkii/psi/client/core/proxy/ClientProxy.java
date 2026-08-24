package vazkii.psi.client.core.proxy;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import java.util.Objects;
import java.util.SequencedMap;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.neoforged.neoforge.client.event.ModelEvent.ModifyBakingResult;
import net.neoforged.neoforge.client.event.ModelEvent.RegisterAdditional;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.ClientPsiAPI;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.cad.ICADColorizer;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.client.fx.FXSparkle;
import vazkii.psi.client.fx.FXWisp;
import vazkii.psi.client.fx.ModParticles;
import vazkii.psi.client.fx.SparkleParticleData;
import vazkii.psi.client.fx.WispParticleData;
import vazkii.psi.client.gui.GuiCADAssembler;
import vazkii.psi.client.gui.GuiFlashRing;
import vazkii.psi.client.gui.GuiProgrammer;
import vazkii.psi.client.model.ArmorModels;
import vazkii.psi.client.model.ModModelLayers;
import vazkii.psi.client.model.ModelCAD;
import vazkii.psi.client.model.ModelPsimetalExosuit;
import vazkii.psi.client.render.entity.RenderSpellCircle;
import vazkii.psi.client.render.entity.RenderSpellProjectile;
import vazkii.psi.client.render.spell.SpellPieceMaterial;
import vazkii.psi.client.render.tile.RenderTileProgrammer;
import vazkii.psi.common.Psi;
import vazkii.psi.common.block.base.ModBlocks;
import vazkii.psi.common.block.tile.TileProgrammer;
import vazkii.psi.common.core.proxy.IProxy;
import vazkii.psi.common.entity.ModEntities;
import vazkii.psi.common.item.base.ModItems;
import vazkii.psi.mixin.client.AccessorRenderBuffers;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(
   value = {Dist.CLIENT},
   modid = "psi"
)
public class ClientProxy implements IProxy {
   @SubscribeEvent
   public static void registerEntityRenderers(RegisterRenderers evt) {
      evt.registerBlockEntityRenderer((BlockEntityType)ModBlocks.programmerType.get(), RenderTileProgrammer::new);
      evt.registerEntityRenderer(ModEntities.spellCircle, RenderSpellCircle::new);
      evt.registerEntityRenderer(ModEntities.spellCharge, RenderSpellProjectile::new);
      evt.registerEntityRenderer(ModEntities.spellGrenade, RenderSpellProjectile::new);
      evt.registerEntityRenderer(ModEntities.spellProjectile, RenderSpellProjectile::new);
      evt.registerEntityRenderer(ModEntities.spellMine, RenderSpellProjectile::new);
   }

   @SubscribeEvent
   public static void registerEntityLayers(RegisterLayerDefinitions evt) {
      evt.registerLayerDefinition(ModModelLayers.PSIMETAL_EXOSUIT_INNER_ARMOR, () -> LayerDefinition.create(ModelPsimetalExosuit.createInsideMesh(), 64, 128));
      evt.registerLayerDefinition(ModModelLayers.PSIMETAL_EXOSUIT_OUTER_ARMOR, () -> LayerDefinition.create(ModelPsimetalExosuit.createOutsideMesh(), 64, 128));
   }

   @SubscribeEvent
   public static void registerParticles(RegisterParticleProvidersEvent evt) {
      evt.registerSpriteSet((ParticleType)ModParticles.WISP.get(), FXWisp.Factory::new);
      evt.registerSpriteSet((ParticleType)ModParticles.SPARKLE.get(), FXSparkle.Factory::new);
   }

   @SubscribeEvent
   public static void registerMenuScreens(RegisterMenuScreensEvent evt) {
      evt.register((MenuType)ModBlocks.containerCADAssembler.get(), GuiCADAssembler::new);
   }

   @OnlyIn(Dist.CLIENT)
   @SubscribeEvent
   public static void initializeClient(RegisterClientExtensionsEvent event) {
      event.registerItem(
         new IClientItemExtensions() {
            @NotNull
            public HumanoidModel<?> getHumanoidArmorModel(
               @NotNull LivingEntity livingEntity, @NotNull ItemStack itemStack, @NotNull EquipmentSlot equipmentSlot, @NotNull HumanoidModel<?> original
            ) {
               return Objects.requireNonNull(ArmorModels.get(itemStack));
            }
         },
         new Holder[]{ModItems.psimetalExosuitHelmet, ModItems.psimetalExosuitChestplate, ModItems.psimetalExosuitLeggings, ModItems.psimetalExosuitBoots}
      );
      ResourceLocation activeProperty = Psi.location("active");
      ItemPropertyFunction hasSpellPredicate = (stack, level, entity, seed) -> ISpellAcceptor.hasSpell(stack) ? 1.0F : 0.0F;
      ItemProperties.register((Item)ModItems.spellBullet.get(), activeProperty, hasSpellPredicate);
      ItemProperties.register((Item)ModItems.chargeSpellBullet.get(), activeProperty, hasSpellPredicate);
      ItemProperties.register((Item)ModItems.projectileSpellBullet.get(), activeProperty, hasSpellPredicate);
      ItemProperties.register((Item)ModItems.loopSpellBullet.get(), activeProperty, hasSpellPredicate);
      ItemProperties.register((Item)ModItems.circleSpellBullet.get(), activeProperty, hasSpellPredicate);
      ItemProperties.register((Item)ModItems.mineSpellBullet.get(), activeProperty, hasSpellPredicate);
      ItemProperties.register((Item)ModItems.flashRing.get(), activeProperty, hasSpellPredicate);
   }

   @Override
   public void registerHandlers(IEventBus bus) {
      bus.addListener(this::modelBake);
      bus.addListener(this::addCADModels);
      bus.addListener(this::loadComplete);
      bus.addListener(this::registerRegistries);
      SpellPieceMaterial.SPELL_PIECE_MATERIAL.register(bus);
   }

   private void registerRegistries(NewRegistryEvent event) {
      event.register(ClientPsiAPI.SPELL_PIECE_MATERIAL_REGISTRY);
   }

   private void loadComplete(FMLLoadCompleteEvent event) {
      event.enqueueWork(() -> {
         SequencedMap<RenderType, ByteBufferBuilder> map = ((AccessorRenderBuffers)Minecraft.getInstance().renderBuffers().bufferSource()).getFixedBuffers();
         RenderType layer = SpellPiece.getLayer();
         map.put(layer, new ByteBufferBuilder(layer.bufferSize()));
         map.put(GuiProgrammer.LAYER, new ByteBufferBuilder(GuiProgrammer.LAYER.bufferSize()));
      });
   }

   private void modelBake(ModifyBakingResult event) {
      event.getModels()
         .computeIfPresent(ModelResourceLocation.inventory(BuiltInRegistries.ITEM.getKey((Item)ModItems.cad.get())), (k, oldModel) -> new ModelCAD());
   }

   private void addCADModels(RegisterAdditional event) {
      event.register(ModelResourceLocation.standalone(Psi.location("item/cad_iron")));
      event.register(ModelResourceLocation.standalone(Psi.location("item/cad_gold")));
      event.register(ModelResourceLocation.standalone(Psi.location("item/cad_psimetal")));
      event.register(ModelResourceLocation.standalone(Psi.location("item/cad_ebony_psimetal")));
      event.register(ModelResourceLocation.standalone(Psi.location("item/cad_ivory_psimetal")));
      event.register(ModelResourceLocation.standalone(Psi.location("item/cad_creative")));
   }

   @Override
   public boolean hasAdvancement(ResourceLocation advancementLocation, Player playerEntity) {
      if (playerEntity instanceof LocalPlayer clientPlayerEntity) {
         return clientPlayerEntity.connection.getAdvancements().get(advancementLocation) != null;
      } else if (playerEntity instanceof ServerPlayer serverPlayerEntity) {
         if (serverPlayerEntity.getServer() == null) {
            return false;
         } else {
            ServerAdvancementManager advancements = serverPlayerEntity.getServer().getAdvancements();
            AdvancementHolder advancement = advancements.get(advancementLocation);
            return advancement != null && serverPlayerEntity.getAdvancements().getOrStartProgress(advancement).isDone();
         }
      } else {
         return false;
      }
   }

   @Override
   public void addParticleForce(Level world, ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
      world.addParticle(particleData, true, x, y, z, xSpeed, ySpeed, zSpeed);
   }

   @Override
   public Player getClientPlayer() {
      return Minecraft.getInstance().player;
   }

   @Override
   public Level getClientWorld() {
      return Minecraft.getInstance().level;
   }

   @Override
   public int getColorForCAD(ItemStack cadStack) {
      ICAD icad = (ICAD)cadStack.getItem();
      return icad.getSpellColor(cadStack);
   }

   @Override
   public int getColorForColorizer(ItemStack colorizer) {
      return !colorizer.isEmpty() && colorizer.getItem() instanceof ICADColorizer icc ? icc.getColor(colorizer) : -15481345;
   }

   @Override
   public void sparkleFX(Level world, double x, double y, double z, float r, float g, float b, float motionX, float motionY, float motionZ, float size, int m) {
      if (m != 0) {
         SparkleParticleData data = new SparkleParticleData(size, r, g, b, m, motionX, motionY, motionZ);
         this.addParticleForce(world, data, x, y, z, motionX, motionY, motionZ);
      }
   }

   @Override
   public void sparkleFX(double x, double y, double z, float r, float g, float b, float motionX, float motionY, float motionZ, float size, int m) {
      this.sparkleFX(Minecraft.getInstance().level, x, y, z, r, g, b, motionX, motionY, motionZ, size, m);
   }

   @Override
   public void wispFX(
      Level world, double x, double y, double z, float r, float g, float b, float size, float motionX, float motionY, float motionZ, float maxAgeMul
   ) {
      if (maxAgeMul != 0.0F) {
         WispParticleData data = new WispParticleData(size, r, g, b, maxAgeMul);
         this.addParticleForce(world, data, x, y, z, motionX, motionY, motionZ);
      }
   }

   @Override
   public void wispFX(double x, double y, double z, float r, float g, float b, float size, float motionX, float motionY, float motionZ, float maxAgeMul) {
      this.wispFX(Minecraft.getInstance().level, x, y, z, r, g, b, size, motionX, motionY, motionZ, maxAgeMul);
   }

   @Override
   public void openProgrammerGUI(TileProgrammer programmer) {
      Minecraft.getInstance().setScreen(new GuiProgrammer(programmer));
   }

   @Override
   public void openFlashRingGUI(ItemStack stack) {
      Minecraft.getInstance().setScreen(new GuiFlashRing(stack));
   }
}
