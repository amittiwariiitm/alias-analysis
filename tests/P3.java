class P3 {
    public static void main (String[] args){
        A objA1;
        A objA2;
        A as2;
        A as3;
        B objB1;
        B objB2;
        F temp1;
        F temp2;


        F s1;
        F s2;

        /*
        o1 o2, af0 = o5
        o1 o2, af1 = o6
        o5 ff0 = o3
        o5 ff1 = o3, o4
        o6 ff1 = o3, o4
        */

        objA1= new A(); //o1
        objB1 = new B(); //o2
        objA2= new A(); //o3
        objB2 = new B(); //o4

        //o5
        temp1 = objA1.init();
        //o5
        temp2 = objB1.init();

        //o5 o6
        s1 = objA1.bar(objA2);

        //o6
        s2 = objB1.bar(objB2);

        //o3
        as2 = s1.get();

    }
}

class F{
    A ff0;
    A ff1;
    //o5

    //o3
    public A foo(A a1){
        ff0 = a1;
        ff1 = a1;
        return a1;
    }

    public A get(){
        return ff0;
    }
}

class G extends F{
    //o5 o6

    //o3 o4
    public A foo(A a1){
        ff1 = a1;
        return a1;
    }

}


class A {
    F af0;
    G af1;

    //o1 o2
    public F init(){
        af0 = new F(); //o5
        af1 = new G(); //o6
        return af0;
        /*
        o1 o2, af0 = o5
        o1 o2, af1 = o6
        */
    }

    //o1

    //o3
    public F bar(A a){
        int ai;
        int bi;
        A temp;
        F retval;
        boolean cond;
        ai = 10;
        bi = 20;
        cond = ai < bi;
        if (cond){
            temp = af0.foo(a);
            retval =  af0;
        }else{
            retval =  af1;
        }
        return retval;
        //o5 o6

    }

}

class B extends A {
    //o1 o2

    //o3 o4
    public F bar(A a){
        int ai;
        int bi;
        A temp;
        F retval;
        boolean cond;
        ai = 20;
        bi = 20;
        cond = ai < bi;
        if (cond){
            temp = af1.foo(a);
            retval =  af1;
        }else{
            retval =  af1;
        }
        return retval;
        //o6
    }
}
