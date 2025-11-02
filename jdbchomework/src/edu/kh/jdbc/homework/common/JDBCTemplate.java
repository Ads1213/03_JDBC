package edu.kh.jdbc.homework.common;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class JDBCTemplate {
	
	// 필드
	private static Connection conn = null;
	
	// 메서드
	
	/** 호출 시 Connection 객체를 생성하여 호출한 곳으로 반환하는 메서드
	 * + AutoCommit 끄기
	 * 예약어가 포함된 메서드를 생성할 경우 다른 클래스에서 클래스명.메서드로 호출 가능!
	 * @return conn
	 */
	public static Connection getConnection() {
		
		try {
			
			if(conn != null && !conn.isClosed()) return conn;
			
			Properties prop = new Properties();
			
			prop.loadFromXML(new FileInputStream("driver.xml"));
			
			Class.forName(prop.getProperty("driver"));
			
			conn = DriverManager.getConnection( prop.getProperty("url"),
												prop.getProperty("userName"),
												prop.getProperty("password"));
			
			conn.setAutoCommit(false);
			
		} catch (Exception e) {
			System.out.println("커넥션 생성 중 예외 발생(JDBCTemplate의 getConnection())");
			e.printStackTrace();
		}
		
		return conn;
		
	}
	
	
	/** 전달 받은 커넥션에서 수행한 SQL을 commit 하는 메서드
	 * 
	 */
	public static void commit(Connection conn) {
		
		try {
			if(conn != null && !conn.isClosed()) conn.commit();
			
		} catch (Exception e) {
			System.out.println("커밋 중 예외 발생");
			e.printStackTrace();
		}
	}

	
	/** 전달 받은 커넥션에서 수행한 SQL을 rollback 하는 메서드
	 * @param conn
	 */
	public static void rollback(Connection conn) {
		
		try {
			// 커넥션이 null이 아니고 닫혀있지도 않으면 커넥션을 통해 롤백해주도록 하겠다.
			if(conn != null && !conn.isClosed()) conn.rollback();
			
		} catch (Exception e) {
			System.out.println("롤백 중 예외발생");
			e.printStackTrace();
		}
		
	}
	
	/** 전달받은 커넥션을 close(자원반환)하는 메서드
	 * 
	 */
	public static void close(Connection conn) {
		
		try {
			if(conn != null && !conn.isClosed()) conn.close();
			
			
		} catch (Exception e) {
			System.out.println("커넥션 close() 중 예외 발생");
			e.printStackTrace();
		}
		
	}
	
	public static void close(Statement stmt) {
		
		try {
			if(stmt != null && !stmt.isClosed()) stmt.close();
			
		} catch (Exception e) {
			System.out.println("Statement close() 중 예외 발생");
			e.printStackTrace();
		}
		
	}
	
	/** 전달 받은 ResultSet을 close() 하는 메서드
	 * @param rs
	 */
	public static void close(ResultSet rs) {
		
		try {
			if(rs != null && !rs.isClosed()) rs.close();
			
		} catch (Exception e) {
			System.out.println("ResultSet close() 중 예외발생");
			e.printStackTrace();
		}
		
	}
	

}
