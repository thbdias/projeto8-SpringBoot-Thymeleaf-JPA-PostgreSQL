package curso.springboot.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import curso.springboot.repository.ProfissaoRepository;
import curso.springboot.repository.TelefoneRepository;

@Controller
public class PessoaController {
	
	@Autowired
	PessoaRepository pessoaRepository;
	
	@Autowired
	TelefoneRepository telefoneRepository; 
	
	@Autowired
	ReportUtil reportUtil;
	
	@Autowired
	ProfissaoRepository profissaoRepository;
	
	

	@RequestMapping(method = RequestMethod.GET, value = "/cadastroPessoa")
	public ModelAndView inicio() {
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastroPessoa");
		modelAndView.addObject("pessoaObj", new Pessoa());
		
		Iterable<Pessoa> pessoasIterable = pessoaRepository.findAll();
		modelAndView.addObject("pessoas", pessoasIterable);
		
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		
		return modelAndView;
	}	
	
	//   **/salvarpessoa => ignora o que está antes e intercepta salvarpessoa 
	// @Valid -> fazer validações que estao nos atributos da entidade Pessoa
	// BindingResult -> retorna as mensagens de validação
	@RequestMapping(method = RequestMethod.POST, value = "**/salvarpessoa")
	public ModelAndView salvar(@Valid Pessoa pessoa, BindingResult bindingResult) {
		
		pessoa.setTelefones(telefoneRepository.getTelefones(pessoa.getId()));
		
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
			modelAndViewErro.addObject("profissoes", profissaoRepository.findAll());
			return modelAndViewErro;
		}
		
		pessoaRepository.save(pessoa);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastroPessoa"); //mostre essa tela		
		Iterable<Pessoa> pessoasIterable = pessoaRepository.findAll();
		modelAndView.addObject("pessoas", pessoasIterable);
		modelAndView.addObject("pessoaObj", new Pessoa());
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		
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
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		
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
	public ModelAndView pesquisar(@RequestParam("nomePesquisa") String nomePesquisa, @RequestParam("pesquisaSexo") String pesquisaSexo) {
		
		List<Pessoa> pessasList = new ArrayList<Pessoa>();
		
		if (pesquisaSexo != null && !pesquisaSexo.isEmpty()) {
			pessasList = pessoaRepository.findPessoByNameSexo(nomePesquisa, pesquisaSexo);
		}
		else {
			pessasList = pessoaRepository.findPessoByName(nomePesquisa);
		}
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastroPessoa");
		modelAndView.addObject("pessoas", pessasList);
		modelAndView.addObject("pessoaObj", new Pessoa());
		
		return modelAndView;
	}
	
	@GetMapping("**/pesquisarpessoa")
	public void imprimirRelatorio(@RequestParam("nomePesquisa") String nomePesquisa, 
									@RequestParam("pesquisaSexo") String pesquisaSexo, 
									HttpServletRequest request,
									HttpServletResponse response) throws Exception {
		List<Pessoa> pessoas = new ArrayList<Pessoa>();
		
		//faz busca do filtro fornecido
		if (nomePesquisa != null && !nomePesquisa.isEmpty() && pesquisaSexo != null && !pesquisaSexo.isEmpty()) {
			pessoas = pessoaRepository.findPessoByNameSexo(nomePesquisa, pesquisaSexo);
		}
		else if (nomePesquisa != null && !nomePesquisa.isEmpty()) {
			pessoas = pessoaRepository.findPessoByName(nomePesquisa);
		}
		else if (pesquisaSexo != null && !pesquisaSexo.isEmpty()) {
			pessoas = pessoaRepository.findPessoBySexo(pesquisaSexo);
		}else {
			Iterable<Pessoa> iterator = pessoaRepository.findAll();
			for (Pessoa pessoa : iterator) {
				pessoas.add(pessoa);
			}
		}
		
		//chamar servico que faz geracao relatorio
		byte[] pdf = reportUtil.gerarRelatorio(pessoas, "pessoa", request.getServletContext());
		
		//tamanho da resposta
		response.setContentLength(pdf.length);
		
		//definir na resposta o tipo de arquivo
		response.setContentType("application/octet-stream");
		
		//cabeçalho da resposta
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", "relatorio.pdf");
		response.setHeader(headerKey, headerValue);
		
		//finaliza a resposta pro navegador
		response.getOutputStream().write(pdf);
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



