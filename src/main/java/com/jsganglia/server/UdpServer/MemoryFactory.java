package com.jsganglia.server.UdpServer;

import java.nio.ByteBuffer;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class MemoryFactory extends BasePooledObjectFactory<ByteBuffer>{

	    final int BUFFER_SIZE = 1500; // TODO Versus max_udp_msg_len = 1472 ?

	    @Override
	    public ByteBuffer create() {
	        return  ByteBuffer.allocate(BUFFER_SIZE);
	    }


	    @Override
	    public PooledObject<ByteBuffer> wrap(ByteBuffer buffer) {
	        return new DefaultPooledObject<ByteBuffer>(buffer);
	    }


	    @Override
	    public void passivateObject(PooledObject<ByteBuffer> pooledObject) {
	    }
	
	
	
}
