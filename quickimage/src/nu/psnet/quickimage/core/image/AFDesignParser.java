/*
 * Software released under Common Public License (CPL) v1.0
 */
package nu.psnet.quickimage.core.image;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class AFDesignParser {
	private static final int THUMB_HEADER_PTR = 0x18;
	private static final int THUMB_SIZE_OFFSET = 0x18;
	private static final int THUMB_START_OFFSET = 0x1D;

	private AFDesignParser() {
	}

	public static ThumbNail parse(final byte[] bytes) {
		final ByteBuffer buffer = ByteBuffer.wrap(bytes);
		buffer.order(ByteOrder.LITTLE_ENDIAN);

		buffer.position(THUMB_HEADER_PTR);

		final int thumbNailHeader = buffer.getInt();

		buffer.position(thumbNailHeader + THUMB_SIZE_OFFSET);

		final int thumbNailLength = buffer.getInt();
		final int thumbNailStart = thumbNailHeader + THUMB_START_OFFSET;

		return new ThumbNail(thumbNailStart, thumbNailLength);
	}

	static class ThumbNail {
		private final int start;
		private final int size;

		ThumbNail(final int start, final int size) {
			this.start = start;
			this.size = size;
		}

		public int getSize() {
			return this.size;
		}

		public int getStart() {
			return this.start;
		}
	}

}
