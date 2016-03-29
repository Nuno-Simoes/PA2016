package examples;
public class TestA {
	
	public static double test(Long l) {
		return l.doubleValue();
	}
	
	public static void main(String[] args) {
		test(new Long(1L));
    }
}
