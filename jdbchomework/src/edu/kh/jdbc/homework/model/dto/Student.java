package edu.kh.jdbc.homework.model.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class Student {
	
	// 필드
	int stdNo;		// 학생번호
	String stdName; // 학생이름
	int stdAge;		// 학생나이
	String major;	// 전공
	Date entDate;	// 날짜

}
