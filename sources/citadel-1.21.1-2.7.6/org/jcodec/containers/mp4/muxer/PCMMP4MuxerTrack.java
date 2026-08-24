package org.jcodec.containers.mp4.muxer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;
import org.jcodec.common.AudioFormat;
import org.jcodec.common.LongArrayList;
import org.jcodec.common.Preconditions;
import org.jcodec.common.model.Packet;
import org.jcodec.common.model.Rational;
import org.jcodec.common.model.Size;
import org.jcodec.common.model.Unit;
import org.jcodec.containers.mp4.MP4TrackType;
import org.jcodec.containers.mp4.boxes.AudioSampleEntry;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.ChunkOffsets64Box;
import org.jcodec.containers.mp4.boxes.HandlerBox;
import org.jcodec.containers.mp4.boxes.Header;
import org.jcodec.containers.mp4.boxes.MediaBox;
import org.jcodec.containers.mp4.boxes.MediaHeaderBox;
import org.jcodec.containers.mp4.boxes.MediaInfoBox;
import org.jcodec.containers.mp4.boxes.MovieHeaderBox;
import org.jcodec.containers.mp4.boxes.NodeBox;
import org.jcodec.containers.mp4.boxes.SampleDescriptionBox;
import org.jcodec.containers.mp4.boxes.SampleEntry;
import org.jcodec.containers.mp4.boxes.SampleSizesBox;
import org.jcodec.containers.mp4.boxes.SampleToChunkBox;
import org.jcodec.containers.mp4.boxes.TimeToSampleBox;
import org.jcodec.containers.mp4.boxes.TrackHeaderBox;
import org.jcodec.containers.mp4.boxes.TrakBox;

public class PCMMP4MuxerTrack extends AbstractMP4MuxerTrack {
   private int frameDuration;
   private int frameSize;
   private int framesInCurChunk;
   private LongArrayList chunkOffsets = LongArrayList.createLongArrayList();
   private int totalFrames;

   public PCMMP4MuxerTrack(int trackId, AudioFormat format) {
      super(trackId, MP4TrackType.SOUND);
      this.frameDuration = 1;
      this.frameSize = (format.getSampleSizeInBits() >> 3) * format.getChannels();
      this.addSampleEntry(AudioSampleEntry.audioSampleEntryPCM(format));
      this._timescale = format.getSampleRate();
      this.setTgtChunkDuration(new Rational(1, 2), Unit.SEC);
   }

   @Override
   public void addFrame(Packet outPacket) throws IOException {
      this.addSamples(outPacket.getData().duplicate());
   }

   public void addSamples(ByteBuffer buffer) throws IOException {
      this.curChunk.add(buffer);
      int frames = buffer.remaining() / this.frameSize;
      this.totalFrames += frames;
      this.framesInCurChunk += frames;
      this.chunkDuration = this.chunkDuration + frames * this.frameDuration;
      this.outChunkIfNeeded();
   }

   private void outChunkIfNeeded() throws IOException {
      Preconditions.checkState(this.tgtChunkDurationUnit == Unit.FRAME || this.tgtChunkDurationUnit == Unit.SEC, "");
      if (this.tgtChunkDurationUnit == Unit.FRAME && this.framesInCurChunk * this.tgtChunkDuration.getDen() == this.tgtChunkDuration.getNum()) {
         this.outChunk();
      } else if (this.tgtChunkDurationUnit == Unit.SEC
         && this.chunkDuration > 0L
         && this.chunkDuration * this.tgtChunkDuration.getDen() >= this.tgtChunkDuration.getNum() * this._timescale) {
         this.outChunk();
      }
   }

   private void outChunk() throws IOException {
      if (this.framesInCurChunk != 0) {
         this.chunkOffsets.add(this.out.position());

         for (ByteBuffer b : this.curChunk) {
            this.out.write(b);
         }

         this.curChunk.clear();
         if (this.samplesInLastChunk == -1 || this.framesInCurChunk != this.samplesInLastChunk) {
            this.samplesInChunks.add(new SampleToChunkBox.SampleToChunkEntry(this.chunkNo + 1, this.framesInCurChunk, 1));
         }

         this.samplesInLastChunk = this.framesInCurChunk;
         this.chunkNo++;
         this.framesInCurChunk = 0;
         this.chunkDuration = 0L;
      }
   }

   @Override
   protected Box finish(MovieHeaderBox mvhd) throws IOException {
      if (this.finished) {
         throw new IllegalStateException("The muxer track has finished muxing");
      } else {
         this.outChunk();
         this.finished = true;
         TrakBox trak = TrakBox.createTrakBox();
         Size dd = this.getDisplayDimensions();
         TrackHeaderBox tkhd = TrackHeaderBox.createTrackHeaderBox(
            this.trackId,
            (long)mvhd.getTimescale() * this.totalFrames * this.frameDuration / this._timescale,
            dd.getWidth(),
            dd.getHeight(),
            new Date().getTime(),
            new Date().getTime(),
            1.0F,
            (short)0,
            0L,
            new int[]{65536, 0, 0, 0, 65536, 0, 0, 0, 1073741824}
         );
         tkhd.setFlags(15);
         trak.add(tkhd);
         this.tapt(trak);
         MediaBox media = MediaBox.createMediaBox();
         trak.add(media);
         media.add(
            MediaHeaderBox.createMediaHeaderBox(this._timescale, this.totalFrames * this.frameDuration, 0, new Date().getTime(), new Date().getTime(), 0)
         );
         HandlerBox hdlr = HandlerBox.createHandlerBox("mhlr", this.type.getHandler(), "appl", 0, 0);
         media.add(hdlr);
         MediaInfoBox minf = MediaInfoBox.createMediaInfoBox();
         media.add(minf);
         this.mediaHeader(minf, this.type);
         minf.add(HandlerBox.createHandlerBox("dhlr", "url ", "appl", 0, 0));
         this.addDref(minf);
         NodeBox stbl = new NodeBox(new Header("stbl"));
         minf.add(stbl);
         this.putEdits(trak);
         this.putName(trak);
         stbl.add(SampleDescriptionBox.createSampleDescriptionBox(this.sampleEntries.toArray(new SampleEntry[0])));
         stbl.add(SampleToChunkBox.createSampleToChunkBox(this.samplesInChunks.toArray(new SampleToChunkBox.SampleToChunkEntry[0])));
         stbl.add(SampleSizesBox.createSampleSizesBox(this.frameSize, this.totalFrames));
         stbl.add(
            TimeToSampleBox.createTimeToSampleBox(
               new TimeToSampleBox.TimeToSampleEntry[]{new TimeToSampleBox.TimeToSampleEntry(this.totalFrames, this.frameDuration)}
            )
         );
         stbl.add(ChunkOffsets64Box.createChunkOffsets64Box(this.chunkOffsets.toArray()));
         return trak;
      }
   }

   @Override
   public long getTrackTotalDuration() {
      return this.totalFrames * this.frameDuration;
   }
}
