package com.eventoapp.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eventoapp.models.Convidado;
import com.eventoapp.models.Evento;
import com.eventoapp.repository.ConvidadoRepository;
import com.eventoapp.repository.EventoRepository;

@Controller
public class EventoController {

	@Autowired
	private EventoRepository er;

	@Autowired
	private ConvidadoRepository cr;

	@RequestMapping(value = "/cadastrarEvento", method = RequestMethod.GET)
	public String form() {
		return "evento/formEvento";
	}

	@RequestMapping(value = "/cadastrarEvento", method = RequestMethod.POST)
	public String form(@Valid Evento evento, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			attributes.addFlashAttribute("mensagem", "Verifique os campos!");
			return "redirect:/{id}";
		}
		er.save(evento);
		attributes.addFlashAttribute("mensagem", "Evento adicionado com Sucesso!");
		return "redirect:/cadastrarEvento";
	}

	@RequestMapping("/eventos")
	public ModelAndView listaEventos() {
		ModelAndView mv = new ModelAndView("index");
		Iterable<Evento> eventos = er.findAll();
		mv.addObject("eventos", eventos);
		return mv;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ModelAndView detalhesEvento(@PathVariable("id") int id) {
		ModelAndView mv = new ModelAndView("evento/detalhesEvento");
		Evento evento = er.findById(id);
		mv.addObject("evento", evento);
		Iterable<Convidado> convidados = cr.findByEvento(evento);
		mv.addObject("convidados", convidados);
		return mv;
	}

	@RequestMapping("/deletarEvento")
	public String deletarEvento(int id) {
		Evento evento = er.findById(id);
		er.delete(evento);
		return "redirect:/eventos";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public String detalhesEventoPost(@PathVariable("id") int id, @Valid Convidado convidado, BindingResult result,
			RedirectAttributes attributes) {
		if (result.hasErrors()) {
			attributes.addFlashAttribute("mensagem", "Verifique os campos!");
			return "redirect:/{id}";
		}
		Evento evento = er.findById(id);
		if(evento.getQtdVagas()<=0) {
			attributes.addFlashAttribute("mensagem", "O evento já está lotado!");
			return "redirect:/{id}";
		}
		
		convidado.setEvento(evento);
		er.save(evento);
		evento.setQtdVagas(evento.getQtdVagas()-1);
		cr.save(convidado);
		attributes.addFlashAttribute("mensagem", "Convidado adicionado com Sucesso!");
		return "redirect:/{id}";
	}

	@RequestMapping("/deletarConvidado")
	public String deletarConvidado(int id) {
		Convidado convidado = cr.findById(id);
		Evento evento = er.findById(convidado.getEvento().getId());
		cr.delete(convidado);
		return "redirect:/" + String.valueOf(evento.getId());
	}
	
	

}
