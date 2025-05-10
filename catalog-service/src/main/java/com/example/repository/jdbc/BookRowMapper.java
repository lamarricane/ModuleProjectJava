package com.example.repository.jdbc;

import com.example.model.Book;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class BookRowMapper implements RowMapper<Book> {
    @Override
    public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
        Book book = new Book();
        book.setId(rs.getLong("id"));
        book.setTitle(rs.getString("title"));
        book.setGenre(rs.getString("genre"));
        book.setPagesNumber(rs.getInt("pages_number"));
        book.setPublishingDate(rs.getObject("publishing_date", LocalDate.class));
        book.setDescription(rs.getString("description"));
        return book;
    }
}