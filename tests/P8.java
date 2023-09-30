class P8{
    public static void main(String[]args) {
        A a1;
        A a2;
        A a3;
        A a4;
        A a5;
        B b1;
        B b2;
        B b3;

        a1 = new A();
        a2 = new A();
        b1 = new B();
        b2 = new B();

        a3 = a1;
        a3 = b2;
        b3 = b1;

        a3.a = a2;
        b3.b = b1;
        b3.a = a3.a;

        a4 = b3.a;
        a5 = a3.a;
    }

}

class A{
    A a;
}

class B extends A{
    B b;
}