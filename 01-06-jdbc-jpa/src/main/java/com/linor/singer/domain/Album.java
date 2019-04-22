package com.linor.singer.domain;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
//@Table(name = "album")
@Data
public class Album implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String title;

	//@Column(name = "release_date")
	private LocalDate releaseDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "singer_id")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Singer singer;

	@Version
	private int version;

}
