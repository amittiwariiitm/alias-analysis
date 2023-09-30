package cs6235.a1.submission;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import soot.Local;
import soot.RefLikeType;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

public class MethodGetter {

	private static Set<SootMethod> allMethods = null;

	/**
	 * get all reachable methods from the main method 
	 * @return
	 */
	public static Set<SootMethod> getAllMethods()
	{
		if(allMethods!=null)
		{
			return allMethods;
		}
		
		SootMethod mainMethod = Scene.v().getMainMethod();
		allMethods = new HashSet<>();
		allMethods.addAll(MethodGetter.callGraphTraversal(mainMethod,new HashSet<>()));
		return allMethods;
		
	}

	private static Set<SootMethod> callGraphTraversal(SootMethod sm, Set<SootMethod> visited){
		
		if (!sm.getDeclaringClass().isApplicationClass()) {
			return new HashSet<>();
		}
		if(visited.contains(sm) || !sm.hasActiveBody())
		{
			return new HashSet<>();
		}
		
		visited.add(sm);
		
		//calling all invokeStmt & visiting them 
		
		CallGraph cg = Scene.v().getCallGraph();
		Set<SootMethod> retSet = new HashSet<>();
		retSet.add(sm);
		
		
		for(Unit u : sm.getActiveBody().getUnits())
		{
			boolean flag = false;
			
			if(u instanceof AssignStmt)
			{
				AssignStmt a = (AssignStmt)u;
				if(a.getRightOp() instanceof VirtualInvokeExpr)
				{
					flag = true;
				}
			}
			
			else if(u instanceof InvokeStmt && !(((InvokeStmt)u).getInvokeExpr() instanceof SpecialInvokeExpr))
			{
				flag = true;
			}
			
			if (flag) {

				Iterator<Edge> targetEdges = cg.edgesOutOf(u);

				while (targetEdges.hasNext()) {
					Edge e = targetEdges.next();
					SootMethod nextMethod = e.tgt();
					retSet.addAll(callGraphTraversal(nextMethod, visited));

				}
			}

		}

		return retSet;
	}
	


	public static Set<SootMethod> getAllTargetMethods(Stmt s) {

		boolean flag = false;
		Set<SootMethod> retSet = new HashSet<>();

		if (s instanceof AssignStmt) 
		{
			AssignStmt st = (AssignStmt) s;
			if (st.getRightOp() instanceof VirtualInvokeExpr) 
			{
				flag = true;
			}
		} 
		else if (s instanceof InvokeStmt && !((s.getInvokeExpr() instanceof SpecialInvokeExpr))) 
		{
			flag = true;
		}

		if (flag)
		{
			CallGraph cg = Scene.v().getCallGraph();
			Iterator<Edge> targetEdges = cg.edgesOutOf(s);

			while (targetEdges.hasNext()) 
			{
				Edge e = targetEdges.next();
				SootMethod nextMethod = e.tgt();
				retSet.add(nextMethod);
			}
		}

		return retSet;
	}

	
	public static List<Local> getAllArgs(Stmt s)
	{
		List<Local> argList = new ArrayList<>();
		
		if(s instanceof InvokeStmt && !(s.getInvokeExpr() instanceof SpecialInvokeExpr))
		{
			InvokeExpr ie = ((InvokeStmt)s).getInvokeExpr();
			 
			for(Value v : ie.getArgs())
			 {
				 if(v.getType() instanceof RefLikeType)
				 {
					 argList.add((Local)v);
				 }
			 }
		}
		else if(s instanceof AssignStmt )
		{
			AssignStmt as = (AssignStmt)s;
			
			if(as.getRightOp() instanceof VirtualInvokeExpr)
			{
				
				for(Value v : ((VirtualInvokeExpr)as.getRightOp()).getArgs())
				{
					if(v.getType() instanceof RefLikeType)
					{
						argList.add((Local)v);
					}
				}
			}
		}
		
		return argList;
	}
	
	public static Set<ReturnStmt> getRetStmt(SootMethod sm)
	{
		Set<ReturnStmt> retStmt = new HashSet<>();
		
		for(Unit u : sm.getActiveBody().getUnits())
		{
			if(u instanceof ReturnStmt)
				{
					retStmt.add((ReturnStmt) u);
				}
		}
		
		return retStmt;
	}

	public static List<Local> getAllParameters(SootMethod sm)
	{
		List<Local> paramList = new ArrayList<>();
		
		for(Local l : sm.getActiveBody().getParameterLocals())
		{
			if(l.getType() instanceof RefLikeType)
			{
				paramList.add(l);
			}
				
		}
		
		return paramList;
	}

}
