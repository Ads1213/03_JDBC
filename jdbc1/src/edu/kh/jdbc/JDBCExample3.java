package edu.kh.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class JDBCExample3 {

	public static void main(String[] args) {
		// 입력받은 최소 급여 이상
		// 입력받은 최소 급여 이하를 받는
		// 사원의 사번, 이름, 급여를
		// 급여 내림차순으로 조회
		// -> 이클립스 콘솔 출력

		// [실행화면]
		// 최소 급여 : 1000000
		// 최대 급여 : 3000000
		// 사번 / 이름 / 급여
		// 사번 / 이름 / 급여
		// 사번 / 이름 / 급여
		// 사번 / 이름 / 급여
		// ...

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		Scanner sc = null;

		try {

			// 2. DriverManager 객체 이용해서 Connection 객체 생성
			// 2-1) Oracle JDBC Driver 객체 메모리 로드
			Class.forName("oracle.jdbc.driver.OracleDriver");

			// 2-2 ) DB 연결 정보 작성
			String dbInfo = "jdbc:oracle:thin:@localhost:1521:xe";

			String userName = "kh_ads";
			String password = "kh1234";

			// 2-3) DB연결 정보와 DriverManger를 이용해서 Connection 객체 생성
			conn = DriverManager.getConnection(dbInfo, userName, password);

			// 3. SQL 작성
			sc = new Scanner(System.in);

			System.out.print("최소 급여 : ");
			int minSalary = sc.nextInt();
			
			System.out.print("최대 급여 : ");
			int maxSalary = sc.nextInt();			

			/*
			String sql = "SELECT EMP_ID, EMP_NAME, SALARY FROM EMPLOYEE "
					+ "WHERE SALARY >=" + minSalary + 
					"AND SALARY <=" + maxSalary + 
					"ORDER BY SALARY DESC";
			*/
			
			// JAVA 13 이상부터 지원하는 TEXT BLOCK(""") 문법
			String sql = """
					SELECT EMP_ID, EMP_NAME, SALARY
					FROM EMPLOYEE
					WHERE SALARY BETWEEN
					""" + minSalary + " AND " + maxSalary + " ORDER BY SALARY DESC";
			
			// 4. Statement 객체 생성
			stmt = conn.createStatement();
//						ㄴ State 객체 생성 후에 참조변수에 담아줘야 하는 거 잊지말기!

			// 5. Statement 객체를 이용하여 SQL 수행 후 결과 반환 받기
			rs = stmt.executeQuery(sql);

			// 6. 조회 결과가 담겨있는 ResultSet을
			//    1행씩 접근해 각 행에 작성된 컬럼값 얻어오기
			// -> ResultSet에 1행 이상이 있을 것으로 예상되는 경우 반복문 사용
			while (rs.next()) {

				String empId = rs.getString("EMP_ID");
				String empName = rs.getString("EMP_NAME");
				int salary = rs.getInt("SALARY");

				System.out.printf("%s / %s / %d원 \n", empId, empName, salary);

			}

		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			
			// 7. 사용완료한 jdbc 객체 자원 반환
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
				if(conn != null) conn.close();
				
				if(sc != null) sc.close();
			
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			
		}

	}

}
