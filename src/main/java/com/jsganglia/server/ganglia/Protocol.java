package com.jsganglia.server.ganglia;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.jsganglia.server.ganglia.xdr.XDRInputBuffer;


public class Protocol {

    private final static Logger log = LoggerFactory.getLogger(Protocol.class);

	static public Gmessage decode(ByteBuffer buf) {
		

		final XDRInputBuffer xdr = new XDRInputBuffer(buf);

		final MessageType recordType;

		try {

			recordType = MessageType.valueOf(xdr.readInt());

		} catch (IllegalArgumentException ex) {

			log.warn("Unknown record type: " + ex);

			return null;
			
		}

		if (recordType.value() < MessageType.METADATA.value()) {
			
			log.warn("Ganglia 2.5 record type: " + recordType);

			return null;

		}
		


		final String hostName = xdr.readString();
		
		final String metricName = xdr.readString();
		
		final boolean spoof = xdr.readInt() == 0 ? false : true;

		switch (recordType) {
		case METADATA: {
			
			/*
			 * Decode a ganglia metric declaration record.
			 */
			
			// metric metadata record.
			final String metricTypeStr = xdr.readString();
			final String metricName2 = xdr.readString();
			final String units = xdr.readString();
			final Gslope slope = Gslope.valueOf(xdr.readInt());
			final int tmax = xdr.readInt();
			final int dmax = xdr.readInt();
			final Map<String, String[]> extraValues;
			final int nextra = xdr.readInt();
			if (nextra == 0) {
				extraValues = Collections.emptyMap();
			} else {
				extraValues = new LinkedHashMap<String, String[]>();
				for (int i = 0; i < nextra; i++) {
					final String name = xdr.readString();
					final String value = xdr.readString();
					final String[] a = extraValues.get(name);
					if (a == null) {
						extraValues.put(name, new String[] { value });
					} else {
						final String[] b = new String[a.length + 1];
						System.arraycopy(a/* src */, 0/* srcPos */, b/* dest */,
								0/* destPos */, a.length);
						b[a.length] = value;
						extraValues.put(name, b);
					}
				}
			}


			final MessageType metricType = MessageType
					.fromGType(metricTypeStr);
			
			final Gmessage msg = new GmetadataMessage(
					hostName, metricName, spoof, metricType, metricName2,
					units, slope, tmax, dmax, extraValues);
			
			return msg;
			
		}
		case REQUEST: {

             //ignore request

			return null;

		}
		case DOUBLE:
		case FLOAT:
		case INT32:
		case INT16:
		case STRING:
		case UINT32:
		case UINT16: {
			/*
			 * Decode a metric value.
			 * 
			 * [format:string][value]
			 * 
			 * where the encoding for the value depends on the recordType.
			 */
			// metric value record.
			final String format = xdr.readString();
			final Object value;
			switch (recordType) {
			case DOUBLE:
				value = xdr.readDouble();
				break;
			case FLOAT:
				value = xdr.readFloat();
				break;
			case INT32:
				value = xdr.readInt();
				break;
			case INT16:
				value = xdr.readShort();
				break;
			case UINT32:
				value = xdr.readUInt();
				break;
			case UINT16:
				value = xdr.readUShort();
				break;
			case STRING:
				value = xdr.readString();
				break;
			default:
				throw new AssertionError();
			}

			final Gmessage msg = new GmetricMessage(
					recordType, hostName, metricName, spoof, format, value);

			return msg;
		}
		default: {

			break;

		}
		}

		/*
		 * Something we are not handling.
		 */
		
		log.warn("Not handled: " + recordType);
		
		return null;

	}

    public static void main(String[] args) throws DecoderException{

    	//decoder.decode(data, off, len);
    	
    	String data1="000000850000000c646174616e6f646531303032000000296d65747269637373797374656d2e4d65747269637353797374656d2e5075626c6973684e756d4f70730000000000000000000002257300000000000331313600";
    	byte[] decodeData1=Hex.decodeHex(data1);
    	
    	
    	String data2="000000800000000c646174616e6f6465313030320000002a6d65747269637373797374656d2e4d65747269637353797374656d2e5075626c69736841766754696d6500000000000000000006646f75626c6500000000002a6d65747269637373797374656d2e4d65747269637353797374656d2e5075626c69736841766754696d65000000000000000000030000003c00000000000000010000000547524f55500000000000001b6d65747269637373797374656d2e4d65747269637353797374656d00";
    	byte[] decodeData2=Hex.decodeHex(data2);
   
    	
    	System.out.println(new Date());
    	int icount=0;
    	for(int i=0;i<1000000;i++){
    	{

    		ByteBuffer buf1=ByteBuffer.wrap(decodeData1);
    	Protocol protocol=new Protocol();
    	
    	Gmessage msg=	protocol.decode(buf1);
    	if(msg.isMetricValue()) {
    	icount++;
    	}
    	//System.out.println(msg);
    	}
    	
    	{
    	 	ByteBuffer buf2=ByteBuffer.wrap(decodeData2);
    	Protocol protocol=new Protocol();
    	Gmessage msg=	protocol.decode(buf2);
    	if(msg.isMetricMetadata()) {
    	icount++;
    	}
    	    //System.out.println(msg);
    	}
    	}
    	System.out.println(new Date());
    	System.out.println(icount);
    }
	
}
