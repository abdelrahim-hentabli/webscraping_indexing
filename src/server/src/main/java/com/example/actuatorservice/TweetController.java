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
  public Response[] sayLucene() {
    // JSONObject jsonObject = new JSONObject();
    //   //Inserting key-value pairs into the json object
    // jsonObject.put("id", "1");
    // jsonObject.put("handle", "@Krishna Kasyap");
    // jsonObject.put("tweet", "Bhagavatula");
    Response [] responses = new Response[2];
    responses[0] = new Response("1", "@Sam", "This is a tweet");
    responses[1] = new Response("1", "@something" , "A tweet from Lucene");
    return responses;
  }

  @GetMapping("/hadoop")
  @ResponseBody
  public Response sayHadoop() {
    return new Response("1", "@something" , "A tweet from Hadoop");
  }

  // @GetMapping("/hadoop")
  // @ResponseBody
  // public Response sayHadoop(@RequestParam(name="name", required=false, defaultValue="Hadoop") String name) {
  //   return new Response(counter.incrementAndGet(), "@something" ,String.format(template, name));
  // }

}