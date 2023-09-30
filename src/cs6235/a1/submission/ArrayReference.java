package cs6235.a1.submission;

import soot.jimple.AssignStmt;

public class ArrayReference extends Reference {
	public final AssignStmt assignStmt;
	
	protected ArrayReference(AssignStmt assignStmt) {
		this.assignStmt = assignStmt;
		refMap.put(assignStmt, this);
	}

}
