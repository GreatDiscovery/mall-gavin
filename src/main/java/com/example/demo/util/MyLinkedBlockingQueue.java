package com.example.demo.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author gavin
 * @date 2019-09-12 21:24
 */
public class MyLinkedBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, Serializable {
    static class Node<E> {
        E item;
        Node<E> next;

        Node(E x) {
            item = x;
        }
    }

    private final int capacity;
    private final AtomicInteger count = new AtomicInteger();
    // 何时初始化的？
    transient Node<E> head;
    transient Node<E> last;
    private final ReentrantLock takeLock = new ReentrantLock();
    private final Condition notEmpty = takeLock.newCondition();
    private final ReentrantLock putLock = new ReentrantLock();
    private final Condition notFull = putLock.newCondition();

    private void signalNotEmpty() {
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            notEmpty.signal();
        } finally {
            takeLock.unlock();
        }

    }

    private void signalNotFull() {
        final ReentrantLock putLock = this.putLock;
        putLock.lock();
        try {
            notFull.signal();
        } finally {
            putLock.unlock();
        }
    }

    private void enqueue(Node<E> node) {
        last = last.next = node;
    }

    // head和last都是空的指针，没有值的
    private E dequeue() {
       Node<E> h = head;
       Node<E> first = h.next;
       h.next = h;
       head = first;
       E x = first.item;
       first.item = null;
       return x;
    }

    void fullyUnlock() {
        takeLock.unlock();
        putLock.unlock();
    }

    // 什么情况下会全锁住呢？在需要迭代的时候，全部锁住
    void fullyLock() {
        putLock.lock();
        takeLock.lock();
    }

    public MyLinkedBlockingQueue() {this(Integer.MAX_VALUE);}
    public MyLinkedBlockingQueue(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException();
        this.capacity = capacity;
        last = head = new Node<E>(null);
    }

    public MyLinkedBlockingQueue(Collection<? extends E> c) {
        this(Integer.MAX_VALUE);
        final ReentrantLock putLock = this.putLock;
        putLock.lock();
        try {
            int n = 0;
            for (E e : c) {
                if (e == null)
                    throw new NullPointerException();
                if (n == capacity) {
                    throw new IllegalStateException("Queue full");
                }
                enqueue(new Node<E>(e));
                ++n;
            }
            count.set(n);
        } finally {
            putLock.unlock();
        }

    }

    public int size() {return count.get();}

    public int remainingCapacity() {return capacity - count.get();}

    public void put(E e) throws InterruptedException {
        if (e == null) throw new NullPointerException();
        // 通过预先设置负值的此方法来检验是否存放成功了
        int c = -1;
        Node<E> node = new Node(e);
        final ReentrantLock putLock = this.putLock;
        final AtomicInteger count = this.count;
        putLock.lockInterruptibly();
        try {
            while (count.get() == capacity) {
                notFull.await();
            }
            enqueue(node);
            c = count.getAndIncrement();
            if (c + 1 < capacity) {
                notFull.signal();
            }
        } finally {
            putLock.unlock();
        }
        if (c == 0)
            signalNotEmpty();
    }


    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        if (e == null) throw new NullPointerException();
        long nanos = unit.toNanos(timeout);
        int c = -1;
        final ReentrantLock putLock = this.putLock;
        putLock.lockInterruptibly();
        try {
            // 如果队列满了，那么就是full，那么notFull条件队列就等待，阻塞线程，知道条件满足了，再唤醒
            while (count.get() == capacity) {
                if (nanos < 0)
                    return false;
                // 如果在给定时间内没有进入队列，那么就返回false
                nanos = notFull.awaitNanos(nanos);
            }
            enqueue(new Node<E>(e));
            c = count.getAndIncrement();
            if (c + 1 < capacity)
                notFull.signal();
        } finally {
            putLock.unlock();
        }
        if (c == 0)
            signalNotEmpty();
        return true;
    }

    public E take() throws InterruptedException {
        E x;
        int c = -1;
        final AtomicInteger count = this.count;
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lockInterruptibly();
        try {
            while (count.get() == 0) {
                notEmpty.await();
            }
            x = dequeue();
            c = count.getAndDecrement();
            if (c > 1)
                notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
        if (c == capacity)
            signalNotFull();
        return x;
    }

    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        E x = null;
        int c = -1;
        long nanos = unit.toNanos(timeout);
        final AtomicInteger count = this.count;
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lockInterruptibly();
        try {
            while (count.get() == 0) {
                if (nanos <= 0) {
                    return null;
                }
                nanos = notEmpty.awaitNanos(nanos);
            }
            x = dequeue();
            c = count.getAndDecrement();
            if (c > 1) {
                notEmpty.signal();
            }
        } finally {
            takeLock.unlock();
        }
        if (c == capacity) {
            signalNotFull();
        }
        return x;
    }

    public E poll() {
        final AtomicInteger count = this.count;
        if (count.get() == 0)
            return null;
        E x = null;
        int c = -1;
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            if (count.get() > 0) {
                x = dequeue();
                c = count.getAndDecrement();
                if (c > 1) {
                    notEmpty.signal();
                }
            }
        } finally {
            takeLock.unlock();
        }
        if (c == capacity) {
            signalNotFull();
        }
        return x;
    }

    public E peek() {
        if (count.get() == 0)
            return null;
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            Node<E> first = head.next;
            if (first == null)
                return null;
            else
                return first.item;
        } finally {
            takeLock.unlock();
        }
    }
    // 把某个点移除
    void unlink(Node<E> p, Node<E> trail) {
        p.item = null;
        trail.next = p.next;
        // 需要处理最后的last指针
        if (last == p) {
            last = trail;
        }
        // 队列已经不是满的状态，可以唤醒notFull干活了
        if (count.getAndDecrement() == capacity)
            notFull.signal();
    }

    public boolean remove(Object o) {
        if (o == null) return false;
        fullyLock();
        try {
            // for循环在try里面，如果有异常，跳出循环
            for (Node<E> trail = head, p = trail.next; p != null;
                 trail = p, p = p.next) {
                if (o.equals(p.item)) {
                    unlink(p, trail);
                    return true;
                }
            }
            return false;
        } finally {
            fullyUnlock();
        }
    }

    public boolean contains(Object o) {
        if (o == null) return false;
        fullyLock();
        try {
            for (Node<E> p = head.next; p != null; p = p.next)
                if (o.equals(p.item))
                    return true;
                return false;
        } finally {
            fullyUnlock();
        }
    }

    public Object[] toArray() {
       fullyLock();
       try {
           int size = count.get();
           Object[] a = new Object[size];
           int k = 0;
           for (Node<E> p = head.next; p != null; p = p.next) {
               a[k++] = p.item;
           }
           return  a;
       } finally {
           fullyUnlock();
       }
    }

    public String toString() {
        fullyLock();
        try {
            Node<E> p = head.next;
            if (p == null)
                return "[]";
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (;;) {
                E e = p.item;
                sb.append(e == this ? "(this Collection)" : e);
                p = p.next;
                if (p == null)
                    return sb.append("]").toString();
                sb.append(",").append(" ");
            }
        } finally {
            fullyUnlock();
        }
    }

    public void clear() {
        fullyLock();
        try {
            for (Node<E> p, h = head; (p = h.next) != null; h = p) {
                // 把指针指向自己，消除指针
                h.next = h;
                p.item = null;
            }
            head = last;
            if (count.getAndSet(0) == capacity)
                notFull.signal();
        } finally {
            fullyUnlock();
        }
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        return drainTo(c, Integer.MAX_VALUE);
    }

    // 把原先链表里的数据扔到指定collection里，并且clear表
    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        if (c == null)
            throw new NullPointerException();
        if (c == this)
            throw new IllegalArgumentException();
        if (maxElements <= 0)
            return 0;
        boolean signalNotFull = false;
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            int n = Math.min(maxElements, count.get());
            Node<E> h = head;
            int i = 0;
            try {
                while (i < n) {
                    Node<E> p = h.next;
                    c.add(p.item);
                    p.item = null;
                    h.next = h;
                    h = p;
                    ++i;
                }
            return n;
            } finally {
                if (i > 0)
                    head = h;
                signalNotFull = (count.getAndAdd(-i) == capacity);
            }
        } finally {
            takeLock.unlock();
            if (signalNotFull)
                signalNotFull();
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }


    private class Itr implements Iterator<E> {

        private Node<E> current;
        private Node<E> lastRet;
        private E currentElement;

        Itr() {
            fullyLock();
            try {
                current = head.next;
                if (current != null)
                    currentElement = current.item;
            } finally {
                fullyUnlock();
            }
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }
        // 这个方法比较怪
        private Node<E> nextNode(Node<E> p) {
            for (;;) {
                Node<E> s = p.next;
                if (s == p)
                    return head.next;
                if (s == null || s.item != null)
                    return s;
                p = s;
            }
        }
        @Override
        public E next() {
            fullyLock();
            try {
                if (current == null)
                    throw new NoSuchElementException();
                E x = currentElement;
                lastRet = current;
                current = nextNode(current);
                currentElement = (current == null) ? null : current.item;
                return x;
            } finally {
                fullyUnlock();
            }
        }

        public void remove() {
            if (lastRet == null)
                throw new IllegalStateException();
            fullyLock();
            try {
                Node<E> node = lastRet;
                lastRet = null;
                for (Node<E> trail = head, p = trail.next;
                p != null; trail = p, p = p.next) {
                    if (p == node) {
                        unlink(p, trail);
                        break;
                    }
                }
            } finally {
                fullyUnlock();
            }
        }
    }

    @Override
    public boolean offer(E e) {
        return false;
    }

    private void writeObject(ObjectOutputStream s) throws IOException, ClassNotFoundException {
        fullyLock();
        try {
            s.defaultWriteObject();
            for (Node<E> p = head.next; p != null; p = p.next)
                s.writeObject(p.item);
            s.writeObject(null);
        } finally {
            fullyUnlock();
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        count.set(0);
        last = head = new Node<>(null);
        for (;;) {
            E item = (E) s.readObject();
            if (item == null)
                break;
            add(item);
        }
    }
}
