package br.com.mvv.aqceacha.resource;

import br.com.mvv.aqceacha.model.*;
import br.com.mvv.aqceacha.repository.*;
import br.com.mvv.aqceacha.repository.filter.VendedorFilter;
import br.com.mvv.aqceacha.repository.projections.VendedorDto;
import br.com.mvv.aqceacha.repository.projections.VendedorRegistroDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/vendedor")
public class VendedorResource {

  @Autowired
  private VendedorRepository vendedorRepository;

  @Autowired
  private ServicoRepository servicoRepository;

  @Autowired
  private ImagensRepository imagensRepository;

  @Autowired
  private FavoritoClienteRepository favoritoClienteRepository;

  @Autowired
  private FavoritoRepository favoritoRepository;

  @Autowired
  private ClienteRepository clienteRepository;

  @Autowired
  private CidadeRepository cidadeRepository;

  @Autowired
  private RamoAtvRepository ramoAtvRepository;

  @GetMapping()
  public Page<VendedorDto> pesquisar(VendedorFilter vendedorFilter, Pageable pageable){
    return vendedorRepository.filtrar(vendedorFilter, pageable);
  }

  @CrossOrigin("*")
  @GetMapping("/{id}")
  public VendedorDto getById(@PathVariable Long id) {
    Optional<Vendedor> vendedorOptional = vendedorRepository.findById(id);
    if (vendedorOptional.isPresent()) {
      Vendedor vendedor = vendedorOptional.get();

      List<ServicoVendedor> servicoVendedor = vendedor.getServicosVendedor();

      List<Servico> servicos = servicoVendedor.stream().map(
              item -> servicoRepository.findById(item.getServico().getIdserv()).get()
      ).collect(Collectors.toList());

      List<ImagensVendedor> imagensVendedor = vendedor.getImagensVendedor();

      List<Imagens> imagens = imagensVendedor.stream().map(
              item -> imagensRepository.findById(item.getImagens().getIdimg()).get()
      ).collect(Collectors.toList());

      VendedorDto vendedorDto = new VendedorDto(
              vendedor.getIdven(),
              vendedor.getNomeven(),
              vendedor.getRamoatv().getRamo(),
              vendedor.getCidade().getNomecidade(),
              vendedor.getCidade().getUf(),
              vendedor.getStar(),
              servicos,
              vendedor.getImgven(),
              vendedor.getApelidoven(),
              vendedor.getEmailven(),
              vendedor.getTelefoneven(),
              imagens
      );

      return vendedorDto;
    }
    return null;
  }

  @PostMapping("/favorito/{idven}")
  public void adicionarFavorito(@PathVariable Long idven){
    Optional<Cliente> clienteOptional = clienteRepository.findById(1L);
    if (clienteOptional.isPresent()) {
      Cliente cliente = clienteOptional.get();
      List<FavoritoCliente> favoritos = cliente.getFavoritoCliente();
      Vendedor vendedor = vendedorRepository.findById(idven).get();
      Favorito favorito = new Favorito();
      favorito.setVendedor(vendedor);
      favorito = favoritoRepository.save(favorito);
      FavoritoCliente favoritoCliente = new FavoritoCliente();
      favoritoCliente.setFavorito(favorito);
      favoritoCliente.setCliente(cliente);
      favoritoClienteRepository.save(favoritoCliente);
    } else {
      return;
    }
  }

  @GetMapping("/favorito/existe/{idven}")
  public boolean verificarFavorito(@PathVariable Long idven){
    Cliente cliente = clienteRepository.findById(1L).get();
    Vendedor vendedor = vendedorRepository.findById(idven).get();
    List<FavoritoCliente> favoritos = cliente.getFavoritoCliente().stream().filter(
            favoritoCliente -> favoritoCliente.getFavorito().getVendedor().equals(vendedor)
    ).collect(Collectors.toList());
    if (favoritos.stream().count() >= 1) {
      return true;
    }
    return false;
  }

  @PostMapping("/favorito/remover/{idven}")
  public void removerFavorito(@PathVariable Long idven) {
    Cliente cliente = clienteRepository.findById(1L).get();
    Vendedor vendedor = vendedorRepository.findById(idven).get();
    List<FavoritoCliente> favoritos = cliente.getFavoritoCliente();
    favoritos.forEach(
            favoritocliente -> {
              Favorito favorito = favoritocliente.getFavorito();
              if (favorito.getVendedor().equals(vendedor)) {
                favoritoClienteRepository.delete(favoritocliente);
                favoritoRepository.delete(favorito);
              }
            }
    );
  }

  @GetMapping("/favorito/get/{idfav}")
  public Vendedor getPorIdFavorito(@PathVariable Long idfav) {
    return vendedorRepository.findByFavoritoIdfav(idfav);
  }

  @CrossOrigin("*")
  @GetMapping("/todos")
  public List<Vendedor> listarTodosVendedor() {return vendedorRepository.findAll();}

  @CrossOrigin("*")
  @PostMapping("/criar")
  public Vendedor criarVendedor(@RequestBody VendedorRegistroDto vendedorRegistroDto){

    System.out.println("print teste");

    Cidade cidade = cidadeRepository.findById(vendedorRegistroDto.getIdcidade()).get();
    RamoAtv ramoAtv = ramoAtvRepository.findById(vendedorRegistroDto.getIdramo()).get();

    Vendedor vendedor = new Vendedor();

    vendedor.setNomeven(vendedorRegistroDto.getNomeven());
    vendedor.setEmailven(vendedorRegistroDto.getEmailven());
    vendedor.setSenhaven(vendedorRegistroDto.getSenhaven());
    vendedor.setApelidoven(vendedorRegistroDto.getApelidoven());
    vendedor.setNascimentoven(vendedorRegistroDto.getNascimentoven());
    vendedor.setTelefoneven(vendedorRegistroDto.getTelefoneven());
    vendedor.setEnderecoven(vendedorRegistroDto.getEnderecoven());
    vendedor.setNumeroven(vendedorRegistroDto.getNumeroven());
    vendedor.setComplementoven(vendedorRegistroDto.getComplementoven());
    vendedor.setCnpj(vendedorRegistroDto.getCnpj());
    vendedor.setDocumentoven(vendedorRegistroDto.getDocumentoven());
    vendedor.setImgven(vendedorRegistroDto.getImgven());

    vendedor.setCidade(cidade);
    vendedor.setRamoatv(ramoAtv);

    return vendedorRepository.save(vendedor);
  }
}

