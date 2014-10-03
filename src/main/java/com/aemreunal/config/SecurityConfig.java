package com.aemreunal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/*
 ***************************
 * Copyright (c) 2014      *
 *                         *
 * This code belongs to:   *
 *                         *
 * @author Ahmet Emre Ünal *
 * S001974                 *
 *                         *
 * aemreunal@gmail.com     *
 * emre.unal@ozu.edu.tr    *
 *                         *
 * aemreunal.com           *
 ***************************
 */

/*
 * Via: https://spring.io/guides/tutorials/rest/5/
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//    @Autowired
//    private BCryptPasswordEncoder encoder;

//    @Override
//    protected void registerAuthentication(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//            .withUser("letsnosh").password("noshing").roles("USER");
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeUrls()
//            .antMatchers("/aggregators/**").hasRole("USER")
//            .anyRequest().anonymous()
//            .and()
//            .httpBasic();
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//            .antMatchers("/**").hasRole("USER")
//            .anyRequest().anonymous()
//            .and()
//            .httpBasic().and().userDetailsService(new UserDetailsServiceImpl());
////            .and()
////            .formLogin()
//        ;
////        new UsernamePasswordAuthenticationToken("test", "test");
//    }
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        /*auth.inMemoryAuthentication()
//            .withUser("user")
//            .password("password")
//            .roles("USER")
//            .and()
//            .withUser("adminr")
//            .password("password")
//            .roles("ADMIN", "USER");*/
////        auth.jdbcAuthentication().
//        auth.userDetailsService(new UserDetailsServiceImpl()).passwordEncoder(encoder);
//    }
}
/*

@Service("userDetailsService")
class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService dao;
    @Autowired
    private Assembler   assembler;

    @Transactional
    public UserDetails loadUserByUsername(String username)
    throws UsernameNotFoundException, DataAccessException {

        com.aemreunal.domain.User user = dao.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("user not found");
        }

        return assembler.buildUserFromUserEntity(user);
    }
}

@Service("assembler")
class Assembler {

    @Transactional
    public UserDetails buildUserFromUserEntity(com.aemreunal.domain.User userEntity) {
        String username = userEntity.getUsername();
        String password = userEntity.getPassword();
        boolean enabled = true; // userEntity.isActive();
        boolean accountNonExpired = true; // userEntity.isActive();
        boolean credentialsNonExpired = true; // userEntity.isActive();
        boolean accountNonLocked = true; // userEntity.isActive();

        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
//        for (SecurityRoleEntity role : userEntity.getRoles()) {
            authorities.add(*/
/*new SimpleGrantedAuthority(role.getRoleName())*//*
new SimpleGrantedAuthority("USER"));
//        }

        org.springframework.security.core.userdetails.User user = new User(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        return user;
    }
}
*/
