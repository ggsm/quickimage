package nu.psnet.quickimage.core.image;

import java.io.File;

import org.eclipse.swt.graphics.ImageData;

public final class ImageDataLoader {
	
	private static ImageLoader[] LOADERS = new ImageLoader[] {
			new AffinityDesignerImageLoader(),
			new DefaultImageLoader()
	}; 
	
	private ImageDataLoader() {
	}
	
	public static ImageData loadImageData(File file) {
		for (ImageLoader loader : LOADERS) {
			if (loader.handles(file)) {
				
			}
		}
		
		throw new RuntimeException("Unknown image format for file: " + file.getAbsolutePath());
	}
}
