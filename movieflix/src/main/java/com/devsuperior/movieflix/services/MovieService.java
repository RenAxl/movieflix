package com.devsuperior.movieflix.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.movieflix.dto.MovieDTO;
import com.devsuperior.movieflix.dto.ReviewDTO;
import com.devsuperior.movieflix.entities.Movie;
import com.devsuperior.movieflix.entities.Review;
import com.devsuperior.movieflix.repositories.MovieRepository;
import com.devsuperior.movieflix.repositories.ReviewRepository;

@Service
public class MovieService {

	@Autowired
	private MovieRepository repository;
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Transactional(readOnly = true)
	public Page<MovieDTO> findAllPaged(PageRequest pageRequest) {
		Page<Movie> page = repository.findAll(pageRequest);

		return page.map(x -> new MovieDTO(x, x.getReviews()));
	}
	
	@Transactional(readOnly = true)
	public MovieDTO findById(Long id) {
		Optional<Movie> obj = repository.findById(id);
		Movie entity = obj.get();
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
			Movie entity = repository.getOne(id);
			copyDtoToEntityUpdate(dto, entity);
			entity = repository.save(entity);
			return new MovieDTO(entity, entity.getReviews());
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
