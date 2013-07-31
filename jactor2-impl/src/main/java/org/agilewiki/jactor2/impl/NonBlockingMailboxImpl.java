package org.agilewiki.jactor2.impl;

import org.agilewiki.jactor2.core.NonBlockingMailbox;
import org.slf4j.Logger;

public class NonBlockingMailboxImpl extends UnboundMailboxImpl implements NonBlockingMailbox {

    /**
     * Create a mailbox.
     *
     * @param _onIdle                Object to be run when the inbox is emptied, or null.
     * @param _factory               The factory of this object.
     * @param _log                   The Mailbox log.
     * @param _initialBufferSize     Initial size of the outbox for each unique message destination.
     * @param _initialLocalQueueSize The initial number of slots in the local queue.
     */
    public NonBlockingMailboxImpl(Runnable _onIdle,
                                  JAMailboxFactory _factory,
                                  Logger _log,
                                  int _initialBufferSize,
                                  final int _initialLocalQueueSize) {
        super(_onIdle, _factory, _log, _initialBufferSize, _initialLocalQueueSize);
    }

    @Override
    protected Inbox createInbox(int _initialLocalQueueSize) {
        return new NonBlockingInbox(_initialLocalQueueSize);
    }
}
