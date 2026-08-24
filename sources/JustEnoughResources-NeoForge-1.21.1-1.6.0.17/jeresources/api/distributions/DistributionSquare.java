package jeresources.api.distributions;

public class DistributionSquare extends DistributionBase {
   public DistributionSquare(int minY, int maxY, float chance) {
      super(DistributionHelpers.getSquareDistribution(Math.max(minY, -64), Math.min(maxY, 255), chance));
      this.bestHeight = (minY + maxY) / 2;
   }

   public DistributionSquare(int min0, int minY, int maxY, int max0, float chance) {
      super(DistributionHelpers.getRoundedSquareDistribution(Math.max(min0, -64), Math.max(minY, -64), Math.min(maxY, 255), Math.min(max0, 255), chance));
      this.bestHeight = DistributionHelpers.calculateMeanLevel(this.getDistribution(), (minY + maxY) / 2);
   }

   public DistributionSquare(int veinCount, int veinSize, int minY, int maxY) {
      this(minY - veinSize / 2, minY, maxY, maxY + veinSize / 2, DistributionHelpers.calculateChance(veinCount, veinSize, minY, maxY));
   }
}
