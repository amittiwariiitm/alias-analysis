class A
{
	public static void main(String[] args)
	{
		B b;
		B b1;
		B b2;
		B b3;
		int [] arr1;
		int [] arr2;
		arr1 = new int [5];
		arr2 = new int [10];
		arr1 = arr2;
		B z;
		z = new B();
		z.b = z;
		C c;
		B z1, z2;
		z1 = new B();
		z2 = z1;
		b = new B();
		c = new C();
		b1 = b.foo();
		B a1 = b.b;
	
		b2 = c.foo();
		b3 = c.foo1(b);
	}
}
class B
{
	B b;
	public B foo()
	{
		B b;
		b = this;
		return b;
	}	
}
class C extends B
{
	B c1;
	public B foo1(B b)
	{
		c1 = b;
		return c1;
	}
}
