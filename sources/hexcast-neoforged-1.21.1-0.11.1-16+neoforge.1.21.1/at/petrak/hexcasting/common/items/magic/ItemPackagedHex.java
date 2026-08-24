package at.petrak.hexcasting.common.items.magic;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.eval.ExecutionClientView;
import at.petrak.hexcasting.api.casting.eval.env.PackagedItemCastEnv;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.item.HexHolderItem;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.msgs.MsgNewSpiralPatternsS2C;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class ItemPackagedHex extends ItemMediaHolder implements HexHolderItem {
   public static final String TAG_PROGRAM = "patterns";
   public static final String TAG_PIGMENT = "pigment";
   public static final ResourceLocation HAS_PATTERNS_PRED = HexAPI.modLoc("has_patterns");

   public ItemPackagedHex(Properties pProperties) {
      super(pProperties);
   }

   public abstract boolean breakAfterDepletion();

   public abstract int cooldown();

   @Override
   public boolean canRecharge(ItemStack stack) {
      return !this.breakAfterDepletion();
   }

   @Override
   public boolean canProvideMedia(ItemStack stack) {
      return false;
   }

   @Override
   public boolean hasHex(ItemStack stack) {
      return NBTHelper.hasList(stack, "patterns", (byte)10);
   }

   @Nullable
   @Override
   public List<Iota> getHex(ItemStack stack, ServerLevel level) {
      ListTag patsTag = NBTHelper.getList(stack, "patterns", 10);
      if (patsTag == null) {
         return null;
      } else {
         ArrayList<Iota> out = new ArrayList<>();

         for (Tag patTag : patsTag) {
            CompoundTag tag = NBTHelper.getAsCompound(patTag);
            out.add(IotaType.deserialize(tag, level));
         }

         return out;
      }
   }

   @Override
   public void writeHex(ItemStack stack, List<Iota> program, @Nullable FrozenPigment pigment, long media) {
      ListTag patsTag = new ListTag();

      for (Iota pat : program) {
         patsTag.add(IotaType.serialize(pat));
      }

      NBTHelper.putList(stack, "patterns", patsTag);
      if (pigment != null) {
         NBTHelper.putCompound(stack, "pigment", pigment.serializeToNBT());
      }

      withMedia(stack, media, media);
   }

   @Override
   public void clearHex(ItemStack stack) {
      NBTHelper.remove(stack, "patterns");
      NBTHelper.remove(stack, "pigment");
      NBTHelper.remove(stack, "hexcasting:media");
      NBTHelper.remove(stack, "hexcasting:start_media");
   }

   @Nullable
   @Override
   public FrozenPigment getPigment(ItemStack stack) {
      CompoundTag ctag = NBTHelper.getCompound(stack, "pigment");
      return ctag == null ? null : FrozenPigment.fromNBT(ctag);
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand usedHand) {
      ItemStack stack = player.getItemInHand(usedHand);
      if (!this.hasHex(stack)) {
         return InteractionResultHolder.fail(stack);
      } else if (world.isClientSide) {
         return InteractionResultHolder.success(stack);
      } else {
         List<Iota> instrs = this.getHex(stack, (ServerLevel)world);
         if (instrs == null) {
            return InteractionResultHolder.fail(stack);
         } else {
            ServerPlayer sPlayer = (ServerPlayer)player;
            PackagedItemCastEnv ctx = new PackagedItemCastEnv(sPlayer, usedHand);
            CastingVM harness = CastingVM.empty(ctx);
            ExecutionClientView clientView = harness.queueExecuteAndWrapIotas(instrs, sPlayer.serverLevel());
            List<HexPattern> patterns = instrs.stream().filter(i -> i instanceof PatternIota).map(i -> ((PatternIota)i).getPattern()).toList();
            MsgNewSpiralPatternsS2C packet = new MsgNewSpiralPatternsS2C(sPlayer.getUUID(), patterns, 140);
            IXplatAbstractions.INSTANCE.sendPacketToPlayer(sPlayer, packet);
            IXplatAbstractions.INSTANCE.sendPacketTracking(sPlayer, packet);
            boolean broken = this.breakAfterDepletion() && this.getMedia(stack) == 0L;
            Stat<?> stat;
            if (broken) {
               stat = Stats.ITEM_BROKEN.get(this);
            } else {
               stat = Stats.ITEM_USED.get(this);
            }

            player.awardStat(stat);
            sPlayer.getCooldowns().addCooldown(this, this.cooldown());
            if (clientView.getResolutionType().getSuccess()) {
               new ParticleSpray(player.position(), new Vec3(0.0, 1.5, 0.0), 0.4, 1.0471975511965976, 30)
                  .sprayParticles(sPlayer.serverLevel(), ctx.getPigment());
            }

            SoundEvent sound = ctx.getSound().sound();
            if (sound != null) {
               Vec3 soundPos = sPlayer.position();
               sPlayer.level().playSound(null, soundPos.x, soundPos.y, soundPos.z, sound, SoundSource.PLAYERS, 1.0F, 1.0F);
            }

            if (broken) {
               stack.shrink(1);
               return InteractionResultHolder.consume(stack);
            } else {
               return InteractionResultHolder.success(stack);
            }
         }
      }
   }

   public UseAnim getUseAnimation(ItemStack pStack) {
      return UseAnim.BLOCK;
   }
}
