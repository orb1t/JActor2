package org.agilewiki.jactor2.utilImpl.durable.incDes;

import org.agilewiki.jactor2.core.messaging.AsyncRequest;
import org.agilewiki.jactor2.core.messaging.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.AncestorBase;
import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.Factory;
import org.agilewiki.jactor2.util.durable.JASerializable;
import org.agilewiki.jactor2.util.durable.incDes.IncDes;
import org.agilewiki.jactor2.utilImpl.durable.AppendableBytes;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor2.utilImpl.durable.ReadableBytes;

import java.util.Arrays;

/**
 * Base class for Incremental Deserialization Actors.
 */
public class IncDesImpl extends AncestorBase implements IncDes {
    /**
     * The actor's processing.
     */
    private MessageProcessor messageProcessor;

    /**
     * The factory, or null.
     */
    private FactoryImpl factory;

    /**
     * The JID actor which holds this actor.
     */
    private IncDesImpl containerJid;

    /**
     * Holds the serialized data.
     */
    protected byte[] serializedBytes;

    /**
     * The start of the serialized data.
     */
    protected int serializedOffset;

    @Override
    public IncDes getDurable() {
        return this;
    }

    @Override
    public AsyncRequest<byte[]> getSerializedBytesReq() {
        return new AsyncRequest<byte[]>(getMessageProcessor()) {
            @Override
            public void processAsyncRequest() throws Exception {
                processAsyncResponse(getSerializedBytes());
            }
        };
    }

    @Override
    public AsyncRequest<Integer> getSerializedLengthReq() {
        return new AsyncRequest<Integer>(getMessageProcessor()) {
            @Override
            public void processAsyncRequest() throws Exception {
                processAsyncResponse(getSerializedLength());
            }
        };
    }

    final public JASerializable createSubordinate(Factory factory)
            throws Exception {
        return createSubordinate(factory, getParent());
    }

    final public JASerializable createSubordinate(String actorType)
            throws Exception {
        return createSubordinate(actorType, getParent());
    }

    final public JASerializable createSubordinate(Factory factory, Ancestor parent)
            throws Exception {
        JASerializable jid = factory.newSerializable(getMessageProcessor(), parent);
        ((IncDesImpl) jid.getDurable()).setContainerJid(this);
        return jid;
    }

    final public JASerializable createSubordinate(String actorType, Ancestor parent)
            throws Exception {
        JASerializable jid = Durables.newSerializable(Durables.getFactoryLocator(messageProcessor), actorType, getMessageProcessor(), parent);
        ((IncDesImpl) jid.getDurable()).setContainerJid(this);
        return jid;
    }

    final public JASerializable createSubordinate(Factory factory, byte[] bytes)
            throws Exception {
        return createSubordinate(factory, getParent(), bytes);
    }

    final public JASerializable createSubordinate(String actorType, byte[] bytes)
            throws Exception {
        return createSubordinate(actorType, getParent(), bytes);
    }

    final public JASerializable createSubordinate(Factory factory, Ancestor parent, byte[] bytes)
            throws Exception {
        if (bytes == null)
            return createSubordinate(factory, parent);
        JASerializable jid = factory.newSerializable(getMessageProcessor(), parent);
        ((IncDesImpl) jid.getDurable()).load(new ReadableBytes(bytes, 0));
        ((IncDesImpl) jid.getDurable()).setContainerJid(this);
        return jid;
    }

    final public JASerializable createSubordinate(String actorType, Ancestor parent, byte[] bytes)
            throws Exception {
        if (bytes == null)
            return createSubordinate(actorType, parent);
        JASerializable jid = Durables.newSerializable(Durables.getFactoryLocator(messageProcessor), actorType, getMessageProcessor(), parent);
        ((IncDesImpl) jid.getDurable()).load(new ReadableBytes(bytes, 0));
        ((IncDesImpl) jid.getDurable()).setContainerJid(this);
        return jid;
    }

    final public JASerializable createSubordinate(Factory factory, ReadableBytes readableBytes)
            throws Exception {
        return createSubordinate(factory, getParent(), readableBytes);
    }

    final public JASerializable createSubordinate(String actorType, ReadableBytes readableBytes)
            throws Exception {
        return createSubordinate(actorType, getParent(), readableBytes);
    }

    final public JASerializable createSubordinate(Factory factory, Ancestor parent, ReadableBytes readableBytes)
            throws Exception {
        JASerializable jid = factory.newSerializable(getMessageProcessor(), parent);
        if (readableBytes != null)
            ((IncDesImpl) jid.getDurable()).load(readableBytes);
        ((IncDesImpl) jid.getDurable()).setContainerJid(this);
        return jid;
    }

    final public JASerializable createSubordinate(String actorType, Ancestor parent, ReadableBytes readableBytes)
            throws Exception {
        JASerializable jid = Durables.newSerializable(Durables.getFactoryLocator(messageProcessor), actorType, getMessageProcessor(), parent);
        if (readableBytes != null)
            ((IncDesImpl) jid.getDurable()).load(readableBytes);
        ((IncDesImpl) jid.getDurable()).setContainerJid(this);
        return jid;
    }

    /**
     * Returns a readable form of the serialized data.
     *
     * @return A ReadableBytes wrapper of the serialized data.
     */
    final protected ReadableBytes readable() {
        return new ReadableBytes(serializedBytes, serializedOffset);
    }

    /**
     * Notification that the persistent data has changed.
     *
     * @param lengthChange The change in the size of the serialized data.
     */
    protected void changed(int lengthChange) {
        serializedBytes = null;
        serializedOffset = -1;
        if (containerJid == null)
            return;
        containerJid.change(lengthChange);
    }

    /**
     * Process a change in the persistent data.
     *
     * @param lengthChange The change in the size of the serialized data.
     */
    public void change(int lengthChange) {
        changed(lengthChange);
    }

    /**
     * Assign the container.
     *
     * @param containerJid The container, or null.
     */
    public void setContainerJid(IncDesImpl containerJid) {
        this.containerJid = containerJid;
    }

    /**
     * Returns the number of bytes needed to serialize the persistent data.
     *
     * @return The minimum size of the byte array needed to serialize the persistent data.
     */
    @Override
    public int getSerializedLength()
            throws Exception {
        return 0;
    }

    /**
     * Returns true when the persistent data is already serialized.
     *
     * @return True when the persistent data is already serialized.
     */
    final protected boolean isSerialized() {
        return serializedBytes != null;
    }

    /**
     * Serialize the persistent data.
     *
     * @param appendableBytes The wrapped byte array into which the persistent data is to be serialized.
     */
    protected void serialize(AppendableBytes appendableBytes)
            throws Exception {
    }

    /**
     * Saves the persistent data in a byte array.
     *
     * @param appendableBytes Holds the byte array and offset.
     */
    public void save(final AppendableBytes appendableBytes)
            throws Exception {
        if (isSerialized()) {
            byte[] bs = appendableBytes.getBytes();
            int off = appendableBytes.getOffset();
            appendableBytes.writeBytes(serializedBytes, serializedOffset, getSerializedLength());
            serializedBytes = bs;
            serializedOffset = off;
        } else {
            serializedBytes = appendableBytes.getBytes();
            serializedOffset = appendableBytes.getOffset();
            serialize(appendableBytes);
        }
        if (serializedOffset + getSerializedLength() != appendableBytes.getOffset()) {
            System.err.println("\n" + getClass().getName());
            System.err.println("" + serializedOffset +
                    " + " + getSerializedLength() + " != " + appendableBytes.getOffset());
            throw new IllegalStateException();
        }
    }

    final public AsyncRequest<Void> saveReq(final AppendableBytes appendableBytes) {
        return new AsyncRequest<Void>(getMessageProcessor()) {
            @Override
            public void processAsyncRequest() throws Exception {
                save(appendableBytes);
                processAsyncResponse(null);
            }
        };
    }

    /**
     * Returns a byte array holding the serialized persistent data.
     *
     * @return The byte array holding the serialized persistent data.
     */
    public final byte[] getSerializedBytes()
            throws Exception {
        byte[] bs = new byte[getSerializedLength()];
        AppendableBytes appendableBytes = new AppendableBytes(bs, 0);
        save(appendableBytes);
        return bs;
    }

    @Override
    final public AsyncRequest<Integer> getSerializedBytesReq(final byte[] bytes, final int offset) {
        return new AsyncRequest<Integer>(getMessageProcessor()) {
            @Override
            public void processAsyncRequest() throws Exception {
                processAsyncResponse(save(bytes, offset));
            }
        };
    }

    @Override
    public int save(byte[] bytes, int offset)
            throws Exception {
        AppendableBytes appendableBytes = new AppendableBytes(bytes, offset);
        save(appendableBytes);
        return appendableBytes.getOffset();
    }

    /**
     * Load the serialized data into the JID.
     *
     * @param readableBytes Holds the serialized data.
     */
    public void load(ReadableBytes readableBytes)
            throws Exception {
        serializedBytes = readableBytes.getBytes();
        serializedOffset = readableBytes.getOffset();
    }

    @Override
    public int load(byte[] bytes, int offset, int length)
            throws Exception {
        byte[] bs = new byte[length];
        System.arraycopy(bytes, offset, bs, 0, length);
        ReadableBytes rb = new ReadableBytes(bytes, 0);
        load(rb);
        return offset + length;
    }

    @Override
    public void load(byte[] bytes)
            throws Exception {
        load(bytes, 0, bytes.length);
    }

    /**
     * Resolves a JID pathname, returning a JID actor or null.
     *
     * @param pathname A JID pathname.
     * @return A JID actor or null.
     */
    @Override
    public JASerializable resolvePathname(final String pathname)
            throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public AsyncRequest<JASerializable> resolvePathnameReq(final String pathname) {
        return new AsyncRequest<JASerializable>(getMessageProcessor()) {
            @Override
            public void processAsyncRequest() throws Exception {
                processAsyncResponse(resolvePathname(pathname));
            }
        };
    }

    /**
     * Returns a copy of the actor.
     *
     * @param m The processing.
     * @return a copy of the actor.
     */
    @Override
    public JASerializable copy(final MessageProcessor m)
            throws Exception {
        MessageProcessor mb = m;
        if (mb == null)
            mb = getMessageProcessor();
        JASerializable serializable = getFactory().newSerializable(mb, getParent());
        IncDesImpl jid = (IncDesImpl) serializable.getDurable();
        jid.load(new ReadableBytes(getSerializedBytes(), 0));
        return serializable;
    }

    public final AsyncRequest<JASerializable> copyReq(final MessageProcessor m) {
        return new AsyncRequest<JASerializable>(getMessageProcessor()) {
            @Override
            public void processAsyncRequest() throws Exception {
                processAsyncResponse(copy(m));
            }
        };
    }

    public final AsyncRequest<Boolean> isEqualReq(final JASerializable jidA) {
        return new AsyncRequest<Boolean>(getMessageProcessor()) {
            AsyncRequest<Boolean> dis = this;

            @Override
            public void processAsyncRequest() throws Exception {
                getSerializedLengthReq().send(getMessageProcessor(), new AsyncResponseProcessor<Integer>() {
                    @Override
                    public void processAsyncResponse(Integer response) throws Exception {
                        if (response.intValue() != getSerializedLength()) {
                            dis.processAsyncResponse(false);
                            return;
                        }
                        getSerializedBytesReq().send(getMessageProcessor(), new AsyncResponseProcessor<byte[]>() {
                            @Override
                            public void processAsyncResponse(byte[] response) throws Exception {
                                boolean eq = Arrays.equals(response, getSerializedBytes());
                                dis.processAsyncResponse(eq);
                            }
                        });
                    }
                });
            }
        };
    }

    /**
     * Returns the factory.
     *
     * @return The factory, or null.
     */
    @Override
    final public FactoryImpl getFactory() {
        return factory;
    }

    /**
     * Returns the jid type.
     *
     * @return The jid type, or null.
     */
    @Override
    final public String getFactoryName() {
        if (factory == null)
            return null;
        return factory.name;
    }

    /**
     * Initialize a LiteActor
     *
     * @param _messageProcessor A processing which may be shared with other actors.
     * @param _parent           The parent actor.
     * @param _factory          The factory.
     */
    public void initialize(final MessageProcessor _messageProcessor, final Ancestor _parent, final FactoryImpl _factory)
            throws Exception {
        super.initialize(_parent);
        messageProcessor = _messageProcessor;
        factory = _factory;
    }

    @Override
    public MessageProcessor getMessageProcessor() {
        return messageProcessor;
    }
}
