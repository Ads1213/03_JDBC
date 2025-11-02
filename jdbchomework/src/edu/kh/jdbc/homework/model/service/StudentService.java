package edu.kh.jdbc.homework.model.service;

import java.sql.Connection;
import java.util.List;

import edu.kh.jdbc.homework.common.JDBCTemplate;
import edu.kh.jdbc.homework.model.dao.StudentDAO;
import edu.kh.jdbc.homework.model.dto.Student;

public class StudentService {

	Student std = new Student();
	StudentDAO dao = new StudentDAO();

	/**
	 * 학생 등록 Service
	 * 
	 * @param std
	 * @return
	 * @throws Exception
	 */
	public int insertStudent(Student std) throws Exception {

		// DB에 연결할 커넥션 객체 생성
		Connection conn = JDBCTemplate.getConnection();

		// 매개변수를 전달할 객체 생성
		int result = dao.insertStudent(conn, std);

		// 성공적으로 행 삽입 시 commit
		// 그렇지 않다면 rollback
		if (result > 0) {
			JDBCTemplate.commit(conn);
		} else {
			JDBCTemplate.rollback(conn);
		}

		JDBCTemplate.close(conn);

		return result;

	}

	/**
	 * 학생 전체 조회 Service
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Student> selectStudentByAll() throws Exception {

		Connection conn = JDBCTemplate.getConnection();

		List<Student> stdList = dao.selectStudentByAll(conn);

		JDBCTemplate.close(conn);

		return stdList;
	}

	/**
	 * 3-1 학생 수정 조회 Service
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public int selectStudentNo(int stdNo) throws Exception {

		Connection conn = JDBCTemplate.getConnection();

		int studentNo = dao.selectStudent(conn, stdNo);

		JDBCTemplate.close(conn);

		return studentNo;

	}

	/** 3-2 학생 수정 Service
	 * @param std
	 * @param stdNo
	 * @return
	 * @throws Exception
	 */
	public int updateStudent(Student std, int stdNo) throws Exception {

		Connection conn = JDBCTemplate.getConnection();

		int result = dao.updateStudent(conn, std, stdNo);

		if (result > 0) {
			JDBCTemplate.commit(conn);
		} else {
			JDBCTemplate.rollback(conn);
		}

		JDBCTemplate.close(conn);

		return result;
	}

	/**
	 * 학생 삭제 Service
	 * 
	 * @param sNum
	 * @return
	 * @throws Exception
	 */
	public int deleteStudent(int stdNo) throws Exception {

		Connection conn = JDBCTemplate.getConnection();

		int result = dao.deleteStudent(conn, stdNo);

		if (result > 0) {
			JDBCTemplate.commit(conn);
		} else {
			JDBCTemplate.rollback(conn);
		}

		JDBCTemplate.close(conn);

		return result;
	}

	/** 학생 전공 조회 Service
	 * @param stdMajor
	 * @return
	 * @throws Exception
	 */
	public List<Student> selectStudentByMajor(String stdMajor) throws Exception {

		Connection conn = JDBCTemplate.getConnection();

		List<Student> stdList = dao.selectStudentByMajor(conn, stdMajor);

		JDBCTemplate.close(conn);
		
		return stdList;
	}

}
