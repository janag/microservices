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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@SpringBootApplication
@RestController
@EnableEurekaClient
@EnableHystrix
@EnableHystrixDashboard
public class SpringMicroservicesLibraryReservationApplication {

  @Value("${server.port}")
  private String port;

  private Map<Integer, Reservation> reservations = new HashMap<Integer, Reservation>();
  private static org.slf4j.Logger log =
      LoggerFactory.getLogger(SpringMicroservicesLibraryReservationApplication.class);

  @CrossOrigin
  @RequestMapping("/reservation/user/{username}/book/{bookId}")
  @HystrixCommand
  public String reserve(
      @PathVariable("username") String username, @PathVariable("bookId") int bookId) {
    log.info("Reserving: " + username + " " + bookId);
    Reservation reservation = new Reservation();
    reservation.setBookId(bookId);
    reservation.setUsername(username);
    reservation.setDate(new Date());
    reservation.setReservationId(new Random().nextInt(100));
    reservations.put(reservation.getReservationId(), reservation);
    System.out.println(reservations.size());
    return "Title has been reserved using server on port: " + port + ".";
  }

  @CrossOrigin
  @RequestMapping("/reservation/user/{username}")
  @HystrixCommand
  public List<Reservation> reservationsByUser(@PathVariable("username") String username) {
    log.info("Reserving: " + username);
    List<Reservation> tmpReservations = new ArrayList<Reservation>();
    for (Reservation reservation : this.reservations.values()) {
      if (reservation.getUsername().equals(username)) {
        tmpReservations.add(reservation);
      }
    }
    return tmpReservations;
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
    SpringApplication.run(SpringMicroservicesLibraryReservationApplication.class, args);
  }
}
