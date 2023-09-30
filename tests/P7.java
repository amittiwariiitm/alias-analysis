class P7{
    public static void main(String[] args){

        A o1;
        A o2;
        A o3;
        A tp;
        A kk;
        A ll;

        o1= new A();
        o2 = new A();
        o3 = new A();

        o1.f0 = new A();
        tp = o1.f0;
        kk = o1.foo(o1);
        ll = tp.bar(o3);
        ll = tp.bar(o2);
    }
}

class A{
    A f0;

    A foo(A a1){
        A p1;
        A p2;
        A tmp;
        A tmp2;
        tmp = a1.f0;
        tmp2 = tmp.f0;
        return tmp2;
    }

    A bar(A s){
        f0= s;
        return s;
    }
}