package com.whck.rainer;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.whck.proto.handler.MessageHandler;
import com.whck.rainer.network.Server;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public static Server server;
	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}
@Override
	public void postWindowCreate() {
		// TODO Auto-generated method stub
		super.postWindowCreate();
		getWindowConfigurer().getWindow().getShell().setMaximized(true);
	}
	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(1000, 700));
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(false);
	}
	@Override
	public void postWindowOpen() {
		ZoneListView zoneListView =(ZoneListView)Activator.getDefault().getWorkbench().
		getActiveWorkbenchWindow().getActivePage().findView("com.whck.rainer.zonelistview");
		ShowView showView =(ShowView)Activator.getDefault().getWorkbench().
				getActiveWorkbenchWindow().getActivePage().findView("com.whck.rainer.showview");
		
		server=Server.getInstance();
		if(!server.start()){
			System.out.println("����������ʧ�ܣ�");
		}
		
		MessageHandler handler=(MessageHandler)server.getAcceptor().getHandler();
		handler.setShowView(showView);
		handler.setZoneListView(zoneListView);
		
	}
}
