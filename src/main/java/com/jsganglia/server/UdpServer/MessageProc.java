package com.jsganglia.server.UdpServer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import com.codahale.metrics.Meter;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.jsganglia.server.Gmetad;
import com.jsganglia.server.conf.ConfigureLoad;
import com.jsganglia.server.ganglia.Gmessage;
import com.jsganglia.server.ganglia.GmetadataMessage;
import com.jsganglia.server.ganglia.GmetricMessage;

@Service
public class MessageProc {

	private final static Logger logger = LoggerFactory.getLogger(MessageProc.class);
	
	@Autowired
	ConfigureLoad configureLoad;
	
	private boolean isRunning = false;
	private Thread thread = null;

	public static BlockingQueue<String> publishDataQueue = new LinkedBlockingQueue(500000);

	public static ConcurrentHashMap<String, String> groupMap = new ConcurrentHashMap<>();

	private static char[] token = "".toCharArray();

	

	
	static Pattern  includeReg=null;

	public static Set<String> excludeMessage=new HashSet<>();
	
	private static Set<String> orgInclude=new HashSet<>();
	
	public void start() {
		if (isRunning()) {
			stop();
		}

	
		
		
		initGroup();
		
		setIncludeRule(configureLoad.getIncludeRule());

		isRunning = true;

		
		
		thread = new Thread(task);
		thread.start();

	}

	static public void setIncludeRule(Set<String> rules ) {
    	StringBuilder fileAll=new StringBuilder();
    	rules.addAll(orgInclude);
    	
    	for(String one:rules) {
    		fileAll.append(one);
    		fileAll.append("|");
    	}
    	fileAll.deleteCharAt(fileAll.length()-1);
    	String ruleStr=fileAll.toString();
    	Pattern reg=Pattern.compile(ruleStr);
    	
    	includeReg=reg;
    	
	}
	
	public void stop() {
		isRunning = false;
		thread.interrupt();
	}

	static public void processMessage(Gmessage data) {

		if (data instanceof GmetadataMessage) {
			GmetadataMessage obj = (GmetadataMessage) data;
			//logger.info(obj.toString());
			String merticName = obj.getMetricName();
			String group = obj.getGroup();
		//	logger.info("get group:"+merticName+":"+group);
			if (group != null) {
				groupMap.put(merticName, group);
			}

		} else if (data instanceof GmetricMessage) {
			GmetricMessage obj = (GmetricMessage) data;
			//logger.info(obj.getMetricName());
			//logger.info(obj.toString());
			String merticName = obj.getMetricName();
			if (merticName.equals("boottime") || merticName.equals("machine_type") || merticName.equals("os_name")
					|| merticName.equals("os_release") || merticName.equals("cpu_num") || merticName.equals("cpu_speed")
					|| merticName.equals("location")|| merticName.equals("gexec")||merticName.equals("heartbeat")) {
				return;
			}
			
			if(includeReg != null) {		
				if(! includeReg.matcher(merticName).matches()) {
					String record = String.format("ignore,hostName=%s %s=%s", obj.getHostName(), obj.getMetricName(),
							String.valueOf(obj.getValue()));
					logger.info(record);
					
					excludeMessage.add(merticName);
					return;
				}
			}
			
			String group = groupMap.get(merticName);
			if (group != null) {
				//// write into
				String record = String.format("%s,hostName=%s %s=%s", group, obj.getHostName(), obj.getMetricName(),
						String.valueOf(obj.getValue()));
			    logger.info(record);

				publishDataQueue.offer(record);

			} else {
				logger.info("no group:" + obj.toString());
			}

		}
	}

	private Runnable task = new Runnable() {
		public void run() {

			InfluxDBClient influxDBClient = InfluxDBClientFactory.create(configureLoad.getIfluxdburl(), token, configureLoad.getIfluxdborg(), configureLoad.getIfluxdbbucket());
			WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

			List<String> writeBuff = new LinkedList<String>();

			Meter write_meter = Gmetad.metrics.meter("influxdb.write");
			
			while (isRunning()) {
				try {

					String data = (String) MessageProc.publishDataQueue.poll(500, TimeUnit.MILLISECONDS);
					if (data == null ) {
						if(writeBuff.size()>0) {
							writeApi.writeRecords(WritePrecision.S, writeBuff);
							write_meter.mark(writeBuff.size());
							writeBuff.clear();
						}
					} else {
						writeBuff.add(data);
						if (writeBuff.size() > 300) {							
							writeApi.writeRecords(WritePrecision.S, writeBuff);
							write_meter.mark(writeBuff.size());
							writeBuff.clear();
						}
					}

				} catch (Exception ex) {
					logger.error("Send Data to MQ Error:", ex);
				}
			}
		}
	};

	public boolean isRunning() {
		return isRunning;
	}

	public boolean isAutoStartup() {
		return true;
	}

	public void stop(Runnable callback) {
		callback.run();
		isRunning = false;
		thread.interrupt();
	}

	void initGroup() {
		groupMap.put("heartbeat", "core");
		groupMap.put("gexec", "core");

		// memory
		groupMap.put("mem_free", "memory");
		groupMap.put("mem_shared", "memory");
		groupMap.put("mem_buffers", "memory");
		groupMap.put("mem_cached", "memory");
		groupMap.put("mem_total", "memory");
		groupMap.put("swap_free", "memory");
		groupMap.put("swap_total", "memory");

		// load
		groupMap.put("load_one", "load");
		groupMap.put("load_five", "load");
		groupMap.put("load_fifteen", "load");
		groupMap.put("proc_run", "process");
		groupMap.put("proc_total", "process");

		// CPU
		groupMap.put("cpu_user", "cpu");
		groupMap.put("cpu_system", "cpu");
		groupMap.put("cpu_idle", "cpu");
		groupMap.put("cpu_nice", "cpu");
		groupMap.put("cpu_aidle", "cpu");
		groupMap.put("cpu_wio", "cpu");
		groupMap.put("cpu_intr", "cpu");
		groupMap.put("cpu_sintr", "cpu");
		groupMap.put("cpu_steal", "cpu");


		// Network
		groupMap.put("bytes_in", "network");
		groupMap.put("bytes_out", "network");
		groupMap.put("pkts_in", "network");
		groupMap.put("pkts_out", "network");
		groupMap.put("network_rx", "network");
		groupMap.put("network_tx", "network");
		groupMap.put("tcp_RtoAlgorithm", "network");
		groupMap.put("tcp_RtoMin", "network");
		groupMap.put("tcp_RtoMax", "network");
		groupMap.put("tcp_MaxConn", "network");
		groupMap.put("tcp_ActiveOpens", "network");
		groupMap.put("tcp_PassiveOpens", "network");
		groupMap.put("tcp_AttemptFails", "network");
		groupMap.put("tcp_EstabResets", "network");
		groupMap.put("tcp_CurrEstab", "network");
		groupMap.put("tcp_InSegs", "network");
		groupMap.put("tcp_OutSegs", "network");
		groupMap.put("tcp_RetransSegs", "network");
		groupMap.put("tcp_InErrs", "network");
		groupMap.put("tcp_OutRsts", "network");
		
		groupMap.put("tcps_estab", "network");
		groupMap.put("tcps_closed", "network");
		groupMap.put("tcps_orphaned", "network");
		groupMap.put("tcps_synrecv", "network");
		groupMap.put("tcps_timewait", "network");
		

		groupMap.put("eth_errs", "network");
		groupMap.put("eth_drop", "network");
		
		
		groupMap.put("udp_InDatagrams", "network");
		groupMap.put("udp_NoPorts", "network");
		groupMap.put("udp_InErrors", "network");
		groupMap.put("udp_OutDatagrams", "network");
		groupMap.put("udp_RcvbufErrors", "network");
		groupMap.put("udp_SndbufErrors", "network");
		         
		// Disk
		groupMap.put("disk_total", "disk");
		groupMap.put("disk_free", "disk");
		groupMap.put("part_max_used", "disk");

		// Metadata
		groupMap.put("boottime", "system");
		groupMap.put("machine_type", "system");

		groupMap.put("os_name", "system");
		groupMap.put("os_release", "system");
		groupMap.put("cpu_num", "system");
		groupMap.put("cpu_speed", "system");
		groupMap.put("location", "system");
		

		for(Entry<String, String> enty:groupMap.entrySet()) {
			orgInclude.add(enty.getKey().trim());
		}

	}

}
