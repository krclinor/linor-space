package com.linor.singer.domain;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "album_uq_01", columnNames = {"singer_id", "title"})})
@NamedQueries({
	@NamedQuery(name="Album.findAlbumsBySinger",
			query="select a from Album a "
					+ "where a.singer.id = :singer_id"),
	@NamedQuery(name="Album.findByTitle",
			query="select a from Album a "
					+ "where a.title like :title||'%'")
})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Album{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 100)
	private String title;

	//@Column(name = "release_date")
	private LocalDate releaseDate;

	@ManyToOne
	@JoinColumn(name = "singer_id", foreignKey = @ForeignKey(name="album_fk_01"))
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Singer singer;

	@Version
	private int version;

}
