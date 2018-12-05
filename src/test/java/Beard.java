/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
import java.util.Optional;

/**
 *
 */
public abstract class Beard {

  public abstract int get(int a);

  public int calc(int a) {
    return Optional.of(5).map(this::get).orElse(a);
  }
}
