package com.jsganglia.server.conf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import com.jsganglia.server.Gmetad;

@Configuration
public class ConfigureLoad {

	private final static Logger logger = LoggerFactory.getLogger(ConfigureLoad.class);

	@Value("${conf.ganglia}")
	private Integer gangliaPort;

	@Value("${conf.filterFile}")
	private String filterFile;

	@Value("${conf.ifluxdb.url}")
	private String ifluxdburl;

	@Value("${conf.ifluxdb.org}")
	private String ifluxdborg;

	@Value("${conf.ifluxdb.bucket}")
	private String ifluxdbbucket;

	public Integer getGangliaPort() {
		return gangliaPort;
	}

	public void setGangliaPort(Integer gangliaPort) {
		this.gangliaPort = gangliaPort;
	}

	public String getFilterFile() {
		return filterFile;
	}

	public void setFilterFile(String filterFile) {
		this.filterFile = filterFile;
	}

	public String getIfluxdburl() {
		return ifluxdburl;
	}

	public void setIfluxdburl(String ifluxdburl) {
		this.ifluxdburl = ifluxdburl;
	}

	public String getIfluxdborg() {
		return ifluxdborg;
	}

	public void setIfluxdborg(String ifluxdborg) {
		this.ifluxdborg = ifluxdborg;
	}

	public String getIfluxdbbucket() {
		return ifluxdbbucket;
	}

	public void setIfluxdbbucket(String ifluxdbbucket) {
		this.ifluxdbbucket = ifluxdbbucket;
	}

	public Set<String> getIncludeRule() {
		Set<String> rules = new HashSet<>();

		try {
			File logoFile = ResourceUtils.getFile(filterFile);
			BufferedReader reader = new BufferedReader(new FileReader(logoFile));
			String tempString = null;

			while ((tempString = reader.readLine()) != null) {
				rules.add(tempString.trim());
			}
			reader.close();

		} catch (Exception e) {
			logger.info("read conf error:", e);
		}
		return rules;
	}

	public void WriteConf(Set<String> rules) {

		try {
			File logoFile = ResourceUtils.getFile(filterFile);
			BufferedWriter out = new BufferedWriter(new FileWriter(logoFile));
			for (String one : rules) {
				out.write(one);
				out.newLine();
			}
			out.close();
			logger.info("write conf ok:");
		} catch (Exception e) {
			logger.info("write conf error:", e);
		}

		return;

	}

}
