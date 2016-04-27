package examples;
public class TestB {
	
	public static long test(Long l) {
		return l.longValue();
	}
	
	public static void main(String[] args) {
		test(new Long(1L));
    }
}
