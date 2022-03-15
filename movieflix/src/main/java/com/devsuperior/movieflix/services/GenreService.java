package com.devsuperior.movieflix.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.movieflix.dto.GenreDTO;
import com.devsuperior.movieflix.dto.MovieDTO;
import com.devsuperior.movieflix.entities.Genre;
import com.devsuperior.movieflix.entities.Movie;
import com.devsuperior.movieflix.repositories.GenreRepository;
import com.devsuperior.movieflix.repositories.MovieRepository;
import com.devsuperior.movieflix.services.exceptions.DatabaseException;
import com.devsuperior.movieflix.services.exceptions.ResourceNotFoundException;

@Service
public class GenreService {

	@Autowired
	private GenreRepository repository;

	@Autowired
	private MovieRepository movieRepository;

	@Transactional(readOnly = true)
	public Page<GenreDTO> findAllPaged(PageRequest pageRequest) {
		Page<Genre> page = repository.findAll(pageRequest);

		return page.map(x -> new GenreDTO(x, x.getMovies()));
	}

	@Transactional(readOnly = true)
	public GenreDTO findById(Long id) {
		Optional<Genre> obj = repository.findById(id);
		Genre entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return new GenreDTO(entity, entity.getMovies());
	}

	@Transactional
	public GenreDTO insert(GenreDTO dto) {
		Genre entity = new Genre();
		copyDtoToEntityInsert(dto, entity);
		entity = repository.save(entity);
		return new GenreDTO(entity, entity.getMovies());
	}

	@Transactional
	public GenreDTO update(Long id, GenreDTO dto) {
		try {
			Genre entity = repository.getOne(id);
			copyDtoToEntityUpdate(dto, entity);
			entity = repository.save(entity);
			return new GenreDTO(entity, entity.getMovies());
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
	}

	public void delete(Long id) {
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
	}

	private void copyDtoToEntityInsert(GenreDTO dto, Genre entity) {
		entity.setName(dto.getName());

		entity.getMovies().clear();

		for (MovieDTO movDto : dto.getMovies()) {
			Movie movie = new Movie();
			movie.setTitle(movieRepository.getOne(movDto.getId()).getTitle());
			movie.setSubTitle(movieRepository.getOne(movDto.getId()).getSubTitle());
			movie.setYear(movieRepository.getOne(movDto.getId()).getYear());
			movie.setImgUrl(movieRepository.getOne(movDto.getId()).getImgUrl());
			movie.setSynopsis(movieRepository.getOne(movDto.getId()).getSynopsis());

			movie.setGenre(entity);

			entity.getMovies().add(movie);
		}
	}

	private void copyDtoToEntityUpdate(GenreDTO dto, Genre entity) {
		entity.setName(dto.getName());

		entity.getMovies().clear();

		for (MovieDTO movDto : dto.getMovies()) {
			Movie movie = movieRepository.getOne(movDto.getId());

			movie.setGenre(entity);

			entity.getMovies().add(movie);
		}
	}
}
