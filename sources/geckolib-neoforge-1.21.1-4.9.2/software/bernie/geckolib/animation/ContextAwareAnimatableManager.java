package software.bernie.geckolib.animation;

import java.util.Map;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.state.BoneSnapshot;
import software.bernie.geckolib.constant.dataticket.DataTicket;

public abstract class ContextAwareAnimatableManager<T extends GeoAnimatable, C> extends AnimatableManager<T> {
   private final Map<C, AnimatableManager<T>> managers;

   public ContextAwareAnimatableManager(GeoAnimatable animatable) {
      super(animatable);
      this.managers = this.buildContextOptions(animatable);
   }

   protected abstract Map<C, AnimatableManager<T>> buildContextOptions(GeoAnimatable var1);

   public abstract C getCurrentContext();

   public AnimatableManager<T> getManagerForContext(C context) {
      return this.managers.get(context);
   }

   @Override
   public void addController(AnimationController controller) {
      this.getManagerForContext(this.getCurrentContext()).addController(controller);
   }

   @Override
   public void removeController(String name) {
      this.getManagerForContext(this.getCurrentContext()).removeController(name);
   }

   @Override
   public Map<String, AnimationController<T>> getAnimationControllers() {
      return this.getManagerForContext(this.getCurrentContext()).getAnimationControllers();
   }

   @Override
   public Map<String, BoneSnapshot> getBoneSnapshotCollection() {
      return this.getManagerForContext(this.getCurrentContext()).getBoneSnapshotCollection();
   }

   @Override
   public void clearSnapshotCache() {
      this.getManagerForContext(this.getCurrentContext()).clearSnapshotCache();
   }

   @Override
   public double getLastUpdateTime() {
      return this.getManagerForContext(this.getCurrentContext()).getLastUpdateTime();
   }

   @Override
   public void updatedAt(double updateTime) {
      this.getManagerForContext(this.getCurrentContext()).updatedAt(updateTime);
   }

   @Override
   public double getFirstTickTime() {
      return this.getManagerForContext(this.getCurrentContext()).getFirstTickTime();
   }

   @Override
   public void startedAt(double time) {
      this.getManagerForContext(this.getCurrentContext()).startedAt(time);
   }

   @Override
   public boolean isFirstTick() {
      return this.getManagerForContext(this.getCurrentContext()).isFirstTick();
   }

   @Override
   protected void finishFirstTick() {
      this.getManagerForContext(this.getCurrentContext()).finishFirstTick();
   }

   @Override
   public void tryTriggerAnimation(String animName) {
      for (AnimatableManager<T> manager : this.managers.values()) {
         manager.tryTriggerAnimation(animName);
      }
   }

   @Override
   public void tryTriggerAnimation(String controllerName, String animName) {
      for (AnimatableManager<T> manager : this.managers.values()) {
         manager.tryTriggerAnimation(controllerName, animName);
      }
   }

   @Override
   public void stopTriggeredAnimation(@Nullable String animName) {
      for (AnimatableManager<T> manager : this.managers.values()) {
         manager.stopTriggeredAnimation(animName);
      }
   }

   @Override
   public void stopTriggeredAnimation(String controllerName, @Nullable String animName) {
      for (AnimatableManager<T> manager : this.managers.values()) {
         manager.stopTriggeredAnimation(controllerName, animName);
      }
   }

   @Override
   public <D> void setData(DataTicket<D> dataTicket, D data) {
      super.setData(dataTicket, data);
   }

   @Override
   public <D> D getData(DataTicket<D> dataTicket) {
      return super.getData(dataTicket);
   }
}
