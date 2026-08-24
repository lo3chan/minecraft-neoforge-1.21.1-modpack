package at.petrak.hexcasting.api.casting.eval.env;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.ExecutionClientView;
import at.petrak.hexcasting.api.casting.eval.ResolvedPattern;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.HexCoord;
import at.petrak.hexcasting.api.mod.HexStatistics;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.common.msgs.IMessage;
import at.petrak.hexcasting.common.msgs.MsgClearSpiralPatternsS2C;
import at.petrak.hexcasting.common.msgs.MsgNewSpellPatternC2S;
import at.petrak.hexcasting.common.msgs.MsgNewSpellPatternS2C;
import at.petrak.hexcasting.common.msgs.MsgNewSpiralPatternsS2C;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import java.util.HashSet;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;

public class StaffCastEnv extends PlayerBasedCastEnv {
   private final InteractionHand castingHand;

   public StaffCastEnv(ServerPlayer caster, InteractionHand castingHand) {
      super(caster, castingHand);
      this.castingHand = castingHand;
   }

   @Override
   public void postExecution(CastResult result) {
      super.postExecution(result);
      if (result.component1() instanceof PatternIota patternIota) {
         MsgNewSpiralPatternsS2C packet = new MsgNewSpiralPatternsS2C(this.caster.getUUID(), List.of(patternIota.getPattern()), 2147483647);
         IXplatAbstractions.INSTANCE.sendPacketToPlayer(this.caster, packet);
         IXplatAbstractions.INSTANCE.sendPacketTracking(this.caster, packet);
      }

      SoundEvent sound = result.getSound().sound();
      if (sound != null) {
         Vec3 soundPos = this.caster.position();
         this.world.playSound(null, soundPos.x, soundPos.y, soundPos.z, sound, SoundSource.PLAYERS, 1.0F, 1.0F);
      }
   }

   @Override
   public long extractMediaEnvironment(long cost) {
      if (this.caster.isCreative()) {
         return 0L;
      } else {
         boolean canOvercast = this.canOvercast();
         long remaining = this.extractMediaFromInventory(cost, canOvercast);
         if (remaining > 0L && !canOvercast) {
            this.caster.sendSystemMessage(Component.translatable("hexcasting.message.cant_overcast"));
         }

         return remaining;
      }
   }

   @Override
   public InteractionHand getCastingHand() {
      return this.castingHand;
   }

   @Override
   public FrozenPigment getPigment() {
      return HexAPI.instance().getColorizer(this.caster);
   }

   public static void handleNewPatternOnServer(ServerPlayer sender, MsgNewSpellPatternC2S msg) {
      boolean cheatedPatternOverlap = false;
      List<ResolvedPattern> resolvedPatterns = msg.resolvedPatterns();
      if (!resolvedPatterns.isEmpty()) {
         HashSet<HexCoord> allPoints = new HashSet<>();

         for (int i = 0; i < resolvedPatterns.size() - 1; i++) {
            ResolvedPattern pat = resolvedPatterns.get(i);
            allPoints.addAll(pat.getPattern().positions(pat.getOrigin()));
         }

         ResolvedPattern currentResolvedPattern = resolvedPatterns.get(resolvedPatterns.size() - 1);
         List<HexCoord> currentSpellPoints = currentResolvedPattern.getPattern().positions(currentResolvedPattern.getOrigin());
         if (currentSpellPoints.stream().anyMatch(allPoints::contains)) {
            cheatedPatternOverlap = true;
         }
      }

      if (!cheatedPatternOverlap) {
         sender.awardStat(HexStatistics.PATTERNS_DRAWN);
         CastingVM vm = IXplatAbstractions.INSTANCE.getStaffcastVM(sender, msg.handUsed());
         ExecutionClientView clientInfo = vm.queueExecuteAndWrapIota(new PatternIota(msg.pattern()), sender.serverLevel());
         if (clientInfo.isStackClear()) {
            IXplatAbstractions.INSTANCE.setStaffcastImage(sender, null);
            IXplatAbstractions.INSTANCE.setPatterns(sender, List.of());
         } else {
            IXplatAbstractions.INSTANCE.setStaffcastImage(sender, vm.getImage().withOverriddenUsedOps(0L));
            if (!resolvedPatterns.isEmpty()) {
               resolvedPatterns.get(resolvedPatterns.size() - 1).setType(clientInfo.getResolutionType());
            }

            IXplatAbstractions.INSTANCE.setPatterns(sender, resolvedPatterns);
         }

         IXplatAbstractions.INSTANCE.sendPacketToPlayer(sender, new MsgNewSpellPatternS2C(clientInfo, resolvedPatterns.size() - 1));
         IMessage packet;
         if (clientInfo.isStackClear()) {
            packet = new MsgClearSpiralPatternsS2C(sender.getUUID());
         } else {
            packet = new MsgNewSpiralPatternsS2C(sender.getUUID(), List.of(msg.pattern()), 2147483647);
         }

         IXplatAbstractions.INSTANCE.sendPacketToPlayer(sender, packet);
         IXplatAbstractions.INSTANCE.sendPacketTracking(sender, packet);
         if (clientInfo.getResolutionType().getSuccess()) {
            new ParticleSpray(sender.position(), new Vec3(0.0, 1.5, 0.0), 0.4, 1.0471975511965976, 30)
               .sprayParticles(sender.serverLevel(), IXplatAbstractions.INSTANCE.getPigment(sender));
         }
      }
   }
}
