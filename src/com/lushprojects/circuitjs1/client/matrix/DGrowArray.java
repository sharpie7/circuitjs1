package com.lushprojects.circuitjs1.client.matrix;

public class DGrowArray {
    public double[] data;
    public int length;

    public DGrowArray( int length ) {
        this.data = new double[length];
        this.length = length;
    }

    public DGrowArray() {
        this(0);
    }

    public int length() {
        return length;
    }

    public void reset() {reshape(0);}

    /**
     * Changes the array's length and doesn't attempt to preserve previous values if a new array is required
     *
     * @param length New array length
     */
    public DGrowArray reshape( int length ) {
        if (data.length < length) {
            data = new double[length];
        }
        this.length = length;
        return this;
    }

    /**
     * Increases the internal array's length by the specified amount. Previous values are preserved.
     * The length value is not modified since this does not change the 'meaning' of the array, just
     * increases the amount of data which can be stored in it.
     *
     * this.data = new data_type[ data.length + amount ]
     *
     * @param amount Number of elements added to the internal array's length
     */
    public void growInternal( int amount ) {
        double[] tmp = new double[data.length + amount];

        System.arraycopy(data, 0, tmp, 0, data.length);
        this.data = tmp;
    }

    public void setTo( DGrowArray original ) {
        reshape(original.length);
        System.arraycopy(original.data, 0, data, 0, original.length);
    }

    public void add( double value ) {
        if (length >= data.length) {
            growInternal(Math.min(500_000, data.length + 10));
        }

        data[length++] = value;
    }

    public double get( int index ) {
        if (index < 0 || index >= length)
            throw new IllegalArgumentException("Out of bounds");
        return data[index];
    }

    public void set( int index, double value ) {
        if (index < 0 || index >= length)
            throw new IllegalArgumentException("Out of bounds");
        data[index] = value;
    }

    public void free() {
        data = new double[0];
        length = 0;
    }
}
