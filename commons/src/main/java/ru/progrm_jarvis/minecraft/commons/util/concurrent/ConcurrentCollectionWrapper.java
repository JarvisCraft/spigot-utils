package ru.progrm_jarvis.minecraft.commons.util.concurrent;


import lombok.NonNull;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ConcurrentCollectionWrapper<E, T extends Collection<E>>
        extends ConcurrentWrapper<T> implements Collection<E> {

    public ConcurrentCollectionWrapper(@NonNull final T wrapped) {
        super(wrapped);
    }

    @Override
    public int size() {
        readLock.lock();
        try {
            return wrapped.size();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        readLock.lock();
        try {
            return wrapped.isEmpty();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean contains(final Object o) {
        readLock.lock();
        try {
            return wrapped.contains(o);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    @Nonnull
    public Iterator<E> iterator() {
        return new ConcurrentIterator(wrapped.iterator());
    }

    @Override
    public void forEach(@NonNull final Consumer<? super E> action) {
        readLock.lock();
        try {
            wrapped.forEach(action);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    @Nonnull public Object[] toArray() {
        readLock.lock();
        try {
            return wrapped.toArray();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    @Nonnull public <R> R[] toArray(@NonNull final R[] a) {
        readLock.lock();
        try {
            //noinspection SuspiciousToArrayCall
            return wrapped.toArray(a);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean add(final E e) {
        writeLock.lock();
        try {
            return wrapped.add(e);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean remove(final Object o) {
        writeLock.lock();
        try {
            return wrapped.remove(o);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean containsAll(@NonNull final Collection<?> c) {
        readLock.lock();
        try {
            return wrapped.containsAll(c);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean addAll(@NonNull final Collection<? extends E> c) {
        writeLock.lock();
        try {
            return wrapped.addAll(c);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean retainAll(@NonNull final Collection<?> c) {
        writeLock.lock();
        try {
            return wrapped.retainAll(c);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean removeAll(@NonNull final Collection<?> c) {
        writeLock.lock();
        try {
            return wrapped.removeAll(c);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean removeIf(@NonNull final Predicate<? super E> filter) {
        writeLock.lock();
        try {
            return wrapped.removeIf(filter);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void clear() {
        writeLock.lock();
        try {
            wrapped.clear();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Spliterator<E> spliterator() {
        readLock.lock();
        try {
            return wrapped.spliterator();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Stream<E> stream() {
        readLock.lock();
        try {
            return wrapped.stream();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Stream<E> parallelStream() {
        readLock.lock();
        try {
            return wrapped.parallelStream();
        } finally {
            readLock.unlock();
        }
    }

    protected class ConcurrentIterator extends ConcurrentWrapper<Iterator<E>> implements Iterator<E> {

        public ConcurrentIterator(@NonNull final Iterator<E> wrapped) {
            super(wrapped);
        }

        @Override
        public boolean hasNext() {
            readLock.lock();
            try {
                return wrapped.hasNext();
            } finally {
                readLock.unlock();
            }
        }

        @Override
        public E next() {
            readLock.lock();
            try {
                return wrapped.next();
            } finally {
                readLock.unlock();
            }
        }

        @Override
        public void remove() {
            writeLock.lock();
            try {
                wrapped.remove();
            } finally {
                writeLock.unlock();
            }
        }

        @Override
        public void forEachRemaining(@NonNull final Consumer<? super E> action) {
            writeLock.lock();
            try {
                wrapped.forEachRemaining(action);
            } finally {
                writeLock.unlock();
            }
        }
    }
}
