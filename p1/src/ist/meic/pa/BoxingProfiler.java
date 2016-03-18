package ist.meic.pa;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;


public class BoxingProfiler implements Translator {
    public static void main(String[] args) throws Throwable {
        ClassPool pool = ClassPool.getDefault();
        Loader classLoader = new Loader();
        classLoader.addTranslator(pool, new BoxingProfiler());
        classLoader.run("SumIntegers", null);

        int count = (int) Class.forName("SumIntegers", false, classLoader).getDeclaredField("mIntUnboxingCounter").get(null);

        System.out.println("count: " + count);
    }


    @Override
    public void start(ClassPool pool) throws NotFoundException, CannotCompileException {
        System.out.println("start");


    }

    @Override
    public void onLoad(ClassPool pool, String className) throws NotFoundException, CannotCompileException {
        System.out.println("onLoad: " + className);

        CtClass ct = pool.get(className);
        CtClass intCt = pool.get(int.class.getName());
        CtField unboxingCounterField = new CtField(intCt, "mIntUnboxingCounter", ct);
        unboxingCounterField.setModifiers(Modifier.STATIC | Modifier.PUBLIC);
        ct.addField(unboxingCounterField);


        for (CtMethod cm : ct.getDeclaredMethods()) {
            cm.instrument(new BoxingProfilerExprEditor());
        }
    }

    private class BoxingProfilerExprEditor extends ExprEditor {
        public void edit(MethodCall m) throws CannotCompileException {
            // FIXME: We'll most likely have to also check the signature: valueOf has 3 overloads
            if (m.getClassName().equals(Integer.class.getName()) && m.getMethodName().equals("valueOf")) {
                m.replace("{ $_ = $0.valueOf($1); System.out.println(\"Hello\");}");
            } else if (m.getClassName().equals(Integer.class.getName()) && m.getMethodName().equals("intValue")) {
                m.replace("{ $_ = $0.intValue(); mIntUnboxingCounter++; }");
            }
        }
    }
}