import java.util.ArrayList;

import weka.clusterers.AbstractClusterer;
import weka.core.Instance;
import weka.core.Instances;

public class HierarchicalCluster extends AbstractClusterer {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private ArrayList<Instance> dataset;
  private ArrayList<Tree<Integer>> cluster;
  private ArrayList<ArrayList<Double>> m_proximity;

  public HierarchicalCluster() {
    dataset = new ArrayList<Instance>();
    cluster = new ArrayList<Tree<Integer>>();
    m_proximity = new ArrayList<ArrayList<Double>>();

  }

  private void initCluster(Instances data) {
    for (int i = 0; i < data.numInstances(); i++) {
      dataset.add(data.instance(i));
      cluster.add(new Tree<Integer>(i));
    }

  }

  private void printCluster() {
    for (int i = 0; i < cluster.size(); i++) {
      System.out.println("Cluster ke-" + i);
      System.out.println("---");
      System.out.println(cluster.get(i).toString(""));
      System.out.println("===");
    }
  }

  private double calcDistance(Instance data0, Instance data1) {
    double d = 0;

    for (int i = 0; i < data0.numAttributes(); i++) {
      if (data0.value(data0.attribute(i)) != data1.value(data1.attribute(i))) {
        d += 1;
      }
    }

    return d;

  }

  private void initProximityMatrix(Instances data) {
    for (int i = 0; i < data.numInstances(); i++) {
      m_proximity.add(new ArrayList<Double>());
    }

    for (int i = 0; i < m_proximity.size(); i++) {
      for (int j = 0; j < i + 1; j++) {
        if (i == j) {
          m_proximity.get(i).add(9999.0d);
        } else { // j < i
          m_proximity.get(i).add(calcDistance(data.instance(i), data.instance(j)));
        }
      }
    }

  }

  private void updateProximityMatrix(int[] pair) {
    m_proximity.add(new ArrayList<Double>());
    for (int i = 0; i < m_proximity.size(); i++) {
      if (i == m_proximity.size() - 1) {
        m_proximity.get(m_proximity.size() - 1).add(9999.0d);
      } else {
        double d0, d1;

        if (i < pair[0]) {
          d0 = m_proximity.get(pair[0]).get(i);
        } else {
          d0 = m_proximity.get(i).get(pair[0]);
        }

        if (i < pair[1]) {
          d1 = m_proximity.get(pair[1]).get(i);
        } else {
          d1 = m_proximity.get(i).get(pair[1]);
        }

        if (d0 < d1) {
          m_proximity.get(m_proximity.size() - 1).add(d0);
        } else {
          m_proximity.get(m_proximity.size() - 1).add(d1);
        }
      }
    }

    m_proximity.remove(pair[0]);
    if (pair[0] < pair[1]) {
      m_proximity.remove(pair[1] - 1);
    } else {
      m_proximity.remove(pair[1]);
    }

    for (int i = 0; i < m_proximity.size(); i++) {
      if (pair[0] < m_proximity.get(i).size()) {
        m_proximity.get(i).remove(pair[0]);
        if (pair[0] < pair[1]) {
          if (pair[1] - 1 < m_proximity.get(i).size() - 1) {
            m_proximity.get(i).remove(pair[1] - 1);
          }
        } else {
          if (pair[1] < m_proximity.get(i).size() - 1) {
            m_proximity.get(i).remove(pair[1]);
          }
        }
      } else {
        if (pair[1] < m_proximity.get(i).size()) {
          m_proximity.get(i).remove(pair[1]);
        }
      }
    }

  }

  private void printProximityMatrix() {
    for (int i = 0; i < m_proximity.size(); i++) {
      for (int j = 0; j < m_proximity.get(i).size(); j++) {
        if (j == 0) {
          System.out.print(i + "\t");
        }
        System.out.print(m_proximity.get(i).get(j) + " ");
      }
      System.out.println();
    }

  }

  private int getLeastIndex(int idx, int idx0, int idx1) {
    int i;

    if (idx0 == idx1) {
      i = idx0;
    } else {
      int j = getLeastIndex(idx, idx0, (idx0 + idx1) / 2);
      int k = getLeastIndex(idx, (idx0 + idx1) / 2 + 1, idx1);

      if (m_proximity.get(idx).get(j) <= m_proximity.get(idx).get(k)) {
        i = j;
      } else {
        i = k;
      }
    }

    return i;

  }

  private int[] getClosestPair(int idx0, int idx1) {
    int[] retval = new int[2];

    if (idx0 == idx1) {
      retval[0] = idx0;
      retval[1] = getLeastIndex(idx0, 0, m_proximity.get(idx0).size() - 1);
    } else {
      int[] t0 = getClosestPair(idx0, (idx0 + idx1) / 2);
      int[] t1 = getClosestPair((idx0 + idx1) / 2 + 1, idx1);

      if (m_proximity.get(t0[0]).get(t0[1]) <= m_proximity.get(t1[0]).get(t1[1])) {
        retval[0] = t0[0];
        retval[1] = t0[1];
      } else {
        retval[0] = t1[0];
        retval[1] = t1[1];
      }
    }

    return retval;

  }

  private void makeCluster(int pair[]) {
    Tree<Integer> t = new Tree<Integer>();
    t.addLeftChild(cluster.remove(pair[0]));
    t.addRightChild(cluster.remove(pair[1]));
    t.setRootFromChildren();

    cluster.add(t);
  }

  @Override
  public void buildClusterer(Instances data) throws Exception {
    initCluster(data);
    initProximityMatrix(data);

    while (cluster.size() > 1) {
      int[] pair = getClosestPair(0, m_proximity.size() - 1);
      makeCluster(pair);
      updateProximityMatrix(pair);
    }

    printCluster();

  }

  public int clusterInstance(Instance instance) throws Exception {
    int idx = -1;
    double dis = 9999.0d;
    for (int i = 0; i < dataset.size(); i++) {
      double d = calcDistance(instance, dataset.get(i));
      if (d < dis) {
        idx = i;
        dis = d;
      }
    }

    if (cluster.get(0).getLeftChild().getRoot().contains(idx)) {
      return 0;
    } else {
      return 1;
    }

  }

  @Override
  public int numberOfClusters() throws Exception {
    return 2;
  }
}
