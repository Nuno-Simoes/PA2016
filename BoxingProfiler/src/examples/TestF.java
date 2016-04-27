package examples;
public class TestF {

	public static Boolean identity(boolean o) { return o; }
	public static Byte identity(byte o) { return o; }
	public static Character identity(char o) { return o; }
	public static Double identity(double o) { return o; }
	public static Float identity(float o) { return o; }
	public static Integer identity(int o) { return o; }
	public static Long identity(long o) { return o; }
	public static Short identity(short o) { return o; }

	public static void main(String[] args) {

		Boolean a = true;
		Byte b = (byte)1;
		Character c = 'a';
		Double d = 1.0;
		Float e = 1.0f;
		Integer f = 1;
		Long g = 1L;
		Short h = (short)1;

		a = (boolean)identity(a);
		b = (byte)identity(b);
		c = (char)identity(c);
		d = (double)identity(d);
		e = (float)identity(e);
		f = (int)identity(f);
		g = (long)identity(g);
		h = (short)identity(h);
    }
}
