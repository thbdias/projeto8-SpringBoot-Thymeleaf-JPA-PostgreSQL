package curso.springboot.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import curso.springboot.model.Pessoa;

@Repository
public interface PessoaRepository extends CrudRepository<Pessoa, Long> {

}
