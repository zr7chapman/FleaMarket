package com.example.fleamaket.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fleamaket.repository.UserRepository;

@Service
public class SimpleUserDetailsService implements UserDetailsService {
	@Autowired
    private UserRepository repository;
 
    // メールアドレスを指定して、ユーザー情報を取得
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        assert(email != null);
        return repository
            .findByEmail(email)
            .map(SimpleLoginUser::new)
            .orElseThrow();
    }
}