package com.zjgsu.jzy;

import com.zjgsu.jzy.client.*;
import com.zjgsu.jzy.exception.*;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class DiscoveredHttpClientTest {
    private final DiscoveryClient discovery = mock(DiscoveryClient.class);
    private final RestTemplate http = new RestTemplate();
    private final MockRestServiceServer server = MockRestServiceServer.bindTo(http).build();
    private DishServiceClient client() {
        when(discovery.getInstances("menu-service")).thenReturn(List.of(
            new DefaultServiceInstance("dynamic", "menu-service", "discovered-host", 9082, false)));
        return new DishServiceClient(http, new ServiceAddressResolver(discovery));
    }
    @Test void usesDiscoveredUriAndExistingBusinessPath() {
        var client=client();
        server.expect(requestTo("http://discovered-host:9082/api/dishes/d1"))
            .andRespond(withSuccess("{\"id\":\"d1\",\"name\":\"dish\",\"category\":\"test\",\"price\":28,\"available\":true}", MediaType.APPLICATION_JSON));
        assertThat(client.getDish("d1").id()).isEqualTo("d1"); server.verify();
    }
    @Test void preserves404() {
        var client=client(); server.expect(anything()).andRespond(withStatus(HttpStatus.NOT_FOUND));
        assertThatThrownBy(()->client.getDish("missing")).isInstanceOf(NotFoundException.class); server.verify();
    }
    @Test void mapsRemote500ToUnavailable() {
        var client=client(); server.expect(anything()).andRespond(withServerError());
        assertThatThrownBy(()->client.getDish("d1")).isInstanceOf(ServiceUnavailableException.class); server.verify();
    }
    @Test void emptyDiscoveryStopsBeforeHttp() {
        when(discovery.getInstances("menu-service")).thenReturn(List.of());
        var client=new DishServiceClient(http,new ServiceAddressResolver(discovery));
        assertThatThrownBy(()->client.getDish("d1")).isInstanceOf(ServiceUnavailableException.class); server.verify();
    }
}
