package org.jcodec.codecs.aac;

import org.jcodec.codecs.aac.blocks.Block;
import org.jcodec.common.io.BitReader;

public class BlockReader {
   public Block nextBlock(BitReader bits) {
      BlockType type = BlockType.values()[(int)bits.readNBit(3)];
      if (type == BlockType.TYPE_END) {
         return null;
      } else {
         int id = bits.readNBit(4);
         return null;
      }
   }
}
