package com.zjgsu.jzy.client;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
@Component
public class CourseClient {
 private final DiscoveryClient discovery;
 private final RestTemplate http;
 public CourseClient(DiscoveryClient discovery) {
  this.discovery=discovery;
  var factory=new org.springframework.http.client.SimpleClientHttpRequestFactory();
  factory.setConnectTimeout(java.time.Duration.ofSeconds(2));
  factory.setReadTimeout(java.time.Duration.ofSeconds(3));
  http=new RestTemplate(factory);
 }
 public Object get(String service,String path) { return call(service,path,false); }
 public void post(String service,String path) { call(service,path,true); }
 private Object call(String service,String path,boolean post) {
  java.util.List<org.springframework.cloud.client.ServiceInstance> instances;
  try { instances=discovery.getInstances(service); }
  catch (RuntimeException e) { throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"服务发现失败: "+service); }
  if(instances.isEmpty()) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"无实例: "+service);
  String url=instances.getFirst().getUri()+path;
  org.slf4j.LoggerFactory.getLogger(getClass()).info("Discovery {} -> {}",service,url);
  try { return post?http.postForObject(url,null,Object.class):http.getForObject(url,Object.class); }
  catch(HttpClientErrorException e) { throw new ResponseStatusException(e.getStatusCode(),"依赖拒绝请求"); }
  catch(RestClientException e) { throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"依赖不可用"); }
 }
}
