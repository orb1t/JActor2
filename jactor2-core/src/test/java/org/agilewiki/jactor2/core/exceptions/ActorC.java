package org.agilewiki.jactor2.core.exceptions;

import org.agilewiki.jactor2.core.messaging.AsyncRequest;
import org.agilewiki.jactor2.core.messaging.ExceptionHandler;
import org.agilewiki.jactor2.core.processing.IsolationMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.threading.ModuleContext;

public class ActorC {
    private final MessageProcessor messageProcessor;

    public ActorC(final ModuleContext _context) {
        this.messageProcessor = new IsolationMessageProcessor(_context);
    }

    public AsyncRequest<String> throwAReq() {
        return new AsyncRequest<String>(messageProcessor) {
            @Override
            public void processAsyncRequest()
                    throws Exception {
                setExceptionHandler(new ExceptionHandler<String>() {
                    @Override
                    public String processException(final Exception exception)
                            throws Exception {
                        return exception.toString();
                    }
                });
                throw new SecurityException("thrown on request");
            }
        };
    }
}