package edu.brown.cs.jjeon5.stars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by jaehyunjeon on 2/11/17.
 *
 * @param <T>
 *          T is a class that implements the KDable interface.
 *
 */
public class KDTree<T extends KDable<T>> {
  private KDNode<T> root;
  private int treeDim;

  /**
   * Construct a KDTree.
   *
   * @param list
   *          List of objects.
   */
  public KDTree(T[] list) {
    treeDim = list[0].getDimension();
    root = construct(list, 0);
  }

  /**
   * Recursive construction of a KDTree.
   *
   * @param list
   *          List of objects.
   * @param dim
   *          Current dimension that KDTree is splitting at.
   * @return KDNode that is the root of the KDTree.
   */
  private KDNode<T> construct(T[] list, int dim) {
    if (list.length == 0) {
      return null;
    } else {
      Arrays.sort(list, new Comparator<T>() {
        @Override
        public int compare(T a, T b) {
          return a.compareDim(dim, b);
        }
      });

      int medianIndex = list.length / 2;
      T median = list[medianIndex];
      T[] leftList = Arrays.copyOfRange(list, 0, medianIndex);
      T[] rightList = Arrays.copyOfRange(list, medianIndex + 1, list.length);
      return new KDNode<T>(median, construct(leftList, (dim + 1) % treeDim),
          construct(rightList, (dim + 1) % treeDim));
    }
  }

  /**
   * returns root of tree.
   *
   * @return root of tree.
   */
  public KDNode<T> getRoot() {
    return root;
  }

  /**
   * KNN - find n nearest neighbors of point pt.
   *
   * @param n
   *          number of neighbors
   * @param pt
   *          originating point
   *
   * @return arraylist of n nearest neighbors of point pt.
   */
  public ArrayList<T> findNearest(int n, T pt) {
    ArrayList<T> neighbors = new ArrayList<T>();
    knn(root, 0, neighbors, new ArrayList<Double>(), n, pt);
    return neighbors;
  }

  private void knn(KDNode<T> node, int dim, ArrayList<T> guessQ,
      ArrayList<Double> distQ, int k, T point) {
    if (node == null) {
      return;
    }
    if (!point.equals(node.getObject())) {
      enqueue(node, guessQ, distQ, k, point);
    }

    int otherSubtree;
    if (point.compareDim(dim, node.getObject()) == -1) {
      knn(node.getLeft(), (dim + 1) % treeDim, guessQ, distQ, k, point);
      otherSubtree = 1;
    } else {
      knn(node.getRight(), (dim + 1) % treeDim, guessQ, distQ, k, point);
      otherSubtree = 0;
    }

    if ((distQ.size() < k) || (point.distanceAtDim(dim,
        node.getObject()) <= distQ.get(distQ.size() - 1))) {
      if (otherSubtree == 0) {
        knn(node.getLeft(), (dim + 1) % treeDim, guessQ, distQ, k, point);
      } else {
        knn(node.getRight(), (dim + 1) % treeDim, guessQ, distQ, k, point);
      }
    }
  }

  private void enqueue(KDNode<T> node, ArrayList<T> guessQ,
      ArrayList<Double> distQ, int k, T point) {
    double dist = point.distance(node.getObject());
    int index = 0;
    for (int i = 0; i < distQ.size(); i++) {
      if (distQ.get(i) < dist) {
        index++;
      }
    }
    distQ.add(index, dist);
    guessQ.add(index, node.getObject());

    if (distQ.size() > k) {
      distQ.remove(distQ.size() - 1);
      guessQ.remove(guessQ.size() - 1);
    }
  }

  /**
   * find neighbors of point pt in radius r.
   *
   * @param r
   *          radius of search
   * @param pt
   *          originating point
   *
   * @return arraylist of neighbors of point pt in radius r.
   */
  public ArrayList<T> radiusSearch(double r, T pt) {
    ArrayList<T> radiusNeighbors = new ArrayList<T>();
    radiusSearchHelper(r, root, 0, radiusNeighbors, pt);
    radiusNeighbors.sort(new Comparator<T>() {
      @Override
      public int compare(T a, T b) {
        return Double.compare(pt.distance(a), pt.distance(b));
      }
    });
    return radiusNeighbors;
  }

  private void radiusSearchHelper(double r, KDNode<T> node, int dim,
      ArrayList<T> neighbors, T point) {
    if (node == null) {
      return;
    }

    if ((!point.equals(node.getObject()))
        && (point.distance(node.getObject()) <= r)) {
      neighbors.add(node.getObject());
    }

    int otherSubtree;
    if (point.compareDim(dim, node.getObject()) == -1) {
      radiusSearchHelper(r, node.getLeft(), (dim + 1) % treeDim, neighbors,
          point);
      otherSubtree = 1;
    } else {
      radiusSearchHelper(r, node.getRight(), (dim + 1) % treeDim, neighbors,
          point);
      otherSubtree = 0;
    }

    if (point.distanceAtDim(dim, node.getObject()) <= r) {
      if (otherSubtree == 0) {
        radiusSearchHelper(r, node.getLeft(), (dim + 1) % treeDim, neighbors,
            point);
      } else {
        radiusSearchHelper(r, node.getRight(), (dim + 1) % treeDim, neighbors,
            point);
      }
    }
  }
}
