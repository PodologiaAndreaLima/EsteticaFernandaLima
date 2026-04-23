package lima.fernanda.ordemservico.infrastructure.persistence.mapper;

import lima.fernanda.ordemservico.domain.model.ItemOrdemServico;
import lima.fernanda.ordemservico.domain.model.OrdemServico;
import lima.fernanda.ordemservico.infrastructure.persistence.entity.ItemOrdemServicoEntity;
import lima.fernanda.ordemservico.infrastructure.persistence.entity.OrdemServicoEntity;

import java.util.ArrayList;
import java.util.List;

public final class OrdemServicoEntityMapper {

    private OrdemServicoEntityMapper() {}

    public static OrdemServicoEntity toEntity(OrdemServico domain) {
        OrdemServicoEntity entity = new OrdemServicoEntity();
        entity.setId(domain.getId());
        entity.setClienteId(domain.getClienteId());
        entity.setUsuarioId(domain.getUsuarioId());
        entity.setValorFinal(domain.getValorFinal());
        entity.setDataCriacao(domain.getDataCriacao());
        entity.setObservacao(domain.getObservacao());

        if (domain.getItens() != null) {
            List<ItemOrdemServicoEntity> entityItens = new ArrayList<>();
            for (ItemOrdemServico item : domain.getItens()) {
                ItemOrdemServicoEntity itemEntity = toItemEntity(item);
                itemEntity.setOrdemServico(entity);
                entityItens.add(itemEntity);
            }
            entity.setItens(entityItens);
        }
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

        if (entity.getItens() != null) {
            domain.setItens(entity.getItens().stream()
                    .map(OrdemServicoEntityMapper::toItemDomain)
                    .toList());
        }
        return domain;
    }

    private static ItemOrdemServicoEntity toItemEntity(ItemOrdemServico item) {
        ItemOrdemServicoEntity entity = new ItemOrdemServicoEntity();
        entity.setServicoProdutoId(item.getServicoProdutoId());
        entity.setComboId(item.getComboId());
        entity.setProdutoId(item.getProdutoId());
        entity.setQuantidade(item.getQuantidade());
        entity.setDesconto(item.getDesconto());
        entity.setNomeServicoProduto(item.getNomeServicoProduto());
        entity.setNomeCombo(item.getNomeCombo());
        entity.setEhProduto(item.getEhProduto());
        return entity;
    }

    private static ItemOrdemServico toItemDomain(ItemOrdemServicoEntity entity) {
        ItemOrdemServico item = new ItemOrdemServico();
        item.setServicoProdutoId(entity.getServicoProdutoId());
        item.setComboId(entity.getComboId());
        item.setProdutoId(entity.getProdutoId());
        item.setQuantidade(entity.getQuantidade());
        item.setDesconto(entity.getDesconto());
        item.setNomeServicoProduto(entity.getNomeServicoProduto());
        item.setNomeCombo(entity.getNomeCombo());
        item.setEhProduto(entity.getEhProduto());
        return item;
    }
}
