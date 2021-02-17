package com.jsganglia.server.UdpServer;

import java.io.IOException;

import info.ganglia.gmetric4j.gmetric.GMetric;
import info.ganglia.gmetric4j.gmetric.GMetricSlope;
import info.ganglia.gmetric4j.gmetric.GMetricType;
import info.ganglia.gmetric4j.gmetric.GMetric.UDPAddressingMode;

public class Tganglia {

    public static void main(String[] args) throws IOException {
        GMetric gm = null;
        try {
            gm = new GMetric("127.0.0.1", 8649, UDPAddressingMode.MULTICAST, 1);
            // gm.announce("heartbeat", "0", GMetricType.UINT32, "",
            // GMetricSlope.ZERO, 0, 0, "core");
            gm.announce("BOILINGPOINT", "100", GMetricType.STRING, "CELSIUS", GMetricSlope.BOTH, 0, 0, "TESTGROUP");
            gm.announce("INTTEST", (int) Integer.MAX_VALUE, "TESTGROUP");
            gm.announce("LONGTEST", (long) Long.MAX_VALUE, "TESTGROUP");
            gm.announce("FLOATTEST", (float) Float.MAX_VALUE, "TESTGROUP");
            gm.announce("DOUBLETEST", (double) Double.MAX_VALUE, "TESTGROUP");
           // gm.
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            gm.close();
        }
    }

}
