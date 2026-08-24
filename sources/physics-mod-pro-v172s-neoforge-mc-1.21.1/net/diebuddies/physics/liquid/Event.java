package net.diebuddies.physics.liquid;

public abstract class Event {
   public LiquidContouringThread thread;
   public long id = -1L;

   public abstract void run();
}
