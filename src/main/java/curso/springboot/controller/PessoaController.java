package curso.springboot.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import curso.springboot.model.Pessoa;
import curso.springboot.model.Telefone;
import curso.springboot.repository.PessoaRepository;
import curso.springboot.repository.TelefoneRepository;

@Controller
public class PessoaController {
	
	@Autowired
	PessoaRepository pessoaRepository;
	
	@Autowired
	TelefoneRepository telefoneRepository; 

	@RequestMapping(method = RequestMethod.GET, value = "/cadastroPessoa")
	public ModelAndView inicio() {
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastroPessoa");
		modelAndView.addObject("pessoaObj", new Pessoa());
		
		Iterable<Pessoa> pessoasIterable = pessoaRepository.findAll();
		modelAndView.addObject("pessoas", pessoasIterable);
		
		return modelAndView;
	}	
	
	//   **/salvarpessoa => ignora o que está antes e intercepta salvarpessoa 
	// @Valid -> fazer validações que estao nos atributos da entidade Pessoa
	// BindingResult -> retorna as mensagens de validação
	@RequestMapping(method = RequestMethod.POST, value = "**/salvarpessoa")
	public ModelAndView salvar(@Valid Pessoa pessoa, BindingResult bindingResult) {
		
		if (bindingResult.hasErrors()) {
			ModelAndView modelAndViewErro = new ModelAndView("cadastro/cadastroPessoa"); //mostre essa tela
			Iterable<Pessoa> pessoasIterable = pessoaRepository.findAll();
			modelAndViewErro.addObject("pessoas", pessoasIterable);
			modelAndViewErro.addObject("pessoaObj", pessoa); //dados já preenchidos de volta a tela
			
			List<String> msg = new ArrayList<String>();
			
			for (ObjectError objectError : bindingResult.getAllErrors()) {
				msg.add(objectError.getDefaultMessage()); //getDefaultMessage -> vem das validacoes dos att das entidades
			}
			
			modelAndViewErro.addObject("msg", msg);
			return modelAndViewErro;
		}
		
		pessoaRepository.save(pessoa);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastroPessoa"); //mostre essa tela		
		Iterable<Pessoa> pessoasIterable = pessoaRepository.findAll();
		modelAndView.addObject("pessoas", pessoasIterable);
		modelAndView.addObject("pessoaObj", new Pessoa());
		
		return modelAndView;
	}
	
	@RequestMapping(method =  RequestMethod.GET, value = "/listapessoas")
	public ModelAndView ListarPessoas() {
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastroPessoa"); //mostre essa tela		
		Iterable<Pessoa> pessoasIterable = pessoaRepository.findAll();
		modelAndView.addObject("pessoas", pessoasIterable);
		modelAndView.addObject("pessoaObj", new Pessoa());
		
		return modelAndView;
	}
	
	@GetMapping("/editarpessoa/{idpessoa}")
	public ModelAndView editarpessoa(@PathVariable("idpessoa") Long idpessoa) {
		
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastroPessoa");
		modelAndView.addObject("pessoaObj", pessoa.get());
		
		return modelAndView;
	}
	
	@GetMapping("/removerpessoa/{idpessoa}")
	public ModelAndView excluirpessoa(@PathVariable("idpessoa") Long idpessoa) {		
		
		pessoaRepository.deleteById(idpessoa);
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastroPessoa");
		modelAndView.addObject("pessoas", pessoaRepository.findAll());
		modelAndView.addObject("pessoaObj", new Pessoa());
		
		return modelAndView;
	}
	
	@PostMapping("**/pesquisarpessoa")
	public ModelAndView pesquisar(@RequestParam("nomePesquisa") String nomePesquisa) {
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastroPessoa");
		modelAndView.addObject("pessoas", pessoaRepository.findPessoByName(nomePesquisa));
		modelAndView.addObject("pessoaObj", new Pessoa());
		
		return modelAndView;
	}
	
	@GetMapping("/telefones/{idpessoa}")
	public ModelAndView telefones(@PathVariable("idpessoa") Long idpessoa) {
		
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		modelAndView.addObject("pessoaObj", pessoa.get());
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(idpessoa));
		
		return modelAndView;
	}
	
	@PostMapping("**/addfonepessoa/{pessoaid}")
	public ModelAndView addFonePessoa(Telefone telefone, @PathVariable("pessoaid") Long pessoaid) {
		
		Pessoa pessoa = pessoaRepository.findById(pessoaid).get();
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		modelAndView.addObject("pessoaObj", pessoa);
		
		if (telefone == null || telefone.getNumero() == null || telefone.getNumero().isEmpty() 
				|| telefone.getTipo() == null || telefone.getTipo().isEmpty()) {			
			
			modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));
			
			List<String> msg = new ArrayList<String>();
			msg.add("Número e Tipo são obrigatórios");
			
			modelAndView.addObject("msg", msg);
			
			return modelAndView;
		}
		
		telefone.setPessoa(pessoa);
		telefoneRepository.save(telefone);				
		
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));
		return modelAndView;
	}
	
	@GetMapping("/removertelefone/{idtelefone}")
	public ModelAndView excluirtelefone(@PathVariable("idtelefone") Long idtelefone) {		
		
		Pessoa pessoa = telefoneRepository.findById(idtelefone).get().getPessoa();
		
		telefoneRepository.deleteById(idtelefone);
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		modelAndView.addObject("pessoaObj", pessoa);
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoa.getId()));
		
		return modelAndView;
	}
	
}



