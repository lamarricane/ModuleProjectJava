package com.example.repository.jdbc;

import com.example.model.Author;
import com.example.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class BookJdbcRepository {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final BookRowMapper bookRowMapper;
    private final BookWithAuthorRowMapper bookWithAuthorRowMapper;

    public BookJdbcRepository(JdbcTemplate jdbcTemplate,
                              NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                              BookRowMapper bookRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.bookRowMapper = bookRowMapper;
        this.bookWithAuthorRowMapper = new BookWithAuthorRowMapper();
    }

    public void save(Book book) {
        if (book.getId() == null) {
            insert(book);
        } else {
            update(book);
        }
    }

    private void insert(Book book) {
        String sql = "INSERT INTO books (title, genre, pages_number, publishing_date, description, author_id) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        Long id = jdbcTemplate.queryForObject(sql, Long.class,
                book.getTitle(),
                book.getGenre(),
                book.getPagesNumber(),
                book.getPublishingDate(),
                book.getDescription(),
                book.getAuthor().getId());
        book.setId(id);
    }

    private void update(Book book) {
        String sql = "UPDATE books SET title = ?, genre = ?, pages_number = ?, " +
                "publishing_date = ?, description = ?, author_id = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sql,
                book.getTitle(),
                book.getGenre(),
                book.getPagesNumber(),
                book.getPublishingDate(),
                book.getDescription(),
                book.getAuthor().getId(),
                book.getId());
    }

    public Optional<Book> findById(Long id) {
        String sql = "SELECT b.*, a.id as author_id, a.name as author_name, " +
                "a.birth_date as author_birth_date, a.location as author_location, " +
                "a.bio as author_bio FROM books b JOIN authors a ON b.author_id = a.id " +
                "WHERE b.id = ?";
        try {
            Book book = jdbcTemplate.queryForObject(sql, bookWithAuthorRowMapper, id);
            return Optional.ofNullable(book);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM books WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM books WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    public Page<Book> findByGenre(String genre, Pageable pageable) {
        String sql = "SELECT b.* FROM books b WHERE genre = ? " +
                "LIMIT ? OFFSET ?";
        List<Book> books = jdbcTemplate.query(sql, bookRowMapper,
                genre,
                pageable.getPageSize(),
                pageable.getOffset());

        String countSql = "SELECT COUNT(*) FROM books WHERE genre = ?";
        int total = jdbcTemplate.queryForObject(countSql, Integer.class, genre);

        return new PageImpl<>(books, pageable, total);
    }

    public Page<Book> findByPublishingDateBetween(LocalDate lowBound, LocalDate highBound, Pageable pageable) {
        String sql = "SELECT b.* FROM books b WHERE publishing_date BETWEEN ? AND ? " +
                "LIMIT ? OFFSET ?";
        List<Book> books = jdbcTemplate.query(sql, bookRowMapper,
                lowBound,
                highBound,
                pageable.getPageSize(),
                pageable.getOffset());

        String countSql = "SELECT COUNT(*) FROM books WHERE publishing_date BETWEEN ? AND ?";
        int total = jdbcTemplate.queryForObject(countSql, Integer.class, lowBound, highBound);

        return new PageImpl<>(books, pageable, total);
    }

    public Page<Book> findByPagesNumberBetween(int minPages, int maxPages, Pageable pageable) {
        String sql = "SELECT b.* FROM books b WHERE pages_number BETWEEN ? AND ? " +
                "LIMIT ? OFFSET ?";
        List<Book> books = jdbcTemplate.query(sql, bookRowMapper,
                minPages,
                maxPages,
                pageable.getPageSize(),
                pageable.getOffset());

        String countSql = "SELECT COUNT(*) FROM books WHERE pages_number BETWEEN ? AND ?";
        int total = jdbcTemplate.queryForObject(countSql, Integer.class, minPages, maxPages);

        return new PageImpl<>(books, pageable, total);
    }

    public Page<Book> findByAuthorNameContainingIgnoreCase(String authorName, Pageable pageable) {
        String sql = "SELECT b.* FROM books b JOIN authors a ON b.author_id = a.id " +
                "WHERE LOWER(a.name) LIKE LOWER(?) " +
                "LIMIT ? OFFSET ?";
        List<Book> books = jdbcTemplate.query(sql, bookRowMapper,
                "%" + authorName + "%",
                pageable.getPageSize(),
                pageable.getOffset());

        String countSql = "SELECT COUNT(*) FROM books b JOIN authors a ON b.author_id = a.id " +
                "WHERE LOWER(a.name) LIKE LOWER(?)";
        int total = jdbcTemplate.queryForObject(countSql, Integer.class, "%" + authorName + "%");

        return new PageImpl<>(books, pageable, total);
    }

    public Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable) {
        String sql = "SELECT b.* FROM books b WHERE LOWER(title) LIKE LOWER(?) " +
                "LIMIT ? OFFSET ?";
        List<Book> books = jdbcTemplate.query(sql, bookRowMapper,
                "%" + title + "%",
                pageable.getPageSize(),
                pageable.getOffset());

        String countSql = "SELECT COUNT(*) FROM books WHERE LOWER(title) LIKE LOWER(?)";
        int total = jdbcTemplate.queryForObject(countSql, Integer.class, "%" + title + "%");

        return new PageImpl<>(books, pageable, total);
    }

    public Page<Book> findAllByOrderByTitleAsc(Pageable pageable) {
        return findAllWithOrder("title ASC", pageable);
    }

    public Page<Book> findAllByOrderByTitleDesc(Pageable pageable) {
        return findAllWithOrder("title DESC", pageable);
    }

    public Page<Book> findAllByOrderByPagesNumberAsc(Pageable pageable) {
        return findAllWithOrder("pages_number ASC", pageable);
    }

    public Page<Book> findAllByOrderByPagesNumberDesc(Pageable pageable) {
        return findAllWithOrder("pages_number DESC", pageable);
    }

    public Page<Book> findAllByOrderByPublishingDateAsc(Pageable pageable) {
        return findAllWithOrder("publishing_date ASC", pageable);
    }

    public Page<Book> findAllByOrderByPublishingDateDesc(Pageable pageable) {
        return findAllWithOrder("publishing_date DESC", pageable);
    }

    private Page<Book> findAllWithOrder(String orderClause, Pageable pageable) {
        String sql = "SELECT b.* FROM books b ORDER BY " + orderClause + " LIMIT ? OFFSET ?";
        List<Book> books = jdbcTemplate.query(sql, bookRowMapper,
                pageable.getPageSize(),
                pageable.getOffset());

        String countSql = "SELECT COUNT(*) FROM books";
        int total = jdbcTemplate.queryForObject(countSql, Integer.class);

        return new PageImpl<>(books, pageable, total);
    }

    public List<Map<String, Object>> getFullAuthorStats() {
        String sql = "SELECT a.id, a.name, COUNT(b.id) as book_count, " +
                "MIN(b.publishing_date) as first_publish_date, " +
                "MAX(b.publishing_date) as last_publish_date, " +
                "AVG(b.pages_number) as avg_pages " +
                "FROM authors a LEFT JOIN books b ON a.id = b.author_id " +
                "GROUP BY a.id, a.name " +
                "ORDER BY book_count DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> stats = new HashMap<>();
            stats.put("authorId", rs.getLong("id"));
            stats.put("authorName", rs.getString("name"));
            stats.put("bookCount", rs.getInt("book_count"));
            stats.put("firstPublishDate", rs.getDate("first_publish_date"));
            stats.put("lastPublishDate", rs.getDate("last_publish_date"));
            stats.put("avgPages", rs.getDouble("avg_pages"));
            return stats;
        });
    }

    public List<Map<String, Object>> getGenreStats() {
        String sql = "SELECT genre, COUNT(*) as book_count, " +
                "AVG(pages_number) as avg_pages " +
                "FROM books " +
                "GROUP BY genre " +
                "ORDER BY book_count DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> stats = new HashMap<>();
            stats.put("genre", rs.getString("genre"));
            stats.put("bookCount", rs.getInt("book_count"));
            stats.put("avgPages", rs.getDouble("avg_pages"));
            return stats;
        });
    }

    public List<Map<String, Object>> getAuthorStatsSummary() {
        String sql = "SELECT a.id, a.name, COUNT(b.id) as book_count " +
                "FROM authors a LEFT JOIN books b ON a.id = b.author_id " +
                "GROUP BY a.id, a.name " +
                "ORDER BY book_count DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> stats = new HashMap<>();
            stats.put("authorId", rs.getLong("id"));
            stats.put("authorName", rs.getString("name"));
            stats.put("bookCount", rs.getInt("book_count"));
            return stats;
        });
    }

    public Map<String, Object> getCombinedAuthorStats() {
        Map<String, Object> result = new HashMap<>();

        String authorsCountSql = "SELECT COUNT(*) FROM authors";
        int authorsCount = jdbcTemplate.queryForObject(authorsCountSql, Integer.class);
        result.put("totalAuthors", authorsCount);

        String topAuthorSql = "SELECT a.name, COUNT(b.id) as book_count " +
                "FROM authors a LEFT JOIN books b ON a.id = b.author_id " +
                "GROUP BY a.id, a.name " +
                "ORDER BY book_count DESC LIMIT 1";
        Map<String, Object> topAuthor = jdbcTemplate.queryForObject(topAuthorSql, (rs, rowNum) -> {
            Map<String, Object> author = new HashMap<>();
            author.put("authorName", rs.getString("name"));
            author.put("bookCount", rs.getInt("book_count"));
            return author;
        });
        result.put("topAuthor", topAuthor);

        String avgBooksSql = "SELECT AVG(book_count) FROM (" +
                "SELECT COUNT(b.id) as book_count " +
                "FROM authors a LEFT JOIN books b ON a.id = b.author_id " +
                "GROUP BY a.id) as counts";
        double avgBooks = jdbcTemplate.queryForObject(avgBooksSql, Double.class);
        result.put("avgBooksPerAuthor", avgBooks);

        return result;
    }

    private static final class BookWithAuthorRowMapper implements RowMapper<Book> {
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
}