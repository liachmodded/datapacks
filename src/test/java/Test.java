/**
 *
 */
public class Test {

  public static void main(String... args) {
    new Test().new Sub().loadStuffTest(args);
  }

  Object load(Sub sub, Object arg) {
    return sub.getString();
  }

  class Sub {

    public Object loadStuffTest(Object arg) {
      Test test = Test.this;

      Object loaded = load(this, arg);
      if (loaded != null) {
        return loaded;
      }

      return new Object();
    }

    String getString() {
      return Test.this.hashCode() + " " + toString();
    }
  }
}
