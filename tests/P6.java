class P6{
    public static void main (String[] args){
        A a1;
        A a2;
        B b1;
        C c1;
        C c2;
        D d1;

        A f0;
        B f1;
        C f2;
        D f3;

        int k;
        int[] somerray;
        boolean boo;

        c1 = new D();
        c1 = new C();

        a2 = a1;
        a2 = b1;

        d1 = new D();

        c2 = d1;

        a1 = new A();
        b1 = new B();

        a1.f0 = c1;
        c1.f0  = b1;

        b1.f1 = d1;
        d1.f1 = a1;
        d1.f1 = b1;

        f2 = a1.f0;
        f3 = b1.f1;
        f1 = a1.f0.f0;
        f0 = b1.f1.f1;

        k=9;
        somerray = new int[k];
        k = 8;
        boo = k>=0;

        while(boo){
            somerray[k] = k;
            k = k-1;
            boo = k >=0;
        }
    }
}


class C{
    B f0;
}
class D extends C{
    A f1;
}

class A{
    C f0;
}

class B extends A {
    D f1;
}