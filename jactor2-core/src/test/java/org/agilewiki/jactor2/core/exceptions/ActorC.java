package org.agilewiki.jactor2.core.exceptions;

import org.agilewiki.jactor2.core.messaging.ExceptionHandler;
import org.agilewiki.jactor2.core.messaging.AsyncRequest;
import org.agilewiki.jactor2.core.processing.IsolationMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.threading.ModuleContext;

public class ActorC {
    private final MessageProcessor messageProcessor;
    public final AsyncRequest<String> throwRequest;

    public ActorC(final ModuleContext _context) {
        this.messageProcessor = new IsolationMessageProcessor(_context);

        throwRequest = new AsyncRequest<String>(messageProcessor) {
            @Override
            public void processAsyncRequest()
                    throws Exception {
                messageProcessor.setExceptionHandler(new ExceptionHandler() {
                    @Override
                    public void processException(final Throwable throwable)
                            throws Exception {
                        processAsyncResponse(throwable.toString());
                    }
                });
                throw new SecurityException("thrown on request");
            }
        };
    }
}
