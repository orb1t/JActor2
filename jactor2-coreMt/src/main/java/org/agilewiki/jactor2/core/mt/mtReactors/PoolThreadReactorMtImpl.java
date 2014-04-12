package org.agilewiki.jactor2.core.mt.mtReactors;

import org.agilewiki.jactor2.core.mt.mtPlant.PlantMtImpl;
import org.agilewiki.jactor2.core.reactors.MigrationException;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.PoolThreadReactorImpl;
import org.agilewiki.jactor2.core.reactors.ReactorImpl;
import org.agilewiki.jactor2.core.requests.RequestImpl;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

abstract public class PoolThreadReactorMtImpl extends ReactorMtImpl implements PoolThreadReactorImpl {
    private Runnable onIdle;

    /**
     * Create an PoolThreadReactorMtImpl.
     *
     * @param _parentReactor         The parent reactor.
     * @param _initialOutboxSize     The initial buffer size for outgoing messages.
     * @param _initialLocalQueueSize The initial local queue size.
     */
    public PoolThreadReactorMtImpl(
            NonBlockingReactor _parentReactor, int _initialOutboxSize, int _initialLocalQueueSize) {
        super(_parentReactor, _initialOutboxSize, _initialLocalQueueSize);
    }

    @Override
    public boolean isIdler() {
        return onIdle != null;
    }

    @Override
    protected void notBusy() throws Exception {
        if ((onIdle != null) && inbox.isIdle()) {
            flush(true);
            onIdle.run();
        }
        flush(true);
    }

    @Override
    protected void afterAdd() {
        if (threadReference == null) {
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err.println("this=" + this);
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            throw new NullPointerException();
        }
        if (threadReference.get() == null) {
            PlantMtImpl.getSingleton().submit(this);
        }
    }

    /**
     * Flushes buffered messages, if any.
     * Returns true if there was any.
     *
     * @param _mayMigrate True when thread migration is allowed.
     * @return True when one or more buffered request/result was delivered.
     */
    protected boolean flush(final boolean _mayMigrate) throws Exception {
        boolean result = false;
        final Iterator<Map.Entry<ReactorImpl, ArrayDeque<RequestImpl>>> iter = outbox
                .getIterator();
        if (iter != null) {
            while (iter.hasNext()) {
                result = true;
                final Map.Entry<ReactorImpl, ArrayDeque<RequestImpl>> entry = iter
                        .next();
                final ReactorImpl target = entry.getKey();
                final ArrayDeque<RequestImpl> messages = entry.getValue();
                iter.remove();
                if (!iter.hasNext() && _mayMigrate
                        && (target instanceof PoolThreadReactorImpl)) {
                    if (!target.isRunning()) {
                        final Thread currentThread = threadReference.get();
                        final PoolThreadReactorImpl targ = (PoolThreadReactorImpl) target;
                        final AtomicReference<Thread> targetThreadReference = targ
                                .getThreadReference();
                        if ((targetThreadReference.get() == null)
                                && targetThreadReference.compareAndSet(null,
                                currentThread)) {
                            while (!messages.isEmpty()) {
                                final RequestImpl m = messages.poll();
                                targ.unbufferedAddMessage(m, true);
                            }
                            throw new MigrationException(targ);
                        }
                    }
                }
                target.unbufferedAddMessages(messages);
            }
        }
        return result;
    }

    /**
     * The object to be run when the inbox is emptied and before the threadReference is cleared.
     */
    public Runnable getOnIdle() {
        return onIdle;
    }

    public void setOnIdle(Runnable onIdle) {
        this.onIdle = onIdle;
    }
}
