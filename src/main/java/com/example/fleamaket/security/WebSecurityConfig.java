package com.example.fleamaket.security;

//import org.springframework.context.annotation.Bean;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.crypto.factory.PasswordEncoderFactories;
//import org.springframework.security.crypto.password.PasswordEncoder;
// 
//@SuppressWarnings("deprecation")
//@EnableWebSecurity
//public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
// 
//    @Bean
//    PasswordEncoder passwordEncoder() {
//        // パスワードの暗号化クラスを戻り値とする
//        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
//    }
// 
//    // WebSecurity の設定
//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        // images, js, css フォルダ以下のファイルはアクセス制限の対象外とする
//        web
//            .debug(false)
//            .ignoring()
//            .antMatchers("/images/**", "/js/**", "/css/**", "/webjars/**");
//    }
// 
//    // HttpSecurity の設定
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//            .authorizeRequests()
//                .mvcMatchers("/users/login", "/users/sign_up").permitAll() // login画面と sign_up 画面については認証無しでアクセスできる
//                .anyRequest().authenticated()
//            .and()
//                .formLogin()
//                .loginPage("/users/login")
//                .loginProcessingUrl("/login") // th:actionに指定したURLを設定
//                .usernameParameter("email") // ログインフォームのユーザー欄のname属性を設定
//                .passwordParameter("password") // ログインフォームのパスワード欄のname属性を設定
//                .defaultSuccessUrl("/items/", true) // ログイン成功時に遷移するURL
//                .failureUrl("/users/login?error")
//                .permitAll()
//            .and()
//                .logout()
//                .invalidateHttpSession(true) // セッションの削除
//                .deleteCookies("JSESSIONID") // クッキーの削除
//                .logoutSuccessUrl("/users/login"); // ログアウト時はログイン画面へ
//    }
//}

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class WebSecurityConfig {
	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
		.authorizeHttpRequests(requests -> requests
			.mvcMatchers("/users/login", "/users/signup", "/images/**", "/css/**", "/webjars/**")
			.permitAll().anyRequest().authenticated()
		)
		.formLogin(login -> login
			.loginPage("/users/login")
            .loginProcessingUrl("/login")		// th:actionに指定したURLを設定
            .usernameParameter("email")			// ログインフォームのユーザー欄のname属性を設定
            .passwordParameter("password")		// ログインフォームのパスワード欄のname属性を設定
            .defaultSuccessUrl("/", true)		// ログイン成功時に遷移するURL
//            .failureUrl("/users/login?error")
            .permitAll()
		)
		.logout(logout -> logout
	        .invalidateHttpSession(true)		// セッションの削除
	        .deleteCookies("JSESSIONID")		// クッキーの削除
	        .logoutSuccessUrl("/users/login")	// ログアウト時はログイン画面へ
		);
		
		return http.build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
