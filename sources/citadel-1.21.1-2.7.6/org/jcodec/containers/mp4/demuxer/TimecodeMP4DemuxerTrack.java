package org.jcodec.containers.mp4.demuxer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jcodec.common.IntArrayList;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.TapeTimecode;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.QTTimeUtil;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.ChunkOffsets64Box;
import org.jcodec.containers.mp4.boxes.ChunkOffsetsBox;
import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.containers.mp4.boxes.NodeBox;
import org.jcodec.containers.mp4.boxes.SampleToChunkBox;
import org.jcodec.containers.mp4.boxes.TimeToSampleBox;
import org.jcodec.containers.mp4.boxes.TimecodeSampleEntry;
import org.jcodec.containers.mp4.boxes.TrakBox;

public class TimecodeMP4DemuxerTrack {
   private TrakBox box;
   private TimeToSampleBox.TimeToSampleEntry[] timeToSamples;
   private int[] sampleCache;
   private TimecodeSampleEntry tse;
   private SeekableByteChannel input;
   private MovieBox movie;
   private long[] chunkOffsets;
   private SampleToChunkBox.SampleToChunkEntry[] sampleToChunks;

   public TimecodeMP4DemuxerTrack(MovieBox movie, TrakBox trak, SeekableByteChannel input) throws IOException {
      this.box = trak;
      this.input = input;
      this.movie = movie;
      NodeBox stbl = trak.getMdia().getMinf().getStbl();
      TimeToSampleBox stts = NodeBox.findFirst(stbl, TimeToSampleBox.class, "stts");
      SampleToChunkBox stsc = NodeBox.findFirst(stbl, SampleToChunkBox.class, "stsc");
      ChunkOffsetsBox stco = NodeBox.findFirst(stbl, ChunkOffsetsBox.class, "stco");
      ChunkOffsets64Box co64 = NodeBox.findFirst(stbl, ChunkOffsets64Box.class, "co64");
      this.timeToSamples = stts.getEntries();
      this.chunkOffsets = stco != null ? stco.getChunkOffsets() : co64.getChunkOffsets();
      this.sampleToChunks = stsc.getSampleToChunk();
      if (this.chunkOffsets.length == 1) {
         this.cacheSamples(this.sampleToChunks, this.chunkOffsets);
      }

      this.tse = (TimecodeSampleEntry)this.box.getSampleEntries()[0];
   }

   public MP4Packet getTimecode(MP4Packet pkt) throws IOException {
      long tv = QTTimeUtil.editedToMedia(this.box, this.box.rescale(pkt.getPts(), pkt.getTimescale()), this.movie.getTimescale());
      int ttsInd = 0;
      int ttsSubInd = 0;

      int sample;
      for (sample = 0; sample < this.sampleCache.length - 1; sample++) {
         int dur = this.timeToSamples[ttsInd].getSampleDuration();
         if (tv < dur) {
            break;
         }

         tv -= dur;
         if (ttsInd < this.timeToSamples.length - 1 && ++ttsSubInd >= this.timeToSamples[ttsInd].getSampleCount()) {
            ttsInd++;
         }
      }

      int frameNo = (int)(2L * tv * this.tse.getTimescale() / this.box.getTimescale() / this.tse.getFrameDuration() + 1L) / 2;
      return MP4Packet.createMP4PacketWithTimecode(pkt, _getTimecode(this.getTimecodeSample(sample), frameNo, this.tse));
   }

   private int getTimecodeSample(int sample) throws IOException {
      if (this.sampleCache != null) {
         return this.sampleCache[sample];
      } else {
         synchronized (this.input) {
            int stscInd = 0;

            int stscSubInd;
            for (stscSubInd = sample; stscInd < this.sampleToChunks.length && stscSubInd >= this.sampleToChunks[stscInd].getCount(); stscInd++) {
               stscSubInd -= this.sampleToChunks[stscInd].getCount();
            }

            long offset = this.chunkOffsets[stscInd] + (Math.min(stscSubInd, this.sampleToChunks[stscInd].getCount() - 1) << 2);
            if (this.input.position() != offset) {
               this.input.setPosition(offset);
            }

            ByteBuffer buf = NIOUtils.fetchFromChannel(this.input, 4);
            return buf.getInt();
         }
      }
   }

   private static TapeTimecode _getTimecode(int startCounter, int frameNo, TimecodeSampleEntry entry) {
      return TapeTimecode.tapeTimecode(frameNo + startCounter, entry.isDropFrame(), entry.getNumFrames() & 255);
   }

   private void cacheSamples(SampleToChunkBox.SampleToChunkEntry[] sampleToChunks, long[] chunkOffsets) throws IOException {
      synchronized (this.input) {
         int stscInd = 0;
         IntArrayList ss = IntArrayList.createIntArrayList();

         for (int chunkNo = 0; chunkNo < chunkOffsets.length; chunkNo++) {
            int nSamples = sampleToChunks[stscInd].getCount();
            if (stscInd < sampleToChunks.length - 1 && chunkNo + 1 >= sampleToChunks[stscInd + 1].getFirst()) {
               stscInd++;
            }

            long offset = chunkOffsets[chunkNo];
            this.input.setPosition(offset);
            ByteBuffer buf = NIOUtils.fetchFromChannel(this.input, nSamples * 4);

            for (int i = 0; i < nSamples; i++) {
               ss.add(buf.getInt());
            }
         }

         this.sampleCache = ss.toArray();
      }
   }

   /** @deprecated */
   public int getStartTimecode() throws IOException {
      return this.getTimecodeSample(0);
   }

   public TrakBox getBox() {
      return this.box;
   }

   public int parseTimecode(String tc) {
      String[] split = tc.split(":");
      TimecodeSampleEntry tmcd = NodeBox.findFirstPath(this.box, TimecodeSampleEntry.class, Box.path("mdia.minf.stbl.stsd.tmcd"));
      byte nf = tmcd.getNumFrames();
      return Integer.parseInt(split[3]) + Integer.parseInt(split[2]) * nf + Integer.parseInt(split[1]) * 60 * nf + Integer.parseInt(split[0]) * 3600 * nf;
   }

   public int timeCodeToFrameNo(String timeCode) throws Exception {
      if (isValidTimeCode(timeCode)) {
         int movieFrame = this.parseTimecode(timeCode.trim()) - this.sampleCache[0];
         int frameRate = this.tse.getNumFrames();
         long framesInTimescale = movieFrame * this.tse.getTimescale();
         long mediaToEdited = QTTimeUtil.mediaToEdited(this.box, framesInTimescale / frameRate, this.movie.getTimescale()) * frameRate;
         return (int)(mediaToEdited / this.box.getTimescale());
      } else {
         return -1;
      }
   }

   private static boolean isValidTimeCode(String input) {
      Pattern p = Pattern.compile("[0-9][0-9]:[0-5][0-9]:[0-5][0-9]:[0-2][0-9]");
      Matcher m = p.matcher(input);
      return input != null && !input.trim().equals("") && m.matches();
   }
}
