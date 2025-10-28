package edu.kh.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class JDBCExample5 {

	public static void main(String[] args) {

		// 아이디, 비밀번호, 이름을 입력받아
		// TB_USER 테이블에 삽입(INSERT)하기

		/*
		 * java.sql.PreparedStatement - SQL 중간에 ? (위치홀더, placeholder)를 작성하여 ? 자리에 java
		 * 값을 대입할 준비가 되어있는 Statement
		 * 
		 * 장점1 : SQL 작성이 간편해짐 장점2 : ?에 값 대입 시 자료형에 맞는 형태의 리터럴으로 대입됨! ex) String 대입 ->
		 * '값' (자동으로 '' 추가) ex) int 대입 -> 값 장점3 : 성능, 속도에서 우위를 가지고 있음
		 * 
		 * *** PreparedStatement는 Statement의 자식이다 ***
		 */

		// 1. JDBC 객체 참조 변수 선언
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

			System.out.print("이름 입력 : ");
			String name = sc.next();

			// 아이디, 비밀번호, 이름을 입력받아
			// TB_USER 테이블에 삽입(INSERT)하기
			String sql = """
					INSERT INTO TB_USER
					VALUES(SEQ_USER_NO.NEXTVAL, ?, ?, ?, DEFAULT)
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
			// pstmt.set자료형(?순서, 대입할 값);
			pstmt.setString(1, id);
			pstmt.setString(2, pw);
			pstmt.setString(3, name);
			// -> 여기까지 수행되면 온전한 SQL이 완성된 상태!
			
			// 6. SQL(INSERT) 수행 후 결과(int)반환 받기
			// + DML 수행 전에 해줘야 할 것!
			// Connection의 AutoCommit 끄기
			// ㄴ 왜 끄는지? 개발자가 트랜잭션을 마음대로 제어하기 위해서
			conn.setAutoCommit(false);
			
			// executeUpdate() : DML 수행, 결과 행 갯수(int) 반환
			// ㄴ 보통 DML 실패 0, 성공 시 0 초과된 값이 반환된다.
			
			// pstmt에서 executeQuery(), executeUpdate()
			// ㄴ 매개변수 자리에 sql 들어오면 안됨!!!!!!!!!
			int result = pstmt.executeUpdate();
			
			// 7. result 값에 따른 결과 처리 + 트랜잭션 제어처리( DML 시에는 트랜잭션 제어를 잘해주는 것이 중요!!!! )
			if(result > 0) { // INSERT 성공 시
				conn.commit(); // COMMIT 수행 -> DB에 INSERT 데이터 영구 반영
				System.out.println(name + "님이 추가되었습니다.");
				
			} else { // 실패
				conn.rollback(); // INSERT 실패 시에도 트랜잭션에 담겨서 rollback 처리
				System.out.println("추가 실패");
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
