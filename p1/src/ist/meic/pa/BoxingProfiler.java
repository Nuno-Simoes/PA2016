package ist.meic.pa;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class BoxingProfiler implements Translator {
    final String mClass;
    private static String className;

    public BoxingProfiler(String className) {
        mClass = className;
    }

    public static void main(String[] args) throws Throwable {
    	if (args.length != 1) {
    		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            className = reader.readLine().split(" ")[0];
            } else {
            	className = args[0];
       }


        ClassPool pool = ClassPool.getDefault();
        Loader classLoader = new Loader();
        classLoader.addTranslator(pool, new BoxingProfiler(className));
        classLoader.run(className, null);

        Object cl = Class.forName(className, false, classLoader).getDeclaredField("mMetric").get(null);
        System.out.println(cl.toString());
    }


    @Override
    public void start(ClassPool pool) throws NotFoundException, CannotCompileException {
        //System.out.println("start");
    }

    @Override
    public void onLoad(ClassPool pool, String className) throws NotFoundException, CannotCompileException {
        if (!className.equals(mClass))
            return;

        //System.out.println("onLoad: " + className);

        CtClass ct = pool.get(className);
        CtClass metricCt = pool.get(Metric.class.getName());
        CtField unboxingCounterField = new CtField(metricCt, "mMetric", ct);
        unboxingCounterField.setModifiers(Modifier.STATIC | Modifier.PUBLIC);
        ct.addField(unboxingCounterField, CtField.Initializer.byNew(pool.get(Metric.class.getName())));


        for (CtMethod cm : ct.getDeclaredMethods()) {
            cm.instrument(new BoxingProfilerExprEditor());
        }
    }

    private class BoxingProfilerExprEditor extends ExprEditor {
    	
        public void edit(MethodCall m) throws CannotCompileException {
            // FIXME: We'll most likely have to also check the signature: valueOf has 3 overloads
        	
            if (m.getMethodName().equals("valueOf")) {
                m.replace("{ $_ = $0.valueOf($1); " + addMetric(m) + "}");

            } else if ((m.getClassName().equals(Integer.class.getName()) && m.getMethodName().equals("intValue"))||
            		(m.getClassName().equals(Boolean.class.getName()) && m.getMethodName().equals("booleanValue"))||
            		(m.getClassName().equals(Byte.class.getName()) && m.getMethodName().equals("byteValue"))||
            		(m.getClassName().equals(Character.class.getName()) && m.getMethodName().equals("charValue"))||
            		(m.getClassName().equals(Double.class.getName()) && m.getMethodName().equals("doubleValue"))||
            		(m.getClassName().equals(Float.class.getName()) && m.getMethodName().equals("floatValue"))||
            		(m.getClassName().equals(Short.class.getName()) && m.getMethodName().equals("shortValue"))||
            		(m.getClassName().equals(Long.class.getName()) && m.getMethodName().equals("longValue"))){ 
                m.replace("{ $_ = $0."+m.getMethodName()+"();" + addMetric(m) + " }");
            }
        }

        private String addMetric(MethodCall m) {
            String op = m.getMethodName().equals("valueOf") ? "Boxed" : "Unboxed";
            String from = m.where().getLongName();
            String cl = m.getClassName();

            String inst = String.format(" mMetric.add(\"%s\", \"%s\", ist.meic.pa.Metric.Operation.%s); ", from, cl, op);
            //System.out.println("INST: " + inst);
            return inst;
        }
    }
}