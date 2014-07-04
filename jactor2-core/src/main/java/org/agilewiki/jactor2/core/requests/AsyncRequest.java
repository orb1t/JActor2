package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.plant.impl.PlantImpl;
import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorClosedException;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;
import org.agilewiki.jactor2.core.requests.impl.RequestImpl;
import org.agilewiki.jactor2.core.util.GwtIncompatible;
import org.agilewiki.jactor2.core.util.Timer;

/**
 * An async request separates data flow from control flow and its effect can span multiple reactors.
 *
 * @param <RESPONSE_TYPE> The type of response value.
 */
@Deprecated
public abstract class AsyncRequest<RESPONSE_TYPE> implements
        AsyncOperation<RESPONSE_TYPE>, Request<RESPONSE_TYPE>, AsyncResponseProcessor<RESPONSE_TYPE> {

    private final AsyncRequestImpl<RESPONSE_TYPE> asyncRequestImpl;

    /**
     * Create an AsyncRequest and bind it to its target targetReactor.
     *
     * @param _targetReactor The targetReactor where this AsyncRequest Objects is passed for processing.
     *                       The thread owned by this targetReactor will process this AsyncRequest.
     */
    public AsyncRequest(final Reactor _targetReactor) {
        asyncRequestImpl = PlantImpl.getSingleton().createAsyncRequestImpl(
                this, _targetReactor);
    }

    /**
     * Create an AsyncRequest and bind it to its target targetReactor.
     *
     * @param _targetBlade Provides the targetReactor where this AsyncRequest Objects is passed for processing.
     *                     The thread owned by this targetReactor will process this AsyncRequest.
     */
    public AsyncRequest(final Blade _targetBlade) {
        this(_targetBlade.getReactor());
    }

    @Override
    public AsyncRequestImpl<RESPONSE_TYPE> asRequestImpl() {
        return asyncRequestImpl;
    }

    @Override
    public Reactor getTargetReactor() {
        return asyncRequestImpl.getTargetReactor();
    }

    @Override
    public Reactor getSourceReactor() {
        return asyncRequestImpl.getSourceReactor();
    }

    @Override
    public void processAsyncResponse(final RESPONSE_TYPE _response) {
        asyncRequestImpl.processAsyncResponse(_response);
    }

    @Override
    public void signal() {
        asyncRequestImpl.signal();
    }

    @GwtIncompatible
    @Override
    public RESPONSE_TYPE call() throws Exception {
        return asyncRequestImpl.call();
    }

    @Override
    public boolean isCanceled() throws ReactorClosedException {
        return asyncRequestImpl.isCanceled();
    }

    @Override
    public void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                      final AsyncResponseProcessor<RESPONSE_TYPE> _asyncResponseProcessor)
            throws Exception {
        processAsyncRequest();
    }

    /**
     * The processAsyncRequest method will be invoked by the target Reactor on its own thread.
     */
    abstract public void processAsyncRequest() throws Exception;

    @Override
    public void onCancel(final AsyncRequestImpl _asyncRequestImpl) {
        onCancel();
    }

    /**
     * An optional callback used to signal that the request has been canceled.
     * This method must be thread-safe, as there is no constraint on which
     * thread is used to call it.
     * The default action of onCancel is to call cancelAll and,
     * if the reactor is not a common reactor, sends a response of null via
     * a bound response processor.
     */
    public void onCancel() {
        asRequestImpl().onCancel(asRequestImpl());
    }

    @Override
    public void onClose(final AsyncRequestImpl _asyncRequestImpl) {
        onClose();
    }

    /**
     * An optional callback used to signal that the request has been closed.
     * This method must be thread-safe, as there is no constraint on which
     * thread is used to call it.
     * By default, onClose does nothing.
     */
    public void onClose() {
        asRequestImpl().onClose(asRequestImpl());
    }

    /**
     * Disables the hung request check, which is necessary when a response to a request
     * is not passed back until another request is received.
     */
    public void setNoHungRequestCheck() {
        asyncRequestImpl.setNoHungRequestCheck();
    }

    /**
     * Returns the number of outstanding requests.
     *
     * @return The number of outstanding requests.
     */
    public int getPendingResponseCount() {
        return asyncRequestImpl.getPendingResponseCount();
    }

    /**
     * Passes back an exception as a response instead of throwing it.
     * But regardless of how a response is returned, if the response is an exception it
     * is passed to the exception handler of the async request that did the send or has the invoking call method
     * throw an exception.
     *
     * @param _response An exception.
     */
    public void processAsyncException(final Exception _response) {
        asyncRequestImpl.processAsyncException(_response);
    }

    /**
     * Pass a request to its target reactor, providing the originating request is not canceled.
     *
     * @param _request           The request to be passed.
     * @param _responseProcessor The callback to be invoked when a response value is received,
     *                           or null if no response is required.
     * @param <RT>               The response value type.
     */
    public <RT> void send(final Request<RT> _request,
                          final AsyncResponseProcessor<RT> _responseProcessor) {
        asyncRequestImpl.send(_request.asRequestImpl(), _responseProcessor);
    }

    /**
     * Pass a request to its target and then replace its response value,
     * providing the originating request is not canceled.
     * Useful when you do not care about the actual response being passed back.
     *
     * @param _request       The request to be passed.
     * @param _dis           The callback to be invoked when a response value is received.
     * @param _fixedResponse The replacement value.
     * @param <RT>           The response value type.
     * @param <RT2>          The replacement value type.
     */
    public <RT, RT2> void send(final Request<RT> _request,
                               final AsyncResponseProcessor<RT2> _dis, final RT2 _fixedResponse) {
        asyncRequestImpl.send(_request.asRequestImpl(), _dis, _fixedResponse);
    }

    /**
     * Replace the current ExceptionHandler with another.
     *
     * @param _exceptionHandler The exception handler to be used now.
     *                          May be null if the default exception handler is to be used.
     * @return The exception handler that was previously in effect, or null if the
     * default exception handler was in effect.
     */
    public ExceptionHandler<RESPONSE_TYPE> setExceptionHandler(
            final ExceptionHandler<RESPONSE_TYPE> _exceptionHandler) {
        return asyncRequestImpl.setExceptionHandler(_exceptionHandler);
    }

    /**
     * Cancel an outstanding request.
     * This method is thread safe, so it can be called from any thread.
     *
     * @param _request A subordinate request.
     * @return True if the request was canceled.
     */
    public boolean cancel(final Request<?> _request) {
        return asyncRequestImpl.cancel(_request.asRequestImpl());
    }

    /**
     * Cancels all outstanding requests.
     * This method is thread safe, so it can be called from any thread.
     */
    public void cancelAll() {
        asyncRequestImpl.cancelAll();
    }

    @Override
    public Timer getTimer() {
        return Timer.DEFAULT;
    }

    public <RT> RT syncDirect(final SOp<RT> _sOp)
            throws Exception {
        return asRequestImpl().syncDirect(_sOp);
    }

    /**
     * Do a direct method call on an AReq.
     *
     * @param _aOp                    The boilerplate-free alternative to AsyncRequest.
     * @param _asyncResponseProcessor Handles the response.
     * @param <RT>                    The type of response returned.
     */
    public <RT> void asyncDirect(final AOp<RT> _aOp,
                                 final AsyncResponseProcessor<RT> _asyncResponseProcessor)
            throws Exception {
        asRequestImpl().asyncDirect(_aOp, _asyncResponseProcessor);
    }

    /**
     * Pass a request to its target reactor, providing the originating request is not canceled.
     *
     * @param _sOp                    A synchronous operation, optionally used to define a SyncRequest.
     * @param _asyncResponseProcessor Handles the response.
     * @param <RT>                    The type of response returned.
     */
    public <RT> void send(final SOp<RT> _sOp,
                          final AsyncResponseProcessor<RT> _asyncResponseProcessor) {
        asyncRequestImpl.send(_sOp, _asyncResponseProcessor);
    }

    /**
     * Pass a request to its target reactor, providing the originating request is not canceled.
     *
     * @param _aOp                    An asynchronous operation, optionally used to define an AsyncRequest.
     * @param _asyncResponseProcessor Handles the response.
     * @param <RT>                    The type of response returned.
     */
    public <RT> void send(final AOp<RT> _aOp,
                          final AsyncResponseProcessor<RT> _asyncResponseProcessor) {
        asyncRequestImpl.send(_aOp, _asyncResponseProcessor);
    }

    /**
     * Pass a request to its target and then replace its response value,
     * providing the originating request is not canceled.
     * Useful when you do not care about the actual response being passed back.
     *
     * @param _sOp           A synchronous operation, optionally used to define a SyncRequest.
     * @param _dis           The callback to be invoked when a response value is received.
     * @param _fixedResponse The replacement value.
     * @param <RT>           The response value type.
     * @param <RT2>          The replacement value type.
     */
    public <RT, RT2> void send(final SOp<RT> _sOp,
                               final AsyncResponseProcessor<RT2> _dis, final RT2 _fixedResponse) {
        asyncRequestImpl.send(_sOp, _dis, _fixedResponse);
    }

    /**
     * Pass a request to its target and then replace its response value,
     * providing the originating request is not canceled.
     * Useful when you do not care about the actual response being passed back.
     *
     * @param _aOp           An asynchronous operation, optionally used to define an AsyncRequest.
     * @param _dis           The callback to be invoked when a response value is received.
     * @param _fixedResponse The replacement value.
     * @param <RT>           The response value type.
     * @param <RT2>          The replacement value type.
     */
    public <RT, RT2> void send(final AOp<RT> _aOp,
                               final AsyncResponseProcessor<RT2> _dis, final RT2 _fixedResponse) {
        asyncRequestImpl.send(_aOp, _dis, _fixedResponse);
    }
}
