package com.whck.msf.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.whck.msf.Activator;
import com.whck.msf.AddVariableDialog;
import com.whck.msf.ShowView;
import com.whck.msf.ZoneListView;
import com.whck.msf.model.Device;

public class VariableAddHandler extends AbstractHandler{
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		Object item = selection.getFirstElement();
		if(item instanceof Device){
			AddVariableDialog d = new AddVariableDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
			Device device=(Device)item;
			d.setDevice(device);
			d.setFlag(0);
			int code = d.open();
			if (code == Window.OK) {
				ZoneListView zoneListView = (ZoneListView) Activator.getDefault().getWorkbench()
						.getActiveWorkbenchWindow().getActivePage().findView("com.whck.msf.zonelistview");
				zoneListView.getTreeViewer().refresh();
				
				ZoneListView.getInitalInput();
				ShowView.refresh();
			}
		}
		return null;
	}
}
