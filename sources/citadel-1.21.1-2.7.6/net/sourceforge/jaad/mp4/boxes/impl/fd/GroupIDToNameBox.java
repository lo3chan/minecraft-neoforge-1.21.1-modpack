package net.sourceforge.jaad.mp4.boxes.impl.fd;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;

public class GroupIDToNameBox extends FullBox {
   private final Map<Long, String> map = new HashMap<>();

   public GroupIDToNameBox() {
      super("Group ID To Name Box");
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
      super.decode(in);
      int entryCount = (int)in.readBytes(2);

      for (int i = 0; i < entryCount; i++) {
         long id = in.readBytes(4);
         String name = in.readUTFString((int)this.getLeft(in), "UTF-8");
         this.map.put(id, name);
      }
   }

   public Map<Long, String> getMap() {
      return this.map;
   }
}
