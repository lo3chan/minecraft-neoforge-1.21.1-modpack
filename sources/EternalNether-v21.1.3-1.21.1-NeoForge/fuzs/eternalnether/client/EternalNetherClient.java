package fuzs.eternalnether.client;

import fuzs.eternalnether.client.handler.FirstPersonRenderingHandler;
import fuzs.eternalnether.client.renderer.ShieldItemRenderer;
import fuzs.eternalnether.client.renderer.blockentity.NetheriteBellRenderer;
import fuzs.eternalnether.client.renderer.entity.CorporRenderer;
import fuzs.eternalnether.client.renderer.entity.PiglinHunterRenderer;
import fuzs.eternalnether.client.renderer.entity.PiglinPrisonerRenderer;
import fuzs.eternalnether.client.renderer.entity.WarpedEnderManRenderer;
import fuzs.eternalnether.client.renderer.entity.WexRenderer;
import fuzs.eternalnether.client.renderer.entity.WitherSkeletonHorseRenderer;
import fuzs.eternalnether.client.renderer.entity.WitherSkeletonKnightRenderer;
import fuzs.eternalnether.client.renderer.entity.WraitherRenderer;
import fuzs.eternalnether.init.ModBlocks;
import fuzs.eternalnether.init.ModEntityTypes;
import fuzs.eternalnether.init.ModItems;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.BlockEntityRenderersContext;
import fuzs.puzzleslib.api.client.core.v1.context.BuiltinModelItemRendererContext;
import fuzs.puzzleslib.api.client.core.v1.context.ColorProvidersContext;
import fuzs.puzzleslib.api.client.core.v1.context.EntityRenderersContext;
import fuzs.puzzleslib.api.client.core.v1.context.ItemModelPropertiesContext;
import fuzs.puzzleslib.api.client.core.v1.context.LayerDefinitionsContext;
import fuzs.puzzleslib.api.client.event.v1.renderer.RenderHandEvents;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class EternalNetherClient implements ClientModConstructor {
   public void onConstructMod() {
      registerEventHandlers();
   }

   private static void registerEventHandlers() {
      RenderHandEvents.MAIN_HAND.register(FirstPersonRenderingHandler.renderMainHand(InteractionHand.MAIN_HAND));
      RenderHandEvents.OFF_HAND.register(FirstPersonRenderingHandler.renderMainHand(InteractionHand.OFF_HAND)::onRenderMainHand);
   }

   public void onRegisterEntityRenderers(EntityRenderersContext context) {
      context.registerEntityRenderer((EntityType)ModEntityTypes.PIGLIN_PRISONER.value(), PiglinPrisonerRenderer::new);
      context.registerEntityRenderer((EntityType)ModEntityTypes.PIGLIN_HUNTER.value(), PiglinHunterRenderer::new);
      context.registerEntityRenderer((EntityType)ModEntityTypes.WEX.value(), WexRenderer::new);
      context.registerEntityRenderer((EntityType)ModEntityTypes.WARPED_ENDERMAN.value(), WarpedEnderManRenderer::new);
      context.registerEntityRenderer((EntityType)ModEntityTypes.WRAITHER.value(), WraitherRenderer::new);
      context.registerEntityRenderer((EntityType)ModEntityTypes.WITHER_SKELETON_KNIGHT.value(), WitherSkeletonKnightRenderer::new);
      context.registerEntityRenderer((EntityType)ModEntityTypes.CORPOR.value(), CorporRenderer::new);
      context.registerEntityRenderer((EntityType)ModEntityTypes.WITHER_SKELETON_HORSE.value(), WitherSkeletonHorseRenderer::new);
      context.registerEntityRenderer((EntityType)ModEntityTypes.WARPED_ENDER_PEARL.value(), ThrownItemRenderer::new);
   }

   public void onRegisterBlockColorProviders(ColorProvidersContext<Block, BlockColor> context) {
      super.onRegisterBlockColorProviders(context);
   }

   public void onRegisterBlockEntityRenderers(BlockEntityRenderersContext context) {
      context.registerBlockEntityRenderer((BlockEntityType)ModBlocks.NETHERITE_BELL_BLOCK_ENTITY_TYPE.value(), NetheriteBellRenderer::new);
   }

   public void onRegisterBuiltinModelItemRenderers(BuiltinModelItemRendererContext context) {
      context.registerItemRenderer(new ShieldItemRenderer(), new ItemLike[]{(ItemLike)ModItems.GILDED_NETHERITE_SHIELD.value()});
   }

   public void onRegisterItemModelProperties(ItemModelPropertiesContext context) {
      context.registerItemProperty(
         ShieldItemRenderer.BLOCKING_ITEM_MODEL_PROPERTY,
         (itemStack, clientLevel, livingEntity, seed) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack
            ? 1.0F
            : 0.0F,
         new ItemLike[]{(ItemLike)ModItems.GILDED_NETHERITE_SHIELD.value()}
      );
   }

   public ContentRegistrationFlags[] getContentRegistrationFlags() {
      return new ContentRegistrationFlags[]{ContentRegistrationFlags.DYNAMIC_RENDERERS};
   }

   public void onRegisterLayerDefinitions(LayerDefinitionsContext context) {
   }
}
