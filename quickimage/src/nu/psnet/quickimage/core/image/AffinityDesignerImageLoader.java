package nu.psnet.quickimage.core.image;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.graphics.ImageData;

import nu.psnet.quickimage.core.image.AFDesignParser.ThumbNail;

class AffinityDesignerImageLoader implements ImageLoader {

	public boolean handles(File file) {
		return file.getName().toLowerCase().endsWith(".afdesign");
	}

	public ImageData load(File file) {
		InputStream is = null;
		try {
			is = toPNGStream(file);
			return new ImageData(is);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	
   private static InputStream toPNGStream(File file) throws IOException {
	   FileInputStream fileStream = new FileInputStream(file);
	   
       final byte[] bytes = toByteArray(fileStream);
       final ThumbNail thumbNail = AFDesignParser.parse(bytes);
        
       final byte[] png = new byte[thumbNail.getSize()];
        
       System.arraycopy(bytes, thumbNail.getStart(), png, 0, thumbNail.getSize());
        
       return new ByteArrayInputStream(png);
   }

   private static byte[] toByteArray(InputStream is) throws IOException {
	   ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	   int amount;
	   byte[] data = new byte[0x4000];

	   while ((amount = is.read(data, 0, data.length)) != -1) {
	     buffer.write(data, 0, amount);
	   }

	   buffer.flush();

	   return buffer.toByteArray();
   }
}
