package com.example.actuatorservice;

public class Response {

  private final String id;
  private final String handle;
  private final String tweet;

  public Response(String id, String handle, String tweet) {
    this.id = id;
    this.handle = handle;
    this.tweet = tweet;
  }

  public String getId() {
    return id;
  }

  public String getHandle() {
    return handle;
  }

  public String getTweet() {
    return tweet;
  }



}