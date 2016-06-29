package com.whck.rainer.panel;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import com.whck.proto.model.RainerData;
import com.whck.rainer.model.Variable;

public class DashBoard extends Canvas {
	
	private Variable var;
	private String value="1";
	private Image dashboard;
	private Font titleFont;
	private Font scaleFont;
	private Font valueFont;
	public DashBoard(Composite parent, int style) {
		super(parent, style);
	}
	
	public DashBoard(Composite parent, int style,Variable var) {
		super(parent, SWT.DOUBLE_BUFFERED);
		this.var=var;
		titleFont=new Font(this.getDisplay(),"΢���ź�",9,SWT.NONE);
		scaleFont=new Font(this.getDisplay(),"΢���ź�",8,SWT.NONE);
		valueFont=new Font(this.getDisplay(),"΢���ź�",8,SWT.NONE);
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				DashBoard.this.paintControl(e);
			}
		});
	}

	public Variable getVar() {
		return var;
	}

	public void setVar(Variable var) {
		this.var = var;
	}

	protected void paintControl(PaintEvent e) {
		String valueString=value;
		String title= var.getName()+"("+var.getUnit()+")";
		if(value.length()>7){
			valueString=value.substring(0, 6);
		}
		//�����Ǳ����ͻ���
		if(var.getDbType().equals("0")){
			dashboard=PictureFactory.getImage("db_temp");
			Rectangle rectangle=getBounds();
			GC gc=e.gc;
			
			//��title
			gc.setFont(titleFont);
			Point titleExtend=gc.stringExtent(title);
			gc.drawString(title, (rectangle.width-titleExtend.x)/2, 0);
			
			//��dashboard
			Rectangle imageRect=dashboard.getBounds();
			gc.drawImage(dashboard,(rectangle.width-imageRect.width)/2, (rectangle.height-imageRect.height)/2);
			
			//������ֵ
			gc.setFont(scaleFont);
			String upSign=var.getUpLimitVal();
			Point upSignPt=gc.stringExtent(upSign);
			gc.drawString(upSign,(rectangle.width-imageRect.width)/2+20-upSignPt.x,  titleExtend.y+1);
		
			//������ֵ
			gc.setFont(scaleFont);
			String downSign=var.getDownLimitVal();
			Point downSignPt=gc.stringExtent(downSign);
			gc.drawString(downSign,(rectangle.width-imageRect.width/3)/2-downSignPt.x-2, titleExtend.y+1+imageRect.height-downSignPt.y/2);
		
			//����̬����
			int size=(Integer.valueOf(value)-Integer.valueOf(downSign))*imageRect.height/(Integer.valueOf(upSign)-Integer.valueOf(downSign));
			int startY= imageRect.height+titleExtend.y-size;
			int startX=(rectangle.width-imageRect.width)/2+30;
			gc.setAdvanced(true);
			gc.setAntialias(SWT.ON);
			gc.setForeground(PictureFactory.getColor("��ɫ"));
//			gc.setBackground(PictureFactory.getColor("��ɫ"));
			gc.setLineWidth(3);	
			gc.drawRectangle(startX, startY, 3, size);
			
			//������
			gc.setFont(valueFont);
			Point valueExtend=gc.stringExtent(valueString);
			
			gc.drawString(valueString, (rectangle.width-valueExtend.x)/2, titleExtend.y+imageRect.height);
			gc.dispose();
			
		}
			
		else if(var.getDbType().equals("1")){
			int rainType=Integer.valueOf(value);
			switch(rainType){
				case 1:
					dashboard=PictureFactory.getImage("termRain");
					valueString="С��";
					break;
				case 2:
					dashboard=PictureFactory.getImage("dblRain");
					valueString="����";
					break;
				case 3:
					dashboard=PictureFactory.getImage("trpRain");
					valueString="����";
					break;
				case 4:
					dashboard=PictureFactory.getImage("altrRain");
					valueString="����";
					break;
			}
			GC gc=e.gc;
		
			Rectangle rectangle=getBounds();
			gc.setFont(titleFont);
			Point titleExtend=gc.stringExtent(title);
			gc.drawString(title, (rectangle.width-titleExtend.x)/2, 0);
			
			//��dashboard
			Rectangle imageRect=dashboard.getBounds();
			gc.setAdvanced(true);
			gc.setAntialias(SWT.ON);
			gc.drawImage(dashboard,(rectangle.width-imageRect.width)/2, (rectangle.height-imageRect.height)/2);
			
			gc.setFont(titleFont);
			Point valueExtend=gc.stringExtent(valueString);
			gc.setForeground(PictureFactory.getColor("��ɫ"));
			gc.drawString(valueString, (rectangle.width-valueExtend.x)/2, titleExtend.y+imageRect.height);
			gc.dispose();
		}else if(var.getDbType().equals("2")){ //��Բ�Ǳ���
			dashboard=PictureFactory.getImage("db_semi");
			Rectangle rectangle=getBounds();
			GC gc=e.gc;
			//��title
			gc.setFont(titleFont);
			Point titleExtend=gc.stringExtent(title);
			gc.drawString(title, (rectangle.width-titleExtend.x)/2, 0);
			
			//��dashboard
			Rectangle imageRect=dashboard.getBounds();
			gc.drawImage(dashboard,(rectangle.width-imageRect.width)/2, (rectangle.height-imageRect.height)/2);
			
			//������ֵ
			gc.setFont(scaleFont);
			String upSign=var.getUpLimitVal();
			Point upSignPt=gc.stringExtent(upSign);

			gc.drawString(upSign,(rectangle.width-imageRect.width)/2+63-upSignPt.x/2, titleExtend.y+1+34);
		
			//������ֵ
			gc.setFont(scaleFont);
			String downSign=var.getDownLimitVal();
			gc.drawString(downSign,(rectangle.width-imageRect.width)/2+1, titleExtend.y+2+33);
			
			//������
			gc.setFont(valueFont);
			Point valueExtend=gc.stringExtent(valueString);
			gc.setForeground(PictureFactory.getColor("��ɫ"));
			gc.drawString(valueString, (rectangle.width-valueExtend.x)/2, titleExtend.y+imageRect.height);
			
			//����̬����
			Transform tr = new Transform(this.getDisplay());
			float rate=(Float.valueOf(value)-Float.valueOf(var.getDownLimitVal()))/(((Float.valueOf(var.getUpLimitVal())-Float.valueOf(var.getDownLimitVal())))*1.0f);
			float angle=(float) (rate*180);
			Image pointer=PictureFactory.getImage("semi_pointer");
			
			tr.translate(98/2.0f, 98/2.0f);
	        tr.rotate(angle);
			tr.translate(-98/2.0f,-98/2.0f);
			gc.setTransform(tr);
			gc.setAdvanced(true);
			gc.setAntialias(SWT.ON);
			gc.drawImage(pointer,(rectangle.width-imageRect.width)/2,  (rectangle.height-imageRect.height)/2); 
			tr.dispose();
			gc.dispose();
		}else if(var.getDbType().equals("3")){
			dashboard=PictureFactory.getImage("db_round");
			Rectangle rectangle=getBounds();
			GC gc=e.gc;
			//��title
			gc.setFont(titleFont);
			Point titleExtend=gc.stringExtent(title);
			gc.drawString(title, (rectangle.width-titleExtend.x)/2, 0);
			
			//��dashboard
			Rectangle imageRect=dashboard.getBounds();
			gc.drawImage(dashboard,(rectangle.width-imageRect.width)/2, (rectangle.height-imageRect.height)/2);
			
			//������
			gc.setFont(valueFont);
			gc.setForeground(PictureFactory.getColor("��ɫ"));
			Point valueExtend=gc.stringExtent(valueString);
			gc.drawString(valueString, (rectangle.width-valueExtend.x)/2, titleExtend.y+imageRect.height);
			//����̬����
			Transform tr = new Transform(getDisplay());
			int order;
			if(var.getName().equals("����")){
				List<String> windOrient=Arrays.asList(com.whck.rainer.model.Constants.WIND_ORIENTIONS_8);
				 order=windOrient.indexOf(value);
			}else if(var.getName().equals("ת��")){
				List<String> windOrient=Arrays.asList(com.whck.rainer.model.Constants.WIND_ORIENTIONS_8);
				 order=windOrient.indexOf(value);
			}else{
				List<String> windOrient=Arrays.asList(com.whck.rainer.model.Constants.WIND_ORIENTIONS_8);
				 order=windOrient.indexOf(value);
			}
			Image pointer=PictureFactory.getImage("round_pointer");
			
			order=3;
			float angle=(float) (order*45);
	        tr.translate(98/2.0f, 98/2.0f);
	        tr.rotate(angle);
			tr.translate(-98/2.0f,-98/2.0f);
			gc.setTransform(tr);
			gc.setAdvanced(true);
			gc.setAntialias(SWT.ON);
			gc.drawImage(pointer,(rectangle.width-imageRect.width)/2,  (rectangle.height-imageRect.height)/2); 
			tr.dispose();
			gc.dispose();
		}
	}
	protected void widgetDisposed(DisposeEvent e) {
		if(dashboard!=null){
			dashboard.dispose();
			dashboard=null;
		}
	}
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(65+33,65+33);
	}
	public void update(Object message) {
		String varname_cn=var.getName();
		String varname_en=Constants.cn_en_map.get(varname_cn);
		@SuppressWarnings("unchecked")
		Method method=matchPojoMethods((Class<RainerData>)message.getClass(),varname_en);
		Object valobj=null;
		try {
			valobj = method.invoke(message);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		this.value=String.valueOf(valobj);
		
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				redraw();		
			}
		});
	}
	protected Method matchPojoMethods(Class<RainerData> clazz, String varName) {
		Method target=null;
		Method[] methods = clazz.getMethods();
		String methodName="get"+varName.substring(0, 1).toUpperCase()+varName.substring(1);
		for (int index = 0; index < methods.length; index++) {
			Method temp=methods[index];
			if (temp.getName().equals(methodName)) {
				target=temp;
				break;
			}
		}
		return target;
	}
	
}
