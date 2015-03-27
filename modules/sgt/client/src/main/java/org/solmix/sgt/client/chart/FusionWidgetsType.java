package org.solmix.sgt.client.chart;

public enum FusionWidgetsType {

	AngularGauge("AngularGauge") ,

	Bulb("Bulb") ,

	Cylinder("Cylinder") ,
	DrawingPad("DrawingPad") ,
	Funnel("Funnel") ,
	Gantt("Gantt") ,
	HBullet("HBullet") ,
	HLED("HLED") ,
	HLinearGauge("HLinearGauge") ,
	Pyramid("Pyramid") ,
	RealTimeArea("RealTimeArea") ,
	RealTimeColumn("RealTimeColumn") ,
	RealTimeLine("RealTimeLine") ,
	RealTimeLineDY("RealTimeLineDY") ,
	RealTimeStackedArea("RealTimeStackedArea") ,
	RealTimeStackedColumn("RealTimeStackedColumn") ,
	SparkColumn("SparkColumn") ,
	SparkLine("SparkLine") ,
	SparkWinLoss("SparkWinLoss") ,
	Thermometer("Thermometer") ,
	VBullet("VBullet") ,
	VLED("VLED");

    private String value;

    FusionWidgetsType(String value)
    {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
