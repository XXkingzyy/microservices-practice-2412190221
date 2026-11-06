package com.zjgsu.jzy;

import com.zjgsu.jzy.client.ServiceAddressResolver;
import com.zjgsu.jzy.exception.ServiceUnavailableException;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceAddressResolverTest {
    @Test void emptyInstancesFailClearly() {
        DiscoveryClient discovery = mock(DiscoveryClient.class);
        when(discovery.getInstances("menu-service")).thenReturn(List.of());
        assertThatThrownBy(() -> new ServiceAddressResolver(discovery).resolve("menu-service"))
            .isInstanceOf(ServiceUnavailableException.class);
    }
    @Test void resolvesAgainForEveryOperation() {
        DiscoveryClient discovery = mock(DiscoveryClient.class);
        when(discovery.getInstances("menu-service"))
            .thenReturn(List.of(new DefaultServiceInstance("a", "menu-service", "10.0.0.1", 8082, false)))
            .thenReturn(List.of(new DefaultServiceInstance("b", "menu-service", "10.0.0.2", 8082, false)));
        var resolver = new ServiceAddressResolver(discovery);
        assertThat(resolver.resolve("menu-service").toString()).isEqualTo("http://10.0.0.1:8082");
        assertThat(resolver.resolve("menu-service").toString()).isEqualTo("http://10.0.0.2:8082");
        verify(discovery, times(2)).getInstances("menu-service");
    }
}
