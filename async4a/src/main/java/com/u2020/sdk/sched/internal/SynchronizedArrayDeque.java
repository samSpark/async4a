package com.u2020.sdk.sched.internal;

import com.u2020.sdk.sched.internal.annotation.NonNull;
import com.u2020.sdk.sched.internal.annotation.Nullable;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;

public class SynchronizedArrayDeque<E> extends ArrayDeque<E> {

    @Override
    public synchronized void addFirst(E e) {
        super.addFirst(e);
    }

    @Override
    public synchronized void addLast(E e) {
        super.addLast(e);
    }

    @Override
    public synchronized boolean offerFirst(E e) {
        return super.offerFirst(e);
    }

    @Override
    public synchronized boolean offerLast(E e) {
        return super.offerLast(e);
    }

    @Override
    public synchronized E removeFirst() {
        return super.removeFirst();
    }

    @Override
    public synchronized E removeLast() {
        return super.removeLast();
    }

    @Nullable
    @Override
    public synchronized E pollFirst() {
        return super.pollFirst();
    }

    @Nullable
    @Override
    public synchronized E pollLast() {
        return super.pollLast();
    }

    @Override
    public synchronized E getFirst() {
        return super.getFirst();
    }

    @Override
    public synchronized E getLast() {
        return super.getLast();
    }

    @Nullable
    @Override
    public synchronized E peekFirst() {
        return super.peekFirst();
    }

    @Nullable
    @Override
    public synchronized E peekLast() {
        return super.peekLast();
    }

    @Override
    public synchronized boolean removeFirstOccurrence(@Nullable Object o) {
        return super.removeFirstOccurrence(o);
    }

    @Override
    public synchronized boolean removeLastOccurrence(@Nullable Object o) {
        return super.removeLastOccurrence(o);
    }

    @Override
    public synchronized boolean add(E e) {
        return super.add(e);
    }

    @Override
    public synchronized boolean offer(E e) {
        return super.offer(e);
    }

    @Override
    public synchronized E remove() {
        return super.remove();
    }

    @Nullable
    @Override
    public synchronized E poll() {
        return super.poll();
    }

    @Override
    public synchronized E element() {
        return super.element();
    }

    @Nullable
    @Override
    public synchronized E peek() {
        return super.peek();
    }

    @Override
    public synchronized void push(E e) {
        super.push(e);
    }

    @Override
    public synchronized E pop() {
        return super.pop();
    }

    @Override
    public synchronized int size() {
        return super.size();
    }

    @Override
    public synchronized boolean isEmpty() {
        return super.isEmpty();
    }

    @NonNull
    @Override
    public synchronized Iterator<E> iterator() {
        return super.iterator();
    }

    @NonNull
    @Override
    public synchronized Iterator<E> descendingIterator() {
        return super.descendingIterator();
    }

    @Override
    public synchronized boolean contains(@Nullable Object o) {
        return super.contains(o);
    }

    @Override
    public synchronized boolean remove(@Nullable Object o) {
        return super.remove(o);
    }

    @Override
    public synchronized void clear() {
        super.clear();
    }

    @NonNull
    @Override
    public synchronized Object[] toArray() {
        return super.toArray();
    }

    @NonNull
    @Override
    public synchronized <T> T[] toArray(@NonNull T[] a) {
        return super.toArray(a);
    }

    @NonNull
    @Override
    public synchronized ArrayDeque<E> clone() {
        return super.clone();
    }

    @NonNull
    @Override
    public synchronized Spliterator<E> spliterator() {
        return super.spliterator();
    }

    @Override
    public synchronized boolean containsAll(@NonNull Collection<?> c) {
        return super.containsAll(c);
    }

    @Override
    public synchronized boolean addAll(@NonNull Collection<? extends E> c) {
        return super.addAll(c);
    }

    @Override
    public synchronized boolean removeAll(@NonNull Collection<?> c) {
        return super.removeAll(c);
    }

    @Override
    public synchronized boolean retainAll(@NonNull Collection<?> c) {
        return super.retainAll(c);
    }

    @NonNull
    @Override
    public synchronized String toString() {
        return super.toString();
    }
}
