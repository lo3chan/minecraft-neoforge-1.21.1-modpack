package org.jcodec.movtool;

import java.util.List;
import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.containers.mp4.boxes.MovieFragmentBox;

public class CompoundMP4Edit implements MP4Edit {
   private List<MP4Edit> edits;

   public CompoundMP4Edit(List<MP4Edit> edits) {
      this.edits = edits;
   }

   @Override
   public void applyToFragment(MovieBox mov, MovieFragmentBox[] fragmentBox) {
      for (MP4Edit command : this.edits) {
         command.applyToFragment(mov, fragmentBox);
      }
   }

   @Override
   public void apply(MovieBox mov) {
      for (MP4Edit command : this.edits) {
         command.apply(mov);
      }
   }
}
