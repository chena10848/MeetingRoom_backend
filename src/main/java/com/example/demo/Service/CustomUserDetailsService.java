package com.example.demo.Service;

import com.example.demo.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.example.demo.Repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用戶不存在"));

        // 打印调试信息
        System.out.println("Loaded user: " + userEntity);

        // 构建 Spring Security 的 UserDetails 对象
        return org.springframework.security.core.userdetails.User
                .withUsername(userEntity.getUserName())
                .password(userEntity.getPassword())
                .build();
    }
}