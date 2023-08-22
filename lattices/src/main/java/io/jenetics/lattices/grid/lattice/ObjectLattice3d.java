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
package io.jenetics.lattices.grid.lattice;

import static java.util.Objects.requireNonNull;
import static io.jenetics.lattices.structure.Structures.checkSameExtent;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import io.jenetics.lattices.grid.array.ObjectArray;

/**
 * This interface <em>structures</em> the elements into a 3-dimensional lattice.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0
 */
public interface ObjectLattice3d<T>
    extends Lattice3d<ObjectArray<T>>, Structure3dOps
{

    /**
     * Returns the matrix cell value at coordinate {@code [slice, row, col]}.
     *
     * @param slice the index of the slice-coordinate
     * @param row the index of the row-coordinate
     * @param col the index of the column-coordinate
     * @return the value of the specified cell
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * bounds
     */
    default T get(int slice, int row, int col) {
        return array().get(structure().offset(slice, row, col));
    }

    /**
     * Sets the matrix cell at coordinate {@code [slice, row, col]} to the specified
     * {@code value}.
     *
     * @param slice the index of the slice-coordinate
     * @param row the index of the row-coordinate
     * @param col the index of the column-coordinate
     * @param value the value to be filled into the specified cell
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * bounds
     */
    default void set(int slice, int row, int col, T value) {
        array().set(structure().offset(slice, row, col), value);
    }

    /**
     * Replaces all cell values of the receiver with the values of another
     * matrix. Both matrices must have the same number of rows and columns.
     *
     * @param other the source matrix to copy from (maybe identical to the
     *        receiver).
     * @throws IllegalArgumentException if
     *         {@code !extent().equals(other.extent())}
     */
    default void assign(ObjectLattice3d<? extends T> other) {
        if (other == this) {
            return;
        }
        checkSameExtent(extent(), other.extent());
        forEach((s, r, c) -> set(s, r, c, other.get(s, r, c)));
    }

    /**
     * Sets all cells to the state specified by given {@code values}. The
     * {@code values} are required to have the form {@code values[row][column]}
     * and have exactly the same number of rows and columns as the receiver.
     *
     * @param values the values to be filled into the cells.
     * @throws IllegalArgumentException if {@code extent() != other.extent()}
     *
     * @implNote
     * The {@code values} are copied and subsequent chances to the {@code values}
     * are not reflected in the matrix, and vice-versa
     */
    default void assign(T[][][] values) {
        if (values.length != slices()) {
            throw new IllegalArgumentException(
                "Values must have the same number of slices: " +
                    values.length + " != " + slices()
            );
        }

        for (int s = slices(); --s >= 0;) {
            final var slice = values[s];
            if (slice.length != rows()) {
                throw new IllegalArgumentException(
                    "Values must have the same number of rows: " +
                        slice.length + " != " + rows()
                );
            }

            for (int r = rows(); --r >= 0;) {
                final var row = slice[r];
                if (row.length != cols()) {
                    throw new IllegalArgumentException(
                        "Values must have the same number of columns: " +
                            row.length + " != " + cols()
                    );
                }

                for (int c = cols(); --c >= 0;) {
                    set(s, r, c, row[c]);
                }
            }
        }
    }

    /**
     * Sets all cells to the state specified by {@code values}.
     *
     * @param value the value to be filled into the cells
     */
    default void assign(T value) {
        forEach((s, r, c) -> set(s, r, c, value));
    }

    /**
     * Assigns the result of a function to each cell
     * {@code x[slice, row, col] = f(x[slice, row, col], y[slice, row, col])}.
     *
     * @param y the secondary matrix to operate on.
     * @param f a function object taking as first argument the current cell's
     * value of {@code this}, and as second argument the current cell's value of
     * {@code y}
     * @throws IllegalArgumentException if {@code extent() != y.extent()}
     */
    default void assign(ObjectLattice3d<T> y, BinaryOperator<T> f) {
        requireNonNull(f);
        checkSameExtent(extent(), y.extent());

        forEach((s, r, c) ->
            set(s, r, c, f.apply(get(s, r, c), y.get(s, r, c)))
        );
    }

    /**
     * Updates this grid with the values of {@code a} which are transformed by
     * the given function {@code f}.
     * <pre>{@code
     * this[i, j, k] = f(a[i, j, k])
     * }</pre>
     * <pre>{@code
     * final ObjectGrid3d<Integer> ints = ObjectGrid3d
     *     .<Integer>dense()
     *     .create(10, 15, 40);
     *
     * final ObjectGrid3d<String> strings = ObjectGrid3d
     *     .<String>dense()
     *     .create(10, 15, 40);
     *
     * ints.forEach((s, r, c) -> ints.set(s, r, c, s*r*c));
     * strings.assign(ints, Object::toString);
     * }</pre>
     *
     * @param a the grid used for the update
     * @param f the mapping function
     * @throws IllegalArgumentException if {@code extent() != other.extent()}
     */
    default  <A> void assign(
        ObjectLattice3d<? extends A> a,
        Function<? super A, ? extends T> f
    ) {
        checkSameExtent(extent(), a.extent());
        forEach((s, r, c) -> set(s, r, c, f.apply(a.get(s, r, c))));
    }

    /**
     * Assigns the result of a function to each cell
     * {@code x[slice, row, col] = f(x[slice, row, col])}.
     *
     * @param f a function object taking as argument the current cell's value.
     */
    default void assign(UnaryOperator<T> f) {
        requireNonNull(f);
        forEach((s, r, c) -> set(s, r, c, f.apply(get(s, r, c))));
    }

    /**
     * Swaps each element {@code this[i, j]} with {@code other[i, j]}.
     *
     * @throws IllegalArgumentException if {@code extent() != other.extent()}.
     */
    default void swap(ObjectLattice3d<T> other) {
        checkSameExtent(extent(), other.extent());

        forEach((s, r, c) -> {
            final var tmp = get(s, r, c);
            set(s, r, c, other.get(s, r, c));
            other.set(s, r, c, tmp);
        });
    }

    /**
     * Applies a function to each cell and aggregates the results. Returns a
     * value <em>v</em> such that <em>v==a(size())</em> where
     * <em>a(i) == reduce(a(i - 1), f(get(slice, row, col)))</em> and
     * terminators are
     * <em>a(1) == f(get(0, 0, 0))</em>.
     * <p><b>Example:</b></p>
     * <pre>
     * 2 x 2 matrix
     * 0 1
     * 2 3
     *
     * // Sum(x[slice, row, col]*x[slice, row, col])
     * matrix.aggregate(Double::sum, a -> a*a) --> 14
     * </pre>
     *
     * @param reducer an aggregation function taking as first argument the
     * current aggregation and as second argument the transformed current cell
     * value
     * @param f a function transforming the current cell value
     * @return the aggregated measure or {@link OptionalDouble#empty()} if
     *         {@code size() == 0}
     */
    default Optional<T> reduce(BinaryOperator<T> reducer, UnaryOperator<T> f) {
        requireNonNull(reducer);
        requireNonNull(f);

        if (extent().nelements() == 0) {
            return Optional.empty();
        }

        T a = f.apply(get(slices() - 1, rows() - 1, cols() - 1));
        int d = 1;
        for (int s = slices(); --s >= 0;) {
            for (int r = rows(); --r >= 0;) {
                for (int c = cols() - d; --c >= 0;) {
                    a = reducer.apply(a, f.apply(get(s, r, c)));
                }
                d = 0;
            }
        }
        return Optional.ofNullable(a);
    }

    /**
     * Checks whether the given matrices have the same dimension and contains
     * the same values.
     *
     * @param other the second matrix to compare
     * @return {@code true} if the two given matrices are equal, {@code false}
     * otherwise
     */
    default boolean equals(ObjectLattice3d<T> other) {
        return extent().equals(other.extent()) &&
            allMatch((s, r, c) -> Objects.equals(get(s, r, c), other.get(s, r, c)));
    }

}
