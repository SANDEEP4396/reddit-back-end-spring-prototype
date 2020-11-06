package com.sandeep.redditclone.exceptions;

public class SubredditNotFoundException extends RuntimeException {
    public SubredditNotFoundException(String exception) {
        super(exception);
    }
}
