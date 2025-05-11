package com.example.repository.jooq;

import com.example.dto.jooq.*;
import com.example.model.Author;
import com.example.model.Book;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import static org.jooq.generated.tables.Authors.AUTHORS;
import static org.jooq.generated.tables.Books.BOOKS;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * jOOQ репозиторий для работы с книгами.
 * Использует типобезопасные запросы jOOQ.
 */
@Repository
public class BookJooqRepository {
    private final DSLContext dsl;

    public BookJooqRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Book save(Book book) {
        Author author = book.getAuthor();
        if (author != null && author.getId() != null) {
            author = dsl.selectFrom(AUTHORS)
                    .where(AUTHORS.ID.eq(author.getId()))
                    .fetchOneInto(Author.class);
            book.setAuthor(author);
        }

        if (book.getId() == null) {
            Long generatedId = dsl.insertInto(BOOKS)
                    .set(BOOKS.TITLE, book.getTitle())
                    .set(BOOKS.GENRE, book.getGenre())
                    .set(BOOKS.PAGES_NUMBER, book.getPagesNumber())
                    .set(BOOKS.PUBLISHING_DATE, book.getPublishingDate())
                    .set(BOOKS.DESCRIPTION, book.getDescription())
                    .set(BOOKS.AUTHOR_ID, author != null ? author.getId() : null)
                    .returning(BOOKS.ID)
                    .fetchOne()
                    .get(BOOKS.ID);

            book.setId(generatedId);
        } else {
            dsl.update(BOOKS)
                    .set(BOOKS.TITLE, book.getTitle())
                    .set(BOOKS.GENRE, book.getGenre())
                    .set(BOOKS.PAGES_NUMBER, book.getPagesNumber())
                    .set(BOOKS.PUBLISHING_DATE, book.getPublishingDate())
                    .set(BOOKS.DESCRIPTION, book.getDescription())
                    .set(BOOKS.AUTHOR_ID, author != null ? author.getId() : null)
                    .where(BOOKS.ID.eq(book.getId()))
                    .execute();
        }
        return book;
    }

    public void deleteById(long id) {
        dsl.deleteFrom(BOOKS)
                .where(BOOKS.ID.eq(id))
                .execute();
    }

    public boolean existsById(long id) {
        return dsl.fetchExists(
                dsl.selectFrom(BOOKS)
                        .where(BOOKS.ID.eq(id))
        );
    }

    public Page<Book> findAll(Pageable pageable, org.jooq.SortField<?>... sortFields) {
        List<Book> books = dsl.selectFrom(BOOKS)
                .orderBy(sortFields)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchInto(Book.class);

        long total = dsl.fetchCount(BOOKS);

        return new PageImpl<>(books, pageable, total);
    }

    public Optional<Book> findById(long id) {
        Book book = dsl.select(BOOKS.fields())
                .select(AUTHORS.fields()) // Загружаем все поля автора
                .from(BOOKS)
                .join(AUTHORS).on(BOOKS.AUTHOR_ID.eq(AUTHORS.ID))
                .where(BOOKS.ID.eq(id))
                .fetchOne(r -> {
                    Book b = r.into(BOOKS).into(Book.class);
                    Author author = r.into(AUTHORS).into(Author.class);
                    b.setAuthor(author);
                    return b;
                });

        return Optional.ofNullable(book);
    }

    public Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable) {
        List<Book> books = dsl.selectFrom(BOOKS)
                .where(BOOKS.TITLE.likeIgnoreCase("%" + title + "%"))
                .orderBy(getSortFields(pageable))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchInto(Book.class);

        long total = dsl.fetchCount(
                dsl.selectFrom(BOOKS)
                        .where(BOOKS.TITLE.likeIgnoreCase("%" + title + "%"))
        );

        return new PageImpl<>(books, pageable, total);
    }

    public Page<Book> findByGenre(String genre, Pageable pageable) {
        List<Book> books = dsl.selectFrom(BOOKS)
                .where(BOOKS.GENRE.eq(genre))
                .orderBy(getSortFields(pageable))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchInto(Book.class);

        long total = dsl.fetchCount(
                dsl.selectFrom(BOOKS)
                        .where(BOOKS.GENRE.eq(genre))
        );

        return new PageImpl<>(books, pageable, total);
    }

    public Page<Book> findByPagesNumberBetween(int minPages, int maxPages, Pageable pageable) {
        List<Book> books = dsl.selectFrom(BOOKS)
                .where(BOOKS.PAGES_NUMBER.between(minPages, maxPages))
                .orderBy(getSortFields(pageable))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchInto(Book.class);

        long total = dsl.fetchCount(
                dsl.selectFrom(BOOKS)
                        .where(BOOKS.PAGES_NUMBER.between(minPages, maxPages))
        );

        return new PageImpl<>(books, pageable, total);
    }

    public Page<Book> findByPublishingDateBetween(LocalDate lowBound, LocalDate highBound, Pageable pageable) {
        List<Book> books = dsl.selectFrom(BOOKS)
                .where(BOOKS.PUBLISHING_DATE.between(lowBound, highBound))
                .orderBy(getSortFields(pageable))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchInto(Book.class);

        long total = dsl.fetchCount(
                dsl.selectFrom(BOOKS)
                        .where(BOOKS.PUBLISHING_DATE.between(lowBound, highBound))
        );

        return new PageImpl<>(books, pageable, total);
    }



    public Page<Book> findByAuthorNameContainingIgnoreCase(String authorName, Pageable pageable) {
        List<Book> books = dsl.select(BOOKS.fields())
                .from(BOOKS)
                .join(AUTHORS).on(BOOKS.AUTHOR_ID.eq(AUTHORS.ID))
                .where(AUTHORS.NAME.likeIgnoreCase("%" + authorName + "%"))
                .orderBy(getSortFields(pageable))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch()
                .map(r -> {
                    Book book = r.into(BOOKS).into(Book.class);
                    Author author = new Author();
                    author.setId(r.get(BOOKS.AUTHOR_ID));
                    book.setAuthor(author);
                    return book;
                });

        long total = dsl.selectCount()
                .from(BOOKS)
                .join(AUTHORS).on(BOOKS.AUTHOR_ID.eq(AUTHORS.ID))
                .where(AUTHORS.NAME.likeIgnoreCase("%" + authorName + "%"))
                .fetchOne(0, Long.class);

        return new PageImpl<>(books, pageable, total);
    }

    public Page<Book> findAllByOrderByTitleAsc(Pageable pageable) {
        return findAll(pageable, BOOKS.TITLE.asc());
    }

    public Page<Book> findAllByOrderByTitleDesc(Pageable pageable) {
        return findAll(pageable, BOOKS.TITLE.desc());
    }

    public Page<Book> findAllByOrderByPagesNumberAsc(Pageable pageable) {
        return findAll(pageable, BOOKS.PAGES_NUMBER.asc());
    }

    public Page<Book> findAllByOrderByPagesNumberDesc(Pageable pageable) {
        return findAll(pageable, BOOKS.PAGES_NUMBER.desc());
    }

    public Page<Book> findAllByOrderByPublishingDateAsc(Pageable pageable) {
        return findAll(pageable, BOOKS.PUBLISHING_DATE.asc());
    }

    public Page<Book> findAllByOrderByPublishingDateDesc(Pageable pageable) {
        return findAll(pageable, BOOKS.PUBLISHING_DATE.desc());
    }

    private org.jooq.SortField<?>[] getSortFields(Pageable pageable) {
        return pageable.getSort().stream()
                .map(order -> {
                    org.jooq.Field<?> field = DSL.field(order.getProperty());
                    return order.isAscending() ? field.asc() : field.desc();
                })
                .toArray(org.jooq.SortField[]::new);
    }

    public List<AuthorStatsDto> getFullAuthorStats() {
        return dsl.select(
                        AUTHORS.NAME,
                        DSL.count(BOOKS.ID).as("bookCount"),
                        DSL.avg(BOOKS.PAGES_NUMBER).as("avgPages"),
                        DSL.min(BOOKS.PUBLISHING_DATE).as("firstPublishDate"),
                        DSL.max(BOOKS.PUBLISHING_DATE).as("lastPublishDate")
                )
                .from(AUTHORS)
                .leftJoin(BOOKS).on(AUTHORS.ID.eq(BOOKS.AUTHOR_ID))
                .groupBy(AUTHORS.ID, AUTHORS.NAME)
                .fetchInto(AuthorStatsDto.class);
    }

    public List<GenreStatsDto> getGenreStats() {
        return dsl.select(
                        BOOKS.GENRE,
                        DSL.count().as("bookCount"),
                        DSL.avg(BOOKS.PAGES_NUMBER).as("avgPages")
                )
                .from(BOOKS)
                .groupBy(BOOKS.GENRE)
                .fetchInto(GenreStatsDto.class);
    }

    public List<AuthorSummaryDto> getAuthorStatsSummary() {
        return dsl.select(
                        AUTHORS.NAME,
                        DSL.count(BOOKS.ID).as("bookCount"),
                        DSL.sum(BOOKS.PAGES_NUMBER).as("totalPages")
                )
                .from(AUTHORS)
                .leftJoin(BOOKS).on(AUTHORS.ID.eq(BOOKS.AUTHOR_ID))
                .groupBy(AUTHORS.ID, AUTHORS.NAME)
                .fetchInto(AuthorSummaryDto.class);
    }

    public CombinedStatsDto getCombinedAuthorStats() {
        CombinedStatsDto result = new CombinedStatsDto();

        result.setAuthors(dsl.select(
                        AUTHORS.NAME,
                        DSL.count(BOOKS.ID).as("bookCount"),
                        DSL.avg(BOOKS.PAGES_NUMBER).as("avgPages")
                )
                .from(AUTHORS)
                .leftJoin(BOOKS).on(AUTHORS.ID.eq(BOOKS.AUTHOR_ID))
                .groupBy(AUTHORS.ID, AUTHORS.NAME)
                .fetchInto(AuthorSummaryDto.class));

        result.setTotals(dsl.select(
                        DSL.count(BOOKS.ID).as("totalBooks"),
                        DSL.sum(BOOKS.PAGES_NUMBER).as("totalPages"),
                        DSL.avg(BOOKS.PAGES_NUMBER).as("avgPagesAll")
                )
                .from(BOOKS)
                .fetchOneInto(TotalStatsDto.class));

        return result;
    }

    private void loadAuthorBooks(Author author) {
        if (author != null) {
            List<Book> books = dsl.selectFrom(BOOKS)
                    .where(BOOKS.AUTHOR_ID.eq(author.getId()))
                    .fetchInto(Book.class);
            author.setBooks(books);
        }
    }
}