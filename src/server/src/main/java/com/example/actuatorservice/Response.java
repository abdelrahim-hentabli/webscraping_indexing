package com.example.actuatorservice;

public class Response {

  private final long id;
  private final String handle;
  private final String tweet;

  public Response(long id, String handle, String tweet) {
    this.id = id;
    this.handle = handle;
    this.tweet = tweet;
  }

  public long getId() {
    return id;
  }

  public String getContent() {
    return handle;
  }

  public String getTweet() {
    return tweet;
  }

}