/*
   Copyright (C) SYSTAP, LLC 2006-2012.  All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.jsganglia.server.ganglia.xdr;

import java.nio.ByteBuffer;

/**
 * A class for writing XDR data onto an internal buffer.
 * 
 * @see <a href="http://www.ietf.org/rfc/rfc4506.txt">RDF 4506</a>
 */
public class XDROutputBuffer {

	private final ByteBuffer buf;

	/**
	 * 
	 * @param BUFFER_SIZE The size of the fixed capacity buffer.
	 */
	public XDROutputBuffer(ByteBuffer buffer) {
		
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		buf=buffer;
		buf.rewind();
		
	}




	/**
	 * Return the backing byte[] buffer.
	 */
	public ByteBuffer getBuffer() {
		
		return buf;
		
	}
	
	/** Return the #of bytes written onto the buffer. */
	public int getLength() {
		
		return buf.position();
		
	}
	
	/**
	 * Puts a string into the buffer by first writing the size of the string as
	 * an int, followed by the bytes of the string, padded if necessary to a
	 * multiple of 4.
	 */
	public void writeString(final String s) {
		final byte[] bytes = s.getBytes();
		final int len = bytes.length;
		buf.put(bytes);
		pad(len);
	}

	/**
	 * Pads the buffer with zero bytes up to the nearest multiple of 4 (all XDR
	 * fields must be padded out to the nearest multiple of four bytes if they
	 * do not fall on a 4 byte boundary).
	 * 
	 * @see http://www.ietf.org/rfc/rfc4506.txt
	 */
	private void pad(int len) {
		
		int ret=len %4;
        if(ret >0) {
        	ret=4-ret;
        }
			while(ret>0) {
				buf.put((byte) 0);
				ret--;
			}

	}
		
		




	/**
	 * Puts a short integer into the buffer as 2 bytes, big-endian but w/
	 * leading zeros (e.g., as if an int32 value). This is based on looking at
	 * ganglia data as received on the wire. For example, <code>cpu_num</code>
	 * is reported as ushort.
	 */
	public void writeShort(final short i) {
		buf.putShort((short) 0);
		buf.putShort(i);

	}

	/**
	 * Puts an integer into the buffer as 4 bytes, big-endian.
	 */
	public void writeInt(final int i) {
		buf.putInt(i);

	}

	/**
	 * Puts a long into the buffer as 8 bytes, big-endian.
	 */
	public void writeLong(final long i) {
		buf.putLong(i);
	}
	/**
	 * Puts a float into the buffer as 4 bytes, big-endian.
	 */
	public void writeFloat(final float f) {
		writeInt(Float.floatToIntBits(f));
	}

	/**
	 * Puts a double into the buffer as 8 bytes, big-endian.
	 */
	public void writeDouble(final double d) {
		writeLong(Double.doubleToLongBits(d));
	}

}
