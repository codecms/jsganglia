package com.jsganglia.server.ganglia;

import java.util.HashMap;
import java.util.Map;


public enum MessageType {
	
	/**
	 * Ganglia metric metadata declaration record.
	 */
	METADATA(128, false, null/* n/a */, null/* n/a */, null/* n/a */), //

	/**
	 * Ganglia metric record with unsigned short value.
	 * <p>
	 * This data type is NOT automatically selected for any Java data values. It
	 * is only used if there is an explicit {@link IGangliaMetadataMessage}
	 * declaring the metric as having this datatype.
	 * {@link IGangliaMetricMessage} associated with this ganglia data type by
	 * an {@link IGangliaMetadataMessage} are internally modeled using a
	 * non-negative Java {@link Integer}. However, when they are sent out on the
	 * wire they are written out using the printf format string for an unsigned
	 * int16 value.
	 */
	UINT16(128 + 1, true, "uint16", "%hu", null/* n/a */), //

	/**
	 * Ganglia metric record with short value.
	 */
	INT16(128 + 2, true, "int16", "%hi", new Class[]{Short.class}), //

	/**
	 * Ganglia metric record with int32 value.
	 */
	INT32(128 + 3, true, "int32", "%i", new Class[]{Integer.class}), //

	/**
	 * Ganglia metric record with unsigned int32 value.
	 * <p>
	 * This data type is NOT automatically selected for any Java data values. It
	 * is only used if there is an explicit {@link IGangliaMetadataMessage}
	 * declaring the metric as having this datatype.
	 * {@link IGangliaMetricMessage} associated with this ganglia data type by
	 * an {@link IGangliaMetadataMessage} are internally modeled using a
	 * non-negative Java {@link Long}. However, when they are sent out on the
	 * wire they are written out using the printf format string for an unsigned
	 * int32 value.
	 */
	UINT32(128 + 4, true, "uint32", "%u", null/* n/a */), //

	/**
	 * Ganglia metric record with string value.
	 */
	STRING(128 + 5, true, "string", "%s", new Class[]{String.class}),//
	
	/**
	 * Ganglia metric record with float value.
	 */
	FLOAT(128 + 6, true, "float", "%f", new Class[]{Float.class}), //
	
	/**
	 * Ganglia metric record with double value.
	 */
	DOUBLE(128 + 7, true, "double", "%lf", new Class[] { Double.class,
			Long.class }), //
	
	/**
	 * Ganglia request record (requests a metadata record for the named metric).
	 */
	REQUEST(128 + 8, false, null/* n/a */, null/* n/a */, null/* n/a */);


	private MessageType(final int v, final boolean isMetric,
			final String gtype, final String format, final Class<?>[] javaClasses) {
		this.v = v;
		this.isMetric = isMetric;
		this.gtype = gtype;
		this.format = format;
		this.javaClasses = javaClasses;

	}

	private final int v;
	private final boolean isMetric;
	private final String gtype;
	private final String format;
	private final Class<?>[] javaClasses;
	
	public int value() {
		return v;
	}

	public boolean isMetric() {
		return isMetric;
	}


	public String getGType() {
		return gtype;
	}
	

	public String getFormat() {
		return format;
	}

	public boolean isNumeric() {

		switch (this) {
		case DOUBLE:
		case FLOAT:
		case INT16:
		case INT32:
		case UINT16:
		case UINT32:
			return true;
		case STRING:
			return false;
		case METADATA:
		case REQUEST:
			return false;
		default:
			throw new AssertionError();
		}
		
	}
	

	static public MessageType valueOf(final int v) {
		switch (v) {
		case 128:
			return METADATA;
		case 128 + 1:
			return UINT16;
		case 128 + 2:
			return INT16;
		case 128 + 3:
			return INT32;
		case 128 + 4:
			return UINT32;
		case 128 + 5:
			return STRING;
		case 128 + 6:
			return FLOAT;
		case 128 + 7:
			return DOUBLE;
		case 128 + 8:
			return REQUEST;
		default:
			throw new IllegalArgumentException("value=" + v);
		}
	}


	static public MessageType fromGType(final String metricType) {

		final MessageType e = gtype2Enum.get(metricType);

		if (e == null)
			throw new IllegalArgumentException("metricType=" + metricType);

		return e;

	}

	static public MessageType forJavaValue(final Object value) {

		if (value == null)
			throw new UnsupportedOperationException();

		final Class<?> cls = value.getClass();

		final MessageType e = javaClass2Enum.get(cls);

		if (e == null)
			throw new IllegalArgumentException();

		return e;

	}
	

	private static final Map<String, MessageType> gtype2Enum;


	private static final Map<Class<?>, MessageType> javaClass2Enum;

	static {

		gtype2Enum = new HashMap<String, MessageType>();

		javaClass2Enum = new HashMap<Class<?>, MessageType>();

		for (MessageType e : values()) {

			gtype2Enum.put(e.getGType(), e);

			if(e.javaClasses != null) {

				for(Class<?> cls : e.javaClasses) {

					javaClass2Enum.put(cls, e);
					
				}
				
			}

		}


	}

}
