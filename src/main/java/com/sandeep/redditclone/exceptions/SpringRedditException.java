package com.sandeep.redditclone.exceptions;

/**
 * In order to disclose the technical information to the user we are using custom exception
 */
public class SpringRedditException extends RuntimeException {
    public SpringRedditException(String exception) {
        super(exception);
    }
}
