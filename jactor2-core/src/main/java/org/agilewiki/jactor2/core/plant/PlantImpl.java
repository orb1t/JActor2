package org.agilewiki.jactor2.core.plant;

import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorImpl;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncRequestImpl;
import org.agilewiki.jactor2.core.requests.RequestImpl;
import org.agilewiki.jactor2.core.requests.SyncRequest;

/**
 * Internal implementation for Plant.
 */
abstract public class PlantImpl {

    private static volatile PlantImpl singleton;

    /**
     * Returns this singleton.
     *
     * @return This singleton.
     */
    public static PlantImpl getSingleton() {
        return singleton;
    }

    /**
     * Create the singleton.
     */
    public PlantImpl() {
        if (singleton != null) {
            throw new IllegalStateException("the singleton already exists");
        }
        singleton = this;
    }

    /**
     * Returns the Plant's internal reactor.
     *
     * @return The reactor belonging to the singleton.
     */
    abstract public NonBlockingReactor getInternalReactor();

    /**
     * Close the Plant.
     */
    public void close() throws Exception {
        singleton = null;
    }

    abstract public ReactorImpl getCurrentReactorImpl();

    abstract public ReactorImpl createNonBlockingReactorImpl(final NonBlockingReactor _parentReactor,
                                                             int _initialOutboxSize, int _initialLocalQueueSize);

    abstract public ReactorImpl createBlockingReactorImpl(final NonBlockingReactor _parentReactor,
                                                          int _initialOutboxSize, int _initialLocalQueueSize);

    abstract public ReactorImpl createIsolationReactorImpl(final NonBlockingReactor _parentReactor,
                                                           int _initialOutboxSize, int _initialLocalQueueSize);

    abstract public ReactorImpl createSwingBoundReactorImpl(final NonBlockingReactor _parentReactor,
                                                            int _initialOutboxSize, int _initialLocalQueueSize);

    abstract public ReactorImpl createThreadBoundReactorImpl(final NonBlockingReactor _parentReactor,
                                                             int _initialOutboxSize, int _initialLocalQueueSize,
                                                             Runnable _boundProcessor);

    abstract public <RESPONSE_TYPE> RequestImpl<RESPONSE_TYPE> createSyncRequestImpl(
            SyncRequest<RESPONSE_TYPE> _syncRequest, Reactor _targetReactor);

    abstract public <RESPONSE_TYPE> AsyncRequestImpl<RESPONSE_TYPE> createAsyncRequestImpl(
            AsyncRequest<RESPONSE_TYPE> _asyncRequest, Reactor _targetReactor);

    /**
     * Returns 16.
     *
     * @return The reactor default initial local message queue size.
     */
    abstract public int getInitialLocalMessageQueueSize();

    /**
     * Returns 16.
     *
     * @return The reactor default initial buffer size.
     */
    abstract public int getInitialBufferSize();

    /**
     * Return the scheduler that is a part of the Plant's configuration.
     *
     * @return The scheduler.
     */
    abstract public PlantScheduler getPlantScheduler();
}
