/*
 * Copyright (c) 2016 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.lazy;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.jcip.annotations.Immutable;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.Counter;
import org.eclipse.collections.impl.EmptyIterator;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.utility.Iterate;

@Immutable
public final class CompositeIterable<E>
        extends AbstractLazyIterable<E>
{
    private final MutableList<Iterable<E>> iterables;

    private CompositeIterable(MutableList<Iterable<E>> newIterables)
    {
        this.iterables = newIterables;
    }

    public CompositeIterable()
    {
        this(FastList.<Iterable<E>>newList());
    }

    public static <T> CompositeIterable<T> with(Iterable<T>... iterables)
    {
        return new CompositeIterable<T>(FastList.newListWith(iterables));
    }

    public void each(final Procedure<? super E> procedure)
    {
        this.iterables.forEach(new Procedure<Iterable<E>>()
        {
            public void value(Iterable<E> iterable)
            {
                Iterate.forEach(iterable, procedure);
            }
        });
    }

    @Override
    public void forEachWithIndex(final ObjectIntProcedure<? super E> objectIntProcedure)
    {
        final Counter index = new Counter();
        this.iterables.forEach(new Procedure<Iterable<E>>()
        {
            public void value(Iterable<E> iterable)
            {
                Iterate.forEach(iterable, new Procedure<E>()
                {
                    public void value(E object)
                    {
                        objectIntProcedure.value(object, index.getCount());
                        index.increment();
                    }
                });
            }
        });
    }

    @Override
    public <P> void forEachWith(final Procedure2<? super E, ? super P> procedure, final P parameter)
    {
        this.iterables.forEach(new Procedure<Iterable<E>>()
        {
            public void value(Iterable<E> iterable)
            {
                Iterate.forEachWith(iterable, procedure, parameter);
            }
        });
    }

    @Override
    public boolean anySatisfy(final Predicate<? super E> predicate)
    {
        return this.iterables.anySatisfy(new Predicate<Iterable<E>>()
        {
            public boolean accept(Iterable<E> each)
            {
                return Iterate.anySatisfy(each, predicate);
            }
        });
    }

    @Override
    public boolean allSatisfy(final Predicate<? super E> predicate)
    {
        return this.iterables.allSatisfy(new Predicate<Iterable<E>>()
        {
            public boolean accept(Iterable<E> each)
            {
                return Iterate.allSatisfy(each, predicate);
            }
        });
    }

    @Override
    public boolean noneSatisfy(final Predicate<? super E> predicate)
    {
        return this.iterables.noneSatisfy(new Predicate<Iterable<E>>()
        {
            public boolean accept(Iterable<E> each)
            {
                return Iterate.anySatisfy(each, predicate);
            }
        });
    }

    @Override
    public E detect(Predicate<? super E> predicate)
    {
        for (int i = 0; i < this.iterables.size(); i++)
        {
            Iterable<E> eachIterable = this.iterables.get(i);
            E result = Iterate.detect(eachIterable, predicate);
            if (result != null)
            {
                return result;
            }
        }
        return null;
    }

    public void add(Iterable<E> iterable)
    {
        this.iterables.add(iterable);
    }

    public Iterator<E> iterator()
    {
        return new CompositeIterator(this.iterables);
    }

    private final class CompositeIterator
            implements Iterator<E>
    {
        private final Iterator<Iterable<E>> iterablesIterator;
        private Iterator<E> innerIterator;

        private CompositeIterator(MutableList<Iterable<E>> iterables)
        {
            this.iterablesIterator = iterables.listIterator();
            this.innerIterator = EmptyIterator.getInstance();
        }

        public boolean hasNext()
        {
            while (true)
            {
                if (this.innerIterator.hasNext())
                {
                    return true;
                }
                if (!this.iterablesIterator.hasNext())
                {
                    return false;
                }
                this.innerIterator = this.iterablesIterator.next().iterator();
            }
        }

        public E next()
        {
            if (!this.hasNext())
            {
                throw new NoSuchElementException();
            }
            return this.innerIterator.next();
        }

        public void remove()
        {
            throw new UnsupportedOperationException("Cannot remove from a composite iterator");
        }
    }
}
