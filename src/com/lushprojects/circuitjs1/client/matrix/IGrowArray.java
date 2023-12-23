package com.lushprojects.circuitjs1.client.matrix;

public class IGrowArray {
    public int[] data;
    public int length;

    public IGrowArray( int length ) {
        this.data = new int[length];
        this.length = length;
    }

    public IGrowArray() {
        this(0);
    }

    public int length() {
        return length;
    }

    public void reshape( int length ) {
        if (data.length < length) {
            data = new int[length];
        }
        this.length = length;
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
        int[] tmp = new int[data.length + amount];

        System.arraycopy(data, 0, tmp, 0, data.length);
        this.data = tmp;
    }

    public void setTo( IGrowArray original ) {
        reshape(original.length);
        System.arraycopy(original.data, 0, data, 0, original.length);
    }

    public int get( int index ) {
        if (index < 0 || index >= length)
            throw new IllegalArgumentException("Out of bounds");
        return data[index];
    }

    public void set( int index, int value ) {
        if (index < 0 || index >= length)
            throw new IllegalArgumentException("Out of bounds");
        data[index] = value;
    }

    public void add( int value ) {
        if (length == data.length) {
            growInternal(Math.min(10_000, 1 + data.length));
        }
        data[length++] = value;
    }

    public void clear() {
        length = 0;
    }

    public void free() {
        data = new int[0];
        length = 0;
    }
}
