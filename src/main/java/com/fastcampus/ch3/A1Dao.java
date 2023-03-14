package com.fastcampus.ch3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class A1Dao {
    @Autowired
    DataSource ds;

    public int insert(int key, int value) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
//            conn = ds.getConnection();
            conn = DataSourceUtils.getConnection(ds);
            pstmt = conn.prepareStatement("insert into a1 value(?,?)");
            pstmt.setInt(1,key);
            pstmt.setInt(2,value);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // 예외를 던짐
        } finally {
//            close(conn,pstmt); // 커넥션을 닫으면 트랜잭션 종료
            close(pstmt);
            DataSourceUtils.releaseConnection(conn,ds); // TxManager가 트랜잭션 close 판단
        }
    }

    private void close(AutoCloseable... acs) {
        for(AutoCloseable ac :acs)
            try { if(ac!=null) ac.close(); } catch(Exception e) { e.printStackTrace(); }
    }

    public void deleteAll() throws Exception{
        Connection conn = ds.getConnection();
        String sql = "delete from a1";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.executeUpdate();
        close(pstmt);
    }
}
