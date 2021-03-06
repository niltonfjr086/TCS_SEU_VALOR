package controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
//import javax.enterprise.context.RequestScoped;
//import javax.enterprise.context.SessionScoped;

import org.json.JSONException;
import org.json.JSONObject;

import component.JsonReader;
import model.dao.FiltroOfertaDAO;
import model.dao.NichoDAO;
import model.dao.RamoDAO;
import model.dao.TipoContatoDAO;
import model.dao.TipoOfertaDAO;
import model.dao.TipoPessoaDAO;
import model.dao.TipoUsuarioDAO;
import model.dao.UsuarioDAO;
import model.entity.Contato;
import model.entity.Endereco;
import model.entity.FiltroOferta;
import model.entity.Nicho;
import model.entity.PessoaFisica;
import model.entity.PessoaJuridica;
import model.entity.Ramo;
import model.entity.TipoContato;
import model.entity.TipoOferta;
import model.entity.TipoPessoa;
import model.entity.TipoUsuario;
import model.entity.Usuario;

@Named
@ViewScoped
// @RequestScoped
public class CadastroAcessoController implements Serializable {

	private static final long serialVersionUID = 2425783147349721021L;
	private LoginController loginController;

	private TipoContatoDAO tipoContatoDAO = new TipoContatoDAO();
	private List<TipoContato> tiposContato = new LinkedList<>();
	private List<SelectItem> itensTiposContato = new LinkedList<>();

	private TipoPessoaDAO tipoPessoaDAO = new TipoPessoaDAO();
	private List<TipoPessoa> tiposPessoa = new LinkedList<>();
	private List<SelectItem> itensTiposPessoa = new LinkedList<>();

	private List<TipoUsuario> tiposUsuario = new LinkedList<>();
	private TipoUsuarioDAO tipoUsuarioDAO = new TipoUsuarioDAO();
	private List<SelectItem> itensTiposUsuario = new LinkedList<>();

	private Boolean logado;
	private UsuarioDAO usuarioDAO;
	private Usuario usuario;
	private String confSenha;

	private String infoContato;
	private TipoContato tipoContatoSelecionado;
	private Long idTipoContatoSelecionado = 1L;
	private Contato contatoAdd = new Contato();

	private Long idTipoPessoaPreSelecionada;
	private TipoPessoa tipoPessoaSelecionada;

	private boolean ofertante = false;

	private FiltroOfertaDAO filtroOfertaDAO = new FiltroOfertaDAO();
	private FiltroOferta filtroOferta = new FiltroOferta();

	private TipoOfertaDAO tipoOfertaDAO = new TipoOfertaDAO();
	private List<TipoOferta> tiposOferta = new LinkedList<>();
	private List<SelectItem> itensTiposOferta = new LinkedList<>();
	private Long idTipoOfertaSelecionada;

	private RamoDAO ramoDAO = new RamoDAO();
	private List<Ramo> ramos = new LinkedList<>();
	private List<SelectItem> itemRamos = new LinkedList<>();
	private Ramo ramoSelecionado = new Ramo();
	private Long idRamoSelecionado;

	private NichoDAO nichoDAO = new NichoDAO();
	private List<Nicho> nichosVigentes = new LinkedList<>();
	private List<SelectItem> itensNichosVigentes = new LinkedList<>();
	private Long idNichoVigenteSelecionado;

	private String mensagemAviso = "";

	@Inject
	public CadastroAcessoController(LoginController loginController) {
		super();
		this.loginController = loginController;

		this.tiposContato = this.tipoContatoDAO.findAll();
		this.carregaSelectTiposContato();

		this.contatoAdd = new Contato();
		this.contatoAdd.setTipoContato(this.tiposContato.get(0));

		this.tiposPessoa = this.tipoPessoaDAO.findAll();
		this.carregaSelectTiposPessoa();

		this.tipoPessoaSelecionada = this.tiposPessoa.get(0);
		this.idTipoPessoaPreSelecionada = this.tipoPessoaSelecionada.getId();

		this.tiposUsuario = this.tipoUsuarioDAO.findAll();
		this.carregaSelectTiposUsuario();

		this.tiposOferta = this.tipoOfertaDAO.findAll();
		this.carregaSelectTiposOferta();

		this.ramos.clear();
		this.ramos.addAll(this.ramoDAO.findAll());
		this.carregaSelectRamos();

		verificarLogin(loginController);

	}

	private void verificarLogin(LoginController loginController) {

		this.usuarioDAO = new UsuarioDAO();

		if (loginController != null) {

			Usuario tmp = loginController.getUsuario();

			if (tmp != null && tmp.getId() != null) {
				this.logado = true;
				this.usuario = tmp;

				this.tipoPessoaSelecionada = this.usuario.getPessoa().getTipoPessoa();
				this.idTipoPessoaPreSelecionada = this.tipoPessoaSelecionada.getId();

				List<Contato> contatos = this.usuario.getPessoa().getContatos();
				List<Contato> contatosLinked = new LinkedList<>();

				for (Contato c : contatos) {
					contatosLinked.add(c);
				}

				this.usuario.getPessoa().getContatos().clear();
				this.usuario.getPessoa().getContatos().addAll(contatosLinked);

				if (this.usuario.getTipoUsuario().getNome().equalsIgnoreCase("Ofertante")) {
					this.ofertante = true;

					this.filtroOferta = this.filtroOfertaDAO.consultarFiltrosUsuario(this.usuario).get(0);
					this.idTipoOfertaSelecionada = this.filtroOferta.getTipo().getId();

					this.ramoSelecionado = this.filtroOferta.getNicho().getRamo();
					// this.nichosVigentes =
					// this.nichoDAO.procurarNichosPorRamo(this.ramoSelecionado);
					this.idRamoSelecionado = this.filtroOferta.getNicho().getRamo().getId();
					this.defineNichosVigentes();
					this.idNichoVigenteSelecionado = this.filtroOferta.getNicho().getId();
				}

			} else {
				this.logado = false;
				this.tipoPessoaSelecionada = this.getTiposPessoa().get(0);
				this.idTipoPessoaPreSelecionada = this.tipoPessoaSelecionada.getId();
				this.usuario = new Usuario();
				this.usuario.setTipoUsuario(this.tiposUsuario.get(1));
				this.usuario.setPessoa(new PessoaFisica());
				this.usuario.getPessoa().setTipoPessoa(this.tipoPessoaSelecionada);

				this.usuario.getPessoa().setDocumento("");

			}

			if (this.usuario.getPessoa().getEndereco() == null) {
				this.usuario.getPessoa().setEndereco(new Endereco());
				this.usuario.getPessoa().getEndereco().setCep("");
			}

			JSONObject json;
			try {
				json = JsonReader.readJsonFromUrl("https://api.myip.com");
				System.out.println("SHOW IP: " + json.get("ip").toString());
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	private void carregaSelectTiposUsuario() {
		for (TipoUsuario item : this.tiposUsuario) {
			this.itensTiposUsuario.add(new SelectItem(item.getId(), item.getNome()));
		}
	}

	private void carregaSelectTiposOferta() {
		for (TipoOferta item : this.tiposOferta) {
			this.itensTiposOferta.add(new SelectItem(item.getId(), item.getNome()));
		}
	}

	private void carregaSelectRamos() {
		for (Ramo r : this.ramos) {
			this.itemRamos.add(new SelectItem(r.getId(), r.getNome()));
		}
	}

	private void carregaSelectTiposPessoa() {
		itensTiposPessoa.clear();
		for (TipoPessoa tp : this.tiposPessoa) {
			itensTiposPessoa.add(new SelectItem(tp.getId(), tp.getNome()));
		}
	}

	public void carregaSelecNichosVigentes() {
		this.itensNichosVigentes.clear();
		for (Nicho item : this.nichosVigentes) {
			this.itensNichosVigentes.add(new SelectItem(item.getId(), item.getNome()));
		}
	}

	public void carregaSelectTiposContato() {
		for (TipoContato tc : this.tiposContato) {
			this.itensTiposContato.add(new SelectItem(tc.getId(), tc.getNome()));
		}
	}

	public void defineDoc() {
		String nome, login, senha;
		nome = this.usuario.getPessoa().getNome();
		login = this.usuario.getLogin();
		senha = this.usuario.getSenha();
		Endereco e = this.usuario.getPessoa().getEndereco();
		List<Contato> contatos = this.usuario.getPessoa().getContatos();

		this.tipoPessoaSelecionada = this.tipoPessoaDAO.findById(this.idTipoPessoaPreSelecionada);
		if (this.tipoPessoaSelecionada.getNome().equalsIgnoreCase("Física")) {

			this.usuario.setPessoa(new PessoaFisica());
			this.usuario.getPessoa().setTipoPessoa(this.tipoPessoaSelecionada);

		} else {

			this.usuario.setPessoa(new PessoaJuridica());
			this.usuario.getPessoa().setTipoPessoa(this.tipoPessoaSelecionada);
		}
		this.usuario.getPessoa().setNome(nome);
		this.usuario.setLogin(login);
		this.usuario.setSenha(senha);
		this.usuario.getPessoa().setEndereco(e);
		this.usuario.getPessoa().setContatos(contatos);
	}

	public void defineTipoOferta() {
		this.filtroOferta.setTipo(this.tipoOfertaDAO.findById(this.idTipoOfertaSelecionada));
	}

	public void defineNichosVigentes() {
		System.out.println(this.idRamoSelecionado);
		this.ramoSelecionado = this.ramoDAO.findById(this.idRamoSelecionado);

		List<Nicho> nichos = this.nichoDAO.procurarNichosPorRamo(this.ramoSelecionado);
		if (nichos != null) {
			this.nichosVigentes.clear();
			this.nichosVigentes.addAll(nichos);
		}
		this.carregaSelecNichosVigentes();
	}

	public void defineNichoOferta() {
		this.filtroOferta.setNicho(this.nichoDAO.findById(this.idNichoVigenteSelecionado));
	}

	public void defineTipoContato() {
		this.tipoContatoSelecionado = this.tipoContatoDAO.findById(this.idTipoContatoSelecionado);
	}

	public void adicionaContato() {

		if (this.contatoAdd.getInformacao() != null && this.contatoAdd.getInformacao().length() > 5) {

			Contato contato = new Contato();

			contato.setTipoContato(this.contatoAdd.getTipoContato());
			contato.setInformacao(this.contatoAdd.getInformacao());
			contato.setTipoContato(this.contatoAdd.getTipoContato());

			this.usuario.getPessoa().getContatos().add(contato);

			this.contatoAdd = new Contato();
			this.contatoAdd.setTipoContato(this.tiposContato.get(0));

		}

	}

	private Boolean formularioValido() {

		if (this.usuario != null && this.usuario.getLogin() != null && this.usuario.getLogin().length() > 3
				&& this.usuario.getSenha() != null && this.usuario.getSenha().length() > 3 && this.confSenha != null

				&& this.usuario.getPessoa().getEndereco().getCep() != null
				&& this.usuario.getPessoa().getEndereco().getCep().length() >= 8
				&& this.usuario.getPessoa().getEndereco().getPais() != null
				&& this.usuario.getPessoa().getEndereco().getPais().length() > 1
				&& this.usuario.getPessoa().getEndereco().getEstado() != null
				&& this.usuario.getPessoa().getEndereco().getEstado().length() > 1
				&& this.usuario.getPessoa().getEndereco().getMunicipio() != null
				&& this.usuario.getPessoa().getEndereco().getMunicipio().length() > 1
				&& this.usuario.getPessoa().getEndereco().getNumero() != null

		) {

			Usuario usuarioResgatado = null;
			if (this.usuario != null && this.usuario.getId() != null) {
				usuarioResgatado = this.usuarioDAO.findById(this.usuario.getId());
			}

			if (usuarioResgatado != null) {
				if (!usuarioResgatado.getSenha().equals(this.usuario.getSenha())) {
					this.mensagemAviso = "Favor confirme sua senha.";
					return false;
				}
			} else {
				if (!this.usuario.getSenha().equals(this.confSenha)) {
					this.mensagemAviso = "Favor confirme sua senha.";
					return false;
				}
			}

			if (this.ofertante) {
				if (this.filtroOferta == null || this.filtroOferta.getNicho() == null
						|| this.filtroOferta.getTipo() == null) {

					this.mensagemAviso = "Com a opção de Prestador você precisa preencher as 3 informações de filtro. Obrigado.";
					return false;
				}
			}

			this.mensagemAviso = "";
			return true;

		} else

		{
			this.mensagemAviso = "Favor preencha todos os campos. Obrigado.";
			return false;

		}

	}

	public void adicionaUsuario() {

		if (this.formularioValido()) {

			if (this.ofertante) {
				this.usuario.setTipoUsuario(this.tiposUsuario.get(2));
			} else {
				this.usuario.setTipoUsuario(this.tiposUsuario.get(1));
			}

			List<Contato> contatos = this.usuario.getPessoa().getContatos();

			if (this.usuario != null) {

				if (this.usuario.getPessoa() != null) {

					for (Contato contato : contatos) {
						contato.setPessoa(this.usuario.getPessoa());
					}
				}

				if (this.usuario.getId() == null) {
					this.usuario = this.usuarioDAO.insert(this.usuario);
				} else {
					this.usuario = this.usuarioDAO.save(this.usuario);
				}
			}

			/**
			 * USUÁRIOS NÃO OFERTANTES NÃO POSSUEM FiltroOferta - Para evitar poluir a
			 * classe Usuario onde somente o tipo Ofertante teria esse atributo foi
			 * realizada a lógica no controller
			 */
			if (this.usuario.getTipoUsuario().getNome().equalsIgnoreCase("Ofertante") && this.usuario.getId() != null) {
				this.filtroOferta.setOfertante(this.usuario);

				if (this.filtroOferta.getId() == null) {
					this.filtroOferta = this.filtroOfertaDAO.insert(this.filtroOferta);

				} else {
					this.filtroOferta = this.filtroOfertaDAO.save(this.filtroOferta);
				}

			}

			this.loginController.setUsuario(this.usuario);

			this.recarregar();
		}

	}

	public void carregaEndereco() {

		if (this.usuario.getPessoa().getEndereco().getCep() != null
				&& this.usuario.getPessoa().getEndereco().getCep().length() == 9) {
			JSONObject json;
			try {

				json = JsonReader.readJsonFromUrl(
						"https://api.postmon.com.br/v1/cep/" + this.usuario.getPessoa().getEndereco().getCep());
				// System.out.println(json.toString());

				this.usuario.getPessoa().getEndereco().setPais("Brasil");

				this.usuario.getPessoa().getEndereco().setEstado(json.get("estado").toString());
				this.usuario.getPessoa().getEndereco().setMunicipio(json.get("cidade").toString());
				this.usuario.getPessoa().getEndereco().setLogradouro(json.get("logradouro").toString());
				this.usuario.getPessoa().getEndereco().setBairro(json.get("bairro").toString());

			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void apagarContato(int index) {
		this.usuario.getPessoa().getContatos().remove(index);

	}

	public void recarregar() {
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("landing_page.xhtml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// GETTERS E SETTER PARA A VIEW

	public List<TipoPessoa> getTiposPessoa() {
		return tiposPessoa;
	}

	public void setTiposPessoa(List<TipoPessoa> tiposPessoa) {
		this.tiposPessoa = tiposPessoa;
	}

	public List<SelectItem> getItensTiposPessoa() {
		return itensTiposPessoa;
	}

	public void setItensTiposPessoa(List<SelectItem> itensTiposPessoa) {
		this.itensTiposPessoa = itensTiposPessoa;
	}

	public List<TipoContato> getTiposContato() {
		return tiposContato;
	}

	public void setTiposContato(List<TipoContato> tiposContato) {
		this.tiposContato = tiposContato;
	}

	public List<SelectItem> getItensTiposContato() {
		return itensTiposContato;
	}

	public void setItensTiposContato(List<SelectItem> itensTiposContato) {
		this.itensTiposContato = itensTiposContato;
	}

	public Boolean getLogado() {
		return logado;
	}

	public void setLogado(Boolean logado) {
		this.logado = logado;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getConfSenha() {
		return confSenha;
	}

	public void setConfSenha(String confSenha) {
		this.confSenha = confSenha;
	}

	public String getInfoContato() {
		return infoContato;
	}

	public void setInfoContato(String infoContato) {
		this.infoContato = infoContato;
	}

	public Contato getContatoAdd() {
		return contatoAdd;
	}

	public void setContatoAdd(Contato contatoAdd) {
		this.contatoAdd = contatoAdd;
	}

	public TipoContato getTipoContatoSelecionado() {
		return tipoContatoSelecionado;
	}

	public void setTipoContatoSelecionado(TipoContato tipoContatoSelecionado) {
		this.tipoContatoSelecionado = tipoContatoSelecionado;
	}

	public Long getIdTipoContatoSelecionado() {
		return idTipoContatoSelecionado;
	}

	public void setIdTipoContatoSelecionado(Long idTipoContatoSelecionado) {
		this.idTipoContatoSelecionado = idTipoContatoSelecionado;
	}

	public TipoPessoa getTipoPessoaSelecionada() {
		return tipoPessoaSelecionada;
	}

	public void setTipoPessoaSelecionada(TipoPessoa tipoPessoaSelecionada) {
		this.tipoPessoaSelecionada = tipoPessoaSelecionada;
	}

	public Long getIdTipoPessoaPreSelecionada() {
		return idTipoPessoaPreSelecionada;
	}

	public void setIdTipoPessoaPreSelecionada(Long idTipoPessoaPreSelecionada) {
		this.idTipoPessoaPreSelecionada = idTipoPessoaPreSelecionada;
	}

	public boolean getOfertante() {
		return ofertante;
	}

	public void setOfertante(boolean ofertante) {
		this.ofertante = ofertante;
	}

	public FiltroOferta getFiltroOferta() {
		return filtroOferta;
	}

	public void setFiltroOferta(FiltroOferta filtroOferta) {
		this.filtroOferta = filtroOferta;
	}

	public List<TipoOferta> getTiposOferta() {
		return tiposOferta;
	}

	public void setTiposOferta(List<TipoOferta> tiposOferta) {
		this.tiposOferta = tiposOferta;
	}

	public List<SelectItem> getItensTiposOferta() {
		return itensTiposOferta;
	}

	public void setItensTiposOferta(List<SelectItem> itensTiposOferta) {
		this.itensTiposOferta = itensTiposOferta;
	}

	public Long getIdTipoOfertaSelecionada() {
		return idTipoOfertaSelecionada;
	}

	public void setIdTipoOfertaSelecionada(Long idTipoOfertaSelecionada) {
		this.idTipoOfertaSelecionada = idTipoOfertaSelecionada;
	}

	public List<Ramo> getRamos() {
		return ramos;
	}

	public void setRamos(List<Ramo> ramos) {
		this.ramos = ramos;
	}

	public List<SelectItem> getItemRamos() {
		return itemRamos;
	}

	public void setItemRamos(List<SelectItem> itemRamos) {
		this.itemRamos = itemRamos;
	}

	public Ramo getRamoSelecionado() {
		return ramoSelecionado;
	}

	public void setRamoSelecionado(Ramo ramoSelecionado) {
		this.ramoSelecionado = ramoSelecionado;
	}

	public Long getIdRamoSelecionado() {
		return idRamoSelecionado;
	}

	public void setIdRamoSelecionado(Long idRamoSelecionado) {
		this.idRamoSelecionado = idRamoSelecionado;
	}

	public List<Nicho> getNichosVigentes() {
		return nichosVigentes;
	}

	public void setNichosVigentes(List<Nicho> nichosVigentes) {
		this.nichosVigentes = nichosVigentes;
	}

	public List<SelectItem> getItensNichosVigentes() {
		return itensNichosVigentes;
	}

	public void setItensNichosVigentes(List<SelectItem> itensNichosVigentes) {
		this.itensNichosVigentes = itensNichosVigentes;
	}

	public Long getIdNichoVigenteSelecionado() {
		return idNichoVigenteSelecionado;
	}

	public void setIdNichoVigenteSelecionado(Long idNichoVigenteSelecionado) {
		this.idNichoVigenteSelecionado = idNichoVigenteSelecionado;
	}

	public String getMensagemAviso() {
		return mensagemAviso;
	}

	public void setMensagemAviso(String mensagemAviso) {
		this.mensagemAviso = mensagemAviso;
	}

}
