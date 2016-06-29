package com.whck.rainer;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import com.whck.rainer.model.Constants;
import com.whck.rainer.model.Device;
import com.whck.rainer.model.Variable;
import com.whck.rainer.model.Zone;
import com.whck.rainer.util.XmlUtil;

public class AddVariableDialog extends Dialog {

	private int flag;
	private XmlUtil xmlUtil;
	private Zone zone;
	private Device device;
	private Variable variable;
	private static final int SUCCESS = 0;
	private static final int DECT_VALUE_EMPTY = 1;
	private static final int UNIT_EMPTY = 2;
	private static final int MIN_MAX_INVALID = 3;
	private static final int VALUE_EXIST = 4;
	private Font font;
	private Label nameLbl;
	private Combo nameComb;
	private Label unitLbl;
	private Text unitTxt;
	private Label maxValLbl;
	private Spinner maxValSpinner;
	private Label minValLbl;
	private Spinner minValSpinner;
	private Group dbTypeGrp;
	private int dbType;
	private Button ctrlBtn;
	private Button showBtn;
	private Button okBtn;
	private Button cancelBtn;
	public AddVariableDialog(Shell parentShell) {
		super(parentShell);
		font=new Font(Display.getCurrent(), "΢���ź�", 12,  SWT.NONE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		GridData gridData = new GridData(GridData.FILL_VERTICAL|GridData.FILL_HORIZONTAL|GridData.GRAB_HORIZONTAL|GridData.GRAB_VERTICAL);
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(2, true));
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER|GridData.FILL_HORIZONTAL|GridData.GRAB_HORIZONTAL|GridData.GRAB_VERTICAL);
		gridData.horizontalSpan = 1;
		gridData.verticalIndent=0;
		nameLbl = new Label(composite, SWT.NONE);
		nameLbl.setText("����");
		nameLbl.setFont(font);
		nameLbl.setAlignment(SWT.CENTER);
		nameLbl.setLayoutData(gridData);
		nameComb=new Combo(composite,SWT.NONE);
		nameComb.setItems(Constants.DEFAULT_VALUE);
		nameComb.setLayoutData(gridData);
		nameComb.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent arg0) {
				hide();
			}
			
			public void widgetDefaultSelected(SelectionEvent arg0) {
				
			}
		});
		nameComb.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent arg0) {
				show();
			}
		});
//		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER|GridData.FILL_HORIZONTAL|GridData.GRAB_HORIZONTAL);
//		gridData.horizontalSpan = 2;
//		container=new Composite(composite,SWT.NONE);
//		container.setLayoutData(gridData);
//		container.setLayout(new GridLayout(2,true));
//		gridData.horizontalSpan = 1;
		unitLbl = new Label(composite, SWT.NONE);
		unitLbl.setText("��λ");
		unitLbl.setFont(font);
		unitLbl.setLayoutData(gridData);
		unitLbl.setAlignment(SWT.CENTER);
		unitTxt = new Text(composite, SWT.NONE);
		unitTxt.setLayoutData(gridData);
		maxValLbl = new Label(composite, SWT.NONE);
		maxValLbl.setText("����ֵ");
		maxValLbl.setFont(font);
		maxValLbl.setLayoutData(gridData);
		maxValLbl.setAlignment(SWT.CENTER);
		maxValSpinner = new Spinner(composite, SWT.NONE);
		maxValSpinner.setMinimum(Integer.MIN_VALUE);
		maxValSpinner.setMaximum(Integer.MAX_VALUE);
		maxValSpinner.setLayoutData(gridData);
		minValLbl = new Label(composite, SWT.NONE);
		minValLbl.setText("����ֵ");
		minValLbl.setFont(font);
		minValLbl.setLayoutData(gridData);
		minValLbl.setAlignment(SWT.CENTER);
		minValSpinner = new Spinner(composite, SWT.NONE);
		minValSpinner.setMaximum(Integer.MAX_VALUE);
		minValSpinner.setMinimum(Integer.MIN_VALUE);
		minValSpinner.setLayoutData(gridData);
		gridData = new GridData(GridData.FILL_HORIZONTAL|GridData.GRAB_HORIZONTAL);
		gridData.horizontalSpan = 2;
		gridData.verticalIndent=6;
		dbTypeGrp=new Group(composite,SWT.None);
		dbTypeGrp.setText("�Ǳ�����ѡ��");
		dbTypeGrp.setFont(font);
		dbTypeGrp.setLayoutData(gridData);
		dbTypeGrp.setLayout(new GridLayout(2,true));
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER|GridData.FILL_HORIZONTAL|GridData.GRAB_HORIZONTAL);
		gridData.horizontalSpan = 1;
		gridData.horizontalIndent=6;
		Button tempRadio=new Button(dbTypeGrp,SWT.RADIO);
		tempRadio.setText("�¶ȼ�");
		tempRadio.setAlignment(SWT.CENTER);
		tempRadio.setFont(font);
		tempRadio.setLayoutData(gridData);
		tempRadio.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				if(tempRadio.getSelection()){
					dbType=0;
				}
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		Button jumpRadio=new Button(dbTypeGrp,SWT.RADIO);
		jumpRadio.setText("��̬ͼ");
		jumpRadio.setAlignment(SWT.CENTER);
		jumpRadio.setFont(font);
		jumpRadio.setLayoutData(gridData);
		jumpRadio.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				if(jumpRadio.getSelection()){
					dbType=1;
				}
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		jumpRadio.setSelection(true);
		Button dashRadio=new Button(dbTypeGrp,SWT.RADIO);
		dashRadio.setText("��Բ����");
		dashRadio.setAlignment(SWT.CENTER);
		dashRadio.setFont(font);
		dashRadio.setLayoutData(gridData);
		dashRadio.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(dashRadio.getSelection()){
					dbType=2;
				}
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		Button roundRadio=new Button(dbTypeGrp,SWT.RADIO);
		roundRadio.setText("������");
		roundRadio.setAlignment(SWT.CENTER);
		roundRadio.setFont(font);
		roundRadio.setLayoutData(gridData);
		roundRadio.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				if(roundRadio.getSelection()){
					dbType=3;
				}
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		ctrlBtn = new Button(composite, SWT.CHECK);
		ctrlBtn.setText("���趨");
		ctrlBtn.setAlignment(SWT.CENTER);
		ctrlBtn.setFont(font);
		ctrlBtn.setLayoutData(gridData);
		showBtn = new Button(composite, SWT.CHECK);
		showBtn.setText("����ʾ");
		showBtn.setAlignment(SWT.CENTER);
		showBtn.setFont(font);
		showBtn.setLayoutData(gridData);
		
		if (flag == 1) {
			loadVariableInfo();
		}
		return composite;
	}

	protected void loadVariableInfo() {
		String varName = variable.getName();
		if(Arrays.asList(Constants.DEFAULT_VALUE).contains(varName)){
			hide();
		}else{
			String varUnit = variable.getUnit();
			String maxVal = variable.getUpLimitVal();
			String minVal = variable.getDownLimitVal();
			boolean ctrlable = variable.isCtrlable();
			boolean showable = variable.isShowable();

			nameComb.setText(varName);
			unitTxt.setText(varUnit);
			maxValSpinner.setSelection(Integer.valueOf(maxVal));
			minValSpinner.setSelection(Integer.valueOf(minVal));

			ctrlBtn.setSelection(ctrlable);
			showBtn.setSelection(showable);
		}
	}
	protected void show(){
		unitLbl.setVisible(true);
		unitTxt.setVisible(true);
		maxValLbl.setVisible(true);
		maxValSpinner.setVisible(true);
		minValLbl.setVisible(true);
		minValSpinner.setVisible(true);
		dbTypeGrp.setVisible(true);
	}
	protected void hide(){
		unitLbl.setVisible(false);
		unitTxt.setVisible(false);
		maxValLbl.setVisible(false);
		maxValSpinner.setVisible(false);
		minValLbl.setVisible(false);
		minValSpinner.setVisible(false);
		dbTypeGrp.setVisible(false);
	}
	@Override
	protected void okPressed() {
		try {
			xmlUtil = XmlUtil.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String varName = nameComb.getText().trim();
		if(Arrays.asList(Constants.DEFAULT_VALUE).contains(varName)){
			if(varName.equals("����")){
				variable=new Variable(varName,Arrays.asList(Constants.RAIN_TYPES),
						"��","0","5",true,true,"1");
			}else if(varName.equals("����")){
				variable=new Variable(varName,Arrays.asList(Constants.WIND_ORIENTIONS_12),
						"��","0","11",true,true,"3");
			}else if(varName.equals("ˮ��")){
				variable=new Variable(varName,Arrays.asList(Constants.PUMP_LEVEL),
						"��","0","2",true,true,"1");
			}
			xmlUtil.deleteVariable(zone.getName(), device.getId(), variable.getName());
			device.remove(variable);
		}else{
			String varUnit = unitTxt.getText().trim();
			int maxVal = maxValSpinner.getSelection();
			int minVal = minValSpinner.getSelection();
			List<String> types = new LinkedList<>();
			boolean ctrlable = ctrlBtn.getSelection();
			boolean showable = showBtn.getSelection();
			if(!showMessage(checkForm(varName, varUnit,maxVal, minVal),flag)){
				return;
			}
			if (flag == 1) { // ������޸ģ���ɾ��
				xmlUtil.deleteVariable(zone.getName(), device.getId(), variable.getName());
				device.remove(variable);
			}
			variable = new Variable(varName, types, varUnit, String.valueOf(maxVal), String.valueOf(minVal), ctrlable,
					showable,String.valueOf(dbType));
		}
		
		xmlUtil.addVraiable(zone.getName(), device.getId(), variable);
		device.add(variable);
		showOK();
		super.okPressed();
	}

	protected int checkForm(String varName, String unit,int maxVal, int minVal) {
		int result = SUCCESS;
		if (varName.equals("")) {
			return DECT_VALUE_EMPTY;
		}
		if (xmlUtil.getVarNames(zone.getName(),device.getId()).contains(varName)) {
			return VALUE_EXIST;
		}
		if (unit.equals("")) {
			return UNIT_EMPTY;
		}
		if (maxVal < minVal) {
			return MIN_MAX_INVALID;
		}
		return result;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		// TODO Auto-generated method stub
		super.configureShell(newShell);
		newShell.setText("��������");
	}
	protected void showOK() {
		MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_INFORMATION);
		box.setText("��ʾ");
		box.setMessage("�����ɹ�");
		box.open();
		getShell().close();
	}

	protected boolean showMessage(int msgType, int flag) {
		if (DECT_VALUE_EMPTY == msgType) {
			MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_WARNING);
			box.setText("��ʾ");
			box.setMessage("����������Ϊ��");
			box.open();
			return false;
		} else if (VALUE_EXIST == msgType && flag == 0) {
			MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_WARNING);
			box.setText("��ʾ");
			box.setMessage("�������Ѵ���");
			box.open();
			return false;
		} else if (UNIT_EMPTY == msgType) {
			MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_WARNING);
			box.setText("��ʾ");
			box.setMessage("������λ����Ϊ��");
			box.open();
			return false;
		} else if (MIN_MAX_INVALID == msgType) {
			MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_WARNING);
			box.setText("��ʾ");
			box.setMessage("������ֵ���Ϸ�(����ֵ�����������ֵ)");
			box.open();
			return false;
		}
		return true;
	}

	public Variable getVariable() {
		return this.variable;
	}

	public Device getDevice() {
		return device;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public void setVariable(Variable var) {
		this.variable = var;
		device = (Device) variable.getParent();
		zone = (Zone) device.getParent();
	}

	public void setDevice(Device device) {
		variable = null;
		this.device = device;
		zone = (Zone) device.getParent();
	}
	@Override
	protected Point getInitialSize() {
		return new Point(280,360);
	}
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		
	}
	@Override
	protected void initializeBounds() {
		Composite compo = (Composite) getButtonBar();
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER|GridData.FILL_HORIZONTAL|GridData.GRAB_HORIZONTAL);
		compo.setLayout(new GridLayout(2,true));
		compo.setLayoutData(gridData);
		okBtn=super.createButton(compo, IDialogConstants.OK_ID, "ȷ��", false);
		cancelBtn=super.createButton(compo, IDialogConstants.CANCEL_ID, "ȡ��", false);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER|GridData.FILL_HORIZONTAL|GridData.GRAB_HORIZONTAL);
		gridData.horizontalSpan = 2;
		gridData.horizontalIndent=6;
		okBtn.setText("ȷ��");
		okBtn.setFont(font);
		okBtn.setLayoutData(gridData);
		cancelBtn.setText("ȡ��");
		cancelBtn.setFont(font);
		cancelBtn.setLayoutData(gridData);
		super.initializeBounds();
	}
}
