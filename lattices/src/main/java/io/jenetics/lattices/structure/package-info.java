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

/**
 * This package contains classes, which allows to use one dimensional arrays
 * (or array like structures) as storage for multidimensional lattices/grids.
 * The following code snippet shows how to do this for a 2-d double array.
 * <pre>{@code
 * final var structure = new Structure2d(new Extent2d(10, 34));
 * final var layout = structure.layout();
 *
 * final var values = new double[structure.extent().size()];
 * values[layout.offset(3, 5)] = Math.PI;
 * assert values[layout.offset(3, 5)] == Math.PI;
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0
 */
package io.jenetics.lattices.structure;
