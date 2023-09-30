package cs6235.a1.submission;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cs6235.a1.AnalysisBase;
import cs6235.a1.Query;
import java_cup.assoc;
import soot.*;
import soot.jimple.DefinitionStmt;
import soot.jimple.Expr;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.NewArrayExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.VirtualInvokeExpr;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.util.Chain;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;

/*
 To do :
 
 1.Get a set of all the methods reachable from main /
 2.Get a set of all the methods targeted by the invoke statement / 
 3.Implement APPLY STATEMENT method 
 4.Implement PROCESS method /
 5.Get a set of all INVOKE STATEMENT for a given method /
 6.Write a code to maintain Worklist /
 7.Obtain the variable which captures the return value for the method 
 8.Obtain the list of parameters given for a method /
 9.Obtain the list of args given for an envokation /
 
 */

public class PointsToAnalysis extends AnalysisBase {

	static Set<SootMethod> worker = new HashSet<>();
	static final HashMap<SootMethod, HashMap<Local, Set<Reference>>> stackSummaryMap = new HashMap<>();
	static final HashMap<SootMethod, Set<Stmt>> methodToInvokationsMap = new HashMap<>();
	static final HashMap<Stmt, SootMethod> stmtToEnclosingMethodMap = new HashMap<>();

	@Override
	protected void internalTransform(String phaseName, Map<String, String> options) {

		SootMethod mainM = Scene.v().getMainMethod();
		initializeHeap();
		initializeStackSummary();
		initializeMethodToInvokations();
		initializeStmtToEnclosingMethodMap();
		runPta();
		printRhoSigma();

	}

	/**
	 * iterate over all the methods, until fixed point.
	 */
	private static void runPta() {
		boolean change;

		do {
			change = false;
			for (SootMethod sm : MethodGetter.getAllMethods()) {
				change = change | processMethod(sm);
			}
		} while (change);
	}

	/**
	 * process one method at a time(ANALYSING THE METHOD) 1.take all the arguments
	 * from all the inwocations of this method & meet them with the parameters.
	 * 2.process all the statements until a fixed point is reached.
	 * 
	 * note: we are taking meet of args & params of foo() when we analyze the foo()
	 * function by looking back what all are the callsites of foo() are like : x =
	 * y.foo(). The returnReceiver(x) is analyzed when we traverse its enclosing
	 * method.
	 * 
	 * @param sm
	 * @return
	 */
	private static boolean processMethod(SootMethod sm) {
		boolean runIt = false;
		// Step 1 :

		List<Local> paramList = MethodGetter.getAllParameters(sm);

		for (Stmt currentStmt : methodToInvokationsMap.get(sm)) {
			List<Local> argList = MethodGetter.getAllArgs(currentStmt);

			SootMethod enclosingMethod = stmtToEnclosingMethodMap.get(currentStmt);

			int i = -1;
			HashMap<Local, Set<Reference>> paramRhoMap = stackSummaryMap.get(sm);
			HashMap<Local, Set<Reference>> argRhoMap = stackSummaryMap.get(enclosingMethod);

			for (Local arg : argList) {
				i++;
				Local param = paramList.get(i);

				runIt = runIt | paramRhoMap.get(param).addAll(argRhoMap.get(arg));

			}
			
			VirtualInvokeExpr vie = null;
			
			if(currentStmt instanceof InvokeStmt)
			{
				vie = (VirtualInvokeExpr) currentStmt.getInvokeExpr();
					
			}
			else if(currentStmt instanceof AssignStmt)
			{
				vie = (VirtualInvokeExpr) ((AssignStmt) currentStmt).getRightOp();
			}
			
			if(vie!=null)
			{
				Local l =  (Local) vie.getBase();
				runIt  = runIt | getRefFromRho("this", paramRhoMap).addAll(argRhoMap.get(l));
			}
		}
		

		// Step 2 :
		do {
			boolean localFlag = false;
			for (Unit u : sm.getActiveBody().getUnits()) {
				boolean temp = applyStatement((Stmt) u, sm);
				localFlag = localFlag | temp;
			}
			if (!localFlag) {
				break;
			} else {
				runIt = true;
			}

		} while (true);

		return runIt;
	}

	private static boolean applyStatement(Stmt stmt, SootMethod enclosingMethod) {
		boolean change = false;
		HashMap<Local, Set<Reference>> localRhoMap = stackSummaryMap.get(enclosingMethod);
		if (stmt instanceof AssignStmt) {
			AssignStmt as = (AssignStmt) stmt;
			Value lhs = as.getLeftOp();
			if (!(lhs.getType() instanceof RefLikeType)) {
				return change;
			}
			Value rhs = as.getRightOp();

			if (lhs instanceof ArrayRef) {
				if (rhs instanceof Local) {
					// a[i] = l;
					// Do-nothing
				}

			}

			else if (lhs instanceof FieldRef) {
				if (rhs instanceof Local) {
					// a.f = l;
					if (lhs instanceof InstanceFieldRef) {
						Value a = ((InstanceFieldRef) lhs).getBase();
						SootField f = ((FieldRef) lhs).getField();
						Value l = rhs;

						for (Reference lhsRef : localRhoMap.get(a)) {
							if (lhsRef instanceof ObjectReference) {
								try {									
									change = change | ((ObjectReference) lhsRef).fieldMap.get(f).addAll(localRhoMap.get(l));
								} catch (NullPointerException e) {
									System.err.println(e.getMessage());
								}
							}
						}
					}

				}

			}

			else if (lhs instanceof Local) {
				if (rhs instanceof FieldRef) {
					// l = a.f;
					FieldRef fr = (FieldRef) rhs;
					if (fr instanceof InstanceFieldRef) {
						Value a = ((InstanceFieldRef) fr).getBase();
						SootField f = fr.getField();
						Set<Reference> lhsSet = localRhoMap.get(lhs);

						for (Reference rhsRef : localRhoMap.get(a)) {
							if (rhsRef instanceof ObjectReference) {
								try {									
									change = change | lhsSet.addAll(((ObjectReference) rhsRef).fieldMap.get(f));
								} catch (NullPointerException e) {
									System.err.println(e.getMessage());
								}
							}
						}
					}

				}

				else if (rhs instanceof ArrayRef) {
					// l = a[f];
					// Do-nothing
				}

				else if (rhs instanceof Local) {
					// l = l';
					change = change | localRhoMap.get(lhs).addAll(localRhoMap.get(rhs));
				}

				else if (rhs instanceof Expr) {
					if (rhs instanceof VirtualInvokeExpr) {
						// l = l.foo();
						for(SootMethod sm : MethodGetter.getAllTargetMethods(stmt))
						{
							for(ReturnStmt rs : MethodGetter.getRetStmt(sm))
							{
								if(rs.getOp() != null && rs.getOp() instanceof Local)
								{
									Local r1 = (Local) rs.getOp();
									change = change | localRhoMap.get(lhs).addAll(stackSummaryMap.get(sm).get(r1));
								}
							}
						}
						
					}

					else if (rhs instanceof SpecialInvokeExpr) {
						// l = new l();
						// change = change | localRhoMap.get(lhs).add(Reference.getRef(stmt));
						// Not Required
					}

					else if (rhs instanceof NewArrayExpr) {
						// l = new int [5];
						change = change | localRhoMap.get(lhs).add(Reference.getRef(stmt));
					}
				}
			}

		}

		else if (stmt instanceof InvokeStmt) {
			InvokeStmt is = (InvokeStmt) stmt;
			InvokeExpr ie = (InvokeExpr) is.getInvokeExpr();

			if (ie instanceof SpecialInvokeExpr) {
				// l.<A: init>();

				if (((SpecialInvokeExpr) ie).getBase() instanceof Local) {
					Local l = (Local) ((SpecialInvokeExpr) ie).getBase();
					change = change | localRhoMap.get(l).add(Reference.getRef((is)));
				}

			}

			else if (ie instanceof VirtualInvokeExpr) {
				// l.foo();
				// Do-nothing
			}
		}

		return change;
	}

	public static Set<Local> getRefLocals(SootMethod sm) {
		Set<Local> retLocal = new HashSet<>();

		for (Local l : sm.getActiveBody().getLocals()) {
			if (l.getType() instanceof RefLikeType) {
				retLocal.add(l);
			}
		}

		return retLocal;
	}

	public static String getFullName(SootMethod sm) {
		String name = "";
		name += sm.getDeclaringClass().getName() + "." + sm.getName();
		return name;
	}

	/**
	 * returns all the fields of the class & the relevant superclasses
	 * 
	 * @return
	 */
	public static Set<SootField> getAllField(SootClass sc) {
		Set<SootField> temp = new HashSet<>();

		do {
			for (SootField sf : sc.getFields()) {
				if (sf.getType() instanceof RefLikeType) {
					temp.add(sf);
				}

			}
			sc = sc.getSuperclass();
		} while (sc.hasSuperclass());

		return temp;

	}

	public static void initializeStmtToEnclosingMethodMap() {
		for (SootMethod sm : MethodGetter.getAllMethods()) {
			for (Unit u : sm.getActiveBody().getUnits()) {

				stmtToEnclosingMethodMap.put((Stmt) u, sm);
			}
		}
	}

	public static void initializeMethodToInvokations() {
		methodToInvokationsMap.put(Scene.v().getMainMethod(), new HashSet<>());
		for (SootMethod sm : MethodGetter.getAllMethods()) {
			for (Unit u : sm.getActiveBody().getUnits()) {
				Stmt s = (Stmt) u;
				for (SootMethod target : MethodGetter.getAllTargetMethods(s)) {
					Set<Stmt> vs;
					if (!methodToInvokationsMap.containsKey(target)) {
						vs = new HashSet<>();
						methodToInvokationsMap.put(target, vs);
					} else {
						vs = methodToInvokationsMap.get(target);
					}

					vs.add(s);

				}
			}
		}
	}

	public static void initializeStackSummary() {
		for (SootMethod sm : MethodGetter.getAllMethods()) {
			HashMap<Local, Set<Reference>> kuchBhi = new HashMap<>();
			for (Local l : getRefLocals(sm)) {
				kuchBhi.put(l, new HashSet<>());
			}

			stackSummaryMap.put(sm, kuchBhi);
		}

	}

	/**
	 * initializes all the invokeStmt with a reference which further invokes the
	 * constructor of Reference which further initializes the fieldMap
	 */

	public static void initializeHeap() {
		for (SootMethod sm : MethodGetter.getAllMethods()) {
			if (!sm.hasActiveBody()) {
				continue;
			}
			for (Unit u : sm.getActiveBody().getUnits()) {
				if (u instanceof InvokeStmt) {
					InvokeStmt is = (InvokeStmt) u;
					if (is.getInvokeExpr() instanceof SpecialInvokeExpr) {
						Reference.getRef(is);
					}
				} else if (u instanceof AssignStmt) {
					AssignStmt as = (AssignStmt) u;
					if (as.getRightOp() instanceof NewArrayExpr) {
						Reference.getRef(as);
					}
				}

			}

		}
	}

	@Override
	public String getResultString() {

		String result = "";
		Map<String, List<Query>> queries = this.getQueries();

		for (String methodname : queries.keySet()) {
			SootMethod sm = getMethodFromName(methodname);
			HashMap<Local, Set<Reference>> rhoMap = stackSummaryMap.get(sm);

			for (Query q : queries.get(methodname)) {
				Set<Reference> lhsSet = getRefFromRho(q.getLhs().trim(), rhoMap);
				Set<Reference> rhsSet = getRefFromRho(q.getRhs().trim(), rhoMap);
				if (lhsSet == null) {
					lhsSet = new HashSet<>();
					for (Reference r : getRefFromRho("this", rhoMap)) {
						if (r instanceof ObjectReference) {
							ObjectReference or = (ObjectReference) r;
							try {
								lhsSet.addAll(getRefFromSigma(q.getLhs().trim(), or));
							} catch (NullPointerException e) {
								System.err.println(e.getMessage());
							}
						}
					}
				}
				if (rhsSet == null) {
					rhsSet = new HashSet<>();
					for (Reference r : getRefFromRho("this", rhoMap)) {
						if (r instanceof ObjectReference) {
							ObjectReference or = (ObjectReference) r;
							try {
								rhsSet.addAll(getRefFromSigma(q.getRhs().trim(), or));
							} catch (NullPointerException e) {
								System.err.println(e.getMessage());
							}
						}
					}
				}
				
				boolean found = false;
				if (q.getLhs().equals(q.getRhs())) {
					found = true;
				}
				for (Reference refL : lhsSet) {
					if (rhsSet.contains(refL)) {
						found = true;
						break;
					}
				}
				result += (found) ? "YES\n" : "NO\n";
				// )?"YES\n":"NO\n";
			}
		}
		return result;
	}

	public static Set<Reference> getRefFromRho(String localName, HashMap<Local, Set<Reference>> rhoMap) {
		for (Local l : rhoMap.keySet()) {
			if (l.getName().equals(localName)) {
				return rhoMap.get(l);
			}
		}

		return null;
	}
	
	public static Set<Reference> getRefFromSigma(String fieldName, ObjectReference or) {
		for (SootField f : or.fieldMap.keySet()) {
			if (f.getName().equals(fieldName)) {
				return or.fieldMap.get(f);
			}
		}
		return null;
	}

	public SootMethod getMethodFromName(String methodName) {
		for (SootMethod sm : stackSummaryMap.keySet()) {
			String fullName = getFullName(sm);

			if (methodName.equals(fullName)) {
				return sm;
			}
		}

		return null;
	}

	public void printRhoSigma() {
		for (SootMethod sm : stackSummaryMap.keySet()) {
			System.err.println(getFullName(sm) + ":::");
			for (Local l : stackSummaryMap.get(sm).keySet()) {
				System.err.print("\t" + l.getName() + "-- {");
				for (Reference ref : stackSummaryMap.get(sm).get(l)) {
					System.err.print("R" + ref.id + ";");
				}
				System.err.println("}");
			}
		}
		for (Reference ref : Reference.refMap.values()) {
			System.err.println("R" + ref.id);
			if (ref instanceof ObjectReference) {
				ObjectReference obj = (ObjectReference) ref;
				for (SootField f : obj.fieldMap.keySet()) {
					System.err.print("\t (f)" + f.getName() + "--{");
					for (Reference r : obj.fieldMap.get(f)) {
						System.err.print("R" + r.id + ";");
					}
					System.err.println("}");
				}
			}
		}
	}
}
