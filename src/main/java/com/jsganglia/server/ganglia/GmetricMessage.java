package com.jsganglia.server.ganglia;



public class GmetricMessage extends Gmessage{

	private final String format;
	private final Object value;

	/**
	 * Constructor from data.
	 * 
	 * @param recordType
	 * @param hostName
	 * @param metricName
	 * @param spoof
	 * @param format
	 * @param value
	 */
	public GmetricMessage(final MessageType recordType,
			final String hostName, final String metricName, final boolean spoof,
			final String format, final Object value) {

		super(recordType, hostName, metricName, spoof);
		
		switch (recordType) {
		case DOUBLE:
		case FLOAT:
		case INT32:
		case INT16:
		case STRING:
		case UINT32:
		case UINT16:
			break;
		default:
			throw new IllegalArgumentException();
		}

		if (format == null)
			throw new IllegalArgumentException();
		
		if (value == null)
			throw new IllegalArgumentException();
		
		this.format = format;
		
		this.value = value;

	}

	@Override
	public boolean isMetricValue() {
		return true;
	}

	@Override
	public boolean isMetricRequest() {
		return false;
	}

	@Override
	public boolean isMetricMetadata() {
		return false;
	}

	@Override
	public String getFormat() {
		return format;
	}

	@Override
	public boolean isNumeric() {
		switch (getRecordType()) {
		case STRING:
			return false;
		default:
			return true;
		}
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public String getStringValue() {
		if (value instanceof String) {
			return (String) value;
		}
		return "" + value;
	}

	@Override
	public Number getNumericValue() {
		if (value instanceof Number) {
			return (Number) value;
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return "GmetricMessage [format=" + format + ", value=" + value + ", recordType=" + recordType + ", hostName="
				+ hostName + ", metricName=" + metricName + ", spoof=" + spoof + "]";
	}


	
	
}
