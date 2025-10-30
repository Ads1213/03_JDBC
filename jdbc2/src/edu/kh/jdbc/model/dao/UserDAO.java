package edu.kh.jdbc.model.dao;

// import static : 지정된 경로에 존재하는 static 구문을 모두 얻어와
// 클래스명.메서드명()이 아닌 메서드명()만 작성해도 호출 가능하게 함.
import static edu.kh.jdbc.common.JDBCTemplate.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import edu.kh.jdbc.model.dto.User;

// (Model 중 하나) DAO (Data Access Object : 데이터 접근 객체)
// 데이터가 저장된 곳(DB)에 접근하는 용도의 객체
// -> DB에 접근하여 Java에서 원하는 결과를 얻기 위해
// SQL을 수행하고 결과를 반환받는 역할
/**
 * 
 */

// 실제로 SQL을 실행하고 DB와 통신하는 역할. JDBC 코드가 들어감.
public class UserDAO {

	// 필드
	// - DB 접근 관련한 JDBC 객체 참조 변수 선언
	private Statement stmt = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;

	/**
	 * 1. User 등록 DAO
	 * 
	 * @param conn : DB 연결 정보가 담겨있는 Connection 객체
	 * @param user : 입력받은 id, pw, name 이 세팅된 User 객체
	 * @return INSERT 결과 행의 갯수
	 */
	public int insertUser(Connection conn, User user) throws Exception {

		// 1. 결과 저장용 변수 선언
		// result set이라고 말할 뻔
		int result = 0;

		try {
			// 2. SQL 작성
			String sql = """
					INSERT INTO TB_USER
					VALUES(SEQ_USER_NO.NEXTVAL, ?, ?, ?, DEFAULT)
					""";

			// 3. PreparedStatement 객체 생성
			pstmt = conn.prepareStatement(sql);

			// 4. ? 위치홀더에 알맞은 값 대입
			pstmt.setString(1, user.getUserId());
			pstmt.setString(2, user.getUserPw());
			pstmt.setString(3, user.getUserName());

			// 5. SQL(INSERT) 수행(executeUpdate()) 후
			// 결과(삽입된 행의 갯수) 반환 받기
			result = pstmt.executeUpdate();

		} finally {

			// 6. 사용한 jdbc 객체 자원 반환
			close(pstmt);

		}

		// 결과 저장용 변수에 저장된 최종 값 반환
		return result;
	}

	/**
	 * 2. User 전체 조회 DAO
	 * 
	 * @param conn
	 * @return List<User> userList
	 */
	public List<User> selectAll(Connection conn) throws Exception {

		// 1. 결과 저장용 변수 선언
		List<User> userList = new ArrayList<User>();

		try {
			// 2. SQL 작성
			String sql = """
					SELECT USER_NO, USER_ID, USER_PW, USER_NAME,
					TO_CHAR(ENROLL_DATE, 'YYYY"년" MM"월" DD"일"') ENROLL_DATE
					FROM TB_USER
					ORDER BY USER_NO
					""";

			// 3. PreparedStatement 생성
			pstmt = conn.prepareStatement(sql);

			// 4. ? (위치홀더)에 알맞은 값 대입(없으면 패스)

			// 5. SQL(SELECT) 수행(executeQuery())후 결과 반환(ResultSet)받기
			rs = pstmt.executeQuery();

			// 6. 조회 결과(rs)를 1행씩 접근하여 컬럼 값 얻어오기
			// 몇 행이 조회될지 모른다 -> while
			// 한 행만 조회된다 -> if
			while (rs.next()) {

				int userNo = rs.getInt("USER_NO");
				String userId = rs.getString("USER_ID");
				String userPw = rs.getString("USER_PW");
				String userName = rs.getString("USER_NAME");
				String enrollDate = rs.getString("ENROLL_DATE");
				// -> java.sql.Date 타입으로 값을 저장하지 않은 이유
				// -> SELECT 문에서 TO_CHAR()를 이용하여 문자열 형태로
				// 변환해 조회해왔기 때문이다.

				// User 객체 새로 생성하여 DB에서 얻어온 컬럼값 필드로 세팅
				User user = new User(userNo, userId, userPw, userName, enrollDate);

				userList.add(user);

			}

		} finally {

			// 7. 사용자 자원 반환
			close(rs);
			close(pstmt);

		}

		// 조회 결과가 담긴 List 반환
		return userList;
	}

	/**
	 * 3. User 중 이름에 검색어가 포함된 회원 조회용 DAO
	 * 
	 * @param conn
	 * @param keyword
	 * @return searchList
	 */
	public List<User> selectAll(Connection conn, String keyword) throws Exception {

		List<User> searchList = new ArrayList<User>();

		try {
			String sql = """
					SELECT USER_NO, USER_ID, USER_PW, USER_NAME,
					TO_CHAR(ENROLL_DATE, 'YYYY"년" MM"월" DD"일"') ENROLL_DATE
					FROM TB_USER
					WHERE USER_NAME LIKE ?
					ORDER BY USER_NO
					""";

			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, "%" + keyword + "%");

			rs = pstmt.executeQuery();

			while (rs.next()) {

				int userNo = rs.getInt("USER_NO");
				String userId = rs.getString("USER_ID");
				String userPw = rs.getString("USER_PW");
				String userName = rs.getString("USER_NAME");
				String enrollDate = rs.getString("ENROLL_DATE");

				// User 객체 새로 생성하여 DB에서 얻어온 컬럼값 필드로 세팅
				User user = new User(userNo, userId, userPw, userName, enrollDate);

				searchList.add(user);

			}

		} finally {

			close(rs);
			close(pstmt);
		}

		return searchList;
	}

	/**
	 * 4. USER_NO를 입력 받아 일치하는 User 조회용 DAO
	 * 
	 * @param conn
	 * @param uNum
	 * @return user
	 * @throws Exception
	 */
	public User selectUser(Connection conn, int uNum) throws Exception {

		// 데이터를 담기 위한 저장용 객체 생성
		User user = null;

		// USER_NO를 조회하기 위한 SQL문 작성
		try {
			String sql = """
					SELECT USER_NO, USER_ID, USER_PW, USER_NAME,
					TO_CHAR(ENROLL_DATE, 'YYYY"년" MM"월" DD"일"') ENROLL_DATE
					FROM TB_USER
					WHERE USER_NO = ?
					ORDER BY USER_NO
					""";

			pstmt = conn.prepareStatement(sql);

			// 플레이스 홀더에 지정할 값 지정
			pstmt.setInt(1, uNum);

			// SELECT문 결과 조회를 위한 executeQuery 메서드 사용 및 rs에 담아주기
			rs = pstmt.executeQuery();

			if (rs.next()) {

				int userNo = rs.getInt("USER_NO");
				String userId = rs.getString("USER_ID");
				String userPw = rs.getString("USER_PW");
				String userName = rs.getString("USER_NAME");
				String enrollDate = rs.getString("ENROLL_DATE");

				// User 객체를 생성하여 데이터를 담은 후 user에 담아준다.
				user = new User(userNo, userId, userPw, userName, enrollDate);

			}

		} finally {

			// 7. 사용자 자원 반환
			close(rs);
			close(pstmt);
		}

		// 결과 리턴
		return user;
	}
	
	
	// 사용자가 USER_NO를 입력했을 때 일치하는 User를 삭제
	/** User 삭제 DAO
	 * @param dNum
	 * @param conn
	 * @return user
	 * @throws Exception
	 */
	public User deleteUser(int dNum, Connection conn) throws Exception {
		
		User user = new User();

		int delete = 0;

		try {
			
			String sql = """
					DELETE FROM TB_USER
					WHERE USER_NO = ?
					""";

			pstmt = conn.prepareStatement(sql);

			// 플레이스 홀더에 지정할 값 지정
			pstmt.setInt(1, dNum);

			delete = pstmt.executeUpdate();

		} finally {

			// 7. 사용자 자원 반환
			close(pstmt);
		}

		return user;
	}

}
