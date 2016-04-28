package ist.meic.pa.GenericFunctions;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/*
 * FIXME: REMOVE THESE DOUBTS!!!
 *  - What happens when 2 or more GFMethod offer the same signature for a call?
 */
public class GenericFunction {
    private final String mName;
    private final List<GFMethod> mMethods;
    private final List<GFMethod> mBeforeMethods;
    private final List<GFMethod> mAfterMethods;

    public GenericFunction(String name) {
        mName = name;
        mMethods = new ArrayList<>();
        mBeforeMethods = new ArrayList<>();
        mAfterMethods = new ArrayList<>();
    }

    public Object call(Object... subArgs) {
        GFMethod effectiveMethod = null;

        for (GFMethod method : mMethods) {
            if (method.isMoreSpecialized(subArgs, effectiveMethod)) {
                effectiveMethod = method;
            }
        }

        if (effectiveMethod == null) {
            throwError(subArgs);
        }


        return effectiveMethod.invoke(subArgs);
    }

    private void throwError(Object... args) {
        String argsFormat = "";
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
        mMethods.add(method);
    }

    public void addBeforeMethod(GFMethod method) {

    }

    public void addAfterMethod(GFMethod method) {

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
