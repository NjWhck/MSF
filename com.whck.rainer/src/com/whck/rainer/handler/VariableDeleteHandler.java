package com.whck.rainer.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.whck.rainer.Activator;
import com.whck.rainer.ZoneListView;
import com.whck.rainer.model.Device;
import com.whck.rainer.model.Variable;
import com.whck.rainer.model.Zone;
import com.whck.rainer.util.XmlUtil;

public class VariableDeleteHandler extends AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		Object item = selection.getFirstElement();

		if (item instanceof Variable) {
			Variable temp = (Variable) item;
			MessageBox box = new MessageBox(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
					SWT.OK | SWT.CANCEL | SWT.ICON_WARNING);
			box.setText("��ʾ");
			box.setMessage("ȷ��ɾ��" + temp.getName() + "������");
			int code = box.open();

			if (code == SWT.OK) {

				XmlUtil xmlUtil = null;
				try {
					xmlUtil = XmlUtil.getInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}

				// ɾ��treeViewer�е�Variable�ڵ�
				Device device = (Device) (temp.getParent());
				device.remove(temp);
				Zone zone = (Zone) device.getParent();
				// ɾ��xml�ļ��е�Variable�ڵ�
				xmlUtil.deleteVariable(zone.getName(), device.getId(), temp.getName());

				ZoneListView zoneListView = (ZoneListView) Activator.getDefault().getWorkbench()
						.getActiveWorkbenchWindow().getActivePage().findView("com.whck.rainer.zonelistview");
				zoneListView.getTreeViewer().refresh();

			}

		}
		return null;
	}
}
