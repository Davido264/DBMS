package test;

public class Utils {
  static void print(Object o) {
    System.out.println(o);
  }

  static void test(Object expected, Object obtained) {
    if (expected.equals(obtained)) {
      print("pass");
    } else {
      print(expected);
      print(obtained);
      print("fail");
    }
  }
}
