package ist.meic.pa.GenericFunctions;


import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/*
 * FIXME: REMOVE THESE DOUBTS!!!
 *  - What happens when 2 or more GFMethod offer the same signature for a call?
 */
public class GenericFunction {
    private final String mName;
    private final Set<GFMethod> mMethods;
    private final Set<GFMethod> mBeforeMethods;
    private final Set<GFMethod> mAfterMethods;

    public GenericFunction(String name) {
        mName = name;
        mMethods = new HashSet<>();
        mBeforeMethods = new HashSet<>();
        mAfterMethods = new HashSet<>();
    }

    public Object call(Object... subArgs) {
        Object result = null;

        // Before methods
        {
            List<GFMethod> beforeMethods = orderSpecializedMethods(mBeforeMethods, subArgs);
            for (GFMethod method : beforeMethods)
                method.invoke(subArgs);
        }

        // Main method
        {
            List<GFMethod> mainMethods = orderSpecializedMethods(mMethods, subArgs);
            if (mainMethods.size() < 1) {
                throwError(subArgs);
            }
            result = mainMethods.get(0).invoke(subArgs);
        }

        // After methods
        {
            List<GFMethod> afterMethods = orderSpecializedMethods(mAfterMethods, subArgs);
            for (int i = afterMethods.size() - 1; i >= 0; i--) {
                GFMethod method = afterMethods.get(i);
                method.invoke(subArgs);
            }
        }

        return result;
    }


    private List<GFMethod> orderSpecializedMethods(final Set<GFMethod> methods, final Object[] params) {

        return methods.stream().filter(gfMethod -> gfMethod.isSuibtable(params)).sorted((meth1, meth2) -> {
            if (meth1.isMoreSpecialized(params, meth2))
                return -1;
            else
                return 1;
        }).collect(Collectors.toList());
    }


    private void throwError(Object... args) {
        String argsFormat = Arrays.deepToString(args);
        String classesFormat = "";


        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            classesFormat += "class " + arg.getClass().getName() + ((i + 1 == args.length) ? "" : ", ");
        }


        String error = String.format(
                Locale.US,
                "%nNo methods for generic function add with args %s%nof classes [%s]",
                argsFormat,
                classesFormat
        );

        throw new IllegalArgumentException(error);
    }


    public void addMethod(GFMethod method) {

        if(mMethods.contains(method))
            mMethods.remove(method);

        mMethods.add(method);
    }

    public void addBeforeMethod(GFMethod method) {
        if(mBeforeMethods.contains(method))
            mBeforeMethods.remove(method);

        mBeforeMethods.add(method);
    }

    public void addAfterMethod(GFMethod method) {
        if(mAfterMethods.contains(method))
            mAfterMethods.remove(method);

        mAfterMethods.add(method);
    }

    public static void main(String[] args) throws IOException {

        final GenericFunction explain = new GenericFunction("explain");
        explain.addMethod(new GFMethod() {
            Object call(Integer entity) {
                System.out.printf("%s is a integer", entity);
                return "";
            }
        });
        explain.addMethod(new GFMethod() {
            Object call(Number entity) {
                System.out.printf("%s is a number", entity);
                return "";
            }
        });
        explain.addMethod(new GFMethod() {
            Object call(String entity) {
                System.out.printf("%s is a string", entity);
                return "";
            }
        });
        explain.addAfterMethod(new GFMethod() {
            void call(Integer entity) {
                System.out.printf(" (in hexadecimal, is %x)", entity);
            }
        });
        explain.addBeforeMethod(new GFMethod() {
            void call(Number entity) {
                System.out.printf("The number ", entity);
            }
        });
        System.out.println(explain.call(123)); // The number 123 is a integer(in hexadecimal, is 7b)
        System.out.println(explain.call("Hi")); // Hi is a string
        System.out.println(explain.call(3.14159)); // The number 3.14159 is a number
    }
}
