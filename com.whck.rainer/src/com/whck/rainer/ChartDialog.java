package com.whck.rainer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import com.whck.proto.model.RainerData;
import com.whck.rainer.db.RainerDataDAO;
import com.whck.rainer.excel.CVS;
import com.whck.rainer.util.XmlUtil;

public class ChartDialog extends Dialog {

	private static Map<String, String> param_rainer_map = new HashMap<>();
	private static Map<String, String> param_time_map = new HashMap<>();

	static {
		param_rainer_map.put("����", "all");
		param_rainer_map.put("��ǿ", "rainintn");
		param_rainer_map.put("ѹǿ", "pressure");
		param_rainer_map.put("����", "rainfall");
		param_time_map.put("��", "YEAR");
		param_time_map.put("��", "MONTH");
		param_time_map.put("��", "DAY");
		param_time_map.put("ʱ", "HOUR");
		param_time_map.put("��", "MINUTE");
	}
	private List<String> columnNames = new ArrayList<>();
	private List<String> properties = new ArrayList<>();
	private static double maxVal = 0;
	private static double minVal = 0;
	private List<String> deviceIds;
	private org.eclipse.swt.graphics.Font font;
	private Label deviceIdLbl;
	private Combo deviceIdComb;
	private Label paramLbl;
	private Combo paramComb;
	private Label fromDateLbl;
	private Label toDateLbl;
	private CDateTime fromDate;
	private CDateTime toDate;
	private Label cllctDurLbl;
	private Combo cllctDurComb;
	private Button srchBtn;
	private ChartComposite chrtFrame;
	private JFreeChart jFreeChart;
	private TableViewer tableViewer;
	private Button expPlotBtn;
	private Button expXlsBtn;
	private DateTickUnit dateTickUnit;
	private static List data = new ArrayList<>();

	// ��ȡ���ݶ��󼯺�
	protected List getData(String deviceName, String fromDate, String toDate) {
		List data = new ArrayList<>();
		RainerDataDAO dao = RainerDataDAO.getInstance();
		try {
			data = dao.findByCnd(deviceName, fromDate, toDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	// ��ð������ݵ�ʱ�伯��
	protected TimeSeriesCollection getAvgData(String deviceId, String durName, String paramName, CDateTime fromDate,
			CDateTime toDate) throws Exception {
		TimeSeriesCollection seriesCollection = new TimeSeriesCollection();
		TimeSeries ts = null;

		RainerDataDAO dao = new RainerDataDAO();
		List avgData = new ArrayList<RainerData>();
		avgData = dao.findAvg(Integer.valueOf(deviceId), param_time_map.get(durName), param_rainer_map.get(paramName),
				formatDt(fromDate), formatDt(toDate));

		if (paramName.equals("��ǿ") || paramName.equals("����")) {
			ts = new TimeSeries("��ǿ");
			for (int i = 0; i < avgData.size(); i++) {
				RainerData avg = (RainerData) avgData.get(i);
				System.out.println("Raindata:" + avg);
				float rainintn = avg.getRainfall();
				if (rainintn > maxVal)
					maxVal = rainintn;
				if (rainintn < minVal)
					minVal = rainintn;
				ts.add(new Minute(avg.getMinute(), avg.getHour(), avg.getDay(), avg.getMonth(), avg.getYear()),
						rainintn);
			}
			seriesCollection.addSeries(ts);
		}
		if (paramName.equals("����") || paramName.equals("����")) {
			ts = new TimeSeries("����");
			for (int i = 0; i < avgData.size(); i++) {
				RainerData avg = (RainerData) avgData.get(i);
				float rainfall = avg.getRainfall();
				if (rainfall > maxVal)
					maxVal = rainfall;
				if (rainfall < minVal)
					minVal = rainfall;
				ts.add(new Minute(avg.getMinute(), avg.getHour(), avg.getDay(), avg.getMonth(), avg.getYear()),
						rainfall);
			}
			seriesCollection.addSeries(ts);
		}
		if (paramName.equals("ѹǿ") || paramName.equals("����")) {
			ts = new TimeSeries("ѹǿ");
			System.out.println(avgData.size());
			for (int i = 0; i < avgData.size(); i++) {
				RainerData avg = (RainerData) avgData.get(i);
				float pressure = avg.getPressure();
				if (pressure > maxVal)
					maxVal = pressure;
				if (pressure < minVal)
					minVal = pressure;
				ts.add(new Minute(avg.getMinute(), avg.getHour(), avg.getDay(), avg.getMonth(), avg.getYear()),
						pressure);
			}
			seriesCollection.addSeries(ts);
		}

		return seriesCollection;
	}

	@Override
	public Control createDialogArea(Composite parent) {
		XmlUtil xmlUtil = null;
		try {
			xmlUtil = XmlUtil.getInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> zoneNames = xmlUtil.getZoneNames();
		deviceIds = xmlUtil.getDevIds(zoneNames.get(0));
		GridLayout layout = new GridLayout(4, true);

		parent.setLayout(layout);
		layout.verticalSpacing = 10;
		layout.marginBottom = 0;

		GridData gridData = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		// ��������ѡ��Combo
		deviceIdLbl = new Label(parent, SWT.NONE);
		deviceIdLbl.setLayoutData(gridData);
		deviceIdLbl.setText("����ѡ��");
		deviceIdLbl.setFont(font);
		deviceIdLbl.setAlignment(SWT.CENTER);

		deviceIdComb = new Combo(parent, SWT.BORDER);
		deviceIdComb.setLayoutData(gridData);
		deviceIdComb.setItems(deviceIds.toArray(new String[0]));
		deviceIdComb.select(0);
		String[] columnNms = { "   �豸��     ", "   �豸ID   ", "       �豸IP      ", "   ��ǿ(mm/h)   ", "   ѹǿ(Mpa)   ",
				"   ����(mm)   ", "    ����       ", "                  ����                " };
		Collections.addAll(columnNames, columnNms);
		String[] pros = { "name", "id", "ip", "rainintn", "pressure", "rainfall", "raintype", "rdate" };
		Collections.addAll(properties, pros);
		paramLbl = new Label(parent, SWT.NONE);
		paramLbl.setText("����ѡ��");
		paramLbl.setLayoutData(gridData);
		paramLbl.setFont(font);
		paramLbl.setAlignment(SWT.CENTER);
		paramComb = new Combo(parent, SWT.NONE);
		paramComb.setItems(param_rainer_map.keySet().toArray(new String[0]));
		paramComb.select(1);
		paramComb.setLayoutData(gridData);
		fromDateLbl = new Label(parent, SWT.NONE);
		fromDateLbl.setLayoutData(gridData);
		fromDateLbl.setText("��ʼʱ��");
		fromDateLbl.setFont(font);
		fromDateLbl.setAlignment(SWT.CENTER);
		fromDate = new CDateTime(parent, CDT.DATE_SHORT | CDT.TIME_SHORT | CDT.DROP_DOWN);
		fromDate.setPattern("yyyy-MM-dd HH:mm:ss");
		fromDate.setSelection(new Date());
		fromDate.setLayoutData(gridData);
		toDateLbl = new Label(parent, SWT.NONE);
		toDateLbl.setLayoutData(gridData);
		toDateLbl.setText("��ֹʱ��");
		toDateLbl.setFont(font);
		toDateLbl.setAlignment(SWT.CENTER);
		toDate = new CDateTime(parent, CDT.DATE_SHORT | CDT.TIME_SHORT | CDT.DROP_DOWN);
		toDate.setPattern("yyyy-MM-dd HH:mm:ss");
		toDate.setSelection(new Date());
		toDate.setLayoutData(gridData);

		cllctDurLbl = new Label(parent, SWT.NONE);
		cllctDurLbl.setText("�ɼ����");
		cllctDurLbl.setFont(font);
		cllctDurLbl.setAlignment(SWT.CENTER);
		cllctDurLbl.setLayoutData(gridData);
		cllctDurComb = new Combo(parent, SWT.BORDER);
		cllctDurComb.setItems(new String[] { "��", "��", "��", "ʱ", "��" });
		cllctDurComb.setLayoutData(gridData);
		cllctDurComb.select(0);
		gridData = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		srchBtn = new Button(parent, SWT.NONE);
		srchBtn.setText("ȷ��");
		srchBtn.setFont(font);
		srchBtn.setAlignment(SWT.CENTER);
		srchBtn.setLayoutData(gridData);
		srchBtn.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if (checkDate()) { // ������ںϷ���
					new LoadHandler().start();
				} else {
					MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_WARNING);
					box.setText("��ʾ");
					box.setMessage("���ڲ��Ϸ�");
					box.open();
					return;
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		try {
			jFreeChart = createChart(null, cllctDurComb.getText(), paramComb.getText());
			jFreeChart.setBackgroundPaint(Color.getHSBColor(184, 112, 112));
			chrtFrame = new ChartComposite(parent, SWT.NONE, jFreeChart);
		} catch (Exception e1) {
			System.out.println("����ͼ��ʧ��");
			e1.printStackTrace();
		}
		gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		gridData.horizontalSpan = 4;
		gridData.minimumHeight = 280;
		chrtFrame.setLayoutData(gridData);
		gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		gridData.horizontalSpan = 4;
		gridData.minimumHeight = 220;
		tableViewer = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		tableViewer.setLabelProvider(new TableLabelProvider());
		tableViewer.setContentProvider(new ContentProvider());
		tableViewer.setColumnProperties(properties.toArray(new String[0]));
		Table table = tableViewer.getTable();
		for (int i = 0; i < columnNames.size(); i++) {
			TableColumn tableColumn = new TableColumn(table, SWT.CENTER);
			tableColumn.setText(columnNames.get(i));
			table.getColumn(i).pack();
		}

		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(gridData);
		gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		gridData.horizontalSpan = 2;
		gridData.minimumHeight = 30;
		gridData.widthHint = 130;
		expPlotBtn = new Button(parent, SWT.PUSH);
		expPlotBtn.setText("����ͼ��");
		expPlotBtn.setFont(font);
		expPlotBtn.setAlignment(SWT.CENTER);
		expPlotBtn.setLayoutData(gridData);
		expPlotBtn.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog export = new FileDialog(parent.getShell(), SWT.SAVE);
				String filePath = export.open();
				exp2Picture(export, filePath, jFreeChart);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		expXlsBtn = new Button(parent, SWT.PUSH);
		expXlsBtn.setText("�������");
		expXlsBtn.setFont(font);
		expXlsBtn.setAlignment(SWT.CENTER);
		expXlsBtn.setLayoutData(gridData);
		expXlsBtn.addSelectionListener(new SelectionListener() {
			@SuppressWarnings("unchecked")
			public void widgetSelected(SelectionEvent e) {
				FileDialog export = new FileDialog(parent.getShell(), SWT.SAVE);
				String filePath = export.open();
				try {
					CVS.saveFile(data, filePath);
					// exp2Excel(filePath, data);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		return parent;
	}

	protected JFreeChart createChart(XYDataset xydataset, String duration, String valueName) {
		JFreeChart jfreechart = ChartFactory.createTimeSeriesChart("����ͼ", null, "��ֵ", xydataset, true, true, true);
		TextTitle textTitle = jfreechart.getTitle();
		textTitle.setFont(new Font("΢���ź�", Font.BOLD, 18));

		// ͼ��ͼ������
		LegendTitle legend = jfreechart.getLegend();
		if (legend != null) {
			legend.setPadding(0, 0, 6, 2);
			legend.setMargin(-2, 0, 5, 0);
			legend.setItemFont(new Font("΢���ź�", Font.PLAIN, 12));
		}

		XYPlot xyplot = jfreechart.getXYPlot();
		xyplot.setBackgroundPaint(new Color(255, 253, 246));
		xyplot.setOutlineStroke(new BasicStroke(1.5f));
		xyplot.setRangeGridlinesVisible(true);
		xyplot.setDomainGridlinesVisible(true);
		xyplot.setRangeGridlinePaint(Color.LIGHT_GRAY);
		xyplot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		xyplot.setNoDataMessage("û������");
		xyplot.setNoDataMessageFont(new Font("΢���ź�", Font.BOLD, 14));
		xyplot.setNoDataMessagePaint(new Color(87, 149, 117));
		xyplot.setWeight(100);
		xyplot.zoom(20);

		DateAxis dateaxis = (DateAxis) xyplot.getDomainAxis();
		dateaxis.setTickMarkPosition(DateTickMarkPosition.START);
		dateaxis.setTickUnit(new DateTickUnit(DateTickUnitType.YEAR, 1, new SimpleDateFormat("yyyy")));

		ValueAxis valueAxis = xyplot.getRangeAxis();
		valueAxis.setLabelFont(new Font("΢���ź�", Font.PLAIN, 12));
		valueAxis.setTickLabelFont(new Font("΢���ź�", Font.PLAIN, 10));

		valueAxis.setUpperBound(10);
		valueAxis.setAutoRangeMinimumSize(0.1);
		valueAxis.setLowerBound(-10);

		valueAxis.setAutoRange(true);
		valueAxis.setAxisLineStroke(new BasicStroke(1.5f));
		valueAxis.setAxisLinePaint(new Color(215, 215, 215));
		valueAxis.setLabelPaint(new Color(10, 10, 10));
		valueAxis.setTickLabelPaint(new Color(102, 102, 102));

		XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
		xylineandshaperenderer.setBaseShapesVisible(true);
		xylineandshaperenderer.setBaseLinesVisible(true);
		StandardXYToolTipGenerator xytool = new StandardXYToolTipGenerator();
		xylineandshaperenderer.setBaseToolTipGenerator((XYToolTipGenerator) xytool);
		xylineandshaperenderer.setSeriesStroke(0, new BasicStroke(1.5f));

		return jfreechart;
	}

	protected void refresh(JFreeChart chart, XYDataset dataset) {
		XYPlot plot = (XYPlot) chart.getPlot();
		if (cllctDurComb.getText().equals("��")) {
			dateTickUnit = new DateTickUnit(DateTickUnitType.YEAR, 1, new SimpleDateFormat("yyyy"));
		} else if (cllctDurComb.getText().equals("��")) {
			dateTickUnit = new DateTickUnit(DateTickUnitType.MONTH, 1, new SimpleDateFormat("yyyy��MM��"));
		} else if (cllctDurComb.getText().equals("��")) {
			dateTickUnit = new DateTickUnit(DateTickUnitType.DAY, 1, new SimpleDateFormat("MM��dd��"));
		} else if (cllctDurComb.getText().equals("ʱ")) {
			dateTickUnit = new DateTickUnit(DateTickUnitType.HOUR, 1, new SimpleDateFormat("dd��HHʱ"));
		} else if (cllctDurComb.getText().equals("��")) {
			dateTickUnit = new DateTickUnit(DateTickUnitType.MINUTE, 1, new SimpleDateFormat("HHʱmm��"));
		} else {
			dateTickUnit = new DateTickUnit(DateTickUnitType.SECOND, 1, new SimpleDateFormat("mm��ss��"));
		}
		ValueAxis valueAxis = plot.getRangeAxis();

		valueAxis.setUpperBound(maxVal);
		valueAxis.setAutoRangeMinimumSize(0.1);
		valueAxis.setLowerBound(minVal);

		System.out.println("Unit:" + dateTickUnit);
		DateAxis dateaxis = (DateAxis) plot.getDomainAxis();
		dateaxis.setTickUnit(dateTickUnit);
		plot.setDataset(dataset);
	}

	protected String formatDt(DateTime dt) {
		return dt.getYear() + "-" + dt.getMonth() + "-" + dt.getDay() + " " + dt.getHours() + ":" + dt.getMinutes()
				+ ":" + dt.getSeconds();
	}

	protected String formatDt(CDateTime dt) {
		return dt.getText();
	}

	protected boolean checkDate() {
		Date fromdt = fromDate.getSelection();
		Date todt = toDate.getSelection();

		if (fromdt.getTime() > todt.getTime()) {
			return false;
		}
		return true;
	}

	class LoadHandler extends Thread {
		public void run() {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					TimeSeriesCollection collection = null;
					try {
						collection = getAvgData(deviceIdComb.getText(), cllctDurComb.getText(), paramComb.getText(),
								fromDate, toDate);
					} catch (Exception e) {
						e.printStackTrace();
					}
					data = getData(deviceIdComb.getText(), formatDt(fromDate), formatDt(toDate));
					refresh(jFreeChart, collection);
					tableViewer.getTable().clearAll();
					tableViewer.setInput(data);
				}
			});
		}
	}

	class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object element, int columnIndex) {
			String result = "";
			RainerData o = (RainerData) element;
			switch (columnIndex) {
			case 0:
				result = o.getName();
				break;
			case 1:
				result = String.valueOf(o.getId());
				break;
			case 2:
				result = o.getIp();
				break;
			case 3:
				result = String.valueOf(o.getRainintn());
				break;
			case 4:
				result = String.valueOf(o.getPressure());
				break;
			case 5:
				result = String.valueOf(o.getRainfall());
				break;
			case 6:
				switch (o.getRaintype()) {
				case 1:
					result = "С��";
					break;
				case 3:
					result = "����";
					break;
				case 4:
					result = "����";
					break;
				case 5:
					result = "����";
					break;
				case 6:
					result = "�ش���";
					break;
				default:
					result = "����";
					break;
				}
				break;
			case 7:
				result = String.valueOf(o.getRdate());
				break;
			default:
				result = "";
			}
			return result;
		}

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
	}

	class ContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof List) {
				return ((List) inputElement).toArray();
			}
			return new Object[0];
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	protected void exp2Picture(FileDialog dialog, String filePath, JFreeChart chart) {
		if (!(filePath.endsWith(".png") || filePath.endsWith(".jpg"))) {
			filePath += ".jpg";
		}
		try {
			ChartUtilities.saveChartAsPNG(new File(filePath), chart, 1200, 350);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ChartDialog(Shell parentShell) {
		super(parentShell);
		font = new org.eclipse.swt.graphics.Font(Display.getCurrent(), "΢���ź�", 14, SWT.NONE);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
	}

	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE | SWT.MAX | SWT.MIN;
	}
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("���ݷ���");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(1100, 860);
	}
}
