package com.jsganglia.server.ganglia;

public interface Gattributes {


		/**
		 * The name of the optional attribute which is used to collect metrics into
		 * groups in the UI. The value of the attribute is the name of the metric
		 * group to which a metric belongs.
		 */
		String ATTR_GROUP = "GROUP";
		
		/**
		 * The name of the optional attribute whose value is a nice "title" for the
		 * metric.
		 */
		String ATTR_TITLE = "TITLE";
		
		/**
		 * The name of the optional attribute whose value is a description of the
		 * attribute.
		 * <p>
		 * Note: The maximum size of a packet for the ganglia protocol puts a
		 * realistic limit on how verbose a description can be.
		 */
		
		String ATTR_DESC = "DESC";
		
		/*
		 * The following are group names used by Ganglia.
		 */
		
		/**
		 * The name of the group for some "core" per-host metrics (boottime, etc).
		 */
		String GROUP_CORE = "core";
		
		/**
		 * The name of the group for per-host CPU metrics.
		 */
		String GROUP_CPU = "cpu";

		/**
		 * The name of the group for per-host memory metrics.
		 */
		String GROUP_MEMORY = "memory";

		/**
		 * The name of the group for per-host disk metrics.
		 */
		String GROUP_DISK = "disk";

		/**
		 * The name of the group for per-host network metrics.
		 */
		String GROUP_NETWORK = "network";

		/**
		 * The name of the group for per-host load metrics (load_one, etc).
		 */
		String GROUP_LOAD = "load";
		
		/**
		 * The name of the group for per-host process metrics (proc_run, proc_total).
		 */
		String GROUP_PROCESS = "process";
		
		/**
		 * The name of the group for per-host metadata metrics (os_name, etc).
		 */
		String GROUP_SYSTEM = "system";

	

}
