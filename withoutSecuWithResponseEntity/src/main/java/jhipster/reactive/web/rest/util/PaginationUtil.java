package jhipster.reactive.web.rest.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * Utility class for handling pagination.
 *
 * <p>
 * Pagination uses the same principles as the <a href="https://developer.github.com/v3/#pagination">Github API</a>,
 * and follow <a href="http://tools.ietf.org/html/rfc5988">RFC 5988 (Link header)</a>.
 */
public final class PaginationUtil {

    private PaginationUtil() {
    }

    public static HttpHeaders generatePaginationHttpHeaders(Page page, String baseUrl) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", Long.toString(page.getTotalElements()));
        String link = "";
        if ((page.getNumber() + 1) < page.getTotalPages()) {
            link = "<" + generateUri(baseUrl, page.getNumber() + 1, page.getSize()) + ">; rel=\"next\",";
        }
        // prev link
        if ((page.getNumber()) > 0) {
            link += "<" + generateUri(baseUrl, page.getNumber() - 1, page.getSize()) + ">; rel=\"prev\",";
        }
        // last and first link
        int lastPage = 0;
        if (page.getTotalPages() > 0) {
            lastPage = page.getTotalPages() - 1;
        }
        link += "<" + generateUri(baseUrl, lastPage, page.getSize()) + ">; rel=\"last\",";
        link += "<" + generateUri(baseUrl, 0, page.getSize()) + ">; rel=\"first\"";
        headers.add(HttpHeaders.LINK, link);
        return headers;
    }

    public static <T> HttpHeaders generatePaginationHttpHeaders(Pageable pageable, List list, Long totalNumber, String baseUrl) {

        HttpHeaders headers = new HttpHeaders();
        int totalPages = Math.toIntExact(totalNumber/pageable.getPageSize()+1);

        headers.add("X-Total-Count", Long.toString(list.size()));
        String link = "";
        if ((pageable.getPageNumber() + 1) < totalPages) {
            link = "<" + PaginationUtil.generateUri(baseUrl, pageable.getPageNumber() + 1, list.size()) + ">; rel=\"next\",";
        }
        // prev link
        if ((pageable.getPageNumber()) > 0) {
            link += "<" + PaginationUtil.generateUri(baseUrl, pageable.getPageNumber() - 1, list.size()) + ">; rel=\"prev\",";
        }
        // last and first link
        int lastPage = 0;
        if (totalPages > 0) {
            lastPage = totalPages - 1;
        }
        link += "<" + PaginationUtil.generateUri(baseUrl, lastPage, list.size()) + ">; rel=\"last\",";
        link += "<" + PaginationUtil.generateUri(baseUrl, 0, list.size()) + ">; rel=\"first\"";
        headers.add(HttpHeaders.LINK, link);
        return headers;
    }

    static String generateUri(String baseUrl, int page, int size) {
        return UriComponentsBuilder.fromUriString(baseUrl).queryParam("page", page).queryParam("size", size).toUriString();
    }
}
