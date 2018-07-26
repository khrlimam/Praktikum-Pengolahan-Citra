package praktikum.pengolahan.citra.digitrecognizer.pojos;

public class SimilarityHolder {
  private int numbLabel;
  private double score;

  public int getNumbLabel() {
    return numbLabel;
  }

  public void setNumbLabel(int numbLabel) {
    this.numbLabel = numbLabel;
  }

  public double getScore() {
    return score;
  }

  public void setScore(double score) {
    this.score = score;
  }

  public SimilarityHolder(int numbLabel, double score) {

    this.numbLabel = numbLabel;
    this.score = score;
  }
}
