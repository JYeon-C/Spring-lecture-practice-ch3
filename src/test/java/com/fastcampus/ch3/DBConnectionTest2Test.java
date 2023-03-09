package com.fastcampus.ch3;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/**/root-context.xml"})
public class DBConnectionTest2Test {
    @Autowired // 아래 주석 처리문과 같은 역할. AC 주입받아 사용 가능
    DataSource ds;

    // insertUser 메서드를 테스트 하는 메서드
    @Test
    public void insertUserTest() throws Exception {
        User user = new User("asdf", "1234", "abc", "aaaa@Aaa.com", new Date(), "fb", new Date());
        deleteAll();
        int rowCnt = insertUser(user);

        assertTrue(rowCnt == 1);
    }

    @Test
    public void selectUserTest() throws Exception {
        deleteAll();
        User user = new User("asdf2", "1234", "abc", "aaaa@Aaa.com", new Date(), "fb", new Date());
        int rowCnt = insertUser(user);
        User user2 = selectUser("asdf2");

        assertTrue(user.getId().equals("asdf2"));
    }

    @Test
    public void deleteUserTest() throws Exception {
        deleteAll();
        int rowCnt = deleteUser("asdf");

        assertTrue(rowCnt == 0);

        // insert한 후 delete
        User user = new User("asdf2", "1234", "abc", "aaaa@Aaa.com", new Date(), "fb", new Date());
        rowCnt = insertUser(user);
        assertTrue(rowCnt == 1);

        rowCnt = deleteUser(user.getId());
        assertTrue(rowCnt == 1);

        assertTrue(selectUser(user.getId())==null);
    }

    // 매개변수로 받은 사용자 정보로 user_info테이블을 uqdate하는 메서드 -- 과제
    public int updateUser(User user) throws Exception {
        return 0;
    }



    // id에 해당하는 user 삭제
    public int deleteUser(String id) throws Exception {
        Connection conn = ds.getConnection();

        String sql = "delete from user_info where id =?";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1,id);
//        int rowCnt = pstmt.executeUpdate(); // insert, delete, update
//        return rowCnt;
        return pstmt.executeUpdate(); // 위와 결과 같음
    }


    // user_info테이블의 key가 id라서 id를 주고 해당하는 사람 정보 가져온다.
    public User selectUser(String id) throws Exception{
        Connection conn = ds.getConnection();

        String sql = "select * from user_info where id = ?";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1,id);
        ResultSet rs = pstmt.executeQuery(); // select

        if(rs.next()) {
            User user = new User();
            user.setId(rs.getString(1));
            user.setPwd(rs.getString(2));
            user.setName(rs.getString(3));
            user.setEmail(rs.getString(4));
            user.setBirth(new Date(rs.getDate(5).getTime()));
            user.setSns(rs.getString(6));
            user.setReg_date(new Date(rs.getDate(7).getTime()));

            return user;
        }
        return null; // 결과가 없으면 rs.next가 false로 null 반환
    }


    private void deleteAll() throws Exception{
        Connection conn = ds.getConnection();

        // 실행할 sql문 작성
        String sql = "delete from user_info";

        // sql문 실행
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.executeUpdate(); // insert, delete, update
    }


    // 사용자 정보를 user_info테이블에 저장하는 메서드
    public int insertUser(User user) throws Exception {
        Connection conn = ds.getConnection();

//        insert into user_info(id, pwd, name, email, birth, sns, reg_date)
//        values ('zxcv','2345','jyeon','ccc@ccc.com','1996-01-03','facebook',now());

        // 실행할 sql문 작성
        String sql = "insert into user_info values (?,?,?,?,?,?,now())";

        // sql문 실행
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, user.getId()); // 첫번째 ?의 값을 user의 id로 채운다.
        pstmt.setString(2, user.getPwd()); // 두번째 ?의 값을 user의 id로 채운다.
        pstmt.setString(3, user.getName()); // 세번째 ?의 값을 user의 id로 채운다.
        pstmt.setString(4, user.getEmail()); // 네번째 ?의 값을 user의 id로 채운다.
        pstmt.setDate(5, new java.sql.Date(user.getBirth().getTime())); // 다섯번째 ?의 값을 user의 id로 채운다. Date는 sql Date
        pstmt.setString(6, user.getSns()); // 여섯번째 ?의 값을 user의 id로 채운다.

        // 쿼리 실행
        int rowCnt = pstmt.executeUpdate(); // 결과를 rowCnt로 받아서 반환

        return rowCnt;
    }

    @Test
    public void springJdbcConnectTest() throws Exception{
//        ApplicationContext ac = new GenericXmlApplicationContext("file:src/main/webapp/WEB-INF/spring/**/root-context.xml");
//        DataSource ds = ac.getBean(DataSource.class);

        Connection conn = ds.getConnection(); // 데이터베이스의 연결을 얻는다.

        System.out.println("conn = " + conn);
        assertTrue(conn!=null); // 테스트가 성공했는지 assert문으로 확인한다. 괄호 안에 조건식이 true면 테스트 성공, 아니면 실패
    }
}