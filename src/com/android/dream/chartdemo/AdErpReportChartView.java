package com.android.dream.chartdemo;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.android.dream.app.bean.AdErpReportChartItemVo;
import com.android.dream.app.bean.AdErpReportChartRowVo;
import com.android.dream.app.bean.AdErpReportChartVo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.View;

/**
 * @author Wang Cheng
 *
 * @date 2015-1-28下午3:50:52
 */
public class AdErpReportChartView {
	double maxvalue = 0;
	double minvalue = 0;
	double yminvalue = 0;
	double ymaxvalue = 0;
	String reporttitle ="";
	
	public View getview(Context context,AdErpReportChartVo adErpReportChartVo,String reportName) {
		List<AdErpReportChartItemVo>  adErpReportChartItemVolis;
		List<AdErpReportChartRowVo> AdErpReportChartRowVolis = adErpReportChartVo.getRows();
		String[] titles = new String[AdErpReportChartRowVolis.size()];
		List<double[]> xlisvalues = new ArrayList<double[]>();  //定义X值
		List<double[]> ylisvalues = new ArrayList<double[]>(); //定义Y值
		reporttitle = adErpReportChartVo.getChartTitle();
//		设置有不同类型曲线
		for(int i =0;i<AdErpReportChartRowVolis.size();i++){
			titles[i]=AdErpReportChartRowVolis.get(i).getTitle();
			adErpReportChartItemVolis = AdErpReportChartRowVolis.get(i).getItems();
			double[] xvalues = new double[adErpReportChartItemVolis.size()];
			double[] yvalues = new double[adErpReportChartItemVolis.size()];
			for(int j = 0;j<adErpReportChartItemVolis.size();j++){
				xvalues[j] = Double.parseDouble(adErpReportChartItemVolis.get(j).getxValue());
//				xvalues[j] = j+1;
				yvalues[j] = Double.parseDouble(adErpReportChartItemVolis.get(j).getyValue());
			}
			xlisvalues.add(xvalues);
			ylisvalues.add(yvalues);
		}
		int[] colors = new int[] { Color.BLUE };// 每个序列的颜色设置
		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE };// 每个序列中点
																		// 的形状设置
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		for (int i = 0; i < colors.length; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors[i]);
			r.setPointStyle(styles[i]);
			renderer.addSeriesRenderer(r);
		}

		int length = renderer.getSeriesRendererCount();
		for (int i = 0; i < length; i++) {
			((XYSeriesRenderer) renderer.getSeriesRendererAt(i))
					.setFillPoints(true);
			renderer.getSeriesRendererAt(i).setDisplayChartValuesDistance(40);
		}
		try{
			yminvalue = Double.parseDouble(adErpReportChartVo.getyMin());
		}catch(Exception ex){
			yminvalue = 0;
		}
		try{
			ymaxvalue = Double.parseDouble(adErpReportChartVo.getyMax());
		}catch(Exception ex){
			ymaxvalue = 10;
		}
		
		setChartSettings(renderer, reporttitle, adErpReportChartVo.getxTitle(), adErpReportChartVo.getyTitle(), 
				0, 15, yminvalue,ymaxvalue,Color.LTGRAY, Color.LTGRAY);
		renderer.setLegendHeight(50); //控制图例的高度
		renderer.setXLabels(15);// 设置 x 轴显示 12 个点,根据 setChartSettings
								// 的最大值和最小值自动计算点的间隔
		renderer.setYLabels(10);// 设置 y 轴显示 10 个点,根据 setChartSettings
								// 的最大值和最小值自动计算点的间隔

		renderer.setShowGrid(true);// 是否显示网格
		renderer.setGridColor(Color.YELLOW);
		renderer.setXLabelsAlign(Align.RIGHT);
		renderer.setYLabelsAlign(Align.RIGHT);// 刻度线与刻度标注之 间的相对位置关系
		renderer.setZoomButtonsVisible(false);// 是否显示放大缩小按钮
		
		renderer.setPanLimits(new double[] { 0, 35, yminvalue,ymaxvalue });// 设置拖动时 X 轴 Y
																// 轴允许的最大值最小值
		renderer.setZoomLimits(new double[] { 0, 35, yminvalue, ymaxvalue });// 设置放大缩小时 X 轴 Y
																// 轴允许的最大最小值
		renderer.setChartTitleTextSize(50);// 设置图表标题文本大小
		renderer.setDisplayChartValues(true);// 是否显示图标值
		renderer.setChartValuesTextSize(20);// 设置值的大小
		renderer.setMargins(new int[] { 80, 40, 30, 0 });
		renderer.setAxisTitleTextSize(15); // 设置坐标轴标题文本大小
		renderer.setLabelsTextSize(15); // 设置轴标签文本大小
		renderer.setLegendTextSize(25); // 设置图例文本大小
		// 图表与屏幕四边的间距颜色
		// renderer.setMarginsColor(Color.argb(0, 0xF3, 0xF3, 0xF3));
		// x、y轴上刻度颜色
		// renderer.setXLabelsColor(Color.BLACK);
		// renderer.setYLabelsColor(0, Color.BLACK);
		// 图表部分的背景颜色
		renderer.setBackgroundColor(Color.parseColor("#D9D9D9")); // 设置背景色透明
		renderer.setApplyBackgroundColor(true); // 使背景色生效
		XYMultipleSeriesDataset dataset = buildDataset(titles, xlisvalues, ylisvalues);
//		XYSeries series = dataset.getSeriesAt(0);
		// series.addAnnotation("Vacation", 6, 30);
		// Intent intent = ChartFactory.getLineChartIntent(context, dataset,
		// renderer,
		// "燃料供应厂焦炭");
		View viewrslt = ChartFactory.getLineChartView(context, dataset,
				renderer);
		return viewrslt;
	}

	protected void setChartSettings(XYMultipleSeriesRenderer renderer,
			String title, String xTitle, String yTitle, double xMin,
			double xMax, double yMin, double yMax, int axesColor,
			int labelsColor) {
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
	}

//	设定值
	protected XYMultipleSeriesDataset buildDataset(String[] titles,
			List<double[]> xValues, List<double[]> yValues) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		addXYSeries(dataset, titles, xValues, yValues, 0);
		return dataset;
	}

	public void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles,
			List<double[]> xValues, List<double[]> yValues, int scale) {
		int length = titles.length;
		for (int i = 0; i < length; i++) {
			XYSeries series = new XYSeries(titles[i], scale);
			double[] xV = xValues.get(i);
			double[] yV = yValues.get(i);
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(xV[k], yV[k]);
			}
			dataset.addSeries(series);
		}
	}
}
