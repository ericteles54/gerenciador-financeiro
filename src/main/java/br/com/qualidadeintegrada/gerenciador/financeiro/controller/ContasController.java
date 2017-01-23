package br.com.qualidadeintegrada.gerenciador.financeiro.controller;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import br.com.qualidadeintegrada.gerenciador.financeiro.model.AnoMes;
import br.com.qualidadeintegrada.gerenciador.financeiro.model.Conta;
import br.com.qualidadeintegrada.gerenciador.financeiro.model.ContaInfoOnline;
import br.com.qualidadeintegrada.gerenciador.financeiro.model.TipoTransacao;
import br.com.qualidadeintegrada.gerenciador.financeiro.model.Transacao;
import br.com.qualidadeintegrada.gerenciador.financeiro.model.Usuario;
import br.com.qualidadeintegrada.gerenciador.financeiro.services.ContaService;
import br.com.qualidadeintegrada.gerenciador.financeiro.services.TransacaoService;
import br.com.qualidadeintegrada.gerenciador.financeiro.services.UsuarioService;

@Controller
@RequestMapping("/contas")
public class ContasController {
	
	@Autowired
	private ContaService contaService;
	
	@Autowired
	private TransacaoService transacaoService;
			
	@Autowired
	private UsuarioService usuarioService;

	@RequestMapping
	@ResponseBody
	public ModelAndView lista() {
		
		Usuario usuarioTmp = this.usuarioService.getUsuarioLogado();
		
		String olaUsuario = "Olá " + usuarioTmp.getUsername() + "!";
		
		List<Conta> contasUsuario = new ArrayList<Conta>();
		contasUsuario = this.contaService.buscaContasPorUsuario(usuarioTmp);
		//contasUsuario = this.contaService.atualizaSaldoContas(contasUsuario);
		
		ModelAndView mv = new ModelAndView("ListaContas");
		mv.addObject("olaUsuario", olaUsuario);
		mv.addObject("contas", contasUsuario);
		mv.addObject(new Conta());
		
		return mv;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String salva(Conta conta) {
						
		// Associa usuário (username) à Conta
		Usuario usuarioTmp = this.usuarioService.getUsuarioLogado();
		conta.setUsuario(usuarioTmp);
		
		if(conta.getSaldo() == null) {
			conta.setSaldo(BigDecimal.ZERO);
		}
				
		this.contaService.salva(conta);
		
		return "redirect:/contas";
	}
	
	@RequestMapping(value = "/deleta", method = RequestMethod.POST)
	public String deleta(@RequestParam("id")String id) {
		
		this.contaService.deleta(Long.parseLong(id));
		
		return "redirect:/contas";
	}
	
	@RequestMapping(value = "/infoContasUsuario", method = RequestMethod.GET)
	@ResponseBody	
	public ModelAndView retornaTransacoesMesAno(HttpServletRequest request) {	
		
		AnoMes anoMes = new AnoMes();
		
		String mesAnoString = request.getParameter("mesAnoString");		
		
		String[] mesAnoArray = mesAnoString.split(Pattern.quote(","));
		anoMes.setMes(Integer.parseInt(mesAnoArray[0]));
		anoMes.setAno(Integer.parseInt(mesAnoArray[1]));
				
		// Recebe usuário logado
		Usuario usuarioTmp = this.usuarioService.getUsuarioLogado();		
						
		// Busca todas as contas do usuário logado
		List<Conta> contasUsuario = new ArrayList<Conta>();
		contasUsuario = this.contaService.buscaContasPorUsuario(usuarioTmp);
						
		// Calcula informações de contas do usuário
		List<Transacao> transacoesContaPorMes;
		ContaInfoOnline contaInfoOnline;
		List<ContaInfoOnline> contasInfoOnline = new ArrayList<ContaInfoOnline>();
		for(Conta conta : contasUsuario) {	
			
			contaInfoOnline = new ContaInfoOnline();
			transacoesContaPorMes = new ArrayList<Transacao>();
								
			// Busca Transacoes relacionadas a conta no mes selecionado
			transacoesContaPorMes.addAll(this.transacaoService.buscaTransacoesPorMesAnoConta(anoMes.getMes(), anoMes.getAno(), conta));
			
			
			// Efetua calculos e coloca no objeto ContaInfoOnline
			BigDecimal despesa = new BigDecimal(0);
			BigDecimal receita = new BigDecimal(0);			
			BigDecimal saldo = new BigDecimal(0);
			for(Transacao transacao : transacoesContaPorMes) {
				if(transacao.getTipoTransacao().equals(TipoTransacao.DESPESA)) {
					safafqeafqae
				}
			}
			contaInfoOnline.setConta(conta);
		}
		
		
		Locale localeBR = new Locale("pt", "BR");
		DateFormat fmtMesNome = new SimpleDateFormat("MMMM yyyy", localeBR);
		String mesAnoSelecionado = fmtMesNome.format(transacoesUsuarioPorMes.get(0).getData());
		
		
		ModelAndView mv = new ModelAndView("InformacoesContas");
		mv.addObject("transacoes", transacoesUsuarioPorMes);
		mv.addObject("mesAnoSelecionado", mesAnoSelecionado);
		return mv;
	}
	
}
