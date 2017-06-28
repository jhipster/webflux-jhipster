package jhipster.reactive.web.rest.errors;

import java.net.URISyntaxException;

public class MyException extends RuntimeException{
    public URISyntaxException uriSyntaxException;

    public MyException(URISyntaxException uriSyntaxException){

        this.uriSyntaxException = uriSyntaxException;
    }
}
