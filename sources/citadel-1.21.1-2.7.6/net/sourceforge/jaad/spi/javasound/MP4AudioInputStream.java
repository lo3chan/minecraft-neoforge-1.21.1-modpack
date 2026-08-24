package net.sourceforge.jaad.spi.javasound;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import net.sourceforge.jaad.aac.Decoder;
import net.sourceforge.jaad.aac.SampleBuffer;
import net.sourceforge.jaad.mp4.MP4Container;
import net.sourceforge.jaad.mp4.api.AudioTrack;
import net.sourceforge.jaad.mp4.api.Frame;
import net.sourceforge.jaad.mp4.api.Movie;
import net.sourceforge.jaad.mp4.api.Track;

class MP4AudioInputStream extends AsynchronousAudioInputStream {
   private final AudioTrack track;
   private final Decoder decoder;
   private final SampleBuffer sampleBuffer;
   private AudioFormat audioFormat;
   private byte[] saved;
   static final String ERROR_MESSAGE_AAC_TRACK_NOT_FOUND = "movie does not contain any AAC track";

   MP4AudioInputStream(InputStream in, AudioFormat format, long length) throws IOException {
      super(in, format, length);
      MP4Container cont = new MP4Container(in);
      Movie movie = cont.getMovie();
      List<Track> tracks = movie.getTracks(AudioTrack.AudioCodec.AAC);
      if (tracks.isEmpty()) {
         throw new IOException("movie does not contain any AAC track");
      } else {
         this.track = (AudioTrack)tracks.get(0);
         this.decoder = new Decoder(this.track.getDecoderSpecificInfo());
         this.sampleBuffer = new SampleBuffer();
      }
   }

   @Override
   public AudioFormat getFormat() {
      if (this.audioFormat == null) {
         this.decodeFrame();
         this.audioFormat = new AudioFormat(
            this.sampleBuffer.getSampleRate(), this.sampleBuffer.getBitsPerSample(), this.sampleBuffer.getChannels(), true, true
         );
         this.saved = this.sampleBuffer.getData();
      }

      return this.audioFormat;
   }

   @Override
   public void execute() {
      if (this.saved == null) {
         this.decodeFrame();
         if (this.buffer.isOpen()) {
            this.buffer.write(this.sampleBuffer.getData());
         }
      } else {
         this.buffer.write(this.saved);
         this.saved = null;
      }
   }

   private void decodeFrame() {
      if (!this.track.hasMoreFrames()) {
         this.buffer.close();
      } else {
         try {
            Frame frame = this.track.readNextFrame();
            if (frame == null) {
               this.buffer.close();
            } else {
               this.decoder.decodeFrame(frame.getData(), this.sampleBuffer);
            }
         } catch (IOException var2) {
            this.buffer.close();
         }
      }
   }
}
