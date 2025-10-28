package edu.kh.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class JDBCExample6 {

	public static void main(String[] args) {
		
		// 아이디, 비밀번호, 이름을 입력받아
		// 아이디, 비밀번호가 일치하는 사용자의
		// 이름을 수정.(UPDATE)
		
		// 성공 시 "수정 성공!" 출력 / 실패 시 "아이디 또는 비밀번호 불일치" 출력
		Connection conn = null;
		PreparedStatement pstmt = null;

		// SELECT가 아니기 때문에 ResultSet 필요 없음!

		Scanner sc = null;

		try {

			Class.forName("oracle.jdbc.driver.OracleDriver");

			String url = "jdbc:oracle:thin:@localhost:1521:xe";
			String userName = "kh_ads";
			String password = "kh1234";

			conn = DriverManager.getConnection(url, userName, password);

			// 3. SQL 작성
			sc = new Scanner(System.in);

			System.out.print("아이디 입력 : ");
			String id = sc.next();

			System.out.print("비밀번호 입력 : ");
			String pw = sc.next();

			System.out.print("수정할 이름 입력 : ");
			String name = sc.next();

			// 아이디, 비밀번호, 이름을 입력받아
			// TB_USER 테이블에 삽입(INSERT)하기
			String sql = """
					UPDATE TB_USER 
					SET USER_NAME = ? 
					WHERE USER_ID = ? 
					AND USER_PW = ?
					""";

			// 위치홀더(?)가 있을 때 : pstmt
			// 없을 때 : stmt, pstmt
			// SELECT / DML 상관없음, 위치홀더 유무만

			// 4. PreparedStatement 객체 생성
			// -> 객체 생성과 동시에 SQL 담겨짐
			// -> 미리 ? (위치홀더)에 값을 받을 준비를 해야되기 때문

			// pstmt = conn.createStatement(); // createStatement 메서드는 버스가 만들고
			// stmt.executeQuery(sql); // executeQuery 메서드로 sql을 담아줘야 한다.

			pstmt = conn.prepareStatement(sql);
			// prepareStatement 위치홀더 때문에 객체 생성과 동시에 값이 만들어짐
			
			// 5. ? 위치홀더에 알맞은 값 대입
			pstmt.setString(1, name);
			pstmt.setString(2, id);
			pstmt.setString(3, pw);
			
			// 6. SQL(INSERT) 수행 후 결과(int)반환 받기
			conn.setAutoCommit(false);
			
			int result = pstmt.executeUpdate();
			
			// 7. result 값에 따른 결과 처리 + 트랜잭션 제어처리( DML 시에는 트랜잭션 제어를 잘해주는 것이 중요!!!! )
			if(result > 0) {   // UPDATE 성공 시
				conn.commit(); // COMMIT 수행 -> DB에 UPDATE 데이터 영구 반영
				System.out.println("해당 " + name + "으로 수정되었습니다.");
				
			} else { // 실패
				conn.rollback();
				System.out.println("아이디 또는 비밀번호 불일치");
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			
			// 8. 사용한 JDBC 객체 자원 반환
			try {
				if (pstmt != null) pstmt.close();
				if (conn != null) conn.close();
				
				if (sc != null) sc.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}

}
