package net.joefoxe.hexerei.item.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import net.joefoxe.hexerei.client.renderer.IFirstPersonItemAnimation;
import net.joefoxe.hexerei.client.renderer.IThirdPersonItemAnimation;
import net.joefoxe.hexerei.client.renderer.IThirdPersonItemRenderer;
import net.joefoxe.hexerei.client.renderer.TwoHandedItemAnimation;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.container.CrowFluteContainer;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.joefoxe.hexerei.sounds.ModSounds;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.CrowFluteClearCrowListToServer;
import net.joefoxe.hexerei.util.message.CrowFluteClearCrowPerchToServer;
import net.joefoxe.hexerei.util.message.CrowFluteCommandModeSyncToServer;
import net.joefoxe.hexerei.util.message.CrowFluteCommandSyncToServer;
import net.joefoxe.hexerei.util.message.CrowFluteHelpCommandSyncToServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import org.jetbrains.annotations.Nullable;

public class CrowFluteItem extends Item implements IThirdPersonItemAnimation, IThirdPersonItemRenderer, IFirstPersonItemAnimation {
   protected final Predicate<CrowEntity> targetEntitySelector = input -> true;
   private static final Predicate<Entity> field_219989_a = EntitySelector.NO_SPECTATORS.and(Entity::canBeCollidedWith);
   public int commandSelected = 0;
   public int helpCommandSelected = 0;

   public CrowFluteItem(Properties properties) {
      super(properties);
   }

   protected AABB getTargetableArea(double targetDistance, Entity entity) {
      Vec3 renderCenter = new Vec3(entity.getX(), entity.getY(), entity.getZ());
      AABB aabb = new AABB(-targetDistance, -targetDistance, -targetDistance, targetDistance, targetDistance, targetDistance);
      return aabb.move(renderCenter);
   }

   public void inventoryTick(ItemStack itemstack, Level level, Entity entity, int slotId, boolean isSelected) {
      FluteData data = (FluteData)itemstack.get(ModDataComponents.FLUTE);
      if (data == null) {
         data = FluteData.empty();
         itemstack.set(ModDataComponents.FLUTE, data);
      }

      if (!level.isClientSide) {
         List<FluteData.CrowIds> list = data.crowList();
         List<FluteData.CrowIds> newList = new ArrayList<>();
         boolean flag = false;

         for (FluteData.CrowIds crowIds : list) {
            Entity crow = ((ServerLevel)level).getEntity(crowIds.uuid());
            if (crow instanceof CrowEntity && crow.getId() != crowIds.id()) {
               newList.add(new FluteData.CrowIds(crowIds.uuid(), crow.getId()));
               flag = true;
            }
         }

         if (flag) {
            FluteData newData = new FluteData(
               data.commandSelected(), data.helpCommandSelected(), data.commandMode(), newList, data.dyeColor1(), data.dyeColor2()
            );
            itemstack.set(ModDataComponents.FLUTE, newData);
         }
      }

      super.inventoryTick(itemstack, level, entity, slotId, isSelected);
   }

   public InteractionResult useOn(UseOnContext ctx) {
      Player player = ctx.getPlayer();
      ItemStack itemstack = ctx.getItemInHand();
      FluteData fluteData = (FluteData)itemstack.get(ModDataComponents.FLUTE);
      if (player != null && !player.isShiftKeyDown() && fluteData != null && fluteData.commandMode() == 2) {
         List<CrowEntity> crows = new ArrayList<>();
         List<FluteData.CrowIds> ids = fluteData.crowList();
         if (ids.isEmpty()) {
            return InteractionResult.FAIL;
         } else {
            if (!player.level().isClientSide) {
               List<FluteData.CrowIds> newIds = ids.stream()
                  .filter(crowIdsx -> ((ServerLevel)player.level()).getEntity(crowIdsx.uuid()) instanceof CrowEntity)
                  .toList();
               fluteData = new FluteData(
                  fluteData.commandSelected(), fluteData.helpCommandSelected(), fluteData.commandMode(), newIds, fluteData.dyeColor1(), fluteData.dyeColor2()
               );

               for (FluteData.CrowIds crowIds : newIds) {
                  if (((ServerLevel)player.level()).getEntity(crowIds.uuid()) instanceof CrowEntity crow) {
                     crows.add(crow);
                     crow.setPerchPos(ctx.getClickedPos());
                  }
               }

               if (!crows.isEmpty()) {
                  player.level()
                     .playSound(
                        null,
                        player.getX() + player.getLookAngle().x(),
                        player.getY() + player.getEyeHeight(),
                        player.getZ() + player.getLookAngle().z(),
                        (SoundEvent)ModSounds.CROW_FLUTE.get(),
                        SoundSource.PLAYERS,
                        1.0F,
                        0.8F + 0.4F * new Random().nextFloat()
                     );
                  player.getCooldowns().addCooldown(this, 20);
               }

               itemstack.set(ModDataComponents.FLUTE, fluteData);
            }

            return InteractionResult.SUCCESS;
         }
      } else {
         return super.useOn(ctx);
      }
   }

   public static ItemStack withColors(int color1, int color2) {
      ItemStack stack = new ItemStack((ItemLike)ModItems.CROW_FLUTE.get());
      stack.set(ModDataComponents.FLUTE, new FluteData(0, 0, 0, new ArrayList<>(), color1, color2));
      return stack;
   }

   public static DyeColor getColor1(ItemStack stack) {
      DyeColor col = HexereiUtil.getDyeColorNamed(stack.getHoverName().getString(), 0);
      FluteData fluteData = (FluteData)stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY);
      return col == null ? DyeColor.byId(fluteData.dyeColor1()) : col;
   }

   public static DyeColor getColor2(ItemStack stack) {
      DyeColor col = HexereiUtil.getDyeColorNamed(stack.getHoverName().getString(), 0);
      FluteData fluteData = (FluteData)stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY);
      return col == null ? DyeColor.byId(fluteData.dyeColor2()) : col;
   }

   public boolean isFoil(ItemStack stack) {
      int commandMode = ((FluteData)stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY)).commandMode();
      return commandMode == 2 || commandMode == 1;
   }

   public UseAnim getUseAnimation(ItemStack pStack) {
      return UseAnim.NONE;
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return 72000;
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand handIn) {
      ItemStack itemstack = player.getItemInHand(handIn);
      player.startUsingItem(handIn);
      if (player instanceof ServerPlayer serverPlayer) {
         FluteData fluteData = (FluteData)itemstack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY);
         if (player.isShiftKeyDown()) {
            List<FluteData.CrowIds> newIds = fluteData.crowList()
               .stream()
               .filter(crowIds -> ((ServerLevel)player.level()).getEntity(crowIds.uuid()) instanceof CrowEntity)
               .toList();
            fluteData = new FluteData(
               fluteData.commandSelected(), fluteData.helpCommandSelected(), fluteData.commandMode(), newIds, fluteData.dyeColor1(), fluteData.dyeColor2()
            );
            itemstack.set(ModDataComponents.FLUTE, fluteData);
            MenuProvider containerProvider = this.createContainerProvider(itemstack, handIn);
            serverPlayer.openMenu(containerProvider, b -> b.writeInt(handIn == InteractionHand.MAIN_HAND ? 0 : 1));
         } else {
            if (fluteData.commandMode() == 0) {
               List<FluteData.CrowIds> newIds = fluteData.crowList()
                  .stream()
                  .filter(crowIds -> ((ServerLevel)player.level()).getEntity(crowIds.uuid()) instanceof CrowEntity)
                  .toList();
               fluteData = new FluteData(
                  fluteData.commandSelected(), fluteData.helpCommandSelected(), fluteData.commandMode(), newIds, fluteData.dyeColor1(), fluteData.dyeColor2()
               );
               List<CrowEntity> crows = new ArrayList<>();

               for (FluteData.CrowIds crowIds : fluteData.crowList()) {
                  crows.add((CrowEntity)((ServerLevel)player.level()).getEntity(crowIds.uuid()));
               }

               if (crows.isEmpty()) {
                  crows = level.getEntitiesOfClass(CrowEntity.class, this.getTargetableArea(64.0, player), this.targetEntitySelector);
                  crows.removeIf(crow -> !crow.isOwnedBy(player));
               }

               if (!crows.isEmpty()) {
                  int selected = fluteData.commandSelected();
                  if (selected == 0) {
                     player.displayClientMessage(
                        Component.translatable(
                           "entity.hexerei.crow_flute_set_message",
                           new Object[]{crows.size(), crows.size() > 1 ? "s" : "", Component.translatable("entity.hexerei.crow_command_gui_0")}
                        ),
                        true
                     );

                     for (CrowEntity crow : crows) {
                        if (crow.isOwnedBy(player)) {
                           crow.setCommandFollow();
                        }
                     }
                  } else if (selected == 1) {
                     player.displayClientMessage(
                        Component.translatable(
                           "entity.hexerei.crow_flute_set_message",
                           new Object[]{crows.size(), crows.size() > 1 ? "s" : "", Component.translatable("entity.hexerei.crow_command_gui_1")}
                        ),
                        true
                     );

                     for (CrowEntity crowx : crows) {
                        if (crowx.isOwnedBy(player)) {
                           crowx.setCommandSit();
                        }
                     }
                  } else if (selected == 2) {
                     player.displayClientMessage(
                        Component.translatable(
                           "entity.hexerei.crow_flute_set_message",
                           new Object[]{crows.size(), crows.size() > 1 ? "s" : "", Component.translatable("entity.hexerei.crow_command_gui_2")}
                        ),
                        true
                     );

                     for (CrowEntity crowxx : crows) {
                        if (crowxx.isOwnedBy(player)) {
                           crowxx.setCommandWander();
                        }
                     }
                  } else if (selected == 3) {
                     player.displayClientMessage(
                        Component.translatable(
                              "entity.hexerei.crow_flute_set_message",
                              new Object[]{crows.size(), crows.size() > 1 ? "s" : "", Component.translatable("entity.hexerei.crow_command_gui_3")}
                           )
                           .append(" (")
                           .append(
                              Component.translatable(
                                 "entity.hexerei.crow_help_command_gui_"
                                    + ((FluteData)itemstack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY)).helpCommandSelected()
                              )
                           )
                           .append(")"),
                        true
                     );

                     for (CrowEntity crowxxx : crows) {
                        if (crowxxx.isOwnedBy(player)) {
                           crowxxx.setHelpCommand(((FluteData)itemstack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY)).helpCommandSelected());
                           crowxxx.setCommandHelp();
                        }
                     }
                  }
               }

               level.playSound(
                  null,
                  player.getX() + player.getLookAngle().x(),
                  player.getY() + player.getEyeHeight(),
                  player.getZ() + player.getLookAngle().z(),
                  (SoundEvent)ModSounds.CROW_FLUTE.get(),
                  SoundSource.PLAYERS,
                  1.0F,
                  0.8F + 0.4F * new Random().nextFloat()
               );
               player.getCooldowns().addCooldown(this, 20);
               return InteractionResultHolder.success(itemstack);
            }

            if (((FluteData)itemstack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY)).commandMode() == 1) {
               HitResult raytraceresult = getPlayerPOVHitResult(level, player, Fluid.NONE);
               if (raytraceresult.getType() == Type.ENTITY) {
                  Vec3 vector3d = player.getLookAngle();
                  List<Entity> list = level.getEntities(player, player.getBoundingBox().expandTowards(vector3d.scale(5.0)).inflate(1.0), field_219989_a);
                  boolean flag = false;

                  for (Entity entity : list) {
                     if (entity instanceof CrowEntity && ((CrowEntity)entity).isOwnedBy(player)) {
                        flag = true;
                        break;
                     }
                  }

                  if (!flag) {
                     player.getCooldowns().addCooldown(this, 5);
                  }
               } else {
                  player.getCooldowns().addCooldown(this, 5);
               }
            } else if (((FluteData)itemstack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY)).commandMode() == 2) {
               HitResult raytraceresult = getPlayerPOVHitResult(level, player, Fluid.NONE);
               if (raytraceresult.getType() == Type.BLOCK) {
                  if (!((FluteData)itemstack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY)).crowList().isEmpty()) {
                     return InteractionResultHolder.success(itemstack);
                  }

                  player.displayClientMessage(Component.translatable("entity.hexerei.crow_flute_perch_message_fail_no_crows"), true);
                  player.getCooldowns().addCooldown(this, 5);
               } else {
                  player.getCooldowns().addCooldown(this, 5);
               }
            }
         }

         return InteractionResultHolder.fail(itemstack);
      } else {
         if (!player.isShiftKeyDown()) {
            if (((FluteData)itemstack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY)).commandMode() == 1) {
               HitResult raytraceresult = getPlayerPOVHitResult(level, player, Fluid.ANY);
               if (raytraceresult.getType() != Type.ENTITY) {
                  player.displayClientMessage(Component.translatable("entity.hexerei.crow_flute_select_message_fail"), true);
                  player.playSound((SoundEvent)ModSounds.CROW_FLUTE_DESELECT.get(), 1.0F, 0.1F);
                  return InteractionResultHolder.fail(itemstack);
               }

               if (raytraceresult.getType() == Type.ENTITY) {
                  Vec3 vector3d = player.getLookAngle();
                  List<Entity> list = level.getEntities(player, player.getBoundingBox().expandTowards(vector3d.scale(5.0)).inflate(1.0), field_219989_a);
                  boolean flag = false;

                  for (Entity entityx : list) {
                     if (entityx instanceof CrowEntity && ((CrowEntity)entityx).isOwnedBy(player)) {
                        flag = true;
                        break;
                     }
                  }

                  if (!flag) {
                     player.displayClientMessage(Component.translatable("entity.hexerei.crow_flute_select_message_fail"), true);
                     player.playSound((SoundEvent)ModSounds.CROW_FLUTE_DESELECT.get(), 1.0F, 0.1F);
                     return InteractionResultHolder.fail(itemstack);
                  }

                  return InteractionResultHolder.success(itemstack);
               }
            }

            if (((FluteData)itemstack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY)).commandMode() == 2) {
               HitResult raytraceresultx = getPlayerPOVHitResult(level, player, Fluid.NONE);
               if (raytraceresultx.getType() != Type.BLOCK) {
                  player.displayClientMessage(Component.translatable("entity.hexerei.crow_flute_perch_message_fail_no_block"), true);
                  player.playSound((SoundEvent)ModSounds.CROW_FLUTE_DESELECT.get(), 1.0F, 0.1F);
                  return InteractionResultHolder.fail(itemstack);
               }

               if (((FluteData)itemstack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY)).crowList().isEmpty()) {
                  player.displayClientMessage(Component.translatable("entity.hexerei.crow_flute_perch_message_fail_no_crows"), true);
                  player.playSound((SoundEvent)ModSounds.CROW_FLUTE_DESELECT.get(), 1.0F, 0.1F);
                  return InteractionResultHolder.fail(itemstack);
               }

               return InteractionResultHolder.fail(itemstack);
            }
         }

         return InteractionResultHolder.success(itemstack);
      }
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      if (Screen.hasShiftDown()) {
         tooltipComponents.add(
            Component.translatable(
                  "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         tooltipComponents.add(Component.translatable("tooltip.hexerei.crow_flute_shift_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         tooltipComponents.add(Component.translatable("tooltip.hexerei.crow_flute_shift_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         tooltipComponents.add(Component.translatable("tooltip.hexerei.crow_flute_shift_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         tooltipComponents.add(Component.translatable("tooltip.hexerei.crow_flute_shift_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         tooltipComponents.add(Component.translatable("tooltip.hexerei.crow_flute_shift_5").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
      } else {
         tooltipComponents.add(
            Component.translatable(
                  "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         FluteData fluteData = (FluteData)stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY);
         String command = "";
         if (fluteData.commandMode() == 0) {
            if (fluteData.commandSelected() == 0) {
               command = "entity.hexerei.crow_command_gui_0";
            }

            if (fluteData.commandSelected() == 1) {
               command = "entity.hexerei.crow_command_gui_1";
            }

            if (fluteData.commandSelected() == 2) {
               command = "entity.hexerei.crow_command_gui_2";
            }

            if (fluteData.commandSelected() == 3) {
               if (fluteData.helpCommandSelected() == 0) {
                  command = "entity.hexerei.crow_help_command_gui_0";
               }

               if (fluteData.helpCommandSelected() == 1) {
                  command = "entity.hexerei.crow_help_command_gui_1";
               }

               if (fluteData.helpCommandSelected() == 2) {
                  command = "entity.hexerei.crow_help_command_gui_2";
               }
            }
         } else if (fluteData.commandMode() == 1) {
            command = "entity.hexerei.crow_flute_perch";
         } else if (fluteData.commandMode() == 2) {
            command = "entity.hexerei.crow_flute_select";
         }

         tooltipComponents.add(
            Component.translatable("-%s-", new Object[]{Component.translatable(command).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))})
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
      }

      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
   }

   public static void setCommand(int command, ItemStack stack, Player player, InteractionHand hand) {
      if (player.level().isClientSide) {
         HexereiPacketHandler.sendToServer(new CrowFluteCommandSyncToServer(stack, command, player.getUUID(), hand == InteractionHand.MAIN_HAND ? 0 : 1));
      }
   }

   public static void setHelpCommand(int helpCommand, ItemStack stack, Player player, InteractionHand hand) {
      if (player.level().isClientSide) {
         HexereiPacketHandler.sendToServer(
            new CrowFluteHelpCommandSyncToServer(stack, helpCommand, player.getUUID(), hand == InteractionHand.MAIN_HAND ? 0 : 1)
         );
      }
   }

   public static void setCommandMode(int mode, ItemStack stack, Player player, InteractionHand hand) {
      if (player.level().isClientSide) {
         HexereiPacketHandler.sendToServer(new CrowFluteCommandModeSyncToServer(stack, mode, player.getUUID(), hand == InteractionHand.MAIN_HAND ? 0 : 1));
      }
   }

   public static void clearCrowList(ItemStack stack, Player player, InteractionHand hand) {
      if (player.level().isClientSide) {
         HexereiPacketHandler.sendToServer(new CrowFluteClearCrowListToServer(stack, player.getUUID(), hand == InteractionHand.MAIN_HAND ? 0 : 1));
      }
   }

   public static void clearCrowPerch(ItemStack stack, Player player, InteractionHand hand) {
      if (player.level().isClientSide) {
         HexereiPacketHandler.sendToServer(new CrowFluteClearCrowPerchToServer(stack, player.getUUID(), hand == InteractionHand.MAIN_HAND ? 0 : 1));
      }
   }

   private MenuProvider createContainerProvider(final ItemStack itemStack, final InteractionHand hand) {
      return new MenuProvider() {
         @Nullable
         public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
            return new CrowFluteContainer(windowId, inv, player, hand);
         }

         public Component getDisplayName() {
            return Component.translatable("").append(itemStack.getHoverName());
         }
      };
   }

   public static float wrapRad(float pValue) {
      float p = 6.2831855F;
      float d0 = pValue % p;
      if (d0 >= 3.141592653589793) {
         d0 -= p;
      }

      if (d0 < -3.141592653589793) {
         d0 += p;
      }

      return d0;
   }

   @Override
   public <T extends LivingEntity> boolean poseRightArm(
      ItemStack stack, HumanoidModel<T> model, T entity, HumanoidArm mainHand, TwoHandedItemAnimation twoHanded
   ) {
      if (entity.getUseItemRemainingTicks() > 0 && entity.getUseItem().getItem() == this) {
         this.animateHands(model, entity, false);
         twoHanded.bool = true;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public <T extends LivingEntity> boolean poseRightArmMixin(
      ItemStack stack, AgeableListModel<T> model, T entity, HumanoidArm mainHand, TwoHandedItemAnimation twoHanded
   ) {
      return IThirdPersonItemAnimation.super.poseRightArmMixin(stack, model, entity, mainHand, twoHanded);
   }

   @Override
   public <T extends LivingEntity> boolean poseLeftArm(
      ItemStack stack, HumanoidModel<T> model, T entity, HumanoidArm mainHand, TwoHandedItemAnimation twoHanded
   ) {
      if (entity.getUseItemRemainingTicks() > 0 && entity.getUseItem().getItem() == this) {
         this.animateHands(model, entity, true);
         twoHanded.bool = true;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public <T extends LivingEntity> boolean poseleftArmMixin(
      ItemStack stack, AgeableListModel<T> model, T entity, HumanoidArm mainHand, TwoHandedItemAnimation twoHanded
   ) {
      return IThirdPersonItemAnimation.super.poseleftArmMixin(stack, model, entity, mainHand, twoHanded);
   }

   @Override
   public boolean isTwoHanded() {
      return IThirdPersonItemAnimation.super.isTwoHanded();
   }

   private <T extends LivingEntity> void animateHands(HumanoidModel<T> model, T entity, boolean leftHand) {
      ModelPart mainHand = leftHand ? model.leftArm : model.rightArm;
      ModelPart offHand = leftHand ? model.rightArm : model.leftArm;
      Vec3 bx = new Vec3(1.0, 0.0, 0.0);
      Vec3 by = new Vec3(0.0, 1.0, 0.0);
      Vec3 bz = new Vec3(0.0, 0.0, 1.0);
      float headXRot = wrapRad(model.head.xRot);
      float headYRot = wrapRad(model.head.yRot);
      float downFacingRot = Mth.clamp(headXRot, 0.0F, 0.8F);
      float xRot = getMaxHeadXRot(headXRot) - (entity.isCrouching() ? 1.0F : 0.0F) - 0.3F + downFacingRot * 0.5F;
      bx = bx.xRot(xRot);
      by = by.xRot(xRot);
      bz = bz.xRot(xRot);
      Vec3 armVec = new Vec3(0.0, 0.0, 1.0);
      float mirror = leftHand ? 1.0F : -1.0F;
      armVec = armVec.yRot(-0.99F * mirror);
      Vec3 newV = bx.scale(armVec.x).add(by.scale(armVec.y)).add(bz.scale(armVec.z));
      float yaw = (float)Math.atan2(-newV.x, newV.z);
      float len = (float)newV.length();
      float pitch = (float)Math.asin(newV.y / len);
      float yRot = yaw + headYRot * 0.8F - 1.6F * mirror - 0.5F * downFacingRot * mirror;
      mainHand.yRot = yRot;
      mainHand.xRot = (float)(pitch - 1.5707963267948966);
      offHand.yRot = yRot;
      offHand.xRot = wrapRad(mainHand.xRot - 0.06F);
      float offset = leftHand ? -Mth.clamp(headYRot, -1.0F, 0.0F) : Mth.clamp(headYRot, 0.0F, 1.0F);
      mainHand.z = -offset * 0.95F;
      AnimationUtils.bobModelPart(model.leftArm, entity.tickCount, 1.0F);
      AnimationUtils.bobModelPart(model.rightArm, entity.tickCount, -1.0F);
   }

   public static float getMaxHeadXRot(float xRot) {
      return Mth.clamp(xRot, -1.2566371F, 1.5707964F);
   }

   @Override
   public <T extends Player, M extends EntityModel<T> & ArmedModel & HeadedModel> void renderThirdPersonItem(
      M parentModel, LivingEntity entity, ItemStack stack, HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource bufferSource, int light
   ) {
      if (!stack.isEmpty()) {
         poseStack.pushPose();
         boolean leftHand = humanoidArm == HumanoidArm.LEFT;
         ItemDisplayContext transform;
         if (entity.getUseItem() == stack) {
            ModelPart head = parentModel.getHead();
            float oldRot = head.xRot;
            head.xRot = getMaxHeadXRot(wrapRad(oldRot));
            poseStack.translate(head.x / 16.0F, head.y / 16.0F, head.z / 16.0F);
            if (head.zRot != 0.0F) {
               poseStack.mulPose(Axis.ZP.rotation(head.zRot / 1.75F));
            }

            if (head.yRot != 0.0F) {
               poseStack.mulPose(Axis.YP.rotation(head.yRot));
            }

            if (head.xRot != 0.0F) {
               poseStack.mulPose(Axis.XP.rotation(head.xRot / 1.75F));
            }

            head.xRot = oldRot;
            CustomHeadLayer.translateToHead(poseStack, false);
            poseStack.translate((leftHand ? -1 : 1) * 4.0F / 16.0F, -0.375F, -0.75F);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F + head.yRot * 6.2831855F * 10.0F + (leftHand ? -1 : 1) * 10));
            poseStack.mulPose(Axis.ZP.rotationDegrees((leftHand ? 1 : -1) * 23));
            poseStack.mulPose(Axis.XP.rotationDegrees((leftHand ? 1 : 0) * -90));
            poseStack.translate(0.0F, 0.4375F, 0.5F);
            transform = ItemDisplayContext.HEAD;
         } else {
            parentModel.translateToHand(humanoidArm, poseStack);
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            poseStack.translate((leftHand ? -1 : 1) / 16.0F, 0.125, -0.625);
            transform = leftHand ? ItemDisplayContext.THIRD_PERSON_LEFT_HAND : ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
         }

         Minecraft.getInstance().gameRenderer.itemInHandRenderer.renderItem(entity, stack, transform, leftHand, poseStack, bufferSource, light);
         poseStack.popPose();
      }
   }

   @Override
   public void animateItemFirstPerson(
      LivingEntity entity, ItemStack stack, InteractionHand hand, PoseStack matrixStack, float partialTicks, float pitch, float attackAnim, float handHeight
   ) {
      if (entity.isUsingItem() && entity.getUseItemRemainingTicks() > 0 && entity.getUsedItemHand() == hand) {
         int mirror = entity.getMainArm() == HumanoidArm.RIGHT ^ hand == InteractionHand.MAIN_HAND ? -1 : 1;
         matrixStack.translate(-0.4 * mirror, 0.2, 0.0);
         float timeLeft = stack.getUseDuration(entity) - (entity.getUseItemRemainingTicks() - partialTicks + 1.0F);
         float sin = Mth.sin((timeLeft - 0.1F) * 1.3F);
         matrixStack.translate(0.0F, sin * 0.0038F, 0.0F);
         matrixStack.mulPose(Axis.ZN.rotationDegrees(90.0F));
         matrixStack.scale(1.0F * mirror, -1.0F * mirror, -1.0F);
      }
   }

   @EventBusSubscriber(
      value = {Dist.CLIENT},
      modid = "hexerei",
      bus = Bus.MOD
   )
   private static class ColorRegisterHandler {
      @SubscribeEvent(
         priority = EventPriority.HIGHEST
      )
      public static void registerFluteColors(net.neoforged.neoforge.client.event.RegisterColorHandlersEvent.Item event) {
         CrowFluteItem.ItemHandlerConsumer items = event.getItemColors()::register;
         items.register(
            (s, t) -> t == 1 ? CrowFluteItem.getColor1(s).getTextureDiffuseColor() : (t == 2 ? CrowFluteItem.getColor2(s).getTextureDiffuseColor() : -1),
            (ItemLike)ModItems.CROW_FLUTE.get()
         );
      }
   }

   public interface ItemHandlerConsumer {
      void register(ItemColor var1, ItemLike... var2);
   }
}
