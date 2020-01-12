package curso.springboot.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.AntPathMatcher;

@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {

	@Override //configura as solicitaçoes de acesso por Http
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf()
		.disable() //desativa as configuracoes padrao de memoria
		.authorizeRequests() //permitir restringir acessos
		.antMatchers(HttpMethod.GET, "/").permitAll() //Qualquer usuario acessa a pagina inicial
		.anyRequest().authenticated()
		.and().formLogin().permitAll() //permite qualquer usuario
		.and().logout() //mapeia URL de Logout e invalida usuário autenticado
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout")); //ao passar url de logout encerra cessao
	}
	
	@Override //cria autenticacao do usuario com banco de dados ou em memoria
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().passwordEncoder(NoOpPasswordEncoder.getInstance())
		.withUser("admin")
		.password("123")
		.roles("ADMIN");
	}
	
	@Override //ignora URL especifica
	public void configure(WebSecurity web) throws Exception {
		//pertmite que tudo que tiver dentro dessa pasta possa ser acessado sem validacao
		//pq na tela de login usa-se materialize
		web.ignoring().antMatchers("/materialize/**"); 		
	}
	
}
