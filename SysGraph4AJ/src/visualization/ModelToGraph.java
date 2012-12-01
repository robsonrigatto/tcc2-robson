package visualization;

import java.util.Vector;

import model.SysElement;
import model.SysAdvice;
import model.SysAspect;
import model.SysClass;
import model.SysMethod;
import model.SysPackage;
import model.SysRoot;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class ModelToGraph {

	public static DelegateTree<SysElement, Float> delegateTree_justFirstChildren(SysElement root) {
		DelegateTree<SysElement, Float> dt = new DelegateTree<SysElement, Float>();
		dt.addVertex(root);
		for(SysElement e : root.getChildElements())
			dt.addChild((float)dt.getEdgeCount(), root, e);
				return dt;
	}

	public static DelegateTree<SysElement, Float> delegateTree_fullGraph(SysElement root) {
		DelegateTree<SysElement, Float> dt = new DelegateTree<SysElement, Float>();
		dt.addVertex(root);
		dt = putAllChildren(dt, root);
		return dt;
	}

	public static DelegateTree<SysElement, Float> putAllChildren_SysRoot(DelegateTree<SysElement, Float> dt, SysRoot e){
		if(e != null){
			for(SysElement e1 : e.getPackages()){
				dt.addChild((float)dt.getEdgeCount(), e, e1);
				dt = putAllChildren(dt, e1);
			}
		}
		return dt;
	}

	public static DelegateTree<SysElement, Float> putAllChildren(DelegateTree<SysElement, Float> dt, SysElement e){
		if(e!=null){
			for(SysElement e1 : e.getChildElements()){
				dt.addChild((float)dt.getEdgeCount(), e, e1);
				dt = putAllChildren(dt, e1);
			}
		}
		return dt;
	}

	public static EspecialEdgesTable<SysElement, Float> getEspecialEdges(SysRoot r, Graph g){
		if(r!=null){
			EspecialEdgesTable<SysElement, Float> et = new EspecialEdgesTable<SysElement, Float>();
			for(SysPackage p : r.getPackages()){
				et.add(getEspecialEdges(p,g));
			}
			return et;
		}
		else return null;
	}

	private static EspecialEdgesTable<SysElement, Float> getEspecialEdges(SysPackage pack, Graph g) {
		if(pack!=null){
			EspecialEdgesTable<SysElement, Float> et = new EspecialEdgesTable<SysElement, Float>();
			for(SysPackage p : pack.getPackages()){
				et.add(getEspecialEdges(p,g));
			}
			for(SysClass c : pack.getClasses()){
				et.add(getEspecialEdges(c,g));
			}
			for(SysAspect a : pack.getAspects()){
				et.add(getEspecialEdges(a,g));
			}
			return et;
		}
		return null;
	}

	private static EspecialEdgesTable<SysElement, Float> getEspecialEdges(SysAspect c, Graph g) {
		EspecialEdgesTable<SysElement, Float> e = getEspecialEdges((SysClass) c, g);
		if(e==null) e = new EspecialEdgesTable<SysElement, Float>();
		for(SysAdvice ad : c.getAdvice()){
			e.add(getEspecialEdges(ad,g));
		}
		return e;
	}

	private static EspecialEdgesTable<SysElement, Float> getEspecialEdges(SysClass c, Graph g) {
		if(c!=null){
			EspecialEdgesTable<SysElement, Float> et = new EspecialEdgesTable<SysElement, Float>();
			for(SysMethod m : c.getMethods()){
				et.add(getEspecialEdges(m,g));
			}
			return et;
		}
		return null;
	}

	private static EspecialEdgesTable<SysElement, Float> getEspecialEdges(SysMethod m, Graph g) {
		if(m!=null){
			EspecialEdgesTable<SysElement, Float> et = new EspecialEdgesTable<SysElement, Float>();
			for(SysMethod s : m.getCalls()){
				et.add((float)g.getEdgeCount()+1,m,s);
			}
			return et;
		}
		return null;
	}

	private static EspecialEdgesTable<SysElement, Float> getEspecialEdges(SysAdvice m, Graph g) {
		EspecialEdgesTable<SysElement, Float> e = getEspecialEdges((SysMethod)m,g);
		if(e==null) e= new EspecialEdgesTable<SysElement,Float>();
		for(SysMethod me: m.getAffecteds()){
			e.add((float)g.getEdgeCount()+1, m, me);
		}
		return e;
	}

	public static void addEspecialEdges(Forest<SysElement, Float> f, EspecialEdgesTable<SysElement, Float> et){
		if(et!=null && f!=null){
			Vector<SysElement> from = et.getFrom();
			Vector<SysElement> to = et.getTo();
			Vector<Float> edge = et.getEdge();
			for(int i=0;i<edge.size();i++){
				f.addEdge((float)f.getEdgeCount()+1.5f, from.get(i), to.get(i));
			}
		}else System.err.println("et==null || f==null");
	}

	public static DelegateForest<SysElement, Float> tree_to_forest(DelegateTree dt){
		if(dt==null)return null;
		DelegateForest df = new DelegateForest();
		for(Object v1 : dt.getVertices()){
			df.addVertex(v1);
		}
		Pair p;
		for(Object e1 : dt.getEdges()){
			p = dt.getEndpoints(e1);
			df.addEdge(e1, p.getFirst(), p.getSecond());
		}
		return df;		
	}






}
