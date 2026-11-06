package com.zjgsu.jzy.client;

import com.zjgsu.jzy.dto.DishResponse;
import com.zjgsu.jzy.exception.NotFoundException;
import com.zjgsu.jzy.exception.ServiceUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * menu-service 的 HTTP 客户端：
 * 菜品不存在 → 404；服务不可用/超时 → 503；调用失败时抛出明确错误，不放行后续写入
 */
@Component
public class DishServiceClient {

    private final RestTemplate restTemplate;
    private final ServiceAddressResolver resolver;

    public DishServiceClient(RestTemplate restTemplate,
                             ServiceAddressResolver resolver) {
        this.restTemplate = restTemplate;
        this.resolver = resolver;
    }

    public DishResponse getDish(String dishId) {
        String baseUrl = resolver.resolve("menu-service").toString();
        String url = baseUrl + "/api/dishes/" + dishId;
        try {
            ResponseEntity<DishResponse> resp =
                    restTemplate.getForEntity(url, DishResponse.class);
            if (resp.getBody() == null) throw new ServiceUnavailableException("依赖服务返回空响应");
            return resp.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NotFoundException("菜品不存在: " + dishId);
            }
            throw new ServiceUnavailableException("菜单服务调用失败，状态码 " + e.getStatusCode().value() + ": " + url);
        } catch (org.springframework.web.client.HttpServerErrorException e) {
            throw new ServiceUnavailableException("依赖服务返回服务器错误");
        } catch (ResourceAccessException e) {
            throw new ServiceUnavailableException("无法连接菜单服务: " + baseUrl);
        }
    }
}
