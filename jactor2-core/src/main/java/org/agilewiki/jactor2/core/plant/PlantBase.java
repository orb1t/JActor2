package org.agilewiki.jactor2.core.plant;

import org.agilewiki.jactor2.core.blades.transactions.ISMap;
import org.agilewiki.jactor2.core.reactors.Facility;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

import java.util.Map;

/**
 * Plant is a singleton and is the top-level object.
 * The plant has an internal reactor which is the root of a tree of all reactors,
 * though this tree is made using weak references.
 */
abstract public class PlantBase {

    /**
     * Closes all reactors and associated closeables, the plant scheduler and the
     * reactor thread pool.
     */
    public static void close() throws Exception {
        PlantImpl plantImpl = PlantImpl.getSingleton();
        if (plantImpl != null)
            plantImpl.close();
    }

    /**
     * Returns the internal reactor.
     *
     * @return The internal reactor.
     */
    public static Facility getInternalReactor() {
        return PlantImpl.getSingleton().getInternalReactor();
    }

    /**
     * Returns the plant scheduler.
     *
     * @return The plant scheduler.
     */
    public static PlantScheduler getPlantScheduler() {
        return PlantImpl.getSingleton().getPlantScheduler();
    }

    /**
     * Create an ISMap.
     *
     * @param <V>    The type of value.
     * @return A new ISMap.
     */
    public static <V> ISMap<V> createISMap() {
        return PlantImpl.getSingleton().createISMap();
    }

    /**
     * Create an ISMap
     *
     * @param key      Key.
     * @param value    Value
     * @param <V>      The type of value.
     * @return A new ISMap
     */
    public static <V> ISMap<V> createISMap(String key, V value) {
        return PlantImpl.getSingleton().createISMap(key, value);
    }

    /**
     * Create an ISMap
     *
     * @param m      Content.
     * @param <V>    The type of value.
     * @return A new ISMap.
     */
    public static <V> ISMap<V> createISMap(Map<String, V> m) {
        return PlantImpl.getSingleton().createISMap(m);
    }
}
