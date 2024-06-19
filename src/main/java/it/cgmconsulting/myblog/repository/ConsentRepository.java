package it.cgmconsulting.myblog.repository;

import it.cgmconsulting.myblog.entity.Consent;
import it.cgmconsulting.myblog.entity.ConsentId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsentRepository extends JpaRepository<Consent, ConsentId> {

    List<Consent> findBySendNewsletterTrue();
}
