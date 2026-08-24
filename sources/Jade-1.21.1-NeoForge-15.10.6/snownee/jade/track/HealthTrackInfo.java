package snownee.jade.track;

public class HealthTrackInfo extends TrackInfo {
   private float health;
   private float lastHealth;
   private int ticksSinceHurt = 1000;

   public HealthTrackInfo(float health) {
      this.health = this.lastHealth = health;
   }

   @Override
   public void update(float pTicks) {
   }

   @Override
   public void tick() {
      this.ticksSinceHurt++;
      if (this.health != this.lastHealth && this.ticksSinceHurt >= 5) {
         this.lastHealth = this.health;
      }
   }

   public float getLastHealth() {
      return this.lastHealth;
   }

   public boolean isBlinking() {
      return this.ticksSinceHurt < 5;
   }

   public void setHealth(float health) {
      if (health < this.health) {
         this.ticksSinceHurt = 0;
      } else if (health > this.health) {
         this.lastHealth = health;
      }

      this.health = health;
   }
}
