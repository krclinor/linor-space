package com.linor.singer.domain2;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"singer_id", "title"})})
@Data
public class Album2 implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 100)
	private String title;

	//@Column(name = "release_date")
	private LocalDate releaseDate;

	@ManyToOne
	@JoinColumn(name = "singer_id", foreignKey = @ForeignKey(name="album2_fk_01"))
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Singer2 singer;

	@Version
	private int version;

}
