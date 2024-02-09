package io.github.jassonluiz.restwithspringbootandjava.unittests.mapper.mocks;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import io.github.jassonluiz.restwithspringbootandjava.dta.vo.v1.BookVO;
import io.github.jassonluiz.restwithspringbootandjava.model.Book;

public class MockBook {

	public Book mockEntity() {
		return mockEntity(0);
	}

	public BookVO mockVO() {
		return mockVO(0);
	}

	public List<Book> mockEntityList() {
		List<Book> books = new ArrayList<>();
		for (int i = 0; i < 14; i++) {
			books.add(mockEntity(i));
		}
		return books;
	}

	public List<BookVO> mockVOList() {
		List<BookVO> books = new ArrayList<>();
		for (int i = 0; i < 14; i++) {
			books.add(mockVO(i));
		}
		return books;
	}

	public Book mockEntity(Integer number) {
		Book book = new Book();
		book.setAuthor("Name Test " + number);
		book.setLaunchDate(Date.valueOf(LocalDate.now()));
		book.setPrice(Double.parseDouble(number.toString()));
		book.setTitle("Title " + number.toString());
		book.setId(number.longValue());
		return book;
	}

	public BookVO mockVO(Integer number) {
		BookVO book = new BookVO();
		book.setAuthor("Name Test " + number);
		book.setLaunchDate(Date.valueOf(LocalDate.now()));
		book.setPrice(Double.parseDouble(number.toString()));
		book.setTitle("Title " + number.toString());
		book.setKey(number.longValue());
		return book;
	}
}
