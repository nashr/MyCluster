import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

public class HierarchicalCluster extends Classifier {

  private ArrayList<ArrayList<Integer>> cluster;
  private ArrayList<ArrayList<Double>> m_proximity;

  public HierarchicalCluster() {
    cluster = new ArrayList<>();
    m_proximity = new ArrayList<>();

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
      cluster.add(new ArrayList<Integer>());
      m_proximity.add(new ArrayList<Double>());
    }

    for (int i = 0; i < m_proximity.size(); i++) {
      for (int j = 0; j < i + 1; j++) {
        cluster.get(i).add(i);
        if (i == j) {
          m_proximity.get(i).add(9999.0d);
        } else { // j < i
          m_proximity.get(i).add(calcDistance(data.instance(i), data.instance(j)));
        }
      }
    }

  }

  private void updateProximityMatrix() {
    // TODO

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

      if (m_proximity.get(idx).get(j) < m_proximity.get(idx).get(k)) {
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

      if (m_proximity.get(t0[0]).get(t0[1]) < m_proximity.get(t1[0]).get(t1[1])) {
        retval[0] = t0[0];
        retval[1] = t0[1];
      } else {
        retval[0] = t1[0];
        retval[1] = t1[1];
      }
    }

    return retval;

  }

  private void makeCluster() {
    int[] pair = getClosestPair(0, m_proximity.size() - 1);

    cluster.get(pair[0]).add(pair[1]);
    cluster.remove(pair[1]);
  }

  @Override
  public void buildClassifier(Instances data) throws Exception {
    initProximityMatrix(data);

    // printProximityMatrix();

    makeCluster();

  }

}
