  class Main {
  	public static void main (String [] args) {
  		A a1 = new A();
  		A a2 = new A();
  		A a3 = new A();
  		A r1 = new A();
  		A r2 = new A();
  		A r3 = new A();
  		B b1 = new B();
  		B b2 = new B();
  		B b3 = new B();
  		C c1 = new C();
  		C c2 = new C();
  		C c3 = new C();
  		F g1 = new F();
  		F g2 = new F();
  		int x,y,z,j,k;
  		boolean p,q,r;
  		int[] arr = new int[20];
  		x = arr[0];
  		a1.f2 = x;
  		a2.f2 = a1.f2;
  		p = true;
  		q = false;
  		for(j = 0; j<20; j++) {
  			for(k = 0;k<30; k++) {
  				if(p) {
  					g1 = g2;
  					r1.f1 = g1;
  					g2 = r2.f1;
  					r1 = c1.bCall(c2);
  					r2 = c2.aCall(r1);
  					c3.recursive(j);
  					g2 = a1.foobar(g1);
  					b1.bar(g2);
  					g1 = g2.foo();
  					z = a2.barfoo(r1,x);
  					q = c3.boolCheck(p);
  					
  				}
  				else {
  					g2 = new F();
  					r1.f1 = g2;
  					g2 = r2.f1;
  					r1 = a2.aCall(r1);
  					r2 = b1.bCall(r1);
  					r2.recursive(k);
  					g2 = a1.foobar(g1);
  					b3.bar(g2);
  					g2 = g2.foo();
  					z = a1.barfoo(r1,x);
  					q = c3.boolCheck(p);
  				}
  			}
  		}
  		
  		
  	}
  }
    
  class A {
  	F f1;
  	int f2;
  	void bar(F x) {
  		F y = this.f1;
  		System.out.println("hello world");
  	}
  	F foobar(F x) {
  		F y = this.f1;
  		return y;
  	}
  	int barfoo(A x,int z) {
  		F y = this.f1;
  		return z;
  	}
  	A aCall(A x) {
  		return x;
  	}
  	A bCall(A x) {
  		return x;
  	}
  	
  	void recursive(int i) {
		  if(i==0) {
			  i = i - 1;
			  A a = new C();
			  F g = new F();
			  a.f1 = a.foobar(g);
			  recursive(i);
		  }
		  
	  }
  }
  
  class B extends A{
	  void bar(F x) {
	  	F y = new F();;
	  	System.out.println("hello world");
	  }
	  F foobar(A x) {
	  		F y = new F();
	  		return y;
	  }
	  int barfoo(A x,int z) {
	  		z = x.f2;
	  		return z;
	  }
	  
	  
	  
	  A bCall(A x) {
		    B b = new B();
	  		return b;
	  	}
  }
  
  class C extends B{
	  void bar(F x) {
		  this.f1 = new F();
		  System.out.println("hello world");
	  }
	  F foobar(F x) {
		  F z = new F();
		  return z;
	  }
	  int barfoo(A x,int z) {
	  		x.f2 = z;
	  		return z;
	  }
	  
	  boolean boolCheck(boolean t) {
		  A a = new A();
		  A b = a;
		  return t;
	  }
	  
	  A aCall(A x) {
		  C c = new C();
	  	  return c;
	  	}
	  
  }
  
  class F {
  	F foo() {
  		return this;
  	}
  }
