package jhipster.reactive.web.rest.errors;

import java.net.URISyntaxException;

public class ReactiveException extends RuntimeException{
    public URISyntaxException uriSyntaxException;

    public ReactiveException(URISyntaxException uriSyntaxException){

        this.uriSyntaxException = uriSyntaxException;
    }
}
