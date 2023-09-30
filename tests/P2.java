class P2 {
    public static void main (String[] args){
        F objF1;
        F objF2;

        G objG1;
        G objG2;
        G objG3;
        F temp;

        A objA1;
        A objA2;
        A objA3;
        A objA4;

        B objB;

        objA1 = new A(); //o1
        objB = new B(); //o2
        objF1 = new F(); //o3
        objG1 = new G(); //o4


        /*
        o4 gf0 = o3
        o1 f0 = o4
        o2 f0 = o4
        o2 bf0 = o4
        o5 bf0 = o4
        o5 fo = o4
        o6 fo = o4
        */

        //o3
        temp = objG1.setG(objF1);
        //o5
        objA2 = objA1.set(objG1);

        //o6
        objA4 = objB.foo(objA2);

        //o5
        objA3 = objB.set(objG1);
    
        //o4
        objG3 = objA4.get();
        
        //o7
        objF2 = objG3.getG();

    }
}

class F{
    public F getG(){
        F retVal;
        retVal = new F(); //o7
        return retVal;
    }
}

class G extends F{
    F gf0;
    //o4

    //o3
    public F setG(F a1){
        gf0 = a1;
        return a1;
        //o4 gf0 o3

        // o3

    }
}

class A {
    G f0;
    // o6 o5
    public G get(){
        return f0;
        //o4
    }

    //o1 o2

    // o4
    public A set(G a1){
        B retval;
        f0= a1;
        retval = new B(); //o5
        retval.f0 = a1;
        retval.bf0 = a1;
        // retval.bf0 = f0;
        return retval;
        //o5
    }
}

class B extends A{
    G bf0;

    //o2 

    //o5
    public A foo(A a1){
        A retval;
        bf0 = a1.get(); 
        retval = new A(); //o6
        retval.f0 = bf0;
        return retval;    
        //o6
    }
}


        /*
        o4 gf0 = o3
        o1 f0 = o4
        o2 f0 = o4
        o2 bf0 = o4
        o5 bf0 = o4
        o5 fo = o4
        o6 fo = o4
        */