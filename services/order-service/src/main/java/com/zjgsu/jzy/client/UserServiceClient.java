package com.zjgsu.jzy.client;

import com.zjgsu.jzy.dto.CustomerResponse;
import com.zjgsu.jzy.exception.NotFoundException;
import com.zjgsu.jzy.exception.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * user-service 的 HTTP 客户端：
 * 顾客不存在 → 404；服务不可用/超时 → 503；调用失败时抛出明确错误，不放行后续写入
 */
@Component
public class UserServiceClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public UserServiceClient(RestTemplate restTemplate,
                             @Value("${service.user-service.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public CustomerResponse getCustomer(String customerId) {
        String url = baseUrl + "/api/customers/" + customerId;
        try {
            ResponseEntity<CustomerResponse> resp =
                    restTemplate.getForEntity(url, CustomerResponse.class);
            return resp.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NotFoundException("顾客不存在: " + customerId);
            }
            throw new ServiceUnavailableException("用户服务调用失败，状态码 " + e.getStatusCode().value() + ": " + url);
        } catch (ResourceAccessException e) {
            throw new ServiceUnavailableException("无法连接用户服务: " + baseUrl);
        }
    }
}
