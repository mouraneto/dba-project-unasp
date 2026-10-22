package br.com.unasp.faculdade.repository;

import br.com.unasp.faculdade.entity.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Integer> {
    Optional<Aluno> findByEmail(String email);
    boolean existsByEmail(String email);
}
