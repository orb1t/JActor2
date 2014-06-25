package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.requests.impl.RequestImpl;

/**
 * A synchronous operation.
 */
public interface SyncOperation<RESPONSE_TYPE> extends Operation<RESPONSE_TYPE> {
    /**
     * The processSyncRequest method will be invoked by the target Reactor on its own thread.
     *
     * @return The value returned by the target blades.
     */
    RESPONSE_TYPE processSyncOperation(final RequestImpl _requestImpl) throws Exception;
}
