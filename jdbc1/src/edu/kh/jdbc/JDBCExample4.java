package edu.kh.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class JDBCExample4 {

	public static void main(String[] args) {
		// 부서명을 입력받아
		// 해당 부서에 근무하는 사원의
		// 사번, 이름, 부서명, 직급명을
		// 직급코드 오름차순으로 조회

		// [실행화면]
		// 부서명 입력 : 총무부
		// 200 / 선동일 / 총무부 / 대표
		// 202 / 노옹철 / 총무부 / 부사장
		// ...

		// 부서명 입력 : 개발팀
		// 일치하는 부서가 없습니다!

		// hint : SQL 에서 '' (홑따옴표) 필요
		// ex) 총무부 입력 -> '총무부'

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		Scanner sc = null;

		try {

			// 2. DriverManager 객체 이용해서 Connection 객체 생성
			// 2-1) Oracle JDBC Driver 객체 메모리 로드
			Class.forName("oracle.jdbc.driver.OracleDriver");

			// 2-2 ) DB 연결 정보 작성
			String url = "jdbc:oracle:thin:@localhost:1521:xe";

			String userName = "kh_ads";
			String password = "kh1234";

			// 2-3) DB연결 정보와 DriverManger를 이용해서 Connection 객체 생성
			conn = DriverManager.getConnection(url, userName, password);

			// 3. SQL 작성
			sc = new Scanner(System.in);

			System.out.print("부서명 입력 : ");
			String deptName = sc.nextLine();

			// 부서명을 입력받아
			// 해당 부서에 근무하는 사원의
			// 사번, 이름, 부서명, 직급명을
			// 직급코드 오름차순으로 조회
			// 부서명 입력 : 개발팀
			// 일치하는 부서가 없습니다!

//			String sql = """
//					SELECT EMP_ID, EMP_NAME, DEPT_TITLE, JOB_NAME
//					FROM EMPLOYEE 
//					JOIN DEPARTMENT ON(DEPT_ID = DEPT_CODE)
//					JOIN JOB USING(JOB_CODE)
//					WHERE DEPT_TITLE = ?
//					ORDER BY JOB_CODE
//					""";

			String sql = """
					SELECT EMP_ID, EMP_NAME, DEPT_TITLE, JOB_NAME
					FROM EMPLOYEE
					JOIN JOB ON(EMPLOYEE.JOB_CODE = JOB.JOB_CODE)
					LEFT JOIN DEPARTMENT ON(DEPT_CODE = DEPT_ID)
					WHERE DEPT_TITLE = '""" + deptName + "' ORDER BY EMPLOYEE.JOB_CODE";

//			PreparedStatement pstmt = conn.prepareStatement(sql);
//			pstmt.setString(1, deptName);

			// 4. Statement 객체 생성
			stmt = conn.createStatement();
//						ㄴ State 객체 생성 후에 참조변수에 담아줘야 하는 거 잊지말기!

			// 5. Statement 객체를 이용하여 SQL 수행 후 결과 반환 받기
			rs = stmt.executeQuery(sql);

			// 6. 조회 결과가 담겨있는 ResultSet을
			// 1행씩 접근해 각 행에 작성된 컬럼값 얻어오기
			// -> ResultSet에 1행 이상이 있을 것으로 예상되는 경우 반복문 사용

			// 6-1) flag 이용법
			/*
			 * boolean flag = true; // 조회결과가 있다면 false, 없으면 true
			 * 
			 * while (rs.next()) {
			 * 
			 * flag = false; // 찾았음을 표시
			 * 
			 * String empId = rs.getString("EMP_ID"); String empName =
			 * rs.getString("EMP_NAME"); String empTitle = rs.getString("DEPT_TITLE");
			 * String jobName = rs.getString("JOB_NAME");
			 * 
			 * System.out.printf("%s / %s / %s / %s \n", empId, empName, empTitle, jobName);
			 * 
			 * }
			 * 
			 * if(flag) { System.out.println("일치하는 부서가 없습니다!"); }
			 */

			// 6-2) return 사용법
			if (!rs.next()) {
				System.out.println("일치하는 부서가 없습니다");
				return;
			}
			
			// 왜 do-while문을 사용해야 될까?
			// ㄴ 위 if문 조건에서 이미 첫번째행 커서가 소비됨.
			// ㄴ 보통 while문 사용 시 next()를 바로 만나면서 2행부터 접근하게됨.
			// do-while문 사용하여 next() 하지 않아도 1번째행 부터 접근할 수 있도록 함.
			
			do {
				String empId = rs.getString("EMP_ID");
				String empName = rs.getString("EMP_NAME");
				String empTitle = rs.getString("DEPT_TITLE");
				String jobName = rs.getString("JOB_NAME");

				System.out.printf("%s / %s / %s / %s \n", 
									empId, empName, empTitle, jobName);

			} while(rs.next());

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			// 7. 사용완료한 jdbc 객체 자원 반환
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();

				if (sc != null)
					sc.close();

			} catch (SQLException e) {

				e.printStackTrace();
			}

		}

	}

}
