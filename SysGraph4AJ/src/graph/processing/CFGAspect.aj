package graph.processing;

import graph.gui.CFGUIHelper;
import graph.model.CFGNode;
import gui.MainWindow;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;

import model.SysAspect;

public aspect CFGAspect {
	
	pointcut addAspect(model.SysAspect a, model.SysPackage p) : execution(public void add(model.SysAspect)) && args(a) && target(p);
	
	pointcut reloadAspectList(gui.MainWindow m) : execution(public void analyse()) && target(m);
	
	pointcut reloadCFGNodeList(CFGBuilder b, Method m) : execution(public CFGNode build(Method)) && args(m) && target(b);
	
	after(model.SysAspect a, model.SysPackage p): addAspect(a, p) {
		CFGUIHelper.activateAspectList.add(a);
		
		for(CFGNode analysedNode : CFGUIHelper.activateCFGNodeList) {
		
			try {
				CFGUIHelper.addAspectEdgesToSysGraph(analysedNode, MainWindow.getInstance());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	after(gui.MainWindow m) : reloadAspectList(m) {
		CFGUIHelper.activateAspectList = new ArrayList<SysAspect>();
		CFGUIHelper.activateCFGNodeList = new HashSet<CFGNode>();
	}
	
	after(CFGBuilder b, Method m) : reloadCFGNodeList(b, m) {
		CFGUIHelper.activateCFGNodeList = new HashSet<CFGNode>();
	}
}
