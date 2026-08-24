package net.sourceforge.jaad.aac.syntax;

import net.sourceforge.jaad.aac.AACException;
import net.sourceforge.jaad.aac.SampleFrequency;
import net.sourceforge.jaad.aac.sbr.SBR;

public abstract class Element implements Constants {
   private int elementInstanceTag;
   private SBR sbr;

   protected void readElementInstanceTag(BitStream in) throws AACException {
      this.elementInstanceTag = in.readBits(4);
   }

   public int getElementInstanceTag() {
      return this.elementInstanceTag;
   }

   void decodeSBR(BitStream in, SampleFrequency sf, int count, boolean stereo, boolean crc, boolean downSampled, boolean smallFrames) throws AACException {
      if (this.sbr == null) {
         int fq = sf.getFrequency();
         if (fq < 24000 && !downSampled) {
            sf = SampleFrequency.forFrequency(2 * fq);
         }

         this.sbr = new SBR(smallFrames, stereo, sf, downSampled);
      }

      this.sbr.decode(in, count, crc);
   }

   boolean isSBRPresent() {
      return this.sbr != null;
   }

   SBR getSBR() {
      return this.sbr;
   }
}
