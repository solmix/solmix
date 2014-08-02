
package org.solmix.sgt.client.chart;

public enum FusionChartType
{

    Area2D("Area2D") ,

    Bar2D("Bar2D") ,

    Bubble("Bubble") ,
    Column2D("Column2D") ,
    Column3D("Column3D") ,
    Doughnut2D("Doughnut2D") ,
    Doughnut3D("Doughnut3D") ,
    Line("Line") ,
    Marimekko("Marimekko") ,
    MSArea("MSArea") ,
    MSBar2D("MSBar2D") ,
    MSBar3D("MSBar3D") ,
    MSColumn2D("MSColumn2D") ,
    MSColumn3D("MSColumn3D") ,
    MSColumn3DLineDY("MSColumn3DLineDY") ,
    MSColumnLine3D("MSColumnLine3D") ,
    MSCombi2D("MSCombi2D") ,
    MSCombi3D("MSCombi3D") ,
    MSCombiDY2D("MSCombiDY2D") ,
    MSLine("MSLine") ,
    MSStackedColumn2D("MSStackedColumn2D") ,
    MSStackedColumn2DLineDY("MSStackedColumn2DLineDY") ,
    Pareto2D("Pareto2D") ,
    Pareto3D("Pareto3D") ,
    Pie2D("Pie2D") ,
    Pie3D("Pie3D") ,
    Scatter("Scatter") ,
    ScrollArea2D("ScrollArea2D") ,
    ScrollColumn2D("ScrollColumn2D") ,
    ScrollCombi2D("ScrollCombi2D") ,
    ScrollCombiDY2D("ScrollCombiDY2D") ,
    ScrollLine2D("ScrollLine2D") ,
    ScrollStackedColumn2D("ScrollStackedColumn2D") ,
    SSGrid("SSGrid") ,
    StackedArea2D("StackedArea2D") ,
    StackedBar2D("StackedBar2D") ,
    StackedBar3D("StackedBar3D") ,
    StackedColumn2D("StackedColumn2D") ,
    StackedColumn2DLine("StackedColumn2DLine") ,
    StackedColumn3D("StackedColumn3D") ,
    StackedColumn3DLine("StackedColumn3DLine") ,
    StackedColumn3DLineDY("StackedColumn3DLineDY") ,
    ZoomLine("ZoomLine");

    private String value;

    FusionChartType(String value)
    {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
