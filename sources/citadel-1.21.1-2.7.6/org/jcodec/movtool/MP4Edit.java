package org.jcodec.movtool;

import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.containers.mp4.boxes.MovieFragmentBox;

public interface MP4Edit {
   void applyToFragment(MovieBox var1, MovieFragmentBox[] var2);

   void apply(MovieBox var1);
}
