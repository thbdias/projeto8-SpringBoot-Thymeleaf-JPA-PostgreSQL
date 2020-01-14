package curso.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import curso.springboot.model.Pessoa;

@Repository
public interface PessoaRepository extends CrudRepository<Pessoa, Long> {
	@Query("select p from Pessoa p where p.nome like %?1%")
	List<Pessoa> findPessoByName(String nome);
	
	@Query("select p from Pessoa p where p.nome like %?1% and p.sexopessoa = ?2")
	List<Pessoa> findPessoByNameSexo(String nome, String sexopessoa);
}
