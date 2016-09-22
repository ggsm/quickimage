package nu.psnet.quickimage.decorator;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

public class ImageSourceFileDecorator implements ILightweightLabelDecorator {

	public void addListener(final ILabelProviderListener listener) {

	}

	public void dispose() {

	}

	public boolean isLabelProperty(final Object element, final String property) {
		return false;
	}

	public void removeListener(final ILabelProviderListener listener) {

	}

	public void decorate(final Object element, final IDecoration decoration) {
		if (element instanceof IFile) {
			final IFile file = (IFile) element;
			if ("afdesign".equals(file.getFileExtension().toLowerCase())) {
				decoration.addSuffix(" source-file");
			}
		}
	}

}