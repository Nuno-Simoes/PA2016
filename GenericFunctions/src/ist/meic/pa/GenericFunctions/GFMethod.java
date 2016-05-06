package ist.meic.pa.GenericFunctions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/*
 * FIXME: REMOVE THESE DOUBTS!!!
 *  - Java allows for GFMethod to have several methods. Can we assume there will only be one?!
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

    public boolean isMoreSpecialized(Object[] args, GFMethod effectiveMethod) {
        Parameter[] myParameters = getParameters();

        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            Class myPType = myParameters[i].getType();
            Class effectivePType = effectiveMethod == null ? null : effectiveMethod.getParameters()[i].getType();

            if (!myPType.isAssignableFrom(arg.getClass())) {
                return false;
            }

            if (effectivePType != null && !effectivePType.isAssignableFrom(myPType)) {
                return false;
            }
        }
        return true;
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
}
