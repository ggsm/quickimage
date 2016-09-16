package nu.psnet.quickimage.core.image;

import java.io.File;

import org.eclipse.swt.graphics.ImageData;

interface ImageLoader {
	boolean handles(File file);
	
	ImageData load(File file);
}
