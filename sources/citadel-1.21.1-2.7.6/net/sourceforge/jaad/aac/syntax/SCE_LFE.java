package net.sourceforge.jaad.aac.syntax;

import net.sourceforge.jaad.aac.AACException;
import net.sourceforge.jaad.aac.DecoderConfig;

class SCE_LFE extends Element {
   private final ICStream ics;

   SCE_LFE(DecoderConfig config) {
      this.ics = new ICStream(config);
   }

   void decode(BitStream in, DecoderConfig conf) throws AACException {
      this.readElementInstanceTag(in);
      this.ics.decode(in, false, conf);
   }

   public ICStream getICStream() {
      return this.ics;
   }
}
