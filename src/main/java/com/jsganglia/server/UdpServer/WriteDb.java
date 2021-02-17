package com.jsganglia.server.UdpServer;

import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.exceptions.InfluxException;

public class WriteDb {

    private static char[] token = "".toCharArray();
    private static String org = "my-org";
    private static String bucket = "test_db";
////默认10万次要一分钟,可以明显看到还是耗在iowait, 即使不在vm下运行也是这个问题， 7.8 比6运行速率高, 调节了
 //// 使用批量后效果好很多，centos7运行也快一些，表明和xfs有关
    public static void main(final String[] args) {
        System.out.println("start"+new Date());

        InfluxDBClient influxDBClient = InfluxDBClientFactory.create("http://localhost:8086", token, org, bucket);


        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();


        try {
            //
            // Write by LineProtocol
            //
            String record = "temperature,location=north value=60.0";

            writeApi.writeRecord(WritePrecision.NS, record);

            //
            // Write by Data Point
            //
            System.out.println("start:"+new Date());
            for(int i=0;i<1000;i++) {
            	List<Point>bathList=new LinkedList<Point>();
                System.out.println("build1:"+new Date());
            	for(int j=0;j<100;j++) {
            Point point = Point.measurement("temperature")
                    .addTag("location", "west")
                    .addField("val", i)
                    .time(Instant.now().toEpochMilli(), WritePrecision.MS);
                  bathList.add(point);
            	}
                System.out.println("build2:"+new Date());

            writeApi.writePoints(bathList);
            }
            System.out.println("end:"+new Date());



        } catch (InfluxException ie) {
            System.out.println("InfluxException: " + ie);
        }

        influxDBClient.close();
        System.out.println("finish");
    }


}
