package ist.meic.pa.GenericFunctions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/*
 * FIXME: REMOVE THESE DOUBTS!!!
 *  - Java allows for GFMethod to have several methods. Can we assume there will only be one?!
 *
 *  XXX: Notes
 *   The before methods are run in most-specific-first order while the after methods are run in least-specific-first order.
 *   The design rationale for this difference can be illustrated with an example.
 *   Suppose class C1 modifies the behavior of its superclass, C2, by adding before methods and after methods. Whether
 *   the behavior of the class C2 is defined directly by methods on C2 or is inherited from its superclasses does not
 *   affect the relative order of invocation of methods on instances of the class C1.
 *   Class C1's before method runs before all of class C2's methods.
 *   Class C1's after method runs after all of class C2's methods.
 */
public class GFMethod {
    private final Method mMethod;

    public GFMethod() {
        Method fMethod = null;
        for (Method method : getClass().getDeclaredMethods()) {
            boolean isValid = method.getName().equals("call") && (method.getReturnType().equals(Object.class) || method.getReturnType().equals(void.class));
            if (isValid)
                fMethod = method;
        }
        if (fMethod == null)
            throw new AssertionError("GFMethod HAS to have a \"public Object call(...)\" or \"public void call(...)\" method! Right?");

        mMethod = fMethod;
    }


    public Parameter[] getParameters() {
        return mMethod.getParameters();
    }


    public boolean isSuibtable(Object[] args) {
        Parameter[] myParameters = getParameters();
        
        if(args.length != myParameters.length)
            return false;
        
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            Class myPType = myParameters[i].getType();
            if (!myPType.isAssignableFrom(arg.getClass())) {
              //  System.out.println(" > "+myPType.getName()+ " not suibtable with " + arg.getClass().getName());
                return false;
            }
        }
        return true;
    }




    public boolean isMoreSpecialized(Object[] args, GFMethod otherMethod) {
        if (isSuibtable(args) && otherMethod == null)
            return true;

        if (isSuibtable(args) && !otherMethod.isSuibtable(args))
            return true;

        Parameter[] myParameters = getParameters();

        for (int i = 0; i < args.length; i++) {
            Class myPType = myParameters[i].getType();
            Class otherPType = otherMethod.getParameters()[i].getType();
            //System.out.println("Comparing: "+myPType.getName()+" with " + otherPType.getName());

            // if they're the same class can't be more specialized
            if(myPType.equals(otherPType)){
              //  System.out.println(" > They're the same");
                continue;
            }


            // This means myPType extends/implements otherType therefor is more specialized
           if(otherPType.isAssignableFrom(myPType)) {
            //   System.out.println(" > "+myPType.getName()+" is more specialized then " + otherPType.getName());
               return true;
            }else{
          //     System.out.println(" > "+otherPType.getName()+" is more specialized " + myPType.getName());
               return false;
           }

        }
        // If we get here all parameters between this and the other GFMethod are the same Class
        //System.out.println(" > All the same");
        return false;
    }


    public Object invoke(Object[] args) {
        try {
            mMethod.setAccessible(true);
            return mMethod.invoke(this, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public int hashCode() {
        int hash = 17;

        for (Parameter param : getParameters()){
            hash = hash * 31 + param.getType().hashCode();
        }

        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof GFMethod))
            return false;

        GFMethod other = (GFMethod) obj;

        Parameter[] myParameters = this.getParameters();
        Parameter[] hisParameters = other.getParameters();

        if (myParameters.length != hisParameters.length)
            return false;

        for (int i = 0; i < myParameters.length; i++) {
            if (!myParameters[i].getType().equals(hisParameters[i].getType()))
                return false;

        }

        return true;
    }
}
