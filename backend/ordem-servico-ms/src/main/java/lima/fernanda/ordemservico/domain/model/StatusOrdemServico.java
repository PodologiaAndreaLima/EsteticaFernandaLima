package lima.fernanda.ordemservico.domain.model;

public enum StatusOrdemServico {
    PENDENTE, EM_ANDAMENTO, CONCLUIDA, CANCELADA;

    public boolean podeTransicionarPara(StatusOrdemServico destino) {
        return this == destino
                || this == PENDENTE && (destino == EM_ANDAMENTO || destino == CANCELADA)
                || this == EM_ANDAMENTO && (destino == CONCLUIDA || destino == CANCELADA);
    }
}
