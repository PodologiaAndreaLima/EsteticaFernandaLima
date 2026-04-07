package lima.fernanda.ordemservico.application.mapper;

import lima.fernanda.ordemservico.application.dto.*;
import lima.fernanda.ordemservico.domain.model.ItemOrdemServico;
import lima.fernanda.ordemservico.domain.model.OrdemServico;

import java.util.Collections;
import java.util.List;

public final class OrdemServicoMapper {

    private OrdemServicoMapper() {
    }

    public static OrdemServico toDomain(OrdemServicoRequest request) {
        OrdemServico ordemServico = new OrdemServico();
        ordemServico.setClienteId(request.clienteId());
        ordemServico.setUsuarioId(request.usuarioId());
        ordemServico.setValorFinal(request.valorFinal());
        ordemServico.setObservacao(request.observacao());

        List<ItemOrdemServico> itens = request.itens() == null ? Collections.emptyList() : request.itens().stream()
                .map(OrdemServicoMapper::toDomainItem)
                .toList();
        ordemServico.setItens(itens);
        return ordemServico;
    }

    public static OrdemServicoResponse toResponse(OrdemServico ordemServico) {
        List<ItemOrdemServicoResponse> itens = ordemServico.getItens() == null ? Collections.emptyList() : ordemServico.getItens()
                .stream()
                .map(OrdemServicoMapper::toResponseItem)
                .toList();

        return new OrdemServicoResponse(
                ordemServico.getId(),
                ordemServico.getClienteId(),
                ordemServico.getUsuarioId(),
                ordemServico.getValorFinal(),
                ordemServico.getDataCriacao(),
                ordemServico.getObservacao(),
                itens
        );
    }

    private static ItemOrdemServico toDomainItem(ItemOrdemServicoRequest request) {
        ItemOrdemServico item = new ItemOrdemServico();
        item.setServicoProdutoId(request.servicoProdutoId());
        item.setComboId(request.comboId());
        item.setProdutoId(request.produtoId());
        item.setQuantidade(request.quantidade() != null ? request.quantidade() : 1);
        item.setDesconto(request.desconto() != null ? request.desconto() : 0.0f);
        return item;
    }

    private static ItemOrdemServicoResponse toResponseItem(ItemOrdemServico item) {
        return new ItemOrdemServicoResponse(
                item.getServicoProdutoId(),
                item.getComboId(),
                item.getProdutoId(),
                item.getQuantidade(),
                item.getDesconto()
        );
    }
}

