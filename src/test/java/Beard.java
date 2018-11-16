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
