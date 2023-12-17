public class Utils {

  public static String method1() {
    return "1";
  }

  public static String method2() {
    return "2";
  }

  public static String method3() {
    return "3";
  }

  public static String method4() {
    return "4";
  }

  public static String computeMethod(String methodName) {
    switch (methodName) {
      case "method1":
        return Utils.method1();
      case "method2":
        return Utils.method2();
      case "method3":
        return Utils.method3();
      case "method4":
        return Utils.method4();
      default:
        return "";
    }
  }

  public static String[] getMethodsForNodeType(String nodeType) {
    switch (nodeType) {
      case "TYPE_A":
        return new String[] { "method1", "method2" };
      case "TYPE_B":
        return new String[] { "method2", "method3" };
      case "TYPE_C":
        return new String[] { "method3", "method4" };
      default:
        return new String[] {};
    }
  }
}
