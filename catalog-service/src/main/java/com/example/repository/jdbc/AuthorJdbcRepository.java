package com.example.repository.jdbc;

import com.example.model.Author;
import com.example.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class AuthorJdbcRepository {
    private final JdbcTemplate jdbcTemplate;
    private final BookRowMapper bookRowMapper;

    public AuthorJdbcRepository(JdbcTemplate jdbcTemplate, BookRowMapper bookRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.bookRowMapper = bookRowMapper;
    }

    private static final class AuthorRowMapper implements RowMapper<Author> {
        @Override
        public Author mapRow(ResultSet rs, int rowNum) throws SQLException {
            Author author = new Author();
            author.setId(rs.getLong("id"));
            author.setName(rs.getString("name"));
            author.setBirthDate(rs.getObject("birth_date", LocalDate.class));
            author.setLocation(rs.getString("location"));
            author.setBio(rs.getString("bio"));
            return author;
        }
    }

    public void save(Author author) {
        if (author.getId() == null) {
            insert(author);
        } else {
            update(author);
        }
    }

    private void insert(Author author) {
        String sql = "INSERT INTO authors (name, birth_date, location, bio) " +
                "VALUES (?, ?, ?, ?) RETURNING id";
        Long id = jdbcTemplate.queryForObject(sql, Long.class,
                author.getName(),
                author.getBirthDate(),
                author.getLocation(),
                author.getBio());
        author.setId(id);
    }

    private void update(Author author) {
        String sql = "UPDATE authors SET name = ?, birth_date = ?, location = ?, bio = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sql,
                author.getName(),
                author.getBirthDate(),
                author.getLocation(),
                author.getBio(),
                author.getId());
    }

    public Optional<Author> findById(Long id) {
        String sql = "SELECT * FROM authors WHERE id = ?";
        try {
            Author author = jdbcTemplate.queryForObject(sql, new AuthorRowMapper(), id);
            if (author != null) {
                loadAuthorBooks(author);
            }
            return Optional.ofNullable(author);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM authors WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM authors WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    public Page<Author> findByLocation(String location, Pageable pageable) {
        String sql = "SELECT * FROM authors WHERE location = ? " +
                "LIMIT ? OFFSET ?";
        List<Author> authors = jdbcTemplate.query(sql, new AuthorRowMapper(),
                location,
                pageable.getPageSize(),
                pageable.getOffset());

        authors.forEach(this::loadAuthorBooks);

        String countSql = "SELECT COUNT(*) FROM authors WHERE location = ?";
        int total = jdbcTemplate.queryForObject(countSql, Integer.class, location);

        return new PageImpl<>(authors, pageable, total);
    }

    public Page<Author> findByBirthDateBetween(LocalDate lowBound, LocalDate highBound, Pageable pageable) {
        String sql = "SELECT * FROM authors WHERE birth_date BETWEEN ? AND ? " +
                "LIMIT ? OFFSET ?";
        List<Author> authors = jdbcTemplate.query(sql, new AuthorRowMapper(),
                lowBound,
                highBound,
                pageable.getPageSize(),
                pageable.getOffset());

        authors.forEach(this::loadAuthorBooks);

        String countSql = "SELECT COUNT(*) FROM authors WHERE birth_date BETWEEN ? AND ?";
        int total = jdbcTemplate.queryForObject(countSql, Integer.class, lowBound, highBound);

        return new PageImpl<>(authors, pageable, total);
    }

    public Page<Author> findByBookGenre(String genre, Pageable pageable) {
        String sql = "SELECT DISTINCT a.* FROM authors a JOIN books b ON a.id = b.author_id " +
                "WHERE b.genre = ? LIMIT ? OFFSET ?";
        List<Author> authors = jdbcTemplate.query(sql, new AuthorRowMapper(),
                genre,
                pageable.getPageSize(),
                pageable.getOffset());

        authors.forEach(this::loadAuthorBooks);

        String countSql = "SELECT COUNT(DISTINCT a.id) FROM authors a JOIN books b ON a.id = b.author_id " +
                "WHERE b.genre = ?";
        int total = jdbcTemplate.queryForObject(countSql, Integer.class, genre);

        return new PageImpl<>(authors, pageable, total);
    }

    public Page<Author> findByNameContainingIgnoreCase(String name, Pageable pageable) {
        String sql = "SELECT * FROM authors WHERE LOWER(name) LIKE LOWER(?) " +
                "LIMIT ? OFFSET ?";
        List<Author> authors = jdbcTemplate.query(sql, new AuthorRowMapper(),
                "%" + name + "%",
                pageable.getPageSize(),
                pageable.getOffset());

        authors.forEach(this::loadAuthorBooks);

        String countSql = "SELECT COUNT(*) FROM authors WHERE LOWER(name) LIKE LOWER(?)";
        int total = jdbcTemplate.queryForObject(countSql, Integer.class, "%" + name + "%");

        return new PageImpl<>(authors, pageable, total);
    }

    public Page<Author> findAllByOrderByNameAsc(Pageable pageable) {
        return findAllWithOrder("name ASC", pageable);
    }

    public Page<Author> findAllByOrderByNameDesc(Pageable pageable) {
        return findAllWithOrder("name DESC", pageable);
    }

    public Page<Author> findAllByOrderByBirthDateAsc(Pageable pageable) {
        return findAllWithOrder("birth_date ASC", pageable);
    }

    public Page<Author> findAllByOrderByBirthDateDesc(Pageable pageable) {
        return findAllWithOrder("birth_date DESC", pageable);
    }

    public Page<Author> findAllOrderByBooksCountAsc(Pageable pageable) {
        String sql = "SELECT a.*, COUNT(b.id) as book_count FROM authors a " +
                "LEFT JOIN books b ON a.id = b.author_id " +
                "GROUP BY a.id ORDER BY book_count ASC " +
                "LIMIT ? OFFSET ?";

        List<Author> authors = jdbcTemplate.query(sql, new AuthorRowMapper(),
                pageable.getPageSize(),
                pageable.getOffset());

        authors.forEach(this::loadAuthorBooks);

        String countSql = "SELECT COUNT(*) FROM authors";
        int total = jdbcTemplate.queryForObject(countSql, Integer.class);

        return new PageImpl<>(authors, pageable, total);
    }

    public Page<Author> findAllOrderByBooksCountDesc(Pageable pageable) {
        String sql = "SELECT a.*, COUNT(b.id) as book_count FROM authors a " +
                "LEFT JOIN books b ON a.id = b.author_id " +
                "GROUP BY a.id ORDER BY book_count DESC " +
                "LIMIT ? OFFSET ?";

        List<Author> authors = jdbcTemplate.query(sql, new AuthorRowMapper(),
                pageable.getPageSize(),
                pageable.getOffset());

        authors.forEach(this::loadAuthorBooks);

        String countSql = "SELECT COUNT(*) FROM authors";
        int total = jdbcTemplate.queryForObject(countSql, Integer.class);

        return new PageImpl<>(authors, pageable, total);
    }

    private Page<Author> findAllWithOrder(String orderClause, Pageable pageable) {
        String sql = "SELECT * FROM authors ORDER BY " + orderClause + " LIMIT ? OFFSET ?";
        List<Author> authors = jdbcTemplate.query(sql, new AuthorRowMapper(),
                pageable.getPageSize(),
                pageable.getOffset());

        authors.forEach(this::loadAuthorBooks);

        String countSql = "SELECT COUNT(*) FROM authors";
        int total = jdbcTemplate.queryForObject(countSql, Integer.class);

        return new PageImpl<>(authors, pageable, total);
    }

    private void loadAuthorBooks(Author author) {
        String sql = "SELECT * FROM books WHERE author_id = ?";
        List<Book> books = jdbcTemplate.query(sql, bookRowMapper, author.getId());
        author.setBooks(books);
    }
}