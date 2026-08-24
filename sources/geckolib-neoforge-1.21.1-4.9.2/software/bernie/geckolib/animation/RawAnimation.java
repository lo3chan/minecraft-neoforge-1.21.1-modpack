package software.bernie.geckolib.animation;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public final class RawAnimation {
   public static final StreamCodec<ByteBuf, RawAnimation> STREAM_CODEC = StreamCodec.composite(
      RawAnimation.Stage.STREAM_CODEC.apply(ByteBufCodecs.list()), instance -> instance.animationList, RawAnimation::new
   );
   private final List<RawAnimation.Stage> animationList;

   private RawAnimation() {
      this(new ObjectArrayList());
   }

   private RawAnimation(List<RawAnimation.Stage> animationList) {
      this.animationList = animationList;
   }

   public static RawAnimation begin() {
      return new RawAnimation();
   }

   public RawAnimation thenPlay(String animationName) {
      return this.then(animationName, Animation.LoopType.DEFAULT);
   }

   public RawAnimation thenLoop(String animationName) {
      return this.then(animationName, Animation.LoopType.LOOP);
   }

   public RawAnimation thenWait(int ticks) {
      this.animationList.add(new RawAnimation.Stage("internal.wait", Animation.LoopType.PLAY_ONCE, ticks));
      return this;
   }

   public RawAnimation thenPlayAndHold(String animation) {
      return this.then(animation, Animation.LoopType.HOLD_ON_LAST_FRAME);
   }

   public RawAnimation thenPlayXTimes(String animationName, int playCount) {
      for (int i = 0; i < playCount; i++) {
         this.then(animationName, i == playCount - 1 ? Animation.LoopType.DEFAULT : Animation.LoopType.PLAY_ONCE);
      }

      return this;
   }

   public RawAnimation then(String animationName, Animation.LoopType loopType) {
      this.animationList.add(new RawAnimation.Stage(animationName, loopType));
      return this;
   }

   public List<RawAnimation.Stage> getAnimationStages() {
      return this.animationList;
   }

   public static RawAnimation copyOf(RawAnimation other) {
      RawAnimation newInstance = begin();
      newInstance.animationList.addAll(other.animationList);
      return newInstance;
   }

   public int getStageCount() {
      return this.animationList.size();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj != null && this.getClass() == obj.getClass() ? this.hashCode() == obj.hashCode() : false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.animationList);
   }

   public record Stage(String animationName, Animation.LoopType loopType, int additionalTicks) {
      public static final StreamCodec<ByteBuf, RawAnimation.Stage> STREAM_CODEC = StreamCodec.composite(
         ByteBufCodecs.STRING_UTF8,
         RawAnimation.Stage::animationName,
         ByteBufCodecs.STRING_UTF8.map(Animation.LoopType::fromString, Animation.LoopType::getId),
         RawAnimation.Stage::loopType,
         ByteBufCodecs.VAR_INT,
         RawAnimation.Stage::additionalTicks,
         RawAnimation.Stage::new
      );
      static final String WAIT = "internal.wait";

      public Stage(String animationName, Animation.LoopType loopType) {
         this(animationName, loopType, 0);
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else {
            return obj != null && this.getClass() == obj.getClass() ? this.hashCode() == obj.hashCode() : false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.animationName, this.loopType);
      }
   }
}
