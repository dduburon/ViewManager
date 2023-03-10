package com.viewmanager.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.postgresql.util.PSQLException;
import org.springframework.jdbc.UncategorizedSQLException;

import java.util.List;

public interface ViewMapper {

    void dropView(@Param("view_name") String view_name) throws UncategorizedSQLException;
    void dropMatView(@Param("view_name") String view_name) throws UncategorizedSQLException;
    void createView(@Param("view_name") String view_name, @Param("sql") String sql);
    void createMatView(@Param("view_name") String view_name, @Param("sql") String sql);
    List<String> getAllViewsFromDB();
}
