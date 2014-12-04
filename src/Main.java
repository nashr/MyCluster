import weka.clusterers.ClusterEvaluation;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class Main {
  public static void main(String[] args) throws Exception {
    String path = "res/weather.nominal.arff";

    DataSource source = new DataSource(path);
    Instances data = source.getDataSet();
    if (data.classIndex() == -1) {
      data.setClassIndex(data.numAttributes() - 1);
    }

    HierarchicalCluster HC = new HierarchicalCluster();
    HC.setMode(HierarchicalCluster.Mode.COMPLETE);
    long begin = System.currentTimeMillis();
    HC.buildClusterer(data);
    long end = System.currentTimeMillis();

    ClusterEvaluation eval = new ClusterEvaluation();
    eval.setClusterer(HC);
    eval.evaluateClusterer(data);

    System.out.println(eval.clusterResultsToString());

    System.out.println("Classifier built in " + (end - begin) + " milliseconds.");
  }
}
