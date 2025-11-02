package edu.kh.jdbc.homework.view;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import edu.kh.jdbc.homework.model.dto.Student;
import edu.kh.jdbc.homework.model.service.StudentService;

public class StudentView {

	// 필드
	private StudentService service = new StudentService();
	private Scanner sc = new Scanner(System.in);

	/**
	 * Student 관리 프로그램 메인 메뉴 UI (View)
	 */
	public void mainMenu() {

		int input = 0; // 메뉴 선택용 변수

		do {

			try {

				System.out.println("\n===== 학생 관리 프로그램 =====\n");
				System.out.println("1. 학생 등록(새로운 학생 정보 삽입)");
				System.out.println("2. 전체 학생 조회(모든 학생 정보 조회)");
				System.out.println("3. 학생 정보 수정(이름, 나이, 전공 변경)");
				System.out.println("4. 학생 삭제(학번 기준 삭제)");
				System.out.println("5. 전공별 학생 조회(특전 전공 학생만 필터링 조회)");

				System.out.print("메뉴 선택 : ");
				input = sc.nextInt();
				sc.nextLine(); // 버퍼에 남은 개행문자 제거

				switch (input) {
				case 1:
					insertStudent();
					break;
				case 2:
					selectStudentByAll();
					break;
				case 3:
					updateStudent();
					break;
				case 4:
					deleteStudent();
					break;
				case 5:
					selectStudentByMajor();
					break;
				case 0:
					System.out.println("\n[프로그램 종료]\n");
					break;
				default:
					System.out.println("\n[메뉴 번호만 입력하세요]\n");
				}
				System.out.println("\n-------------------------------------\n");

			} catch (InputMismatchException e) {
				// Scanner를 이용한 입력 시 자료형이 잘못된 경우
				System.out.println("\n***잘못 입력하셨습니다***\n");

				input = -1; // 잘못 입력해서 while문 멈추는걸 방지
				sc.nextLine(); // 입력 버퍼에 남아있는 잘못된 문자 제거

			} catch (Exception e) {
				// 발생되는 예외를 모두 해당 catch 구문으로 모아서 처리
				e.printStackTrace();
			}

		} while (input != 0);

	}


	/** 학생 등록 View
	 * @throws Exception
	 */
	private void insertStudent() throws Exception {

		// 학생의 정보를 전달할 객체 생성
		Student std = new Student();

		// 입력 받은 정보를 세팅
		System.out.print("학생 이름 입력 : ");
		String stdName = sc.nextLine();

		System.out.print("학생 나이 입력 : ");
		int stdAge = sc.nextInt();
		sc.nextLine(); // 버퍼에 남은 엔터 제거

		System.out.print("학생 전공 입력 : ");
		String stdMajor = sc.nextLine();
		System.out.println();

		std.setStdName(stdName);
		std.setStdAge(stdAge);
		std.setMajor(stdMajor);

		// 입력 받은 정보를 한 번에 전달하기 위해 담아주기
		int result = service.insertStudent(std);

		// 조건문 작성
		if (result > 0)
			System.out.println(stdName + "님이 등록되었습니다.");
		else
			System.out.println("등록 실패");
		return;

	}

	/** 학생 전체 조회 View
	 * @throws Exception
	 */
	private void selectStudentByAll() throws Exception {

		System.out.println("\n=====2. 학생 전체 조회=====\n");

		// 전체 조회를 위해 List 객체 생성
		List<Student> stdList = service.selectStudentByAll();

		if (stdList.isEmpty()) {
			System.out.println("조회 결과가 없습니다.");
			return; // 조회 결과가 없을 경우 메서드 종료
		}

		for (Student std : stdList) {
			System.out.println(std);
		}
	}
	
	
	/** 학생 수정 View
	 * @throws Exception
	 */
	private void updateStudent() throws Exception {
		
		System.out.println("\n   ==========학생의 이름, 나이, 전공 수정==========\n");
		
		System.out.print("학생 번호 : ");
		int stdNo = sc.nextInt();
		
		Student std = new Student();
		
		int studentNo = service.selectStudentNo(stdNo);
			
		// 조회 결과가 없을 때
		if (studentNo == 0) {
			System.out.println("해당 학생 번호는 조회되지 않습니다.");
			return;
		}
			
			// 조회 결과 있을 때
			System.out.print("이름 입력 : ");
			String name = sc.next();
			
			System.out.print("나이 입력 : ");
			int age = sc.nextInt();
			sc.nextLine(); // 입력 버퍼 제거
			
			System.out.print("전공 입력 : ");
			String major = sc.next();
			
			std.setStdName(name);
			std.setStdAge(age);
			std.setMajor(major);
			
			// std 변수에 데이터를 담아 Service에 전달
			int result = service.updateStudent(std, stdNo);
			
			if (result > 0) {
				System.out.println("\n수정 성공");
			} else {
				System.out.println("수정 실패");
				return;
			}
			
		}
	
	/** 학생 삭제 View
	 * @throws Exception
	 */
	private void deleteStudent() throws Exception {
		
		System.out.println("=============== 학생 삭제 ===============");
		
		System.out.print("학생 번호 입력 : ");
		int stdNo = sc.nextInt();
		
		int result = service.deleteStudent(stdNo);
		
		if(result > 0) {
			System.out.println(stdNo + "번 학생 삭제 성공");
		} else {
			System.out.println("삭제 실패");
		}
		
	}

	/** 전공별 학생 조회 View
	 * @throws Exception
	 */
	private void selectStudentByMajor() throws Exception {
		
		System.out.println("=============== 전공별 학생 조회 ===============");
		
		System.out.print("전공 입력 : ");
		String stdMajor = sc.next();
		
		List<Student> stdList = service.selectStudentByMajor(stdMajor);
		
		if(stdList.isEmpty()) {
			System.out.println("검색 결과 없음");
			return;
		} 
		
		for(Student std : stdList) {
			System.out.println(std);
		}
		
		
		

	}

}
