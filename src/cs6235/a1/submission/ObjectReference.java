package cs6235.a1.submission;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import soot.SootClass;
import soot.SootField;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.SpecialInvokeExpr;

public class ObjectReference extends Reference {
	public final InvokeStmt invokeStmt;
	public final HashMap<SootField,Set<Reference>> fieldMap = new HashMap<>();

	protected ObjectReference(InvokeStmt invokeStmt) {
		this.invokeStmt = invokeStmt;
		refMap.put(invokeStmt, this); 
		
		InvokeExpr ie = invokeStmt.getInvokeExpr();
		assert (ie instanceof SpecialInvokeExpr);
		SootClass sc = ie.getMethod().getDeclaringClass();
		
		
		for(SootField sf : PointsToAnalysis.getAllField(sc))
		{
			this.fieldMap.put(sf, new HashSet<>());
		}
	}

}
