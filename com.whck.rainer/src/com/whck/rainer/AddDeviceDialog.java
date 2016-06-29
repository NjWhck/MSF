package com.whck.rainer;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import com.whck.rainer.model.Device;
import com.whck.rainer.model.Variable;
import com.whck.rainer.model.Zone;
import com.whck.rainer.util.MethodUtil;
import com.whck.rainer.util.XmlUtil;

public class AddDeviceDialog extends Dialog {

	private static final int SUCCESS = 0;
	private static final int DEVICE_NAME_EMPTY = 1;
	private static final int DEVICE_ID_EMPTY = 2;
	private static final int DEVICE_ID_EXSIT = 3;
	private static final int DEVICE_ID_INVALID = 4;
	private static final int DEVICE_IP_EMPTY = 5;
	private static final int DEVICE_IP_INVALID = 6;
	private XmlUtil xmlUtil;
	
	private Font font;
	private int flag;
	private Zone zone;
	private Device device;
	private Label nameLbl;
	private Text nameTxt;
	private Label idLbl;
	private Text idTxt;
	private Label ipLbl;
	private Text ipTxt;
	private Button ctrlChk;
	private Button dectChk;
	private Group typeGrp;
	private Button motorRadio;
	private Button triggerRadio;
	private Button okBtn;
	private Button cancelBtn;

	public AddDeviceDialog(Shell parentShell) {
		super(parentShell);
		font=new Font(Display.getCurrent(), "΢���ź�", 12,  SWT.NONE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		GridData gridData = new GridData(GridData.FILL_VERTICAL|GridData.FILL_HORIZONTAL|GridData.GRAB_HORIZONTAL|GridData.GRAB_VERTICAL);
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, true));
		composite.setLayoutData(gridData);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER|GridData.FILL_HORIZONTAL|GridData.GRAB_HORIZONTAL|GridData.GRAB_VERTICAL);
		gridData.horizontalSpan = 1;
		gridData.verticalIndent=0;
		nameLbl = new Label(composite, SWT.NONE);
		nameLbl.setText("�豸����");
		nameLbl.setFont(font);
		nameLbl.setAlignment(SWT.CENTER);
		nameLbl.setLayoutData(gridData);
		nameTxt = new Text(composite, SWT.BORDER);
		nameTxt.setLayoutData(gridData);
		idLbl = new Label(composite, SWT.NONE);
		idLbl.setText("�豸ID");
		idLbl.setFont(font);
		idLbl.setAlignment(SWT.CENTER);
		idLbl.setLayoutData(gridData);
		idTxt = new Text(composite, SWT.BORDER);
		idTxt.setLayoutData(gridData);
		ipLbl = new Label(composite, SWT.NONE);
		ipLbl.setText("�豸IP");
		ipLbl.setFont(font);
		ipLbl.setAlignment(SWT.CENTER);
		ipLbl.setLayoutData(gridData);
		ipTxt = new Text(composite, SWT.BORDER);
		ipTxt.setLayoutData(gridData);

		ctrlChk = new Button(composite, SWT.CHECK);
		ctrlChk.setText("�����豸");
		ctrlChk.setFont(font);
		ctrlChk.setAlignment(SWT.CENTER);
		ctrlChk.setLayoutData(gridData);
		ctrlChk.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (ctrlChk.getSelection()) {
					typeGrp.setVisible(true);
				} else {
					typeGrp.setVisible(false);
				}
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		dectChk = new Button(composite, SWT.CHECK);
		dectChk.setText("����豸");
		dectChk.setFont(font);
		dectChk.setAlignment(SWT.CENTER);
		dectChk.setLayoutData(gridData);

		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER|GridData.FILL_HORIZONTAL|GridData.GRAB_HORIZONTAL|GridData.GRAB_VERTICAL);
		gridData.horizontalSpan = 2;
		typeGrp = new Group(composite, SWT.NONE);
		typeGrp.setVisible(false);
		typeGrp.setLayoutData(gridData);
		typeGrp.setLayout(new GridLayout(2, true));
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER|GridData.FILL_HORIZONTAL|GridData.GRAB_HORIZONTAL|GridData.GRAB_VERTICAL);
		gridData.horizontalSpan =1;
		gridData.verticalIndent=0;
		motorRadio = new Button(typeGrp, SWT.RADIO);
		motorRadio.setText("����豸");
		motorRadio.setFont(font);
		motorRadio.setAlignment(SWT.CENTER);
		motorRadio.setLayoutData(gridData);
		triggerRadio = new Button(typeGrp, SWT.RADIO);
		triggerRadio.setText("�����豸");
		triggerRadio.setFont(font);
		triggerRadio.setAlignment(SWT.CENTER);
		triggerRadio.setLayoutData(gridData);
		if (flag == 1) {
			nameTxt.setText(device.getName());
			idTxt.setText(device.getId());
			ipTxt.setText(device.getIp());
			int type = device.getType();

			if (10 == type) { // �������
				typeGrp.setVisible(true);
				ctrlChk.setSelection(true);
				motorRadio.setSelection(true);
			} else if (11 == type) { // �������
				typeGrp.setVisible(true);
				ctrlChk.setSelection(true);
				triggerRadio.setSelection(true);
			} else if (20 == type) {
				dectChk.setSelection(true);
			}
		}
		// loadDeviceInfo();
		return composite;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		// TODO Auto-generated method stub
		super.configureShell(newShell);
		newShell.setText("����豸");
	}

	@Override
	protected void okPressed() {
		try {
			xmlUtil = XmlUtil.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String devName = nameTxt.getText().trim();
		String devId = idTxt.getText().trim();
		String devIp = ipTxt.getText().trim();
		int type = 0;
		if (ctrlChk.getSelection()) {
			if (motorRadio.getSelection()) {
				type = 10;
			} else {
				type = 11;
			}
		} else {
			type = 20;
		}

		if (!showMessage(checkForm(devName, devId, devIp), flag)) {
			return;
		}
		if (flag == 1) {
			List<Variable> vars = device.getVariables();
			xmlUtil.deleteDevice(zone.getName(), device.getId());
			zone.remove(device);
			device = new Device(devId, devName, devIp, type, new ArrayList<Variable>(0));
			device.setVariables(vars);
			zone.add(device);
			xmlUtil.addDevice(zone.getName(), device);

		} else {
			device = new Device(devId, devName, devIp, type, new ArrayList<Variable>(0));
			zone.add(device);
			xmlUtil.addDevice(zone.getName(), device);
		}
		showOK();
		super.okPressed();
	}

	protected void showOK() {
		MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_INFORMATION);
		box.setText("��ʾ");
		box.setMessage("�����ɹ�");
		box.open();
		getShell().close();
	}

	protected boolean showMessage(int msgType, int flag) {
		if (DEVICE_NAME_EMPTY == msgType) {
			MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_WARNING);
			box.setText("��ʾ");
			box.setMessage("�豸����Ϊ��");
			box.open();
			return false;
		} else if (DEVICE_ID_EXSIT == msgType && flag == 0) {
			MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_WARNING);
			box.setText("��ʾ");
			box.setMessage("��ID�Ѵ���");
			box.open();
			return false;
		} else if (DEVICE_ID_EMPTY == msgType) {
			MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_WARNING);
			box.setText("��ʾ");
			box.setMessage("ID����Ϊ��");
			box.open();
			return false;
		} else if (DEVICE_ID_INVALID == msgType) {
			MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_WARNING);
			box.setText("��ʾ");
			box.setMessage("ID������,����д����");
			box.open();
			return false;
		} else if (DEVICE_IP_EMPTY == msgType) {
			MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_WARNING);
			box.setText("��ʾ");
			box.setMessage("IP��ַ����Ϊ��");
			box.open();
			return false;
		} else if (DEVICE_IP_INVALID == msgType) {
			MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_WARNING);
			box.setText("��ʾ");
			box.setMessage("IP��ַ���Ϸ�");
			box.open();
			return false;
		}
		return true;
	}

	protected int checkForm(String name, String id, String ip) {
		int result = SUCCESS;
		XmlUtil xmlUtil = null;
		try {
			xmlUtil = XmlUtil.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (name.equals("")) {
			return DEVICE_NAME_EMPTY;
		}
		if (id.equals("")) {
			return DEVICE_ID_EMPTY;
		}
		if (ip.equals("")) {
			return DEVICE_IP_EMPTY;
		}
		if (!MethodUtil.isValidIp(ip)) {
			return DEVICE_IP_INVALID;
		}
		if (xmlUtil.getDevIds(zone.getName()).contains(id)) {
			return DEVICE_ID_EXSIT;
		}
		if (!MethodUtil.isNumeric(id)) {
			return DEVICE_ID_INVALID;
		}

		return result;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}

	public Zone getZone() {
		return zone;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
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
