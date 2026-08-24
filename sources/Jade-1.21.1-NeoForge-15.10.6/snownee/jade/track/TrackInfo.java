package snownee.jade.track;

public abstract class TrackInfo {
   protected boolean alive = true;
   protected boolean updatedThisTick;

   public abstract void update(float var1);

   public abstract void tick();
}
