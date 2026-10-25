package lima.fernanda.esteticaFernandaLima.mensagem.dto;

import lima.fernanda.esteticaFernandaLima.mensagem.model.TipoMensagem;

public record GerarMensagemResponse(Long lembreteId, TipoMensagem tipo, String conteudo,
                                    boolean prontaParaRevisao) {}
