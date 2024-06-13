package com.GreenThumb.GT.repositories.MaterialExchangeRepositories;

import com.GreenThumb.GT.models.MaterialExchange.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Long> {

}
