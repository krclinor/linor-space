package com.linor.singer.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.linor.singer.domain.Instrument;

public interface InstrumentRepository extends JpaRepository<Instrument, String> {

}
