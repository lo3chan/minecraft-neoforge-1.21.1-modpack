package at.petrak.hexcasting.api.casting.eval;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.PatternShapeMatch;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadLocation;
import at.petrak.hexcasting.api.casting.mishaps.MishapDisallowedSpell;
import at.petrak.hexcasting.api.casting.mishaps.MishapEntityTooFarAway;
import at.petrak.hexcasting.api.mod.HexConfig;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.api.utils.HexUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CastingEnvironment {
   private static final List<Consumer<CastingEnvironment>> createEventListeners = new ArrayList<>();
   private boolean createEventTriggered = false;
   protected final ServerLevel world;
   protected Map<CastingEnvironmentComponent.Key<?>, CastingEnvironmentComponent> componentMap = new HashMap<>();
   private final List<CastingEnvironmentComponent.PostExecution> postExecutions = new ArrayList<>();
   private final List<CastingEnvironmentComponent.ExtractMedia> extractMedias = new ArrayList<>();
   private final List<CastingEnvironmentComponent.IsVecInRange> isVecInRanges = new ArrayList<>();
   private final List<CastingEnvironmentComponent.HasEditPermissionsAt> hasEditPermissionsAts = new ArrayList<>();

   public static void addCreateEventListener(Consumer<CastingEnvironment> listener) {
      createEventListeners.add(listener);
   }

   public final void triggerCreateEvent() {
      if (!this.createEventTriggered) {
         for (Consumer<CastingEnvironment> listener : createEventListeners) {
            listener.accept(this);
         }

         this.createEventTriggered = true;
      }
   }

   protected CastingEnvironment(ServerLevel world) {
      this.world = world;
   }

   public final ServerLevel getWorld() {
      return this.world;
   }

   @Nullable
   public abstract ServerPlayer getCaster();

   public abstract MishapEnvironment getMishapEnvironment();

   public <T extends CastingEnvironmentComponent> void addExtension(@NotNull T extension) {
      this.componentMap.put(extension.getKey(), extension);
      if (extension instanceof CastingEnvironmentComponent.PostExecution postExecution) {
         this.postExecutions.add(postExecution);
      }

      if (extension instanceof CastingEnvironmentComponent.ExtractMedia extractMedia) {
         this.extractMedias.add(extractMedia);
      }

      if (extension instanceof CastingEnvironmentComponent.IsVecInRange isVecInRange) {
         this.isVecInRanges.add(isVecInRange);
      }

      if (extension instanceof CastingEnvironmentComponent.HasEditPermissionsAt hasEditPermissionsAt) {
         this.hasEditPermissionsAts.add(hasEditPermissionsAt);
      }
   }

   public void removeExtension(@NotNull CastingEnvironmentComponent.Key<?> key) {
      CastingEnvironmentComponent extension = this.componentMap.remove(key);
      if (extension != null) {
         if (extension instanceof CastingEnvironmentComponent.PostExecution postExecution) {
            this.postExecutions.remove(postExecution);
         }

         if (extension instanceof CastingEnvironmentComponent.ExtractMedia extractMedia) {
            this.extractMedias.remove(extractMedia);
         }

         if (extension instanceof CastingEnvironmentComponent.IsVecInRange isVecInRange) {
            this.isVecInRanges.remove(isVecInRange);
         }

         if (extension instanceof CastingEnvironmentComponent.HasEditPermissionsAt hasEditPermissionsAt) {
            this.hasEditPermissionsAts.remove(hasEditPermissionsAt);
         }
      }
   }

   @Nullable
   public <T extends CastingEnvironmentComponent> T getExtension(@NotNull CastingEnvironmentComponent.Key<T> key) {
      return (T)this.componentMap.get(key);
   }

   public void precheckAction(PatternShapeMatch match) throws Mishap {
      ResourceLocation key = this.actionKey(match);
      if (!HexConfig.server().isActionAllowed(key)) {
         throw new MishapDisallowedSpell();
      }
   }

   @Nullable
   protected ResourceLocation actionKey(PatternShapeMatch match) {
      ResourceLocation key;
      if (match instanceof PatternShapeMatch.Normal normal) {
         key = normal.key.location();
      } else if (match instanceof PatternShapeMatch.PerWorld perWorld) {
         key = perWorld.key.location();
      } else if (match instanceof PatternShapeMatch.Special special) {
         key = special.key.location();
      } else {
         key = null;
      }

      return key;
   }

   public void postExecution(CastResult result) {
      for (CastingEnvironmentComponent.PostExecution postExecutionComponent : this.postExecutions) {
         postExecutionComponent.onPostExecution(result);
      }
   }

   public abstract Vec3 mishapSprayPos();

   public boolean isEnlightened() {
      ServerPlayer caster = this.getCaster();
      if (caster == null) {
         return false;
      } else {
         AdvancementHolder adv = this.world.getServer().getAdvancements().get(HexAPI.modLoc("enlightenment"));
         return adv == null ? false : caster.getAdvancements().getOrStartProgress(adv).isDone();
      }
   }

   public long extractMedia(long cost) {
      for (CastingEnvironmentComponent.ExtractMedia extractMediaComponent : this.extractMedias) {
         cost = extractMediaComponent.onExtractMedia(cost);
      }

      return this.extractMediaEnvironment(cost);
   }

   protected abstract long extractMediaEnvironment(long var1);

   public boolean isVecInRange(Vec3 vec) {
      boolean isInRange = this.isVecInRangeEnvironment(vec);

      for (CastingEnvironmentComponent.IsVecInRange isVecInRangeComponent : this.isVecInRanges) {
         isInRange = isVecInRangeComponent.onIsVecInRange(vec, isInRange);
      }

      return isInRange;
   }

   protected abstract boolean isVecInRangeEnvironment(Vec3 var1);

   public boolean hasEditPermissionsAt(BlockPos pos) {
      boolean hasEditPermissionsAt = this.hasEditPermissionsAtEnvironment(pos);

      for (CastingEnvironmentComponent.HasEditPermissionsAt hasEditPermissionsAtComponent : this.hasEditPermissionsAts) {
         hasEditPermissionsAt = hasEditPermissionsAtComponent.onHasEditPermissionsAt(pos, hasEditPermissionsAt);
      }

      return hasEditPermissionsAt;
   }

   protected abstract boolean hasEditPermissionsAtEnvironment(BlockPos var1);

   public final boolean isVecInWorld(Vec3 vec) {
      return this.world.isInWorldBounds(BlockPos.containing(vec)) && this.world.getWorldBorder().isWithinBounds(vec.x, vec.z, 0.5);
   }

   public final boolean isVecInAmbit(Vec3 vec) {
      return this.isVecInRange(vec) && this.isVecInWorld(vec);
   }

   public final boolean isEntityInRange(Entity e) {
      return e instanceof Player || this.isVecInRange(e.position());
   }

   public final void assertVecInRange(Vec3 vec) throws MishapBadLocation {
      this.assertVecInWorld(vec);
      if (!this.isVecInRange(vec)) {
         throw new MishapBadLocation(vec, "too_far");
      }
   }

   public final void assertPosInRange(BlockPos vec) throws MishapBadLocation {
      this.assertVecInRange(new Vec3(vec.getX(), vec.getY(), vec.getZ()));
   }

   public final void assertPosInRangeForEditing(BlockPos vec) throws MishapBadLocation {
      this.assertVecInRange(new Vec3(vec.getX(), vec.getY(), vec.getZ()));
      if (!this.canEditBlockAt(vec)) {
         throw new MishapBadLocation(Vec3.atCenterOf(vec), "forbidden");
      }
   }

   public final boolean canEditBlockAt(BlockPos vec) {
      return this.isVecInRange(Vec3.atCenterOf(vec)) && this.hasEditPermissionsAt(vec);
   }

   public final void assertEntityInRange(Entity e) throws MishapEntityTooFarAway {
      if (!this.isVecInWorld(e.position())) {
         throw new MishapEntityTooFarAway(e);
      } else if (!this.isVecInRange(e.position())) {
         throw new MishapEntityTooFarAway(e);
      }
   }

   public final void assertVecInWorld(Vec3 vec) throws MishapBadLocation {
      if (!this.isVecInWorld(vec)) {
         throw new MishapBadLocation(vec, "out_of_world");
      }
   }

   public abstract InteractionHand getCastingHand();

   public InteractionHand getOtherHand() {
      return HexUtils.otherHand(this.getCastingHand());
   }

   protected abstract List<ItemStack> getUsableStacks(CastingEnvironment.StackDiscoveryMode var1);

   protected abstract List<CastingEnvironment.HeldItemInfo> getPrimaryStacks();

   @Nullable
   public ItemStack queryForMatchingStack(Predicate<ItemStack> stackOk) {
      for (ItemStack stack : this.getUsableStacks(CastingEnvironment.StackDiscoveryMode.QUERY)) {
         if (stackOk.test(stack)) {
            return stack;
         }
      }

      return null;
   }

   @Nullable
   public CastingEnvironment.HeldItemInfo getHeldItemToOperateOn(Predicate<ItemStack> stackOk) {
      for (CastingEnvironment.HeldItemInfo stack : this.getPrimaryStacks()) {
         if (stackOk.test(stack.stack)) {
            return stack;
         }
      }

      return null;
   }

   protected boolean isCreativeMode() {
      return false;
   }

   public boolean withdrawItem(Predicate<ItemStack> stackOk, int count, boolean actuallyRemove) {
      if (this.isCreativeMode()) {
         return true;
      } else {
         List<ItemStack> stacks = this.getUsableStacks(CastingEnvironment.StackDiscoveryMode.EXTRACTION);
         int presentCount = 0;
         ArrayList<ItemStack> matches = new ArrayList<>();

         for (ItemStack stack : stacks) {
            if (stackOk.test(stack)) {
               presentCount += stack.getCount();
               matches.add(stack);
               if (presentCount >= count) {
                  break;
               }
            }
         }

         if (presentCount < count) {
            return false;
         } else if (!actuallyRemove) {
            return true;
         } else {
            int remaining = count;

            for (ItemStack match : matches) {
               int toWithdraw = Math.min(match.getCount(), remaining);
               match.shrink(toWithdraw);
               remaining -= toWithdraw;
               if (remaining <= 0) {
                  return true;
               }
            }

            throw new IllegalStateException("unreachable");
         }
      }
   }

   public abstract boolean replaceItem(Predicate<ItemStack> var1, ItemStack var2, @Nullable InteractionHand var3);

   public abstract FrozenPigment getPigment();

   @Nullable
   public abstract FrozenPigment setPigment(@Nullable FrozenPigment var1);

   public abstract void produceParticles(ParticleSpray var1, FrozenPigment var2);

   public abstract void printMessage(Component var1);

   public record HeldItemInfo(ItemStack stack, @Nullable InteractionHand hand) {
      public ItemStack component1() {
         return this.stack;
      }

      @Nullable
      public InteractionHand component2() {
         return this.hand;
      }
   }

   protected static enum StackDiscoveryMode {
      QUERY,
      EXTRACTION;
   }
}
