package edu.kh.jdbc.model.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import edu.kh.jdbc.common.JDBCTemplate;
import edu.kh.jdbc.model.dao.UserDAO;
import edu.kh.jdbc.model.dto.User;

// (Model 중 하나) Service : 비즈니 로직을 처리하는 계층,
// 데이터를 가공하고 트랜잭션(commit, rollback 관리)

// 기능을 제공하는 클래스
public class UserService {

	// 필드
	private UserDAO dao = new UserDAO();

	/**
	 * 1. User 등록 서비스 매개 변수를 parameter라고 함
	 * 
	 * @param user : 입력받은 id, pw, name 이 세팅된 객체
	 * @return insert 된 결과 행의 갯수
	 */
	public int insertUser(User user) throws Exception {

		// 1. 커넥션 생성
		Connection conn = JDBCTemplate.getConnection();

		// 2. 데이터 가공(할 것 없으면 생략)

		// 3. DAO 메서드 호출 후 결과 반환받기
		int result = dao.insertUser(conn, user);

		// 4. DML(INSERT) 수행 결과에 따라 트랜잭션 제어 처리
		if (result > 0) { // INSERT 성공
			JDBCTemplate.commit(conn);

		} else { // INSERT 실패
			JDBCTemplate.rollback(conn);

		}

		// 5. Connection 반환하기
		JDBCTemplate.close(conn);

		// 6. 결과 반환
		return result;
	}

	/**
	 * 2. User 전체 조회 서비스
	 * 
	 * @return 조회된 User들이 담긴 List
	 */
	public List<User> selectAll() throws Exception {

		// 1. 커넥션 생성
		Connection conn = JDBCTemplate.getConnection();

		// 2. 데이터 가공

		// 3. DAO 메서드 호출 후 결과 반환 받기
		// Service 단에서 conn은 무조건 전달해 줘야 함
		List<User> userList = dao.selectAll(conn);

		// 4. SELECT라서 트랜잭션 제어 X

		// 5. Connection 반환
		JDBCTemplate.close(conn);

		// 6. 결과 반환
		return userList;

	}

	public List<User> selectName(String keyword) throws Exception {

		Connection conn = JDBCTemplate.getConnection();

		List<User> searchList = dao.selectAll(conn, keyword);

		JDBCTemplate.close(conn);

		return searchList;
	}

	/**
	 * User 번호 조회 서비스
	 * 
	 * @param uNum
	 * @return
	 * @throws Exception
	 */
	public User selectUser(int uNum) throws Exception {

		// 커넥션 생성
		Connection conn = JDBCTemplate.getConnection();

		// dao 클래스로 매개 변수인 conn, uNum을 전달
		User user = dao.selectUser(conn, uNum);

		// Connection 반환
		JDBCTemplate.close(conn);

		// 결과 반환
		return user;
	}

	/**
	 * User 삭제 서비스
	 * 
	 * @param dNum
	 * @return
	 * @throws Exception
	 */
	public User deleteUser(int dNum) throws Exception {

		Connection conn = JDBCTemplate.getConnection();

		User user = dao.deleteUser(dNum, conn);

		// DELETE 수행 결과에 따라 트랜잭션 제어 처리
		if (dNum > 0) { // DELETE 성공
			JDBCTemplate.commit(conn);

		} else { // DELETE 실패
			JDBCTemplate.rollback(conn);

		}

		JDBCTemplate.close(conn);

		return user;
	}

}
