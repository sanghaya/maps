package edu.brown.cs.jjeon5.stars;

/**
 * Created by jaehyunjeon on 2/10/17.
 *
 * KDNode is a class that represents node in a KDTree
 *
 * @param <T>
 *          T is a class that implements the KDable interface.
 *
 */
public class KDNode<T extends KDable<T>> {
  private T object;
  private KDNode<T> left;
  private KDNode<T> right;

  /**
   *
   * Constructs a KDNode.
   *
   * @param object
   *          object of type T to store in node.
   *
   * @param left
   *          left KDNode
   *
   * @param right
   *          right kdnode
   *
   *
   */
  public KDNode(T object, KDNode<T> left, KDNode<T> right) {
    this.object = object;
    this.left = left;
    this.right = right;
  }

  /**
   *
   * returns the object of type T.
   *
   * @return the object
   */
  public T getObject() {
    return object;
  }

  // public double[] getPoint() {
  // return new double[] { star.getX(), star.getY(), star.getZ() };
  // }

  /**
   * Return the left child of this KDNode.
   *
   * @return KDNode that is the left child of this KDNode.
   */
  public KDNode<T> getLeft() {
    return left;
  }

  /**
   * Return the right child of this KDNode.
   *
   * @return KDNode that is the right child of this KDNode.
   */
  public KDNode<T> getRight() {
    return right;
  }
}
