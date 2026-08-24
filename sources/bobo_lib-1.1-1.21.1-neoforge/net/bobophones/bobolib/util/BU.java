package net.bobophones.bobolib.util;

import java.util.Comparator;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation;

public class BU {
   public static final int day_in_ticks = 24000;
   public static final int bow_use_time = 20;
   public static final int trident_use_time = 10;
   public static final double rad = 0.01745329251994;
   public static final double deg = 57.29577951308232;

   public static double rad(double value) {
      return value * 0.01745329251994;
   }

   public static float rad(float value) {
      return value * 0.017453292F;
   }

   public static double deg(double value) {
      return value * 57.29577951308232;
   }

   public static Item item(ResourceLocation loc) {
      return (Item)BuiltInRegistries.ITEM.get(loc);
   }

   public static Block block(ResourceLocation loc) {
      return (Block)BuiltInRegistries.BLOCK.get(loc);
   }

   public static ResourceLocation item_id(Item value) {
      return BuiltInRegistries.ITEM.getKey(value);
   }

   public static ResourceLocation block_id(Block value) {
      return BuiltInRegistries.BLOCK.getKey(value);
   }

   public static String reg_loc(Item value) {
      return item_id(value).getNamespace() + "." + item_id(value).getPath();
   }

   public static String reg_loc(Block value) {
      return block_id(value).getNamespace() + "." + block_id(value).getPath();
   }

   public static void Give(Player player, ItemStack stack) {
      if (!player.getInventory().add(stack)) {
         player.drop(stack, false);
      }
   }

   public static void ShrinkCreative(Player player, ItemStack stack) {
      ShrinkCreative(player, stack, 1);
   }

   public static void ShrinkCreative(Player player, ItemStack stack, int value) {
      stack.consume(value, player);
   }

   public static void SpawnParticleOnFace(Level level, BlockPos pos, Direction dir, ParticleOptions particle, Vec3 speed) {
      Vec3 vec = Vec3.atCenterOf(pos);
      int nx = dir.getStepX();
      int ny = dir.getStepY();
      int nz = dir.getStepZ();
      double x = vec.x + (nx == 0 ? Mth.nextDouble(level.random, -0.5, 0.5) : nx);
      double y = vec.y + (ny == 0 ? Mth.nextDouble(level.random, -0.5, 0.5) : ny);
      double z = vec.z + (nz == 0 ? Mth.nextDouble(level.random, -0.5, 0.5) : nz);
      double x_speed = nx == 0 ? speed.x() : 0.0;
      double y_speed = ny == 0 ? speed.y() : 0.0;
      double z_speed = nz == 0 ? speed.z() : 0.0;
      level.addParticle(particle, x, y - 0.5, z, x_speed, y_speed, z_speed);
   }

   public static void SpawnItemParticle(Level level, ItemStack stack, Vec3 pos) {
      RandomSource random = level.random;
      level.addParticle(
         new ItemParticleOption(ParticleTypes.ITEM, stack),
         pos.x,
         pos.y,
         pos.z,
         (random.nextDouble() - 0.5) * 0.08,
         (random.nextDouble() - 0.5) * 0.08,
         (random.nextDouble() - 0.5) * 0.08
      );
   }

   public static void ActionBarText(ServerPlayer player, Component comp) {
      player.connection.send(new ClientboundSetActionBarTextPacket(comp));
   }

   public static void ActionBarText(Component comp) {
      Minecraft.getInstance().gui.setOverlayMessage(comp, false);
   }

   public static void BlockPlaceSound(Level level, BlockPos pos, BlockState state) {
      level.playSound(null, pos, state.getSoundType(level, pos, null).getBreakSound(), SoundSource.BLOCKS);
   }

   public static void BlockBreakSound(Level level, BlockPos pos, BlockState state) {
      level.playSound(null, pos, state.getSoundType(level, pos, null).getBreakSound(), SoundSource.BLOCKS);
   }

   public static float fixed_fps() {
      return Minecraft.getInstance().getFps() / 60.0F;
   }

   public static ItemStack random_item(Ingredient ingredient) {
      ItemStack[] items = ingredient.getItems();
      return items.length == 0 ? null : items[new Random().nextInt(items.length)];
   }

   public static String trim_text(Font font, String text, int max_width) {
      if (font.width(text) <= max_width) {
         return text;
      } else {
         String end = "...";
         int end_width = font.width(end);

         for (int i = text.length(); i >= 0; i--) {
            String sub = text.substring(0, i);
            if (font.width(sub) + end_width <= max_width) {
               return sub + end;
            }
         }

         return end;
      }
   }

   public static <T extends Entity> boolean entity_render_distance(double distance, T entity, double cam_x, double cam_y, double cam_z) {
      double x = entity.getX() - cam_x;
      double y = entity.getY() - cam_y;
      double z = entity.getZ() - cam_z;
      return x * x + y * y + z * z < distance * distance;
   }

   public static BlockHitResult get_hit_block(Entity entity, double dist) {
      Vec3 pos = entity.getEyePosition(1.0F);
      Vec3 look = entity.getLookAngle();
      Vec3 reach = pos.add(look.scale(dist));
      return entity.level().clip(new ClipContext(pos, reach, net.minecraft.world.level.ClipContext.Block.OUTLINE, Fluid.NONE, entity));
   }

   public static Entity get_hit_entity(Entity entity, double dist) {
      Vec3 pos = entity.getEyePosition(1.0F);
      Vec3 look = entity.getViewVector(1.0F);
      Vec3 reach = pos.add(look.scale(dist));
      BlockHitResult block_hit = entity.level().clip(new ClipContext(pos, reach, net.minecraft.world.level.ClipContext.Block.COLLIDER, Fluid.NONE, entity));
      double block_dist = block_hit.getType() == Type.BLOCK ? block_hit.getLocation().distanceTo(pos) : dist;
      AABB area = entity.getBoundingBox().expandTowards(look.scale(block_dist)).inflate(1.0);
      return entity.level().getEntitiesOfClass(Entity.class, area, e -> e != entity && e.isPickable() && e.isAlive()).stream().filter(e -> {
         Optional<Vec3> hit = e.getBoundingBox().clip(pos, reach);
         return hit.isPresent() && hit.get().distanceTo(pos) < block_dist;
      }).min(Comparator.comparingDouble(e -> e.distanceToSqr(entity))).orElse(null);
   }

   public static Vec3 get_hit_pos(Entity entity, double dist) {
      Entity entity_hit = get_hit_entity(entity, dist);
      if (entity_hit != null) {
         return entity_hit.position();
      } else {
         BlockHitResult block_hit = get_hit_block(entity, dist);
         return block_hit.getType() == Type.BLOCK ? block_hit.getLocation() : entity.getEyePosition().add(entity.getLookAngle().scale(dist));
      }
   }

   public static boolean can_see_entity(LivingEntity entity, LivingEntity target) {
      Level level = entity.level();
      Vec3 from = entity.getEyePosition();
      Vec3 to = target.getEyePosition();
      return level.clip(new ClipContext(from, to, net.minecraft.world.level.ClipContext.Block.COLLIDER, Fluid.NONE, entity)).getType() == Type.MISS;
   }

   public static BlockPos find_block_near_entity(Entity entity, Block target, int radius) {
      for (int x = -radius; x <= radius; x++) {
         for (int y = -radius; y <= radius; y++) {
            for (int z = -radius; z <= radius; z++) {
               BlockPos pos = entity.blockPosition().offset(x, y, z);
               if (entity.level().getBlockState(pos).getBlock() == target) {
                  return pos;
               }
            }
         }
      }

      return null;
   }

   public static Vec3 get_target_angle(Mob mob) {
      return mob.getTarget().position().subtract(mob.position()).normalize();
   }

   public static float yaw_to_target(Entity from, Entity to) {
      double x = to.getX() - from.getX();
      double y = to.getZ() - from.getZ();
      return (float)(deg(Math.atan2(y, x)) - 90.0);
   }

   public static Vec3 rotate_vec(Vec3 vec, float x_rot, float z_rot) {
      float pitch = rad(x_rot);
      float yaw = rad(z_rot);
      double d = -vec.x * Math.sin(yaw) + vec.z * Math.cos(yaw);
      double x = vec.x * Math.cos(yaw) + vec.z * Math.sin(yaw);
      double y = vec.y * Math.cos(pitch) - d * Math.sin(pitch);
      double z = vec.y * Math.sin(pitch) + d * Math.cos(pitch);
      return new Vec3(x, y, z);
   }

   public static float[] color_array(int value) {
      return new float[]{(value >> 16 & 0xFF) / 255.0F, (value >> 8 & 0xFF) / 255.0F, (value & 0xFF) / 255.0F};
   }

   public static int color_from_array(float[] value) {
      return (int)(value[0] * 255.0F) << 16 | (int)(value[1] * 255.0F) << 8 | (int)(value[2] * 255.0F);
   }

   public static class AddEntitySpawn {
      public static <T extends Mob> void Ambient(RegisterSpawnPlacementsEvent event, Supplier<EntityType<T>> entity) {
         event.register(entity.get(), SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, BU.AddEntitySpawn::ambient, Operation.OR);
      }

      public static <T extends Mob> void Animal(RegisterSpawnPlacementsEvent event, Supplier<EntityType<T>> entity) {
         event.register(entity.get(), SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, BU.AddEntitySpawn::animal, Operation.OR);
      }

      public static <T extends Mob> void Monster(RegisterSpawnPlacementsEvent event, Supplier<EntityType<T>> entity) {
         event.register(entity.get(), SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, BU.AddEntitySpawn::monster, Operation.OR);
      }

      private static <T extends Mob> boolean ambient(EntityType<T> entity, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
         return is_bright_enough(level, pos);
      }

      private static <T extends Mob> boolean animal(EntityType<T> entity, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
         return level.getBlockState(pos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) && is_bright_enough(level, pos);
      }

      private static <T extends Mob> boolean monster(EntityType<T> entity, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
         return level.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(level, pos, random);
      }

      private static boolean is_bright_enough(BlockAndTintGetter level, BlockPos pos) {
         return level.getRawBrightness(pos, 0) > 8;
      }
   }

   public static class GUI {
      public static void Click() {
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      }

      public static void String(GuiGraphics gr, String text, int x, int y) {
         String(gr, Component.literal(text), x, y);
      }

      public static void String(GuiGraphics gr, Component comp, int x, int y) {
         String(gr, comp, x, y, 16777215);
      }

      public static void String(GuiGraphics gr, String text, int x, int y, int color) {
         String(gr, Component.literal(text), x, y, color);
      }

      public static void String(GuiGraphics gr, Component comp, int x, int y, int color) {
         String(gr, comp, x, y, color, true);
      }

      public static void String(GuiGraphics gr, String text, int x, int y, int color, boolean shadow) {
         String(gr, Component.literal(text), x, y, color, shadow);
      }

      public static void String(GuiGraphics gr, Component comp, int x, int y, int color, boolean shadow) {
         gr.drawString(Minecraft.getInstance().font, comp, x, y, color, shadow);
      }

      public static void Translatable(GuiGraphics gr, String key, int x, int y) {
         String(gr, Component.translatable(key), x, y);
      }

      public static void Translatable(GuiGraphics gr, String key, int x, int y, int color) {
         String(gr, Component.translatable(key), x, y, color);
      }

      public static void Tooltip(GuiGraphics gr, String text, int x, int y) {
         Tooltip(gr, Component.literal(text), x, y);
      }

      public static void Tooltip(GuiGraphics gr, Component comp, int x, int y) {
         gr.renderTooltip(Minecraft.getInstance().font, comp, x, y);
      }

      public static void Tooltip(GuiGraphics gr, ItemStack stack, int x, int y) {
         gr.renderTooltip(Minecraft.getInstance().font, stack, x, y);
      }

      public static void Triangle(GuiGraphics gr, int[] p0, int[] p1, int[] p2, int color) {
         Triangle(gr, p0[0], p0[1], p1[0], p1[1], p2[0], p2[1], color);
      }

      public static void Triangle(GuiGraphics gr, int x0, int y0, int x1, int y1, int x2, int y2, int color) {
         if (y1 < y0) {
            int tx = x0;
            int ty = y0;
            x0 = x1;
            y0 = y1;
            x1 = tx;
            y1 = ty;
         }

         if (y2 < y0) {
            int tx = x0;
            int ty = y0;
            x0 = x2;
            y0 = y2;
            x2 = tx;
            y2 = ty;
         }

         if (y2 < y1) {
            int tx = x1;
            int ty = y1;
            x1 = x2;
            y1 = y2;
            x2 = tx;
            y2 = ty;
         }

         for (int y = y0; y <= y2; y++) {
            boolean second_half = y > y1 || y1 == y0;
            int segment_y = second_half ? y2 - y1 : y1 - y0;
            int segment_x = second_half ? x2 - x1 : x1 - x0;
            float f0 = (float)(y - y0) / (y2 - y0);
            float f1 = (float)(y - (second_half ? y1 : y0)) / (segment_y == 0 ? 1 : segment_y);
            int ax = x0 + (int)((x2 - x0) * f0);
            int bx = second_half ? x1 + (int)(segment_x * f1) : x0 + (int)(segment_x * f1);
            if (ax > bx) {
               int tmp = ax;
               ax = bx;
               bx = tmp;
            }

            gr.fill(ax, y, bx, y + 1, color);
         }
      }
   }

   public static class Model {
      public static void HeadRot(ModelPart head, float head_yaw, float head_pitch) {
         head_yaw = Mth.clamp(head_yaw, -30.0F, 30.0F);
         head_pitch = Mth.clamp(head_pitch, -25.0F, 45.0F);
         head.yRot = BU.rad(head_yaw);
         head.xRot = BU.rad(head_pitch);
      }

      public static void HeadBabyScale(LivingEntity entity, ModelPart head) {
         HeadBabyScale(entity, head, 2.0F);
      }

      public static void HeadBabyScale(LivingEntity entity, ModelPart head, float scale) {
         float f = entity.isBaby() ? scale : 1.0F;
         head.xScale = f;
         head.yScale = f;
         head.zScale = f;
      }

      public static void ArmSwing(LivingEntity entity, ModelPart arm_right, ModelPart arm_left, float attack_time) {
         if (!(attack_time <= 0.0F)) {
            boolean right_handed = entity.getMainArm() == HumanoidArm.RIGHT;
            ModelPart main_arm = right_handed ? arm_right : arm_left;
            ModelPart off_arm = right_handed ? arm_left : arm_right;
            ModelPart arm = entity.swingingArm == InteractionHand.MAIN_HAND ? main_arm : off_arm;
            ArmSwing(arm, attack_time);
         }
      }

      public static void ArmSwing(ModelPart arm, float attack_time) {
         if (!(attack_time <= 0.0F)) {
            float f = 1.0F - attack_time;
            f *= f;
            f *= f;
            f = 1.0F - f;
            arm.xRot = arm.xRot - Mth.sin(f * 3.1415927F) * 1.2F;
            arm.zRot = arm.zRot + Mth.sin(attack_time * 3.1415927F) * -0.4F;
         }
      }

      public static void LimbSwing(ModelPart arm_right, ModelPart arm_left, ModelPart leg_right, ModelPart leg_left, float limb_swing, float limb_swing_amount) {
         arm_right.xRot = Mth.cos(limb_swing * 0.6662F + 3.1415927F) * 2.0F * limb_swing_amount * 0.5F;
         arm_left.xRot = Mth.cos(limb_swing * 0.6662F) * 2.0F * limb_swing_amount * 0.5F;
         leg_right.xRot = Mth.cos(limb_swing * 0.6662F) * 1.4F * limb_swing_amount * 0.5F;
         leg_left.xRot = Mth.cos(limb_swing * 0.6662F + 3.1415927F) * 1.4F * limb_swing_amount * 0.5F;
      }
   }
}
