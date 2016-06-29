package com.whck.rainer.panel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.mina.core.session.IoSession;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Spinner;
import com.whck.proto.handler.MessageHandler;
import com.whck.proto.model.RainerData;
import com.whck.proto.model.SendMulti;
import com.whck.proto.model.SendSingle;
import com.whck.rainer.db.RainerDataDAO;
import com.whck.rainer.model.Constants;
import com.whck.rainer.model.Device;
import com.whck.rainer.model.Variable;
import com.whck.rainer.model.Zone;
import com.whck.rainer.util.AlgorithmUtil;

public class SoHard extends Composite {
	private Device device;
	private Font font;
//	private Button relaChkBtn;
	private Label stateBar;
	private int rainType = 2;
	private int rainFall = 0;
	private int timeDur = 0;
	private DashBoard timeDB;
	private Map<String, Control> showCtrls = new HashMap<String, Control>(0);

	public SoHard(Composite parent, int style) {
		super(parent, SWT.BORDER);
	}

	public SoHard(Composite parent, int style, Device device) {
		super(parent, SWT.BORDER);
		this.setLayout(new GridLayout(1, true));
		this.device = device;
		font = new Font(Display.getCurrent(), "΢���ź�", 10, SWT.NONE);
		createControlArea();
	}

	private void createControlArea() {
		// ����device�����ؼ�
		GridData outGridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		Composite header = new Composite(this, SWT.NONE);
		header.setLayoutData(outGridData);
		header.setLayout(new GridLayout(3, true));
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_VERTICAL
				| GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_CENTER);
		gridData.heightHint = 22;
		gridData.horizontalIndent = 6;
		gridData.verticalIndent = 1;

//		relaChkBtn = new Button(header, SWT.CHECK);
//		relaChkBtn.setLayoutData(gridData);
//		relaChkBtn.setText("����");
//		relaChkBtn.setFont(font);
//		relaChkBtn.addSelectionListener(new SelectionListener() {
//			public void widgetSelected(SelectionEvent e) {
//				Map<String, SoHard> target = ShowView.sPanels.get(ShowView.zoneNames.get(0));
//				if (relaChkBtn.getSelection()) {
//					for(Iterator<SoHard> it=target.values().iterator();it.hasNext();){
//						SoHard sh=it.next();
//						sh.relaTrigger(true);
//					}
//				} else {
//					for(Iterator<SoHard> it=target.values().iterator();it.hasNext();){
//						SoHard sh=it.next();
//						sh.relaTrigger(false);
//					}
//				}
//			}
//
//			public void widgetDefaultSelected(SelectionEvent e) {
//			}
//		});
		Label titleLbl = new Label(header, SWT.NONE);
		titleLbl.setLayoutData(gridData);
		titleLbl.setFont(new Font(this.getDisplay(), "΢���ź�", 13, SWT.BOLD));
		titleLbl.setAlignment(SWT.CENTER);
		titleLbl.setText(device.getName() + "(" + device.getId() + ")");

		gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_VERTICAL
				| GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_CENTER);
		gridData.heightHint = 22;
		gridData.horizontalSpan=2;
		gridData.horizontalIndent = 6;
		gridData.verticalIndent = 1;
		stateBar = new Label(header, SWT.NONE);
		stateBar.setText("��ֹͣ");
		stateBar.setFont(new Font(this.getDisplay(), "΢���ź�", 12, SWT.NONE));
		stateBar.setAlignment(SWT.CENTER);
		stateBar.setLayoutData(gridData);
		// Button settingBtn = new Button(header, SWT.INHERIT_FORCE);
		// settingBtn.setText("����");
		// settingBtn.setLayoutData(gridData);
		// settingBtn.addSelectionListener(new SelectionListener() {
		//
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// SettingDialog dialog = new SettingDialog(SoHard.this.getShell(),
		// device);
		// dialog.open();
		// }
		//
		// @Override
		// public void widgetDefaultSelected(SelectionEvent e) {
		// }
		// });
		gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_VERTICAL
				| GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_CENTER);
		gridData.heightHint = 22;
		gridData.horizontalSpan=1;
		gridData.horizontalIndent = 6;
		gridData.verticalIndent = 1;
		
		Composite body = new Composite(this, SWT.NONE);
		outGridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		body.setLayoutData(outGridData);
		body.setLayout(new GridLayout(1, true));
		Composite showArea = new Composite(body, SWT.BORDER);
		showArea.setLayoutData(outGridData);
		RowLayout layout = new RowLayout();
		layout.marginTop = 13;
		layout.spacing=0;
		layout.justify = true;
		showArea.setLayout(layout);
		for (Iterator<Variable> it = device.getVariables().iterator(); it.hasNext();) {
			Control control = null;
			Variable var = it.next();
			String varName = var.getName();
			control = new DashBoard(showArea, SWT.NONE, var);
			showCtrls.put(varName, control);
		}
		outGridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		Composite setArea = new Composite(body, SWT.BORDER);
		setArea.setLayoutData(outGridData);
		setArea.setLayout(new GridLayout(2, true));
		GridData setGridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		setGridData.verticalIndent = 8;
		Label rainTypeLbl = new Label(setArea, SWT.NONE);
		rainTypeLbl.setText("����");
		rainTypeLbl.setFont(font);
		rainTypeLbl.setAlignment(SWT.CENTER);
		rainTypeLbl.setLayoutData(setGridData);
		Combo rainTypeComb = new Combo(setArea, SWT.NONE);
		rainTypeComb.setItems(Constants.RAIN_TYPES);
		rainTypeComb.select(1);
		rainTypeComb.setLayoutData(setGridData);
		Button rainFallBtn = new Button(setArea, SWT.CHECK);
		rainFallBtn.setText("����(mm)");
		rainFallBtn.setFont(font);
		rainFallBtn.setAlignment(SWT.CENTER);
		rainFallBtn.setLayoutData(setGridData);
		Spinner rainFallSpn = new Spinner(setArea, SWT.NONE);
		rainFallSpn.setMaximum(999);
		rainFallSpn.setMinimum(0);
		rainFallSpn.setLayoutData(setGridData);
		Button timeBtn = new Button(setArea, SWT.CHECK);
		setGridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		setGridData.verticalIndent = 4;
		timeBtn.setText("��ʱ(min)");
		timeBtn.setFont(font);
		timeBtn.setAlignment(SWT.CENTER);
		timeBtn.setLayoutData(setGridData);
		timeBtn.setLayoutData(setGridData);
		Spinner timeSpn = new Spinner(setArea, SWT.NONE);
		timeSpn.setMaximum(720);
		timeSpn.setMinimum(1);
		timeSpn.setLayoutData(setGridData);
		rainFallBtn.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (rainFallBtn.getSelection()) {
					timeBtn.setSelection(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

			}
		});
		timeBtn.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				if (timeBtn.getSelection()) {
					rainFallBtn.setSelection(false);
				}
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		Composite footer = new Composite(this, SWT.NONE);
		outGridData = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_END);
		footer.setLayoutData(outGridData);
		footer.setLayout(new GridLayout(3, true));

		gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		Button modeSet = new Button(footer, SWT.NONE);
		modeSet.setText("ģʽȷ��");
		modeSet.setLayoutData(gridData);
		modeSet.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				// ������ݷ���
				boolean syn = true;
				int mode = 4;// Ĭ��Ϊͬ���ֶ�ģʽ
				rainType = rainTypeComb.getSelectionIndex() + 1;
				Zone zone = (Zone) device.getParent();
				for (Iterator<Device> it = zone.getDevices().iterator(); it.hasNext();) {
					Device device = it.next();
					if (!device.isTriggered()) {// �����δ��ѡ�е��豸����ȡ������ģʽ
						syn = false;
					}
				}
				if (rainFallBtn.getSelection()) {
					if (syn) {
						mode = 6;
					} else {
						mode = 2;
					}
				}
				else if (timeBtn.getSelection()) {
					// TODO:new DashBoard
					if (timeDB == null) {
						Variable timeVar = new Variable();
						timeVar.setName("����ʱ");
						timeVar.setUpLimitVal(String.valueOf(60 * timeSpn.getSelection()));
						timeVar.setDownLimitVal("0");
						timeVar.setUnit("s");
						timeVar.setDbType("0");
						timeDB = new DashBoard(showArea, SWT.NONE, timeVar);
						showCtrls.put("Timer", timeDB);
					} else {
						timeDB.getVar().setUpLimitVal(String.valueOf(60 * timeSpn.getSelection()));
					}
					showArea.layout();
					if (syn) {
						mode = 5;
					} else {
						mode = 3;
					}
				} else {
					if (timeDB != null) {
						showCtrls.remove("Timer");
						timeDB.dispose();
						timeDB = null;
					}
					showArea.layout();
					if (syn) {
						mode = 4;
					} else {
						mode = 1;
					}
				}
				System.out.println("mode:"+mode);
				if (mode == 2 || mode == 6) {
					timeDur = 0;
					rainFall = rainFallSpn.getSelection()*10;
				} else if (3 == mode || 5 == mode) {
					timeDur = 60 * timeSpn.getSelection();
					rainFall = 0;
				} else {
					timeDur = 0;
					rainFall = 0;
				}
				byte[] value = new byte[12];
				for (int i = 0; i < 3; i++) {
					byte[] temp = new byte[4];
					if (i == 0)
						temp = AlgorithmUtil.intToByteArray(rainFall);
					else if (i == 1)
						temp = AlgorithmUtil.intToByteArray(timeDur);
					else
						temp = AlgorithmUtil.intToByteArray(rainType);

					for (int j = 0; j < 4; j++) {
						value[i * 4 + j] = temp[j];
					}
				}
				SendMulti sm = new SendMulti();
				byte[] prePart = { 0, 0, 0, (byte) mode, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0 };
				sm.setAdr(Byte.valueOf(device.getId()));
				sm.setFuncCode(com.whck.proto.model.Constants.MULTI_REGISTER);
				sm.setData(AlgorithmUtil.byteArrsMerger(prePart, value));
				sm.setDataLen((byte) 28);
				sm.setDataNum(new byte[] { 0, 7 });
				sm.setFromPos(new byte[] { 0, 0 });
				new Thread() {
					public void run() {
						IoSession session = MessageHandler.sessions.get(device.getIp());
						if (session == null || (!session.isConnected())) {
							Display.getDefault().syncExec(new Runnable() {
								public void run() {
									showError();
								}
							});

						} else {
							session.write(sm);
							Display.getDefault().syncExec(new Runnable() {
								public void run() {
									showOK();
								}
							});
						}
					};
				}.start();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		Button startBtn = new Button(footer, SWT.NONE);
		startBtn.setLayoutData(gridData);
		startBtn.setText("����");
		startBtn.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SendSingle ss = new SendSingle();
				ss.setAdr(Byte.valueOf(device.getId()));
				ss.setFuncCode(com.whck.proto.model.Constants.SINGLE_REGISTER);
				ss.setData(new byte[] { 0, 0, 0, 2 });
				ss.setRegisterAdr(new byte[] { 0, 1 });
				new Thread() {
					public void run() {
						IoSession session = MessageHandler.sessions.get(device.getIp());
						if (session == null || (!session.isConnected())) {
							Display.getDefault().syncExec(new Runnable() {
								public void run() {
									showError();
								}
							});
						} else {
							session.write(ss);
							Display.getDefault().syncExec(new Runnable() {
								public void run() {
									showOK();
								}
							});
						}
					};
				}.start();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		Button stopBtn = new Button(footer, SWT.NONE);
		stopBtn.setText("ֹͣ");
		stopBtn.setLayoutData(gridData);
		stopBtn.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				SendSingle ss = new SendSingle();
				ss.setAdr(Byte.valueOf(device.getId()));
				ss.setFuncCode(com.whck.proto.model.Constants.SINGLE_REGISTER);
				ss.setData(new byte[] { 0, 0, 0, 1 });
				ss.setRegisterAdr(new byte[] { 0, 1 });
				IoSession session = MessageHandler.sessions.get(device.getIp());
				if (session == null || (!session.isConnected())) {
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							showError();
						}
					});
				} else {
					session.write(ss);
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							showOK();
						}
					});
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	public void update(String ip, Object msg) {
		if (msg instanceof RainerData) {
			RainerData rData = (RainerData) msg;

			if (String.valueOf(rData.getId()).equals(device.getId())) {
				System.out.println("RainerData:()()()():" + rData);
				if (1 == rData.getStatus()) {
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							stateBar.setText("��ֹͣ");
						}
					});

				} else if (2 == rData.getStatus()) {
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							String mode = com.whck.rainer.panel.Constants.MODES[rData.getMode() - 1];
							if(mode.equals("��ʱģʽ")||mode.equals("ͬ����ʱ")){
								stateBar.setText(mode + "����:"+rData.getTimedur());
							}else{
								stateBar.setText(mode + "����");
							}
							
						}
					});
				}
				// �����ݿ�
				new Thread() {
					public void run() {
						RainerDataDAO dao = RainerDataDAO.getInstance();
						try {
							dao.save(rData);
						} catch (Exception e) {
							System.out.println("���ݿ��쳣���������ݳ���");
							e.printStackTrace();
						}
					}
				}.start();
				for (Iterator<Control> canvas = showCtrls.values().iterator(); canvas.hasNext();) {
					DashBoard db = (DashBoard) canvas.next();
					db.update(msg);
				}
			}
		}
	}
	
	public void relaTrigger(boolean triggered){
		this.getDevice().setTriggered(triggered);
//		relaChkBtn.setSelection(triggered);
	}
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		// Rectangle bgRect = backgroud.getBounds();
		// return new Point(bgRect.width, bgRect.height);
		return new Point(360, 430);
	}

	@Override
	public void dispose() {
		super.dispose();
		if (font != null) {
			font.dispose();
		}
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	protected void showOK() {
		MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_INFORMATION);
		box.setText("��ʾ");
		box.setMessage("�����ɹ�");
		box.open();
	}

	protected void showError() {
		MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_INFORMATION);
		box.setText("��ʾ");
		box.setMessage("�����ѶϿ�");
		box.open();
	}
}
