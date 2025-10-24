package org.example.replicapractice.domain.User.Service;


import lombok.RequiredArgsConstructor;

import org.example.replicapractice.domain.User.entity.User;
import org.example.replicapractice.domain.User.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	// 쓰기 작업 - Master DB 사용
	@Transactional
	public User createUser(String name, String email) {
		User user = new User(name, email);
		return userRepository.save(user);
	}

	// 읽기 작업 - Slave DB 사용
	@Transactional(readOnly = true)
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	// 읽기 작업 - Slave DB 사용
	@Transactional(readOnly = true)
	public User getUserById(Long id) {
		return userRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("User not found"));
	}
}