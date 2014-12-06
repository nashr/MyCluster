import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import weka.clusterers.AbstractClusterer;
import weka.core.Instance;
import weka.core.Instances;

public class PartitionalCluster extends AbstractClusterer {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public static enum INIT_METHOD {
    FORGY, RANDOM
  }

  private INIT_METHOD init_method;

  private int n_cluster;
  private int f_converge;
  private ArrayList<ArrayList<Instance>> clusters;
  private ArrayList<ArrayList<Integer>> ID_clusters;
  private ArrayList<ArrayList<Double>> centroids;
  private Instances dataset;

  private static double bound = 9999.0d;

  public PartitionalCluster() {
    init_method = INIT_METHOD.FORGY;
    n_cluster = 2;
    f_converge = 9999;
    clusters = new ArrayList<ArrayList<Instance>>();
    ID_clusters = new ArrayList<ArrayList<Integer>>();
    centroids = new ArrayList<ArrayList<Double>>();
  }

  public void setNumberOfCluster(int k) {
    n_cluster = k;
  }

  public void setInitMethod(INIT_METHOD method) {
    init_method = method;
  }

  private double calcDistance(ArrayList<Double> centroid, Instance data) {
    double d = 0;

    for (int i = 0; i < dataset.numAttributes() - 1; i++) {
      if (data.value(dataset.attribute(i)) != centroid.get(i)) {
        d += 1;
      }
    }

    return d;
  }

  private void initCentroid() {
    if (init_method == INIT_METHOD.FORGY) {
      ArrayList<Integer> c = new ArrayList<Integer>();
      c.add(new Random().nextInt(dataset.numInstances()));
      for (int i = 1; i < n_cluster; i++) {
        int e = new Random().nextInt(dataset.numInstances());
        while (c.contains(e)) {
          e = new Random().nextInt(dataset.numInstances());
        }
        c.add(e);
      }

      for (int i = 0; i < n_cluster; i++) {
        centroids.add(new ArrayList<Double>());
        for (int j = 0; j < dataset.numAttributes() - 1; j++) {
          centroids.get(i).add(dataset.instance(c.get(i)).value(dataset.attribute(j)));
        }
      }
    } else if (init_method == INIT_METHOD.RANDOM) {
      // TODO
    }
  }

  private void updateCentroid() {
    for (int i = 0; i < n_cluster; i++) {
      for (int j = 0; j < dataset.numAttributes() - 1; j++) {
        ArrayList<Integer> t = new ArrayList<Integer>();
        for (int k = 0; k < dataset.attribute(j).numValues(); k++) {
          t.add(0);
        }

        for (int k = 0; k < clusters.get(i).size(); k++) {
          int idx = (int) clusters.get(i).get(k).value(dataset.attribute(j));
          t.set(idx, t.get(idx) + 1);
        }

        double newval = (double) t.indexOf(Collections.max(t));
        if (newval != centroids.get(i).get(j)) {
          f_converge += 1;
          centroids.get(i).set(j, newval);
        }
      }
    }
  }

  private void initCluster() {
    for (int i = 0; i < n_cluster; i++) {
      clusters.add(new ArrayList<Instance>());
      ID_clusters.add(new ArrayList<Integer>());
    }
  }

  private void resetCluster() {
    for (int i = 0; i < n_cluster; i++) {
      clusters.get(i).clear();
      ID_clusters.get(i).clear();
    }
  }

  private void makeCluster() {
    resetCluster();

    for (int i = 0; i < dataset.numInstances(); i++) {
      int cluster = -1;
      double dist = bound;
      for (int j = 0; j < n_cluster; j++) {
        double d = calcDistance(centroids.get(j), dataset.instance(i));
        if (d < dist) {
          cluster = j;
          dist = d;
        }
      }

      clusters.get(cluster).add(dataset.instance(i));
      ID_clusters.get(cluster).add(i);
    }
  }

  @Override
  public void buildClusterer(Instances data) throws Exception {
    dataset = data;
    initCentroid();
    initCluster();
    while (f_converge > 0) {
      f_converge = 0;
      makeCluster();
      updateCentroid();
    }

    printCluster();
  }

  public int clusterInstance(Instance instance) throws Exception {
    int idx = -1;
    double dis = bound;
    for (int i = 0; i < centroids.size(); i++) {
      double d = calcDistance(centroids.get(i), instance);
      if (d < dis) {
        idx = i;
        dis = d;
      }
    }

    return idx;
  }

  @Override
  public int numberOfClusters() throws Exception {
    return n_cluster;
  }

  private void printCluster() {
    for (int i = 0; i < n_cluster; i++) {
      System.out.println("Cluster ke-" + i);
      for (int j = 0; j < ID_clusters.get(i).size(); j++) {
        System.out.print(ID_clusters.get(i).get(j));
        if (j < ID_clusters.get(i).size() - 1) {
          System.out.print(",");
        } else {
          System.out.println();
        }
      }
    }
  }
}
