/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.bimap;

import org.eclipse.collections.api.bag.ImmutableBagIterable;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.map.ImmutableMapIterable;
import org.eclipse.collections.api.multimap.set.ImmutableSetMultimap;
import org.eclipse.collections.api.partition.set.PartitionImmutableSet;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.tuple.Pair;

/**
 * A {@link BiMap} whose contents cannot be altered after initialization.
 *
 * @since 4.2
 */
public interface ImmutableBiMap<K, V> extends BiMap<K, V>, ImmutableMapIterable<K, V>
{
    ImmutableBiMap<K, V> newWithKeyValue(K key, V value);

    ImmutableBiMap<K, V> newWithAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues);

    ImmutableBiMap<K, V> newWithAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValuePairs);

    ImmutableBiMap<K, V> newWithoutKey(K key);

    ImmutableBiMap<K, V> newWithoutAllKeys(Iterable<? extends K> keys);

    ImmutableBiMap<V, K> inverse();

    ImmutableSetMultimap<V, K> flip();

    ImmutableBiMap<V, K> flipUniqueValues();

    ImmutableBiMap<K, V> tap(Procedure<? super V> procedure);

    ImmutableBiMap<K, V> select(Predicate2<? super K, ? super V> predicate);

    ImmutableBiMap<K, V> reject(Predicate2<? super K, ? super V> predicate);

    <K2, V2> ImmutableBiMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function);

    <R> ImmutableBiMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function);

    ImmutableSet<V> select(Predicate<? super V> predicate);

    <P> ImmutableSet<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    ImmutableSet<V> reject(Predicate<? super V> predicate);

    <P> ImmutableSet<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    PartitionImmutableSet<V> partition(Predicate<? super V> predicate);

    <P> PartitionImmutableSet<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter);

    <S> ImmutableSet<S> selectInstancesOf(Class<S> clazz);

    <V1> ImmutableBagIterable<V1> collect(Function<? super V, ? extends V1> function);

    <P, V1> ImmutableBagIterable<V1> collectWith(Function2<? super V, ? super P, ? extends V1> function, P parameter);

    <V1> ImmutableBagIterable<V1> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends V1> function);

    <V1> ImmutableBagIterable<V1> flatCollect(Function<? super V, ? extends Iterable<V1>> function);

    <V1> ImmutableSetMultimap<V1, V> groupBy(Function<? super V, ? extends V1> function);

    <V1> ImmutableSetMultimap<V1, V> groupByEach(Function<? super V, ? extends Iterable<V1>> function);

    <VV> ImmutableBiMap<VV, V> groupByUniqueKey(Function<? super V, ? extends VV> function);

    <S> ImmutableSet<Pair<V, S>> zip(Iterable<S> that);

    ImmutableSet<Pair<V, Integer>> zipWithIndex();
}
