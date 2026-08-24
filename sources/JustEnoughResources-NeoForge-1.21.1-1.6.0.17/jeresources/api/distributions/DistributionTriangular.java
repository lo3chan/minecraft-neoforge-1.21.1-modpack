package jeresources.api.distributions;

public class DistributionTriangular extends DistributionBase {
   public DistributionTriangular(int midY, int range, float maxChance) {
      super(DistributionHelpers.getTriangularDistribution(midY, range, maxChance));
      this.bestHeight = midY;
   }

   public DistributionTriangular(int veinCount, int veinSize, int midY, int range) {
      super(DistributionHelpers.getTriangularDistribution(midY, range, DistributionHelpers.calculateChance(veinCount, veinSize, midY - range, midY + range)));
      this.bestHeight = midY;
   }
}
