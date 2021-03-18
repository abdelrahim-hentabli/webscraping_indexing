package com.example.actuatorservice;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TweetController {

  private static final String template = "Hello, %s!";
  private final AtomicLong counter = new AtomicLong();

  @GetMapping("/lucene")
  @ResponseBody
  public Response sayLucene(@RequestParam(name="name", required=false, defaultValue="Lucene") String name) {
    return new Response(counter.incrementAndGet(), String.format(template, name));
  }

  @GetMapping("/hadoop")
  @ResponseBody
  public Response sayHadoop(@RequestParam(name="name", required=false, defaultValue="Hadoop") String name) {
    return new Response(counter.incrementAndGet(), String.format(template, name));
  }

}