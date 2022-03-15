package com.devsuperior.movieflix.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.devsuperior.movieflix.entities.Movie;
import com.devsuperior.movieflix.entities.Review;

public class MovieDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;

	@Size(min = 5, max = 60, message = "O Titulo deve ter entre 5 a 60 caracteres")
	@NotBlank(message = "Campo requerido")
	private String title;
	
	@Size(min = 5, max = 60, message ="A legenda deve ter entre 5 a 60 caracteres")
	@NotBlank(message = "Campo requerido")
	private String subTitle;
	
	@NotNull
	private Integer year;
	
	@NotBlank(message = "Campo requerido")
	private String imgUrl;
	
	@Size(min = 5, max = 60, message = "A sinopse deve ter entre 5 a 60 caracteres")
	@NotBlank(message = "Campo requerido")
	private String synopsis;

	private List<ReviewDTO> reviews = new ArrayList<>();

	public MovieDTO() {
	}

	public MovieDTO(Long id, String title, String subTitle, Integer year, String imgUrl, String synopsis) {
		this.id = id;
		this.title = title;
		this.subTitle = subTitle;
		this.year = year;
		this.imgUrl = imgUrl;
		this.synopsis = synopsis;
	}

	public MovieDTO(Movie entity) {
		this.id = entity.getId();
		this.title = entity.getTitle();
		this.subTitle = entity.getSubTitle();
		this.year = entity.getYear();
		this.imgUrl = entity.getImgUrl();
		this.synopsis = entity.getSynopsis();

	}

	public MovieDTO(Movie entity, Set<Review> reviews) {
		this(entity);
		reviews.forEach(review -> this.reviews.add(new ReviewDTO(review)));
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getSynopsis() {
		return synopsis;
	}

	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}

	public List<ReviewDTO> getReviews() {
		return reviews;
	}

}
