import java.util.ArrayList;

public class Tree<T> {
  private ArrayList<T> root;
  private Tree<T> child_left;
  private Tree<T> child_right;

  public Tree() {
    root = new ArrayList();
    child_left = null;
    child_right = null;

  }

  public Tree(T e) {
    root = new ArrayList();
    root.add(e);
    child_left = null;
    child_right = null;

  }

  public ArrayList<T> getRoot() {
    return root;
  }

  public void addLeftChild(Tree<T> t) {
    child_left = t;
  }

  public void addRightChild(Tree<T> t) {
    child_right = t;
  }

  public void setRootFromChildren() {
    for (int i = 0; i < child_left.getRoot().size(); i++) {
      root.add(child_left.getRoot().get(i));
    }
    for (int i = 0; i < child_right.getRoot().size(); i++) {
      root.add(child_right.getRoot().get(i));
    }
  }

  public String toString(String prefix) {
    String retval = prefix + "";

    for (int i = 0; i < root.size(); i++) {
      retval += root.get(i);
      if (i < root.size() - 1) {
        retval += ",";
      }
    }

    if (child_left != null) {
      retval += "\n";
      retval += child_left.toString(prefix + "\t");
    }

    if (child_right != null) {
      retval += "\n";
      retval += child_right.toString(prefix + "\t");
    }

    return retval;

  }
}
