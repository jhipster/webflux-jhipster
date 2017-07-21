package jhipster.reactive.web.rest;

import com.codahale.metrics.annotation.Timed;
import jhipster.reactive.domain.User;
import jhipster.reactive.repository.UserRepository;
import jhipster.reactive.security.SecurityUtils;
import jhipster.reactive.service.MailService;
import jhipster.reactive.service.UserService;
import jhipster.reactive.service.dto.UserDTO;
import jhipster.reactive.web.rest.util.AsyncUtil;
import jhipster.reactive.web.rest.util.HeaderUtil;
import jhipster.reactive.web.rest.vm.KeyAndPasswordVM;
import jhipster.reactive.web.rest.vm.ManagedUserVM;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final UserRepository userRepository;

    private final UserService userService;

    private final MailService mailService;

    public AccountResource(UserRepository userRepository, UserService userService,
                           MailService mailService) {

        this.userRepository = userRepository;
        this.userService = userService;
        this.mailService = mailService;
    }

    /**
     * POST  /register : register the user.
     *
     * @param managedUserVM the managed user View Model
     * @return the ResponseEntity with status 201 (Created) if the user is registered or 400 (Bad Request) if the login or email is already in use
     */
    @PostMapping(path = "/register",
        produces={MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    @Timed
    public Mono<ServerResponse> registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {
        HttpHeaders textPlainHeaders = new HttpHeaders();
        textPlainHeaders.setContentType(MediaType.TEXT_PLAIN);
        if (!checkPasswordLength(managedUserVM.getPassword())) {
            return ServerResponse.status(HttpStatus.BAD_REQUEST).syncBody("Incorrect password");
        }
        return userRepository.findOneByLogin(managedUserVM.getLogin().toLowerCase())
            .flatMap(user -> AsyncUtil.header(ServerResponse.badRequest(), textPlainHeaders).syncBody("login already in use"))
            .switchIfEmpty(
                userRepository.findOneByEmail(managedUserVM.getEmail())
                .flatMap(user1 -> AsyncUtil.header(ServerResponse.badRequest(), textPlainHeaders).syncBody("email address already in use"))
                .switchIfEmpty( userService
                        .createUser(managedUserVM.getLogin(),
                            managedUserVM.getPassword(),
                            managedUserVM.getFirstName(), managedUserVM.getLastName(),
                            managedUserVM.getEmail().toLowerCase(), managedUserVM.getImageUrl(),
                            managedUserVM.getLangKey())
                        .flatMap((User registeredUser) -> {
                            mailService.sendActivationEmail(registeredUser);
                            return ServerResponse.created(URI.create("")).build();
                        })
                        .switchIfEmpty(ServerResponse.badRequest().build())
                )
            );
    }

    /**
     * GET  /activate : activate the registered user.
     *
     * @param key the activation key
     * @return the ResponseEntity with status 200 (OK) and the activated user in body, or status 500 (Internal Server Error) if the user couldn't be activated
     */
    @GetMapping("/activate")
    @Timed
    public Mono<ServerResponse> activateAccount(@RequestParam(value = "key") String key) {
        return userService.activateRegistration(key)
            .flatMap(user -> ServerResponse.ok().build())
            .switchIfEmpty(ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    /**
     * GET  /authenticate : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request
     * @return the login if the user is authenticated
     */
    @GetMapping("/authenticate")
    @Timed
    public Mono<String> isAuthenticated(ServerRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.principal().map(user -> user.getName());
    }

    /**
     * GET  /account : get the current user.
     *
     * @return the ResponseEntity with status 200 (OK) and the current user in body, or status 500 (Internal Server Error) if the user couldn't be returned
     */
    @GetMapping("/account")
    @Timed
    public Mono<ServerResponse> getAccount() {
        String login = SecurityUtils.getCurrentUserLogin();
        Mono<User> u = userService.getUserWithAuthoritiesByLogin(login);
        return AsyncUtil.wrapOrNotFound(u.map(UserDTO::new));
    }

    /**
     * POST  /account : update the current user information.
     *
     * @param userDTO the current user information
     * @return the ResponseEntity with status 200 (OK), or status 400 (Bad Request) or 500 (Internal Server Error) if the user couldn't be updated
     */
    @PostMapping("/account")
    @Timed
    public Mono<ServerResponse> saveAccount(@Valid @RequestBody UserDTO userDTO) {
        final String userLogin = SecurityUtils.getCurrentUserLogin();
        Mono<User> existingUser = userRepository.findOneByEmail(userDTO.getEmail());
        return existingUser.flatMap (user -> {
            if (!user.getLogin().equalsIgnoreCase(userLogin)) return AsyncUtil.header(ServerResponse.badRequest(), HeaderUtil.createFailureAlert("user-management", "emailexists", "Email already in use")).build();
            else  return userRepository
                .findOneByLogin(userLogin)
                .flatMap(u -> {
                    userService.updateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(),
                        userDTO.getLangKey(), userDTO.getImageUrl());
                    return ServerResponse.ok().build();
                })
                .switchIfEmpty(ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        });
    }

    /**
     * POST  /account/change_password : changes the current user's password
     *
     * @param password the new password
     * @return the ResponseEntity with status 200 (OK), or status 400 (Bad Request) if the new password is not strong enough
     */
    @PostMapping(path = "/account/change_password",
        produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    public Mono<ServerResponse> changePassword(@RequestBody String password) {
        if (!checkPasswordLength(password)) {
            return ServerResponse.status(HttpStatus.BAD_REQUEST).syncBody("Incorrect password");
        }
        userService.changePassword(password);
        return ServerResponse.status(HttpStatus.OK).build();
    }

    /**
     * POST   /account/reset_password/init : Send an email to reset the password of the user
     *
     * @param mail the mail of the user
     * @return the ResponseEntity with status 200 (OK) if the email was sent, or status 400 (Bad Request) if the email address is not registered
     */
    @PostMapping(path = "/account/reset_password/init",
        produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    public Mono<ServerResponse> requestPasswordReset(@RequestBody String mail) {
        return userService.requestPasswordReset(mail)
            .flatMap(user -> {
                mailService.sendPasswordResetMail(user);
                return ServerResponse.status(HttpStatus.OK).syncBody("email was sent");
            }).switchIfEmpty(ServerResponse.status(HttpStatus.BAD_REQUEST).syncBody("email address not registered"));
    }

    /**
     * POST   /account/reset_password/finish : Finish to reset the password of the user
     *
     * @param keyAndPassword the generated key and the new password
     * @return the ResponseEntity with status 200 (OK) if the password has been reset,
     * or status 400 (Bad Request) or 500 (Internal Server Error) if the password could not be reset
     */
    @PostMapping(path = "/account/reset_password/finish",
        produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    public Mono<ServerResponse> finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
            return ServerResponse.badRequest().syncBody("Incorrect password");
        }
        return userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey())
            .flatMap(user -> ServerResponse.ok().build())
            .switchIfEmpty(ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    private boolean checkPasswordLength(String password) {
        return !StringUtils.isEmpty(password) &&
            password.length() >= ManagedUserVM.PASSWORD_MIN_LENGTH &&
            password.length() <= ManagedUserVM.PASSWORD_MAX_LENGTH;
    }
}
