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
  private ArrayList<ArrayList<Instance>> clusters;
  private ArrayList<ArrayList<Double>> centroids;
  private Instances dataset;

  private static double bound = 9999.0d;

  public PartitionalCluster() {
    init_method = INIT_METHOD.FORGY;
    n_cluster = 2;
    clusters = new ArrayList<ArrayList<Instance>>();
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
        ArrayList<Integer> t = new ArrayList<Integer>(dataset.attribute(j).numValues());
        for (int k = 0; k < clusters.get(i).size(); k++) {
          int idx = (int) clusters.get(i).get(k).value(dataset.attribute(j));
          t.set(idx, t.get(idx) + 1);
        }

        centroids.get(i).set(j, (double) t.indexOf(Collections.max(t)));
      }
    }
  }

  private void initCluster() {
    for (int i = 0; i < n_cluster; i++) {
      clusters.add(new ArrayList<Instance>());
    }
  }

  private void makeCluster() {
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
    }
  }

  @Override
  public void buildClusterer(Instances data) throws Exception {
    dataset = data;
    initCentroid();
    initCluster();
    makeCluster();
    updateCentroid();
  }

  @Override
  public int numberOfClusters() throws Exception {
    return n_cluster;
  }

}
