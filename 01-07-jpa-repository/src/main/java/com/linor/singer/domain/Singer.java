package com.linor.singer.domain;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import lombok.Data;
import lombok.Singular;

@Entity
@Table(name="singer", uniqueConstraints = {@UniqueConstraint(name = "singer_uq_01", columnNames = {"firstName", "lastName"})})
@NamedQueries({
	@NamedQuery(name="Singer.findById",
			query="select distinct s from Singer s " +
			"left join fetch s.albums a " +
			"left join fetch s.instruments i " +
			"where s.id = :id"),
	@NamedQuery(name="Singer.findAllWithAlbum",
			query="select distinct s from Singer s \n"
					+ "left join fetch s.albums a \n"
					+ "left join fetch s.instruments i"),
	@NamedQuery(name="Singer.findByFirstName",
	query="select distinct s from Singer s \n"
			+ "where s.firstName = :firstName")
})
@Data
public class Singer implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	//@Column(name="first_name")
	@Column(length = 60)
	private String firstName;
	
	@Column(length = 60)
	private String lastName;
	
	//@Column(name="birth_date")
	private LocalDate birthDate;
	
	@OneToMany(mappedBy="singer", cascade=CascadeType.ALL, orphanRemoval=true, fetch = FetchType.LAZY)
	//@ToString.Exclude
	//@EqualsAndHashCode.Exclude
	@Singular
	private Set<Album> albums = new HashSet<>();

	@ManyToMany
	@JoinTable(name="singer_instrument", 
		joinColumns=@JoinColumn(name="singer_id",foreignKey = @ForeignKey(name="fk_singer_instrument_fk_01")),
		inverseJoinColumns=@JoinColumn(name="instrument_id",foreignKey = @ForeignKey(name="fk_singer_instrument_fk_02")))
	//@ToString.Exclude
	//@EqualsAndHashCode.Exclude
	@Singular
	private Set<Instrument> instruments = new HashSet<>();
	
	@Version
	private int version;
	
	public boolean addAlbum(Album album) {
		album.setSinger(this);
		return getAlbums().add(album);
	}
	public void removeAlbum(Album album) {
		getAlbums().remove(album);
	}

	public boolean addInstrument(Instrument instrument) {
		return getInstruments().add(instrument);
	}
	public void removeInstrument(Instrument instrument) {
		getInstruments().remove(instrument);
	}
}
