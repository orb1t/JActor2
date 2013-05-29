package org.agilewiki.jactor.util.osgi.durable;

import org.agilewiki.jactor.api.Properties;
import org.agilewiki.jactor.api.Transport;
import org.agilewiki.jactor.util.durable.FactoryLocator;
import org.agilewiki.jactor.util.osgi.MailboxFactoryActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * An activator that provides both a mailbox factory and a registered factory locator service.
 */
abstract public class FactoryLocatorActivator extends MailboxFactoryActivator {
    private final Logger log = LoggerFactory.getLogger(FactoryLocatorActivator.class);

    /**
     * The factory locator service.
     */
    protected OsgiFactoryLocator factoryLocator;

    @Override
    public void start(final BundleContext _bundleContext) throws Exception {
        initializeActivator(_bundleContext);
        mailboxFactoryStart();
        createFactoryLocator();
        beginReq().signal();
    }

    protected boolean configImports() {
        return true;
    }

    @Override
    protected void begin(final Transport<Void> _transport) throws Exception {
        if (!configImports())
            factoryLocator.register(bundleContext);
        managedServiceRegistration();
        _transport.processResponse(null);
    }

    protected void createFactoryLocator() throws Exception {
        factoryLocator = new OsgiFactoryLocator();
        Properties properties = getMailboxFactory().getProperties();
        properties.putProperty("factoryLocator", factoryLocator);
    }

    @Override
    public void updated(final Dictionary<String, ?> _config) throws ConfigurationException {
        Dictionary<String, ?> oldConfig = getConfig();
        super.updated(_config);
        if (oldConfig == null && _config != null)
            configInitialized();
        else
            factoryLocator.updated(_config);

    }

    protected void configInitialized() throws ConfigurationException {
        if (configImports()) {
            Dictionary<String, ?> config = getConfig();
            Enumeration<String> keys = config.keys();
            TreeMap<String, String> imports = new TreeMap<String, String>();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                if (!(key.startsWith("import_")))
                    continue;
                String value = (String) config.get(key);
                imports.put(key, value);
            }
            Iterator<Map.Entry<String, String>> it = imports.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                String key = entry.getKey();
                String value = entry.getValue();
                int i = value.indexOf('|');
                try {
                    FactoriesImporter factoriesImporter = new FactoriesImporter(getMailbox());
                    if (i > -1) {
                        String bundleName = value.substring(0, i);
                        String niceVersion = value.substring(i + 1);
                        factoriesImporter.startReq(bundleName, niceVersion).call();
                    } else {
                        factoriesImporter.startReq(value).call();
                    }
                } catch (Exception e) {
                    throw new ConfigurationException(key, "unable to process", e);
                }
            }
            factoryLocator.setEssentialService(getMailboxFactory());
            factoryLocator.register(bundleContext);
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        setClosing();
        getMailboxFactory().close();
    }
}
