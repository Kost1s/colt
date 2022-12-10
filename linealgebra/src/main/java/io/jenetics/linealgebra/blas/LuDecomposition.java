/*
 * Java Linear Algebra Library (@__identifier__@).
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
package io.jenetics.linealgebra.blas;

import static java.util.Objects.requireNonNull;
import static io.jenetics.linealgebra.blas.Algebra.checkRectangular;
import static io.jenetics.linealgebra.blas.Algebra.isNonSingular;

import io.jenetics.linealgebra.matrix.DoubleMatrix2d;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public final class LuDecomposition {
    private final DoubleMatrix2d lu;

    private int[] work1;

    private LuDecomposition(final DoubleMatrix2d lu) {
        this.lu = requireNonNull(lu);
    }

    public static LuDecomposition of(final DoubleMatrix2d a) {
        final var matrix = a.copy();
        LU.decompose(matrix);

        return new LuDecomposition(matrix);
    }

}
