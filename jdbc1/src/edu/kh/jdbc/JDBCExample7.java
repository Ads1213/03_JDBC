package edu.kh.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class JDBCExample7 {

	public static void main(String[] args) {

		// EMPLOYEE 테이블에서
		// 사번, 이름, 성별, 급여, 직급명, 부서명을 조회
		// 단, 입력 받은 조건에 맞는 결과만 조회하고 정렬할 것

		// - 조건 1 : 성별 (M, F)
		// - 조건 2 : 급여 범위
		// - 조건 3 : 급여 오름차순/내림차순

		// [실행화면]
		// 조회할 성별(M/F) : F
		// 급여 범위(최소, 최대 순서로 작성) :
		// 3000000
		// 4000000
		// 급여 정렬(1.ASC, 2.DESC) : 2

		// 사번 | 이름 | 성별 | 급여 | 직급명 | 부서명
		// --------------------------------------------------------
		// 217 | 전지연 | F | 3660000 | 대리 | 인사관리부
		// -------------------------------------------------------

		// 사번 | 이름 | 성별 | 급여 | 직급명 | 부서명
		// --------------------------------------------------------
		// 218 | 이오리 | F | 3890000 | 사원 | 없음
		// 203 | 송은희 | F | 3800000 | 차장 | 해외영업2부
		// 212 | 장쯔위 | F | 3550000 | 대리 | 기술지원부
		// 222 | 이태림 | F | 3436240 | 대리 | 기술지원부
		// 207 | 하이유 | F | 3200000 | 과장 | 해외영업1부
		// 210 | 윤은해 | F | 3000000 | 사원 | 해외영업1부
		
		// 1. JDBC 객체 참조변수 선언
		
		// DB와 연결 도와주는 객체 생성
		Connection conn = null;

		// SQL 문 실행 결과를 JAVA -> DB
		// DB -> JAVA로 반환
		PreparedStatement pstmt = null;

		// SELECT 문 결과를 저장할 객체 생성
		ResultSet rs = null;

		// 정보를 입력 받을 객체 생성
		Scanner sc = new Scanner(System.in);

		try {
			
			// 2. DriverManager를 이용해서 Connection 생성

			// 오라클과 연결
			Class.forName("oracle.jdbc.driver.OracleDriver");

			// 오라클 정보 작성
			String url = "jdbc:oracle:thin:@localhost:1521:xe";
			String userName = "kh_ads";
			String password = "kh1234";

			// conn 에게 전달
			conn = DriverManager.getConnection(url, userName, password);
			
			System.out.print("조회할 성별(M/F) : ");
			String gender = sc.next().toUpperCase(); // 모든 문자열을 대문자로 변경
			
			System.out.print("최소 급여 : ");
			int minSalary = sc.nextInt();
			
			System.out.print("최대 급여 : ");
			int maxSalary = sc.nextInt();
			
			System.out.print("급여 정렬(1.ASC, 2.DESC) : ");
			int sort = sc.nextInt();
			String order = (sort == 1) ? "ASC" : "DESC";
			
			// 3. SQL문 작성
			String sql = """
					SELECT EMP_ID, EMP_NAME,
					CASE SUBSTR(EMP_NO, 8, 1)
						WHEN '1' THEN 'M'
						WHEN '3' THEN 'M'
						WHEN '2' THEN 'F'
						WHEN '4' THEN 'F'
						END AS GENDER, SALARY, JOB_NAME, NVL(DEPT_TITLE, '없음') DEPT_TITLE
					FROM EMPLOYEE JOIN JOB USING(JOB_CODE)
					LEFT JOIN DEPARTMENT ON(DEPT_ID = DEPT_CODE)
					WHERE SALARY BETWEEN ? AND ?
					AND SUBSTR(EMP_NO, 8, 1) IN (?, ?)
					ORDER BY SALARY""" + order;
			
			// ORDER BY 절에 ? (위치홀더) 사용 시 오류 : SQL 명령어가 올바르게 종료되지 않았습니다.
			// 왜?
			// PreparedStatement의 위치홀더(?)는 ** 데이터 값(리터럴) ** 을 대체하는 용도로만 사용가능.
			// -> SQL에서 ORDER BY 절의 정렬 기준 (ASC/DESC)와 같은
			// -> SQL구문(문법)의 일부는 PreparedStatement의 위치 홀더(?)로 대체될 수 없음.
			
			// 4. PrepareStatement 객체 생성
			pstmt = conn.prepareStatement(sql);
			
			// 5. ? 위치 홀더에 알맞은 값 세팅
			pstmt.setInt(1, minSalary);
			pstmt.setInt(2, maxSalary);
			
			// M을 입력 받아 SUBSTR로 잘라온 숫자가 1, 3일 경우 M
			if(gender.equals("M")) { 
				pstmt.setString(3, "1");
				pstmt.setString(4, "3");
			} else if (gender.equals("F")) {
				pstmt.setString(3, "2");
				pstmt.setString(4, "4");
			} else {
				System.out.println("잘못된 성별 입력입니다. (M 또는 F만 입력)");
				return;
			}
			
			// sql문 결과를 보여주기 위한 executeQuery 메서드 사용
			rs = pstmt.executeQuery();
			
			System.out.println("사번 | 이름   | 성별 | 급여    | 직급명 | 부서명");
			System.out.println("--------------------------------------------------------");

			
			boolean flag = true;
			
			while (rs.next()) {
				
				flag = false; // while문이 1회 이상 반복됨 == 조회 결과가 1행이라도 있다.

				String empId 	= rs.getString("EMP_ID");
				String empName 	= rs.getString("EMP_NAME");
				String gen 		= rs.getString("GENDER");
				int salary 		= rs.getInt("SALARY");
				String jobName 	= rs.getString("JOB_NAME");
				String deptTitle = rs.getString("DEPT_TITLE");

				System.out.printf("%-4s | %3s | %-4s | %7d | %-3s  | %s \n",
						empId, empName, gen, salary, jobName, deptTitle);
			}
			
			if(flag) { // flag == true인 경우 -> while문 안쪽 수행 X -> 조회 결과가 1행도 없음
				System.out.println("조회 결과 없음");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			// 8. 사용한 JDBC 객체 자원 반환
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
				if (conn != null) conn.close();

				sc.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
