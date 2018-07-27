package praktikum.pengolahan.citra.digitrecognizer;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import praktikum.pengolahan.citra.digitrecognizer.pojos.ImageCoordinat;
import praktikum.pengolahan.citra.digitrecognizer.pojos.ModelHolder;
import praktikum.pengolahan.citra.digitrecognizer.pojos.SimilarityHolder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static praktikum.pengolahan.citra.utils.Constants.K;

public class SimilarityProcess {

  private SingularValueDecomposition modelSVD;
  private Matrix U, S, V, Vt, Uk, Sk, Vk, Vkt;
  private static List<Integer> LABELS = new ArrayList<>(Objects.requireNonNull(MatrixModel.modelSource())
      .stream()
      .map(ModelHolder::getLabel)
      .collect(Collectors.toList()));

  private List<ImageCoordinat> imageCoordinats;

  public SimilarityProcess() {
    Matrix model = ModelInitializer.getModel().transpose();
    modelSVD = new SingularValueDecomposition(model);
  }

  public Matrix getU() {
    if (U == null)
      U = modelSVD.getU();
    return U;
  }

  public Matrix getUk() {
    if (Uk == null) {
      double[][] matrix = new double[getU().getRowDimension()][K];
      for (int h = 0; h < getU().getRowDimension(); h++) {
        for (int i = 0; i < K; i++) {
          matrix[h][i] = getU().get(h, i);
        }
      }
      Uk = new Matrix(matrix);
    }
    return Uk;
  }

  public Matrix getS() {
    if (S == null)
      S = modelSVD.getS();
    return S;
  }

  public Matrix getSk() {
    if (Sk == null) {
      double[][] matrix = new double[2][2];
      for (int h = 0; h < 2; h++) {
        for (int i = 0; i < 2; i++) {
          matrix[h][i] = getS().get(h, i);
        }
      }
      Sk = new Matrix(matrix);
    }
    return Sk;
  }

  public Matrix getV() {
    if (V == null)
      V = modelSVD.getV();
    return V;
  }

  public Matrix getVk() {
    if (Vk == null) {
      double[][] matrix = new double[getV().getRowDimension()][K];
      for (int h = 0; h < getV().getRowDimension(); h++) {
        for (int i = 0; i < K; i++) {
          matrix[h][i] = getV().get(h, i);
        }
      }
      Vk = new Matrix(matrix);
    }
    return Vk;
  }

  public Matrix getVkt() {
    if (Vkt == null)
      Vkt = getVk().transpose();
    return Vkt;
  }

  public List<ImageCoordinat> getImageCoordinats() {
    if (imageCoordinats == null) {
      imageCoordinats = new ArrayList<>();
      for (int i = 0; i < getVkt().getColumnDimension(); i++) {
        double x = getVkt().get(0, i);
        double y = getVkt().get(1, i);
        imageCoordinats.add(new ImageCoordinat(x, y));
      }
    }
    return imageCoordinats;
  }

  public List<SimilarityHolder> findSimilarity(Matrix newComer) {
    Matrix newComerCoordinat = newComer.times(getUk()).times(getSk().inverse());

    double x = newComerCoordinat.get(0, 0);
    double y = newComerCoordinat.get(0, 1);
    ImageCoordinat qCoordinate = new ImageCoordinat(x, y);

    List<SimilarityHolder> similarityHolders = new ArrayList<>();
    for (int i = 0; i < getImageCoordinats().size(); i++) {
      double enumerator = newPointTimesExistingPoint(qCoordinate, getImageCoordinats().get(i));
      double denominator = absoluteRootSquaredNewPointTimesAbsoluteRootSquaredExistingPoint(qCoordinate, getImageCoordinats().get(i));
      double cosineSimilarity = enumerator / denominator;
      similarityHolders.add(new SimilarityHolder(LABELS.get(i), cosineSimilarity));
    }
    return similarityHolders;
  }

  public List<SimilarityHolder> findSimilarityAndSort(Matrix newComer) {
    List<SimilarityHolder> toSortSimilarities = findSimilarity(newComer);
    toSortSimilarities.sort(Comparator.comparing(SimilarityHolder::getScore).reversed());
    return toSortSimilarities;
  }

  public static double newPointTimesExistingPoint(ImageCoordinat newPoint, ImageCoordinat existingPoint) {
    return newPoint.getX() * existingPoint.getX() + newPoint.getY() * existingPoint.getY();
  }

  public double absoluteRootSquaredNewPointTimesAbsoluteRootSquaredExistingPoint(ImageCoordinat newPoint, ImageCoordinat existingPoint) {
    return Math.sqrt(Math.pow(newPoint.getX(), 2) + Math.pow(newPoint.getY(), 2))
        * Math.sqrt(Math.pow(existingPoint.getX(), 2) + Math.pow(existingPoint.getY(), 2));
  }

}
