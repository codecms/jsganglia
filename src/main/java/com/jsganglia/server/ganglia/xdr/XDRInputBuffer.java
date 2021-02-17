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
 * A class for reading XDR data from an internal buffer.
 * 
 * @see <a href="http://www.ietf.org/rfc/rfc4506.txt">RDF 4506</a>
 */
final public class XDRInputBuffer {

	/** The backing buffer. */
	private final ByteBuffer buf;



	/**
	 * 
	 * @param buffer
	 *            The buffer containing the data to be read.
	 * @param off
	 *            The offset of the first byte with data to be read.
	 * @param len
	 *            The #of bytes with data to be read starting at that offset.
	 */
	public XDRInputBuffer( ByteBuffer buffer ) {

		if (buffer == null || buffer.limit() <= 0) {
			throw new IllegalArgumentException();
		}

		this.buf = buffer;

	}

	public long readLong() {
        
		long v = buf.getLong();
       
        
		return v;

	}

	public int readInt() {
        
		int v = buf.getInt();
        
		return v;

	}

	public long readUInt() {
		
        long v =   buf.getInt() & 0xffffffffL;
        


        return v;
        
	}
	
	public short readShort() {

		int v = buf.getShort();
		 v=buf.getShort();


		return (short) v;

	}

	public int readUShort() {

		int v = buf.getShort();
		v=buf.getShort() & 0xffff;


		return v;

	}



	public float readFloat() {

		final int v = readInt();
        
        return Float.intBitsToFloat(v);
        
	}
	
	public double readDouble() {

		final long v = readLong();
        
        return Double.longBitsToDouble(v);
        
	}

	public String readString() {
		
		final int n = readInt();
		byte[] data=new byte[n];
		buf.get(data);
		
		final String s = new String(data);

		
		pad(n);
		
		return s;
		
	}

	/**
	 * Skips pad bytes in the buffer up to the nearest multiple of 4 (all XDR
	 * fields must be padded out to the nearest multiple of four bytes if they
	 * do not fall on a 4 byte boundary).
	 * 
	 * @see http://www.ietf.org/rfc/rfc4506.txt
	 */
	private void pad(int n) {
		int ret=n %4;
        if(ret >0) {
        	ret=4-ret;
        }
			while(ret>0) {
				buf.get();
				ret--;
			}

	}

}
