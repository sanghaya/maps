package edu.brown.cs.jjeon5.stars;

/**
 * Interface that is implemented to make a KDTree out of.
 *
 * @param <T>
 *          T is a class that implements the KDable interface.
 *
 */
public interface KDable<T> {
  /**
   * returns distance between two KDables.
   *
   * @param b
   *          the other KDable
   * @return returns distance between two KDables
   */
  double distance(T b);

  /**
   * returns distance between two KDables at dimension dim.
   *
   * @param dim
   *          the dimension to compare
   * @param b
   *          the other KDable
   * @return returns distance between two KDables at dimension dim.
   */
  double distanceAtDim(int dim, T b);

  /**
   * returns -1, 1, 0 depending on comparison of two KDables at dimension dim.
   *
   * @param dim
   *          the dimension to compare
   * @param b
   *          the other KDable
   * @return returns -1, 1, 0 depending on comparison of two KDables at
   *         dimension dim.
   */
  int compareDim(int dim, T b);

  /**
   * returns total number of dimension of this kdable.
   *
   * @return returns total number of dimension of this kdable
   */
  int getDimension();
}
