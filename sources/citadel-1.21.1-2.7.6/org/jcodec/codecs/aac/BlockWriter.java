package org.jcodec.codecs.aac;

import org.jcodec.codecs.aac.blocks.Block;
import org.jcodec.common.io.BitWriter;

public class BlockWriter {
   public void nextBlock(BitWriter bits, Block block) {
      bits.writeNBit(block.getType().ordinal(), 3);
      if (block.getType() != BlockType.TYPE_END) {
         ;
      }
   }
}
