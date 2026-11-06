package com.zjgsu.jzy.client;

import com.zjgsu.jzy.exception.ServiceUnavailableException;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import java.net.URI;

@Component
public class ServiceAddressResolver {
    private final DiscoveryClient discovery;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ServiceAddressResolver.class);
    public ServiceAddressResolver(DiscoveryClient discovery) { this.discovery = discovery; }
    public URI resolve(String serviceName) {
        try {
            var instances = discovery.getInstances(serviceName);
            if (instances == null || instances.isEmpty())
                throw new ServiceUnavailableException("依赖服务无可用实例: " + serviceName);
            URI uri = instances.getFirst().getUri();
            log.info("Discovery {} -> {}", serviceName, uri);
            return uri;
        } catch (ServiceUnavailableException e) { throw e; }
        catch (RuntimeException e) { throw new ServiceUnavailableException("服务发现失败: " + serviceName); }
    }
}
