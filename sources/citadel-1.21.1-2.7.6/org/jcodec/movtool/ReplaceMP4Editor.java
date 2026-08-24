package org.jcodec.movtool;

import java.io.File;
import java.io.IOException;
import org.jcodec.containers.mp4.MP4Util;

public class ReplaceMP4Editor {
   public void modifyOrReplace(File src, MP4Edit edit) throws IOException {
      boolean modify = new InplaceMP4Editor().modify(src, edit);
      if (!modify) {
         this.replace(src, edit);
      }
   }

   public void replace(File src, MP4Edit edit) throws IOException {
      File tmp = new File(src.getParentFile(), "." + src.getName());
      this.copy(src, tmp, edit);
      tmp.renameTo(src);
   }

   public void copy(File src, File dst, MP4Edit edit) throws IOException {
      MP4Util.Movie movie = MP4Util.createRefFullMovieFromFile(src);
      edit.apply(movie.getMoov());
      Flatten fl = new Flatten();
      fl.flatten(movie, dst);
   }
}
