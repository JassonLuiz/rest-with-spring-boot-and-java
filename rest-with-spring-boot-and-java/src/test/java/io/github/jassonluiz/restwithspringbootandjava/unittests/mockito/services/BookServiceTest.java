package io.github.jassonluiz.restwithspringbootandjava.unittests.mockito.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.jassonluiz.restwithspringbootandjava.dta.vo.v1.BookVO;
import io.github.jassonluiz.restwithspringbootandjava.exceptions.RequiredObjectIsNullException;
import io.github.jassonluiz.restwithspringbootandjava.model.Book;
import io.github.jassonluiz.restwithspringbootandjava.repositories.BookRepository;
import io.github.jassonluiz.restwithspringbootandjava.services.BookService;
import io.github.jassonluiz.restwithspringbootandjava.unittests.mapper.mocks.MockBook;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

	MockBook input;

	@InjectMocks
	private BookService service;

	@Mock
	BookRepository repository;

	@BeforeEach
	void setUpMocks() throws Exception {
		input = new MockBook();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testFindById() {
		Book book = input.mockEntity();
		book.setId(1L);

		when(repository.findById(1L)).thenReturn(Optional.of(book));

		var result = service.findById(1L);
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());
		assertTrue(result.toString().contains("links: [</api/v1/book/1>;rel=\"self\"]"));
	}

	@Test
	void testFindAll() {
		List<Book> list = input.mockEntityList();

		when(repository.findAll()).thenReturn(list);

		var books = service.findAll();
		assertNotNull(books);
		assertEquals(14, books.size());
		assertTrue(books.toString().contains("links: [</api/v1/book/1>;rel=\"self\"]"));
	}

	@Test
	void testCreate() {
		Book book = input.mockEntity();

		Book persisted = book;
		persisted.setId(1L);

		BookVO vo = input.mockVO(1);
		vo.setKey(1L);

		when(repository.save(Mockito.any(Book.class))).thenReturn(persisted);

		var result = service.create(vo);
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());
		assertTrue(result.toString().contains("links: [</api/v1/book/1>;rel=\"self\"]"));
	}

	@Test
	void testCreatNullBook() {
		Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
			service.create(null);
		});

		String expectedMessage = "It is not allowed to persist a null object!";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void testUpdate() {
		Book book = input.mockEntity();
		book.setId(1L);

		Book persisted = book;
		persisted.setId(1L);

		BookVO vo = input.mockVO(1);
		vo.setKey(1L);

		when(repository.findById(1L)).thenReturn(Optional.of(book));
		when(repository.save(Mockito.any(Book.class))).thenReturn(persisted);

		var result = service.update(vo);
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());
		assertTrue(result.toString().contains("links: [</api/v1/book/1>;rel=\"self\"]"));

	}

	@Test
	void testUpdateWithNullBook() throws Exception {

		Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
			service.update(null);
		});

		String expectedMessage = "It is not allowed to persist a null object!";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void testDelete() {
		Book book = input.mockEntity();
		book.setId(1L);

		when(repository.findById(1L)).thenReturn(Optional.of(book));

		service.delete(1L);
	}

}
