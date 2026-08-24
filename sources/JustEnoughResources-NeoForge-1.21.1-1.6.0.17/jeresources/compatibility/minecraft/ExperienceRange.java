package jeresources.compatibility.minecraft;

public record ExperienceRange(int min, int max) {
   public static final ExperienceRange ZERO = new ExperienceRange(0, 0);

   public String getExpString() {
      return this.max == this.min ? Integer.toString(this.min) : this.min + " - " + this.max;
   }
}
