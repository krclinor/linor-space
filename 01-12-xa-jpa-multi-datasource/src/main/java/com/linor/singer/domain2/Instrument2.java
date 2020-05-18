package com.linor.singer.domain2;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "instrument")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Instrument2{
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
