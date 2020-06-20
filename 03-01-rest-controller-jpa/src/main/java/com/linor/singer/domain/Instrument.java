package com.linor.singer.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
//@Table(name = "instrument")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Instrument{
	@Id
	//@Column(name = "instrument_id")
	private String instrumentId;

//	@ManyToMany
//	@JoinTable(name = "singer_instrument", joinColumns = @JoinColumn(name = "instrument_id"), inverseJoinColumns = @JoinColumn(name = "singer_id"))
//	@ToString.Exclude
//	@EqualsAndHashCode.Exclude
//	@Singular
//	private Set<Singer> singers = new HashSet<>();
//	
//	public boolean addSinger(Singer singer) {
//		return getSingers().add(singer);
//	}
//	public void removeSinger(Singer singer) {
//		getSingers().remove(singer);
//	}
}
