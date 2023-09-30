class C 
{
	public static void main(String[] args)
	{
		B b1;
		D d2;
		D d1;
		b1 = new B();
		d1 = b1.foo();
		d2 = b1.foo1();
	}
}
class A
{
	D d;
	public D foo()
	{
		d = new D();
		return d;
	}
	public D foo1()
	{
		return d;
	}
}

class B extends A
{
	public D bar()
	{
		D d;
		d = new D();
		return d;
	}
}
class D
{
	int s;
	public int f1()
	{
		s = 5;
		return s;
	}
}

