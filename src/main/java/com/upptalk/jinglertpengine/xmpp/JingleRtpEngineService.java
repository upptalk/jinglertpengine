package com.upptalk.jinglertpengine.xmpp;

import com.upptalk.jinglertpengine.metrics.MetricsHolder;
import com.upptalk.jinglertpengine.xmpp.component.ExternalComponent;
import com.upptalk.jinglertpengine.xmpp.component.MessageProcessor;
import com.upptalk.jinglertpengine.xmpp.component.NamespaceProcessor;
import org.apache.log4j.Logger;
import org.jivesoftware.whack.ExternalComponentManager;
import org.xmpp.component.ComponentException;

import java.util.ArrayList;

/**
 *  Jingle RTPEngine Service
 *
 * @author bhlangonijr
 * Date: 4/30/13
 * Time: 4:51 PM
 */
public class JingleRtpEngineService {

    private static final Logger log = Logger.getLogger(JingleRtpEngineService.class);

    private ExternalComponent externalComponent;
    private ExternalComponentManager manager;
    private String password;
    private String subDomain;
    protected ArrayList<NamespaceProcessor> processorList;
    protected MessageProcessor messageProcessor;

    public ExternalComponent getExternalComponent() {
        return externalComponent;
    }

    public ExternalComponentManager getManager() {
        return manager;
    }

    public String getPassword() {
        return password;
    }

    public String getSubDomain() {
        return subDomain;
    }

    public void setExternalComponent(ExternalComponent externalComponent) {
        this.externalComponent = externalComponent;
    }

    public void setManager(ExternalComponentManager manager) {
        this.manager = manager;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSubDomain(String subDomain) {
        this.subDomain = subDomain;
    }

    public void setProcessorList(ArrayList<NamespaceProcessor> processorList) {
        this.processorList = processorList;
    }

    public void setMessageProcessor(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    public void initialize() {
        log.info("Initializing JingleRTPEngine Component...");
        MetricsHolder.start();
    }

    public void init() {
        log.info("Init AbstractApplication");
        int t = 2;
        while (true) {
            try {
                manager.setSecretKey(subDomain, password);
                manager.setMultipleAllowed(subDomain, false);
                manager.addComponent(subDomain, externalComponent);

                break;
            } catch (Exception e) {
                log.error("Connection Error... ", e);
            }
            log.info("Retrying Connection in " + (t * 1000) + "s");
            try {
                Thread.sleep(t * 1000);
            } catch (InterruptedException e) {
                // Do Nothing
            }
            t = t * 2;
            if (t > 120) {
                t = 2;
            }
        }

        initialize();

        if (null != processorList) {
            externalComponent.addProcessorList(processorList);
        }

        if (null != messageProcessor) {
            externalComponent.addMessageProcessor(messageProcessor);
        }
    }

    public void destroy() {
        try {
            manager.removeComponent(subDomain);
        } catch (ComponentException e) {
            log.error("Could Not Remove Component.", e);
        }
        if (externalComponent != null) {
            externalComponent.shutdown();
        }
    }

}
