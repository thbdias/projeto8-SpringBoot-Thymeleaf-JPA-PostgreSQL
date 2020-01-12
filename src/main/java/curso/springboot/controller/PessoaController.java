package curso.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import curso.springboot.model.Pessoa;
import curso.springboot.repository.PessoaRepository;

@Controller
public class PessoaController {
	
	@Autowired
	PessoaRepository pessoaRepository;

	@RequestMapping(method = RequestMethod.GET, value = "/cadastroPessoa")
	public String inicio() {
		return "cadastro/cadastroPessoa";
	}	
	
	@RequestMapping(method = RequestMethod.POST, value = "/salvarpessoa")
	public ModelAndView salvar(Pessoa pessoa) {
		pessoaRepository.save(pessoa);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastroPessoa"); //mostre essa tela		
		Iterable<Pessoa> pessoasIterable = pessoaRepository.findAll();
		modelAndView.addObject("pessoas", pessoasIterable);
		
		return modelAndView;
	}
	
	@RequestMapping(method =  RequestMethod.GET, value = "/listapessoas")
	public ModelAndView ListarPessoas() {
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastroPessoa"); //mostre essa tela		
		Iterable<Pessoa> pessoasIterable = pessoaRepository.findAll();
		modelAndView.addObject("pessoas", pessoasIterable);
		
		return modelAndView;
	}
	
}



