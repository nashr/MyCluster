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
    long begin = System.currentTimeMillis();
    HC.buildClassifier(data);
    long end = System.currentTimeMillis();

    System.out.println(HC.toString());
    System.out.println("Classifier built in " + (end - begin) + " milliseconds.");
  }
}
