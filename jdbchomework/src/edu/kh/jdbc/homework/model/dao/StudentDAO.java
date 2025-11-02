package edu.kh.jdbc.homework.model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import edu.kh.jdbc.homework.common.JDBCTemplate;
import edu.kh.jdbc.homework.model.dto.Student;

public class StudentDAO {

	private Statement stmt = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;

	/** 학생 수정 DAO
	 * @param conn
	 * @param std
	 * @return
	 * @throws Exception
	 */
	public int insertStudent(Connection conn, Student std) throws Exception {

		// 결과 저장용 변수 생성
		int result = 0;

		try {

			// sql문 작성
			String sql = """
					INSERT INTO KH_STUDENT
					VALUES(SEQ_STUDENT_NO.NEXTVAL, ?, ?, ?, DEFAULT)
					""";

			// PreparedStatement 객체 생성 후 예외 처리
			pstmt = conn.prepareStatement(sql);

			// 플레이스 홀더 지정
			pstmt.setString(1, std.getStdName());
			pstmt.setInt(2, std.getStdAge());
			pstmt.setString(3, std.getMajor());

			// SQL(INSERT) 수행(executeUpdate()) 후
			// 결과(삽입된 행의 갯수) 반환 받기
			result = pstmt.executeUpdate();

		} finally {

			// 사용한 jdbc 객체 자원 반환
			JDBCTemplate.close(pstmt);

		}

		return result;
	}

	/** 학생 전체 조회 DAO
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public List<Student> selectStudentByAll(Connection conn) throws Exception {

		List<Student> stdList = new ArrayList<Student>();

		try {

			String sql = """
					SELECT STD_NO, STD_NAME, STD_AGE, MAJOR, ENT_DATE
					FROM KH_STUDENT
					""";

			pstmt = conn.prepareStatement(sql);

			rs = pstmt.executeQuery();

			// 조회 시 단일 행이 아니기 때문에 while문 사용
			while (rs.next()) {

				int stdNo = rs.getInt("STD_NO");
				String stdName = rs.getString("STD_NAME");
				int stdAge = rs.getInt("STD_AGE");
				String major = rs.getString("MAJOR");
				Date entDate = rs.getDate("ENT_DATE");

				Student std = new Student(stdNo, stdName, stdAge, major, entDate);

				stdList.add(std);
			}

		} finally {

			JDBCTemplate.close(pstmt);
			JDBCTemplate.close(rs);
		}

		return stdList;
	}

	/**
	 * 3-1 학생 수정 조회 DAO
	 * 
	 * @param conn
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public int selectStudent(Connection conn, int stdNo) throws Exception {

		int studentNo = 0;

		try {
			String sql = """
					SELECT STD_NO
					FROM KH_STUDENT
					WHERE STD_NO = ?
					""";

			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, stdNo);

			rs = pstmt.executeQuery();

			// 조회된 행이 1개가 있을 경우
			if (rs.next()) {
				studentNo = rs.getInt("STD_NO");
			}

		} finally {
			JDBCTemplate.close(rs);
			JDBCTemplate.close(pstmt);
		}

		return studentNo;

	}


	/** 3-2 학생 수정 DAO
	 * @param conn
	 * @param std
	 * @param stdNo
	 * @return
	 * @throws Exception
	 */
	public int updateStudent(Connection conn, Student std, int stdNo) throws Exception {

		int result = 0;

		try {

			String sql = """
					UPDATE KH_STUDENT
					SET STD_NAME = ?, STD_AGE = ?, MAJOR = ?
					WHERE STD_NO = ?
					""";

			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, std.getStdName());
			pstmt.setInt(2, std.getStdAge());
			pstmt.setString(3, std.getMajor());
			pstmt.setInt(4, stdNo);

			result = pstmt.executeUpdate();

		} finally {

			JDBCTemplate.close(pstmt);

		}

		return result;
	}

	/** 학생 삭제 DAO
	 * @param conn
	 * @param stdNo
	 * @return
	 * @throws Exception
	 */
	public int deleteStudent(Connection conn, int stdNo) throws Exception {

		int result = 0;

		try {
			String sql = """
					DELETE FROM KH_STUDENT
					WHERE STD_NO = ?
					""";

			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, stdNo);

			result = pstmt.executeUpdate();

		} finally {

			JDBCTemplate.close(pstmt);
		}

		return result;
	}

	/** 전공별 학생 조회 DAO 
	 * ( 특정 전공 학생 필터링 조회에 대해서 생각을 해봤는데 어떻게 구현해야 할지 잘 모르겠습니다. )
	 * @param conn
	 * @param stdMajor
	 * @return
	 * @throws Exception
	 */
	public List<Student> selectStudentByMajor(Connection conn, String stdMajor) 
																throws Exception {
		
		List<Student> stdList = new ArrayList<Student>();
		
		try {
			
			String sql = """
					SELECT STD_NO, STD_NAME, STD_AGE, MAJOR, ENT_DATE
					FROM KH_STUDENT
					WHERE MAJOR LIKE ?
					""";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, "%" + stdMajor + "%");
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				int stdNo = rs.getInt("STD_NO");
				String stdName = rs.getString("STD_NAME");
				int stdAge = rs.getInt("STD_AGE");
				String major = rs.getString("MAJOR");
				Date entDate = rs.getDate("ENT_DATE");

				Student std = new Student(stdNo, stdName, stdAge, major, entDate);

				stdList.add(std);
				
			}
			
		} finally {
			
			JDBCTemplate.close(pstmt);
			
		}
		
		return stdList;
	}

}