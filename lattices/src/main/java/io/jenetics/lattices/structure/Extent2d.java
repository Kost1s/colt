/*
 * Java Lattice Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.lattices.structure;

import static io.jenetics.lattices.structure.Structures.multNotSave;

import java.util.Iterator;

/**
 * The extent of 2-d structures.
 *
 * @param rows the number of rows, must be greater or equal zero
 * @param cols the number of columns, must be greater or equal zero
 * @param channels the number of channels
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0
 */
public record Extent2d(int rows, int cols, int channels) implements Iterable<Index2d> {

    /**
     * Create a new 2-d extent.
     *
     * @param rows the number of rows
     * @param cols the number of cols
     * @throws IllegalArgumentException if one of the arguments is smaller than
     *         zero or {@code rows*cols > Integer.MAX_VALUE}
     */
    public Extent2d(int rows, int cols) {
        this(rows, cols, 1);
    }

    /**
     * Create a new 2-d extent.
     *
     * @param rows the number of rows
     * @param cols the number of cols
     * @param channels the number of channels
     * @throws IllegalArgumentException if one of the arguments is smaller than
     *         zero or {@code rows*cols*channels > Integer.MAX_VALUE}
     */
    public Extent2d {
        if (rows < 0 || cols < 0 || channels < 1 ||
            multNotSave(rows, cols, channels))
        {
            throw new IllegalArgumentException(
                "Extent is out of bounds: [%d, %d, channels=%d]."
                    .formatted(rows, cols, channels)
            );
        }
    }

    /**
     * The number of elements of the structure.
     *
     * @return the number of cells of the structure
     */
    public int size() {
        return rows*cols;
    }

    /**
     * Return the length of the array, needed for storing all cells:
     * {@code size()*channels}.
     *
     * @return the array length needed for storing all cells
     */
    public int length() {
        return size()*channels;
    }

    @Override
    public Iterator<Index2d> iterator() {
        return new Index2dIterator(new Range2d(this));
    }

    @Override
    public String toString() {
        return "[%d, %d]".formatted(rows(), cols());
    }

}
