package com.bank.teller.util;

import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

public class DBUtil {

    private static EmbeddedDatabase getDataSource() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();

        EmbeddedDatabase db = builder.setType(EmbeddedDatabaseType.DERBY).addScript("sql/createschema.sql").addScript("sql/insertdata.sql").build();
        return db;
    }

    @Bean
    public static JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(getDataSource());
    }

}
