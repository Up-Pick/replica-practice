package org.example.replicapractice.domain.User.Service;


import lombok.RequiredArgsConstructor;

import org.example.replicapractice.config.DataSourceContextHolder;
import org.example.replicapractice.config.DataSourceType;
import org.example.replicapractice.domain.User.entity.User;
import org.example.replicapractice.domain.User.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	/**
	 * 쓰기 작업 - MASTER DB 사용 (자동 라우팅)
	 * @Transactional만 붙이면 자동으로 MASTER 사용
	 */
	@Transactional
	public User createUser(String name, String email) {
		User user = new User(name, email);
		return userRepository.save(user);
	}

	/**
	 * 쓰기 작업 - MASTER DB 사용 (자동 라우팅)
	 */
	@Transactional
	public User updateUser(Long id, String name, String email) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("User not found"));
		user.setName(name);
		user.setEmail(email);
		return userRepository.save(user);
	}

	/**
	 * 쓰기 작업 - MASTER DB 사용 (자동 라우팅)
	 */
	@Transactional
	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}

	/**
	 * 읽기 작업 - SLAVE DB 사용 (자동 라우팅)
	 * @Transactional(readOnly = true)만 붙이면 자동으로 SLAVE 사용
	 */
	@Transactional(readOnly = true)
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	/**
	 * 읽기 작업 - SLAVE DB 사용 (자동 라우팅)
	 */
	@Transactional(readOnly = true)
	public User getUserById(Long id) {
		return userRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("User not found"));
	}

	/**
	 * 읽기 작업 - SLAVE DB 사용 (자동 라우팅)
	 */
	@Transactional(readOnly = true)
	public User getUserByEmail(String email) {
		return userRepository.findAll().stream()
			.filter(user -> user.getEmail().equals(email))
			.findFirst()
			.orElseThrow(() -> new RuntimeException("User not found"));
	}

	/**
	 * 읽기 작업 - SLAVE DB 사용 (자동 라우팅)
	 */
	@Transactional(readOnly = true)
	public long getUserCount() {
		return userRepository.count();
	}

	/**
	 * 특수 케이스 - MASTER DB에서 읽기 (명시적 지정)
	 *
	 * 사용 시나리오:
	 * 1. 방금 생성/수정한 데이터를 즉시 조회할 때 (replication lag 회피)
	 * 2. 중요한 금액/재고 등 최신 데이터가 필수인 경우
	 * 3. 쓰기 후 즉시 읽기 패턴이 필요한 경우
	 */
	@Transactional(readOnly = true)
	public User getUserByIdFromMaster(Long id) {
		try {
			// 명시적으로 MASTER 지정 (readOnly지만 MASTER에서 조회)
			DataSourceContextHolder.setDataSourceType(DataSourceType.MASTER);
			return userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("User not found"));
		} finally {
			DataSourceContextHolder.clearDataSourceType();
		}
	}

	/**
	 * 실전 예시 - 사용자 생성 후 즉시 반환 (명시적 지정)
	 * Slave에서 조회하면 replication lag로 인해 데이터가 아직 안 보일 수 있음
	 */
	@Transactional
	public User createUserAndReturn(String name, String email) {
		User user = new User(name, email);
		user = userRepository.save(user);

		try {
			// 방금 저장한 데이터를 MASTER에서 즉시 조회
			DataSourceContextHolder.setDataSourceType(DataSourceType.MASTER);
			return userRepository.findById(user.getId())
				.orElseThrow(() -> new RuntimeException("User not found"));
		} finally {
			DataSourceContextHolder.clearDataSourceType();
		}
	}
}