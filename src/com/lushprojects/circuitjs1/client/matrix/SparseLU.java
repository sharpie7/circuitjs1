package com.lushprojects.circuitjs1.client.matrix;


import java.util.Arrays;

public class SparseLU {


    public  SparseLU(){

    }

    private final IGrowArray gw = new IGrowArray();
    private final DMatrixSparseCSC L = new DMatrixSparseCSC(0, 0, 0);
    private final DMatrixSparseCSC U = new DMatrixSparseCSC(0, 0, 0);
    private double[] x = new double[0];

    private void initialize(DMatrixSparseCSC A) {
        int m = A.numRows;
        int n = A.numCols;
        int o = Math.min(m, n);
        this.L.reshape(m, m, 4 * A.nz_length + o);
        this.L.nz_length = 0;
        this.U.reshape(m, n, 4 * A.nz_length + o);
        this.U.nz_length = 0;
        this.singular = false;
        if (this.pinv.length != m) {
            this.pinv = new int[m];
            this.x = new double[m];
        }

        for(int i = 0; i < m; ++i) {
            this.pinv[i] = -1;
            this.L.col_idx[i] = 0;
        }

    }

    private final IGrowArray gxi = new IGrowArray();
    private int[] pinv = new int[0];

    private boolean singular;
    private boolean performLU(DMatrixSparseCSC A) {
        int m = A.numRows;
        int n = A.numCols;
        int[] q = null;
        int[] w = adjust(this.gw, m * 2, m);

        int k;
        for(k = 0; k < n; ++k) {
            this.L.col_idx[k] = this.L.nz_length;
            this.U.col_idx[k] = this.U.nz_length;
            if (this.L.nz_length + n > this.L.nz_values.length) {
                this.L.growMaxLength(2 * this.L.nz_values.length + n, true);
            }

            if (this.U.nz_length + n > this.U.nz_values.length) {
                this.U.growMaxLength(2 * this.U.nz_values.length + n, true);
            }

            int col = q != null ? q[k] : k;
            int top = solveColB(this.L, true, A, col, this.x, this.pinv, this.gxi, w);
            int[] xi = this.gxi.data;
            int ipiv = -1;
            double a = -1.7976931348623157E308;

            for(int p = top; p < n; ++p) {
                int i = xi[p];
                if (this.pinv[i] < 0) {
                    double t;
                    if ((t = Math.abs(this.x[i])) > a) {
                        a = t;
                        ipiv = i;
                    }
                } else {
                    this.U.nz_rows[this.U.nz_length] = this.pinv[i];
                    this.U.nz_values[this.U.nz_length++] = this.x[i];
                }
            }

            if (ipiv == -1 || a <= 0.0) {
                this.singular = true;
                return false;
            }

            double pivot = this.x[ipiv];
            this.U.nz_rows[this.U.nz_length] = k;
            this.U.nz_values[this.U.nz_length++] = pivot;
            this.pinv[ipiv] = k;
            this.L.nz_rows[this.L.nz_length] = ipiv;
            this.L.nz_values[this.L.nz_length++] = 1.0;

            for(int p = top; p < n; ++p) {
                int i = xi[p];
                if (this.pinv[i] < 0) {
                    this.L.nz_rows[this.L.nz_length] = i;
                    this.L.nz_values[this.L.nz_length++] = this.x[i] / pivot;
                }

                this.x[i] = 0.0;
            }
        }

        this.L.col_idx[n] = this.L.nz_length;
        this.U.col_idx[n] = this.U.nz_length;

        for(k = 0; k < this.L.nz_length; ++k) {
            this.L.nz_rows[k] = this.pinv[this.L.nz_rows[k]];
        }

        return true;
    }

    int AnumRows;
    int AnumCols;

    public boolean setA(DMatrixSparseCSC A) {
        this.AnumRows = A.numRows;
        this.AnumCols = A.numCols;
        return this.decompose(A);
    }

    public boolean decompose(DMatrixSparseCSC A) {
        this.initialize(A);
        return this.performLU(A);
    }

    private final DGrowArray gx = new DGrowArray();
    private final DGrowArray gb = new DGrowArray();

    public void solve(double[] B, double[] X) {

        if (B.length != this.AnumRows) {
            int var10002 = B.length;
            throw new IllegalArgumentException("Unexpected number of rows in B based on shape of A. Found=" + var10002 + " Expected=" + this.AnumRows);
        }

        double[] x = adjust(this.gx, X.length);
        double[] b = adjust(this.gb, B.length);
        DMatrixSparseCSC L = this.L;
        DMatrixSparseCSC U =  this.U;

        for(int i = 0; i < B.length; i++) {
            b[i] = B[i];
        }

        permuteInv(pinv, b, x, X.length);
        solveL(L, x);
        solveU(U, x);

        for(int i = 0; i < X.length; i++) {
            X[i] = x[i];
        }
    }
    public static int solveColB(DMatrixSparseCSC G, boolean lower, DMatrixSparseCSC B, int colB, double[] x,   int[] pinv,   IGrowArray g_xi, int[] w) {
        int X_rows = G.numCols;
        int[] xi = adjust(g_xi, X_rows);
        int top = searchNzRowsInX(G, B, colB, pinv, xi, w);

        int idxB0;
        for(idxB0 = top; idxB0 < X_rows; ++idxB0) {
            x[xi[idxB0]] = 0.0;
        }

        idxB0 = B.col_idx[colB];
        int idxB1 = B.col_idx[colB + 1];

        int px;
        for(px = idxB0; px < idxB1; ++px) {
            x[B.nz_rows[px]] = B.nz_values[px];
        }

        for(px = top; px < X_rows; ++px) {
            int j = xi[px];
            int J = pinv != null ? pinv[j] : j;
            if (J >= 0) {
                int p;
                int q;
                if (lower) {
                    x[j] /= G.nz_values[G.col_idx[J]];
                    p = G.col_idx[J] + 1;
                    q = G.col_idx[J + 1];
                } else {
                    x[j] /= G.nz_values[G.col_idx[J + 1] - 1];
                    p = G.col_idx[J];
                    q = G.col_idx[J + 1] - 1;
                }

                while(p < q) {
                    int var10001 = G.nz_rows[p];
                    x[var10001] -= G.nz_values[p] * x[j];
                    ++p;
                }
            }
        }

        return top;
    }

    public static int searchNzRowsInX(DMatrixSparseCSC G,  DMatrixSparseCSC B, int colB,  int[] pinv, int[] xi, int[] w) {
        int X_rows = G.numCols;
        if (xi.length < X_rows) {
            throw new IllegalArgumentException("xi must be at least G.numCols=" + G.numCols);
        } else if (w.length < 2 * X_rows) {
            throw new IllegalArgumentException("w must be at least 2*G.numCols in length (2*number of rows in X) and first N elements must be zero");
        } else {
            int idx0 = B.col_idx[colB];
            int idx1 = B.col_idx[colB + 1];
            int top = X_rows;

            int i;
            for(i = idx0; i < idx1; ++i) {
                int rowB = B.nz_rows[i];
                if (rowB < X_rows && w[rowB] == 0) {
                    top = searchNzRowsInX_DFS(rowB, G, top, pinv, xi, w);
                }
            }

            for(i = top; i < X_rows; ++i) {
                w[xi[i]] = 0;
            }

            return top;
        }
    }
    private static int searchNzRowsInX_DFS(int rowB,  DMatrixSparseCSC G, int top,  int[] pinv, int[] xi, int[] w) {
        int N = G.numCols;
        int head = 0;
        xi[head] = rowB;

        while(head >= 0) {
            int G_col = xi[head];
            int G_col_new = pinv != null ? pinv[G_col] : G_col;
            if (w[G_col] == 0) {
                w[G_col] = 1;
                w[N + head] = G_col_new >= 0 && G_col_new < N ? G.col_idx[G_col_new] : 0;
            }

            boolean done = true;
            int idx0 = w[N + head];
            int idx1 = G_col_new >= 0 && G_col_new < N ? G.col_idx[G_col_new + 1] : 0;

            for(int j = idx0; j < idx1; ++j) {
                int jrow = G.nz_rows[j];
                if (jrow < N && w[jrow] == 0) {
                    w[N + head] = j + 1;
                    ++head;
                    xi[head] = jrow;
                    done = false;
                    break;
                }
            }

            if (done) {
                --head;
                --top;
                xi[top] = G_col;
            }
        }

        return top;
    }

    public static void solveL(DMatrixSparseCSC L, double[] x) {
        int N = L.numCols;
        int idx0 = L.col_idx[0];

        for(int col = 0; col < N; ++col) {
            int idx1 = L.col_idx[col + 1];
            double x_j = x[col] /= L.nz_values[idx0];

            for(int i = idx0 + 1; i < idx1; ++i) {
                int row = L.nz_rows[i];
                x[row] -= L.nz_values[i] * x_j;
            }

            idx0 = idx1;
        }

    }

    public static void solveU(DMatrixSparseCSC U, double[] x) {
        int N = U.numCols;
        int idx1 = U.col_idx[N];

        for(int col = N - 1; col >= 0; --col) {
            int idx0 = U.col_idx[col];
            double x_j = x[col] /= U.nz_values[idx1 - 1];

            for(int i = idx0; i < idx1 - 1; ++i) {
                int row = U.nz_rows[i];
                x[row] -= U.nz_values[i] * x_j;
            }

            idx1 = idx0;
        }

    }


    public static int[] adjust( IGrowArray gwork, int desired, int zeroToM) {
        int[] w = adjust(gwork, desired);
        Arrays.fill(w, 0, zeroToM, 0);
        return w;
    }

    public static int[] adjust( IGrowArray gwork, int desired) {
        if (gwork == null) {
            gwork = new IGrowArray();
        }

        gwork.reshape(desired);
        return gwork.data;
    }


    public static double[] adjust(DGrowArray gwork, int desired) {
        if (gwork == null) {
            gwork = new DGrowArray();
        }

        gwork.reshape(desired);
        return gwork.data;
    }

    public static void permuteInv(int[] perm, double[] input, double[] output, int N) {
        for(int k = 0; k < N; ++k) {
            output[perm[k]] = input[k];
        }

    }
}
