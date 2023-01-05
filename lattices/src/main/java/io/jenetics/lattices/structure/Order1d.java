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

import static java.util.Objects.requireNonNull;

/**
 * Represents the <em>row-major</em> order.
 *
 * @param start the index of the first element
 * @param stride the number of indexes between any two elements
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0
 */
public record Order1d(Index1d start, Stride1d stride) {

    /**
     * The default order.
     */
    public static final Order1d DEFAULT = new Order1d(0, 1);

    public Order1d {
        requireNonNull(start);
        requireNonNull(stride);
    }

    public Order1d(final int start, final int stride) {
        this(new Index1d(start), new Stride1d(stride));
    }

    /**
     * Return the position of the element with the given relative {@code rank}
     * within the (virtual or non-virtual) internal 1-d array.
     *
     * @param index the index of the element.
     * @return the (linearized) index of the given {@code index}
     */
    public int index(final int index) {
        return start.value() + index*stride.value();
    }

    /**
     * Return the <em>array</em> index from the given <em>dimensional</em> index.
     *
     * @param index the dimensional index
     * @return the array index
     */
    public int index(final Index1d index) {
        return index(index.value());
    }

}
