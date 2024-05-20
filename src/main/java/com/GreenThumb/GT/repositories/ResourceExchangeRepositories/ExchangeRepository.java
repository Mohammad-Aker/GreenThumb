package com.GreenThumb.GT.repositories.ResourceExchangeRepositories;

import com.GreenThumb.GT.models.ResourceExchange.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Long> {

}
