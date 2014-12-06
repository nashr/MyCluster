import java.util.ArrayList;
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

  private double calcDistance(Instance data0, Instance data1) {

    return 0;
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
        for (int j = 0; j < dataset.numAttributes(); j++) {
          centroids.get(i).add(dataset.instance(c.get(i)).value(dataset.attribute(j)));
        }
      }
    } else if (init_method == INIT_METHOD.RANDOM) {
      // TODO
    }
  }

  private void makeCluster() {
    for (int i = 0; i < dataset.numInstances(); i++) {
      int cluster = 0;
      for (int j = 0; j < n_cluster; j++) {

      }
    }
  }

  private void initCluster() {
    for (int i = 0; i < n_cluster; i++) {
      clusters.add(new ArrayList<Instance>());
    }
  }

  @Override
  public void buildClusterer(Instances data) throws Exception {
    dataset = data;
    initCentroid();
    initCluster();
    makeCluster();
  }

  @Override
  public int numberOfClusters() throws Exception {
    return n_cluster;
  }

}
