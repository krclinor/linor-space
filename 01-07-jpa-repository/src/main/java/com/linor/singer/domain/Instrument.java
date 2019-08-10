package com.linor.singer.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;

@Entity
//@Table(name = "instrument")
@Data
public class Instrument implements Serializable{
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
