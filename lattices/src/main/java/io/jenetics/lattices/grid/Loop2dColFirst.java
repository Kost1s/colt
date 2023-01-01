package io.jenetics.lattices.grid;

import static java.util.Objects.requireNonNull;

import io.jenetics.lattices.function.IntIntConsumer;
import io.jenetics.lattices.function.IntIntPredicate;
import io.jenetics.lattices.structure.Extent2d;
import io.jenetics.lattices.structure.Range2d;

/**
 * Column-major loop implementation. he rows and columns are iterated forward.
 *
 * @param range the range which defines the boundaries of the loop
 */
record Loop2dColFirst(Range2d range) implements Loop2d {

    /**
     * Column-major implementation of the loop strategy
     *
     * @param extent the extent which defines the boundaries of the loop
     */
    Loop2dColFirst(final Extent2d extent) {
        this(new Range2d(extent));
    }

    @Override
    public void forEach(final IntIntConsumer action) {
        requireNonNull(action);

        for (int c = range.start().col(),
             w = range.start().col() + range.extent().cols();
             c < w; ++c)
        {
            for (int r = range.start().row(),
                 h = range.start().row() + range.extent().rows();
                 r < h; ++r)
            {
                action.accept(r, c);
            }
        }
    }

    @Override
    public boolean anyMatch(final IntIntPredicate predicate) {
        requireNonNull(predicate);

        for (int c = range.start().col(),
             w = range.start().col() + range.extent().cols();
             c < w; ++c)
        {
            for (int r = range.start().row(),
                 h = range.start().row() + range.extent().rows();
                 r < h; ++r)
            {
                if (predicate.test(r, c)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean allMatch(final IntIntPredicate predicate) {
        requireNonNull(predicate);

        for (int c = range.start().col(),
             w = range.start().col() + range.extent().cols();
             c < w; ++c)
        {
            for (int r = range.start().row(),
                 h = range.start().row() + range.extent().rows();
                 r < h; ++r)
            {
                if (!predicate.test(r, c)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public boolean nonMatch(final IntIntPredicate predicate) {
        requireNonNull(predicate);

        for (int c = range.start().col(),
             w = range.start().col() + range.extent().cols();
             c < w; ++c)
        {
            for (int r = range.start().row(),
                 h = range.start().row() + range.extent().rows();
                 r < h; ++r)
            {
                if (predicate.test(r, c)) {
                    return false;
                }
            }
        }

        return true;
    }
}
