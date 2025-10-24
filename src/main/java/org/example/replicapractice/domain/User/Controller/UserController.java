package org.example.replicapractice.domain.User.Controller;

import lombok.RequiredArgsConstructor;

import org.example.replicapractice.domain.User.Service.UserService;
import org.example.replicapractice.domain.User.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	/**
	 * POST /api/users - 사용자 생성 (MASTER DB 사용 - 자동 라우팅)
	 */
	@PostMapping
	public ResponseEntity<User> createUser(@RequestParam String name, @RequestParam String email) {
		User user = userService.createUser(name, email);
		return ResponseEntity.ok(user);
	}

	/**
	 * POST /api/users/immediate - 사용자 생성 후 즉시 반환 (MASTER에서 조회 - 명시적 지정)
	 * replication lag를 회피해야 하는 경우 사용
	 */
	@PostMapping("/immediate")
	public ResponseEntity<User> createUserAndReturn(@RequestParam String name, @RequestParam String email) {
		User user = userService.createUserAndReturn(name, email);
		return ResponseEntity.ok(user);
	}

	/**
	 * PUT /api/users/{id} - 사용자 정보 업데이트 (MASTER DB 사용)
	 */
	@PutMapping("/{id}")
	public ResponseEntity<User> updateUser(
		@PathVariable Long id,
		@RequestParam String name,
		@RequestParam String email) {
		User user = userService.updateUser(id, name, email);
		return ResponseEntity.ok(user);
	}

	/**
	 * DELETE /api/users/{id} - 사용자 삭제 (MASTER DB 사용)
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return ResponseEntity.ok().build();
	}

	/**
	 * GET /api/users - 모든 사용자 조회 (SLAVE DB 사용)
	 */
	@GetMapping
	public ResponseEntity<List<User>> getAllUsers() {
		List<User> users = userService.getAllUsers();
		return ResponseEntity.ok(users);
	}

	/**
	 * GET /api/users/{id} - ID로 사용자 조회 (SLAVE DB 사용)
	 */
	@GetMapping("/{id}")
	public ResponseEntity<User> getUserById(@PathVariable Long id) {
		User user = userService.getUserById(id);
		return ResponseEntity.ok(user);
	}

	/**
	 * GET /api/users/email/{email} - 이메일로 사용자 조회 (SLAVE DB 사용)
	 */
	@GetMapping("/email/{email}")
	public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
		User user = userService.getUserByEmail(email);
		return ResponseEntity.ok(user);
	}

	/**
	 * GET /api/users/count - 총 사용자 수 조회 (SLAVE DB 사용)
	 */
	@GetMapping("/count")
	public ResponseEntity<Long> getUserCount() {
		long count = userService.getUserCount();
		return ResponseEntity.ok(count);
	}

	/**
	 * GET /api/users/{id}/master - MASTER에서 사용자 조회 (최신 데이터 필요시)
	 */
	@GetMapping("/{id}/master")
	public ResponseEntity<User> getUserByIdFromMaster(@PathVariable Long id) {
		User user = userService.getUserByIdFromMaster(id);
		return ResponseEntity.ok(user);
	}
}
