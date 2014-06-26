package org.agilewiki.jactor2.core.impl.mtRequests;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.SyncOperation;
import org.agilewiki.jactor2.core.requests.SyncRequest;
import org.agilewiki.jactor2.core.requests.impl.RequestImpl;
import org.agilewiki.jactor2.core.util.Timer;

/**
 * Internal implementation of a SyncRequest.
 *
 * @param <RESPONSE_TYPE> The response value type.
 */
public class SyncRequestMtImpl<RESPONSE_TYPE> extends
        RequestMtImpl<RESPONSE_TYPE> implements SyncOperation<RESPONSE_TYPE> {

    private final SyncOperation<RESPONSE_TYPE> syncOperation;

    /**
     * Create a SyncRequestMtImpl and bind it to its target reactor.
     *
     * @param _syncOperation   The request being implemented.
     * @param _targetReactor The target reactor.
     */
    public SyncRequestMtImpl(final SyncOperation<RESPONSE_TYPE> _syncOperation,
            final Reactor _targetReactor) {
        super(_targetReactor);
        syncOperation = _syncOperation;
    }

    public SyncRequestMtImpl(final Reactor _targetReactor) {
        super(_targetReactor);
        syncOperation = this;
    }

    @Override
    public SyncOperation<RESPONSE_TYPE> asOperation() {
        return syncOperation;
    }

    @Override
    protected void processRequestMessage() throws Exception {
        final Timer timer = syncOperation.getTimer();
        final long start = timer.nanos();
        boolean success = false;
        final RESPONSE_TYPE result;
        try {
            result = syncOperation.processSyncOperation(this);
            success = true;
        } finally {
            timer.updateNanos(timer.nanos() - start, success);
        }

        processObjectResponse(result);
    }

    @Override
    public RESPONSE_TYPE processSyncOperation(final RequestImpl _requestImpl) throws Exception {
        if (this == syncOperation)
            throw new IllegalStateException();
        return syncOperation.processSyncOperation(_requestImpl);
    }
}
