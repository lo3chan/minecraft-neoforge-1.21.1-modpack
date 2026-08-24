package at.petrak.paucal.xplat.common.sounds;

import java.io.IOException;
import java.nio.ByteBuffer;
import javax.sound.sampled.AudioFormat;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.JOrbisAudioStream;

public class ImmediateAudioStream implements AudioStream {
   protected final JOrbisAudioStream backing;
   protected ByteBuffer lazyRead;

   public ImmediateAudioStream(JOrbisAudioStream backing) {
      this.backing = backing;
      this.lazyRead = null;
   }

   public AudioFormat getFormat() {
      return this.backing.getFormat();
   }

   public ByteBuffer read(int size) throws IOException {
      if (this.lazyRead == null) {
         this.lazyRead = this.backing.readAll();
         this.backing.close();
      }

      int maxSize = Math.min(size, this.lazyRead.remaining());
      ByteBuffer out = this.lazyRead.slice(0, maxSize);
      this.lazyRead.position(this.lazyRead.position() + maxSize);
      return out;
   }

   public void close() throws IOException {
      this.lazyRead.clear();
   }
}
