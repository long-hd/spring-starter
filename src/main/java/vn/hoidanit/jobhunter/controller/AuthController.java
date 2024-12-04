package vn.hoidanit.jobhunter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.LoginDTO;
import vn.hoidanit.jobhunter.domain.dto.ResLoginDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
        private final AuthenticationManagerBuilder authenticationManagerBuilder;
        private final SecurityUtil sercurityUtil;
        private final UserService userService;

        @Value("${hoidanit.jwt.refresh-token-validity-in-seconds}")
        private long refreshTokenExpiration;

        public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil sercurityUtil,
                        UserService userService) {
                this.authenticationManagerBuilder = authenticationManagerBuilder;
                this.sercurityUtil = sercurityUtil;
                this.userService = userService;
        }

        @PostMapping("/auth/login")
        public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
                // Nạp input gồm username/password vào Security
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                loginDTO.getUsername(), loginDTO.getPassword());

                // xác thực người dùng => cần viết hàm loadUserByUsername
                Authentication authentication = authenticationManagerBuilder.getObject()
                                .authenticate(authenticationToken);
                // lưu thông tin người dùng đăng nhập vào context
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // create response {token, user}
                ResLoginDTO res = new ResLoginDTO();
                User userDb = userService.handleGetUserByUsername(loginDTO.getUsername());
                ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(userDb.getId(), userDb.getName(),
                                userDb.getEmail());
                res.setUser(userLogin);
                // create access token
                String accessToken = this.sercurityUtil.createAccessToken(authentication.getName(), res.getUser());
                res.setAccessToken(accessToken);
                // create refresh token
                String refreshToken = this.sercurityUtil.createRefreshToken(loginDTO.getUsername(), res);
                // update user refresh token
                this.userService.updateUserRefreshToken(loginDTO.getUsername(), refreshToken);
                // set cookies
                ResponseCookie responseCookie = ResponseCookie.from("refresh_token", refreshToken)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                // .domain("example.com")
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                                .body(res);
        }

        @GetMapping("/auth/account")
        @ApiMessage(Value = "fetch account")
        public ResponseEntity<ResLoginDTO.UserLogin> getAccount() {
                String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                                : "";
                User user = this.userService.handleGetUserByUsername(email);

                ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
                if (user != null) {
                        userLogin.setId(user.getId());
                        userLogin.setEmail(user.getEmail());
                        userLogin.setName(user.getName());
                }

                return ResponseEntity.ok().body(userLogin);
        }

        @GetMapping("/auth/refresh")
        @ApiMessage(Value = "get user by refresh token")
        public ResponseEntity<ResLoginDTO> getRefreshToken(
                        @CookieValue(name = "refresh_token", defaultValue = "default_refresh_token") String refresh_token)
                        throws IdInvalidException {
                if (refresh_token.equals("default_refresh_token")) {
                        throw new IdInvalidException("Chưa truyền Refresh Token");
                }
                // check token refresh valid
                Jwt decodedToken = this.sercurityUtil.checkValidRefreshToken(refresh_token);
                String email = decodedToken.getSubject();

                // check user by token + email
                User user = this.userService.getByRefreshTokenAndEmail(refresh_token, email);
                if (user == null) {
                        throw new IdInvalidException("Refresh Token không hợp lệ");
                }
                // issue new token / set refresh token as cookies
                // create response {token, user}
                ResLoginDTO res = new ResLoginDTO();
                User userDb = userService.handleGetUserByUsername(email);
                ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(userDb.getId(), userDb.getName(),
                                userDb.getEmail());
                res.setUser(userLogin);
                // create access token
                String accessToken = this.sercurityUtil.createAccessToken(email, res.getUser());
                res.setAccessToken(accessToken);
                // create refresh token
                String refreshToken = this.sercurityUtil.createRefreshToken(email, res);
                // update user refresh token
                this.userService.updateUserRefreshToken(email, refreshToken);
                // set cookies
                ResponseCookie responseCookie = ResponseCookie.from("refresh_token", refreshToken)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                // .domain("example.com")
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                                .body(res);
        }
}
