package br.com.unasp.faculdade.repository;

import br.com.unasp.faculdade.entity.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Integer> {
    Optional<Professor> findByEmail(String email);
    boolean existsByEmail(String email);
}
