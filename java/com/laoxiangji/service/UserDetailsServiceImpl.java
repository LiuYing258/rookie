package com.laoxiangji.service;

import com.laoxiangji.entity.User;
import com.laoxiangji.repository.UserRepository;
import com.laoxiangji.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmployeeId(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found: " + username));

        return UserDetailsImpl.build(user);
    }
}