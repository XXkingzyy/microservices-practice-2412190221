package com.zjgsu.jzy.client;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.server.ResponseStatusException;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
class CourseClientTest {
 @Test void discoveryExceptionIs503() {
  var discovery=mock(DiscoveryClient.class);
  when(discovery.getInstances("student-service")).thenThrow(new IllegalStateException("discovery failed"));
  assertThatThrownBy(()->new CourseClient(discovery).get("student-service","/api/students/s1"))
   .isInstanceOfSatisfying(ResponseStatusException.class,e->assertThat(e.getStatusCode().value()).isEqualTo(503));
 }
}
