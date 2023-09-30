class P5 {
    public static void main (String[] args){
        B objB;
        A objA;
        A objA1;
        A objA2;
        A objA3;
        int num;

        objB = new B(); //o1
        objA = new A(); //o2

        num = 10;
        // o1
        objA1 = objB.foo(objA, num, objB);
        // o2
        objA2 = objA1.get();
        num = 20;
        // o1, o3
        objA3 = objA2.foo(objA, num, objB);
    }
}

class A{
    A fa;
    int k;

    public A get(){
        return fa;
    }

    //o2

    // o2
    // o1
    public A foo(A a1, int nn, A b1){
        B retB;
        fa = b1;
        retB = new B(); //o3
        retB.fa = a1;
        retB.fb = a1;
        k = nn;
        return retB;
        /*
        o2 fa = o1
        o3 fa = o2
        o3 fb = o2
        */

        //o3
    }
}

class B extends A{
    A fb;
    //o1
    public A get(){
        return fb;
        // o2
    }


    // o1

    // o2 
    // o1 
    public A foo(A a1, int nn, A b1){
        fb = a1;
        fa = b1;
        k = nn;
        return b1;
        /* 
        o1, fb = o2
        o1, fa = o1
        */

        //o1
    }
}

