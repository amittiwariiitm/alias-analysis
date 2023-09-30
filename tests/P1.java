class P1 {
    public static void main (String[] args){
        A objA; 
        B objB;

        A objA2;
        A objA3;
        A objA4;
        objA =  new A(); //o1
        objB = new B(); //o2
        //o3
        objA2 = objB.foo(objA);
        // o1, o2, o3
        objA3 = objA2.foo(objB);
        // o1, o2
        objA4 = objA2.bar();
    }
}

class A {
    A f0;
    //o3 o1

    // o1 o2
    public A foo(A a1){
        f0 = a1;
        /*
        o3 o1, f0 = o1 o2
        */
        return a1;
    }

    // o3
    public A bar(){
        return  f0;
    }
}

class B extends A {
    A bf0;

    //o2

    //o1 o2
    public A foo(A a1){
        A retVal;
        A temp1;
        A temp2;
        retVal = new A(); //o3
        temp1 = retVal.foo(a1);
        temp2 = a1.foo(a1);
        bf0 = a1;
        return retVal;
        /*
        o2, bf0 = o1 o2
        */
    }

    // o3
    public A bar(){
        return bf0;
    }
}
