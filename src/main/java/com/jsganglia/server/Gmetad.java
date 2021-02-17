package com.jsganglia.server;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.TimeUnit;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.jsganglia.server.UdpServer.MemoryFactory;
import com.jsganglia.server.UdpServer.MessageProc;

import com.jsganglia.server.conf.ConfigureLoad;
import com.jsganglia.server.ganglia.Gmessage;
import com.jsganglia.server.ganglia.Protocol;



@SpringBootApplication
@EnableAsync
public class Gmetad {

    private final static Logger logger = LoggerFactory.getLogger(Gmetad.class);
	

	
	public static MetricRegistry metrics = new MetricRegistry();

	
	@Autowired
	ConfigureLoad configureLoad;
	
	@Autowired
	MessageProc messageProc;

	
	
	public  void mainRun() throws Exception {
		// TODO Auto-generated method stub

		Meter requests = metrics.meter("udp.recv");

		Slf4jReporter slfreport = Slf4jReporter.forRegistry(metrics).convertRatesTo(TimeUnit.SECONDS)
		.convertDurationsTo(TimeUnit.MILLISECONDS).build();
		slfreport.start(300, TimeUnit.SECONDS);
		
		

		GenericObjectPoolConfig config = new GenericObjectPoolConfig();

		config.setMaxIdle(100);
		config.setMaxTotal(10000);
		PooledObjectFactory factory = new MemoryFactory();
		final ObjectPool<ByteBuffer> pool = new GenericObjectPool<ByteBuffer>(factory, config);

		metrics.register("pool.active", new Gauge<Integer>() {

			public Integer getValue() {
				return pool.getNumActive();
			}
		});

		metrics.register("pool.idle", new Gauge<Integer>() {

			public Integer getValue() {
				return pool.getNumIdle();
			}
		});

		
		messageProc.start();
		
		ByteBuffer buf = null;

		DatagramChannel channel;

		channel = DatagramChannel.open();
		channel.setOption(StandardSocketOptions.SO_RCVBUF, 1024*1024*50);
		channel.socket().bind(new InetSocketAddress(configureLoad.getGangliaPort()));
		
		System.out.println("udp start:"+configureLoad.getGangliaPort());

		while (!Thread.interrupted()) {

			buf = pool.borrowObject();// 不归还就不能借

			buf.clear();
			buf.rewind();
             
			
			channel.receive(buf);
			buf.rewind();

			requests.mark();

			Gmessage msg;
			try {
				msg = Protocol.decode(buf);
				MessageProc.processMessage(msg);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			pool.returnObject(buf);

		}

	}
	
	
	public static void main(String[] args) throws Exception  {
		

        //web app start
        ConfigurableApplicationContext context = SpringApplication.run(Gmetad.class,args);
        logger.info("Http Server start!");
        
        Gmetad metad= context.getBean(Gmetad.class);
        
        metad.mainRun();
		
	}

}
