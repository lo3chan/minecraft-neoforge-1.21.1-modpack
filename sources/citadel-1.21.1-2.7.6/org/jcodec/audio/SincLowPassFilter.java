package org.jcodec.audio;

public class SincLowPassFilter extends ConvolutionFilter {
   private int kernelSize;
   private double cutoffFreq;

   public static SincLowPassFilter createSincLowPassFilter(double cutoffFreq) {
      return new SincLowPassFilter(40, cutoffFreq);
   }

   public static SincLowPassFilter createSincLowPassFilter2(int cutoffFreq, int samplingRate) {
      return new SincLowPassFilter(40, (double)cutoffFreq / samplingRate);
   }

   public SincLowPassFilter(int kernelSize, double cutoffFreq) {
      this.kernelSize = kernelSize;
      this.cutoffFreq = cutoffFreq;
   }

   @Override
   protected double[] buildKernel() {
      double[] kernel = new double[this.kernelSize];
      double sum = 0.0;

      for (int i = 0; i < this.kernelSize; i++) {
         int a = i - this.kernelSize / 2;
         if (a != 0) {
            kernel[i] = Math.sin(6.283185307179586 * this.cutoffFreq * (i - this.kernelSize / 2))
               / (i - this.kernelSize / 2)
               * (0.54 - 0.46 * Math.cos(6.283185307179586 * i / this.kernelSize));
         } else {
            kernel[i] = 6.283185307179586 * this.cutoffFreq;
         }

         sum += kernel[i];
      }

      for (int i = 0; i < this.kernelSize; i++) {
         kernel[i] /= sum;
      }

      return kernel;
   }
}
