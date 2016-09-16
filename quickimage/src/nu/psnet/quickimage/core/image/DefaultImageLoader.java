/*
 * Software released under Common Public License (CPL) v1.0
 */
package nu.psnet.quickimage.core.image;

import java.io.File;

import org.eclipse.swt.graphics.ImageData;

class DefaultImageLoader implements ImageLoader {

	public boolean handles(File file) {
		return true;
	}

	public ImageData load(File file) {
		return new ImageData(file.getAbsolutePath());
	}

}
