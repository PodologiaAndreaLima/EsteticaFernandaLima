package lima.fernanda.ordemservico.infrastructure.persistence.mapper;

import lima.fernanda.ordemservico.domain.model.ItemOrdemServico;
import lima.fernanda.ordemservico.domain.model.OrdemServico;
import lima.fernanda.ordemservico.infrastructure.persistence.entity.ItemOrdemServicoEntity;
import lima.fernanda.ordemservico.infrastructure.persistence.entity.OrdemServicoEntity;

import java.util.ArrayList;
import java.util.List;

public final class OrdemServicoEntityMapper {

    private OrdemServicoEntityMapper() {
    }

    public static OrdemServicoEntity toEntity(OrdemServico domain) {
        OrdemServicoEntity entity = new OrdemServicoEntity();
        entity.setId(domain.getId());
        entity.setClienteId(domain.getClienteId());
        entity.setUsuarioId(domain.getUsuarioId());
        entity.setValorFinal(domain.getValorFinal());
        entity.setDataCriacao(domain.getDataCriacao());
        entity.setObservacao(domain.getObservacao());

        List<ItemOrdemServicoEntity> itens = new ArrayList<>();
        if (domain.getItens() != null) {
            for (ItemOrdemServico item : domain.getItens()) {
                ItemOrdemServicoEntity itemEntity = new ItemOrdemServicoEntity();
                itemEntity.setServicoProdutoId(item.getServicoProdutoId());
                itemEntity.setComboId(item.getComboId());
                itemEntity.setProdutoId(item.getProdutoId());
                itemEntity.setQuantidade(item.getQuantidade());
                itemEntity.setDesconto(item.getDesconto());
                itemEntity.setOrdemServico(entity);
                itens.add(itemEntity);
            }
        }
        entity.setItens(itens);

        return entity;
    }

    public static OrdemServico toDomain(OrdemServicoEntity entity) {
        OrdemServico domain = new OrdemServico();
        domain.setId(entity.getId());
        domain.setClienteId(entity.getClienteId());
        domain.setUsuarioId(entity.getUsuarioId());
        domain.setValorFinal(entity.getValorFinal());
        domain.setDataCriacao(entity.getDataCriacao());
        domain.setObservacao(entity.getObservacao());

        List<ItemOrdemServico> itens = new ArrayList<>();
        if (entity.getItens() != null) {
            for (ItemOrdemServicoEntity itemEntity : entity.getItens()) {
                ItemOrdemServico item = new ItemOrdemServico();
                item.setServicoProdutoId(itemEntity.getServicoProdutoId());
                item.setComboId(itemEntity.getComboId());
                item.setProdutoId(itemEntity.getProdutoId());
                item.setQuantidade(itemEntity.getQuantidade());
                item.setDesconto(itemEntity.getDesconto());
                itens.add(item);
            }
        }
        domain.setItens(itens);

        return domain;
    }
}

