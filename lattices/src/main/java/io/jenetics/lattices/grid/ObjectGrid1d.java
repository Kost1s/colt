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
package io.jenetics.lattices.grid;

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.jenetics.lattices.array.DenseObjectArray;
import io.jenetics.lattices.array.ObjectArray;

/**
 * Object grid class.
 *
 * @param <T> the grid element type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0
 */
public class ObjectGrid1d<T> implements Grid1d {

    /**
     * The structure, which defines the <em>extent</em> of the grid and the
     * <em>order</em> which determines the index mapping {@code N -> N}.
     */
    protected final Structure1d structure;

    /**
     * The underlying {@code double[]} array.
     */
    protected final ObjectArray<T> array;

    /**
     * Create a new 1-d grid with the given {@code structure} and element
     * {@code array}.
     *
     * @param structure the matrix structure
     * @param array the element array
     * @throws IllegalArgumentException if the size of the given {@code array}
     *         is not able to hold the required number of elements. It is still
     *         possible that an {@link IndexOutOfBoundsException} is thrown when
     *         the defined order of the grid tries to access an array index,
     *         which is not within the bounds of the {@code array}.
     * @throws NullPointerException if one of the arguments is {@code null}
     */
    public ObjectGrid1d(final Structure1d structure, final ObjectArray<T> array) {
        if (structure.extent().size() > array.length()) {
            throw new IllegalArgumentException(
                "The number of available elements is smaller than the number of " +
                    "required grid cells: %d > %d."
                        .formatted(structure.extent().size(), array.length())
            );
        }

        this.structure = structure;
        this.array = array;
    }

    @Override
    public Structure1d structure() {
        return structure;
    }

    /**
     * Return the underlying element array.
     *
     * @return the underlying element array
     */
    public ObjectArray<T> array() {
        return array;
    }

    /**
     * Returns the matrix cell value at coordinate {@code index}.
     *
     * @param index the index of the cell
     * @return the value of the specified cell
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     *         bounds
     */
    public T get(final int index) {
        return array.get(order().index(index));
    }

    /**
     * Sets the matrix cell at coordinate {@code index} to the specified
     * {@code value}.
     *
     * @param index the index of the cell
     * @param value  the value to be filled into the specified cell
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     *         bounds
     */
    public void set(final int index, final T value) {
        array.set(order().index(index),  value);
    }

    /**
     * Return an iterator of the grid elements.
     *
     * @return a new grid element iterator
     */
    public Iterator<T> iterator() {
        return new ObjectGrid1dIterator<>(this);
    }

    /**
     * Return a new stream of the grid elements.
     *
     * @return a new grid element stream
     */
    public Stream<T> stream() {
        return StreamSupport.stream(
            ((Iterable<T>)this::iterator).spliterator(),
            false
        );
    }

    /**
     * Replaces all cell values of the receiver with the values of another
     * matrix. Both matrices must have the same number of rows and columns.
     *
     * @param other the source matrix to copy from (maybe identical to the
     *        receiver).
     * @throws IllegalArgumentException if {@code !extent().equals(other.extent())}
     */
    public void assign(final ObjectGrid1d<? extends T> other) {
        if (other == this) {
            return;
        }
        Grids.checkSameExtent(this, other);

        // Fast track assign.
        if (order() instanceof StrideOrder1d so1 &&
            other.order() instanceof StrideOrder1d so2 &&
            so1.stride().value() == 1 &&
            so2.stride().value() == 1 &&
            array instanceof DenseObjectArray<T> doa1 &&
            other.array instanceof DenseObjectArray<?> doa2)
        {
            System.arraycopy(
                doa2.elements(), so2.start().value(),
                doa1.elements(), so1.start().value(), size()
            );
        } else {
            forEach(i -> set(i, other.get(i)));
        }
    }

    /**
     * Sets all cells to the state specified by {@code values}.
     *
     * @param values the values to be filled into the cells
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public final void assign(final T... values) {
        final var size = Math.min(values.length, size());

        // Fast track assign.
        if (order() instanceof StrideOrder1d so1 &&
            so1.stride().value() == 1 &&
            array instanceof DenseObjectArray<T> a1)
        {
            System.arraycopy(
                values, 0, a1.elements(),
                so1.start().value(), size
            );
        } else {
            forEach(i -> set(i, values[i]));
        }
    }

    /**
     * Sets all cells to the state specified by {@code values}.
     *
     * @param value the value to be filled into the cells
     */
    public void assign(final T value) {
        forEach(i -> set(i, value));
    }

    /**
     * Assigns the result of a function to each cell.
     * <pre>{@code
     * this[i] = f(this[i])
     * }</pre>
     *
     * @param f a function object taking as argument the current cell's value.
     */
    public void assign(final UnaryOperator<T> f) {
        requireNonNull(f);
        forEach(i -> set(i, f.apply(get(i))));
    }

    /**
     * Updates this grid with the values of {@code a} which are transformed by
     * the given function {@code f}.
     * <pre>{@code
     * this[i] = f(this[i], a[i])
     * }</pre>
     *
     * @param a the grid used for the update
     * @param f the combiner function
     */
    public void assign(final ObjectGrid1d<? extends T> a, final BinaryOperator<T> f) {
        Grids.checkSameExtent(this, a);
        forEach(i -> set(i, f.apply(get(i), a.get(i))));
    }

    /**
     * Swaps each element {@code this[i]} with {@code other[i]}.
     *
     * @throws IllegalArgumentException if {@code size() != other.size()}.
     */
    public void swap(final ObjectGrid1d<T> other) {
        Grids.checkSameExtent(this, other);

        forEach(i -> {
            final var tmp = get(i);
            set(i, other.get(i));
            other.set(i, tmp);
        });
    }

    /**
     * Checks whether the given matrices have the same dimension and contains
     * the same values.
     *
     * @param other the second matrix to compare
     * @return {@code true} if the two given matrices are equal, {@code false}
     *         otherwise
     */
    public boolean equals(final ObjectGrid1d<?> other) {

        return extent().equals(other.extent()) &&
            allMatch(i -> Objects.equals(get(i), other.get(i)));
    }

    @Override
    public int hashCode() {
        final int[] hash = new int[] { 37 };
        forEach(i -> hash[0] += Objects.hashCode(get(i))*17);
        return hash[0];
    }

    @Override
    public boolean equals(final Object object) {
        return object == this ||
            object instanceof ObjectGrid1d<?> grid &&
            equals(grid);
    }

    @Override
    public String toString() {
        final var out = new StringBuilder();
        out.append("[");
        for (int i = 0; i < size(); ++i) {
            out.append(get(i));
            if (i < size() - 1) {
                out.append(", ");
            }
        }
        out.append("]");
        return out.toString();
    }

    /**
     * Return a factory for creating dense 1-d object grids.
     *
     * @param __ not used (Java trick for getting "reified" element type)
     * @return the dense object factory
     * @param <T> the grid element type
     */
    @SuppressWarnings("varargs")
    @SafeVarargs
    public static <T> Factory1d<ObjectGrid1d<T>> dense(final T... __) {
        return struct -> new ObjectGrid1d<T>(
            struct,
            DenseObjectArray.ofSize(struct.extent().size(), __)
        );
    }

}
