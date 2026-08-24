package jeresources.api.restrictions;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class DimensionRestriction {
   public static final DimensionRestriction OVERWORLD = new DimensionRestriction(Level.OVERWORLD);
   public static final DimensionRestriction NETHER = new DimensionRestriction(Level.NETHER);
   public static final DimensionRestriction END = new DimensionRestriction(Level.END);
   public static final DimensionRestriction NONE = new DimensionRestriction();
   private Restriction.Type type;
   private ResourceKey<Level> dimension;

   private DimensionRestriction() {
      this.type = Restriction.Type.NONE;
   }

   public DimensionRestriction(ResourceKey<Level> type) {
      this(Restriction.Type.WHITELIST, type);
   }

   public DimensionRestriction(Restriction.Type type, ResourceKey<Level> dimension) {
      this.type = type;
      this.dimension = dimension;
   }

   @Override
   public boolean equals(Object obj) {
      return !(obj instanceof DimensionRestriction other) ? false : this.type == other.type && this.dimension.equals(other.dimension);
   }

   @Override
   public String toString() {
      return "Dimension: " + (this.type == Restriction.Type.NONE ? "None" : this.type.name() + " " + this.dimension.toString());
   }

   @Override
   public int hashCode() {
      return this.type == Restriction.Type.NONE ? super.hashCode() : this.type.hashCode() ^ this.dimension.hashCode();
   }

   public String getDimensionName() {
      return this.type == Restriction.Type.NONE ? "all" : this.dimension.location().toString();
   }
}
