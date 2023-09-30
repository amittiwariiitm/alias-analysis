class P4 {
    public static void main (String[] args){
        A someObj;
        A objA;
        B objB;
        F objF;
        A loop1;
        A loop2;
        A loop3;
        A loop4;
        int n;
        int ten;
        int nine;
        boolean cond;
        boolean tr;
        int[] array;

        F b1;
        F b2;
        F b3;


        objF = new F(); //o3
        ten = 10;
        n = objF.num(ten);

        nine = 9;
        cond = n < nine;
        array = new int[nine];
        if(cond){
            objA = new A();
            someObj = objA; //o1
        }else{
            objB = new B(); //o2
            someObj = objB;
        }
        //someobj o1 o2


        /*
        o1 f0 o3
        o2 fo o3
        o1 f0 o4
        o2 fo o4
        */

        // o3 o4
        b1 = someObj.bar(objF, ten);

        // o3 o4
        b2 = someObj.bar(b1, nine);

        // o3 o4
        b3 = someObj.foo(b1);


        loop1 = new A();
        loop2 = new A();
        loop3 = new A();
        loop4 = new A();

        loop1.f0 = new F();
        loop2.f0 = new F();
        loop3.f0 = new F();
        loop4.f0 = new F();



        tr = true;
        while(tr){
            loop4.f0 = loop3.f0;
            loop3.f0 = loop2.f0;
            loop2.f0 = loop1.f0;
            loop1.f0 = loop4.f0;
        }

    }
}

class F{
    int a;
    public int num(int nn){
        a = nn;
        return a;
    }
    // o3 o4
    public F give(){
        F retVal;
        retVal =  new F(); // o4
        return retVal;
    }
}

class A {
    int a;
    F f0;
    //o1 o2

    // o3 o4
    public F bar(F a1, int nn){
        F temp;
        f0 = a1;
        a = nn;
        temp = this.ses(f0);
        return f0;
        // o3 o4
    }

    public F ses(F a1){
        return a1;
    }

    //o1 o2

    // o3 o4
    public F foo(F a1){
        return f0;
    }
}

class B extends A{
    int b;
    //o1 o2

    //o3 o4
    public F bar(F a1, int nn){
        F newF;
        b = nn;
        newF = a1.give();
        return newF;
        // o4
    }


    //o1 o2

    // o3 o4
    public F foo(F a1){
        return a1;
    }
}

