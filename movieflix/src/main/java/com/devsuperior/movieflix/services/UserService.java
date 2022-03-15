package com.devsuperior.movieflix.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.movieflix.dto.ReviewDTO;
import com.devsuperior.movieflix.dto.RoleDTO;
import com.devsuperior.movieflix.dto.UserDTO;
import com.devsuperior.movieflix.entities.Review;
import com.devsuperior.movieflix.entities.Role;
import com.devsuperior.movieflix.entities.User;
import com.devsuperior.movieflix.repositories.ReviewRepository;
import com.devsuperior.movieflix.repositories.RoleRepository;
import com.devsuperior.movieflix.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository repository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private ReviewRepository reviewRepository;

	@Transactional(readOnly = true)
	public Page<UserDTO> findAllPaged(PageRequest pageRequest) {
		Page<User> list = repository.findAll(pageRequest);
		return list.map(x -> new UserDTO(x));
	}

	@Transactional(readOnly = true)
	public UserDTO findById(Long id) {
		Optional<User> obj = repository.findById(id);
		User entity = obj.get();
		return new UserDTO(entity);
	}

	@Transactional
	public UserDTO insert(UserDTO dto) {
		User entity = new User();
		copyDtoToEntityInsert(dto, entity);
		entity = repository.save(entity);
		return new UserDTO(entity);
	}

	private void copyDtoToEntityInsert(UserDTO dto, User entity) {

		entity.setName(dto.getName());
		;
		entity.setEmail(dto.getEmail());
		entity.setPassword(dto.getPassword());

		entity.getRoles().clear();

		for (RoleDTO roleDto : dto.getRoles()) {
			Role role = roleRepository.getOne(roleDto.getId());
			entity.getRoles().add(role);
		}

		for (ReviewDTO revDto : dto.getReviews()) {
			Review review = new Review();
			review.setText(reviewRepository.getOne(revDto.getId()).getText());
			review.setUser(entity);
			entity.getReviews().add(review);
		}
	}

	@Transactional
	public UserDTO update(Long id, UserDTO dto) {
		User entity = repository.getOne(id);
		copyDtoToEntityUpdate(dto, entity);
		entity = repository.save(entity);
		return new UserDTO(entity);
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}

	private void copyDtoToEntityUpdate(UserDTO dto, User entity) {

		entity.setName(dto.getName());
		entity.setEmail(dto.getEmail());
		entity.setPassword(dto.getPassword());

		entity.getRoles().clear();

		for (RoleDTO roleDto : dto.getRoles()) {
			Role role = roleRepository.getOne(roleDto.getId());
			entity.getRoles().add(role);
		}

		for (ReviewDTO revDto : dto.getReviews()) {
			Review review = reviewRepository.getOne(revDto.getId());
			review.setUser(entity);
			entity.getReviews().add(review);
		}
	}
}
