/*
 * Software released under Common Public License (CPL) v1.0
 */
package nu.psnet.quickimage.core;

import java.io.File;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import nu.psnet.quickimage.core.image.ImageDataLoader;

/**
 * @author Per Salomonsson
 * 
 */
public class ImageHolder {
	
	private Image thumb;
	private Image fullsize;
	private IStorage storage;
	private File file;
	private String displayName = null;
	private Display display;
	private final int space = 16;
	private int absx, absy = 0;
	private final Point dim = new Point(140, 160);
	private boolean selected = false;
	private Color COLOR_GRAY;
	private Color COLOR_DARKBLUE;
	private QManager manager;
	private double imageSize = 0;
	static Font font;

	// TODO add "implements comparable" etc

	public ImageHolder(QManager manager, Display display) {
		this.manager = manager;
		this.display = display;
		COLOR_GRAY = display.getSystemColor(SWT.COLOR_DARK_GRAY);
		COLOR_DARKBLUE = display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND);

		if(font == null) {
    		try {
    			font = new Font(display, "", 8, SWT.NORMAL);
    		} catch (Exception e) {
    			font = display.getSystemFont();
    		}
    		dim.y = dim.x + font.getFontData()[0].getHeight() + 10;
		}
	}

	/**
	 * Use Point to transport x= width and y=height of the area that each thumb needs. including text
	 * @return
	 */
	public Point getThumbDimension() {
		return dim;
	}

	public void drawThumb(GC gc, int inX, int inY) {
		int x = inX;
		int y = inY;

		try {
			if (thumb == null)
				initThumb();

			Color c = gc.getForeground();
			if (isSelected()) {
				gc.setLineWidth(3);
				gc.setForeground(COLOR_DARKBLUE);
			} else
				gc.setForeground(COLOR_GRAY);

			Rectangle rect = new Rectangle(x + space / 2, y + space / 2, dim.x - space, dim.x - space);
			Rectangle rectClip = new Rectangle(rect.x - 2, rect.y - 2, rect.width + 4, dim.y);
			gc.setClipping(rectClip);

			gc.drawRectangle(rect);

			gc.setFont(font);
			gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
			gc.drawString(getDisplayName(), rect.x, rect.y + rect.height + 2, true);

			absx = x;
			absy = y;
			x += dim.x / 2 - thumb.getBounds().width / 2;
			y += dim.x / 2 - thumb.getBounds().height / 2;
			gc.drawImage(thumb, x, y);
			gc.setLineWidth(1);
			gc.setForeground(c);
		} catch (RuntimeException e) {
			replaceWithCannotDisplayImage();
			drawThumb(gc, inX, inY);
			// manager.getImageOrganizer().removeHolder(this);
			// e.printStackTrace();
		}
	}

	public boolean mouseClickedOver(int x, int y) {
		return (x > absx && x < absx + dim.x && y > absy && y < absy + dim.y);
	}

	public void drawFullsize(GC gc, int inX, int inY) {
		int x = inX;
		int y = inY;

		try {
			if (fullsize == null || fullsize.isDisposed())
				initFullsize();

			gc.drawImage(fullsize, x, y);
		} catch (RuntimeException e) {
			replaceWithCannotDisplayImage();
			drawFullsize(gc, inX, inY);
		}
	}

	public IStorage getStorage() {
		return storage;
	}

	public void setStorage(IStorage storage) {
		this.storage = storage;
		setDisplayName(storage.getName());
	}

	public double getImageSize() {
		return imageSize;
	}

	public void setFile(File file) {
		this.file = file;
		setDisplayName(file.getName());
		imageSize = file.length() / 1024D;
	}

	private void initFullsize() throws RuntimeException {
		if (fullsize != null && !fullsize.isDisposed())
			fullsize.dispose();

		ImageData data = null;
		if (file != null) {
			data = ImageDataLoader.loadImageData(file);
		} else {
			try {
				data = new ImageData(storage.getContents());
			} catch (CoreException e) {
				throw new RuntimeException("Could not load image from " + "IStorage: " + e.getMessage());
			}
		}
		fullsize = new Image(display, data);
	}

	public Image getFullsize() {
		try {
			if (fullsize == null || fullsize.isDisposed())
				initFullsize();
		} catch (RuntimeException e) {
			replaceWithCannotDisplayImage();
			initFullsize();
		}

		return fullsize;
	}

	/**
	 * Returns if the fullsize image has been initialize or not.. ie null or
	 * not.
	 */
	public boolean hasFullsize() {
		if (fullsize != null && !fullsize.isDisposed())
			return true;

		return false;
	}

	private void initThumb() {
		if (thumb != null)
			thumb.dispose();

		ImageData data = ImageDataLoader.loadImageData(file);
		float w = 0;
		float h = 0;
		boolean doscale = false;

		if (data.height > dim.x - space * 2) {
			doscale = true;
			h = (float) (data.height - (dim.x - space * 2)) / data.height;
		}
		if (data.width > dim.x - space * 2) {
			doscale = true;
			w = (float) (data.width - (dim.x - space * 2)) / data.width;
		}

		if (doscale) {
			float scale = Math.max(w, h);
			w = data.width - data.width * scale;
			h = data.height - data.height * scale;
			if (w < 1)
				w = 1;
			if (h < 1)
				h = 1;
			data = data.scaledTo((int) w, (int) h);
		}

		thumb = new Image(display, data);
	}

	public void dispose() {
		if (fullsize != null && !fullsize.isDisposed()) {
			fullsize.dispose();
		}

		if (thumb != null && !thumb.isDisposed()) {
			thumb.dispose();
		}
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	private void replaceWithCannotDisplayImage() {
		try {
			dispose();
			fullsize = null;
			thumb = null;
			String tmpName = getDisplayName();
			setFile(new File(manager.getImageEditor().getIconsdir() + "broken_image.gif"));
			setDisplayName(tmpName + " (image could not be displayed)");
		} catch (Exception e) {
			manager.getImageOrganizer().removeHolder(this);
		}
	}
}
