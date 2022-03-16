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

import com.devsuperior.movieflix.dto.MovieDTO;
import com.devsuperior.movieflix.dto.ReviewDTO;
import com.devsuperior.movieflix.entities.Genre;
import com.devsuperior.movieflix.entities.Movie;
import com.devsuperior.movieflix.entities.Review;
import com.devsuperior.movieflix.repositories.GenreRepository;
import com.devsuperior.movieflix.repositories.MovieRepository;
import com.devsuperior.movieflix.repositories.ReviewRepository;
import com.devsuperior.movieflix.services.exceptions.DatabaseException;
import com.devsuperior.movieflix.services.exceptions.ResourceNotFoundException;

@Service
public class MovieService {

	@Autowired
	private MovieRepository repository;

	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private GenreRepository genreRepository;

	@Transactional(readOnly = true)
	public Page<MovieDTO> findAllPaged(Long genreId, String title, PageRequest pageRequest) {
		Genre genre = (genreId == 0L) ? null : genreRepository.getOne(genreId);
		Page<Movie> page = repository.find(genre, title, pageRequest);
		repository.findMoviesWithGenre(page.getContent());
		return page.map(x -> new MovieDTO(x, x.getReviews()));
	}

	@Transactional(readOnly = true)
	public MovieDTO findById(Long id) {
		Optional<Movie> obj = repository.findById(id);
		Movie entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return new MovieDTO(entity, entity.getReviews());
	}

	@Transactional
	public MovieDTO insert(MovieDTO dto) {
		Movie entity = new Movie();
		copyDtoToEntityInsert(dto, entity);
		entity = repository.save(entity);
		return new MovieDTO(entity, entity.getReviews());
	}

	@Transactional
	public MovieDTO update(Long id, MovieDTO dto) {
		try {
			Movie entity = repository.getOne(id);
			copyDtoToEntityUpdate(dto, entity);
			entity = repository.save(entity);
			return new MovieDTO(entity, entity.getReviews());
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

	private void copyDtoToEntityInsert(MovieDTO dto, Movie entity) {
		entity.setTitle(dto.getTitle());
		entity.setSubTitle(dto.getSubTitle());
		entity.setYear(dto.getYear());
		entity.setImgUrl(dto.getImgUrl());
		entity.setSynopsis(dto.getSynopsis());

		entity.getReviews().clear();

		for (ReviewDTO revDto : dto.getReviews()) {
			Review review = new Review();
			review.setText(reviewRepository.getOne(revDto.getId()).getText());
			review.setMovie(entity);
			entity.getReviews().add(review);
		}
	}

	private void copyDtoToEntityUpdate(MovieDTO dto, Movie entity) {
		entity.setTitle(dto.getTitle());
		entity.setSubTitle(dto.getSubTitle());
		entity.setYear(dto.getYear());
		entity.setImgUrl(dto.getImgUrl());
		entity.setSynopsis(dto.getSynopsis());

		entity.getReviews().clear();

		for (ReviewDTO revDto : dto.getReviews()) {
			Review review = reviewRepository.getOne(revDto.getId());
			review.setMovie(entity);
			entity.getReviews().add(review);
		}
	}

}
