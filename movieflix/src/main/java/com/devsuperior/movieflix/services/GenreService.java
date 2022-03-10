package com.devsuperior.movieflix.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
		Genre entity = obj.get();
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
			Genre entity = repository.getOne(id);
			copyDtoToEntityUpdate(dto, entity);
			entity = repository.save(entity);
			return new GenreDTO(entity, entity.getMovies());
	}
	
	public void delete(Long id) {
			repository.deleteById(id);
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
