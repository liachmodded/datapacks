/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
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
