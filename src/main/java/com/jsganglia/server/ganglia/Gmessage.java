package com.jsganglia.server.ganglia;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public abstract  class Gmessage  {
	

	protected final MessageType recordType;
	protected final String hostName;
	protected final String metricName;
	protected final boolean spoof;

	public Gmessage(final MessageType recordType,
			final String hostName, final String metricName, final boolean spoof) {
		
		if (recordType == null)
			throw new IllegalArgumentException();
		
		if (hostName == null)
			throw new IllegalArgumentException();
		
		if (metricName == null)
			throw new IllegalArgumentException();
		
		this.recordType = recordType;
		
		this.hostName = hostName;
		
		this.metricName = metricName;
		
		this.spoof = spoof;
		
	}
	
	public MessageType getRecordType() {
		return recordType;
	}

	public String getHostName() {
		return hostName;
	}

	public String getMetricName() {
		return metricName;
	}

	public boolean isSpoof() {
		return spoof;
	}

	abstract public boolean isMetricValue();

	abstract public boolean isMetricRequest();

	abstract public boolean isMetricMetadata();

	abstract public String getFormat() ;

	abstract public boolean isNumeric();

	abstract public Object getValue();

	abstract public String getStringValue();

	abstract public Number getNumericValue();



	  @Override 
      public String toString() { 
              return ReflectionToStringBuilder.toString(this); 
      }
	  
		@Override
		public int hashCode() {
			     return HashCodeBuilder.reflectionHashCode(this);
		}
		
		
		@Override
		public boolean equals(Object obj) {
			return  EqualsBuilder.reflectionEquals(this, obj);
		}
	   
}
