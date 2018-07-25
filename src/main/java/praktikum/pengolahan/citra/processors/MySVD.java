package praktikum.pengolahan.citra.processors;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import praktikum.pengolahan.citra.utils.Log;

public class MySVD extends SingularValueDecomposition {
  private int m, n;
  private double[] s;

  public MySVD(Matrix matrix) {
    super(matrix);
    n = matrix.getColumnDimension();
    m = matrix.getRowDimension();
    Log.i(getClass().getName(), Math.min(this.m + 1, this.n)+"");
    this.s = new double[m];
  }

  @Override
  public Matrix getS() {
    Matrix var1 = new Matrix(this.n, this.n);
    double[][] var2 = var1.getArray();
    for (int var3 = 0; var3 < this.n; var3++) {
      for (int var4 = 0; var4 < this.n; var4++) {
        var2[var3][var4] = 0.0D;
      }
      var2[var3][var3] = this.s[var3];
    }

    return var1;
  }
}
