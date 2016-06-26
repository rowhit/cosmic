package com.cloud.network.element;

import com.cloud.exception.ResourceUnavailableException;
import com.cloud.network.Network;
import com.cloud.network.rules.PortForwardingRule;

import java.util.List;

public interface PortForwardingServiceProvider extends NetworkElement, IpDeployingRequester {
    /**
     * Apply rules
     *
     * @param network
     * @param rules
     * @return
     * @throws ResourceUnavailableException
     */
    boolean applyPFRules(Network network, List<PortForwardingRule> rules) throws ResourceUnavailableException;
}
