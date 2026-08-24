package jeresources.api.restrictions;

import java.util.List;

public class Restriction {
   public static final Restriction OVERWORLD = new Restriction(DimensionRestriction.OVERWORLD);
   public static final Restriction NETHER = new Restriction(DimensionRestriction.NETHER);
   public static final Restriction END = new Restriction(DimensionRestriction.END);
   public static final Restriction NONE = new Restriction();
   private BiomeRestriction biomeRestriction;
   private DimensionRestriction dimensionRestriction;

   private Restriction() {
      this(BiomeRestriction.NO_RESTRICTION);
   }

   public Restriction(BiomeRestriction biomeRestriction) {
      this(biomeRestriction, DimensionRestriction.NONE);
   }

   public Restriction(DimensionRestriction dimensionRestriction) {
      this(BiomeRestriction.NO_RESTRICTION, dimensionRestriction);
   }

   public Restriction(BiomeRestriction biomeRestriction, DimensionRestriction dimensionRestriction) {
      this.biomeRestriction = biomeRestriction;
      this.dimensionRestriction = dimensionRestriction;
   }

   public List<String> getBiomeRestrictions() {
      return this.biomeRestriction.toStringList();
   }

   public String getDimensionRestriction() {
      return this.dimensionRestriction.getDimensionName();
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof Restriction other)) {
         return false;
      } else {
         return !other.biomeRestriction.equals(this.biomeRestriction) ? false : other.dimensionRestriction.equals(this.dimensionRestriction);
      }
   }

   @Override
   public String toString() {
      return this.dimensionRestriction.toString() + ", " + this.biomeRestriction.toString();
   }

   @Override
   public int hashCode() {
      return this.dimensionRestriction.hashCode() ^ this.biomeRestriction.hashCode();
   }

   public static enum Type {
      NONE,
      BLACKLIST,
      WHITELIST;
   }
}
