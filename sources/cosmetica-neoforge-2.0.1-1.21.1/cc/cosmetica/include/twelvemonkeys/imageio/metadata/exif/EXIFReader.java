package cc.cosmetica.include.twelvemonkeys.imageio.metadata.exif;

import cc.cosmetica.include.twelvemonkeys.imageio.metadata.Directory;
import cc.cosmetica.include.twelvemonkeys.imageio.metadata.MetadataReader;
import cc.cosmetica.include.twelvemonkeys.imageio.metadata.tiff.TIFFReader;
import java.io.IOException;
import javax.imageio.stream.ImageInputStream;

@Deprecated
public final class EXIFReader extends MetadataReader {
   private final TIFFReader delegate = new TIFFReader();

   @Override
   public Directory read(ImageInputStream var1) throws IOException {
      return this.delegate.read(var1);
   }
}
