package vectorwing.farmersdelight.client.event;

import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.client.gui.CookingPotScreen;
import vectorwing.farmersdelight.client.gui.CookingPotTooltip;
import vectorwing.farmersdelight.client.gui.HUDOverlays;
import vectorwing.farmersdelight.client.particle.SparkleParticle;
import vectorwing.farmersdelight.client.particle.StarParticle;
import vectorwing.farmersdelight.client.particle.SteamParticle;
import vectorwing.farmersdelight.client.recipebook.RecipeCategories;
import vectorwing.farmersdelight.client.renderer.CanvasSignRenderer;
import vectorwing.farmersdelight.client.renderer.CuttingBoardRenderer;
import vectorwing.farmersdelight.client.renderer.DefaultStoveRenderer;
import vectorwing.farmersdelight.client.renderer.HangingCanvasSignRenderer;
import vectorwing.farmersdelight.client.renderer.SkilletItemRenderer;
import vectorwing.farmersdelight.client.renderer.SkilletRenderer;
import vectorwing.farmersdelight.common.EnumParameters;
import vectorwing.farmersdelight.common.item.component.ItemStackWrapper;
import vectorwing.farmersdelight.common.registry.ModBlockEntityTypes;
import vectorwing.farmersdelight.common.registry.ModDataComponents;
import vectorwing.farmersdelight.common.registry.ModEntityTypes;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.registry.ModMenuTypes;
import vectorwing.farmersdelight.common.registry.ModParticleTypes;

@EventBusSubscriber(
   modid = "farmersdelight",
   value = {Dist.CLIENT}
)
public class ClientSetupEvents {
   public static void init(FMLClientSetupEvent event) {
      event.enqueueWork(
         () -> ItemProperties.register(
            ModItems.SKILLET.get(),
            ResourceLocation.withDefaultNamespace("cooking"),
            (stack, world, entity, s) -> ((ItemStackWrapper)stack.getOrDefault(ModDataComponents.SKILLET_INGREDIENT, ItemStackWrapper.EMPTY))
                  .getStack()
                  .isEmpty()
               ? 0.0F
               : 1.0F
         )
      );
   }

   @SubscribeEvent
   public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
      event.registerItem(new IClientItemExtensions() {
         BlockEntityWithoutLevelRenderer renderer = new SkilletItemRenderer();

         @NotNull
         public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return this.renderer;
         }

         @Nullable
         public ArmPose getArmPose(LivingEntity living, InteractionHand hand, ItemStack stack) {
            return stack.has((DataComponentType)ModDataComponents.SKILLET_FLIP_TIMESTAMP.get()) ? (ArmPose)EnumParameters.PROXY_SKILLET_FLIP.getValue() : null;
         }
      }, new Item[]{ModItems.SKILLET.get()});
   }

   @SubscribeEvent
   public static void registerRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {
      RecipeCategories.init(event);
   }

   @SubscribeEvent
   public static void registerCustomTooltipRenderers(RegisterClientTooltipComponentFactoriesEvent event) {
      event.register(CookingPotTooltip.CookingPotTooltipComponent.class, CookingPotTooltip::new);
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void registerGuiLayers(RegisterGuiLayersEvent event) {
      HUDOverlays.register(event);
   }

   @SubscribeEvent
   public static void onRegisterRenderers(RegisterRenderers event) {
      event.registerEntityRenderer(ModEntityTypes.ROTTEN_TOMATO.get(), ThrownItemRenderer::new);
      event.registerBlockEntityRenderer(ModBlockEntityTypes.STOVE.get(), DefaultStoveRenderer::new);
      event.registerBlockEntityRenderer(ModBlockEntityTypes.CUTTING_BOARD.get(), CuttingBoardRenderer::new);
      event.registerBlockEntityRenderer(ModBlockEntityTypes.CANVAS_SIGN.get(), CanvasSignRenderer::new);
      event.registerBlockEntityRenderer(ModBlockEntityTypes.HANGING_CANVAS_SIGN.get(), HangingCanvasSignRenderer::new);
      event.registerBlockEntityRenderer(ModBlockEntityTypes.SKILLET.get(), SkilletRenderer::new);
   }

   @SubscribeEvent
   public static void registerMenuScreens(RegisterMenuScreensEvent event) {
      event.register(ModMenuTypes.COOKING_POT.get(), CookingPotScreen::new);
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void registerParticles(RegisterParticleProvidersEvent event) {
      event.registerSpriteSet((ParticleType)ModParticleTypes.STAR.get(), StarParticle.Factory::new);
      event.registerSpriteSet((ParticleType)ModParticleTypes.STEAM.get(), SteamParticle.Factory::new);
      event.registerSpriteSet((ParticleType)ModParticleTypes.SPARKLE.get(), SparkleParticle.Factory::new);
   }
}
