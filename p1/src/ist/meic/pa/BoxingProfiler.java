package ist.meic.pa;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

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
        System.err.println(cl.toString());
    }


    @Override
    public void start(ClassPool pool) throws NotFoundException, CannotCompileException { }

    @Override
    public void onLoad(ClassPool pool, String className) throws NotFoundException, CannotCompileException {
        if (!className.equals(mClass))
            return;

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
        	final Map<String, String> map = new HashMap<>();
        		map.put((Integer.class.getName()), "intValue");
        		map.put((Boolean.class.getName()), "booleanValue");
        		map.put((Byte.class.getName()), "byteValue");
        		map.put((Character.class.getName()), "charValue");
        		map.put((Double.class.getName()), "doubleValue");
        		map.put((Float.class.getName()), "floatValue");
        		map.put((Short.class.getName()), "shortValue");
        		map.put((Long.class.getName()), "longValue");
        	
            	System.out.println(m.getClassName() + "\n");
            	System.out.println(m.getMethodName() + "\n");

            if (map.containsKey(m.getClassName()) && m.getMethodName().equals("valueOf")) {
            	m.replace("{ $_ = $0.valueOf($1); " + addMetric(m) + "}");
            } else if (map.containsKey(m.getClassName()) && 
        			m.getMethodName().equals(map.get(m.getClassName()))){
                m.replace("{ $_ = $0."+m.getMethodName()+"();" + addMetric(m) + " }");
            }
        }

        private String addMetric(MethodCall m) {
            String op = m.getMethodName().equals("valueOf") ? "Boxed" : "Unboxed";
            String from = m.where().getLongName();
            String cl = m.getClassName();

            String inst = String.format(" mMetric.add(\"%s\", \"%s\", ist.meic.pa.Metric.Operation.%s); ", from, cl, op);
            return inst;
        }
    }
}