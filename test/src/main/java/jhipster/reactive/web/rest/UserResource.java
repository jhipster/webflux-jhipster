package jhipster.reactive.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.ApiParam;
import jhipster.reactive.config.Constants;
import jhipster.reactive.domain.User;
import jhipster.reactive.repository.UserRepository;
import jhipster.reactive.security.AuthoritiesConstants;
import jhipster.reactive.service.MailService;
import jhipster.reactive.service.UserService;
import jhipster.reactive.service.dto.UserDTO;
import jhipster.reactive.web.rest.util.AsyncUtil;
import jhipster.reactive.web.rest.util.HeaderUtil;
import jhipster.reactive.web.rest.util.PaginationUtil;
import jhipster.reactive.web.rest.vm.ManagedUserVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing users.
 *
 * <p>This class accesses the User entity, and needs to fetch its collection of authorities.</p>
 * <p>
 * For a normal use-case, it would be better to have an eager relationship between User and Authority,
 * and send everything to the client side: there would be no View Model and DTO, a lot less code, and an outer-join
 * which would be good for performance.
 * </p>
 * <p>
 * We use a View Model and a DTO for 3 reasons:
 * <ul>
 * <li>We want to keep a lazy association between the user and the authorities, because people will
 * quite often do relationships with the user, and we don't want them to get the authorities all
 * the time for nothing (for performance reasons). This is the #1 goal: we should not impact our users'
 * application because of this use-case.</li>
 * <li> Not having an outer join causes n+1 requests to the database. This is not a real issue as
 * we have by default a second-level cache. This means on the first HTTP call we do the n+1 requests,
 * but then all authorities come from the cache, so in fact it's much better than doing an outer join
 * (which will get lots of data from the database, for each HTTP call).</li>
 * <li> As this manages users, for security reasons, we'd rather have a DTO layer.</li>
 * </ul>
 * <p>Another option would be to have a specific JPA entity graph to handle this case.</p>
 */
@RestController
@RequestMapping("/api")
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    private static final String ENTITY_NAME = "userManagement";

    private final UserRepository userRepository;

    private final MailService mailService;

    private final UserService userService;

    public UserResource(UserRepository userRepository, MailService mailService,
            UserService userService) {

        this.userRepository = userRepository;
        this.mailService = mailService;
        this.userService = userService;
    }

    /**
     * POST  /users  : Creates a new user.
     * <p>
     * Creates a new user if the login and email are not already used, and sends an
     * mail with an activation link.
     * The user needs to be activated on creation.
     * </p>
     *
     * @param managedUserVM the user to create
     * @return the ResponseEntity with status 201 (Created) and with body the new user, or with status 400 (Bad Request) if the login or email is already in use
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/users")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public Mono<ServerResponse> createUser(@Valid @RequestBody ManagedUserVM managedUserVM) throws URISyntaxException {
        log.debug("REST request to save User : {}", managedUserVM);
        if (managedUserVM.getId() != null) {
            return AsyncUtil.header(ServerResponse.badRequest(),HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new user cannot already have an ID"))
                .build();
            // Lowercase the user login before comparing with database
        } else return userRepository.findOneByLogin(managedUserVM.getLogin().toLowerCase())
            .flatMap(user -> AsyncUtil.header(ServerResponse.badRequest(),HeaderUtil.createFailureAlert(ENTITY_NAME, "userexists", "Login already in use")).build())
            .switchIfEmpty(
                    userRepository.findOneByEmail(managedUserVM.getEmail())
                        .flatMap(user -> AsyncUtil.header(ServerResponse.badRequest(),HeaderUtil.createFailureAlert(ENTITY_NAME, "emailexists", "Email already in use")).build())
                        .switchIfEmpty(userService.createUser(managedUserVM).flatMap(user -> {
                                    mailService.sendCreationEmail(user);
                                    return AsyncUtil.header(ServerResponse.created(URI.create("/api/users/" + user.getLogin())), HeaderUtil.createAlert("A user is created with identifier " + user.getLogin(), user.getLogin())).syncBody(user);
                                })
                        )
            );
    }

    /**
     * PUT  /users : Updates an existing User.
     *
     * @param managedUserVM the user to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated user,
     * or with status 400 (Bad Request) if the login or email is already in use,
     * or with status 500 (Internal Server Error) if the user couldn't be updated
     */
    @PutMapping("/users")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public Mono<ServerResponse> updateUser(@Valid @RequestBody ManagedUserVM managedUserVM) {
        log.debug("REST request to update User : {}", managedUserVM);
        Mono<User> existingUser = userRepository.findOneByEmail(managedUserVM.getEmail());
        return existingUser.flatMap(user -> {
            if (!user.getId().equals(managedUserVM.getId()))
                return AsyncUtil.header(ServerResponse.badRequest(),HeaderUtil.createFailureAlert(ENTITY_NAME, "emailexists", "Email already in use")).body(null);
            else return userRepository.findOneByLogin(managedUserVM.getLogin().toLowerCase())
                .flatMap(user1 -> {
                    if (!user1.getId().equals(managedUserVM.getId()))
                        return AsyncUtil.header(ServerResponse.badRequest(),HeaderUtil.createFailureAlert(ENTITY_NAME, "userexists", "Login already in use")).body(null);
                    else return AsyncUtil.wrapOrNotFound(
                        userService.updateUser(managedUserVM), HeaderUtil.createAlert("A user is updated with identifier " + managedUserVM.getLogin(), managedUserVM.getLogin()));
                })
                .switchIfEmpty(AsyncUtil.wrapOrNotFound(
                    userService.updateUser(managedUserVM), HeaderUtil.createAlert("A user is updated with identifier " + managedUserVM.getLogin(), managedUserVM.getLogin()))
                );
        });
    }

    /**
     * GET  /users : get all users.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and with body all users
     */
    @GetMapping("/users")
    @Timed
    public Mono<ServerResponse> getAllUsers(@ApiParam Pageable pageable) {
        final Mono<List<UserDTO>> page = userService.getAllManagedUsers(pageable).collectList();
        return page.flatMap(list -> ServerResponse.ok()
                                .header("X-Total-Count", Long.toString(list.size()))
                                .header(HttpHeaders.LINK, PaginationUtil.link(pageable, list, new Long(list.size()), "/api/users"))
                                .syncBody(list));
    }

    /**
     * @return a string list of the all of the roles
     */
    @GetMapping("/users/authorities")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public List<String> getAuthorities() {
        return userService.getAuthorities();
    }

    /**
     * GET  /users/:login : get the "login" user.
     *
     * @param login the login of the user to find
     * @return the ResponseEntity with status 200 (OK) and with body the "login" user, or with status 404 (Not Found)
     */
    @GetMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
    @Timed
    public Mono<ServerResponse> getUser(@PathVariable String login) {
        log.debug("REST request to get User : {}", login);
        return AsyncUtil.wrapOrNotFound(
            userService.getUserWithAuthoritiesByLogin(login).map(UserDTO::new));
    }

    /**
     * DELETE /users/:login : delete the "login" User.
     *
     * @param login the login of the user to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public Mono<ServerResponse> deleteUser(@PathVariable String login) {
        log.debug("REST request to delete User: {}", login);
        userService.deleteUser(login);
        return ServerResponse.ok()
        .header("X-defaultMongoDbApp-alert", "A user is deleted with identifier " + login)
        .header("X-defaultMongoDbApp-params", login).build();
    }
}
