package examples;
public class TestD {

	public static Object identity(Object o) {
		return o;
	}

	public static void main(String[] args) {
		int a = (int)identity(1);
		long b = (long)identity(1L);
		char c = (char)identity('a');
		byte d = (byte)identity((byte)1);
		short e = (short)identity((short)1);
		double f = (double)identity(1.0);
		float g = (float)identity(1.0f);
		boolean h = (boolean)identity(true);
	}
}
