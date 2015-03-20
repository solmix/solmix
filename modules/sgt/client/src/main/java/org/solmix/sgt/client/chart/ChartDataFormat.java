package org.solmix.sgt.client.chart;

public enum ChartDataFormat {

	XML("xml"), JSON("json");
	private String value;

	ChartDataFormat(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
