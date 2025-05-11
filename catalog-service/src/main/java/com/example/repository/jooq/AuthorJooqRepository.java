package com.example.repository.jooq;

import com.example.model.Author;
import com.example.model.Book;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.jooq.generated.Tables.AUTHORS;
import static org.jooq.generated.Tables.BOOKS;

/**
 * jOOQ репозиторий для работы с авторами.
 * Использует типобезопасные запросы jOOQ.
 */
@Repository
public class AuthorJooqRepository {
    private final DSLContext dsl;

    public AuthorJooqRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Author save(Author author) {
        Long generatedId = dsl.insertInto(AUTHORS)
                .set(AUTHORS.NAME, author.getName())
                .set(AUTHORS.BIRTH_DATE, author.getBirthDate())
                .set(AUTHORS.LOCATION, author.getLocation())
                .set(AUTHORS.BIO, author.getBio())
                .returning(AUTHORS.ID)
                .fetchOne()
                .get(AUTHORS.ID);

        author.setId(generatedId);
        return author;
    }

    public void deleteById(long id) {
        dsl.deleteFrom(AUTHORS)
                .where(AUTHORS.ID.eq(id))
                .execute();
    }

    public boolean existsById(long id) {
        return dsl.fetchExists(
                dsl.selectFrom(AUTHORS)
                        .where(AUTHORS.ID.eq(id))
        );
    }

    public Page<Author> findAll(Pageable pageable, org.jooq.SortField<?>... sortFields) {
        List<Author> authors = dsl.selectFrom(AUTHORS)
                .orderBy(sortFields)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchInto(Author.class);

        authors.forEach(this::loadAuthorBooks);

        long total = dsl.fetchCount(AUTHORS);
        return new PageImpl<>(authors, pageable, total);
    }

    public Optional<Author> findById(long id) {
        Author author = dsl.selectFrom(AUTHORS)
                .where(AUTHORS.ID.eq(id))
                .fetchOneInto(Author.class);
        loadAuthorBooks(author);
        return Optional.ofNullable(author);
    }

    public Page<Author> findByNameContainingIgnoreCase(String name, Pageable pageable) {
        List<Author> authors = dsl.selectFrom(AUTHORS)
                .where(AUTHORS.NAME.likeIgnoreCase("%" + name + "%"))
                .orderBy(getSortFields(pageable))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchInto(Author.class);

        authors.forEach(this::loadAuthorBooks);

        long total = dsl.fetchCount(
                dsl.selectFrom(AUTHORS)
                        .where(AUTHORS.NAME.likeIgnoreCase("%" + name + "%"))
        );

        return new PageImpl<>(authors, pageable, total);
    }

    public Page<Author> findByBirthDateBetween(LocalDate lowBound, LocalDate highBound, Pageable pageable) {
        List<Author> authors = dsl.selectFrom(AUTHORS)
                .where(AUTHORS.BIRTH_DATE.between(lowBound, highBound))
                .orderBy(getSortFields(pageable))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchInto(Author.class);

        authors.forEach(this::loadAuthorBooks);

        long total = dsl.fetchCount(
                dsl.selectFrom(AUTHORS)
                        .where(AUTHORS.BIRTH_DATE.between(lowBound, highBound))
        );

        return new PageImpl<>(authors, pageable, total);
    }

    public Page<Author> findByLocation(String location, Pageable pageable) {
        List<Author> authors = dsl.selectFrom(AUTHORS)
                .where(AUTHORS.LOCATION.eq(location))
                .orderBy(getSortFields(pageable))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchInto(Author.class);

        authors.forEach(this::loadAuthorBooks);

        long total = dsl.fetchCount(
                dsl.selectFrom(AUTHORS)
                        .where(AUTHORS.LOCATION.eq(location))
        );

        return new PageImpl<>(authors, pageable, total);
    }

    public Page<Author> findByBookGenre(String genre, Pageable pageable) {
        List<Author> authors = dsl.selectDistinct(AUTHORS.fields())
                .from(AUTHORS)
                .join(BOOKS).on(AUTHORS.ID.eq(BOOKS.AUTHOR_ID))
                .where(BOOKS.GENRE.eq(genre))
                .orderBy(getSortFields(pageable))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchInto(Author.class);

        authors.forEach(this::loadAuthorBooks);

        long total = dsl.fetchCount(
                dsl.selectDistinct(AUTHORS.ID)
                        .from(AUTHORS)
                        .join(BOOKS).on(AUTHORS.ID.eq(BOOKS.AUTHOR_ID))
                        .where(BOOKS.GENRE.eq(genre))
        );

        return new PageImpl<>(authors, pageable, total);
    }

    public Page<Author> findAllByOrderByNameAsc(Pageable pageable) {
        return findAll(pageable, AUTHORS.NAME.asc());
    }

    public Page<Author> findAllByOrderByNameDesc(Pageable pageable) {
        return findAll(pageable, AUTHORS.NAME.desc());
    }

    public Page<Author> findAllByOrderByBirthDateAsc(Pageable pageable) {
        return findAll(pageable, AUTHORS.BIRTH_DATE.asc());
    }

    public Page<Author> findAllByOrderByBirthDateDesc(Pageable pageable) {
        return findAll(pageable, AUTHORS.BIRTH_DATE.desc());
    }

    public Page<Author> findAllOrderByBooksCountAsc(Pageable pageable) {
        return findAllOrderByBooksCount(pageable, DSL.count(BOOKS.ID).asc());
    }

    public Page<Author> findAllOrderByBooksCountDesc(Pageable pageable) {
        return findAllOrderByBooksCount(pageable, DSL.count(BOOKS.ID).desc());
    }

    private Page<Author> findAllOrderByBooksCount(Pageable pageable, org.jooq.SortField<?> sortField) {
        List<Author> authors = dsl.select(AUTHORS.fields())
                .from(AUTHORS)
                .leftJoin(BOOKS).on(AUTHORS.ID.eq(BOOKS.AUTHOR_ID))
                .groupBy(AUTHORS.ID)
                .orderBy(sortField)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchInto(Author.class);

        long total = dsl.fetchCount(AUTHORS);

        return new PageImpl<>(authors, pageable, total);
    }

    private org.jooq.SortField<?>[] getSortFields(Pageable pageable) {
        return pageable.getSort().stream()
                .map(order -> {
                    org.jooq.Field<?> field = DSL.field(order.getProperty());
                    return order.isAscending() ? field.asc() : field.desc();
                })
                .toArray(org.jooq.SortField[]::new);
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