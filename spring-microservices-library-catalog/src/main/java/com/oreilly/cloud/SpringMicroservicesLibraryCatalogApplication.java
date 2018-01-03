package com.oreilly.cloud;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
@RestController
@EnableEurekaClient
@EnableHystrix
@EnableHystrixDashboard
public class SpringMicroservicesLibraryCatalogApplication {

  @Value("${catalog.size}")
  private int size;

  private static org.slf4j.Logger log =
      LoggerFactory.getLogger(SpringMicroservicesLibraryCatalogApplication.class);

  @RequestMapping("/catalog")
  @CrossOrigin
  @HystrixCommand
  public List<Book> getCatalog() {
    log.info("getCatalog method has been called.");
    return Book.getBooks().subList(0, size);
  }

  public String failover() {
    return "Message is from failover method.";
  }

  /*
    We need to tell our application how often we want to sample our logs to be exported to Zipkin.
    Since this is a demo, lets tell our app that we want to sample everything.
    We can do this by creating a bean for the AlwaysSampler.

  Source: https://dzone.com/articles/tracing-in-microservices-with-spring-cloud-sleuth
  */

  @Bean
  public AlwaysSampler defaultSampler() {

    return new AlwaysSampler();
  }

  public static void main(String[] args) {

    SpringApplication.run(SpringMicroservicesLibraryCatalogApplication.class, args);
  }
}
