package com.lushprojects.circuitjs1.client.matrix;

import java.util.Arrays;
import java.util.Iterator;
public class DMatrixSparseCSC {
    /**
     * Storage for non-zero values. Only valid up to length-1.
     */
    public double[] nz_values = new double[0];
    /**
     * Length of data. Number of non-zero values in the matrix
     */
    public int nz_length;
    /**
     * Specifies which row a specific non-zero value corresponds to. If they are sorted or not with in each column
     * is specified by the {@link #indicesSorted} flag.
     */
    public int[] nz_rows = new int[0];
    /**
     * Stores the range of indexes in the non-zero lists that belong to each column. Column 'i' corresponds to
     * indexes col_idx[i] to col_idx[i+1]-1, inclusive.
     */
    public int[] col_idx;

    /**
     * Number of rows in the matrix
     */
    public int numRows;
    /**
     * Number of columns in the matrix
     */
    public int numCols;

    /**
     * Flag that's used to indicate of the row indices are sorted or not.
     */
    public boolean indicesSorted = false;

    public static double EPS = Math.pow(2.0, -52.0);
    /**
     * Constructor with a default arrayLength of zero.
     *
     * @param numRows Number of rows
     * @param numCols Number of columns
     */
    public DMatrixSparseCSC(int numRows, int numCols) {
        this(numRows, numCols, 0);
    }

    /**
     * Specifies shape and number of non-zero elements that can be stored.
     *
     * @param numRows     Number of rows
     * @param numCols     Number of columns
     * @param arrayLength Initial maximum number of non-zero elements that can be in the matrix
     */
    public DMatrixSparseCSC(int numRows, int numCols, int arrayLength) {
        if (numRows < 0 || numCols < 0 || arrayLength < 0)
            throw new IllegalArgumentException("Rows, columns, and arrayLength must be not be negative");
        this.numRows = numRows;
        this.numCols = numCols;
        this.nz_length = 0;
        col_idx = new int[numCols + 1];
        growMaxLength(arrayLength, false);
    }

    public DMatrixSparseCSC(DMatrixSparseCSC original) {
        this(original.numRows, original.numCols, original.nz_length);

        setTo(original);
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public DMatrixSparseCSC copy() {
        return new DMatrixSparseCSC(this);
    }

    public DMatrixSparseCSC createLike() {
        return new DMatrixSparseCSC(numRows, numCols);
    }

    public void setTo(DMatrixSparseCSC original) {
        DMatrixSparseCSC o = original;
        reshape(o.numRows, o.numCols, o.nz_length);
        this.nz_length = o.nz_length;

        System.arraycopy(o.nz_values, 0, nz_values, 0, nz_length);
        System.arraycopy(o.nz_rows, 0, nz_rows, 0, nz_length);
        System.arraycopy(o.col_idx, 0, col_idx, 0, numCols + 1);
        this.indicesSorted = o.indicesSorted;
    }

//    public void print() {
//        MatrixIO.printFancy(System.out, this, MatrixIO.DEFAULT_LENGTH);
//    }

//    public void print(String format) {
//        MatrixIO.print(System.out, this, format);
//    }

//    public void printNonZero() {
//        String format = "%d %d " + MatrixIO.DEFAULT_FLOAT_FORMAT + "\n";
//        System.out.println("Type = " + getType().name() + " , rows = " + numRows + " , cols = " + numCols
//                + " , nz_length = " + nz_length);
//
//        for (int col = 0; col < numCols; col++) {
//            int idx0 = col_idx[col];
//            int idx1 = col_idx[col + 1];
//
//            for (int i = idx0; i < idx1; i++) {
//                int row = nz_rows[i];
//                double value = nz_values[i];
//
//                System.out.printf(format, row, col, value);
//            }
//        }
//    }

    public boolean isAssigned(int row, int col) {
        return nz_index(row, col) >= 0;
    }

    public double get(int row, int col) {
        if (row < 0 || row >= numRows || col < 0 || col >= numCols)
            throw new IllegalArgumentException("Outside of matrix bounds");

        return unsafe_get(row, col);
    }

    public double get(int row, int col, double fallBackValue) {
        if (row < 0 || row >= numRows || col < 0 || col >= numCols)
            throw new IllegalArgumentException("Outside of matrix bounds");

        return unsafe_get(row, col, fallBackValue);
    }

    public double unsafe_get(int row, int col) {
        int index = nz_index(row, col);
        if (index >= 0)
            return nz_values[index];
        return 0;
    }

    public double unsafe_get(int row, int col, double fallBackValue) {
        int index = nz_index(row, col);
        if (index >= 0)
            return nz_values[index];
        return fallBackValue;
    }

    /**
     * Returns the index in nz_rows for the element at (row,col) if it already exists in the matrix. If not then -1
     * is returned.
     *
     * @param row row coordinate
     * @param col column coordinate
     * @return nz_row index or -1 if the element does not exist
     */
    public int nz_index(int row, int col) {
        int col0 = col_idx[col];
        int col1 = col_idx[col + 1];

        if (this.indicesSorted) {
            return Arrays.binarySearch(nz_rows, col0, col1, row);
        } else {
            for (int i = col0; i < col1; i++) {
                if (nz_rows[i] == row) {
                    return i;
                }
            }
            return -1;
        }
    }

    public void set(int row, int col, double val) {
        if (row < 0 || row >= numRows || col < 0 || col >= numCols)
            throw new IllegalArgumentException("Outside of matrix bounds");

        unsafe_set(row, col, val);
    }

    public void unsafe_set(int row, int col, double val) {
        int index = nz_index(row, col);
        if (index >= 0) {
            nz_values[index] = val;
        } else {

            int idx0 = col_idx[col];
            int idx1 = col_idx[col + 1];

            // determine the index the new element should be inserted at. This is done to keep it sorted if
            // it was already sorted
            for (index = idx0; index < idx1; index++) {
                if (row < nz_rows[index]) {
                    break;
                }
            }

            // shift all the col_idx after this point by 1
            for (int i = col + 1; i <= numCols; i++) {
                col_idx[i]++;
            }

            // if it's already at the maximum array length grow the arrays
            if (nz_length >= nz_values.length)
                growMaxLength(nz_length * 2 + 1, true);

            // shift everything by one
            for (int i = nz_length; i > index; i--) {
                nz_rows[i] = nz_rows[i - 1];
                nz_values[i] = nz_values[i - 1];
            }
            nz_rows[index] = row;
            nz_values[index] = val;
            nz_length++;
        }
    }

    public void remove(int row, int col) {
        int index = nz_index(row, col);

        if (index < 0) // it's not in the nz structure
            return;

        // shift all the col_idx after this point by -1
        for (int i = col + 1; i <= numCols; i++) {
            col_idx[i]--;
        }

        nz_length--;
        for (int i = index; i < nz_length; i++) {
            nz_rows[i] = nz_rows[i + 1];
            nz_values[i] = nz_values[i + 1];
        }
    }

    public void zero() {
        Arrays.fill(col_idx, 0, numCols + 1, 0);
        nz_length = 0;
        indicesSorted = false; // see justification in reshape
    }

    public DMatrixSparseCSC create(int numRows, int numCols) {
        return new DMatrixSparseCSC(numRows, numCols);
    }

    public int getNonZeroLength() {
        return nz_length;
    }

    public void reshape(int numRows, int numCols, int arrayLength) {
        if (numRows < 0 || numCols < 0 || arrayLength < 0)
            throw new IllegalArgumentException("Rows, columns, and arrayLength must be not be negative");

        // OK so technically it is sorted, but forgetting to correctly set this flag is a common mistake so
        // decided to be conservative and mark it as unsorted so that stuff doesn't blow up
        this.indicesSorted = false;
        this.numRows = numRows;
        this.numCols = numCols;
        growMaxLength(arrayLength, false);
        this.nz_length = 0;

        if (numCols + 1 > col_idx.length) {
            col_idx = new int[numCols + 1];
        } else {
            Arrays.fill(col_idx, 0, numCols + 1, 0);
        }
    }


    public void reshape(int numRows, int numCols) {
        reshape(numRows, numCols, 0);
    }


    public void shrinkArrays() {
        if (nz_length < nz_values.length) {
            double[] tmp_values = new double[nz_length];
            int[] tmp_rows = new int[nz_length];

            System.arraycopy(this.nz_values, 0, tmp_values, 0, nz_length);
            System.arraycopy(this.nz_rows, 0, tmp_rows, 0, nz_length);

            this.nz_values = tmp_values;
            this.nz_rows = tmp_rows;
        }
    }

    /**
     * Increases the maximum size of the data array so that it can store sparse data up to 'length'. The class
     * parameter nz_length is not modified by this function call.
     *
     * @param arrayLength   Desired maximum length of sparse data
     * @param preserveValue If true the old values will be copied into the new arrays. If false that step will be skipped.
     */
    public void growMaxLength(int arrayLength, boolean preserveValue) {
        if (arrayLength < 0)
            throw new IllegalArgumentException("Negative array length. Overflow?");

        // NOTE: The code below has been (experimentally) commented out. A situation arose where we wanted to exceed
        //       the max physical size, which would then be corrected later on.

        // see if multiplying numRows*numCols will cause an overflow. If it won't then pick the smaller of the two
//        if( numRows != 0 && numCols <= Integer.MAX_VALUE / numRows ) {
//            // save the user from themselves
//            arrayLength = Math.min(numRows*numCols, arrayLength);
//        }
        if (arrayLength > this.nz_values.length) {
            double[] data = new double[arrayLength];
            int[] row_idx = new int[arrayLength];

            if (preserveValue) {
                System.arraycopy(this.nz_values, 0, data, 0, this.nz_length);
                System.arraycopy(this.nz_rows, 0, row_idx, 0, this.nz_length);
            }

            this.nz_values = data;
            this.nz_rows = row_idx;
        }
    }

    /**
     * Increases the maximum number of columns in the matrix.
     *
     * @param desiredColumns Desired number of columns.
     * @param preserveValue  If the array needs to be expanded should it copy the previous values?
     */
    public void growMaxColumns(int desiredColumns, boolean preserveValue) {
        if (col_idx.length < desiredColumns + 1) {
            int[] c = new int[desiredColumns + 1];
            if (preserveValue)
                System.arraycopy(col_idx, 0, c, 0, col_idx.length);
            col_idx = c;
        }
    }

    /**
     * Given the histogram of columns compute the col_idx for the matrix. nz_length is automatically set and
     * nz_values will grow if needed.
     *
     * @param histogram histogram of column values in the sparse matrix. modified, see above.
     */
    public void histogramToStructure(int[] histogram) {
        col_idx[0] = 0;
        int index = 0;
        for (int i = 1; i <= numCols; i++) {
            col_idx[i] = index += histogram[i - 1];
        }
        nz_length = index;
        growMaxLength(nz_length, false);
        if (col_idx[numCols] != nz_length)
            throw new RuntimeException("Egads");
    }

    /**
     * Copies the non-zero structure of orig into "this"
     *
     * @param orig Matrix who's structure is to be copied
     */
    public void copyStructure(DMatrixSparseCSC orig) {
        reshape(orig.numRows, orig.numCols, orig.nz_length);
        this.nz_length = orig.nz_length;
        System.arraycopy(orig.col_idx, 0, col_idx, 0, orig.numCols + 1);
        System.arraycopy(orig.nz_rows, 0, nz_rows, 0, orig.nz_length);
    }

    /**
     * If the indices has been sorted or not
     *
     * @return true if sorted or false if not sorted
     */
    public boolean isIndicesSorted() {
        return indicesSorted;
    }

    /**
     * Returns true if number of non-zero elements is the maximum size
     *
     * @return true if no more non-zero elements can be added
     */
    public boolean isFull() {
        return nz_length == numRows * numCols;
    }

    public static DMatrixSparseCSC convert(double circuitMatrix[][], double tol) {
        int nonzero =0;
        for (int i = 0; i != circuitMatrix.length; i++)
            for (int j = 0; j != circuitMatrix.length; j++){
                if( circuitMatrix[i][j]!=0){
                    nonzero++;
                }
            }
        DMatrixSparseCSC	dst =  new DMatrixSparseCSC(circuitMatrix.length, circuitMatrix.length, nonzero);
        dst.nz_length = 0;
        dst.col_idx[0] = 0;
        int i, j;
        for (i = 0; i != circuitMatrix.length; i++) {
            for (j = 0; j != circuitMatrix.length; j++) {
                double value = circuitMatrix[j][i];
                if (!(Math.abs(value) <= tol)) {
                    dst.nz_rows[dst.nz_length] = j;
                    dst.nz_values[dst.nz_length] = value;
                    ++dst.nz_length;
                }
            }
            dst.col_idx[i + 1] = dst.nz_length;
        }

        return dst;
    }

    /**
     * Value of an element in a sparse matrix
     */
    class CoordinateRealValue {
        /** The coordinate */
        public int row,col;
        /** The value of the coordinate */
        public double value;
    }

    public Iterator<CoordinateRealValue> createCoordinateIterator() {
        return new Iterator<CoordinateRealValue>() {
            final CoordinateRealValue coordinate = new CoordinateRealValue();
            int nz_index = 0; // the index of the non-zero value and row
            int column = 0; // which column it's in

            {
                incrementColumn();
            }


            public boolean hasNext() {
                return nz_index < nz_length;
            }


            public CoordinateRealValue next() {
                coordinate.row = nz_rows[nz_index];
                coordinate.col = column;
                coordinate.value = nz_values[nz_index];
                nz_index++;
                incrementColumn();
                return coordinate;
            }

            private void incrementColumn() {
                while (column + 1 <= numCols && nz_index >= col_idx[column + 1]) {
                    column++;
                }
            }
        };
    }
}
