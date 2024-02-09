package io.github.jassonluiz.restwithspringbootandjava.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.jassonluiz.restwithspringbootandjava.controllers.BookController;
import io.github.jassonluiz.restwithspringbootandjava.dta.vo.v1.BookVO;
import io.github.jassonluiz.restwithspringbootandjava.exceptions.RequiredObjectIsNullException;
import io.github.jassonluiz.restwithspringbootandjava.exceptions.ResourceNotFoundException;
import io.github.jassonluiz.restwithspringbootandjava.mapper.DozerMapper;
import io.github.jassonluiz.restwithspringbootandjava.model.Book;
import io.github.jassonluiz.restwithspringbootandjava.repositories.BookRepository;

@Service
public class BookService {

	private Logger logger = Logger.getLogger(BookService.class.getName());

	@Autowired
	BookRepository repository;

	public List<BookVO> findAll() {
		logger.info("Finding all books!");

		var books = DozerMapper.parseListObjects(repository.findAll(), BookVO.class);
		books.stream().forEach(b -> {
			try {
				b.add(linkTo(methodOn(BookController.class).findById(b.getKey())).withSelfRel());
			} catch (Exception e) {
				logger.info(e.getMessage());
				e.printStackTrace();
			}
		});
		return books;
	}

	public BookVO findById(Long id) {
		logger.info("Finding one book!");

		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		var vo = DozerMapper.parseObject(entity, BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
		return vo;
	}

	public BookVO create(BookVO book) {
		if (book == null) {
			throw new RequiredObjectIsNullException();
		}

		logger.info("Creating one book!");

		var entity = DozerMapper.parseObject(book, Book.class);
		var vo = DozerMapper.parseObject(repository.save(entity), BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(entity.getId())).withSelfRel());
		return vo;
	}

	public BookVO update(BookVO book) {
		if (book == null) {
			throw new RequiredObjectIsNullException();
		}

		logger.info("Updating one book!");

		var entity = repository.findById(book.getKey())
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

		entity.setAuthor(book.getAuthor());
		entity.setLaunchDate(book.getLaunchDate());
		entity.setPrice(book.getPrice());
		entity.setTitle(book.getTitle());

		var vo = DozerMapper.parseObject(repository.save(entity), BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	public void delete(Long id) {
		logger.info("Deleting one book!");
		
		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this IS!"));
		
		repository.delete(entity);
	}

}
