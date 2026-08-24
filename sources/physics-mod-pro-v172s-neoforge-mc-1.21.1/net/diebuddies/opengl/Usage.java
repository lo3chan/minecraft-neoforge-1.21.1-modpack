package net.diebuddies.opengl;

public enum Usage {
   STATIC(35044),
   DYNAMIC(35048),
   STREAM(35040),
   DYNAMIC_READ(35049);

   private int usage;

   private Usage(int usage) {
      this.usage = usage;
   }

   public int getUsage() {
      return this.usage;
   }
}
