package com.example.repository.jdbc;

import com.example.model.Author;
import com.example.model.Book;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Component
public class BookWithAuthorRowMapper implements RowMapper<Book> {
    @Override
    public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
        Book book = new Book();
        book.setId(rs.getLong("id"));
        book.setTitle(rs.getString("title"));
        book.setGenre(rs.getString("genre"));
        book.setPagesNumber(rs.getInt("pages_number"));
        book.setPublishingDate(rs.getObject("publishing_date", LocalDate.class));
        book.setDescription(rs.getString("description"));

        Author author = new Author();
        author.setId(rs.getLong("author_id"));
        author.setName(rs.getString("author_name"));
        author.setBirthDate(rs.getObject("author_birth_date", LocalDate.class));
        author.setLocation(rs.getString("author_location"));
        author.setBio(rs.getString("author_bio"));

        book.setAuthor(author);
        return book;
    }
}