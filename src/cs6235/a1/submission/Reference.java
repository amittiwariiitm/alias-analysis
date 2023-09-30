package cs6235.a1.submission;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import polyglot.ast.New;
import soot.SootClass;
import soot.SootField;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.Stmt;

public abstract class Reference {

	public final int id;
	static int counter = 0;
	
	public static final HashMap<Stmt,Reference> refMap = new HashMap<>();
	
	protected Reference() {
		this.id = counter++;
	}
	
	/**
	 * Factory Method to ensure the ref is created once per invokeStmt
	 * @param invokeStmt
	 * @return
	 */
	public static Reference getRef(Stmt stmt) 
	{
		if (stmt instanceof InvokeStmt) {			
			InvokeStmt invokeStmt = (InvokeStmt) stmt; 
			if(refMap.get(invokeStmt)!= null)
				return refMap.get(invokeStmt);
			else
			{
				ObjectReference or = new ObjectReference(invokeStmt);
				return or;
			}
		} else if (stmt instanceof AssignStmt) {
			AssignStmt aStmt = (AssignStmt) stmt;
			if(refMap.get(aStmt)!=null)
				return refMap.get(aStmt);
			else
			{
				ArrayReference ar = new ArrayReference(aStmt);
				return ar;
			}
		}
		assert(false); 
		return null;
	}
}
