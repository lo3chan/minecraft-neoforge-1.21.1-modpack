package net.joefoxe.hexerei.event;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.util.List;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.client.renderer.entity.model.ArmorModels;
import net.joefoxe.hexerei.config.HexConfig;
import net.joefoxe.hexerei.container.ModContainers;
import net.joefoxe.hexerei.fluid.ModFluidTypes;
import net.joefoxe.hexerei.fluid.PotionFluidType;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.BookCanvasItemRenderer;
import net.joefoxe.hexerei.item.custom.BroomItemRenderer;
import net.joefoxe.hexerei.item.custom.BroomKeychainItemRenderer;
import net.joefoxe.hexerei.item.custom.CandleItemRenderer;
import net.joefoxe.hexerei.item.custom.ChestItemRenderer;
import net.joefoxe.hexerei.item.custom.CrowBlankAmuletItemRenderer;
import net.joefoxe.hexerei.item.custom.HerbJarItemRenderer;
import net.joefoxe.hexerei.item.custom.HexereiBookItemRenderer;
import net.joefoxe.hexerei.item.custom.MixingCauldronItemRenderer;
import net.joefoxe.hexerei.item.custom.WitchArmorItem;
import net.joefoxe.hexerei.light.LightManager;
import net.joefoxe.hexerei.screen.BroomScreen;
import net.joefoxe.hexerei.screen.CofferScreen;
import net.joefoxe.hexerei.screen.CourierPackageScreen;
import net.joefoxe.hexerei.screen.CrowFluteScreen;
import net.joefoxe.hexerei.screen.CrowScreen;
import net.joefoxe.hexerei.screen.HerbJarScreen;
import net.joefoxe.hexerei.screen.MixingCauldronScreen;
import net.joefoxe.hexerei.screen.OwlScreen;
import net.joefoxe.hexerei.screen.WoodcutterScreen;
import net.joefoxe.hexerei.tileentity.renderer.CofferRenderer;
import net.joefoxe.hexerei.tileentity.renderer.CrystalBallRenderer;
import net.joefoxe.hexerei.util.ClientProxy;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent.Pre;
import net.neoforged.neoforge.client.event.ModelEvent.RegisterAdditional;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class ClientEvents {
   public static ShaderInstance hueSliderShader;
   public static ShaderInstance sliderShader;
   public static ShaderInstance bookTranslucentShader;
   static float clientTicks = 0.0F;
   static float clientTicksPartial = 0.0F;

   @SubscribeEvent
   public static void clientTickEvent(Pre event) {
      clientTicks++;
      if (ClientProxy.fontList.isEmpty()) {
         for (String str : (List)HexConfig.FONT_LIST.get()) {
            if (!ClientProxy.fontList.containsKey(str)) {
               ClientProxy.fontList
                  .put(
                     str,
                     new Font(
                        p_95014_ -> Minecraft.getInstance()
                           .fontManager
                           .fontSets
                           .getOrDefault(ResourceLocation.parse(str), Minecraft.getInstance().fontManager.missingFontSet),
                        false
                     )
                  );
            }
         }
      }
   }

   public static float getClientTicks() {
      return clientTicks + getPartial();
   }

   public static float getClientTicksWithoutPartial() {
      return clientTicks;
   }

   public static float getPartial() {
      return clientTicksPartial;
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void renderWorldLastEvent(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_TRIPWIRE_BLOCKS) {
         LightManager.updateAll(event.getLevelRenderer());
      }

      if (event.getStage() == Stage.AFTER_SKY) {
         clientTicksPartial = event.getPartialTick().getGameTimeDeltaPartialTick(false);
      }
   }

   @SubscribeEvent
   public static void registerMenu(RegisterMenuScreensEvent event) {
      event.register((MenuType)ModContainers.MIXING_CAULDRON_CONTAINER.get(), MixingCauldronScreen::new);
      event.register((MenuType)ModContainers.COFFER_CONTAINER.get(), CofferScreen::new);
      event.register((MenuType)ModContainers.PACKAGE_CONTAINER.get(), CourierPackageScreen::new);
      event.register((MenuType)ModContainers.HERB_JAR_CONTAINER.get(), HerbJarScreen::new);
      event.register((MenuType)ModContainers.BROOM_CONTAINER.get(), BroomScreen::new);
      event.register((MenuType)ModContainers.CROW_CONTAINER.get(), CrowScreen::new);
      event.register((MenuType)ModContainers.OWL_CONTAINER.get(), OwlScreen::new);
      event.register((MenuType)ModContainers.CROW_FLUTE_CONTAINER.get(), CrowFluteScreen::new);
      event.register((MenuType)ModContainers.WOODCUTTER_CONTAINER.get(), WoodcutterScreen::new);
   }

   @SubscribeEvent
   public static void onRegisterShaders(RegisterShadersEvent event) {
      try {
         event.registerShader(
            new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath("hexerei", "hue_slider"), DefaultVertexFormat.NEW_ENTITY),
            shaderInstance -> hueSliderShader = shaderInstance
         );
         event.registerShader(
            new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath("hexerei", "slider"), DefaultVertexFormat.NEW_ENTITY),
            shaderInstance -> sliderShader = shaderInstance
         );
         event.registerShader(
            new ShaderInstance(
               event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath("hexerei", "book_translucent"), DefaultVertexFormat.NEW_ENTITY
            ),
            shaderInstance -> bookTranslucentShader = shaderInstance
         );
      } catch (Exception var2) {
         System.out.println("shader failed");
         throw new RuntimeException(var2);
      }
   }

   public static void onRegisterAdditionalModels(RegisterAdditional event) {
      event.register(BookCanvasItemRenderer.CANVAS);
      event.register(HerbJarItemRenderer.JAR);
      event.register(CrystalBallRenderer.ORB);
      event.register(CrystalBallRenderer.ORB2);
      event.register(CofferRenderer.ENTANGLED_COFFER_CHEST);
      event.register(CofferRenderer.ENTANGLED_COFFER_CONTAINER);
      event.register(CofferRenderer.ENTANGLED_COFFER_HINGE);
      event.register(CofferRenderer.ENTANGLED_COFFER_LID);
   }

   @OnlyIn(Dist.CLIENT)
   @SubscribeEvent
   public static void tooltipEvent(ItemTooltipEvent event) {
      ItemStack stack = event.getItemStack();
      if (stack.is(ModItems.ENTANGLED_COFFER)) {
         CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
         if (event.getFlags().isAdvanced() && tag.contains("CofferId")) {
            event.getToolTip().add(Component.literal("UUID: " + tag.getUUID("CofferId")).withStyle(ChatFormatting.DARK_GRAY));
         }
      }
   }

   @SubscribeEvent
   public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
      ModBlocks.afterRegisterConsumer.forEach((block, consumer) -> consumer.accept((Block)block.get()));
      ModBlocks.afterRegisterConsumer.clear();
      final BroomItemRenderer broomRenderer = new BroomItemRenderer();
      event.registerItem(new IClientItemExtensions() {
         public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return broomRenderer.getRenderer();
         }
      }, new Item[]{(Item)ModItems.WILLOW_BROOM.get(), (Item)ModItems.MAHOGANY_BROOM.get(), (Item)ModItems.WITCH_HAZEL_BROOM.get()});
      final BookCanvasItemRenderer bookCanvasItemRenderer = new BookCanvasItemRenderer();
      event.registerItem(new IClientItemExtensions() {
         public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return bookCanvasItemRenderer.getRenderer();
         }
      }, new Item[]{(Item)ModItems.BOOK_CANVAS.get()});
      final CandleItemRenderer candleItemRenderer = new CandleItemRenderer();
      event.registerItem(new IClientItemExtensions() {
         public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return candleItemRenderer.getRenderer();
         }
      }, new Item[]{(Item)ModItems.CANDLE.get()});
      final BroomKeychainItemRenderer broomKeychainItemRenderer = new BroomKeychainItemRenderer();
      event.registerItem(new IClientItemExtensions() {
         public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return broomKeychainItemRenderer.getRenderer();
         }
      }, new Item[]{(Item)ModItems.BROOM_KEYCHAIN.get()});
      final ChestItemRenderer chestItemRenderer = new ChestItemRenderer();
      event.registerItem(new IClientItemExtensions() {
         public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return chestItemRenderer.getRenderer();
         }
      }, new Item[]{(Item)ModItems.MAHOGANY_CHEST.get(), (Item)ModItems.WILLOW_CHEST.get(), (Item)ModItems.WITCH_HAZEL_CHEST.get()});
      final MixingCauldronItemRenderer mixingCauldronItemRenderer = new MixingCauldronItemRenderer();
      event.registerItem(new IClientItemExtensions() {
         public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return mixingCauldronItemRenderer.getRenderer();
         }
      }, new Item[]{(Item)ModItems.MIXING_CAULDRON.get()});
      final HerbJarItemRenderer herbJarItemRenderer = new HerbJarItemRenderer();
      event.registerItem(new IClientItemExtensions() {
         public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return herbJarItemRenderer.getRenderer();
         }
      }, new Item[]{(Item)ModItems.HERB_JAR.get()});
      final CrowBlankAmuletItemRenderer crowBlankAmuletItemRenderer = new CrowBlankAmuletItemRenderer();
      event.registerItem(new IClientItemExtensions() {
         public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return crowBlankAmuletItemRenderer.getRenderer();
         }
      }, new Item[]{(Item)ModItems.CROW_BLANK_AMULET.get()});
      final HexereiBookItemRenderer renderer = new HexereiBookItemRenderer();
      event.registerItem(new IClientItemExtensions() {
         public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return renderer.getRenderer();
         }
      }, new Item[]{(Item)ModItems.BOOK_OF_SHADOWS.get(), (Item)ModItems.NOTEBOOK.get(), (Item)ModItems.BOOK_OF_COLORS.get()});
      event.registerItem(
         new IClientItemExtensions() {
            public HumanoidModel<?> getHumanoidArmorModel(
               LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original
            ) {
               return ArmorModels.get(itemStack);
            }

            public int getArmorLayerTintColor(ItemStack stack, LivingEntity entity, Layer layer, int layerIdx, int fallbackColor) {
               return layer.dyeable() && stack.getItem() instanceof WitchArmorItem armoritem
                  ? armoritem.getColor(stack)
                  : super.getArmorLayerTintColor(stack, entity, layer, layerIdx, fallbackColor);
            }
         },
         new Holder[]{ModItems.WITCH_HELMET, ModItems.WITCH_CHESTPLATE, ModItems.WITCH_BOOTS, ModItems.MUSHROOM_WITCH_HAT}
      );
      event.registerFluidType(
         new IClientFluidTypeExtensions() {
            private static final ResourceLocation STILL = ModFluidTypes.BLOOD_STILL_RL;
            private static final ResourceLocation FLOW = ModFluidTypes.BLOOD_FLOWING_RL;
            private static final ResourceLocation OVERLAY = ModFluidTypes.BLOOD_OVERLAY_RL;
            private static final ResourceLocation VIEW_OVERLAY = ResourceLocation.parse("minecraft:textures/block/nether_wart_block.png");

            public ResourceLocation getStillTexture() {
               return STILL;
            }

            public ResourceLocation getFlowingTexture() {
               return FLOW;
            }

            public ResourceLocation getOverlayTexture() {
               return OVERLAY;
            }

            public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
               return VIEW_OVERLAY;
            }

            public int getTintColor() {
               return -100663297;
            }

            @NotNull
            public Vector3f modifyFogColor(
               Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor
            ) {
               int color = this.getTintColor();
               return new Vector3f(0.1875F, 0.015686275F, 0.015686275F);
            }

            public void modifyFogRender(
               Camera camera, FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape
            ) {
               nearDistance = -8.0F;
               farDistance = 4.0F;
               if (farDistance > renderDistance) {
                  farDistance = renderDistance;
                  shape = FogShape.CYLINDER;
               }

               RenderSystem.setShaderFogStart(nearDistance);
               RenderSystem.setShaderFogEnd(farDistance);
               RenderSystem.setShaderFogShape(shape);
            }
         },
         new Holder[]{ModFluidTypes.BLOOD_FLUID_TYPE}
      );
      event.registerFluidType(
         new IClientFluidTypeExtensions() {
            private static final ResourceLocation STILL = ModFluidTypes.TALLOW_STILL_RL;
            private static final ResourceLocation FLOW = ModFluidTypes.TALLOW_FLOWING_RL;
            private static final ResourceLocation OVERLAY = ModFluidTypes.TALLOW_OVERLAY_RL;
            private static final ResourceLocation VIEW_OVERLAY = ResourceLocation.parse("minecraft:textures/block/sand.png");

            public ResourceLocation getStillTexture() {
               return STILL;
            }

            public ResourceLocation getFlowingTexture() {
               return FLOW;
            }

            public ResourceLocation getOverlayTexture() {
               return OVERLAY;
            }

            public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
               return VIEW_OVERLAY;
            }

            public int getTintColor() {
               return -100663297;
            }

            @NotNull
            public Vector3f modifyFogColor(
               Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor
            ) {
               int color = this.getTintColor();
               return new Vector3f(0.59765625F, 0.6F, 0.44705883F);
            }

            public void modifyFogRender(
               Camera camera, FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape
            ) {
               nearDistance = -8.0F;
               farDistance = 3.0F;
               if (farDistance > renderDistance) {
                  farDistance = renderDistance;
                  shape = FogShape.CYLINDER;
               }

               RenderSystem.setShaderFogStart(nearDistance);
               RenderSystem.setShaderFogEnd(farDistance);
               RenderSystem.setShaderFogShape(shape);
            }
         },
         new Holder[]{ModFluidTypes.TALLOW_FLUID_TYPE}
      );
      event.registerFluidType(
         new IClientFluidTypeExtensions() {
            private static final ResourceLocation STILL = ModFluidTypes.QUICKSILVER_STILL_RL;
            private static final ResourceLocation FLOW = ModFluidTypes.QUICKSILVER_FLOWING_RL;
            private static final ResourceLocation OVERLAY = ModFluidTypes.QUICKSILVER_OVERLAY_RL;
            private static final ResourceLocation VIEW_OVERLAY = ResourceLocation.parse("minecraft:textures/block/red_sand.png");

            public ResourceLocation getStillTexture() {
               return STILL;
            }

            public ResourceLocation getFlowingTexture() {
               return FLOW;
            }

            public ResourceLocation getOverlayTexture() {
               return OVERLAY;
            }

            public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
               return VIEW_OVERLAY;
            }

            public int getTintColor() {
               return -100663297;
            }

            @NotNull
            public Vector3f modifyFogColor(
               Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor
            ) {
               int color = this.getTintColor();
               return new Vector3f(0.3125F, 0.3137255F, 0.3137255F);
            }

            public void modifyFogRender(
               Camera camera, FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape
            ) {
               nearDistance = -8.0F;
               farDistance = 3.0F;
               if (farDistance > renderDistance) {
                  farDistance = renderDistance;
                  shape = FogShape.CYLINDER;
               }

               RenderSystem.setShaderFogStart(nearDistance);
               RenderSystem.setShaderFogEnd(farDistance);
               RenderSystem.setShaderFogShape(shape);
            }
         },
         new Holder[]{ModFluidTypes.QUICKSILVER_FLUID_TYPE}
      );
      event.registerFluidType(new IClientFluidTypeExtensions() {
         private static final ResourceLocation POTION_STILL = ResourceLocation.fromNamespaceAndPath("hexerei", "block/potion_still");
         private static final ResourceLocation POTION_FLOW = ResourceLocation.fromNamespaceAndPath("hexerei", "block/potion_flow");

         public int getTintColor(FluidStack stack) {
            int col = PotionFluidType.getTintColor(stack);
            int a = (col >> 24 & 0xFF) / 3 * 2;
            int r = col >> 16 & 0xFF;
            int g = col >> 8 & 0xFF;
            int b = col & 0xFF;
            return HexereiUtil.getColorValueAlpha(r / 255.0F, g / 255.0F, b / 255.0F, a / 255.0F);
         }

         public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
            return PotionFluidType.getTintColor(state, getter, pos);
         }

         public ResourceLocation getStillTexture() {
            return POTION_STILL;
         }

         public ResourceLocation getFlowingTexture() {
            return POTION_FLOW;
         }
      }, new Holder[]{ModFluidTypes.POTION_FLUID_TYPE});
   }
}
