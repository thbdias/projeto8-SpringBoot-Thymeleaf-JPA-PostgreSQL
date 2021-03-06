package curso.springboot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.AntPathMatcher;

@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;
	
	

	@Override //configura as solicitaçoes de acesso por Http
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf()
		.disable() //desativa as configuracoes padrao de memoria
		.authorizeRequests() //permitir restringir acessos
		.antMatchers(HttpMethod.GET, "/").permitAll() //Qualquer usuario acessa a pagina inicial
		.antMatchers(HttpMethod.GET, "/cadastroPessoa").hasAnyRole("ADMIN") //so pode acessar essa pagina quem possui regra ADMIN
		.anyRequest().authenticated()
		.and().formLogin().permitAll() //permite qualquer usuario
		.loginPage("/login") //manda pra tela de login
		.defaultSuccessUrl("/cadastropessoa") //se logar manda pra essa tela
		.failureForwardUrl("/login?error=true") //se falhar o login
		.and().logout().logoutSuccessUrl("/login") //mapeia URL de Logout e invalida usuário autenticado
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout")); //ao passar url de logout encerra cessao
	}
	
	@Override //cria autenticacao do usuario com banco de dados ou em memoria
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.userDetailsService(implementacaoUserDetailsService)
		.passwordEncoder(new BCryptPasswordEncoder());
		
		/* //auth.inMemoryAuthentication().passwordEncoder(NoOpPasswordEncoder.getInstance())
		auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
		.withUser("admin")
		//.password("123")
		.password("$2a$10$4FwR2ejZNUQVO6VOQJiHz.gANug3ykY/pV1Gb/zF.3NGoey.aYZmy")
		.roles("ADMIN");*/
	}
	
	@Override //ignora URL especifica
	public void configure(WebSecurity web) throws Exception {
		//pertmite que tudo que tiver dentro dessa pasta possa ser acessado sem validacao
		//pq na tela de login usa-se materialize
		web.ignoring().antMatchers("/materialize/**"); 		
	}
	
}
