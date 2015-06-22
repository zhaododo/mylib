package com.android.dream.chart;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.View;

/**
 * @author Wang Cheng
 *
 * @date 2015-1-27下午5:42:36
 */
public class MyTestChart extends AbstractDemoChart {

	 /**
	   * Returns the chart name.
	   * 
	   * @return the chart name
	   */
	  public String getName() {
	    return "平均温度";
	  }

	  /**
	   * Returns the chart description.
	   * 
	   * @return the chart description
	   */
	  public String getDesc() {
	    return "中国地方天气统计";
	  }

	  /**
	   * Executes the chart demo.
	   * 
	   * @param context the context
	   * @return the built intent
	   */
	  public View getview(Context context) {
	    String[] titles = new String[] { "焦炭"};
	    List<double[]> x = new ArrayList<double[]>();
	    for (int i = 0; i < titles.length; i++) {
	      x.add(new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31 });
	    }
	    List<double[]> values = new ArrayList<double[]>();
	    values.add(new double[] { 4004.090, 4023.350, 4004.740, 4003.250, 3995.290, 4020.190, 4023.740, 4022.980, 4018.920, 3945.740, 3986.730,
	    		3894.720,1340.060,3621.990,3947.120,3793.050,4002.620,4077.760,4001.930,4118.460,4113.960,4113.960,4076.540,4064.270,4091.760,0,0,0,0,0,0});
//	    values.add(new double[] { 10, 10, 12, 15, 20, 24, 26, 26, 23, 18, 14, 11 });
//	    values.add(new double[] { 5, 5.3, 8, 12, 17, 22, 24.2, 24, 19, 15, 9, 6 });
//	    values.add(new double[] { 9, 10, 11, 15, 19, 23, 26, 25, 22, 18, 13, 10 });
	    int[] colors = new int[] { Color.BLUE};//每个序列的颜色设置
	    PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE};//每个序列中点 的形状设置
	    XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
	    int length = renderer.getSeriesRendererCount();
	    for (int i = 0; i < length; i++) {
	      ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
	    }
	    double maxvalue = 4500;
	    double minvalue = 0;
	    setChartSettings(renderer, "燃料供应厂焦炭", "月", "吨", 0, 15, 0, 4500,
	        Color.LTGRAY, Color.LTGRAY);
	    renderer.setXLabels(15);//设置 x 轴显示 12 个点,根据 setChartSettings 的最大值和最小值自动计算点的间隔
	    renderer.setYLabels(10);//设置 y 轴显示 10 个点,根据 setChartSettings 的最大值和最小值自动计算点的间隔
	     
	    renderer.setShowGrid(true);//是否显示网格
	    renderer.setXLabelsAlign(Align.RIGHT);
	    renderer.setYLabelsAlign(Align.RIGHT);//刻度线与刻度标注之 间的相对位置关系
	    renderer.setZoomButtonsVisible(true);// 是否显示放大缩小按钮
	    renderer.setPanLimits(new double[] { 0, 31, 0, 4500 });//设置拖动时 X 轴 Y 轴允许的最大值最小值
	    renderer.setZoomLimits(new double[] { 0, 31, 0, 4500 });//设置放大缩小时 X 轴 Y 轴允许的最大最小值
	    renderer.setChartTitleTextSize(50);// 设置图表标题文本大小
	    renderer.setDisplayChartValues(true);//是否显示图标值
	    renderer.setChartValuesTextSize(20);//设置值的大小
	    renderer.setMargins(new int[] {80,40,60,0});
	    renderer.setAxisTitleTextSize(15); // 设置坐标轴标题文本大小
	    renderer.setLabelsTextSize(15); // 设置轴标签文本大小
	    renderer.setLegendTextSize(25); // 设置图例文本大小
	    // 图表与屏幕四边的间距颜色  
//	    renderer.setMarginsColor(Color.argb(0, 0xF3, 0xF3, 0xF3));  
	 // 图表部分的背景颜色  
//	    renderer.setBackgroundColor(Color.BLACK);
	//   x、y轴上刻度颜色  
	//   renderer.setXLabelsColor(Color.BLACK);  
	//   renderer.setYLabelsColor(0, Color.BLACK);
	    renderer.setBackgroundColor(Color.parseColor("#D9D9D9")); // 设置背景色透明
	    renderer.setApplyBackgroundColor(true); // 使背景色生效
	    XYMultipleSeriesDataset dataset = buildDataset(titles, x, values);
	    XYSeries series = dataset.getSeriesAt(0);
//	    series.addAnnotation("Vacation", 6, 30);
//	    Intent intent = ChartFactory.getLineChartIntent(context, dataset, renderer,
//	        "燃料供应厂焦炭");
	    View viewrslt = ChartFactory.getLineChartView(context, dataset, renderer);
	    return viewrslt;
	  }

	/* (non-Javadoc)
	 * @see com.android.dream.chart.IDemoChart#execute(android.content.Context)
	 */
	@Override
	public Intent execute(Context context) {
		// TODO Auto-generated method stub
		return null;
	}

}
