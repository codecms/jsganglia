package com.jsganglia.server.ganglia;

import java.util.Arrays;
import java.util.Map;



public class GmetadataMessage extends Gmessage{
	private final MessageType metricType;
	private final String metricName2;
	private final String units;
	private final Gslope slope;
	private final int tmax;
	private final int dmax;
	private final Map<String, String[]> extraValues;

	public GmetadataMessage(
			final String hostName, final String metricName,
			final boolean spoof, final MessageType metricType,
			final String metricName2, final String units,
			final Gslope slope, final int tmax, final int dmax,
			final Map<String, String[]> extraValues) {

		super(MessageType.METADATA, hostName, metricName,
				spoof);
		
		if (metricType == null)
			throw new IllegalArgumentException();
		
		if (metricName2 == null)
			throw new IllegalArgumentException();

		if (!metricName.equals(metricName2))
			throw new IllegalArgumentException();
		
		if (units == null)
			throw new IllegalArgumentException();
		
		if (slope == null)
			throw new IllegalArgumentException();
		
		if (extraValues == null)
			throw new IllegalArgumentException();

		this.metricType = metricType;
		this.metricName2 = metricName2;
		this.units = units;
		this.slope = slope;
		this.tmax = tmax;
		this.dmax = dmax;
		this.extraValues = extraValues;
	}

	@Override
	public boolean isMetricValue() {
		return false;
	}

	@Override
	public boolean isMetricRequest() {
		return false;
	}

	@Override
	public boolean isMetricMetadata() {
		return true;
	}


	public MessageType getMetricType() {
		return metricType;
	}


	public String getMetricName2() {
		return metricName2;
	}


	public String getUnits() {
		return units;
	}

	
	public Gslope getSlope() {
		return slope;
	}


	public int getTMax() {
		return tmax;
	}


	public int getDMax() {
		return dmax;
	}

	
	public Map<String, String[]> getExtraValues() {
		return extraValues;
	}
	
	public String getGroup() {
		String[]  groups=extraValues.get(Gattributes.ATTR_GROUP);
		if(groups != null && groups.length>0) {
			return groups[0];
		}
		return null;
		
	}
	
	public String[] getGroups() {
		return extraValues.get(Gattributes.ATTR_GROUP);
	}
	
	public String getTitle() {
		return getFirstValue(Gattributes.ATTR_TITLE);
	}
	
	public String getDescription() {
		return getFirstValue(Gattributes.ATTR_DESC);
	}
	
	public String getFirstValue(final String key) {
		final String[] a = extraValues.get(key);
		if (a == null || a.length == 0)
			return null;
		return a[0];
	}





	public Object translateValue(final Object value) {

		return value;
		
	}

	public boolean isChanged(final Object oldValue, final Object newValue) {

		final boolean changed = !oldValue.equals(newValue);

		return changed;

	}

	@Override
	public String getFormat() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isNumeric() {

		return false;
	}

	@Override
	public Object getValue() {

		return null;
	}


	public String getStringValue() {

		return null;
	}


	public Number getNumericValue() {

		return null;
	}

	public String mString(Map<String, String[]> extraValues) {
		final StringBuilder sb = new StringBuilder(128);
		sb.append(", extraValues=");
		{
			sb.append("{");
			if (!extraValues.isEmpty()) {
				boolean first = true;
				for (Map.Entry<String, String[]> e : extraValues.entrySet()) {
					if (!first)
						sb.append(",");
					first = false;
					sb.append(e.getKey());
					sb.append("=");
					final String[] v = e.getValue();
					if (v.length == 0) {
						// Nothing given. Should not happen.
						sb.append("[]");
					} else if (v.length == 1) {
						sb.append('\"');
						sb.append(v[0]);
						sb.append('\"');
					} else {
						sb.append("[");
						for (int i = 0; i < v.length; i++) {
							if (i > 1)
								sb.append(",");
							sb.append('\"');
							sb.append(v[i]);
							sb.append('\"');
						}
						sb.append("]");
					}
				}
			} // if(!extraValues.isEmpty()) ...
			sb.append("}");
		}
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return "GmetadataMessage [metricType=" + metricType + ", metricName2=" + metricName2 + ", units=" + units
				+ ", slope=" + slope + ", tmax=" + tmax + ", dmax=" + dmax + ", extraValues=" + mString(extraValues )+ "]";
	}
	
	
	
	
}
