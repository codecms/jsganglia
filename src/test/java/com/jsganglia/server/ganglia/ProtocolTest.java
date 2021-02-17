package com.jsganglia.server.ganglia;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.util.regex.Pattern;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

public class ProtocolTest {

	@Test
	public void test() throws DecoderException {
		
    	String data1="000000850000000c646174616e6f646531303032000000296d65747269637373797374656d2e4d65747269637353797374656d2e5075626c6973684e756d4f70730000000000000000000002257300000000000331313600";
    	byte[] decodeData1=Hex.decodeHex(data1);
    	
    	
    	String data2="000000800000000c646174616e6f6465313030320000002a6d65747269637373797374656d2e4d65747269637353797374656d2e5075626c69736841766754696d6500000000000000000006646f75626c6500000000002a6d65747269637373797374656d2e4d65747269637353797374656d2e5075626c69736841766754696d65000000000000000000030000003c00000000000000010000000547524f55500000000000001b6d65747269637373797374656d2e4d65747269637353797374656d00";
    	byte[] decodeData2=Hex.decodeHex(data2);
		
    	{
   		ByteBuffer buf1=ByteBuffer.wrap(decodeData1);
    	Protocol protocol=new Protocol();
    	
    	Gmessage msg=	protocol.decode(buf1);

    	System.out.println(msg);
    	}
    	
    	{
    	 	ByteBuffer buf2=ByteBuffer.wrap(decodeData2);
    	Protocol protocol=new Protocol();
    	Gmessage msg=	protocol.decode(buf2);

    	    System.out.println(msg);
    	}
	}
	
	@Test
	public void testMatch() throws DecoderException {
		 String org="metricssystem.MetricsSystem.NumActiveSources";
		 String mstr="metricssystem.1Me";
		 Pattern  reg= Pattern.compile("abc|.*"+mstr+".*");
		 assertFalse(reg.matcher(org).matches());

	}
	@Test
	public void testMatch0() throws DecoderException {
		 String org="metricssystem.MetricsSystem.NumActiveSources";
		 String mstr="metricssystem.Me";
		 Pattern  reg= Pattern.compile("abc|"+mstr+"");
		 assertFalse(reg.matcher(org).matches());

	}
	
	@Test
	public void testMatch01() throws DecoderException {
		 String org="abc";
		 String mstr="metricssystem.Me";
		 Pattern  reg= Pattern.compile("abc|"+mstr+"");
		 
		 assertTrue(reg.matcher(org).matches());

	}
	
	
	@Test
	public void testMatch2() throws DecoderException {
		 String org="metricssystem.MetricsSystem.NumActiveSources";
		 String mstr="metricssystem.Me";
		 Pattern  reg= Pattern.compile("abc|.*"+mstr+".*");
		 assertTrue(reg.matcher(org).matches());
        
		 //  private final Pattern tagPattern = Pattern.compile("^(\\w+):(.*)");
	}
	

}
